package com.bean;

public interface MessageType {
    String MESSAGE_LOGIN_SUCCESS = "1";
    String MESSAGE_LOGIN_FAIL = "2";
    String MESSAGE_COMM_MES = "3";//普通信息包
    String MESSAGE_ONLINE_USERS = "4";// 要求获取在线用户列表
    String MESSAGE_RETURN_USERS = "5";// 返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6";//客户端退出
    String MESSAGE_COMM_SMS = "7"; //群聊消息
    String MESSAGE_FILE_MES = "8"; //发送文件
}
