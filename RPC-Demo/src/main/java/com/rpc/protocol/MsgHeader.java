package com.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 消息头
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-30 12:38
 */
@Data
public class MsgHeader implements Serializable {

    private short magic; // 魔数
    private byte version; // 协议版本号
    private byte msgType; // 数据类型
    private byte status; // 状态
    private long requestId; // 请求 ID
    private int serializationLen;
    private byte[] serializations;
    private int msgLen; // 数据长度


}
