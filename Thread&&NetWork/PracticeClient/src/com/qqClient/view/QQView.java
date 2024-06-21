package com.qqClient.view;

import com.qqClient.service.UserClient;
import com.qqClient.utility.Utility;

import java.io.IOException;

public class QQView {
    private boolean loop = true; //控制是否显示菜单
    private String key = ""; //接收选择

    //用于登录服务器和注册
    private UserClient userClient = new UserClient();
    public void mainMenu() throws IOException {
        while (loop) {
            System.out.println("=====Welcome O.O=====");
            System.out.println("1. Login System");
            System.out.println("0. Exit system");
            System.out.print("Please enter your choose:");
            key = Utility.readString(1);

            switch (key) {
                case "1":
                    System.out.print("Please enter userId:");
                    String userId = Utility.readString(50);
                    System.out.print("Please enter password:");
                    String password = Utility.readString(50);
                    //在服务端验证用户是否合法
                    /**/
                    if (userClient.checkUser(userId, password)) {
                        System.out.println("=====Welcome" + userId + "Login=====");
                        //进入二级菜单,
                        while (loop) {
                            System.out.println("=====网络通讯系统二级菜单=====");
                            System.out.println("1. 显示在线用户列表");
                            System.out.println("2. 群送消息");
                            System.out.println("3. 私发消息");
                            System.out.println("4. 发送文件");
                            System.out.println("9. 退出系统");
                            System.out.print("请输入你的选择:");
                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    userClient.onlineFriendsList();
                                    break;
                                case "2":
                                    userClient.sms(userId);
                                    break;
                                case "3":
                                    userClient.privateMessage(userId);
                                    break;
                                case "4":
                                    userClient.sendFile(userId);
                                    break;
                                case "9":
                                    //主线程退出了, 但是通信线程没有退出, 所以进程还在运行
                                    //调用方法,给服务器发送消息, 退出线程
                                    loop = userClient.logout();
                                    break;
                                default:
                                    System.out.println("输入错误");
                                    break;
                            }
                        }
                    } else {
                        System.out.println("登               录失败");
                    }
                    break;
                case "0":
                    System.out.println("退出成功");
                    loop = false;
                    break;
                default:
                    System.out.println("输入错误");
                    break;
            }

        }
    }
}
