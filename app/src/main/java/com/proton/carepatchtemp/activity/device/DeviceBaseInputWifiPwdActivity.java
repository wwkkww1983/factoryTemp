package com.proton.carepatchtemp.activity.device;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.ActivityDeviceBaseInputWifiPwdBinding;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.utils.ble.BleManager;
import com.proton.carepatchtemp.view.WarmDialog;
import com.wms.ble.utils.BluetoothUtils;
import com.wms.logger.Logger;
import com.wms.utils.NetUtils;

/**
 * 客户端wifi连接可用情况下，充电器开始配网
 * <extra>
 * deviceType String 设备类型
 * wifiName String wifi名称
 * profileBean ProfileBean 为哪位宝宝测量
 * </extra>
 */
public class DeviceBaseInputWifiPwdActivity extends BaseActivity<ActivityDeviceBaseInputWifiPwdBinding> {

    private String deviceType;//设备类型
    /**
     * wifi名称
     */
    private String wifiName;
    /**
     * 选择测量的宝宝档案
     */
    private ProfileBean profileBean;
    private WarmDialog mNotConnectWifiDialog;


    @Override
    protected void init() {
        super.init();
        Intent mIntent = getIntent();
        deviceType = mIntent.getStringExtra("deviceType");
        wifiName = mIntent.getStringExtra("wifiName");
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
            if (!NetUtils.isWifiConnected(mContext)) {
                if (mNotConnectWifiDialog == null) {
                    mNotConnectWifiDialog = new WarmDialog(ActivityManager.currentActivity())
                            .setContent(getString(R.string.string_wifi_not_connect))
                            .setTopText(R.string.string_warm_tips)
                            .hideCancelBtn()
                            .setConfirmText(R.string.string_confirm)
                            .setConfirmListener(v -> {
                                startActivity(new Intent(this, DeviceBaseCheckWifiConnectActivity.class).putExtra("deviceType", deviceType)
                                        .putExtra("profileBean", profileBean));
                                finish();
                            });
                }
                mNotConnectWifiDialog.show();
                return;
            }

            if (mNotConnectWifiDialog != null) {
                mNotConnectWifiDialog.dismiss();
            }

            String wifiName = Utils.getWifiSsid();
            wifiName = wifiName.replace('"', ' ').replace('"', ' ');
            binding.idTvWifiName.setText(wifiName);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        wifiName = Utils.getWifiSsid();

        if (!TextUtils.isEmpty(wifiName)) {
            wifiName = wifiName.replace('"', ' ').replace('"', ' ');
            binding.idTvWifiName.setText(wifiName);
        }
        binding.idIncludeTop.title.setText(R.string.string_base_connect_wifi);
    }

    @Override
    protected void setListener() {
        super.setListener();
        //连接
        binding.idBtnConnect.setOnClickListener(v -> {

            String ssid = binding.idTvWifiName.getText().toString().trim();
            String password = binding.idEdWifiPwd.getText().toString().trim();
            if (TextUtils.isEmpty(ssid)) {
                BlackToast.show(R.string.string_please_connect_wifi);
                return;
            }

            if (Utils.is5GWIFI(mContext)) {
                BlackToast.show(R.string.string_can_not_use_5g_wifi);
                return;
            }

            if (!BluetoothUtils.isBluetoothOpened()) {
                new WarmDialog(this)
                        .setTopText(R.string.string_bluetooth_unopen)
                        .setConfirmText(R.string.string_confirm2)
                        .setContent(R.string.string_open_bluetooth_tip)
                        .setConfirmListener(v12 -> BluetoothUtils.openBluetooth())
                        .show();
            } else {
                new WarmDialog(this)
                        .setTopText(R.string.string_base_connect_wifi)
                        .setConfirmText(R.string.string_confirm2)
                        .setCancelText(R.string.string_cancel2)
                        .setContent(R.string.string_reset_charge)
                        .setConfirmListener(v13 -> {

                            if (TextUtils.isEmpty(password)) {
                                new WarmDialog(this)
                                        .setTopText(R.string.string_warm_tips)
                                        .setConfirmText(R.string.string_confirm)
                                        .setContent(R.string.string_not_input_wifi_pwd_tips)
                                        .setConfirmListener(v1 -> IntentUtils.goToDockerSetingNetwork(mContext, ssid, password, profileBean))
                                        .show();
                                return;
                            }

                            IntentUtils.goToDockerSetingNetwork(mContext, ssid, password, profileBean);

                            finish();
                        })
                        .show();
            }


        });

        binding.getRoot().setOnClickListener(v -> Utils.hideKeyboard(mContext, binding.getRoot()));
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_device_base_input_wifi_pwd;
    }

    @Override
    public String getTopCenterText() {
        return UIUtils.getString(R.string.string_base_connect_wifi);
    }

}
