package com.proton.carepatchtemp.activity.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.ActivityDeviceBaseConnectingBinding;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.DeviceCenter;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.DockerSetNetworkManager;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.utils.ble.BleManager;
import com.proton.temp.connector.bean.DeviceType;
import com.wms.logger.Logger;

import blufi.espressif.apputil.utils.BluFiUtil;

/**
 * 配网页面    已知ssid  pawwword
 * <extra>
 * profileBean ProfileBean 为哪位宝宝测量
 * </extra>
 */
public class DeviceBaseConnectingActivity extends BaseActivity<ActivityDeviceBaseConnectingBinding> {

    private boolean isSetNetSuccess;
    public static final int REQUEST_CODE = 0x1000;
    private boolean isBulefiSucessFlag = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable runnable = null;
    private boolean isActived = true;
    /**
     * 蓝牙配网和wifi配网的标志   true：表示蓝牙配网
     */
    private boolean isBluefi = true;

    BluFiUtil bluFiUtil;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_device_base_connecting;
    }

    @Override
    protected void initView() {
        super.initView();
        DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                //设置uri,加载本地的gif资源
                .setUri(Uri.parse("res://" + getPackageName() + "/" + R.drawable.gif_patch_power))//设置uri
                .build();
        //设置Controller
//        binding.idSdvPatchwifi.setController(mDraweeController);
    }

    @Override
    protected void initData() {
        super.initData();
        registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        String ssid = getIntent().getStringExtra("ssid");
        String password = getIntent().getStringExtra("password");

//        resetCharge();//发送广播包重置底座（此功能取消，因为底座可以重复配网）
    /*    new Handler(getMainLooper()).postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BleManager.getInstance().stopAdvertising();
            }
            configWifiByBluetooth(ssid, password);
        }, 3000);*/
        configWifiByBluetooth(ssid, password);
        App.get().setNewUser(false);
    }

    /**
     * bluFil蓝牙连接配网
     */
    private void configWifiByBluetooth(String ssid, String password) {

        /**
         * 如果30秒还没有配好改成wifi配网
         */

        runnable = () -> {
            if (!isBulefiSucessFlag && isBluefi) {
                connectFail();
            }
        };
        mHandler.postDelayed(runnable, 40000);

        bluFiUtil = new BluFiUtil.Builder()
                .setContext(mContext)
                .setSsid(ssid)
                .setSortScanResultByRssi(true)
                .setBlufiliFilter("PROTON")
                .setPwd(password)
                .setScanTime(10000L)
                .build();
        bluFiUtil.startConfigWifi(new BluFiUtil.OnBluFiSetNetworkListener() {
            @Override
            public void onStart() {
                binding.idSdvPatchwifi.setImageResource(R.drawable.bluefil_complete);
                binding.idTvConnectingTip.setVisibility(View.VISIBLE);
            }

            @Override
            public void onConnecting(String dockerMac, int rssi) {
                binding.idDockerMacRssi.setText(String.format("正在给底座%s（%s）配网", dockerMac, rssi));
            }

            @Override
            public void onSuccess(String macaddress, String bssid) {
                isBulefiSucessFlag = true;
                binding.idDockerMacRssi.setText("");
                if (!App.get().isLogined()) {
                    connectSuccess(macaddress);
                } else {
                    addDocker(macaddress, "", "", ssid, bssid);
                }
                if (runnable != null) {
                    mHandler.removeCallbacks(runnable);
                }
            }

            @Override
            public void onFail(String msg) {
                BlackToast.show(msg);
                connectFail();
                isBulefiSucessFlag = false;
                binding.idDockerMacRssi.setText("");
            }

            @Override
            public void onNotFound() {
                binding.idDockerMacRssi.setText("");
                configWifiByWifi(ssid, password);
            }
        });

    }

    /**
     * 通过局域网配网
     *
     * @param ssid
     * @param password
     */
    private void configWifiByWifi(String ssid, String password) {
        isBluefi = false;
        DockerSetNetworkManager.getInstance().start(ssid, password, new DockerSetNetworkManager.OnSetNetworkListener() {
            @Override
            public void onStart() {
//                showDialog(getString(R.string.string_network_setting));
                binding.idSdvPatchwifi.setImageResource(R.drawable.wifi_complete);
            }

            @Override
            public void onSuccess(String macaddress, String bssid) {
                if (!App.get().isLogined()) {
                    connectSuccess(macaddress);
                } else {
                    addDocker(macaddress, "", "", ssid, bssid);
                }
            }

            @Override
            public void onFail() {
                connectFail();
                dismissDialog();
            }

            @Override
            public void onNotFound() {
                BlackToast.show(R.string.string_not_found_temp_docker);
                dismissDialog();
                finish();
            }
        });
    }

    @Override
    protected void onDialogDismiss() {
        super.onDialogDismiss();
        DockerSetNetworkManager.getInstance().stop();
    }

    /**
     * 添加充电器
     */
    private void addDocker(String macaddress, String serial, String hardVersion, String ssid, String bssid) {
        DeviceCenter.addDocker(macaddress, serial, hardVersion, ssid, bssid, new NetCallBack<Boolean>() {
            @Override
            public void onSucceed(Boolean data) {
                Logger.w("绑定充电器成功");
                changeDeviceType(macaddress);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(R.string.string_bind_docker_fail);
                dismissDialog();
                connectFail();
            }
        });
    }

    /**
     * 更改设备类型
     */
    private void changeDeviceType(String macaddress) {
        DeviceCenter.changeDeviceType("P03", new NetCallBack<Boolean>() {

            @Override
            public void onSucceed(Boolean data) {
                Logger.w("切换设备类型成功");
                connectSuccess(macaddress);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(R.string.string_bind_docker_fail);
                dismissDialog();
                connectFail();
            }
        });
    }

    /**
     * 连接成功
     */
    private void connectSuccess(String macaddress) {
        dismissDialog();
        isSetNetSuccess = true;
        BlackToast.show(R.string.string_network_setting_success);
        binding.idTvConnectingTip.setText(R.string.string_power_using_tips);
        binding.idTvConnectSuccess.setText(UIUtils.getString(R.string.string_wifi_connect_success));
        binding.idTvConnectSuccessFlag.setText(R.string.string_power_light_on);
        //连接静态图
        binding.idSdvPatchwifi.setImageResource(R.drawable.icon_docker_set_net_success);
        binding.idBtnFinish.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_radius5dp_blue30_fill));
        binding.idBtnFinish.setEnabled(true);
        binding.idBtnFinish.setOnClickListener(v -> doSetNetSuccess());
    }

    private void doSetNetSuccess() {
        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_DEVICE, DeviceType.P03));
        //判断一下是否是从充电器详情页过来
        DockerDetailActivity detailActivity = ActivityManager.findActivity(DockerDetailActivity.class);
        if (detailActivity != null) {
            ActivityManager.keepActivity(HomeActivity.class, DockerDetailActivity.class);
        } else {
            Utils.showHomeMeasure();
            IntentUtils.goToMain(mContext);
        }
    }

    @Override
    public void onBackPressed() {
        if (isSetNetSuccess) {
            doSetNetSuccess();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 连接失败
     */
    private void connectFail() {
        if (isBulefiSucessFlag) {
            return;
        }

        if (!isActived) {
            return;
        }

        startActivity(new Intent(this, DeviceBaseConnectFailActivity.class));
        finish();
    }

    @Override
    public String getTopCenterText() {
        return UIUtils.getString(R.string.string_base_connect_wifi);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BleManager.getInstance().stopAdvertising();
        }
        if (bluFiUtil != null) {
            bluFiUtil.stopConfig();
        }
        if (runnable != null) {
            mHandler.removeCallbacks(runnable);
        }

        unregisterReceiver(mReceiver);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BleManager.getInstance().startAdvertising(this, -1);
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Logger.d("网络状态已经改变");
                ConnectivityManager connectivityManager = (ConnectivityManager)

                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    String name = info.getTypeName();
                    Logger.d("当前网络名称：" + name);
                } else {
                    BlackToast.show("当前网络不可用");
                    finish();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        isActived = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActived = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActived = false;
    }
}
