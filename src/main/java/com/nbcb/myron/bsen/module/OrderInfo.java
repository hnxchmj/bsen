package com.nbcb.myron.bsen.module;

import java.io.Serializable;

public class OrderInfo implements Serializable {
    private static final long serialVersionUID = -1938552304189035715L;
    private Integer id;
    private String pid;
    private String turnOver;
    private Integer price;
    private String imgUrl;
    private String desc;
    private String order_num;
    private String time;
    private String name;//收货人
    private String phone;//电话
    private String address;//收货地址
    private String self_get;//是否自取
    private String evaluate;//评价
    private String evaluateTime;//添加评价时间
    private String orderStatus;//发货状态:1-待发货;2-已发货;3-已完成
    private String transportCode;//物流公司编码
    private String transportName;//物流公司名称
    private String transportNum;//运单号,
    private String flowTime;//发货时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTurnOver() {
        return turnOver;
    }

    public void setTurnOver(String turnOver) {
        this.turnOver = turnOver;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOrder_num() {
        return order_num;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSelf_get() {
        return self_get;
    }

    public void setSelf_get(String self_get) {
        this.self_get = self_get;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public String getEvaluateTime() {
        return evaluateTime;
    }

    public void setEvaluateTime(String evaluateTime) {
        this.evaluateTime = evaluateTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getTransportNum() {
        return transportNum;
    }

    public void setTransportNum(String transportNum) {
        this.transportNum = transportNum;
    }

    public String getFlowTime() {
        return flowTime;
    }

    public void setFlowTime(String flowTime) {
        this.flowTime = flowTime;
    }

    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }
}
