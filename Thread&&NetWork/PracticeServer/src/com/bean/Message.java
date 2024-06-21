package com.bean;
//表示客户端和服务端通讯的消息对象

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sender;
    private String receiver;
    private String content;
    private String time;//发送时间
    private String messType;//消息类型, 可以在接口中定义消息类型
    private byte[] fileBytes; //note:用字节数组存储文件
    private int fileLen; //文件大小
    private String fileSrc;//文件来源
    private String fileDest; //文件发送目的地

    public Message() {

    }
    public Message(String sender, String receiver, String content, String time, String messType) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.time = time;
        this.messType = messType;

    }

    public String getFileSrc() {
        return fileSrc;
    }

    public void setFileSrc(String fileSrc) {
        this.fileSrc = fileSrc;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public int getFileLen() {
        return fileLen;
    }

    public void setFileLen(int fileLen) {
        this.fileLen = fileLen;
    }

    public String getFileDest() {
        return fileDest;
    }

    public void setFileDest(String fileDest) {
        this.fileDest = fileDest;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getMessType() {
        return messType;
    }
    public void setMessType(String messType) {
        this.messType = messType;
    }
}
