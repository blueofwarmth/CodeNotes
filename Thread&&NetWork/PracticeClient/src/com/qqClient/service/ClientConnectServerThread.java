package com.qqClient.service;

import com.bean.Message;
import java.io.*;
import java.net.Socket;
import static com.bean.MessageType.*;

/**
 * 与服务端保持通信的线程
 */
public class ClientConnectServerThread extends Thread {
    private static final long serialVersionUID = 1L;
    //该线程需要持有socket
    private Socket socket;
    String[] onlineUsers =  null;

    //构造器, 接受一个socket对象
    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }
    public Socket getSocket() {
        return socket;
    }
    UserClient userClient = new UserClient();

    @Override
    public void run() {
        //用while保持和服务端的通信
        while(true) {
            try {
                System.out.println("客户端线程, 等待读取服务器发送的数据");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message)ois.readObject();
                //判断MESSAGE类型, 做出相应的操作
                switch (msg.getMessType()) {
                    case MESSAGE_LOGIN_SUCCESS:
                        System.out.println("Login Success");
                        break;
                    case MESSAGE_LOGIN_FAIL:
                        System.out.println("Login Fail");
                        break;
                    case MESSAGE_COMM_MES:
                        System.out.println("收到" + msg.getSender() + "的消息:" + msg.getContent());
                        break;
                    case MESSAGE_COMM_SMS:
                        System.out.println();
                        System.out.println(msg.getSender() + "对大家说:" + msg.getContent());
                        break;
                    case MESSAGE_ONLINE_USERS:
                        //在线用户列表
                        userClient.getOnline(msg);
                        break;
                    case MESSAGE_RETURN_USERS:
                        System.out.println("收到返回在线用户列表");
                        break;
                    case MESSAGE_FILE_MES:
                        //取出接收到的文件字节流
                        userClient.getFile(msg);
                        break;
                    case MESSAGE_CLIENT_EXIT:
                        System.out.println("客户端退出");
                        break;
                    default:
                        break;
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
