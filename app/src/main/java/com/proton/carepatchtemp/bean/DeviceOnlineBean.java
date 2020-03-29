package com.proton.carepatchtemp.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangmengsi on 2017/10/17.
 * 贴通过mqtt查是否在线
 */
public class DeviceOnlineBean {
    /**
     * 是否在线
     */
    @SerializedName("connect")
    private boolean isOnline;
    /**
     * 充电器mac地址
     */
    private String dockerMac;

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getDockerMac() {
        return dockerMac;
    }

    public void setDockerMac(String dockerMac) {
        this.dockerMac = dockerMac;
    }
}
