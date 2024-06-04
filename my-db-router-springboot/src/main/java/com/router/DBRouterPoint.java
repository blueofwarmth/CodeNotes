package com.router;

import com.router.annotation.DBAnnotation;
import com.router.strategy.IDBRouterStrategy;
import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author Qyingli
 * @date 2024/4/27 17:25
 * @package: com.router.util
 * @description: TODO 切入点, 设置路由策略
 */
@Aspect
public class DBRouterPoint {
    private Logger logger = LoggerFactory.getLogger(DBRouterPoint.class);

    private DBRouterConfig dbRouterConfig;

    private IDBRouterStrategy dbRouterStrategy;

    public DBRouterPoint(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        this.dbRouterConfig = dbRouterConfig;
        this.dbRouterStrategy = dbRouterStrategy;
    }

    //设置切面信息
    @Pointcut("@annotation(com.router.annotation.DBAnnotation)")
    public void aopPoint() {}

    //设置路由策略
    public Object doRouter(ProceedingJoinPoint jp, DBAnnotation dbAnnotation) throws Throwable {
        //1. 获取路由key
        String key = dbAnnotation.key();
        if(key == null ||dbRouterConfig.getRouterKey() == null) {
            throw new RuntimeException("annotation DBRouter key is null！");
        }
        key = !key.isEmpty() ? key : dbRouterConfig.getRouterKey();
        //2. 获得key的属性
        //切点参数
        Object[] args = jp.getArgs();
        String property = null;
        if(args.length == 1 && args[0] instanceof String) {
            //如果只有一个参数，则直接获取
             property = args[0].toString();
        }
        for(Object arg : args) {
            //arg中如果一个key的属性, 则赋值
            property = BeanUtils.getProperty(arg, key);
            if(!property.isEmpty()) {
                break;
            }
        }
        //3. 设置路由策略
        dbRouterStrategy.doRouter(property);
        //4. 返回对象
        try {
            return jp.proceed();
        } finally {
            dbRouterStrategy.clear();
        }
    }


}
