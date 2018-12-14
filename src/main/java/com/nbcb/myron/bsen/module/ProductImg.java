package com.nbcb.myron.bsen.module;

import java.io.Serializable;

public class ProductImg implements Serializable {
    private static final long serialVersionUID = 4068011298287041932L;
    private String path;
    private String filename;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
