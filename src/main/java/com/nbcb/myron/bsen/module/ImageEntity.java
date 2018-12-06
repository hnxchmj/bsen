package com.nbcb.myron.bsen.module;

import java.io.Serializable;

public class ImageEntity implements Serializable {
    private static final long serialVersionUID = -1928290395794054528L;
    private String id;
    private String imgPath;
    private String imgName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
}
