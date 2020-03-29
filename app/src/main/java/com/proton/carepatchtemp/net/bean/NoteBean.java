package com.proton.carepatchtemp.net.bean;

import java.io.Serializable;

public class NoteBean implements Serializable {


    /**
     * deviceId : 893
     * reportid : 20908
     * type : 1
     * content : 头昏某呢得得呢8too嗯
     * created : 1477884092000
     */

    private long id;
    private long reportid;
    private int type;
    private String content;
    private long created;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReportid() {
        return reportid;
    }

    public void setReportid(long reportid) {
        this.reportid = reportid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
