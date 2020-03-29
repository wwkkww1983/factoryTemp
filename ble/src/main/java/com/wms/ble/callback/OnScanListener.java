package com.wms.ble.callback;

import com.wms.ble.bean.ScanResult;

/**
 * Created by 王梦思 on 2017/7/8.
 * 扫描监听器
 */

public class OnScanListener {

    /**
     * 开始扫描
     */
    public void onScanStart() {
    }

    /**
     * 发现设备
     */
    public void onDeviceFound(ScanResult result){
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
