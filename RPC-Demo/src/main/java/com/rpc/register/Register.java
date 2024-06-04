package com.rpc.register;

import com.alibaba.fastjson.JSON;
import com.rpc.common.RpcServiceNameBuilder;
import com.rpc.common.ServiceMeta;
import com.rpc.config.RpcProperties;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Qyingli
 * @date 2024/5/17 10:43
 * @package: com.rpc.register
 * @description: TODO Redis注册中心
 * 思路：
 *      使用集合保存所有服务节点信息
 * 1. 服务启动：节点使用了redis作为注册中心后，将自身信息注册到redis当中(ttl：10秒)，并开启定时任务，ttl/2。
 * 2. 定时任务用于检测各个节点的信息，如果发现节点的时间 < 当前时间->则将节点踢出，如果没有发现->则续签自身节点
 * 3. 将节点踢出后，从服务注册表中找到对应key删除该节点的下的服务数据信息
 *
 * ttl :10秒
 * 定时任务为ttl/2
 * 节点注册后启动心跳检测，检测服务注册的key集合，如果有服务到期，则删除,自身的服务则续签
 * 服务注册后将服务注册到redis以及保存到自身的服务注册key集合，供心跳检测
 *
 * 如果有节点宕机，则其他服务会检测的，如果服务都宕机，则ttl会进行管理
 */
public class Register implements RegisterService {
    private JedisPool jedisPool;

    private String UUID;

    private static final int ttl = 10 * 1000;

    private Set<String> serviceKeyMap = new HashSet<>();

    //用于调度任务执行的接口。它是 ExecutorService 的子接口，提供了一种方便的方式来执行定时任务、周期性任务和延迟任务
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    /**
     * 注册当前服务,将当前服务ip，端口，时间注册到redis当中，并且开启定时任务
     * 使用集合存储服务节点信息
     */
    public Register(){
        RpcProperties properties = RpcProperties.getInstance();
        String[] split = properties.getRegisterAddr().split(":");
        String addr = split[0];
        Integer port = Integer.valueOf(split[1]);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        jedisPool = new JedisPool(poolConfig, addr, port);
        this.UUID = java.util.UUID.randomUUID().toString();
        // 健康监测
        heartbeat();
    }
/*************************************************/




    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        //构建key
        String key = RpcServiceNameBuilder.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion());
        //不包含则添加
        if(!serviceKeyMap.contains(key)) {
            serviceKeyMap.add(key);
        }
        serviceMeta.setUUID(this.UUID);
        serviceMeta.setEndTime(new Date().getTime() + ttl);
        //注册到redis
        Jedis jedis = getJedis();
        String script = "redis.call('RPUSH', KEYS[1], ARGV[1])\n" +
                "redis.call('EXPIRE', KEYS[1], ARGV[2])";
        //原子性操作
        List<String> value = new ArrayList<>();
        //将ServiceMeta转换成Json字符串
        value.add(JSON.toJSONString(serviceMeta));
        value.add(String.valueOf(10));
        //放入Jedis
        jedis.eval(script, Collections.singletonList(key),value);
        jedis.close();
    }

    /**
     * 返回当前服务
     * @param key
     * @return
     */
    private List<ServiceMeta> listServices(String key){
        Jedis jedis = getJedis();
        //获取列表范围内的元素
        List<String> list = jedis.lrange(key, 0, -1);
        jedis.close();
        //转换为JSON返回
        List<ServiceMeta> serviceMetas = list.stream().map(o -> JSON.parseObject(o, ServiceMeta.class)).collect(Collectors.toList());
        return serviceMetas;
    }

    /**
     * 配置Jedis
     * @return
     */
    private Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        RpcProperties properties = RpcProperties.getInstance();
        if(!ObjectUtils.isEmpty(properties.getRegisterPsw())){
            jedis.auth(properties.getRegisterPsw());
        }
        return jedis;
    }



    /**
     * 每个服务有给自己续签(延长ttl)的功能, 也有删除其他超时的服务功能
     */
    private void heartbeat(){
        int sch = 5;
        scheduledExecutorService.scheduleWithFixedDelay(()->{
            for (String key : serviceKeyMap) {
                // 1.获取所有服务节点,查询服务节点的过期时间是否 < 当前时间。如果小于则有权将节点下的服务信息都删除
                List<ServiceMeta> serviceNodes = listServices(key);
                Iterator<ServiceMeta> iterator = serviceNodes.iterator();
                while (iterator.hasNext()){
                    ServiceMeta node = iterator.next();
                    // 1.删除过期服务
                    if (node.getEndTime() < new Date().getTime()){
                        iterator.remove();
                    }
                    // 2.自身续签
                    if (node.getUUID().equals(this.UUID)){
                        node.setEndTime(node.getEndTime()+ttl/2);
                    }
                }
                // 2.重新加载服务
                if (!ObjectUtils.isEmpty(serviceNodes)) {
                    loadService(key,serviceNodes);
                }
            }

        },sch,sch, TimeUnit.SECONDS);
    }
    /**
     * 原子性操作
     * 负载均衡
     * @param key
     * @param serviceMetas
     */
    private void loadService(String key, List<ServiceMeta> serviceMetas){
        String script = "redis.call('DEL', KEYS[1])\n" +
                "for i = 1, #ARGV do\n" +
                "   redis.call('RPUSH', KEYS[1], ARGV[i])\n" +
                "end \n"+
                "redis.call('EXPIRE', KEYS[1],KEYS[2])";
        List<String> keys = new ArrayList<>();
        keys.add(key);
        keys.add(String.valueOf(10));
        List<String> values = serviceMetas.stream().map(o -> JSON.toJSONString(o)).collect(Collectors.toList());
        Jedis jedis = getJedis();
//        用于执行 Lua 脚本的方法
        jedis.eval(script,keys,values);
        jedis.close();
    }

    @Override
    public void unregister(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public List<ServiceMeta> getService(String serviceName) {
        return listServices(serviceName);
    }

    @Override
    public void close(ServiceMeta serviceMeta) throws IOException {
        scheduledExecutorService.shutdown();
    }
}
