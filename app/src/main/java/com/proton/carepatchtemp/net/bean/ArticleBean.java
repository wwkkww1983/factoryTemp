package com.proton.carepatchtemp.net.bean;

import java.io.Serializable;

/**
 * Created by MoonlightSW on 2017/8/23.
 */

public class ArticleBean implements Serializable {

    /**
     * id : 10
     * title : 糖尿病也是一种心血管病
     * summary : 1999 年，美国心脏协会 （AHA）就指出2型糖尿病在本质上是一种心血管疾病，美国国家胆固醇教育计划成人治疗指南中则将糖尿病视为冠心病等危症。
     * url : http://www.protontek.com/heartHealth/one.html
     * image : http://www.protontek.com/heartHealth/img/one-01.jpg
     * time : 1503453842000
     * deleted : 0
     * type : 2
     */

    private long id;
    private String title;
    private String summary;
    private String url;
    private String image;
    private long time;
    private int type;
    private long readNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getReadNumber() {
        return readNumber;
    }

    public void setReadNumber(long readNumber) {
        this.readNumber = readNumber;
    }
}
