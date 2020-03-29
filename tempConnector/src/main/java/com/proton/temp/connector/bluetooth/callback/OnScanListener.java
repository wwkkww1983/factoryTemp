package com.proton.temp.connector.bluetooth.callback;

import com.proton.temp.connector.bean.DeviceBean;

/**
 * Created by 王梦思 on 2017/7/8.
 * 扫描监听器
 */

abstract public class OnScanListener {
    /**
     * 发现设备
     */
    abstract public void onDeviceFound(DeviceBean device);

    /**
     * 开始扫描
     */
    public void onScanStart() {
    }

    /**
     * 停止搜索
     */
    public void onScanStopped() {
    }

    /**
     * 搜索取消
     */
    public void onScanCanceled() {
    }
}
