package com.nbcb.myron.bsen.module;

import java.io.Serializable;
import java.util.List;

/**
 * @author:黄孟军
 * @date:2019/1/28
 * @description:
 */
public class MessageDetail implements Serializable {
    private static final long serialVersionUID = 389307434133623256L;
    private String uId;
    private String nickName;
    private String headImgPath;
    private List<Message> messageList;

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImgPath() {
        return headImgPath;
    }

    public void setHeadImgPath(String headImgPath) {
        this.headImgPath = headImgPath;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
}
