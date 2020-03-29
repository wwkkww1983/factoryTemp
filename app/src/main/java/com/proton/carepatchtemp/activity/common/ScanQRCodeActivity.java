package com.proton.carepatchtemp.activity.common;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.text.TextUtils;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.bean.ScanDeviceInfoBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.databinding.ActivityScanQrcodeBinding;
import com.proton.carepatchtemp.enums.QRPatchType;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.DeviceCenter;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.carepatchtemp.utils.UrlParse;
import com.proton.temp.connector.bean.DeviceType;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.wms.logger.Logger;
import com.wms.utils.NetUtils;

import java.util.Map;

/**
 * Created by wangmengsi on 2018/2/26.
 */
public class ScanQRCodeActivity extends BaseActivity<ActivityScanQrcodeBinding> {
    private ProfileBean mProfile;
    private boolean go2ScanDevice;
    private CaptureFragment captureFragment;
    private String deviceType;
    private String macaddress;
    /**
     * 当前activity是否依附在AddNewDeviceActivity  1:是依附在addNewDeviceActivity   0：依附在homeActivity
     */
    private int isAttachAddNew;


    @Override
    protected int inflateContentView() {
        return R.layout.activity_scan_qrcode;
    }

    @Override
    protected void init() {
        super.init();
        mProfile = (ProfileBean) getIntent().getSerializableExtra("profile");
        go2ScanDevice = getIntent().getBooleanExtra("go2ScanDevice", false);
        if (mProfile != null) {
            App.get().getHasScanQRCode().add(mProfile.getProfileId());
            isAttachAddNew=mProfile.getIsAttachAddNew();
        }
        showScanFragment();
    }

    private void showScanFragment() {
        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.layout_camera, false);
        captureFragment.setAnalyzeCallback(new CodeUtils.AnalyzeCallback() {
            @Override
            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                Logger.w("二维码扫描结果:" + result);
                parseQRCode(result);
            }

            @Override
            public void onAnalyzeFailed() {
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.id_qrcode_container, captureFragment).commit();
    }

    /**w
     * 二维码格式
     * http://www.protontek.com/device/temp?type=P03&macId=0C:61:CF:C7:E7:0E&sn=18051601003
     */
    private void parseQRCode(String result) {
        if (!NetUtils.isConnected(mContext)) {
            reScan();
            return;
        }
        if (TextUtils.isEmpty(result)) {
            BlackToast.show(R.string.string_qrcode_invalid);
            reScan();
            return;
        }

        String prefix = "http://www.protontek.com/device/temp";
        if (!result.startsWith(prefix)) {
            BlackToast.show(R.string.string_qrcode_invalid);
            reScan();
            return;
        }

        //解析设备类型和mac地址
        Map<String, String> params = UrlParse.getUrlParams(result);
        if (params.containsKey("type")
                && params.containsKey("macId")
                && params.containsKey("sn")) {
            macaddress = params.get("macId");
            deviceType = params.get("type");

            /**
             * 添加润生体温贴的过滤
             */
            QRPatchType qrPatchType = QRPatchType.getQRPatchType(deviceType);
            Logger.w("扫描到的体温贴类型是：", qrPatchType.getType(), "  ", qrPatchType.getDes());
            if (qrPatchType == QRPatchType.P07) {
                BlackToast.show(R.string.string_cannot_recongnize);
                getSupportFragmentManager().beginTransaction().remove(captureFragment);
                showScanFragment();
                return;
            }

            if (qrPatchType == QRPatchType.P10) {
                BlackToast.show(R.string.string_cannot_recongnize);
                return;
            }
            getDeviceInfo();
        } else {
            BlackToast.show(R.string.string_qrcode_invalid);
            reScan();
        }
    }

    private void getDeviceInfo() {
        DeviceCenter.getScanDeviceInfo(macaddress, new NetCallBack<ScanDeviceInfoBean>() {

            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
                reScan();
            }

            @Override
            public void onSucceed(ScanDeviceInfoBean data) {
                if (App.get().isLogined()) {
                    addDevice(data);
                } else {
                    SpUtils.saveString(AppConfigs.SP_KEY_EXPERIENCE_BIND_DEVICE, macaddress);
                    finishOrGoToWeb();
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                reScan();
                BlackToast.show(resultPair.getData());
            }
        });
    }

    private void reScan() {
        binding.getRoot().postDelayed(() -> {
            if (captureFragment != null) {
                captureFragment.restartPreviewAndDecode();
            }
        }, 2000);
    }

    /**
     * 绑定分两步先添加设备，然后调用编辑档案接口
     */
    private void addDevice(ScanDeviceInfoBean deviceInfo) {
        showDialog();
        DeviceCenter.addDevice(DeviceType.valueOf(deviceInfo.getType()).toString(), deviceInfo.getSerialNum(), macaddress, deviceInfo.getVersion(), new NetCallBack<String>() {

            @Override
            public void noNet() {
                super.noNet();
                dismissDialog();
                BlackToast.show(R.string.string_no_net);
                reScan();
            }

            @Override
            public void onSucceed(String data) {
                //添加成功
                bindDevice(data);
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_CHANGED));
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                dismissDialog();
                Logger.w("添加设备失败:" + resultPair.getData());
                reScan();
            }
        });
    }

    private void bindDevice(String deviceId) {
        if (mProfile == null || mProfile.getProfileId() == -1) {
            dismissDialog();
            App.get().setLastScanDeviceId(deviceId);
            IntentUtils.goToMain(mContext);
            return;
        }
        DeviceCenter.editShareProfile(String.valueOf(mProfile.getProfileId()), deviceId, false, new NetCallBack<Boolean>() {

            @Override
            public void noNet() {
                super.noNet();
                dismissDialog();
                BlackToast.show(R.string.string_no_net);
                reScan();
            }

            @Override
            public void onSucceed(Boolean data) {
                dismissDialog();
                mProfile.setMacaddress(macaddress);
                finishOrGoToWeb();
                Logger.w("更新分享设备成功");
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                dismissDialog();
                BlackToast.show(R.string.string_bind_fail);
                reScan();
            }
        });
    }

    private void finishOrGoToWeb() {
//        if (!ActivityManager.hasActivity(HomeActivity.class)) {
//            //可能是首次注册从添加档案进来
//            IntentUtils.goToMain(mContext);
//        }
        if (DeviceType.valueOf(deviceType) == DeviceType.P03) {
//            IntentUtils.goToWeb(mContext, HttpUrls.URL_SCAN_HELP + "?profileId=" + (mProfile == null ? "-1" : mProfile.getProfileId()), true);
            IntentUtils.goToDockerSetNetwork(mContext,false,"",mProfile);
            finish();
        } else {
            EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_BIND_SUCCESS, mProfile));

            if (!ActivityManager.hasActivity(HomeActivity.class)) {
                //可能是首次注册从添加档案进来
                IntentUtils.goToMain(mContext);
            }
            finish();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idOpenLight.setOnClickListener(v -> {
            try {
                CodeUtils.isLightEnable(!binding.getIsLightOn());
                binding.setIsLightOn(!binding.getIsLightOn());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.idNoQrcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.idNoQrcode.getPaint().setAntiAlias(true);
        binding.idNoQrcode.setOnClickListener(v -> {
            if (ActivityManager.hasActivity(HomeActivity.class)) {
                if (go2ScanDevice) {
                    if (isAttachAddNew==1) {
                        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.ADD_NEW_DEVICE_BIND, mProfile));
                    }else {
                        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_BIND_SUCCESS, mProfile));
                    }
                } else {
                    unbind();
                }
            } else {
                IntentUtils.goToMain(mContext);
            }
            finish();
        });
    }

    private void unbind() {
        DeviceCenter.unbind(mProfile.getProfileId(), new NetCallBack<Boolean>() {
            @Override
            public void onSucceed(Boolean data) {
                if (data) {
                    mProfile.setMacaddress("");
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.UNBIND_DEVICE_SUCCESS, mProfile));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mProfile != null) {
            if (go2ScanDevice) {
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_BIND_SUCCESS, mProfile));
            }
        }
        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SCAN_COMPLETE));
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.setIsLightOn(false);
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    protected boolean showBackBtn() {
        return false;
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public String getTopCenterText() {
        return getString(R.string.string_scan_temp);
    }


}
