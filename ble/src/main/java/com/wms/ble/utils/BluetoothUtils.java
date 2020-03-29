package com.wms.ble.utils;

import com.wms.ble.BleOperatorManager;

public class BluetoothUtils {
    /**
     * 打开蓝牙
     */
    public static void openBluetooth() {
        BleOperatorManager.getInstance().openBluetooth();
    }

    /**
     * 关闭蓝牙
     */
    public static void closeBluetooth() {
        if (isBluetoothOpened())
            BleOperatorManager.getInstance().closeBluetooth();
    }

    /**
     * 蓝牙是否打开
     */
    public static boolean isBluetoothOpened() {
        return BleOperatorManager.getInstance().isBluetoothOpened();
    }

    /**
     * 是否支持ble
     */
    public static boolean isSupportBle() {
        return BleOperatorManager.getInstance().isSupportBle();
    }
}
