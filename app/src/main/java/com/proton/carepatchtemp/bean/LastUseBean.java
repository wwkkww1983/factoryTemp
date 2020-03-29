package com.proton.carepatchtemp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wangmengsi on 2018/06/17.
 * 上次使用的设备
 */
public class LastUseBean implements Serializable {
    private long deviceId;
    @SerializedName("patchMac")
    private String macaddress;
    /**
     * 是否存在最近使用的设备
     */
    private boolean exist;
    @SerializedName("version")
    private String hardVersion;
    @SerializedName("sn")
    private String serialNumber;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public String getHardVersion() {
        return hardVersion;
    }

    public void setHardVersion(String hardVersion) {
        this.hardVersion = hardVersion;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
