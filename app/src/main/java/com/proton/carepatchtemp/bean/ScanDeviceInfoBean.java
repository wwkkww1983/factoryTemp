package com.proton.carepatchtemp.bean;

import com.google.gson.annotations.SerializedName;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.temp.connector.bean.DeviceType;

import java.io.Serializable;

/**
 * Created by wangmengsi on 2017/10/17.
 */
public class ScanDeviceInfoBean implements Serializable {
    @SerializedName("btaddress")
    private String macaddress;
    @SerializedName("sn")
    private String serialNum;
    private String version;
    private int type;
    private String factory;

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public int getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
