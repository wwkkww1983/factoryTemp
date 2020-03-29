package com.proton.carepatchtemp.bean;

import java.io.Serializable;

/**
 * Created by wangmengsi on 2017/10/17.
 */
public class TipsBean implements Serializable {
    private String title;
    private int imageRes;
    private String url;

    public TipsBean() {
    }

    public TipsBean(String title, int imageRes, String url) {
        this.title = title;
        this.url = url;
        this.imageRes = imageRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }
}
