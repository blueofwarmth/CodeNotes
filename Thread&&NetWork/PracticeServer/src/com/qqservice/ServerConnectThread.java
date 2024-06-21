package com.qqservice;

import com.bean.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

import static com.bean.MessageType.*;

public class ServerConnectThread extends Thread {
    private Socket socket;
    private String userId;
    private boolean loop = true;

    public Socket getSocket() {
        return socket;
    }

    public String getUserId() {
        return userId;
    }

    public ServerConnectThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public void run() {
        while (loop) {
            try {
                System.out.println("与客户端" + userId + "保持连接, 读取数据");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                //根据message类型做出相应的处理
                switch (message.getMessType()) {
                    case MESSAGE_ONLINE_USERS:
                        //客户端要求在线用户列表
                        System.out.println(message.getSender() + "请求获取在线用户列表.");
                        String onlineUsers = ManageClientThread.getOnlineUsers();
                        //返回message
                        Message message1 = new Message();
                        message1.setMessType(MESSAGE_ONLINE_USERS);
                        message1.setContent(onlineUsers);
                        message1.setReceiver(message.getSender());
                        //返回给客户端
                        ObjectOutputStream oos = new ObjectOutputStream(getSocket().getOutputStream());
                        oos.writeObject(message1);
                        System.out.println("已发送在线用户列表.");
                        break;
                    //受到客服端的退出消息, 关系持有的socket, 退出循环

                    case MESSAGE_CLIENT_EXIT:
                        loop = ManageClientThread.removeClientThread(userId);
                        socket.close();
                        break;

                    case MESSAGE_COMM_MES:
                        //获取接受消息用户的线程
                        ServerConnectThread serverConnectThread =
                                ManageClientThread.getCilientThread(message.getReceiver());
                        //转发消息
                        ObjectOutputStream oos2 =
                                new ObjectOutputStream(serverConnectThread.getSocket().getOutputStream());
                        oos2.writeObject(message);
                        break;

                    case MESSAGE_COMM_SMS:
                        //获取所有在线的线程, 遍历集合得到
                        HashMap<String, ServerConnectThread> HashThreads = ManageClientThread.getHasMapping();
                        Iterator<String> it = HashThreads.keySet().iterator();
                        while (it.hasNext()) {
                            //取出在线用户ID
                            String userSmsId = it.next().toString();
                            //排除发送者
                            if (!userSmsId.equals(message.getSender())) {
                                //转发消息
                                ObjectOutputStream oosSms =
                                        new ObjectOutputStream(HashThreads.get(userSmsId).getSocket().getOutputStream());
                                oosSms.writeObject(message);
                            }
                        }
                        break;

                    //接受文件, 并转发
                    case MESSAGE_FILE_MES:
                        //获取到接收者对应的线程, 将message对象转发
                         ServerConnectThread serverConnectThread1 =
                                ManageClientThread.getCilientThread(message.getReceiver());
                        ObjectOutputStream objectOutputStream1 =
                                new ObjectOutputStream(serverConnectThread1.getSocket().getOutputStream());
                        objectOutputStream1.writeObject(message);
                        break;

                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}