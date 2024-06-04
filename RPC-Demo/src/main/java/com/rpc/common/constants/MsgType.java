package com.rpc.common.constants;

/**
 * 消息类型, 返回对应的类型
 */
public enum  MsgType {

    REQUEST,
    RESPONSE,
    HEARTBEAT;

    public static MsgType findByType(int type) {

        return MsgType.values()[type];
    }
}