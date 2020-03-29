package com.proton.carepatchtemp.utils;

import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.temp.connector.utils.ConnectorSetting;

/**
 * Created by wangmengsi on 2018/2/26.
 */
public class Settings {
    /**
     * 手机号码加密Key
     */
    public static final String ENCRYPT_KEY = "proton521liucome";
    public static final String COMPANY = "protontekTemp";
    /**
     * 最高温度
     */
    public static final float DEFAULT_HIGHTEST_TEMP = 37.50f;
    /**
     * 最低温度
     */
    public static final float DEFAULT_LOWEST_TEMP = 34.00f;
    /**
     * 最小电量
     */
    public static final int MIN_BATTERY = 20;
    /**
     * 温度曲线最小显示温度
     */
    public static final float CURVE_MIN_TEMP = 30.0F;
    /**
     * 温度曲线最大显示温度
     */
    public static final float CURVE_MAX_TEMP = 42.0F;
    /**
     * 默认提醒时间
     */
    public static final long DEFAULT_WARM_DURATION = 30 * 60000;
    /**
     * 测量viewmodel的activity
     */
    public static final Class<HomeActivity> MEASURE_ACTIVITY = HomeActivity.class;
    /**
     * 断开连接弹框超时时间
     */
    public static long RECONNECT_TIME = 15 * 60000;

    /**
     * 重连一次的时长（默认是15s）
     */
    public long ONE_CONNECT_TIME = 15000;

    /**
     * 蓝牙断开连接重连次数 40次重连时间大概10分钟
     */
    public int BLUETOOTH_RECONNECT_COUNT = (int) (RECONNECT_TIME / ONE_CONNECT_TIME);

    /**
     * 蓝牙断开连接重连次数
     */
    public static final int BLUETOOTH_RECONNECT_COUNT_NOT_OPEN_BLUETOOTH = (int) (RECONNECT_TIME / ConnectorSetting.NO_BLUETOOTH_RECONNECT_TIME);
    /**
     * MQTT断开连接重连次数
     */
    public static final int WIFI_RECONNECT_COUNT = (int) (RECONNECT_TIME / 30 * 1000);


    public void setOneConnectTime(long oneConnectTime) {
        this.ONE_CONNECT_TIME = oneConnectTime;
    }

    public int getBluetoothReconnectCount() {
        return (int) (RECONNECT_TIME / ONE_CONNECT_TIME);
    }


}
