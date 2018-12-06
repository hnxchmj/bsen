package com.nbcb.myron.bsen.module;

import java.io.Serializable;

public class DictEntity implements Serializable {

    private static final long serialVersionUID = -907788219768168525L;

    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
