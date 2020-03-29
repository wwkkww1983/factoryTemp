package com.proton.carepatchtemp.activity.device;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.databinding.ActivityDeviceBaseConnectFailBinding;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.view.WiFiDisconnectDialog;

public class DeviceBaseConnectFailActivity extends BaseActivity<ActivityDeviceBaseConnectFailBinding> {
    WiFiDisconnectDialog wiFiDisconnectDialog;
    @Override
    protected int inflateContentView() {
        return R.layout.activity_device_base_connect_fail;
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.idBtnRetry.setOnClickListener(v -> {
            //重试
            finish();
        });

        binding.ivExplain.setOnClickListener(v->{
            if (Utils.needRecreateDialog(wiFiDisconnectDialog)) {
                wiFiDisconnectDialog=new WiFiDisconnectDialog(this);
            }

            if (!wiFiDisconnectDialog.isShowing()) {
                wiFiDisconnectDialog.show();
            }
        });


    }

    @Override
    public String getTopCenterText() {
        return UIUtils.getString(R.string.string_base_connect_wifi);
    }
}
