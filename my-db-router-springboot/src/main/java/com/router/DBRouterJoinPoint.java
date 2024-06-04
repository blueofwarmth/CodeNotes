package com.router;

import com.router.strategy.IDBRouterStrategy;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Qyingli
 * @date 2024/4/23 21:01
 * @package: com.router
 * @description: TODO AOP, 设置路由策略,库表信息
 *
 */
public class DBRouterJoinPoint {
    private Logger logger = LoggerFactory.getLogger(DBRouterJoinPoint.class);

    private DBRouterConfig dbRouterConfig;

    private IDBRouterStrategy dbRouterStrategy;

    public DBRouterJoinPoint(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        this.dbRouterConfig = dbRouterConfig;
        this.dbRouterStrategy = dbRouterStrategy;
    }
    //用于定义一个切入点（Pointcut），指定所有被DBRouter标注的方法
    @Pointcut("@annotation(com.router.annotation.DBAnnotation)")
    public void aopPoint() {}

    /**
     * 所有需要分库分表的操作，都需要使用自定义注解进行拦截，拦截后读取方法中的入参字段，根据字段进行路由操作。
     * 1. dbRouter.key() 确定根据哪个字段进行路由
     * 2. getAttrValue 根据数据库路由字段，从入参中读取出对应的值。比如路由 key 是 uId，那么就从入参对象 Obj 中获取到 uId 的值。
     * 3. dbRouterStrategy.doRouter(dbKeyAttr) 路由策略根据具体的路由值进行处理
     * 4. 路由处理完成后放行。 jp.proceed();
     * 5. 最后 dbRouterStrategy 需要执行 clear 因为这里用到了 ThreadLocal 需要手动清空。关于 ThreadLocal 内存泄漏介绍 https://t.zsxq.com/027QF2fae
     *
     * - `aopPoint()` 是一个切入点表达式，它指定了通知应该应用于哪些方法。
     * - `@annotation(dbRouter)` 表示只有那些带有 `dbRouter` 注解的方法会被这个通知所拦截。
     * */
    @Around("aopPoint() && @annotation(dbRouter)")
    public Object doRouter(ProceedingJoinPoint jp, DBRouter dbRouter) throws Throwable {
        //获取key
        String key = dbRouter.key();
        //校验
        if (StringUtils.isEmpty(key) && StringUtils.isBlank(dbRouterConfig.getRouterKey())) {
            throw new RuntimeException("annotation DBRouter key is null！");
        }
        key = StringUtils.isNotBlank(key) ? key : dbRouterConfig.getRouterKey();
        // 拿到路由属性
        String dbKeyAttr = getAttrValue(key, jp.getArgs());
        // 设置路由策略
        dbRouterStrategy.doRouter(dbKeyAttr);
        // 返回结果
        try {
            return jp.proceed();
        } finally {
            //存储在本地线程的分组信息
            dbRouterStrategy.clear();
        }
    }

    /**
     * 获取切面连接点所在类中与传入的 JoinPoint 对象相匹配的方法。
     * @param jp
     * @return
     * @throws NoSuchMethodException
     */
    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
//        从 jp 中获取方法的签名（Signature）。
        Signature signature = jp.getSignature();
//        将签名转换为方法签名（MethodSignature）。
        MethodSignature methodSignature = (MethodSignature) signature;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }

    /**
     * 根据attr从args中获取值
     * @param attr
     * @param args
     * @return
     */
    public String getAttrValue(String attr, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(args.length == 1) {
            Object arg = args[0];
            //检查是否是String类型
            if(arg instanceof String) {
                return arg.toString();
            }
        }
        String value = null;
        for(Object arg : args) {
            if (StringUtils.isNotBlank(value)) {
                break;
            }
            /*
             * 如果 arg 对象中确实有一个名为 attr 的属性，并且该属性有一个可访问的 getter 方法,
             * 将返回该属性的值。
             * */
            value = BeanUtils.getProperty(arg, attr);
        }
        return value;
    }
}
