package com.wms.ble.operator;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.proton.bluetooth.BluetoothClient;
import com.proton.bluetooth.Constants;
import com.proton.bluetooth.connect.listener.BleConnectStatusListener;
import com.proton.bluetooth.connect.options.BleConnectOptions;
import com.proton.bluetooth.connect.response.BleConnectResponse;
import com.proton.bluetooth.connect.response.BleMtuResponse;
import com.proton.bluetooth.connect.response.BleNotifyResponse;
import com.proton.bluetooth.connect.response.BleReadResponse;
import com.proton.bluetooth.connect.response.BleUnnotifyResponse;
import com.proton.bluetooth.connect.response.BleWriteResponse;
import com.proton.bluetooth.model.BleGattProfile;
import com.proton.bluetooth.search.SearchRequest;
import com.proton.bluetooth.search.SearchResult;
import com.proton.bluetooth.search.response.SearchResponse;
import com.wms.ble.bean.ScanResult;
import com.wms.ble.callback.OnConnectListener;
import com.wms.ble.callback.OnReadCharacterListener;
import com.wms.ble.callback.OnScanListener;
import com.wms.ble.callback.OnSubscribeListener;
import com.wms.ble.callback.OnUnSubscribeListener;
import com.wms.ble.callback.OnWriteCharacterListener;
import com.wms.ble.utils.Logger;

import java.util.UUID;

/**
 * Created by wangmengsi on 2017/10/26.
 */

public class WmsBleOperator implements IBleOperator {

    private BluetoothClient mClient;
    private BleConnectOptions mConnectOptions = new BleConnectOptions.Builder()
            .setConnectRetry(2)
            .setConnectTimeout(5000)
            .setServiceDiscoverTimeout(5000)
            .setServiceDiscoverRetry(2).build();
    private OnConnectListener mConnectListener;
    /**
     * 是否正在扫描连接
     */
    private boolean isScanConnect;
    private BleConnectStatusListener mConnectStateListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == Constants.STATUS_DISCONNECTED) {
                if (mConnectListener != null) {
                    mConnectListener.onDisconnect(false);
                }
            }
        }
    };

    public WmsBleOperator(Context context) {
        mClient = new BluetoothClient(context);
    }

    @Override
    public void setConnectTimeoutTime(long time) {
        mConnectOptions.setConnectTimeout((int) time);
    }

    @Override
    public void setConnectListener(OnConnectListener listener) {
        mConnectListener = listener;
    }

    @Override
    public void connect(final String mac) {
        mClient.unregisterConnectStatusListener(mac, mConnectStateListener);
        mClient.connect(mac, mConnectOptions, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                if (mConnectListener != null) {
                    if (code == Constants.REQUEST_SUCCESS) {
                        mClient.registerConnectStatusListener(mac, mConnectStateListener);
                        BluetoothDevice device = mClient.getDevice(mac);
                        if (device != null) {
                            mConnectListener.onConnectSuccess(new ScanResult(device));
                        }
                        mConnectListener.onConnectSuccess();
                    } else {
                        mConnectListener.onConnectFaild();
                    }
                }
            }
        });
    }

    @Override
    public void scanDevice(final OnScanListener listener, int scanTime, final String... name) {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(scanTime)
                .build();
        mClient.search(request, new SearchResponse() {

            @Override
            public void onSearchStarted() {
                if (listener != null) {
                    listener.onScanStart();
                }
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                if (name == null || name.length <= 0) {
                    if (listener != null) {
                        listener.onDeviceFound(new ScanResult(device.device, device.rssi, device.scanRecord));
                    }
                    return;
                }
                for (String deviceName : name) {
                    if (deviceName.equalsIgnoreCase(device.getName())) {
                        if (listener != null) {
                            listener.onDeviceFound(new ScanResult(device.device, device.rssi, device.scanRecord));
                            break;
                        }
                    }
                }
            }

            @Override
            public void onSearchStopped() {
                if (listener != null) {
                    listener.onScanStopped();
                }
            }

            @Override
            public void onSearchCanceled() {
                if (listener != null) {
                    listener.onScanCanceled();
                }
            }
        });
    }

    @Override
    public void scanAndConnect(int scanTime, final String mac) {
        isScanConnect = true;
        scanDevice(new OnScanListener() {
            private boolean hasScanDevice;

            @Override
            public void onDeviceFound(final ScanResult result) {
                if (result != null && mac.equalsIgnoreCase(result.getDevice().getAddress())) {
                    hasScanDevice = true;
                    isScanConnect = false;
                    stopScan();
                    mClient.unregisterConnectStatusListener(mac, mConnectStateListener);
                    mClient.connect(mac, mConnectOptions, new BleConnectResponse() {
                        @Override
                        public void onResponse(int code, BleGattProfile data) {
                            if (mConnectListener != null) {
                                if (code == Constants.REQUEST_SUCCESS) {
                                    mClient.registerConnectStatusListener(mac, mConnectStateListener);
                                    mConnectListener.onConnectSuccess();
                                    mConnectListener.onConnectSuccess(result);
                                } else {
                                    mConnectListener.onConnectFaild();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onScanStopped() {
                Logger.w("扫描结束:", hasScanDevice);
                isScanConnect = false;
                if (mConnectListener != null && !hasScanDevice) {
                    mConnectListener.onConnectFaild();
                }
            }

            @Override
            public void onScanCanceled() {
                Logger.w("扫描取消:", hasScanDevice);
                isScanConnect = false;
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
        mClient.read(mac, UUID.fromString(uuid_server), UUID.fromString(uuid_charactor), new BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] data) {
                if (onReadCharacterListener == null) return;
                if (code == Constants.REQUEST_SUCCESS) {
                    onReadCharacterListener.onSuccess(data);
                } else if (code == Constants.REQUEST_FAILED) {
                    onReadCharacterListener.onFail();
                }
            }
        });
    }

    @Override
    public void write(String mac, String uuid_service, String uuid_charactor, byte[] value, final OnWriteCharacterListener onWriteCharacterListener) {
        mClient.write(mac, UUID.fromString(uuid_service), UUID.fromString(uuid_charactor), value, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (onWriteCharacterListener == null) return;
                if (code == Constants.REQUEST_SUCCESS) {
                    onWriteCharacterListener.onSuccess();
                } else if (code == Constants.REQUEST_FAILED) {
                    onWriteCharacterListener.onFail();
                }
            }
        });
    }

    @Override
    public void writeNoRsp(String mac, String uuid_service, String uuid_charactor, byte[] value, final OnWriteCharacterListener onWriteCharacterListener) {
        mClient.writeNoRsp(mac, UUID.fromString(uuid_service), UUID.fromString(uuid_charactor), value, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (onWriteCharacterListener == null) return;
                if (code == Constants.REQUEST_SUCCESS) {
                    onWriteCharacterListener.onSuccess();
                } else if (code == Constants.REQUEST_FAILED) {
                    onWriteCharacterListener.onFail();
                }
            }
        });
    }

    @Override
    public void writeDescriptor(String mac, String uuid_service, String uuid_charactor, String uuid_descriptor, byte[] value, final OnWriteCharacterListener onWriteCharacterListener) {
        mClient.writeDescriptor(mac, UUID.fromString(uuid_service), UUID.fromString(uuid_charactor), UUID.fromString(uuid_descriptor), value, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (onWriteCharacterListener == null) return;
                if (code == Constants.REQUEST_SUCCESS) {
                    onWriteCharacterListener.onSuccess();
                } else if (code == Constants.REQUEST_FAILED) {
                    onWriteCharacterListener.onFail();
                }
            }
        });
    }

    @Override
    public void disConnect(String mac) {
        mClient.disconnect(mac);
    }

    @Override
    public boolean isConnected(String mac) {
        return mClient.getConnectStatus(mac) == BluetoothProfile.STATE_CONNECTED;
    }

    @Override
    public void cancelConnect(String mac) {
        mClient.clearRequest(mac, 0);
        mClient.refreshCache(mac);
        disConnect(mac);
    }

    @Override
    public void openBluetooth() {
        mClient.openBluetooth();
    }

    @Override
    public void closeBluetooth() {
        mClient.closeBluetooth();
    }

    @Override
    public boolean isBluetoothOpened() {
        return mClient.isBluetoothOpened();
    }

    @Override
    public boolean isSupportBle() {
        return mClient.isBleSupported();
    }

    @Override
    public void subscribeNotification(String mac, String uuid_service, String uuid_charactor) {
        subscribeNotification(mac, uuid_service, uuid_charactor, null);
    }

    @Override
    public void subscribeNotification(String mac, String uuid_service, String uuid_charactor, final OnSubscribeListener listener) {
        mClient.notify(mac, UUID.fromString(uuid_service), UUID.fromString(uuid_charactor), new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, final byte[] value) {
                if (listener != null) {
                    listener.onNotify(character.toString(), value);
                }
            }

            @Override
            public void onResponse(int code) {
                if (listener == null) return;
                if (code == Constants.REQUEST_SUCCESS) {
                    listener.onSuccess();
                } else {
                    listener.onFail();
                }
            }
        });
    }

    @Override
    public void unsubscribeNotification(String mac, String uuid_service, String uuid_charactor) {
        unsubscribeNotification(mac, uuid_service, uuid_charactor, null);
    }

    @Override
    public void unsubscribeNotification(String mac, String uuid_service, String uuid_charactor, final OnUnSubscribeListener listener) {
        mClient.unnotify(mac, UUID.fromString(uuid_service), UUID.fromString(uuid_charactor), new BleUnnotifyResponse() {
            @Override
            public void onResponse(int code) {
                if (listener == null) return;
                if (code == Constants.REQUEST_SUCCESS) {
                    listener.onSuccess();
                } else {
                    listener.onFail();
                }
            }
        });
    }

    @Override
    public void stopScan() {
        if (isScanConnect) return;
        mClient.stopSearch();
    }

    @Override
    public void setMTU(String mac, int mtu) {
        mClient.requestMtu(mac, mtu + 3, new BleMtuResponse() {
            @Override
            public void onResponse(int i, Integer integer) {
            }
        });
    }
}
