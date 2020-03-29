package com.proton.carepatchtemp.viewmodel.managecenter;

import android.databinding.ObservableField;

import com.proton.carepatchtemp.viewmodel.BaseViewModel;

/**
 * Created by luochune on 2018/3/14.
 */

public class DeviceManageDetailViewModel extends BaseViewModel {

    /**
     * 设备名称
     */
    public ObservableField<String> deviceName = new ObservableField<>("--");
    /**
     * 设备IP
     */
    public ObservableField<String> deviceIpAddress = new ObservableField<>("-");
    /**
     * 最近使用
     */
    public ObservableField<String> latestUseDate = new ObservableField<>("-");
    /**
     * 序列号
     */
    public ObservableField<String> serializableNum = new ObservableField<>("-");
    /**
     * 版本号
     */
    public ObservableField<String> versionNameCode = new ObservableField<>("-");
    /**
     * 蓝牙
     */
    public ObservableField<String> bluetoothId = new ObservableField<>("-");
    /**
     * 序列号
     */
    //public ObservableField<boolean> isNeedUpdate= new ObservableField<>(false);

}
