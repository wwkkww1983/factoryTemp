package com.proton.carepatchtemp.net.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by MoonlightSW on 2016/12/20.
 */

public class ShareHistoryBean implements Serializable {
    @SerializedName("uid")
    private long sharedUid;
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getSharedUid() {
        return sharedUid;
    }

    public void setSharedUid(long sharedUid) {
        this.sharedUid = sharedUid;
    }
}
