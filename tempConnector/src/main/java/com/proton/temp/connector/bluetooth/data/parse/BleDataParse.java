package com.proton.temp.connector.bluetooth.data.parse;

import com.proton.temp.connector.bean.TempDataBean;
import com.proton.temp.connector.bluetooth.utils.BleUtils;

import java.util.List;

/**
 * Created by wangmengsi on 2017/8/7.
 * 心电卡数据解析
 */

public class BleDataParse implements IBleDataParse {
    /**
     * 解析电量
     */
    @Override
    public int parseBattery(byte[] value) {
        return value[0] & 0x7F;
    }

    @Override
    public boolean parseCharge(byte[] value) {
        return (value[0] & 0x80) != 0;
    }

    @Override
    public String parseHardVersion(byte[] value) {
        return new String(value);
    }

    @Override
    public String parseSerial(byte[] value) {
        return new String(value);
    }

    @Override
    public TempDataBean parseTemp(byte[] value) {
        return BleUtils.parseTemp(value);
    }

    @Override
    public List<TempDataBean> parseTempV1_5(byte[] value) {
        return BleUtils.parseTempV1_5(value);
    }
}
