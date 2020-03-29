package com.proton.carepatchtemp.bean;

import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.temp.connector.bean.DeviceBean;

import java.io.Serializable;

/**
 * Created by wangmengsi on 2018/3/22.
 * 测量界面所需的信息
 */

public class MeasureBean implements Serializable {
    private ProfileBean profile;
    private DeviceBean device;
    /**
     * 是否是共享
     */
    private boolean isShare;
    /**
     * 贴的mac地址
     */
    private String patchMac;
    private String hardVersion;
    private String serialNum;

    public MeasureBean(DeviceBean device) {
        this.device = device;
    }

    public MeasureBean(ProfileBean profile, DeviceBean device) {
        this.profile = profile;
        this.device = device;
    }

    public ProfileBean getProfile() {
        return profile;
    }

    public void setProfile(ProfileBean profile) {
        this.profile = profile;
    }

    public String getMacaddress() {
        return device.getMacaddress();
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public boolean isShare() {
        return isShare;
    }

    public void setShare(boolean share) {
        isShare = share;
    }

    public String getPatchMac() {
        return patchMac;
    }

    public void setPatchMac(String patchMac) {
        this.patchMac = patchMac;
    }

    public String getHardVersion() {
        return hardVersion;
    }

    public void setHardVersion(String hardVersion) {
        this.hardVersion = hardVersion;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }
}
