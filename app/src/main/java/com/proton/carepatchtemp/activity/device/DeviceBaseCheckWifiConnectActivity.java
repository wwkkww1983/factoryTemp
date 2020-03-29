package com.proton.carepatchtemp.activity.device;

import android.content.Intent;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.databinding.ActivityDeviceBaseCheckWifiConnectBinding;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.utils.UIUtils;

/**
 * 检查手机连网状况
 * <extra>
 * deviceType String 设备类型
 * profileBean ProfileBean 为哪位宝宝测量
 * </extra>
 */
public class DeviceBaseCheckWifiConnectActivity extends BaseActivity<ActivityDeviceBaseCheckWifiConnectBinding> {

    private ProfileBean profileBean;

    @Override
    protected void init() {
        super.init();
        Intent mIntent = getIntent();
        profileBean = (ProfileBean) mIntent.getSerializableExtra("profileBean");
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.NET_CHANGE) {
            startActivity(new Intent(this, DeviceBaseInputWifiPwdActivity.class)
                    .putExtra("wifiName", event.getMsg())
                    .putExtra("profileBean", profileBean));
            finish();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        String[] clickAryStr = new String[]{getString(R.string.string_2_4g)};
        UIUtils.spanStr(binding.idTips, getString(R.string.string_not_support_5g), clickAryStr, R.color.color_red, false, null);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_device_base_check_wifi_connect;
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_base_connect_wifi);
    }
}
