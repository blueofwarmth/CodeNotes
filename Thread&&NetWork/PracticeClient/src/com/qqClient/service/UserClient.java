package com.qqClient.service;

import com.qqClient.utility.Utility;
import com.bean.MessageType;
import com.bean.User;
import com.bean.Message;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

/**
 * 完成登录验证和用户注册等功能的方法
 * */
public class UserClient {
    private User u = new User(); //可能在其他地方使用User属性, checkUser中完成初始化
    private Socket socket = null;//因为其他地方可能还会使用, 如多线程
    private Message message = new Message();


    /**
     * 根据userID和password到服务器验证是否合法
     * @param userId
     * @param password
     * @return boolean
     */
    public boolean checkUser(String userId, String password) {
        boolean result = false;//记录是否登录成功
        u.setUserId(userId);
        u.setPassword(password);
        //连接到服务端

        try {
            socket = new Socket(InetAddress.getByName("localhost"), 7777);
            //得到ObjectOutputStream对象
            ObjectOutputStream oss = new ObjectOutputStream(socket.getOutputStream());
            //将User对象序列化成字节序列并输出到输出流中的方法
            oss.writeObject(u);

            //读取从服务端回送的对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //从指定的文件输入流中读取对象并反序列化为Message
            Message msg = (Message) ois.readObject();
            if (msg.getMessType().equals(MessageType.MESSAGE_LOGIN_SUCCESS)) {
                //创建一个和服务端保持通信的线程, 再将线程放在一个集合里面去管理
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                //启动客户端线程
                clientConnectServerThread.start();
               //将线程放入到集合当中
                ManageClientConnect.addClientThread(userId, clientConnectServerThread);
                result = true;
            } else {
                 //登录失败, 关闭socket
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 向服务器请求在线用户列表
     * @throws IOException
     */
    public void onlineFriendsList() throws IOException {
        //发用一个MESSAGE_ONLINE_USERS和ID
        try {
            message.setMessType(MessageType.MESSAGE_ONLINE_USERS);
            message.setSender(u.getUserId());
            //从管理线程的集合中通过ID得到对应线程
            ClientConnectServerThread ccsThread = ManageClientConnect.getClientThread(u.getUserId());
            Socket socket = ccsThread.getSocket();
            //得到当前线程的socket对应的ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //将消息发送给服务端
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 完成退出登录
     * @return boolean
     * @throws IOException
     */
    public boolean logout() throws IOException {
        message.setMessType(MessageType.MESSAGE_CLIENT_EXIT); //使用接口message中的属性
        message.setSender(u.getUserId()); //指明需要关闭的线程

        //发送message
//        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectOutputStream oos =
                new ObjectOutputStream(ManageClientConnect.getClientThread(u.getUserId()).getSocket().getOutputStream()); //适应多用户
        oos.writeObject(message);
        //关闭socket
        socket.close();
        System.out.println("用户" + u.getUserId() + "下线了.\n");
        System.exit(0); //结束进程
        return false;
    }
    //私发消息
    /**
     * @param senderId 发送者Id
     * */
    public boolean privateMessage(String senderId) throws IOException {
        System.out.print("聊天的对象:");
        String receiverId = Utility.readString(20);
        System.out.print("你想说:");
        String mesContent = Utility.readString(200);

        //设置message
        message.setMessType(MessageType.MESSAGE_COMM_MES);
        message.setSender(senderId);
        message.setReceiver(receiverId);
        message.setContent(mesContent);
        message.setTime(new Date().toString());
        //发送给服务端
        ObjectOutputStream oos  =
                new ObjectOutputStream(ManageClientConnect.getClientThread(senderId).getSocket().getOutputStream());
        oos.writeObject(message);

        return true;
    }

    /**
     * 处理群发消息
     * @param sender
     * @throws IOException
     */
    public void sms(String sender) throws IOException {
        System.out.println("你相对大家说的话:");
        String content = Utility.readString(200);

        message.setMessType(MessageType.MESSAGE_COMM_SMS);
        message.setSender(sender);
        message.setContent(content);
        message.setTime(new Date().toString());
        //发送给服务端
        ObjectOutputStream oos  =
                new ObjectOutputStream(ManageClientConnect.getClientThread(sender).getSocket().getOutputStream());
        oos.writeObject(message);

        System.out.println(sender + "对大家说:" + content);
    }

    /**
     *  完成接受发送文件的信息
     *  用户群发消息
     * @param userId 发送者
     */
    public void sendFile(String userId) throws IOException {
        System.out.println("你想发送文件给谁?");
        String receive = Utility.readString(100);
        System.out.println("想发送文件的路径:");
        String filePath = Utility.readString(255);
        System.out.println("文件目的地址:");
        String fileDestination = Utility.readString(255);
        sendFileToOne(filePath,fileDestination,userId, receive);
    }

    /**
     * 获取在线用户列表
     * @param msg 保存着用户列表
     */
    public void  getOnline(Message msg) {
        System.out.println("在线用户列表:");
//        for (int i = 0; i < onlineUsers.length; i++) {
//            System.out.println("用户:" + onlineUsers[i]);
//        }
        //取出在线列表信息，并显示
        //在线用户列表形式 Jack rose sam
        String[] onlineUsers = msg.getContent().split(" ");
        for(String user : onlineUsers) {
            System.out.println("用户:" + user);
        }
    }

    /**
     * 用于处理接收到的文件
     * @param msg
     * @throws IOException
     */
    public void getFile(Message msg) throws IOException {
        FileOutputStream fos = new FileOutputStream(msg.getFileDest());
        fos.write(msg.getFileBytes());
        fos.close();
        System.out.println("收到" + msg.getSender() + "的文件.");
    }

    /**
     * 完成文件传输功能
     * @param src
     * @param dest
     * @param senderId
     * @param receiverId
     * @throws IOException
     */
    public void sendFileToOne(String src, String dest, String senderId, String receiverId) throws IOException {
        //设置基本信息
        Message message = new Message();
        message.setMessType(MessageType.MESSAGE_FILE_MES);
        message.setReceiver(receiverId);
        message.setSender(senderId);
        message.setFileDest(dest);
        message.setFileSrc(src);

        //读取文件, 使用缓冲流
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(src));
//        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(dest));
        byte[] bytes = new byte[(int)new File(src).length()]; //byte类型的数组, 大小为src下文
        // 件大小, 将字节数转为int, 即xxx个字节
        inputStream.read(bytes, 0, bytes.length);
//        int len = 0;
//        while ((len = inputStream.read(bytes))!= -1) {
//            outputStream.write(bytes, 0, len); //note:从off位开始,写入len长度字节到bytes中
//        }
//        outputStream.close();
        message.setFileBytes(bytes);// 将文件存储到message中
        inputStream.close();

        //发送文件
        ObjectOutputStream oos =
                new ObjectOutputStream(ManageClientConnect.getClientThread(senderId).getSocket().getOutputStream());
        oos.writeObject(message);

        //输出提示
        System.out.println(senderId + "发送文件给" + receiverId + "到:" + dest);
    }
}