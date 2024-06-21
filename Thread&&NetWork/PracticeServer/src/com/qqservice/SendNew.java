package com.qqservice;

import com.bean.Message;
import com.bean.MessageType;
import com.utility.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;

/**
 * 用与完成向客户端新闻的推送
 */
public class SendNew implements Runnable {
    boolean sendFlag = true;
    @Override
    public void run() {
        do{
            Message message = new Message();
            System.out.println("Please enter New Message:");
            String newMessage = Utility.readString(255);
            message.setSender("服务端");
            message.setMessType(MessageType.MESSAGE_COMM_SMS);
            message.setContent(newMessage);
            message.setTime(new Date().toString());
            System.out.println("服务端推送:" + newMessage);

            //获取当前所有线程, 并推送消息
            HashMap<String, ServerConnectThread> HashThreads = ManageClientThread.getHasMapping();
            for (String userId : HashThreads.keySet()) {
                //取出在线用户ID
                //通过id获取到线程, 然后再获取到流
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(HashThreads.get(userId).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("Continuing send News(1) Or exit(0)");
            String input = Utility.readString(255);
            if (input.equals("0")) {
                sendFlag = false;
            }
        } while(sendFlag);
    }
}
