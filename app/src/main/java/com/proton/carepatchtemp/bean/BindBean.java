package com.proton.carepatchtemp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 王梦思 on 2018/9/25.
 * <p/>
 */
public class BindBean implements Serializable {
    private String uid;
    private String token;
    @SerializedName("bind")
    private boolean isBind;
    private String checkToken;
    private int type;
    @SerializedName("basebtaddress")
    private String macaddress;
    private String mobile;
    /**
     * 是否是新用户
     */
    private boolean isNew;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }

    public String getCheckToken() {
        return checkToken;
    }

    public void setCheckToken(String checkToken) {
        this.checkToken = checkToken;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
