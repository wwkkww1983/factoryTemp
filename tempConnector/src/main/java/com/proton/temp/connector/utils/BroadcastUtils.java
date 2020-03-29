package com.proton.temp.connector.utils;

import com.proton.temp.connector.bean.DeviceType;
import com.proton.temp.connector.bean.TempDataBean;
import com.proton.temp.connector.bluetooth.utils.BleUtils;
import com.wms.ble.bean.ScanResult;
import com.wms.logger.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by 王梦思 on 2018/12/4.
 * <p/>
 */
public class BroadcastUtils {
    /**
     * 是不是广播形式
     */
    public static boolean isBroadcast(byte[] scanRecord) {
        if (scanRecord != null) {
            String scanRecordHex = BleUtils.bytesToHexString(scanRecord);
            return scanRecordHex != null && (scanRecordHex.startsWith("12ff") || scanRecordHex.startsWith("14ff"));
        }
        return false;
    }

    /**
     * 解析广播包中的版本号
     */
    public static String getHardVersionByBroadcast(byte[] scanRecord) {
        if (scanRecord.length < 18) {
            return "";
        }

        try {
            int first = Integer.parseInt(new String(new byte[]{scanRecord[16]}));
            int second = Integer.parseInt(new String(new byte[]{scanRecord[17]}));
            int third = Integer.parseInt(new String(new byte[]{scanRecord[18]}));
            return first + "." + second + "." + third;
        } catch (NumberFormatException e) {
            return "";
        }
    }

    /**
     * 解析广播包中的温度
     */
    public static List<TempDataBean> getTempByBroadcast(byte[] scanRecord, int packageNumber) {
        List<TempDataBean> allTemps = new ArrayList<>();
        if (scanRecord.length < 15) {
            return allTemps;
        }

        byte[] firstTemp = new byte[2];
        firstTemp[0] = scanRecord[11];
        firstTemp[1] = scanRecord[10];

        byte[] secondTemp = new byte[2];
        secondTemp[0] = scanRecord[13];
        secondTemp[1] = scanRecord[12];

        byte[] thirdTemp = new byte[2];
        thirdTemp[0] = scanRecord[15];
        thirdTemp[1] = scanRecord[14];

        allTemps.add(new TempDataBean(Integer.parseInt(Objects.requireNonNull(BleUtils.bytesToHexString(firstTemp)), 16) / 100.0f, 1, packageNumber));
        allTemps.add(new TempDataBean(Integer.parseInt(Objects.requireNonNull(BleUtils.bytesToHexString(secondTemp)), 16) / 100.0f, 1, packageNumber));
        allTemps.add(new TempDataBean(Integer.parseInt(Objects.requireNonNull(BleUtils.bytesToHexString(thirdTemp)), 16) / 100.0f, 1, packageNumber));
        return allTemps;
    }

    /**
     * 解析广播包中的包序
     */
    public static int getPackageNumber(byte[] scanRecord) {
        if (scanRecord.length < 8) {
            return 0;
        }
        return scanRecord[8] & 0xFF;
    }

    /**
     * 解析广播包中的电量
     */
    public static int getBattery(byte[] scanRecord) {
        if (scanRecord.length < 9) {
            return 0;
        }
        return Integer.parseInt(Objects.requireNonNull(BleUtils.bytesToHexString(new byte[]{scanRecord[9]})), 16);
    }

    /**
     * 解析广播包中的电量
     */
    public static boolean isCharge(byte[] scanRecord) {
        if (scanRecord.length < 9) {
            return false;
        }
        return (scanRecord[9] & 0x80) == 0x80;
    }

    public static String getMacaddressByBroadcat(byte[] scanRecord) {
        String mac;
        if (isBroadcast(scanRecord)) {
            mac = getMacaddressByBroadcastNew(scanRecord);
        } else {
            mac = getMacaddressByBroadcastOld(scanRecord);
        }
        return mac;
    }

    /**
     * 解析广播包中的mac地址(广播模式)
     */
    public static String getMacaddressByBroadcastNew(byte[] scanRecord) {
        if (scanRecord.length < 7) {
            return "";
        }
        byte[] macbytes = new byte[6];
        System.arraycopy(scanRecord, 2, macbytes, 0, macbytes.length);
        return Utils.parseBssid2Mac(BleUtils.bytesToHexString(macbytes)).toUpperCase();
    }

    /**
     * 解析广播包中的mac地址(老版本)
     */
    public static String getMacaddressByBroadcastOld(byte[] scanRecord) {
        if (scanRecord.length < 7) {
            return "";
        }
        byte[] macbytes = new byte[6];
        System.arraycopy(scanRecord, 4, macbytes, 0, macbytes.length);
        return Utils.parseBssid2Mac(BleUtils.bytesToHexString(macbytes)).toUpperCase();
    }

    /**
     * 解析体温贴设备类型
     * P02	0002
     * P03	0102
     * P04	0202
     * P05	0302
     *
     * @param scanRecord 广播包
     */
    public static DeviceType parseDeviceType(byte[] scanRecord) {

        DeviceType type = DeviceType.None;

        if (scanRecord == null || scanRecord.length < 3) {
            return type;
        }

        byte[] data = new byte[2];
        if (isBroadcast(scanRecord)) {
            type = DeviceType.P03;
            data[0] = scanRecord[19];
            data[1] = scanRecord[20];
        } else {
            data[0] = scanRecord[2];
            data[1] = scanRecord[3];
        }

        int typeInt = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getShort();
        if (typeInt == Integer.parseInt("0002", 16)) {
            type = DeviceType.P02;
        } else if (typeInt == Integer.parseInt("0102", 16)) {
            type = DeviceType.P03;
        } else if (typeInt == Integer.parseInt("0202", 16)) {
            type = DeviceType.P04;
        } else if (typeInt == Integer.parseInt("0302", 16)) {
            type = DeviceType.P05;
        } else if (typeInt == Integer.parseInt("0402", 16)) {
            type = DeviceType.P06;
        } else if (typeInt == Integer.parseInt("0502", 16)) {
//            type = DeviceType.P07;
            type = DeviceType.None;
        } else if (typeInt == Integer.parseInt("0602", 16)) {
            type = DeviceType.P08;
        } else if (typeInt == Integer.parseInt("0802", 16)) {
            //过滤掉儿童医院的体温贴0208(p10)
//            type = DeviceType.P10;
            type = DeviceType.None;
        } else if (typeInt == Integer.parseInt("0902", 16)) {
            //过滤掉p11的贴
//            type = DeviceType.P11;
            type = DeviceType.None;
        }
        return type;
    }

    /**
     * 判断是否是iBeacon设备
     *
     * @return
     */
    public static boolean isIBeaconDevice(byte[] scanRecord) {
        if (scanRecord[5] == 0x4C && scanRecord[6] == 0x00 && scanRecord[7] == 0x02 && scanRecord[8] == 0x15) {
            //这是一个iBeacon设备。
            //注意这里004C的判断，在广播里厂商id这2个字节的数据是颠倒的
            int tempLow = Integer.parseInt(Objects.requireNonNull(BleUtils.bytesToHexString(new byte[]{scanRecord[34]})), 16);
            return true;
        }
        return false;
    }

    /**
     * 09ff开头是升级模式广播(除p02)
     */
    public static boolean isUpdateStatus(ScanResult result) {
        byte[] scanRecord = result.getScanRecord();
        return (BleUtils.bytesToHexString(scanRecord).startsWith("09ff")
                && parseDeviceType(scanRecord) != DeviceType.P02
                && parseDeviceType(scanRecord) != DeviceType.None)
                || "OAD THEM".equals(result.getDevice().getName());
    }
}
