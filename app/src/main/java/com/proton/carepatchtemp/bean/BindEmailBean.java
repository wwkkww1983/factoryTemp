package com.proton.carepatchtemp.bean;

import java.io.Serializable;

/**
 * Created by yuxiongfeng.
 * Date: 2019/5/10
 */
public class BindEmailBean implements Serializable{
    /**
     * uid : 440
     * token : 186205C747A92F2DD275B90D55AD601C
     * bind : false
     * mailBind : true
     * mail : cheng.chen@protontek.com
     * mobile : null
     */

    private int uid;
    private String token;
    private boolean bind;
    private boolean mailBind;
    private String mail;
    private Object mobile;
    private String cert;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isMailBind() {
        return mailBind;
    }

    public void setMailBind(boolean mailBind) {
        this.mailBind = mailBind;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Object getMobile() {
        return mobile;
    }

    public void setMobile(Object mobile) {
        this.mobile = mobile;
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }
}
