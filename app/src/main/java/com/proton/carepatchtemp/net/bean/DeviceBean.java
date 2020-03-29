package com.proton.carepatchtemp.net.bean;

import com.google.gson.annotations.SerializedName;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.utils.UIUtils;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * Created by luochune on 2018/3/12.
 */

public class DeviceBean extends LitePalSupport implements Serializable {


    /**
     * id : 310
     * name : P03
     * type : 2
     * btaddress : 24:71:89:D8:D1:BD
     * hint : 05-04 15:13
     * shareto : null
     * creator : 27
     * creatorname : 刘萌萌
     * sn : 11A17040321
     * version : V1.0.4
     */
    /**
     * 设备id
     */
    @SerializedName("id")
    private int deviceId;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 设备类型: 2-P02,3-P03,4-P04,5-P05
     */
    @SerializedName("devicetype")
    private int deviceType;
    /**
     * 设备蓝牙地址
     */
    private String btaddress;
    /**
     * 最近使用时间	String	MM-dd HH:mm
     */
    @SerializedName("hint")
    private String lastUseTime;
    /**
     * 创建用户id
     */
    private int creator;
    /**
     * 创建用户姓名
     */
    private String creatorname;
    /**
     * 序列号
     */
    @SerializedName(value = "sn", alternate = "basesn")
    private String sn;
    /**
     * 设备固件版本
     */
    private String version;
    /**
     * 与设备类型匹配的设备名称
     */
    private String deviceTypeName;

    //充电器相关字段
    private String ssid;
    @SerializedName("wifiname")
    private String wifiName;
    /**
     * 是否是充电器
     */
    private boolean isDocker;
    @SerializedName("lastupdated")
    private long lastUpdated;
    private String firmwareType;

    public String getDeviceTypeName() {
        if (isDocker()) {
            return UIUtils.getString(R.string.string_carepatch_charger);
        }
        return UIUtils.getString(R.string.string_carepatch_patch);
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public int getId() {
        return deviceId;
    }

    public void setId(int id) {
        this.deviceId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int devicetype) {
        this.deviceType = devicetype;
    }

    public String getBtaddress() {
        return btaddress;
    }

    public void setBtaddress(String btaddress) {
        this.btaddress = btaddress;
    }

    public String getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(String hint) {
        this.lastUseTime = hint;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getCreatorname() {
        return creatorname;
    }

    public void setCreatorname(String creatorname) {
        this.creatorname = creatorname;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public boolean isDocker() {
        return isDocker;
    }

    public void setDocker(boolean docker) {
        isDocker = docker;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getFirmwareType() {
        return firmwareType;
    }

    public void setFirmwareType(String firmwareType) {
        this.firmwareType = firmwareType;
    }
}
