package com.nbcb.myron.bsen.module;

import java.io.Serializable;

/**
 * @author:黄孟军
 * @date:2019/1/26
 * @description:用户发送的消息实体
 */
public class Message implements Serializable {
    private static final long serialVersionUID = -6107886130999231868L;

    private Integer id;//消息表id
    private String sessionID;//会话Id;记录谁对谁的消息
    private String content;
    private String createdTime;
    private String type;
    private String isReaded;
    private String msgId;//消息id 可用于排重

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsReaded() {
        return isReaded;
    }

    public void setIsReaded(String isReaded) {
        this.isReaded = isReaded;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
