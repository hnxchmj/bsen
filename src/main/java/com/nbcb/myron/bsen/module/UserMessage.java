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
    private String uId;
    private String nickName;
    private String headImgPath;
    private String content;
    private String isReaded;
    private String createTime;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsReaded() {
        return isReaded;
    }

    public void setIsReaded(String isReaded) {
        this.isReaded = isReaded;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
