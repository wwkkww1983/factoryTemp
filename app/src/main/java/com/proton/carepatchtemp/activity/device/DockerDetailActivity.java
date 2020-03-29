package com.proton.carepatchtemp.activity.device;

import android.text.TextUtils;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.databinding.ActivityDockerDetailBinding;
import com.proton.carepatchtemp.net.bean.DeviceBean;
import com.proton.carepatchtemp.utils.DateUtils;
import com.proton.carepatchtemp.utils.IntentUtils;

/**
 * p03设备详情页
 * <传入extra>
 * profileId int 设备id
 * </extra>
 */
public class DockerDetailActivity extends BaseActivity<ActivityDockerDetailBinding> {

    @Override
    protected int inflateContentView() {
        return R.layout.activity_docker_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIncludeTop.title.setText(getString(R.string.string_device_detail));
        binding.idIncludeTop.idTvRightOperate.setText(R.string.string_wifi_reconnect);
        binding.idIncludeTop.idTvRightOperate.setVisibility(View.VISIBLE);

        DeviceBean device = (DeviceBean) getIntent().getSerializableExtra("device");
        if (device == null) {
            return;
        }
        //设备名称
        binding.idTvDeviceDetailName.setText(device.getDeviceTypeName());
        //充电器固件版本
        String version = device.getVersion();
        if (!TextUtils.isEmpty(version) && (version.startsWith("V") || version.startsWith("v"))) {
            binding.idTvBaseHardVersion.setText(version);
        }

        if (device.getLastUpdated() != 0) {
            binding.idTvBaseLatestUse.setText(DateUtils.dateStrToYMDHM(device.getLastUpdated()));
        }

        if (!TextUtils.isEmpty(device.getBtaddress())) {
            binding.idDeviceMac.setText(device.getBtaddress());
        }
        if (!TextUtils.isEmpty(device.getWifiName())) {
            binding.idTvWifiAddress.setText(device.getWifiName());
        }

        binding.idIncludeTop.idTvRightOperate.setTextSize(10);
        binding.idIncludeTop.idTvRightOperate.setOnClickListener(v -> {
            //重新联网
            IntentUtils.goToDockerSetNetwork(mContext, true, device.getBtaddress());
        });
    }
}
