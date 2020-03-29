package com.wms.ble.callback;

/**
 * Created by wangmengsi on 2017/7/17.
 * 通知读取监听
 */

public abstract class OnReadCharacterListener {
    public void onFail() {
    }

    public void onSuccess(byte[] data) {
    }
}
