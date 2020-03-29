package com.proton.temp.connector.bluetooth.data.parse;

import com.proton.temp.connector.bean.TempDataBean;

import java.util.List;

/**
 * Created by wangmengsi on 2017/8/7.
 */

public interface IBleDataParse {
    /**
     * 解析电量
     */
    int parseBattery(byte[] value);

    /**
     * 解析是否充电
     */
    boolean parseCharge(byte[] value);

    /**
     * 解析版本号
     */
    String parseHardVersion(byte[] value);

    /**
     * 解析序列号
     */
    String parseSerial(byte[] value);

    /**
     * 解析体温1.0版本
     *
     * @since 1.0
     */
    TempDataBean parseTemp(byte[] value);

    /**
     * 解析体温1.5版本
     *
     * @since 1.5
     */
    List<TempDataBean> parseTempV1_5(byte[] value);
}
