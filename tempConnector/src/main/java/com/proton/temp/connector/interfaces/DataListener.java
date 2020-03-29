package com.proton.temp.connector.interfaces;

import com.proton.temp.connector.bean.TempDataBean;

import java.util.List;

/**
 * Created by wangmengsi on 2018/3/15.
 * 只接收数据
 */
public class DataListener {
    public void receiveSourceData(String data) {
    }

    /**
     * 接收原始温度
     */
    public void receiveRawTemp(float temp) {
    }

    /**
     * 接收当前温度
     */
    public void receiveCurrentTemp(float currentTemp) {
    }

    /**
     * 接收当前温度
     */
    public void receiveCurrentTemp(TempDataBean currentTemp) {
    }

    /**
     * 接收当前温度
     */
    public void receiveCurrentTemp(float currentTemp, long time) {
    }

    /**
     * 接收当前温度
     *
     * @since v1.5版本及其之后
     */
    public void receiveCurrentTemp(List<TempDataBean> temps) {
    }

    /**
     * 读取电量
     */
    public void receiveBattery(Integer battery) {
    }

    /**
     * 是否充电
     */
    public void receiveCharge(boolean isCharge) {
    }

    /**
     * 读取序列号
     */
    public void receiveSerial(String serial) {
    }

    /**
     * 读取硬件版本号
     */
    public void receiveHardVersion(String hardVersion) {
    }

    /**
     * 接收缓存温度数量
     */
    public void receiveCacheTotal(Integer cacheCount) {
    }

    /**
     * 接收缓存温度
     */
    public void receiveCacheTemp(List<TempDataBean> cacheTemps) {
    }

    /**
     * 接收包序
     */
    public void receivePackageNumber(int packageNumber) {
    }

    /**
     * 判断体温贴是否断裂
     *
     * @param isEnable
     */
    public void judgeCarepatchEnable(boolean isEnable) {

    }

    /**
     * @param bleRssi  蓝牙信号强度
     * @param wifiRssi wifi信号强度
     */
    public void receiveBleAndWifiRssi(Integer bleRssi, Integer wifiRssi) {
    }

}
