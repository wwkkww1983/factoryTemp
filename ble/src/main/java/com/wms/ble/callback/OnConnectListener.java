package com.wms.ble.callback;

import com.wms.ble.bean.ScanResult;

/**
 * Created by 王梦思 on 2017/7/8.
 * connect listener
 */

abstract public class OnConnectListener {
    /**
     * 连接成功
     */
    public void onConnectSuccess() {
    }

    public void onConnectSuccess(ScanResult result) {
    }

    /**
     * 连接失败
     */
    public void onConnectFaild() {
    }

    /**
     * 断开连接了
     */
    public void onDisconnect(boolean isManual) {
    }

    /**
     * 设备需要升级
     */
    public void onDeviceNeedUpdate(){
    }
}
