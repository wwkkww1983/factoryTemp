package com.proton.carepatchtemp.bean;

/**
 * Created by yuxiongfeng.
 * Date: 2019/8/22
 */
public class WechatUserInfo {

    /**
     * uid : null
     * token : null
     * bind : false
     * success : true
     * nickName : 站在花下
     * avatar : http://thirdwx.qlogo.cn/mmopen/vi_32/ica2sO8ico1AsiaoYlJwgIx0u5ibUVcVoJmRT0DkeTfR2c7rgicicPRbsv7cZMA0Hmq6IIprbriaMsdajDgiaT80Ric6AFQ/132
     * checkToken : 4F247D8B5BC003EE48D54D3B6213F3FE
     * expired : 1566480185032
     */

    private String uid;
    private String token;
    private boolean bind;
    private boolean success;
    private String nickName;
    private String avatar;
    private String checkToken;
    private long expired;
    private String mobile;//手机号

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
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCheckToken() {
        return checkToken;
    }

    public void setCheckToken(String checkToken) {
        this.checkToken = checkToken;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    @Override
    public String toString() {
        return "WechatUserInfo{" +
                "uid='" + uid + '\'' +
                ", token='" + token + '\'' +
                ", bind=" + bind +
                ", success=" + success +
                ", nickName='" + nickName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", checkToken='" + checkToken + '\'' +
                ", expired=" + expired +
                ", mobile='" + mobile + '\'' +
                ", isNew=" + isNew +
                '}';
    }
}
