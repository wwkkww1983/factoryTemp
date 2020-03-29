package com.proton.carepatchtemp.utils.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.proton.carepatchtemp.R;
import com.wms.ble.utils.BluetoothUtils;
import com.wms.logger.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yuxiongfeng.
 * Date: 2019/5/14
 * 设备版本需要 api 21  android5.0以上才能发送广播
 */
public class BleManager {

    private static final String TAG = "bleManager";
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private String originalName;//用户原始蓝牙名称
    private static final String ADVERTISE_LOCAL_NAME = "proton";//要发送广播数据包的本地名字

    private ExecutorService service = Executors.newSingleThreadExecutor();

    /**
     * 静态内部类实现单例（优势：懒加载，线程安全，高效）
     */
    private static class TempInner {
        private static final BleManager instance = new BleManager();
    }

    public static BleManager getInstance() {
        return TempInner.instance;
    }

    /**
     * 发送广播数据包
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertising(Activity activity, int requestCode) {

        if (!BluetoothUtils.isSupportBle()) {
            Logger.w(R.string.string_not_support_ble);
            return;
        }

        /**
         * 打开蓝牙设备
         */
        if (!BluetoothUtils.isBluetoothOpened()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, requestCode);
            return;
        }

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        }

        if (mBluetoothManager != null && mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }

        //获取BluetoothLeAdvertiser，BLE发送BLE广播用的一个API
        if (mBluetoothLeAdvertiser == null) {
            mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        }

        //获取设备的初始蓝牙名称
        originalName = mBluetoothAdapter.getName();
        //设置蓝牙本地名称
        mBluetoothAdapter.setName(ADVERTISE_LOCAL_NAME);

        service.execute(() -> {
            try {
                mBluetoothLeAdvertiser.startAdvertising(BleUtil.createAdvSettings(), BleUtil.createAdvertiseData(), mAdvCallback);
            } catch (Exception e) {
                if (!TextUtils.isEmpty(e.getMessage())) {
                    Logger.w(e.getMessage());
                } else {
                    Logger.w("fail start advertise");
                }
            }
        });

    }

    /**
     * 停止广播发送
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopAdvertising() {
        if (!BluetoothUtils.isSupportBle()) {
            Logger.w(R.string.string_not_support_ble);
            return;
        }
        //关闭BluetoothLeAdvertiser,BluetoothAdapter
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvCallback);
            mBluetoothLeAdvertiser = null;
        }

        if (mBluetoothAdapter != null) {
            //关闭广播发送的时候将蓝牙设备名字还原
            mBluetoothAdapter.setName(originalName);
            mBluetoothAdapter = null;
        }
    }

    public boolean isStopedAdvertise() {
        if (mBluetoothLeAdvertiser != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothLeAdvertiser.stopAdvertising(mAdvCallback);
            }
            mBluetoothLeAdvertiser = null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) return true;

        if (mBluetoothLeAdvertiser == null || mBluetoothAdapter == null) {
        }

        return true;
    }


    /**
     * 发送广播的回调
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Logger.w("Advertise Start Success");
            if (settingsInEffect != null) {
                Logger.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode() + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Logger.d("onStartSuccess, settingInEffect is null");
            }
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            String description;
            switch (errorCode) {
                case ADVERTISE_FAILED_DATA_TOO_LARGE:
                    description = "data is to large";
                    break;
                case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    description = "no advertising instance is available";
                    break;
                case ADVERTISE_FAILED_ALREADY_STARTED:
                    description = "advertising is already started";
                    break;
                case ADVERTISE_FAILED_INTERNAL_ERROR:
                    description = "Operation failed due to an internal error";
                    break;
                case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    description = "This feature is not supported on this platform";
                    break;
                default:
                    description = "fail";
            }
            Logger.w(description);
        }
    };


}
