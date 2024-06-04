package com.rpc.consumer;

import com.rpc.common.*;
import com.rpc.common.constants.MsgType;
import com.rpc.common.constants.ProtocolField;
import com.rpc.config.RpcProperties;

import com.rpc.faultTol.FaultTolerantStrategy;
import com.rpc.faultTol.FaultTolerantStrategyFactory;
import com.rpc.filter.FilterConfig;
import com.rpc.filter.FilterData;

import com.rpc.protocol.MsgHeader;
import com.rpc.protocol.RpcProtocol;


import com.rpc.router.LoadBalenceFactory;
import com.rpc.router.LoadBalenceMeta;
import com.rpc.router.LoadBalencer;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static com.rpc.common.constants.FaultTolerantRules.*;

/**
 * @author Qyingli
 * @date 2024/5/21 21:33
 * @package: com.rpc.consumer
 * @description: TODO 动态代理 加载各项中间服务
 */
@Data
public class RpcInvokerProxy implements InvocationHandler {
    private String serviceVersion;
    private long timeout;
    private String loadBalancerType; //负载均衡算法
    private String faultTolerantType; //容错策略
    private long retryCount; //重连次数
    private Logger log = LoggerFactory.getLogger(RpcInvokerProxy.class);

    public RpcInvokerProxy(String serviceVersion, long timeout, String faultTolerantType, String loadBalancerType, long retryCount) {
        // 初始化默认值
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.faultTolerantType = faultTolerantType;
        this.loadBalancerType = loadBalancerType;
        this.retryCount = retryCount;
    }
/***********************************************************/

    // 实现动态代理逻辑
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构建消息
        RpcProtocol rpcProtocol = new RpcProtocol();
        //构建消息头
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setMagic(ProtocolField.MAGIC);
        msgHeader.setVersion(ProtocolField.VERSION);
        msgHeader.setMsgLen(ProtocolField.HEADER_TOTAL_LEN);
        //序列化
        byte[] serialize = RpcProperties.getInstance().getSerialization().getBytes();
        msgHeader.setSerializationLen(serialize.length);
        msgHeader.setSerializations(serialize);
        //消息
        msgHeader.setMsgType((byte) MsgType.REQUEST.ordinal());
        msgHeader.setStatus((byte) 0x1);
        rpcProtocol.setHeader(msgHeader);
        // 构建请求体
        RpcRequest request = new RpcRequest();
        request.setServiceVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setData(ObjectUtils.isEmpty(args) ? new Object[0] : args);
        request.setDataClass(ObjectUtils.isEmpty(args) ? null : args[0].getClass());
        //服务附件
        request.setServiceAttachments(RpcProperties.getInstance().getServiceAttachments());
        request.setClientAttachments(RpcProperties.getInstance().getClientAttachments());
        rpcProtocol.setBody(request);

        //拦截器上下文拿到请求体
        FilterData filterData = new FilterData(request);
        try {
            FilterConfig.getClientBeforeFilterChain().doFilter(filterData);
        } catch (Exception e) {
            throw e;
        }


        // 1.获取负载均衡策略
        RpcConsumer rpcConsumer = new RpcConsumer();
        String serviceName = RpcServiceNameBuilder.buildServiceKey(request.getClassName(), request.getServiceVersion());
        Object[] params = {request.getData()};
        final LoadBalencer loadBalancer = LoadBalenceFactory.get(loadBalancerType);

        // 2.根据策略获取对应服务
        final LoadBalenceMeta serviceMetaRes = loadBalancer.select(params, serviceName);

        ServiceMeta curServiceMeta = serviceMetaRes.getCurServiceMeta();
        final Collection<ServiceMeta> otherServiceMeta = serviceMetaRes.getOtherServiceMeta();


        // 重试机制
        long count = 1;
        long retryCount = this.retryCount;
        RpcResponse rpcResponse = null;
        while (count <= retryCount ){
            // 处理返回数据
            RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
            // XXXHolder
            RpcRequestHolder.REQUEST_MAP.put(requestId, future);
            try {
                // 发送消息
                rpcConsumer.sendRequest(protocol, curServiceMeta);
                // 等待响应数据返回
                rpcResponse = future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS);
                // 如果有异常并且没有其他服务
                if(rpcResponse.getException()!=null && otherServiceMeta.size() == 0){
                    throw rpcResponse.getException();
                }
                if (rpcResponse.getException()!=null){
                    throw rpcResponse.getException();
                }
                log.info("rpc 调用成功, serviceName: {}",serviceName);
                try {
                    FilterConfig.getClientAfterFilterChain().doFilter(filterData);
                }catch (Throwable e){
                    throw e;
                }
                return rpcResponse.getData();
            }catch (Throwable e){
                String errorMsg = e.toString();
                //获取容错策略, 从spi中
                FaultTolerantStrategy faultTolerantStrategy = FaultTolerantStrategyFactory.get(faultTolerantType);
                switch (faultTolerantType){
                    // 快速失败
                    case FailFast:
                        faultTolerantStrategy.handler(rpcResponse, errorMsg);
                        break;
                    // 故障转移
                    case Failover:
                        count++;
                       faultTolerantStrategy.handler(curServiceMeta, otherServiceMeta, errorMsg, count);
                        break;
                    // 忽视这次错误
                    case Failsafe:
                        faultTolerantStrategy.handler();
                        break;
                }
            }
        }

        throw new RuntimeException("rpc 调用失败，超过最大重试次数: {}" + retryCount);
    }
}
