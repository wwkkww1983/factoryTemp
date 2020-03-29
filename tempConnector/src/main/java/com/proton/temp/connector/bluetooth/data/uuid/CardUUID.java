package com.proton.temp.connector.bluetooth.data.uuid;

/**
 * Created by wangmengsi on 2017/8/7.
 * 心电卡uuid
 */

public class CardUUID implements IDeviceUUID {

    private static final String SERVICE_TEMP = "0000fff6-0000-1000-8000-00805f9b34fb";
    /**
     * 特征:体温，可读(v1.0及其以后)可订阅(v1.5及其以后)
     */
    private static final String CHARACTOR_TEMP = "0000fff7-0000-1000-8000-00805f9b34fb";
    /**
     * 特征:缓存温度
     */
    private static final String CHARACTOR_CACHE_TEMP = "0000fff8-0000-1000-8000-00805f9b34fb";
    /**
     * 特征:缓存温度   体温贴校准时候使用 关闭体温贴
     */
    private static final String CHARACTOR_CACHE_TEMP_SEND = "0000fff9-0000-1000-8000-00805f9b34fb";
    /**
     * 服务:设备信息服务
     */
    private static final String SERVICE_DEVICE_INFO = "0000180a-0000-1000-8000-00805f9b34fb";
    /**
     * 特征:电量（可读可订阅）
     */
    private static final String CHARACTOR_BATTERY = "0000fffa-0000-1000-8000-00805f9b34fb";
    /**
     * 特征:设备版本号（可读）
     */
    private static final String CHARACTOR_VERSION = "00002a26-0000-1000-8000-00805f9b34fb";
    /**
     * 特征:序列号（可读）
     */
    private static final String CHARACTOR_SEARIAL = "00002a25-0000-1000-8000-00805f9b34fb";

    @Override
    public String getServiceTemp() {
        return SERVICE_TEMP;
    }

    @Override
    public String getCharactorTemp() {
        return CHARACTOR_TEMP;
    }

    @Override
    public String getCharactorCacheTemp() {
        return CHARACTOR_CACHE_TEMP;
    }

    @Override
    public String getCharactorCacheTempSend() {
        return CHARACTOR_CACHE_TEMP_SEND;
    }

    @Override
    public String getDeviceInfoServiceUUID() {
        return SERVICE_DEVICE_INFO;
    }

    @Override
    public String getCharactorVersionUUID() {
        return CHARACTOR_VERSION;
    }

    @Override
    public String getCharactorBatteryUUID() {
        return CHARACTOR_BATTERY;
    }

    @Override
    public String getCharactorSearialUUID() {
        return CHARACTOR_SEARIAL;
    }
}
