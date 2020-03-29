package com.proton.carepatchtemp.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 王梦思 on 2018/12/5.
 * <p/>
 */
public class MessageBean {
    @SerializedName("id")
    private long messageId;
    private String title;
    private String content;
    @SerializedName("subContent")
    private String description;
    private String buttonContent;
    /**
     * 用户是否可以关闭
     */
    private int closable;
    /**
     * 是否跳转 0 不跳转 1 跳转
     */
    private int jumpStatus;
    private String url;
    private int flag;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getButtonContent() {
        return buttonContent;
    }

    public void setButtonContent(String buttonContent) {
        this.buttonContent = buttonContent;
    }

    public boolean isClosable() {
        return closable == 1;
    }

    public void setClosable(int closable) {
        this.closable = closable;
    }

    public int getJumpStatus() {
        return jumpStatus;
    }

    public void setJumpStatus(int jumpStatus) {
        this.jumpStatus = jumpStatus;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
}
