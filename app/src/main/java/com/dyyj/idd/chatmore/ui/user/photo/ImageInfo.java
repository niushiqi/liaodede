package com.dyyj.idd.chatmore.ui.user.photo;

import java.io.Serializable;

public class ImageInfo implements Serializable {
    private String url;
    private int ori;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOri() {
        return ori;
    }

    public void setOri(int ori) {
        this.ori = ori;
    }
}
