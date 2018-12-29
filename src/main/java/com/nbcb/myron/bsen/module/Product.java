package com.nbcb.myron.bsen.module;

import java.io.Serializable;
import java.util.List;

/**
 * 产品类
 */
public class Product implements Serializable {

    private static final long serialVersionUID = -1808750462342354396L;
    private String id;//商品id
    private List<ProductImg> imgUrls;//轮播图集合
    private Integer oldPrice;//原价
    private String newPrice;//折后价
    private String deliverPrice;//运费
    private String desc;//商品描述
    private String soldNum;//已售
    private String browseNum;//已浏览次数
    private List<ProductImg> detailImgUrls;//详情图片列表
    private List<User> evaluates;//商品评价

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ProductImg> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(List<ProductImg> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public Integer getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(Integer oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    public String getDeliverPrice() {
        return deliverPrice;
    }

    public void setDeliverPrice(String deliverPrice) {
        this.deliverPrice = deliverPrice;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public List<ProductImg> getDetailImgUrls() {
        return detailImgUrls;
    }

    public void setDetailImgUrls(List<ProductImg> detailImgUrls) {
        this.detailImgUrls = detailImgUrls;
    }

    public List<User> getEvaluates() {
        return evaluates;
    }

    public void setEvaluates(List<User> evaluates) {
        this.evaluates = evaluates;
    }
}
