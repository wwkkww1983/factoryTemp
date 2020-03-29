package com.proton.carepatchtemp.utils.ble;

import android.annotation.TargetApi;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by yuxiongfeng.
 * Date: 2019/5/14
 */
public class BleUtil {

    /**
     * advertiseMode说明：广播发送的频率
     * ADVERTISE_MODE_LOW_LATENCY 100ms
     * ADVERTISE_MODE_BALANCED  250ms
     * ADVERTISE_MODE_LOW_POWER 1s
     * 蓝牙广播的设置（模式 时长  是否可连接  信号强度）
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static AdvertiseSettings createAdvSettings() {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);//平衡模式
        builder.setConnectable(false);//设置广播是否可连接
        builder.setTimeout(0);//设置为0表示无限发送
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);//广播的信号强度
        return builder.build();
    }


    /**
     * 创建广播数据包
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static AdvertiseData createAdvertiseData() {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.setIncludeDeviceName(true);//是否包含本地设备名字
        AdvertiseData adv = builder.build();
        return adv;
    }
}
