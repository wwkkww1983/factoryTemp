package com.proton.carepatchtemp.activity.device;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.databinding.ActivityFirmwareUpdatingBinding;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.UpdateFirmwareBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.DeviceCenter;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.Density;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.FileUtils;
import com.proton.carepatchtemp.view.WarmDialog;
import com.proton.temp.connector.bean.DeviceType;
import com.proton.temp.connector.utils.FirewareUpdateManager;
import com.wms.ble.utils.BluetoothUtils;
import com.wms.logger.Logger;

import org.litepal.LitePal;

import java.text.DecimalFormat;
import java.util.List;

public class FirewareUpdatingActivity extends BaseActivity<ActivityFirmwareUpdatingBinding> {
    private DecimalFormat df = new java.text.DecimalFormat("#0.00");
    private String macaddress;
    private DeviceType deviceType;
    private FirewareUpdateManager updateManager;
    private boolean isUpdateSuccessed;

    /**
     * 进度是否首次到达99%
     */
    private boolean isFirst = true;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_firmware_updating;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void init() {
        super.init();
        macaddress = getIntent().getStringExtra("macaddress");
        deviceType = (DeviceType) getIntent().getSerializableExtra("deviceType");
    }

    @Override
    protected void initData() {
        super.initData();

        binding.txtUpdateReady.setText(getString(R.string.string_update_ready) + "...");
        binding.txtUpdateReady.setTextAppearance(mContext, R.style.fireware_update_style_updating);

        DeviceCenter.getUpdatePackage(new NetCallBack<List<UpdateFirmwareBean>>() {
            @Override
            public void noNet() {
                startUpdate();
            }

            @Override
            public void onSucceed(List<UpdateFirmwareBean> data) {
                startUpdate();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                startUpdate();
            }
        });
    }

    private void startUpdate() {
        updateManager = new FirewareUpdateManager(getApplication(), macaddress, type -> {
            if (type == DeviceType.P02) {
                type = DeviceType.P03;
            }

            UpdateFirmwareBean firmwareBean = LitePal.where("deviceType = ?", String.valueOf(type.getValue())).findFirst(UpdateFirmwareBean.class);
            String firewarePath = "";
            if (firmwareBean != null) {
                //检查升级固件包是否存在，不存在就下载
                firewarePath = FileUtils.getFireware(DeviceType.valueOf(firmwareBean.getDeviceType()), firmwareBean.getVersion());
                Logger.w("固件路径:" + firewarePath);
            }

            if (!TextUtils.isEmpty(firewarePath)) {
                binding.ivUpdateReady.setVisibility(View.VISIBLE);
            }

            return firewarePath;
        });

        updateManager.setOnFirewareUpdateListener(new FirewareUpdateManager.OnFirewareUpdateListener() {
            @Override
            public void onSuccess(DeviceType type, String macaddress) {
                runOnUiThread(() -> {
                    BlackToast.show(R.string.string_update_fireware_success);
                    UpdateFirmwareBean firmwareBean = LitePal.where("deviceType = ?", String.valueOf(type.getValue())).findFirst(UpdateFirmwareBean.class);
                    if (!"0A:D0:AD:0A:D0:AD".equals(macaddress)) {
                        DeviceCenter.addDevice(type.toString(), "", macaddress, firmwareBean.getVersion(), new NetCallBack<String>() {
                            @Override
                            public void onSucceed(String data) {
                                Logger.w("添加设备成功:" + macaddress + "，version:" + firmwareBean.getVersion() + ",type=" + type.toString());
                                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_DEVICE, DeviceType.P02));
                            }
                        });
                    }
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.FIREWARE_UPDATE_SUCCESS));
                    new Handler(getMainLooper()).postDelayed(() -> {
                        binding.idProgressbarDownload.setProgress(100);
                        binding.tvUpdateProgress.setText("100%");
                        ActivityManager.finishOthersActivity(HomeActivity.class);
                    }, 1500);

                    isUpdateSuccessed = true;
                });
            }

            @Override
            public void onFail(String msg, FirewareUpdateManager.UpdateFailType type) {
                if (isUpdateSuccessed) {
                    return;
                }

                if (type.equals(FirewareUpdateManager.UpdateFailType.BATTERY_FAIL)) {
                    new WarmDialog(FirewareUpdatingActivity.this)
                            .setTopText(R.string.string_low_charge)
                            .setContent(R.string.string_un_update_battery)
                            .setConfirmText(R.string.string_confirm)
                            .setConfirmListener(v -> finish())
                            .hideCancelBtn()
                            .show();

                    return;
                }

                BlackToast.show(msg);
                startActivity(new Intent(mContext, FirewareUpdateFailActivity.class)
                        .putExtra("macaddress", macaddress)
                        .putExtra("deviceType", deviceType));
                finish();
            }

            @SuppressLint("CheckResult")
            @Override
            public void onProgress(float progress) {
                if (progress < 0.99) {
                    binding.idProgressbarDownload.setProgress((int) (progress * 100));
                    binding.tvUpdateProgress.setText(df.format(progress * 100) + "%");
                } else if (progress >= 0.99 && progress < 1) {
                    if (isFirst) {
                        isFirst = false;
                        binding.idProgressbarDownload.setProgress((int) (progress * 100));
                        binding.tvUpdateProgress.setText(df.format(progress * 100) + "%");
                        binding.ivUpdateing.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        updateManager.setOnFirewareStatusListener(new FirewareUpdateManager.OnFirewareStatusListener() {
            @Override
            public void onUpdateReadyCompelete() {
                runOnUiThread(() -> {
                    binding.ivUpdateReady.setImageResource(R.drawable.complete_gray);
                    binding.ivUpdateReady.setVisibility(View.VISIBLE);
                    binding.txtUpdateReady.setText(getString(R.string.string_update_ready));
                    binding.txtUpdateReady.setTextAppearance(mContext, R.style.fireware_update_style_update);
                });

            }

            @Override
            public void onUpdatind() {
                runOnUiThread(() -> {
                    binding.txtUpdateing.setText(getString(R.string.string_updating) + "...");
                    binding.txtUpdateing.setTextAppearance(mContext, R.style.fireware_update_style_updating);
                });
            }

            @Override
            public void onUpdatingComplete() {
                binding.ivUpdateing.setImageResource(R.drawable.complete_gray);
                binding.ivUpdateing.setVisibility(View.VISIBLE);
                binding.txtUpdateing.setText(getString(R.string.string_updating));
                binding.txtUpdateing.setTextAppearance(mContext, R.style.fireware_update_style_update);
            }

            @Override
            public void onUpdateVerify() {
                runOnUiThread(() -> {
                    binding.txtUpdateVerify.setText(getString(R.string.string_update_verify) + "...");
                    binding.txtUpdateVerify.setTextAppearance(mContext, R.style.fireware_update_style_updating);
                });
            }

            @Override
            public void onUpdateVerifyComplete() {
                new Handler(getMainLooper()).postDelayed(() -> {
                    binding.ivUpdateVerify.setVisibility(View.VISIBLE);
                    binding.ivUpdateVerify.setImageResource(R.drawable.complete_blue);
                    binding.txtUpdateVerify.setText(getString(R.string.string_update_verify));
                    binding.txtUpdateVerify.setTextAppearance(mContext, R.style.fireware_update_style_updating);
                }, 1000);
            }
        });

        updateManager.update();
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIvUpdatePic.setImageResource(R.drawable.img_carepatch_simple);
    }

    @Override
    protected boolean showBackBtn() {
        return false;
    }

    @Override
    public void onBackPressed() {
        new WarmDialog(this)
                .setContent(R.string.string_updating_exit_will_cause_problem)
                .hideCancelBtn()
                .setConfirmText(R.string.string_i_konw)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateManager != null) {
            updateManager.stopUpdate();
        }
    }

    @Override
    public String getTopCenterText() {
        return getString(R.string.string_update_firware);
    }
}
