package com.proton.carepatchtemp.activity.device;

import android.content.Intent;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.databinding.ActivityFirewareUpdateFailBinding;
import com.proton.temp.connector.bean.DeviceType;

/**
 * Created by 王梦思 on 2018/12/6.
 * <p/>
 */
public class FirewareUpdateFailActivity extends BaseActivity<ActivityFirewareUpdateFailBinding> {
    private String macaddress;
    private DeviceType deviceType;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_fireware_update_fail;
    }

    @Override
    protected void init() {
        super.init();
        macaddress = getIntent().getStringExtra("macaddress");
        deviceType = (DeviceType) getIntent().getSerializableExtra("deviceType");
    }

    @Override
    protected void initView() {
        super.initView();
        if (deviceType != DeviceType.P02) {
            binding.idIvUpdatePic.setImageResource(R.drawable.img_carepatch_simple);
        }

        binding.idRetry.setOnClickListener(v -> {
            startActivity(new Intent(mContext, FirewareUpdatingActivity.class)
                    .putExtra("macaddress", macaddress)
                    .putExtra("deviceType", deviceType));
            finish();
        });
    }

    @Override
    public String getTopCenterText() {
        return getString(R.string.string_update_firware);
    }
}
