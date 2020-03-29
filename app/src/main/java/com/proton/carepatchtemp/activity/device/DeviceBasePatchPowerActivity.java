package com.proton.carepatchtemp.activity.device;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.activity.common.GlobalWebActivity;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.ActivityDeviceBasePatchPowerBinding;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.HttpUrls;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.view.WarmDialog;
import com.wms.utils.NetUtils;

/**
 * 充电器配网连接电源
 * <extra>
 * deviceType String 设备类型 "P"开头
 * profileBean ProfileBean 为哪位宝宝测量
 * </extra>
 */
public class DeviceBasePatchPowerActivity extends BaseActivity<ActivityDeviceBasePatchPowerBinding> {

    private String deviceType;//设备类型
    /**
     * 选择为哪位宝宝测量
     */
    private ProfileBean profileBean;
    private boolean isReset;

    @Override
    protected void init() {
        super.init();
        Intent mIntent = getIntent();
        deviceType = mIntent.getStringExtra("deviceType");
        profileBean = (ProfileBean) mIntent.getSerializableExtra("profileBean");
        isReset = getIntent().getBooleanExtra("isReSet", false);
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIncludeTop.title.setText(R.string.string_base_connect_wifi);
        //设置帮助提示下划线
        binding.idTvLightNotShining.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        Uri gifUri = Uri.parse("res://" + getPackageName() + "/" + R.drawable.gif_patch_power);
        if (isReset) {
            gifUri = Uri.parse("res://" + getPackageName() + "/" + R.drawable.gif_retry_patch_network);
        }
        DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                //设置uri,加载本地的gif资源
                .setUri(gifUri)//设置uri
                .build();
        //设置Controller
        binding.idSdvPatchPower.setController(mDraweeController);
        if (isReset) {
            binding.idResetTip.setText(R.string.string_press_reset_lightly);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void setListener() {
        super.setListener();
        //指示灯未闪烁帮助
        binding.idTvLightNotShining.setOnClickListener(v -> startActivity(new Intent(mContext, GlobalWebActivity.class).putExtra("url", HttpUrls.URL_POWER_LIGHT_NOT_SHINING)));
        //检查连网状态
        binding.idBtnConnectWifi.setOnClickListener(v -> {
            //判断广播是否已经关闭，否则不能进入配网界面
            //wifi连接去连网，否者进入指示连网页
            String dockerMac = getIntent().getStringExtra("dockerMac");
            if (!TextUtils.isEmpty(dockerMac)
                    && Utils.checkMacIsMeasuring(dockerMac)) {
                new WarmDialog(this)
                        .setTopText(R.string.string_close_measure_card)
                        .setContent(R.string.string_continue_set_net_will_close_card)
                        .setConfirmListener(v1 -> {
                            Utils.closeAllCards();
                            goToSetNetwork();
                        }).show();
                return;
            }
            goToSetNetwork();
        });
    }

    private void goToSetNetwork() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android 5.0
            if (!checkGPS()) {
                return;
            }
        }

        if (NetUtils.isWifiConnected(this)) {
            startActivity(new Intent(this, DeviceBaseInputWifiPwdActivity.class).putExtra("deviceType", deviceType)
                    .putExtra("wifiName", Utils.getWifiSsid())
                    .putExtra("profileBean", profileBean));
        } else {
            startActivity(new Intent(this, DeviceBaseCheckWifiConnectActivity.class).putExtra("deviceType", deviceType)
                    .putExtra("profileBean", profileBean));
        }
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_device_base_patch_power;
    }

    private boolean checkGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (locationManager != null
                && !locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            WarmDialog warmDialog = new WarmDialog(this)
                    .setTopText(R.string.string_open_gps)
                    .setContent("因系统原因,请允许定位权限,并开启定位功能")
                    .setConfirmText(R.string.string_confirm)
                    .setConfirmListener(v -> {
                        // 转到手机设置界面，用户设置GPS
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                    });
            warmDialog.show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected boolean showBackBtn() {
        if (App.get().isNewUser()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (!App.get().isNewUser()) {
            MessageEvent messageEvent = new MessageEvent(MessageEvent.EventType.ADD_NEW_DEVICE_BIND);
            if (profileBean != null) {
                messageEvent.setObject(profileBean);
            }
            EventBusManager.getInstance().post(messageEvent);

            super.onBackPressed();
        }
    }
}
