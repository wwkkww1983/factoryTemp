package com.proton.temp.connector.bluetooth.utils;

import android.text.TextUtils;

import com.proton.temp.connector.bean.DockerDataBean;
import com.proton.temp.connector.bean.TempDataBean;
import com.wms.logger.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王梦思 on 2017/7/5.
 */

public class BleUtils {

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String bytes2BinaryString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
        }
        return stringBuilder.toString();
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }

        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String bytesToHexStringTemp(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }

        return stringBuilder.toString().substring(2, 4) + stringBuilder.toString().substring(0, 2);
    }

    /**
     * 解析v1.0温度
     */
    public static TempDataBean parseTemp(byte[] value) {
        return new TempDataBean(System.currentTimeMillis(), Integer.parseInt(BleUtils.bytesToHexStringTemp(value), 16) / 100.0f, 24);
    }

    /**
     * 解析v1.5温度
     */
    public static List<TempDataBean> parseTempV1_5(byte[] value) {
        List<TempDataBean> temps = new ArrayList<>();
        if (value.length % 2 == 0) {
            String hex = bytesToHexString(value);
            if (TextUtils.isEmpty(hex)) return temps;
            for (int i = 0; i < value.length / 2; i++) {
                String tempHex = hex.substring(i * 4, (i + 1) * 4);
                if (TextUtils.isEmpty(tempHex) || tempHex.equalsIgnoreCase("0000")) break;
                int data = ByteBuffer.wrap(hexStringToBytes(tempHex)).order(ByteOrder.LITTLE_ENDIAN).getShort();
                //温度
                float temp = (data & 0x3FFF) / 100.0f;
                //采样
                int sample = (data & 0xC000) >> 14;
                if (sample == 1) {
                    sample = 1;
                } else if (sample == 2) {
                    sample = 4;
                } else {
                    sample = 24;
                }
                Logger.w("实时温度:", temp, ", sample:", sample);
                temps.add(new TempDataBean(temp, sample));
            }
        }
        return temps;
    }

    /**
     * 解析v1.5温度
     */
    public static List<TempDataBean> parseTempV1_5(String value) {
        return parseTempV1_5(hexStringToBytes(value));
    }

    /**
     * 解析缓存温度
     */
    public static List<TempDataBean> parseCacheTemp(byte[] value) {
        String cacheTemp = BleUtils.bytesToHexString(value);
        //f208 f208 7909 0000 0000 0000 0000 0000 0000 0000
        List<TempDataBean> cacheTempList = new ArrayList<>();
        if (cacheTemp == null || cacheTemp.length() < 20) return cacheTempList;
        for (int i = 0; i < 10; i++) {
            String temp = cacheTemp.substring(i * 4, (i + 1) * 4);
            if (temp.equalsIgnoreCase("0000")) {
                continue;
            }
            cacheTempList.add(new TempDataBean(Integer.parseInt(temp.substring(2, 4) + temp.substring(0, 2), 16) / 100.0f, 24));
        }
        return cacheTempList;
    }

    /**
     * 解析缓存温度
     *
     * @param isV1_5 是否是1.5设备
     */
    public static List<TempDataBean> parseCacheTemp(byte[] values, boolean isV1_5) {
        List<TempDataBean> cacheTemps;
        if (isV1_5) {
            cacheTemps = parseTempV1_5(values);
        } else {
            cacheTemps = parseCacheTemp(values);
        }

        return cacheTemps;
    }

    /**
     * 计算每个缓存的时间
     */
    public static List<TempDataBean> getTempTime(List<TempDataBean> cacheTemps) {
        if (cacheTemps == null || cacheTemps.size() <= 0) return cacheTemps;
        //计算温度时间
        //用缓存的最后一个温度当做当前时间，然后倒推开始测量时间
        long lastTempTime = System.currentTimeMillis();
        for (int i = cacheTemps.size() - 1; i >= 0; i--) {
            long time = lastTempTime;
            if (i != cacheTemps.size() - 1) {
                time = cacheTemps.get(i + 1).getTime() - cacheTemps.get(i + 1).getSample() * 1000;
            }
            cacheTemps.get(i).setTime(time);
        }
        return cacheTemps;
    }

    /**
     * 解析mqtt的实际温度
     */
    public static List<TempDataBean> parseMqttTemp(DockerDataBean dockerData) {
        List<TempDataBean> tempList = new ArrayList<>();
        if (!TextUtils.isEmpty(dockerData.getRawTemp())
                && dockerData.getRawTemp().contains(",")) {
            String[] rawTemps = dockerData.getRawTemp().split(",");
            String[] algorithmTemps = dockerData.getAlgorithmTemp().split(",");
            for (int i = 0; i < rawTemps.length; i++) {
                float temp = Integer.parseInt(rawTemps[i]) / 100.0f;
                float algorithmTemp = Integer.parseInt(algorithmTemps[i]) / 100.0f;
                tempList.add(new TempDataBean(temp, algorithmTemp, 1, dockerData.getPackageNumber(), dockerData.getAlgorithmStatus(), dockerData.getAlgorithmGesture(), dockerData.getPercent()));
            }
        }
        return tempList;
    }
}
