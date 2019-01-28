package com.nbcb.myron.bsen.module;

import java.io.Serializable;

/**
 * @author:黄孟军
 * @date:2019/1/26
 * @description:用户发送的消息实体
 */
public class Message implements Serializable {
    private static final long serialVersionUID = -6107886130999231868L;

    private String sessionID;//会话Id;记录谁对谁的消息
    private String content;
    private String createTime;
    private String isReaded;

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIsReaded() {
        return isReaded;
    }

    public void setIsReaded(String isReaded) {
        this.isReaded = isReaded;
    }
}
