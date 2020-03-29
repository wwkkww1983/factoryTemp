package com.proton.carepatchtemp.activity.device;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.activity.managecenter.DeviceUpdateMsgActivity;
import com.proton.carepatchtemp.databinding.ActivityDeviceManageDetailBinding;
import com.proton.carepatchtemp.net.bean.DeviceBean;
import com.proton.carepatchtemp.net.bean.UpdateFirmwareBean;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.StringUtils;

import org.litepal.LitePal;

/**
 * 设备详情 p02 p04 p05的设备详情
 * <传入extra>
 * profileId int 设备id
 * updateInfo UpdateFirmwareBean 设备最新信息
 * </>
 */
public class PatchDetailActivity extends BaseActivity<ActivityDeviceManageDetailBinding> {
    /**
     * 请求得到的设备详情实体对象
     */
    private DeviceBean deviceBean;
    private UpdateFirmwareBean updateFirmwareBean;

    @Override
    protected void init() {
        super.init();
        deviceBean = (DeviceBean) getIntent().getSerializableExtra("device");
        updateFirmwareBean = LitePal.where("deviceType = ?", String.valueOf(deviceBean.getDeviceType())).findFirst(UpdateFirmwareBean.class);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_device_manage_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        if (deviceBean == null) {
            return;
        }
        //设备名称
        binding.idTvDeviceDetailName.setText(deviceBean.getDeviceTypeName());
        //最近使用
        binding.idTvDeviceDetailLatestUse.setText(deviceBean.getLastUseTime());
        //序列号
        binding.idTvDeviceDetailSerialNum.setText(deviceBean.getSn());
        binding.idFirewareType.setText(deviceBean.getFirmwareType());
        //版本号
        String version = deviceBean.getVersion();
        if (!TextUtils.isEmpty(version)) {
            if (version.startsWith("V") || version.startsWith("v")) {
                binding.idTvDeviceDetailFirmwareVersion.setText(version);
            } else {
                binding.idTvDeviceDetailFirmwareVersion.setText("v" + version);
            }
        } else {
            binding.idTvDeviceDetailFirmwareVersion.setText("--");
        }
        //蓝牙ip地址
        binding.idTvDeviceDetailBlueIp.setText(deviceBean.getBtaddress());
        if (updateFirmwareBean != null) {
            if (StringUtils.compareVersion(deviceBean.getVersion(), updateFirmwareBean.getVersion()) < 0 && deviceBean.getDeviceType() != 2) {
                //该固件需要升级
                binding.idTvFirmwareNeedUpdate.setVisibility(View.VISIBLE);
            } else {
                binding.idTvFirmwareNeedUpdate.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.idFlDeviceUpdate.setOnClickListener(v -> {
            //固件升级
            if (deviceBean == null) {
                BlackToast.show("设备数据为空");
                return;
            }
            Intent goUpdateDetial = new Intent(mContext, DeviceUpdateMsgActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("device", deviceBean);
            goUpdateDetial.putExtras(bundle);
            startActivity(goUpdateDetial);
        });
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_device_detail);
    }
}
