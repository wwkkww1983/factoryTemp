package com.proton.carepatchtemp.activity.managecenter;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.activity.device.FirewareUpdatingActivity;
import com.proton.carepatchtemp.databinding.ActivityDeviceUpdateMsgBinding;
import com.proton.carepatchtemp.net.bean.DeviceBean;
import com.proton.carepatchtemp.net.bean.UpdateFirmwareBean;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.StringUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.temp.connector.bean.DeviceType;
import com.wms.ble.utils.BluetoothUtils;

import org.litepal.LitePal;

/**
 * 设备升级
 */
public class DeviceUpdateMsgActivity extends BaseActivity<ActivityDeviceUpdateMsgBinding> {
    /**
     * 固件详情
     */
    private DeviceBean mDeviceBean;
    private UpdateFirmwareBean mUpdateInfo;

    @Override
    protected void init() {
        super.init();
        mDeviceBean = (DeviceBean) getIntent().getSerializableExtra("device");
        mUpdateInfo = LitePal.where("deviceType = ?", String.valueOf(mDeviceBean.getDeviceType())).findFirst(UpdateFirmwareBean.class);
    }

    @Override
    protected void initView() {
        super.initView();
        if (mDeviceBean == null) {
            return;
        }
        //设备名称
        binding.idTvDeviceName.setText(mDeviceBean.getName());
        //设备固件版本
        binding.idTvDeviceOldVersion.setText(mDeviceBean.getVersion());
        if (mUpdateInfo != null) {
            if (StringUtils.compareVersion(mDeviceBean.getVersion(), mUpdateInfo.getVersion()) < 0 && mDeviceBean.getDeviceType() != 2) {
                binding.idUpdateLayout.setVisibility(View.VISIBLE);
                binding.idNoUpdate.setVisibility(View.GONE);
                binding.idUpdateVersion.setText(String.format(getString(R.string.string_new_version_hardware), mUpdateInfo.getVersion()));
                binding.idUpdateMsg.setText(mUpdateInfo.getContent());
                binding.idTvUpdateDevice.setEnabled(true);
                binding.idTvUpdateDevice.setBackground(getResources().getDrawable(R.drawable.shape_radius20_blue30));
                binding.idTvUpdateDevice.setTextColor(ContextCompat.getColor(this, R.color.color_blue_00));
            }
        }
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_device_update_msg;
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.idTvUpdateDevice.setOnClickListener(v -> {
            if (Utils.checkPatchIsMeasuring(mDeviceBean.getBtaddress())) {
                BlackToast.show(R.string.string_please_end_measure_and_update_version);
                return;
            }
            if (!BluetoothUtils.isBluetoothOpened()) {
                BlackToast.show(R.string.string_please_open_bluetooth);
                BluetoothUtils.openBluetooth();
                return;
            }
            //升级固件
            startActivity(new Intent(mContext, FirewareUpdatingActivity.class)
                    .putExtra("macaddress", mDeviceBean.getBtaddress())
                    .putExtra("deviceType", DeviceType.valueOf(mDeviceBean.getDeviceType())));
        });
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_device_update);
    }
}
