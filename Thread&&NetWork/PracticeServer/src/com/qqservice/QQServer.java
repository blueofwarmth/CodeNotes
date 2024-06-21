package com.qqservice;

import com.bean.Message;
import com.bean.MessageType;
import com.bean.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class QQServer {
    private ServerSocket serverSocket = null;
    //创建一个集合, 存放用户
    //HashMap线程不安全
    //ConcurrentHashMap处理的线程安全, 即线程同步处理, 多线程下安全
    private static  HashMap<String, User> users = new HashMap<String, User>();
    //静态代码块, 初始化user
    static{
        users.put("卡芙卡", new User("卡芙卡", "111111"));
        users.put("刃", new User("刃", "111111"));
        users.put("镜流", new User("镜流", "111111"));
        users.put("三月七", new User("三月七", "111111"));
    }
    //验证用户是否存在
    private boolean checkUser(String userId, String password) {
        User user = users.get(userId);
        if(user == null) {
            System.out.println("用户不存在");
            return false;
        } else if( !password.equals(user.getPassword())) {
            System.out.println("密码错误");
            return false;
        } else {
            System.out.println("账户验证通过");
            return true;
        }


    }
    public QQServer() {
        try {
            System.out.println("ServerSocket is listening in 7777port");
            serverSocket = new ServerSocket(7777);
            //启动新闻推送
            new Thread(new SendNew()).start();

            while(true) {//连接后持续监听
                Socket socket = serverSocket.accept();//如果没有连接, 则会阻塞1
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                //反序列化
                User u = (User)ois.readObject();
                //创建Message回复Client
                Message message = new Message();
                if(checkUser(u.getUserId(), u.getPassword())) {
                    message.setMessType(MessageType.MESSAGE_LOGIN_SUCCESS);
                    oos.writeObject(message);
                    //创建一条线程, 与客户端保持连接
                    ServerConnectThread  serverConnectThread =  new ServerConnectThread(socket, u.getUserId());
                    serverConnectThread.start();
                    //放入集合
                    ManageClientThread.addClientThread(u.getUserId(), serverConnectThread);

                } else {//登录失败
                    message.setMessType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //退出while后, 关闭Server Socket
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
