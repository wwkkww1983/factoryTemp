package com.proton.carepatchtemp.bean;

import com.google.gson.annotations.SerializedName;
import com.proton.carepatchtemp.utils.Utils;

import java.io.Serializable;

/**
 * Created by wangmengsi on 2018/4/28.
 * mqtt共享温度
 */

public class ShareTempBean implements Serializable {
    /**
     * 请求码
     */
    private int code;
    /**
     * 当前温度
     */
    @SerializedName("rt")
    private float currentTemp;
    /**
     * 最高温度
     */
    @SerializedName("top")
    private float highestTemp;
    /**
     * 共享的用户id
     */
    @SerializedName("uid")
    private long sharedUid;
    /**
     * 档案id
     */
    @SerializedName("pid")
    private long profileId;

    public ShareTempBean(int code) {
        this.code = code;
    }

    public ShareTempBean(int code, long sharedUid) {
        this.code = code;
        this.sharedUid = sharedUid;
    }

    public ShareTempBean() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public float getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(float currentTemp) {
        this.currentTemp = Float.parseFloat(Utils.formatTempToStr(currentTemp));
    }

    public float getHighestTemp() {
        return highestTemp;
    }

    public void setHighestTemp(float highestTemp) {
        this.highestTemp = highestTemp;
    }

    public long getSharedUid() {
        return sharedUid;
    }

    public void setSharedUid(long sharedUid) {
        this.sharedUid = sharedUid;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }
}
