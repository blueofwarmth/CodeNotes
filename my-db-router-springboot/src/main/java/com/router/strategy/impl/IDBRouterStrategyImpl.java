package com.router.strategy.impl;

import com.router.DBContextHolder;
import com.router.DBRouterConfig;
import com.router.strategy.IDBRouterStrategy;
import lombok.Data;

/**
 * @author Qyingli
 * @date 2024/4/27 17:06
 * @package: com.router.strategy.impl
 * @description: TODO 分库分表策略
 */
@Data
public class IDBRouterStrategyImpl implements IDBRouterStrategy {
    private DBRouterConfig dbRouterConfig;

    public IDBRouterStrategyImpl(DBRouterConfig dbRouterConfig) {
        this.dbRouterConfig = dbRouterConfig;
    }

    //计算路由
    @Override
    public void doRouter(String dbKeyAttr) {
        //1. 获取分库分表数
        int num = dbRouterConfig.getTbCount() * dbRouterConfig.getDbCount();
        //2. 计算散列的那个库
        // 扰动函数；在 JDK 的 HashMap 中，对于一个元素的存放，需要进行哈希散列。而为了让散列更加均匀，所以添加了扰动函数。
        int idx = (num - 1) & (dbKeyAttr.hashCode() ^ (dbKeyAttr.hashCode() >>> 16));
        //3. 那个库
        int dbNum = idx / dbRouterConfig.getDbCount() + 1;
        //4. 那张表
        int tbNum = idx - dbRouterConfig.getTbCount() * (idx - 1);
        //5. 设置结果
        DBContextHolder.setDBKey(String.format("%02d", dbNum));
        DBContextHolder.setTBKey(String.format("%03d", tbNum));
    }

    @Override
    public void setDBKey(int dbIdx) {
        DBContextHolder.setDBKey(String.format("%03d", dbIdx));
    }

    @Override
    public void setTBKey(int tbIdx) {
        DBContextHolder.setTBKey(String.format("%02d", tbIdx));
    }

    @Override
    public int dbCount() {
        return dbRouterConfig.getDbCount();
    }

    @Override
    public int tbCount() {
        return dbRouterConfig.getTbCount();
    }

    @Override
    public void clear() {
        DBContextHolder.clearTBKey();
        DBContextHolder.clearDBKey();
    }
}
