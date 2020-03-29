package com.proton.carepatchtemp.net.bean;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * Created by luochune on 2018/4/18.
 */

public class UpdateFirmwareBean extends LitePalSupport implements Serializable {

    /**
     * version : V1.0.6
     * content : 修复某些使用情况下，异常关机问题。
     * url : http://feaecg1st.oss-cn-hangzhou.aliyuncs.com/TEMP_2017-06-29-17-35-26TEMP_V1.0.6
     */
    private String version;
    private String content;
    @SerializedName("address")
    private String url;
    private int deviceType;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "UpdateFirmwareBean{" +
                "version='" + version + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }
}
