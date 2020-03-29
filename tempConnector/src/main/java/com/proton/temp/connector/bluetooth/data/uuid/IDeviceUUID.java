package com.proton.temp.connector.bluetooth.data.uuid;

/**
 * Created by wangmengsi on 2017/8/7.
 */

public interface IDeviceUUID {
    /**
     * 体温数据服务uuid
     */
    String getServiceTemp();

    /**
     * 体温数据Charactor uuid
     */
    String getCharactorTemp();
    /**
     * 缓存温度数据Charactor uuid
     */
    String getCharactorCacheTemp();
    /**
     * 写一个值获取一个缓存温度数据Charactor uuid
     */
    String getCharactorCacheTempSend();

    /**
     * 设备信息服务uuid
     */
    String getDeviceInfoServiceUUID();

    /**
     * 硬件版本Charactor uuid
     */
    String getCharactorVersionUUID();

    /**
     * 电量uuid
     */
    String getCharactorBatteryUUID();

    /**
     * 序列号uuid
     */
    String getCharactorSearialUUID();
}
