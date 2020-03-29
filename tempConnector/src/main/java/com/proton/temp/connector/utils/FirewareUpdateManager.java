package com.proton.temp.connector.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.proton.temp.connector.R;
import com.proton.temp.connector.bean.DeviceType;
import com.proton.temp.connector.bluetooth.data.parse.BleDataParse;
import com.proton.temp.connector.bluetooth.data.parse.IBleDataParse;
import com.proton.temp.connector.bluetooth.data.uuid.CardUUID;
import com.proton.temp.connector.bluetooth.data.uuid.IDeviceUUID;
import com.proton.temp.connector.bluetooth.utils.BleUtils;
import com.wms.ble.bean.ScanResult;
import com.wms.ble.callback.OnConnectListener;
import com.wms.ble.callback.OnReadCharacterListener;
import com.wms.ble.callback.OnSubscribeListener;
import com.wms.ble.callback.OnWriteCharacterListener;
import com.wms.ble.operator.FastBleOperator;
import com.wms.ble.operator.IBleOperator;
import com.wms.ble.utils.BluetoothUtils;
import com.wms.logger.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by 王梦思 on 2018-11-06.
 * <p/>
 */
public class FirewareUpdateManager {

    /**
     * 数据解析
     */
    private IBleDataParse dataParse = new BleDataParse();
    /**
     * 设备uuid数据提供者
     */
    private IDeviceUUID deviceUUID = new CardUUID();

    /**
     * 首次扫描需要获取电量
     */
    private boolean isFirstScan = true;

    /**
     * 重置服务uuid
     */
    private static final String SERVICE_RESET = "f000ffd0-0451-4000-b000-000000000000";
    /**
     * 特征:重置服务特征
     */
    private static final String CHARACTOR_RESET = "f000ffd1-0451-4000-b000-000000000000";
    /**
     * 固件升级服务
     */
    private static final String SERVICE_UPDATE_FIRMWARE = "f000ffc0-0451-4000-b000-000000000000";
    /**
     * 固件升级特征值(可写)
     */
    private static final String CHARACTOR_UPDATE_WRITE = "f000ffc1-0451-4000-b000-000000000000";
    /**
     * 固件升级回调特征值(可订阅)
     */
    private static final String CHARACTOR_UPDATE_CALLBACK = "f000ffc2-0451-4000-b000-000000000000";
    /**
     * 固件升级状态的mac地址
     */
    private static final String UPDATE_MACADDRESS = "0A:D0:AD:0A:D0:AD";
    private FirewareAdapter firewareAdapter;

    private String filePath;
    private String macaddress;
    /**
     * 固件包文件大小
     */
    private long mFileSize;
    private IBleOperator bleOperator;
    private int buffSize = 16;
    private byte[] mFileBytes;
    private long startTime;
    private Context mContext;
    private DeviceType deviceType;
    private boolean hasResetDevice;
    /**
     * 当前连接的设备mac地址
     */
    private String connectDeviceMac;
    private BroadcastReceiver mBluetoothReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Logger.w("固件更新:蓝牙关闭");
                        updateFail(getString(R.string.connector_please_open_bluetooth), UpdateFailType.BLUETOOTH_NOT_OPEN);
                        break;
                }
            }
        }
    };

    public FirewareUpdateManager(Context context, String macaddress, FirewareAdapter firewareAdapter) {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null");
        }
        this.macaddress = macaddress;
        this.mContext = context;
        this.firewareAdapter = firewareAdapter;
    }

    public FirewareUpdateManager update() {
        if (!BluetoothUtils.isBluetoothOpened()) {
            updateFail(getString(R.string.connector_please_open_bluetooth), UpdateFailType.BLUETOOTH_NOT_OPEN);
            BluetoothUtils.openBluetooth();
            return this;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBluetoothReceive, filter);
        scanDevice(macaddress);
        return this;
    }

    private void scanDevice(final String mac) {
        bleOperator = new FastBleOperator(mContext);
        bleOperator.setConnectListener(new OnConnectListener() {
            @Override
            public void onConnectSuccess(final ScanResult result) {
                if (firewareAdapter == null) {
                    throw new IllegalArgumentException("you need to provide a firewareAdapter");
                }

                deviceType = BroadcastUtils.parseDeviceType(result.getScanRecord());
                filePath = firewareAdapter.getFirewarePath(deviceType);
                if (TextUtils.isEmpty(filePath) || !new File(filePath).exists()) {
                    updateFail(getString(R.string.connector_fireware_not_exist), UpdateFailType.FIREWARE_NOT_EXIST);
                    bleOperator.disConnect(mac);
                    return;
                }
                mFileBytes = toByteArray(filePath);
                mFileSize = mFileBytes.length;
                connectDeviceMac = result.getDevice().getAddress();
                Logger.w("固件升级:connectDeviceMac:", connectDeviceMac);
                Logger.w("固件升级:广播包:", BleUtils.bytesToHexString(result.getScanRecord()));

                if (isFirstScan && !judgeOad(result)) {
                    judgeBattery();
                    isFirstScan = false;
                    return;
                }
                isFirstScan = false;
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (hasResetDevice || connectDeviceMac.equals(UPDATE_MACADDRESS)) {
                            uploadData();
                        } else {
                            resetDevice();
                        }
                    }
                }, 200);


            }

            @Override
            public void onConnectFaild() {
                Logger.w("固件升级:连接失败:", mac);
                if (mac.equals(UPDATE_MACADDRESS)) {
                    updateFail(getString(R.string.connector_connect_fail), UpdateFailType.CONNECT_FAIL);
                } else {
                    scanDevice(UPDATE_MACADDRESS);
                }
            }

            @Override
            public void onDisconnect(boolean isManual) {
                if (!isManual) {
                    updateFail(getString(R.string.connector_device_disconnect), UpdateFailType.DISCONNECT);
                }
            }
        });
        bleOperator.scanAndConnect(mac);
    }


    /**
     * 判断是否是oad模式,如果是oad直接进行升级不用判断电量
     *
     * @param result
     * @return
     */
    private boolean judgeOad(ScanResult result) {
        //判断是否是oad模式
        byte[] scanRecord = result.getScanRecord();
        DeviceType type = BroadcastUtils.parseDeviceType(scanRecord);
        if (type != DeviceType.None) {
            String deviceName = result.getDevice().getName();
            if ("OAD THEM".equals(deviceName)) {
                Logger.w("oad模式升级不需要判断电量");
                return true;
            }
        }
        return false;
    }

    /**
     * 判断电量是否大于50%（异步操作）
     *
     * @return
     */
    private void judgeBattery() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                bleOperator.read(connectDeviceMac, deviceUUID.getServiceTemp(), deviceUUID.getCharactorBatteryUUID(), new OnReadCharacterListener() {
                    @Override
                    public void onFail() {
                        super.onFail();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (hasResetDevice || connectDeviceMac.equals(UPDATE_MACADDRESS)) {
                                    uploadData();
                                } else {
                                    resetDevice();
                                }
                            }
                        }, 200);
                    }

                    @Override
                    public void onSuccess(byte[] bytes) {
                        super.onSuccess(bytes);
                        int battery = dataParse.parseBattery(bytes);
                        if (battery < 30) {
                            updateFail(getString(R.string.string_battery_low_fifty), UpdateFailType.BATTERY_FAIL);
                        } else {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (hasResetDevice || connectDeviceMac.equals(UPDATE_MACADDRESS)) {
                                        uploadData();
                                    } else {
                                        resetDevice();
                                    }
                                }
                            }, 200);
                        }
                    }
                });
            }
        }, 200);
    }

    /**
     * 上传固件包
     */
    private void uploadData() {

        bleOperator.subscribeNotification(connectDeviceMac, SERVICE_UPDATE_FIRMWARE, CHARACTOR_UPDATE_CALLBACK, new OnSubscribeListener() {

            @Override
            public void onSuccess() {
                Logger.w("订阅成功");
                write(0, null);
            }

            @Override
            public void onFail() {
                Logger.w("订阅失败");
                updateFail(getString(R.string.connector_fireware_write_fail), UpdateFailType.SUBSCRIBE_FAIL);
            }

            @Override
            public void onNotify(String s, byte[] bytes) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onFirewareStatusListener.onUpdateReadyCompelete();//升级准备阶段完成
                        onFirewareStatusListener.onUpdatind();//升级中，固件写入
                    }
                });
                String countString = BleUtils.bytesToHexString(bytes).substring(0, 4);
                int index = Integer.parseInt(countString.substring(2, 4) + countString.substring(0, 2), 16);
                write(index, bytes);
            }
        });

        startTime = System.currentTimeMillis();
    }

    private void write(final int index, byte[] data) {
        final float progress = (float) index * buffSize / mFileSize;
        byte[] temp = new byte[buffSize];
        System.arraycopy(mFileBytes, buffSize * index, temp, 0, buffSize);
        byte[] writeData;
        if (data != null) {
            writeData = new byte[data.length + temp.length];
            System.arraycopy(data, 0, writeData, 0, data.length);
            System.arraycopy(temp, 0, writeData, data.length, temp.length);
        } else {
            writeData = temp;
        }
        bleOperator.writeNoRsp(connectDeviceMac, SERVICE_UPDATE_FIRMWARE, data == null ? CHARACTOR_UPDATE_WRITE : CHARACTOR_UPDATE_CALLBACK, writeData, new OnWriteCharacterListener() {
            @Override
            public void onFail() {
                updateFail(getString(R.string.connector_fireware_write_fail), UpdateFailType.FIREWARE_WRITE_FAIL);
            }
        });
        //这个回调不放到写入成功是因为有时候会出现最后一包不回调导致提示升级失败
        if (onFirewareUpdateListener != null) {
            onFirewareUpdateListener.onProgress(progress);
            if (progress >= 0.99 && progress < 1) {
                onFirewareStatusListener.onUpdatingComplete();
                onFirewareStatusListener.onUpdateVerify();
            }
            if (index == mFileSize / buffSize - 1) {
                onFirewareStatusListener.onUpdateVerifyComplete();
                onFirewareUpdateListener.onSuccess(deviceType, connectDeviceMac);
                Logger.w("升级总耗时:", (System.currentTimeMillis() - startTime));
            }
        }
    }

    private static byte[] toByteArray(String filename) {
        File f = new File(filename);
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 重置设备,设备重置成功后,设备重启，蓝牙地址会变成0A:D0:AD:0A:D0:AD
     */
    private void resetDevice() {
        bleOperator.write(connectDeviceMac, SERVICE_RESET, CHARACTOR_RESET, BleUtils.hexStringToBytes("01"), new OnWriteCharacterListener() {
            @Override
            public void onSuccess() {
                Logger.w("固件升级:重置成功");
                hasResetDevice = true;
                bleOperator.disConnect(connectDeviceMac);
                scanDevice(connectDeviceMac);
            }

            @Override
            public void onFail() {
                Logger.w("固件升级:重置失败");
                uploadData();
            }
        });
    }

    public void stopUpdate() {
        try {
            if (mBluetoothReceive != null) {
                mContext.unregisterReceiver(mBluetoothReceive);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bleOperator != null) {
            bleOperator.disConnect(macaddress);
            bleOperator.disConnect(UPDATE_MACADDRESS);
        }
    }

    private void updateFail(String msg, UpdateFailType type) {
        if (onFirewareUpdateListener != null) {
            onFirewareUpdateListener.onFail(msg, type);
        }
        stopUpdate();
    }

    public void setOnFirewareUpdateListener(OnFirewareUpdateListener onFirewareUpdateListener) {
        this.onFirewareUpdateListener = onFirewareUpdateListener;
    }

    public interface OnFirewareUpdateListener {
        /**
         * 更新成功
         */
        void onSuccess(DeviceType type, String macaddress);

        /**
         * 更新失败
         *
         * @param msg  更新失败原因文字，已做国际化(中英文)
         * @param type 更新失败类型
         */
        void onFail(String msg, UpdateFailType type);

        /**
         * 更新进度
         */
        void onProgress(float progress);
    }

    private OnFirewareUpdateListener onFirewareUpdateListener;

    private OnFirewareStatusListener onFirewareStatusListener;

    public void setOnFirewareStatusListener(OnFirewareStatusListener onFirewareStatusListener) {
        this.onFirewareStatusListener = onFirewareStatusListener;
    }

    public interface OnFirewareStatusListener {

        /**
         * 升级准备完成
         */
        void onUpdateReadyCompelete();

        /**
         * 升级中
         */
        void onUpdatind();

        /**
         * 升级中完成
         */
        void onUpdatingComplete();

        /**
         * 验证中
         */
        void onUpdateVerify();

        /**
         * 验证完成
         */
        void onUpdateVerifyComplete();
    }


    private String getString(int stringRes) {
        return mContext.getString(stringRes);
    }

    public enum UpdateFailType {
        /**
         * 连接中断
         */
        DISCONNECT,
        /**
         * 固件不存在
         */
        FIREWARE_NOT_EXIST,
        /**
         * 蓝牙没有打开
         */
        BLUETOOTH_NOT_OPEN,
        /**
         * 连接失败
         */
        CONNECT_FAIL,
        /**
         * 订阅失败
         */
        SUBSCRIBE_FAIL,
        /**
         * 固件写入失败
         */
        FIREWARE_WRITE_FAIL,
        /**
         * 获取电量失败(电量不足)
         */
        BATTERY_FAIL
    }

    public interface FirewareAdapter {
        /**
         * 获取固件的本地路径
         */
        String getFirewarePath(DeviceType type);
    }
}
