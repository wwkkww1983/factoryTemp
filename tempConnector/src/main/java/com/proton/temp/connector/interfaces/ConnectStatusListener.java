package com.proton.temp.connector.interfaces;

/**
 * Created by wangmengsi on 2018/3/15.
 * 连接器回调
 */
public abstract class ConnectStatusListener {

    /**
     * 连接成功
     */
    public void onConnectSuccess() {
    }

    /**
     * 连接失败
     */
    public void onConnectFaild() {
    }

    /**
     * 断开连接了
     *
     * @param isManual 是否是手动调用连接断开的
     */
    public void onDisconnect(boolean isManual) {
    }

    /**
     * 接收重连次数
     * @param retryCount 当前重连次数
     * @param leftCount 剩余次数
     * @param totalTime 重连总耗时
     */
    public void receiveReconnectTimes(int retryCount, int leftCount,long totalTime) {
    }
    /**
     * mqtt连接如果底座换了贴，则回调该方法
     */
    public void receiveNotSampleDevice(String oldMac, String newMac) {
    }

    /**
     * 底座离线
     */
    public void receiveDockerOffline(boolean isOffline) {
    }

    /**
     * 设备准备页面断开，6秒内没有重连则显示弹框---蓝牙连接
     * wifi连接，30秒内没有重连则显示弹框
     */
    public void showBeforeMeasureDisconnect() {

    }

}
