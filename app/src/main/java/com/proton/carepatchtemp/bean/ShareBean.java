package com.proton.carepatchtemp.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.proton.temp.connector.bean.DeviceType;

import java.io.Serializable;

/**
 * Created by wangmengsi on 2017/10/17.
 */
public class ShareBean implements Serializable {
    /**
     * 1-男，2-女
     */
    @SerializedName("gender")
    private int sex;
    private int age;
    private String avatar;
    /**
     * 贴的mac地址
     */
    @SerializedName("btaddress")
    private String macaddress;
    /**
     * 充电器的mac地址
     */
    @SerializedName("baseaddress")
    private String dockerMacaddress;

    @SerializedName("realname")
    private String realName;
    /**
     * 分享人 uid
     */
    @SerializedName("creator")
    private String id;
    @SerializedName("profileid")
    private long profileId;
    @SerializedName("devicetype")
    private String deviceType;
    /**
     * 当前共享是否正在测量
     */
    @SerializedName("share")
    private int isMeasuring;

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getId() {
        return Integer.parseInt(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public DeviceType getDeviceType() {
        if (!TextUtils.isEmpty(dockerMacaddress)) {
            return DeviceType.P03;
        } else {
            return DeviceType.P02;
        }
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDockerMacaddress() {
        return dockerMacaddress;
    }

    public void setDockerMacaddress(String dockerMacaddress) {
        this.dockerMacaddress = dockerMacaddress;
    }

    public boolean isMeasuring() {
        return isMeasuring == 1;
    }

    public void setIsMeasuring(int isMeasuring) {
        this.isMeasuring = isMeasuring;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }
}
