package com.nbcb.myron.bsen.module;

import java.io.Serializable;

/**
 * @author:黄孟军
 * @date:2019/1/12
 * @description:物流公司类
 */
public class Logistics implements Serializable {

    private static final long serialVersionUID = -8540451789814003288L;
    private Integer id;
    private String companyName;
    private String companyCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
