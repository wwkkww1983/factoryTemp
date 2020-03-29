package com.proton.temp.connector.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.proton.temp.connector.utils.Utils;

/**
 * 充电器数据
 */
public class DockerDataBean {

    @JSONField(name = "mac")
    private String macaddress;
    /**
     * 硬件版本号
     */
    @JSONField(name = "ver")
    private String hardVersion;
    /**
     * 电量
     */
    @JSONField(name = "bat")
    private int battery;
    /**
     * 是否在充电
     */
    @JSONField(name = "chg_sta")
    private boolean charge;
    /**
     * 原始温度
     */
    @JSONField(name = "raw_temp")
    private String rawTemp;
    /**
     * 算法温度
     */
    @JSONField(name = "alg_temp")
    private String algorithmTemp;
    /**
     * 信号强度
     */
    private int rssi;
    @JSONField(name = "sta")
    private int algorithmStatus;
    @JSONField(name = "alg_gstr")
    private int algorithmGesture;

    @JSONField(name = "pct")
    private int percent;
    /**
     * 包序
     */
    @JSONField(name = "pkt")
    private int packageNumber;

    /**
     * 蓝牙信号强度
     */
    @JSONField(name = "ble_rssi")
    private int bleRssi;

    /**
     * wifi信号强度
     */
    @JSONField(name = "wifi_rssi")
    private int wifiRssi;

    public String getHardVersion() {
        return hardVersion;
    }

    public void setHardVersion(String hardVersion) {
        this.hardVersion = hardVersion;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public String getRawTemp() {
        return rawTemp;
    }

    public void setRawTemp(String rawTemp) {
        this.rawTemp = rawTemp;
    }

    @Override
    public String toString() {
        return "DockerDataBean{" +
                ", hardVersion='" + hardVersion + '\'' +
                ", battery=" + battery +
                ", rawTemp=" + rawTemp +
                ", bleRssi=" + bleRssi +
                ", wifiRssi=" + wifiRssi +
                '}';
    }


    public String getMacaddress() {
        return Utils.parseBssid2Mac(macaddress);
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public boolean isCharge() {
        return charge;
    }

    public void setCharge(boolean charge) {
        this.charge = charge;
    }

    public String getAlgorithmTemp() {
        return algorithmTemp;
    }

    public void setAlgorithmTemp(String algorithmTemp) {
        this.algorithmTemp = algorithmTemp;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getAlgorithmStatus() {
        return algorithmStatus;
    }

    public void setAlgorithmStatus(int algorithmStatus) {
        this.algorithmStatus = algorithmStatus;
    }


    public int getAlgorithmGesture() {
        return algorithmGesture;
    }

    public void setAlgorithmGesture(int algorithmGesture) {
        this.algorithmGesture = algorithmGesture;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(int packageNumber) {
        this.packageNumber = packageNumber;
    }

    public int getBleRssi() {
        return bleRssi;
    }

    public void setBleRssi(int bleRssi) {
        this.bleRssi = bleRssi;
    }

    public int getWifiRssi() {
        return wifiRssi;
    }

    public void setWifiRssi(int wifiRssi) {
        this.wifiRssi = wifiRssi;
    }
}
