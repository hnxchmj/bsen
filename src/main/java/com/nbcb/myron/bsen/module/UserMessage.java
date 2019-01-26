package com.nbcb.myron.bsen.module;

import java.io.Serializable;
import java.util.List;

/**
 * @author:黄孟军
 * @date:2019/1/26
 * @description:
 */
public class UserMessage  implements Serializable {
    private static final long serialVersionUID = -828060829170717297L;
    private Integer id;
    private String nickName;
    private String headImgPath;
    private List<Message> messages;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
