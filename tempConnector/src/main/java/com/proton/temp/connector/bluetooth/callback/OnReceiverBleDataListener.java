package com.proton.temp.connector.bluetooth.callback;

import com.proton.temp.connector.bean.TempDataBean;

import java.util.List;

/**
 * 数据接收监听器
 */
public abstract class OnReceiverBleDataListener {
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
     * 接收当前温度值
     */
    public void receiveCurrentTemp(TempDataBean currentTemp) {
    }

    /**
     * 接收当前温度
     *
     * @since v1.5版本及其之后
     */
    public void receiveCurrentTemp(List<TempDataBean> temps) {
    }

    /**
     * 缓存温度读取完成
     */
    public void onReadCacheFinish() {
    }

    /**
     * 判断体温贴是否断裂
     *
     * @param isEnable
     */
    public void judgeCarepatchEnable(boolean isEnable) {

    }

    /**
     * 体温贴校准回调
     *
     * @param calibrateResult
     */
    public void calibrateTempResult(boolean calibrateResult) {

    }
}