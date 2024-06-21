package com.qqClient.service;

import java.util.HashMap;

/**
 *
 * 管理客户端连接到服务器的线程的类
 */
public class ManageClientConnect {

    private static HashMap<String, ClientConnectServerThread> hashThreads = new HashMap<>();

    /**
     * 将多线程放入集合, key为用户ID, value为通信线程
     * @param userId
     * @param thread
     */
    public static void addClientThread(String userId, ClientConnectServerThread thread) {
        //put方法
        hashThreads.put(userId, thread);
    }

    /**
     * @param userId
     * @return 返回客户端线程根据id
     */
    public  static ClientConnectServerThread getClientThread(String userId) {
        //根据userId返回对应的Value
        return hashThreads.get(userId);
    }

}
