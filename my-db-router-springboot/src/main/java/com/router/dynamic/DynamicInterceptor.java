package com.router.dynamic;

import com.router.DBContextHolder;
import com.router.annotation.DBAnnotationSwtich;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qyingli
 * @date 2024/4/27 15:49
 * @package: com.router.dynamic
 * @description: TODO 获取sql拦截
 */
@Intercepts({
        @Signature(
                type = StatementHandler.class, //通过获取StatementHandler来拦截, 识别是否需要分库分表, 通过反射获取sql
                method = "prepare",
                args = {Connection.class, Integer.class})})
public class DynamicInterceptor implements Interceptor {
    private Pattern pattern = Pattern.compile("(from|into|update)[\\s]{1,}(\\w{1,})", Pattern.CASE_INSENSITIVE);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //1.拿到statementHandler
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        //用在不显式的使用反射的情况下 ,获取待处理的对象的属性
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        //2. 从 metaObject 中获取了一个名为 delegate.mappedStatement 的属性，该属性的类型是 MappedStatement
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        //3. 判断是否需要分库分表通过获取自定义注解
        String id = mappedStatement.getId();
        //从头开始到"."的所有内容
        String className = id.substring(0, id.lastIndexOf("."));
        Class<?> clazz = Class.forName(className);
        DBAnnotationSwtich dbAnnotationSwtich = clazz.getAnnotation(DBAnnotationSwtich.class);
        if(dbAnnotationSwtich == null || !dbAnnotationSwtich.swtich()) {
            return invocation.proceed();
        }
        //4. 获取sql, 替换内容
        //得到完整sql
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        //替换sql的表名user ->user003
        Matcher matcher = pattern.matcher(sql);
        String sqlTable = null;
        if (matcher.find()) {
            //使用 group() 方法获取匹配的部分。trim() 方法会删除匹配的子字符串中的前导和尾随空格。
            sqlTable = matcher.group().trim();
        }
        //替换
        if(sqlTable != null) {
            //拼接分表id
            String replaceSql = matcher.replaceAll(sqlTable + "_" + DBContextHolder.getTBKey());
            Field sqlNew = sql.getClass().getDeclaredField("sql");
            sqlNew.setAccessible(true);
            sqlNew.set(boundSql, replaceSql);
            sqlNew.setAccessible(false);
        }
        return invocation.proceed();
    }
}
