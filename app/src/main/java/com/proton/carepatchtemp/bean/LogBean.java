package com.proton.carepatchtemp.bean;

import org.litepal.crud.LitePalSupport;

/**
 * @Description: 日志bean
 * @Author: yxf
 * @CreateDate: 2020/1/15 20:00
 * @UpdateUser: yxf
 * @UpdateDate: 2020/1/19 15:20
 */
public class LogBean extends LitePalSupport {

    /**
     * 用户uid
     */
    private String uid;
    /**
     * 日志类型 mqtt、bluetooth、warn、network
     */
    private String logType;
    /**
     * 连接状态
     */
    private String connectStatus;
    /**
     * 连接类型
     */
    private int connectType;
    /**
     * mqtt连接，订阅的topic
     */
    private String topic;
    /**
     * 网络是否可用
     */
    private boolean networkAvailable;
    /**
     * 是否报警
     */
    private boolean warn;
    /**
     * 报警类型（低温、低电量、断开连接）
     */
    private String warnType;
    /**
     * 日志创建时间
     */
    private String createTime;

    /**
     * 请求"判断体温贴是否在线"的接口是否超时（可能存在网络已连接，但是不稳定，请求超时）
     */
    private boolean checkPatchTimeOut;

    public String getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(String connectStatus) {
        this.connectStatus = connectStatus;
    }

    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isNetworkAvailable() {
        return networkAvailable;
    }

    public void setNetworkAvailable(boolean networkAvailable) {
        this.networkAvailable = networkAvailable;
    }

    public boolean isWarn() {
        return warn;
    }

    public void setWarn(boolean warn) {
        this.warn = warn;
    }

    public String getWarnType() {
        return warnType;
    }

    public void setWarnType(String warnType) {
        this.warnType = warnType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public boolean isCheckPatchTimeOut() {
        return checkPatchTimeOut;
    }

    public void setCheckPatchTimeOut(boolean checkPatchTimeOut) {
        this.checkPatchTimeOut = checkPatchTimeOut;
    }

    @Override
    public String toString() {
        return "LogBean{" +
                "uid='" + uid + '\'' +
                ", logType='" + logType + '\'' +
                ", connectStatus='" + connectStatus + '\'' +
                ", connectType=" + connectType +
                ", topic='" + topic + '\'' +
                ", networkAvailable=" + networkAvailable +
                ", warn=" + warn +
                ", warnType='" + warnType + '\'' +
                ", createTime='" + createTime + '\'' +
                ", checkPatchTimeOut=" + checkPatchTimeOut +
                '}';
    }
}
