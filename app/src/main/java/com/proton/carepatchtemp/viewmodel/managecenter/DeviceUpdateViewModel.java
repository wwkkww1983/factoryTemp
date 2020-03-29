package com.proton.carepatchtemp.viewmodel.managecenter;

import android.databinding.ObservableField;

import com.proton.carepatchtemp.viewmodel.BaseViewModel;

/**
 * Created by luochune on 2018/3/14.
 */

public class DeviceUpdateViewModel extends BaseViewModel {
    /**
     * 设备名称
     */
    public ObservableField<String> deviceName = new ObservableField<>("");
    /**
     * 设备ip地址
     */
    public ObservableField<String> deviceIpAddress = new ObservableField<>("");

    /**
     * 设备版本号
     */
    public ObservableField<String> deviceVersion = new ObservableField<>("");

    /**
     * 设备更新版本提示: 新版本固件: 1.1
     */
    public ObservableField<String> deviceUpdateVersionTip = new ObservableField<>("");
    /**
     * 设备更新内容
     */
    public ObservableField<String> deviceUpdateContent = new ObservableField<>("");
}
