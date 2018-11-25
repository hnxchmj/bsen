package com.nbcb.myron.bsen.module;

import java.util.List;

public class Dynamic {
    private String id;
    private String uId;
    private String headImgUrl;
    private String nickName;
    private String releaseTime;
    private String content;
    private List<String> contentImgs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getContentImgs() {
        return contentImgs;
    }

    public void setContentImgs(List<String> contentImgs) {
        this.contentImgs = contentImgs;
    }
}
