package com.nbcb.myron.bsen.module;

public class CutProduct{
    private String id;//id
    private String desc;//产品描述
    private String imgUrl;//缩略图
    private String oldPrice;//原价
    private String newPrice;//砍后价
    private String cutPriceNum;//参与砍价人数
    private String soldNum;//已售
    private String browseNum;//已浏览次数

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    public String getCutPriceNum() {
        return cutPriceNum;
    }

    public void setCutPriceNum(String cutPriceNum) {
        this.cutPriceNum = cutPriceNum;
    }

    public String getSoldNum() {
        return soldNum;
    }

    public void setSoldNum(String soldNum) {
        this.soldNum = soldNum;
    }

    public String getBrowseNum() {
        return browseNum;
    }

    public void setBrowseNum(String browseNum) {
        this.browseNum = browseNum;
    }
}
