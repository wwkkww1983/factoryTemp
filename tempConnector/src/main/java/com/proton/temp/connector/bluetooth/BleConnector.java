package com.proton.temp.connector.bluetooth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.proton.temp.connector.bean.DeviceBean;
import com.proton.temp.connector.bean.DeviceType;
import com.proton.temp.connector.bean.TempDataBean;
import com.proton.temp.connector.bluetooth.callback.OnReceiverBleDataListener;
import com.proton.temp.connector.bluetooth.callback.OnScanListener;
import com.proton.temp.connector.bluetooth.data.parse.BleDataParse;
import com.proton.temp.connector.bluetooth.data.parse.IBleDataParse;
import com.proton.temp.connector.bluetooth.data.uuid.CardUUID;
import com.proton.temp.connector.bluetooth.data.uuid.IDeviceUUID;
import com.proton.temp.connector.bluetooth.utils.BleUtils;
import com.proton.temp.connector.interfaces.ConnectStatusListener;
import com.proton.temp.connector.interfaces.Connector;
import com.proton.temp.connector.interfaces.DataListener;
import com.proton.temp.connector.utils.BroadcastUtils;
import com.proton.temp.connector.utils.ConnectorSetting;
import com.wms.ble.BleOperatorManager;
import com.wms.ble.bean.ScanResult;
import com.wms.ble.callback.OnConnectListener;
import com.wms.ble.callback.OnReadCharacterListener;
import com.wms.ble.callback.OnSubscribeListener;
import com.wms.ble.callback.OnWriteCharacterListener;
import com.wms.ble.operator.IBleOperator;
import com.wms.ble.operator.WmsBleOperator;
import com.wms.ble.utils.BluetoothUtils;
import com.wms.ble.utils.ScanManager;
import com.wms.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 王梦思 on 2017/7/7.
 * ble设备管理器
 */
public class BleConnector implements Connector {
    /**
     * 设备管理器存储
     */
    private static HashMap<String, BleConnector> bleManagerMap = new HashMap<>();
    private static Context mContext;
    private static com.wms.ble.callback.OnScanListener mScanCallback;
    private IBleOperator mBleOperator;
    /**
     * 接受数据监听器
     */
    private OnReceiverBleDataListener mReceiverDataListener;
    /**
     * 数据解析
     */
    private IBleDataParse dataParse = new BleDataParse();
    /**
     * 设备uuid数据提供者
     */
    private IDeviceUUID deviceUUID = new CardUUID();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    /**
     * 当前设备管理的mac地址
     */
    private String macaddress;
    /**
     * 获取缓存温度写的值
     */
    private String mGetCacheTempWriteValue;
    /**
     * 缓存温度数量
     */
    private int mCacheTempCount;
    /**
     * 缓存温度
     */
    private List<TempDataBean> mCacheTemps = new ArrayList<>();
    private ConnectStatusListener connectStatusListener;
    private Timer mGetTempTimer;
    /**
     * 当蓝牙断开失败的时候，重复断开
     */
    private Timer mCycDisconnectTimer;
    private int disconnectCount = 0;
    /**
     * 循环5次
     */
    private int disconnectTotalCount = Integer.MAX_VALUE;

    private DataListener dataListener;
    /**
     * 当前采样频率
     */
    private int mCurrentSampleRate = 4;
    /**
     * 是否是1.5版本蓝牙，目前体温贴包含两个蓝牙版本，处理方式不一样
     */
    private boolean isV1_5;

    /**
     * 从第二次开始接收数据的时候开始检测体温贴是否断裂
     */
    private boolean isFirstCheckPatchEnable = true;

    /**
     * 校准回调
     */
    private CalibrateCallBack calibrateCallBack;

    public void setCalibrateCallBack(CalibrateCallBack calibrateCallBack) {
        this.calibrateCallBack = calibrateCallBack;
    }

    private BleConnector(Context context, String macaddress, boolean isV1_5) {
        if (context != null) {
            mBleOperator = new WmsBleOperator(context);
        }
        this.macaddress = macaddress;
        this.isV1_5 = isV1_5;
    }

    /**
     * @param context application context
     */
    public static void init(Context context) {
        mContext = context;
        BleOperatorManager.init(context);
    }

    public static BleConnector getInstance(String macaddress) {
        return getInstance(macaddress, false);
    }

    public static BleConnector getInstance(String macaddress, boolean isV1_5) {
        if (mContext == null) {
            throw new IllegalStateException("You should initialize BleConnector before using,You can initialize in your Application class");
        }
        if (!bleManagerMap.containsKey(macaddress)) {
            bleManagerMap.put(macaddress, new BleConnector(mContext, macaddress, isV1_5));
        }
        return bleManagerMap.get(macaddress);
    }

    /**
     * 通过mac地址连接
     */
    public void connect() {
        if (mCycDisconnectTimer != null) {
            mCycDisconnectTimer.cancel();
            mCycDisconnectTimer = null;
        }
        connect(null, null);
    }

    @Override
    public void connect(final ConnectStatusListener connectListener, DataListener dataListener) {
        if (mCycDisconnectTimer != null) {
            mCycDisconnectTimer.cancel();
            mCycDisconnectTimer = null;
        }
        this.connectStatusListener = connectListener;
        this.dataListener = dataListener;
        if (!BluetoothUtils.isBluetoothOpened()) {
            if (connectStatusListener == null) return;
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (connectStatusListener != null) {
                        connectStatusListener.onConnectFaild();
                    }
                }
            }, ConnectorSetting.NO_BLUETOOTH_RECONNECT_TIME);
            return;
        }
        if (isConnected()) {
            return;
        }
        clear(false);
        mBleOperator.setConnectListener(new OnConnectListener() {

            @Override
            public void onConnectSuccess(ScanResult result) {
                if (result != null && result.getScanRecord() != null && result.getScanRecord().length > 0) {
                    isV1_5 = BroadcastUtils.parseDeviceType(result.getScanRecord()) != DeviceType.P02;
                }
                doConnectSuccess();
            }

            @Override
            public void onConnectFaild() {
                if (connectStatusListener != null) {
                    connectStatusListener.onConnectFaild();
                }
            }

            @Override
            public void onDisconnect(boolean isManual) {
                Logger.w("设备断开连接了");
                if (connectStatusListener != null) {
                    connectStatusListener.onDisconnect(false);
                }
                clear(false);
            }
        });

        if (isV1_5) {
            //已经明确指定了设备类型
            mBleOperator.connect(macaddress);
        } else {
            //没有指定设备类型或者就是p02设备，这时候扫描一下，目的是为了自动区分设备类型
            mBleOperator.scanAndConnect(macaddress);
        }
    }

    private void doConnectSuccess() {
        if (connectStatusListener != null) {
            connectStatusListener.onConnectSuccess();
        }
        setDataListener();
        //订阅电量通知
        subscribeBatteryNotification();
        //获取电量
        getBattery();
        //获取序列号
        getSerial();
        //获取版本号
        getHardVersion();
        //连接成功，则读取缓存，每4秒获取温度
        getCacheTemp();
    }

    private void setDataListener() {
        mReceiverDataListener = new OnReceiverBleDataListener() {
            @Override
            public void receiveBattery(Integer battery) {
                if (dataListener != null) {
                    dataListener.receiveBattery(battery);
                }
            }

            @Override
            public void receiveCharge(boolean isCharge) {
                if (dataListener != null) {
                    dataListener.receiveCharge(isCharge);
                }
            }

            @Override
            public void receiveSerial(String serial) {
                if (dataListener != null) {
                    dataListener.receiveSerial(serial);
                }
            }

            @Override
            public void receiveHardVersion(String hardVersion) {
                if (dataListener != null) {
                    dataListener.receiveHardVersion(hardVersion);
                }
            }

            @Override
            public void receiveCurrentTemp(TempDataBean currentTemp) {
                if (dataListener != null) {
                    dataListener.receiveCurrentTemp(currentTemp);
                }
            }

            @Override
            public void receiveCurrentTemp(List<TempDataBean> temps) {
                if (dataListener != null) {
                    dataListener.receiveCurrentTemp(temps);
                }
            }

            @Override
            public void onReadCacheFinish() {
                if (isV1_5) {
                    Logger.w("v1.5固件设备");
                    subscribeTempNotification();
                } else {
                    Logger.w("v1.0固件设备");
                    startGetTempTimer();
                }
            }


            @Override
            public void judgeCarepatchEnable(boolean isEnable) {
                dataListener.judgeCarepatchEnable(isEnable);
            }
        };
    }

    /**
     * 扫描全部的体温贴设备
     */
    public static void scanDevice(final OnScanListener listener) {
        scanDevice(10000, listener);
    }

    /**
     * 扫描全部的体温贴设备
     */
    public static void scanDevice(final int scanTime, final OnScanListener listener) {
        mScanCallback = new MyScanCallback(listener);
        ScanManager.getInstance().scan(mScanCallback, scanTime);
    }

    /**
     * 获取当前的温度
     */
    public void getCurrentTemp() {
        Logger.w("读温度");
        mBleOperator.read(macaddress, deviceUUID.getServiceTemp(), deviceUUID.getCharactorTemp(), new OnReadCharacterListener() {
            @Override
            public void onFail() {
                Logger.w("读温度失败");
            }

            @Override
            public void onSuccess(byte[] data) {
                parseTemp(data);
            }
        });
    }

    /**
     * 获取缓存的温度分三步：
     * 1，先订阅缓存通知
     * 2，写一个值0x01用来获取多少个数据，（一包10个温度值）
     * 3，写一个值0x02用来获取包数据
     */
    public void getCacheTemp() {
        Logger.w("获取缓存");
        subscribeCacheTempNotification();
        mGetCacheTempWriteValue = "01";
        writeCacheTemp(BleUtils.hexStringToBytes(mGetCacheTempWriteValue));
    }

    /**
     * 向缓存温度特征值中写数据
     */
    public void writeCacheTemp(byte[] value, OnWriteCharacterListener onWriteCharacterListener) {
        mBleOperator.write(macaddress, deviceUUID.getServiceTemp(), deviceUUID.getCharactorCacheTempSend(), value, onWriteCharacterListener);
    }

    /**
     * 向缓存温度特征值中写数据
     */
    public void writeCacheTemp(byte[] value) {
        mBleOperator.write(macaddress, deviceUUID.getServiceTemp(), deviceUUID.getCharactorCacheTempSend(), value, new OnWriteCharacterListener() {
            @Override
            public void onFail() {
                super.onFail();
                Logger.w("写缓存失败:", mGetCacheTempWriteValue);
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
                Logger.w("写缓存成功:", mGetCacheTempWriteValue);
            }
        });
    }


    /**
     * 体温校准
     *
     * @param tHex 校准值十六进制
     */
    public void calibrateTemp(String tHex,CalibrateCallBack calibrateCallBack) {
        this.calibrateCallBack=calibrateCallBack;
        byte[] value = BleUtils.hexStringToBytes(tHex);
        calibrateTemp(value);
    }

    public void calibrateTemp(String tHex) {
        byte[] value = BleUtils.hexStringToBytes(tHex);
        calibrateTemp(value);
    }

    /**
     * 向缓存温度特征值中写数据,用于体温贴校准
     */
    private void calibrateTemp(byte[] value) {
        mBleOperator.write(macaddress, deviceUUID.getServiceTemp(), deviceUUID.getCharactorCacheTempSend(), value, new OnWriteCharacterListener() {
            @Override
            public void onFail() {
                super.onFail();
                Logger.w("写入校准值失败");
                if (calibrateCallBack!=null) {
                    calibrateCallBack.calibrateFail();
                }
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
                Logger.w("写入校准值成功");
                if (calibrateCallBack!=null) {
                    calibrateCallBack.calibrateSuccess();
                }
            }
        });
    }

    /**
     * 解析温度
     */
    private void parseTemp(final byte[] data) {
        if (mReceiverDataListener == null) return;

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isV1_5) {
                    mReceiverDataListener.receiveCurrentTemp(dataParse.parseTempV1_5(data));
                } else {
                    mReceiverDataListener.receiveCurrentTemp(dataParse.parseTemp(data));
                }

                if (!isFirstCheckPatchEnable) {
                    mReceiverDataListener.judgeCarepatchEnable(judgeCarepatchEnableByBluetooth(data));
                }
                isFirstCheckPatchEnable = false;


            }
        });
    }

    /**
     * 判断体温贴是否断裂（蓝牙方式）
     *
     * @param tempData
     * @return
     */
    public static boolean judgeCarepatchEnableByBluetooth(byte[] tempData) {
        String temp = BleUtils.bytesToHexString(tempData);
        if (temp.equalsIgnoreCase("3fff")) {
            return false;
        }
        return true;
    }


    /**
     * 订阅温度通知
     * * @since   1.5 固件版本v1.5才支持订阅温度
     */
    public BleConnector subscribeTempNotification() {
        if (!isV1_5) return this;
        mBleOperator.subscribeNotification(macaddress, deviceUUID.getServiceTemp(), deviceUUID.getCharactorTemp(), new OnSubscribeListener() {
            @Override
            public void onNotify(String uuid, byte[] data) {
                parseTemp(data);
            }

            @Override
            public void onSuccess() {
                Logger.w("温度订阅成功");
            }

            @Override
            public void onFail() {
                Logger.w("温度订阅失败");
            }
        });
        return this;
    }

    /**
     * 订阅电量通知
     */
    public BleConnector subscribeBatteryNotification() {
        mBleOperator.subscribeNotification(macaddress, deviceUUID.getServiceTemp(), deviceUUID.getCharactorBatteryUUID(), new OnSubscribeListener() {
            @Override
            public void onNotify(String uuid, byte[] data) {
                parseBattery(data);
            }

            @Override
            public void onSuccess() {
                Logger.w("电量订阅成功");
            }

            @Override
            public void onFail() {
                Logger.w("电量订阅失败");
            }
        });
        return this;
    }

    /**
     * 订阅缓存温度通知
     */
    public BleConnector subscribeCacheTempNotification() {
        mBleOperator.subscribeNotification(macaddress, deviceUUID.getServiceTemp(), deviceUUID.getCharactorCacheTemp(), new OnSubscribeListener() {
            @Override
            public void onNotify(String uuid, byte[] data) {
                parseCacheTempNotification(data);
            }

            @Override
            public void onSuccess() {
                Logger.w("缓存订阅成功");
            }

            @Override
            public void onFail() {
                Logger.w("缓存订阅失败");
            }
        });
        return this;
    }

    /**
     * 取消订阅缓存温度通知
     */
    public BleConnector unSubscribeCacheTempNotification() {
        mBleOperator.unsubscribeNotification(macaddress, deviceUUID.getServiceTemp(), deviceUUID.getCharactorCacheTemp());
        return this;
    }

    /**
     * 解析缓存温度
     */
    private void parseCacheTempNotification(byte[] value) {
        if ("01".equals(mGetCacheTempWriteValue)) {
            //获取缓存的数量
            String countString = BleUtils.bytesToHexString(value).substring(0, 4);
            mCacheTempCount = Integer.parseInt(countString.substring(2, 4) + countString.substring(0, 2), 16);
            Logger.w("缓存温度大小:", mCacheTempCount);
            if (dataListener != null) {
                dataListener.receiveCacheTotal(mCacheTempCount);
            }
            if (mCacheTempCount > 0) {
                //有缓存温度，写02
                mGetCacheTempWriteValue = "02";
                writeCacheTemp(BleUtils.hexStringToBytes(mGetCacheTempWriteValue));
            } else {
                if (mReceiverDataListener != null) {
                    mReceiverDataListener.onReadCacheFinish();
                }
            }
        } else {
            //获取缓存的温度值
            parseCacheTemp(value);
            writeCacheTemp(BleUtils.hexStringToBytes(mGetCacheTempWriteValue));
        }
    }

    private void parseCacheTemp(byte[] value) {
        List<TempDataBean> caches = BleUtils.parseCacheTemp(value, isV1_5);
        mCacheTemps.addAll(caches);
        if (mCacheTemps.size() >= mCacheTempCount || caches.size() <= 0) {
            //取消订阅
            unSubscribeCacheTempNotification();
            if (mReceiverDataListener != null) {
                mReceiverDataListener.onReadCacheFinish();
            }

            if (dataListener != null) {
                dataListener.receiveCacheTemp(mCacheTemps);
            }
        }

        Logger.w("获取到了缓存温度:", mCacheTemps.size());
    }

    /**
     * x2和x1中间间隔n个点的处理方式
     * 一共有n+1个间隔
     * del = x2-x1;
     * a = del/n/(n+1);
     * k = del/2/(n+1);
     * x(i)=x(i-1)+k+(n-i+1)*a;;
     */
    private List<Float> fillCacheData(float first, float second, int fillCount) {
        float delta = second - first;
        float a = delta / fillCount / (fillCount + 1);
        float k = delta / 2 / (fillCount + 1);

        List<Float> tempList = new ArrayList<>();
        float temp = first;
        for (int i = 0; i < fillCount; i++) {
            temp = temp + k + (fillCount - i) * a;
            tempList.add(temp);
        }
        return tempList;
    }

    /**
     * 解析电量
     */
    private void parseBattery(final byte[] value) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mReceiverDataListener != null) {
                    Integer battery = dataParse.parseBattery(value);
                    boolean isCharge = dataParse.parseCharge(value);
                    Logger.w("电池:", battery, ",isCharge:", isCharge);
                    mReceiverDataListener.receiveBattery(battery);
                    mReceiverDataListener.receiveCharge(isCharge);
                }
            }
        });
    }

    /**
     * 获取硬件版本
     */
    public BleConnector getHardVersion() {
        mBleOperator.read(macaddress, deviceUUID.getDeviceInfoServiceUUID(), deviceUUID.getCharactorVersionUUID(), new OnReadCharacterListener() {
            @Override
            public void onSuccess(byte[] data) {
                String hardVersion = dataParse.parseHardVersion(data);
                if (mReceiverDataListener != null) {
                    mReceiverDataListener.receiveHardVersion(hardVersion);
                }
            }
        });
        return this;
    }

    /**
     * 获取电量
     */
    public BleConnector getBattery() {
        mBleOperator.read(macaddress, deviceUUID.getServiceTemp(), deviceUUID.getCharactorBatteryUUID(), new OnReadCharacterListener() {
            @Override
            public void onSuccess(byte[] data) {
                parseBattery(data);
            }

            @Override
            public void onFail() {
                super.onFail();
            }
        });
        return this;
    }

    /**
     * 获取硬件序列号
     */
    public BleConnector getSerial() {
        mBleOperator.read(macaddress, deviceUUID.getDeviceInfoServiceUUID(), deviceUUID.getCharactorSearialUUID(), new OnReadCharacterListener() {
            @Override
            public void onSuccess(byte[] data) {
                if (mReceiverDataListener != null) {
                    mReceiverDataListener.receiveSerial(dataParse.parseSerial(data));
                }
            }
        });
        return this;
    }

    /**
     * 断开连接
     */
    @Override
    public void disConnect() {
        clear(true);
        mBleOperator.disConnect(macaddress);

        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isConnected() && mBleOperator != null) {
                    Logger.w("蓝牙断开失败，开启定时器重复断开");
                    disconnectFailRes();
                } else {
                    Logger.w("成功断开蓝牙连接。。。");
                }
            }
        }, 2000);

    }

    @Override
    public boolean isConnected() {
        return mBleOperator.isConnected(macaddress);
    }

    @Override
    public void cancelConnect() {
        mBleOperator.cancelConnect(macaddress);
    }

    @Override
    public void setSampleRate(int sampleRate) {
        //采样频率变动
        Logger.w("当前采样:", mCurrentSampleRate, ",调整采样:"
                , sampleRate, ",mac = ", macaddress, ",", (isV1_5 ? "p03设备" : "p02设备"));
        if (sampleRate == mCurrentSampleRate || sampleRate <= 0) return;
        mCurrentSampleRate = sampleRate;
        if (isV1_5) {
            //p03设备通知设备采样
            adjustSample(mCurrentSampleRate);
        }
    }

    @Override
    public void setConnectTimeoutTime(long time) {
        mBleOperator.setConnectTimeoutTime(time);
    }

    @Override
    public void setDisconnectTimeoutTime(long time) {
    }

    /**
     * p03调整采样
     *
     * @param sampleRate 1秒或者四秒
     */
    private void adjustSample(final int sampleRate) {
        String data = sampleRate == 1 ? "03" : "04";
        writeCacheTemp(BleUtils.hexStringToBytes(data), new OnWriteCharacterListener() {
            @Override
            public void onSuccess() {
                Logger.w("调整采样成功:", sampleRate);
            }

            @Override
            public void onFail() {
                Logger.w("调整采样失败:", sampleRate);
            }
        });
    }

    /**
     * 启动定时获取温度
     */
    private void startGetTempTimer() {
        //1.5设备不需要主动获取温度
        if (isV1_5) return;
        //缓存读取结束，不管成功还是失败
        if (mGetTempTimer != null) {
            mGetTempTimer.cancel();
        }
        mGetTempTimer = new Timer();
        mGetTempTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //每隔4秒获取温度
                getCurrentTemp();
            }
        }, 0, 4000);
    }

    /**
     * 当断开蓝牙失败时候的操作
     */
    private void disconnectFailRes() {
        if (mCycDisconnectTimer == null) {
            mCycDisconnectTimer = new Timer();
        }
        mCycDisconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Logger.w("蓝牙断开失败，开启定时器重复断开，断开次数 ：", disconnectCount, "次");
                disconnectCount++;
                mBleOperator.disConnect(macaddress);
                if (disconnectCount > disconnectTotalCount) {
                    mCycDisconnectTimer.cancel();
                    mCycDisconnectTimer = null;
                    disconnectCount = 0;
                }
            }
        }, 2000, 2000);

    }

    /**
     * 清空信息
     */
    public void clear(boolean isClearListener) {
        if (mGetTempTimer != null) {
            mGetTempTimer.cancel();
        }
        if (mCycDisconnectTimer != null) {
            mCycDisconnectTimer.cancel();
            mCycDisconnectTimer = null;
        }
        disconnectCount = 0;

        mCacheTemps.clear();
        mCacheTempCount = 0;
        if (isClearListener) {
            connectStatusListener = null;
            mReceiverDataListener = null;
            bleManagerMap.remove(macaddress);
        }
    }

    public static void clearAllManager() {
        for (String mac : bleManagerMap.keySet()) {
            bleManagerMap.get(mac).clear(true);
        }
        bleManagerMap.clear();
    }

    /**
     * 停止搜索OnScanListener
     */
    public static void stopScan() {
        ScanManager.getInstance().stop(mScanCallback);
    }

    /**
     * 设置数据接受监听器，只能有一个监听器
     */
    public BleConnector setDataListener(OnReceiverBleDataListener mReceiverDataListener) {
        this.mReceiverDataListener = mReceiverDataListener;
        return this;
    }

    /**
     * 设置数据解析策略，可动态设置，如果是心电帖，直接传心电帖的解析方式
     */
    public BleConnector setDataParseStrategy(IBleDataParse dataParse) {
        if (dataParse == null) {
            throw new IllegalArgumentException("data parse startegy can not be null");
        }
        this.dataParse = dataParse;
        return this;
    }

    /**
     * 设置uuid加载策略，动态提供uuid
     */
    public BleConnector setDeviceUUIDStrategy(IDeviceUUID deviceUUID) {
        if (deviceUUID == null) {
            throw new IllegalArgumentException("device uuid startegy can not be null");
        }
        this.deviceUUID = deviceUUID;
        return this;
    }

    public static int getSize() {
        return bleManagerMap.size();
    }

    public static class MyScanCallback extends com.wms.ble.callback.OnScanListener {

        private OnScanListener listener;

        MyScanCallback(OnScanListener listener) {
            this.listener = listener;
        }

        @Override
        public void onDeviceFound(ScanResult result) {
            byte[] scanRecord = result.getScanRecord();
            DeviceType type = BroadcastUtils.parseDeviceType(scanRecord);
            if (type != DeviceType.None) {
                //解析mac地址，系统api拿到的mac地址和广播包的mac地址可能不一致(嵌入式问题)
                String mac;
                if (BroadcastUtils.isBroadcast(scanRecord)) {
                    mac = BroadcastUtils.getMacaddressByBroadcastNew(scanRecord);
                } else {
                    mac = BroadcastUtils.getMacaddressByBroadcastOld(scanRecord);
                }
                DeviceBean deviceBean = new DeviceBean(mac, type, BroadcastUtils.getHardVersionByBroadcast(result.getScanRecord()), result.getRssi());
                if (BroadcastUtils.isUpdateStatus(result)) {
                    deviceBean.setNeedUpdate(true);
                }
                //Logger.w("广播包:", BleUtils.bytesToHexString(scanRecord), ",name :", result.getDevice().getName());
                onDeviceFound(deviceBean);
            }
        }

        private void onDeviceFound(DeviceBean deviceBean) {
            if (listener != null) {
                listener.onDeviceFound(deviceBean);
            }
        }

        @Override
        public void onScanCanceled() {
            if (listener != null) {
                listener.onScanCanceled();
            }
        }

        @Override
        public void onScanStart() {
            if (listener != null) {
                listener.onScanStart();
            }
        }

        @Override
        public void onScanStopped() {
            if (listener != null) {
                listener.onScanStopped();
            }
        }
    }


    public interface CalibrateCallBack {
        void calibrateSuccess();

        void calibrateFail();
    }
}
