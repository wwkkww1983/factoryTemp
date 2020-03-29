package com.proton.temp.connector.bean;

import com.wms.ble.utils.Logger;

public enum DeviceType {
    /**
     * 不同版本设备，p02只有蓝牙，p03带有蓝牙和wifi，蓝牙部分和p02有部分区别
     */
    P02(2), P03(3), P04(4), P05(5), P06(6), P07(7), P08(8), P10(10), P11(11), None(-1);

    private int value;

    DeviceType(int value) {
        this.value = value;
    }

    public static String getDeviceTypeBroadcast(DeviceType deviceType) {
        switch (deviceType) {
            case P02:
                return "0002";
            case P03:
                return "0102";
            case P04:
                return "0202";
            case P05:
                return "0302";
            case P06:
                return "0402";
            case P07:
                return "0502";
            case P08:
                return "0602";
            case P10:
                return "0802";
            case P11:
                return "0902";
        }
        return null;
    }

    public static int getDeviceTypeByTypeDesc(String type) {
        Logger.w("type is :",type);
        switch (type) {
            case "0002":
                return 2;
            case "0102":
                return 3;
            case "0202":
                return 4;
            case "0302":
                return 5;
            case "0402":
                return 6;
            case "0502":
                return 7;
            case "0602":
                return 8;
            case "0702":
                return 9;
            case "0802":
                return 10;
        }
        return 0;
    }

    public static DeviceType valueOf(int value) {
        switch (value) {
            case 2:
                return P02;
            case 3:
                return P03;
            case 4:
                return P04;
            case 5:
                return P05;
            case 6:
                return P06;
            case 7:
                return P07;
            case 8:
                return P08;
            case 10:
                return P10;
            case 11:
                return P11;
            default:
                return None;
        }
    }

    public int getValue() {
        return value;
    }
}
