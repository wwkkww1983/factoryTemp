package com.proton.carepatchtemp.net.bean;

/**
 * Created by Api on 2016/11/3.
 */
public class MessageBean {

    /**
     * id : 5220
     * uid : 811
     * reportId : null
     * title : 设备共享提醒
     * contents : 测试2共享的"卡帕奇儿童体温贴90:94"开始记录
     * time : 2017-10-23 10:36:04
     * deleted : 0
     * readstatus : 0
     * type : 1
     */

    private String id;
    private int uid;
    private Object reportId;
    private String title;
    private String contents;
    private String time;
    private int deleted;
    private int readstatus;
    private int type;
    private boolean isChecked;//是否选中删除

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Object getReportId() {
        return reportId;
    }

    public void setReportId(Object reportId) {
        this.reportId = reportId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getReadstatus() {
        return readstatus;
    }

    public void setReadstatus(int readstatus) {
        this.readstatus = readstatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
