package com.qqservice;

import java.util.HashMap;
import java.util.Iterator;

public class ManageClientThread {
    private static HashMap<String, ServerConnectThread> connectThreads = new HashMap<String, ServerConnectThread>();

    /**
     * 获取当前连接的线程
     * @return 在线的线程
     */
    public static HashMap getHasMapping() {
        return connectThreads;
    }

    /**
     * 添加线程到集合当中
     * @param userId
     * @param serverConnectThread
     */
    public static void addClientThread(String userId, ServerConnectThread serverConnectThread) {
        connectThreads.put(userId, serverConnectThread);
    }

    /**
     * 获取线程
     * @param userId
     * @return 已连接的线程
     */
    public ServerConnectThread getServerThread(String userId) {
        return connectThreads.get(userId);
    }

    /**
     * 获取在线用户名单
     * @return 在线用户列表
     */
    public static String getOnlineUsers() {
        //遍历集合
        String onlineUserList = "";
//        for (String i : connectThreads.keySet()) {
//            onlineUserList += connectThreads.get(i).toString();
//        }
        Iterator<String> iterator = connectThreads.keySet().iterator();
        while (iterator.hasNext()) {
            onlineUserList += iterator.next().toString() + " ";
        }
        return onlineUserList;
    }

    /**
     * 从集合中删除指定线程
     * @param userId
     * @return boolean
     */
    public static boolean removeClientThread(String userId) {
        connectThreads.remove(userId);
        System.out.println(userId + "下线了");
        return false;
    }

    //返回指定线程
    public static ServerConnectThread getCilientThread(String userId) {
        return connectThreads.get(userId);
    }

}

