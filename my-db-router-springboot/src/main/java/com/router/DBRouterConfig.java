package com.router;

import lombok.Data;

/**
 * @description: 数据路由配置
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439?spm_id_from=333.1007.0.0
 * @CreateTime: 2023-04-10 16:52
 */
@Data
public class DBRouterConfig {

    /**
     * 分库数量
     */
    private int dbCount;

    /**
     * 分表数量
     */
    private int tbCount;

    /**
     * 路由字段
     */
    private String routerKey;

    public DBRouterConfig(int dbCount, int tbCount, String routerKey) {
        this.dbCount = dbCount;
        this.tbCount = tbCount;
        this.routerKey = routerKey;
    }
}
