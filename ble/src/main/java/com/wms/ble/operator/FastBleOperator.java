package com.wms.ble.operator;

import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.wms.ble.bean.ScanResult;
import com.wms.ble.callback.OnConnectListener;
import com.wms.ble.callback.OnReadCharacterListener;
import com.wms.ble.callback.OnScanListener;
import com.wms.ble.callback.OnSubscribeListener;
import com.wms.ble.callback.OnUnSubscribeListener;
import com.wms.ble.callback.OnWriteCharacterListener;
import com.wms.ble.utils.Logger;

import java.util.List;

/**
 * Created by wangmengsi on 2017/10/26.
 */
public class FastBleOperator implements IBleOperator {
    private OnConnectListener mConnectListener;
    private BleManager mBleManager = BleManager.getInstance();
    private BleDevice mConnectDevice;
    private byte[] mConnectDeviceScanRecord;

    public FastBleOperator(Context context) {
        mBleManager.init((Application) context);
        mBleManager
                .setReConnectCount(1, 10000)
                .setConnectOverTime(15000)
                .setOperateTimeout(15000);
    }

    @Override
    public void setConnectTimeoutTime(long time) {
    }

    @Override
    public void setConnectListener(OnConnectListener listener) {
        this.mConnectListener = listener;
    }

    @Override
    public void connect(String mac) {
        mBleManager.connect(mac, new BleGattCallback() {
            @Override
            public void onStartConnect() {
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                // 连接失败
                if (mConnectListener != null) {
                    mConnectListener.onConnectFaild();
                }
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                // 连接成功，BleDevice即为所连接的BLE设备
                mConnectDevice = bleDevice;
                if (mConnectListener != null) {
                    mConnectListener.onConnectSuccess();
                    if (bleDevice.getScanRecord() == null) {
                        bleDevice.setScanRecord(mConnectDeviceScanRecord);
                    }
                    mConnectListener.onConnectSuccess(new ScanResult(bleDevice.getDevice(), bleDevice.getRssi(), bleDevice.getScanRecord()));
                }
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                // 连接中断，isActiveDisConnected表示是否是主动调用了断开连接方法
                if (mConnectListener != null) {
                    mConnectListener.onDisconnect(isActiveDisConnected);
                }
                mConnectDevice = null;
            }
        });

    }

    @Override
    public void scanDevice(final OnScanListener listener, int scanTime, final String... name) {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setDeviceName(true, name)         // 只扫描指定广播名的设备，可选
                .setScanTimeOut(scanTime)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();
        mBleManager.initScanRule(scanRuleConfig);
        mBleManager.scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                if (listener != null) {
                    listener.onScanStart();
                }
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if (listener != null) {
                    listener.onDeviceFound(new ScanResult(bleDevice.getDevice(), bleDevice.getRssi(), bleDevice.getScanRecord()));
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                if (listener != null) {
                    listener.onScanStopped();
                }
            }
        });
    }

    @Override
    public void scanAndConnect(int scanTime, final String mac) {
        scanDevice(new OnScanListener() {
            private boolean hasScanDevice;

            @Override
            public void onDeviceFound(final ScanResult result) {
                if (mac.equalsIgnoreCase(result.getDevice().getAddress())) {
                    hasScanDevice = true;
                    stopScan();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mConnectDeviceScanRecord = result.getScanRecord();
                            connect(mac);
                        }
                    }, 500);
                }
            }

            @Override
            public void onScanStopped() {
                if (mConnectListener != null && !hasScanDevice) {
                    mConnectListener.onConnectFaild();
                }
            }

            @Override
            public void onScanCanceled() {
                if (mConnectListener != null && !hasScanDevice) {
                    mConnectListener.onConnectFaild();
                }
            }
        }, scanTime);
    }

    @Override
    public void scanAndConnect(String mac) {
        scanAndConnect(10000, mac);
    }

    @Override
    public void read(String mac, String uuid_server, String uuid_charactor, final OnReadCharacterListener onReadCharacterListener) {
        mBleManager.read(mConnectDevice, uuid_server, uuid_charactor, new BleReadCallback() {
            @Override
            public void onReadSuccess(byte[] data) {
                // 读特征值数据成功
                if (onReadCharacterListener != null) {
                    onReadCharacterListener.onSuccess(data);
                }
            }

            @Override
            public void onReadFailure(BleException exception) {
                // 读特征值数据失败
                if (onReadCharacterListener != null) {
                    onReadCharacterListener.onFail();
                }
            }
        });
    }

    @Override
    public void write(String mac, String uuid_service, String uuid_charactor, byte[] value, final OnWriteCharacterListener onWriteCharacterListener) {
        mBleManager.write(mConnectDevice, uuid_service, uuid_charactor, value, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                // 发送数据到设备成功
                if (onWriteCharacterListener != null) {
                    onWriteCharacterListener.onSuccess();
                }
            }

            @Override
            public void onWriteFailure(BleException exception) {
                // 发送数据到设备失败
                if (onWriteCharacterListener != null) {
                    onWriteCharacterListener.onFail();
                }
            }
        });
    }

    @Override
    public void writeNoRsp(String mac, String uuid_service, String uuid_charactor, byte[] value, final OnWriteCharacterListener onWriteCharacterListener) {
        mBleManager.write(mConnectDevice, uuid_service, uuid_charactor, value, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                // 发送数据到设备成功
                if (onWriteCharacterListener != null) {
                    onWriteCharacterListener.onSuccess();
                }
            }

            @Override
            public void onWriteFailure(BleException exception) {
                // 发送数据到设备失败
                Logger.w("exception:" + exception.toString());
                if (onWriteCharacterListener != null) {
                    onWriteCharacterListener.onFail();
                }
            }
        });
    }

    @Override
    public void writeDescriptor(String mac, String uuid_service, String uuid_charactor, String uuid_descriptor, byte[] value, OnWriteCharacterListener onWriteCharacterListener) {
    }

    @Override
    public void disConnect(String mac) {
        mBleManager.disconnect(mConnectDevice);
        mConnectDevice = null;
        mConnectDeviceScanRecord = null;
    }

    @Override
    public boolean isConnected(String mac) {
        return mBleManager.isConnected(mac);
    }

    @Override
    public void cancelConnect(String mac) {
        mBleManager.destroy();
    }

    @Override
    public void openBluetooth() {
        mBleManager.enableBluetooth();
    }

    @Override
    public void closeBluetooth() {
        mBleManager.disableBluetooth();
    }

    @Override
    public boolean isBluetoothOpened() {
        return mBleManager.isBlueEnable();
    }

    @Override
    public boolean isSupportBle() {
        return mBleManager.isSupportBle();
    }

    @Override
    public void subscribeNotification(String mac, String uuid_service, String uuid_charactor) {
        subscribeNotification(mac, uuid_service, uuid_charactor, null);
    }

    @Override
    public void subscribeNotification(String mac, final String uuid_service, final String uuid_charactor, final OnSubscribeListener listener) {
        if (mConnectDevice == null) return;
        mBleManager.notify(mConnectDevice, uuid_service, uuid_charactor, new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {
                // 打开通知操作成功
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onNotifyFailure(BleException exception) {
                // 打开通知操作失败
                if (listener != null) {
                    listener.onFail();
                }
            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                // 打开通知后，设备发过来的数据将在这里出现
                if (listener != null) {
                    listener.onNotify(uuid_charactor, data);
                }
            }
        });
    }

    @Override
    public void unsubscribeNotification(String mac, String uuid_service, String uuid_charactor) {
        unsubscribeNotification(mac, uuid_service, uuid_charactor, null);
    }

    @Override
    public void unsubscribeNotification(String mac, String uuid_service, String uuid_charactor, OnUnSubscribeListener listener) {
        mBleManager.stopNotify(mConnectDevice, uuid_service, uuid_charactor);
        if (listener != null) {
            listener.onSuccess();
        }
    }

    @Override
    public void stopScan() {
        mBleManager.cancelScan();
    }

    @Override
    public void setMTU(String mac, int mtu) {
    }
}
