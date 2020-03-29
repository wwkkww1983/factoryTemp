package com.proton.carepatchtemp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wangmengsi on 2017/10/17.
 * 推送信息
 */
public class PushBean implements Serializable {
    @SerializedName("btaddress")
    private String patchMacAddress;
    private String birthday;
    @SerializedName("senderid")
    private int shareUid;
    @SerializedName("gender")
    private int sex;
    private String name;
    private String avatar;
    private String type;
    private String pushtext;
    @SerializedName("baseaddress")
    private String dockerMacaddress;
    @SerializedName("profileid")
    private long profileId;

    public String getPatchMacAddress() {
        return patchMacAddress;
    }

    public void setPatchMacAddress(String patchMacAddress) {
        this.patchMacAddress = patchMacAddress;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getShareUid() {
        return shareUid;
    }

    public void setShareUid(int shareUid) {
        this.shareUid = shareUid;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPushtext() {
        return pushtext;
    }

    public void setPushtext(String pushtext) {
        this.pushtext = pushtext;
    }

    public String getDockerMacaddress() {
        return dockerMacaddress;
    }

    public void setDockerMacaddress(String dockerMacaddress) {
        this.dockerMacaddress = dockerMacaddress;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }
}
