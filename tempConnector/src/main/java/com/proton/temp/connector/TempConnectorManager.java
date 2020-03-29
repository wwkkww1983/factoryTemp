package com.proton.temp.connector;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.proton.temp.algorithm.AlgorithmManager;
import com.proton.temp.algorithm.bean.AlgorithmData;
import com.proton.temp.algorithm.interfaces.AlgorithmListener;
import com.proton.temp.connector.bean.ConnectionType;
import com.proton.temp.connector.bean.DeviceBean;
import com.proton.temp.connector.bean.DeviceType;
import com.proton.temp.connector.bean.MQTTConfig;
import com.proton.temp.connector.bean.TempDataBean;
import com.proton.temp.connector.bluetooth.BleConnector;
import com.proton.temp.connector.bluetooth.utils.BleUtils;
import com.proton.temp.connector.broadcast.BroadcastConnector;
import com.proton.temp.connector.interfaces.AlgorithmStatusListener;
import com.proton.temp.connector.interfaces.ConnectStatusListener;
import com.proton.temp.connector.interfaces.ConnectionTypeListener;
import com.proton.temp.connector.interfaces.Connector;
import com.proton.temp.connector.interfaces.DataListener;
import com.proton.temp.connector.mqtt.MQTTConnector;
import com.proton.temp.connector.utils.Utils;
import com.wms.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by wangmengsi on 2018/3/15.
 * 体温连接管理器
 */
@SuppressLint("StaticFieldLeak")
public class TempConnectorManager {
    private static Context mContext;
    /**
     * 设备管理器
     */
    private static HashMap<String, TempConnectorManager> mTempManager = new HashMap<>();
    private static String NET_DEFAULT_PATCH_MAC = "NET_DEFAULT_PATCH_MAC";
    /**
     * 当前设备对应的贴mac地址
     */
    private String patchMacaddress;
    /**
     * 当前设备对应的底座mac地址
     */
    private String dockerMacaddress;
    /**
     * 连接器
     */
    private Connector mConnector;
    /**
     * 是否启用算法
     */
    private boolean enableAlgorithm = true;
    /**
     * 体温算法处理后数据
     */
    private List<TempDataBean> mAllTemps = new ArrayList<>();
    /**
     * 保存采样频率
     */
    private List<Integer> mSamples = new ArrayList<>();
    /**
     * 保存连接方式
     */
    private List<Integer> mConnectionType = new ArrayList<>();
    /**
     * 当前电量
     */
    private int mBattery;
    private long mCurrentTempTime;
    /**
     * 当前温度值
     */
    private TempDataBean mCurrentTemp;
    /**
     * 最高温度
     */
    private float mHighestTemp;
    /**
     * 最低温度
     */
    private float mLowestTemp;
    /**
     * 是否断线重连
     */
    private boolean isAutoReconnect;
    /**
     * 数据监听器
     */
    private List<DataListener> mDataListeners = new ArrayList<>();
    /**
     * 硬件版本号
     */
    private String mHardVersion = "";
    /**
     * 断开重连次数，默认无无限重连
     */
    private int mReconnectCount = Integer.MAX_VALUE;
    /**
     * 当前已经重连次数
     */
    private int mCurrentReconnectCount;
    /**
     * 断开连接时间
     */
    private long mDisconnectTime;

    private long firstDisConnectTime;

    /**
     * 总重连时间
     */
    private long totalReConnectTime;

    /**
     * 测量准备页面断开连接的标志
     */
    private boolean isFirst = true;

    private int connectStatus = 2;

    /**
     * 断开连接后重连获取温度第一个温度采样
     */
    private int mDisconnectFirstTempSample;
    private int mCacheCount;
    /**
     * 当前收到的温度个数
     */
    private int mCurrentTempSize;
    /**
     * 当前收到的温度第几个温度
     */
    private int mCurrentSize;
    /**
     * 当前收到的温度(经过算法处理)
     */
    private List<TempDataBean> mCurrentTempDataList = new ArrayList<>();
    private DeviceBean mDeviceBean = new DeviceBean();
    /**
     * 是否启用缓存温度
     */
    private boolean enableCacheTemp = true;
    /**
     * 连接方式监听器
     */
    private List<ConnectionTypeListener> connectTypeListener = new ArrayList<>();
    /**
     * 算法数据接收
     */
    private AlgorithmListener algorithmListener = new AlgorithmListener() {
        @Override
        public void onComplete(AlgorithmData data) {
            doAlgorithmCallback(data.getProcessTemp(), data.getSample(), data.getMeasureStatus(), data.getPercent(), data.getGesture());
        }
    };
    private BroadcastReceiver mNetReceiver = new BroadcastReceiver() {
        private boolean isFirstReceiveNetChange = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (Utils.isConnected(mContext)) {
                    Logger.w("网络连接成功");
                    if (isFirstReceiveNetChange) {
                        isFirstReceiveNetChange = false;
                        return;
                    }
                    connect();
                } else {
                    if (isMQTTConnect()) {
                        for (ConnectStatusListener listener : connectStatusListeners) {
                            listener.onConnectFaild();
                        }
                    }
                }
            }
        }
    };
    /**
     * 算法状态回调
     */
    private List<AlgorithmStatusListener> algorithmStatusListeners = new ArrayList<>();
    /**
     * 连接状态回调
     */
    private List<ConnectStatusListener> connectStatusListeners = new ArrayList<>();
    /**
     * 连接回调
     */
    private ConnectStatusListener connectStatusListener = new ConnectStatusListener() {

        @Override
        public void onConnectSuccess() {
            Logger.w("连接成功:", patchMacaddress);
            mCurrentReconnectCount = 0;
            for (ConnectStatusListener listener : connectStatusListeners) {
                listener.onConnectSuccess();
            }
            connectStatus = 2;
            isFirst = true;
        }

        @Override
        public void onConnectFaild() {
            connectStatus = 1;
            reconnect();
            Logger.w("onConnectFaild....");
        }

        @Override
        public void onDisconnect(boolean isManual) {
            mDisconnectTime = System.currentTimeMillis();
            Logger.w("onDisconnect....");
            connectStatus = 1;
            if (getConnectionType().equals(ConnectionType.NET)) {
                List<ConnectStatusListener> listeners = new ArrayList<>(connectStatusListeners);
                for (ConnectStatusListener listener : listeners) {
                    listener.onDisconnect(false);
                }
            } else {
                reconnect();
            }
        }

        @Override
        public void receiveNotSampleDevice(String oldMac, String newMac) {
            for (ConnectStatusListener listener : connectStatusListeners) {
                listener.receiveNotSampleDevice(oldMac, newMac);
            }
        }

        @Override
        public void receiveDockerOffline(boolean isOffline) {
            for (ConnectStatusListener listener : connectStatusListeners) {
                listener.receiveDockerOffline(isOffline);
            }
            Logger.w("底座是否下线:", isOffline);
        }
    };
    /**
     * 数据回调
     */
    private DataListener mDataListener = new DataListener() {

        @Override
        public void receiveCurrentTemp(TempDataBean currentTemp) {
            super.receiveCurrentTemp(currentTemp);
            //只有p02固件回调
            mCurrentTempSize = 1;
            doAlgorithm(currentTemp);
        }

        @Override
        public void receiveCurrentTemp(List<TempDataBean> temps) {
            super.receiveCurrentTemp(temps);
            temps = BleUtils.getTempTime(temps);
            if (temps != null && temps.size() > 0) {
                mCurrentTempSize = temps.size();
                //缓存补数据开始
                if (mDisconnectTime != 0) {
                    long time = temps.get(0).getTime() - mCacheCount * 24 * 1000 - mDisconnectTime;
                    if (time > 0) {
                        mDisconnectFirstTempSample = (int) (time / 1000);
                    }
                    if (mDisconnectFirstTempSample > 0) {
                        temps.get(0).setSample(mDisconnectFirstTempSample);
                    }
                    mCacheCount = 0;
                    mDisconnectTime = 0;
                    mDisconnectFirstTempSample = 0;
                }
                //缓存补数据结束
                //p03固件回调当前温度，如果是wifi连接则不用跑算法
                if (isMQTTConnect()) {
                    //wifi连接，已经包含实际温度和原始温度
                    doNetworkData(temps);
                } else {
                    //蓝牙连接
                    doAlgorithm(temps);
                }
            }
        }

        @Override
        public void receiveBattery(Integer battery) {
            if (battery > 100 || battery < 0) {
                battery = 100;
            }
            for (DataListener connectorListener : mDataListeners) {
                connectorListener.receiveBattery(battery);
            }
            mBattery = battery;
            Logger.w("电量:", battery, ",mac = ", patchMacaddress);
        }

        @Override
        public void receiveCharge(boolean isCharge) {
            Logger.w("充电:", isCharge, ",mac = ", patchMacaddress);
            for (DataListener connectorListener : mDataListeners) {
                connectorListener.receiveCharge(isCharge);
            }
        }

        @Override
        public void receiveSerial(String serial) {
            for (DataListener connectorListener : mDataListeners) {
                connectorListener.receiveSerial(serial);
            }
        }

        @Override
        public void receiveHardVersion(String hardVersion) {
            for (DataListener connectorListener : mDataListeners) {
                connectorListener.receiveHardVersion(hardVersion);
            }

            mHardVersion = hardVersion;
            Logger.w("硬件版本:", mHardVersion, ",mac = ", patchMacaddress);
        }

        @Override
        public void receiveCacheTotal(Integer cacheCount) {
            mCacheCount = cacheCount;
            for (DataListener connectorListener : mDataListeners) {
                connectorListener.receiveCacheTotal(cacheCount);
            }
        }

        @Override
        public void receiveCacheTemp(List<TempDataBean> cacheTemps) {
            if (!enableCacheTemp) {
                return;
            }
            cacheTemps = BleUtils.getTempTime(cacheTemps);
            if (cacheTemps == null || cacheTemps.size() <= 0) return;
            mCurrentTempSize = cacheTemps.size();
            if (isMQTTConnect()) {
                doNetworkData(cacheTemps);
            } else {
                doAlgorithm(cacheTemps);
                for (DataListener connectorListener : mDataListeners) {
                    connectorListener.receiveCacheTemp(cacheTemps);
                }
            }
        }

        @Override
        public void judgeCarepatchEnable(boolean isEnable) {
            Logger.w("体温贴是否可用:", isEnable, ",mac = ", patchMacaddress);
            for (DataListener connectorListener : mDataListeners) {
                connectorListener.judgeCarepatchEnable(isEnable);
            }
        }

        @Override
        public void receiveBleAndWifiRssi(Integer bleRssi, Integer wifiRssi) {
            for (DataListener connectorListener : mDataListeners) {
                connectorListener.receiveBleAndWifiRssi(bleRssi, wifiRssi);
            }
        }
    };

    private TempConnectorManager(DeviceBean deviceBean) {
        if (deviceBean == null) return;
        this.patchMacaddress = deviceBean.getMacaddress();
        this.dockerMacaddress = deviceBean.getDockerMacaddress();
        this.mDeviceBean = deviceBean;
        setConnectionType(deviceBean.getConnectionType());
    }

    /**
     * 设置连接方式，必须在调用connect方法之前调用
     */
    public TempConnectorManager setConnectionType(ConnectionType connectionType) {
        if (mDeviceBean.getDeviceType() == DeviceType.P02) {
            mConnector = BleConnector.getInstance(patchMacaddress, false);
            doConnectionTypeCallback();
            return this;
        }

        if (mConnector != null) {
            mConnector.disConnect();
        }

        if (connectionType != ConnectionType.NET && (TextUtils.isEmpty(patchMacaddress) || NET_DEFAULT_PATCH_MAC.equals(patchMacaddress))) {
            //如果是从网络切换到蓝牙或者广播
            throw new IllegalArgumentException("you should set patchMacaddress before you switch to net connect");
        }
        switch (connectionType) {
            case BROADCAST:
                mConnector = new BroadcastConnector(patchMacaddress);
                break;
            case BLUETOOTH:
                mConnector = BleConnector.getInstance(patchMacaddress, true);
                break;
            case NET:
                if (TextUtils.isEmpty(dockerMacaddress)) {
                    throw new IllegalArgumentException("you should set dockerMacaddress before you switch to net connect");
                }
                mContext.registerReceiver(mNetReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                mConnector = MQTTConnector.getInstance(dockerMacaddress);
                break;
        }
        doConnectionTypeCallback();
        Logger.w("连接方式:", getConnectionType(), ",mac:", patchMacaddress);
        return this;
    }

    /**
     * 切换连接方式蓝牙->mqtt
     * mqtt->蓝牙切换
     */
    public TempConnectorManager switchConnectionType(ConnectionType connectionType) {
        if (getConnectionType() == connectionType && getConnectionType() != ConnectionType.NET) {
            //网络连接方式的时候可以切换底座mac地址
            doConnectionTypeCallback();
            return this;
        }

        if (mDeviceBean.getDeviceType() == DeviceType.P02) {
            throw new IllegalArgumentException("you can not switch connection type on p02 devices,because it does not support!");
        }
        if (connectionType == ConnectionType.NET && TextUtils.isEmpty(dockerMacaddress)) {
            throw new IllegalArgumentException("you should set dockerMacaddress before you switch to net connect");
        }
        setConnectionType(connectionType);
        mConnector.connect(connectStatusListener, mDataListener);
        return this;
    }

    private void doConnectionTypeCallback() {
        for (ConnectionTypeListener listener : connectTypeListener) {
            listener.receiveConnectType(getConnectionType());
        }
    }

    /**
     * 设置贴的mac地址
     */
    public TempConnectorManager setPatchMacaddress(String patchMacaddress) {
        this.patchMacaddress = patchMacaddress;
        return this;
    }

    /**
     * 设置底座的mac地址
     */
    public TempConnectorManager setDockerMacaddress(String dockerMac) {
        this.dockerMacaddress = dockerMac;
        return this;
    }

    /**
     * 获取底座的mac地址
     */
    public String getDockerMacaddress() {
        return dockerMacaddress;
    }

    /**
     * 设置是否启用缓存温度，该方法必须在连接成功之前调用
     */
    public TempConnectorManager setEnableCacheTemp(boolean enableCacheTemp) {
        this.enableCacheTemp = enableCacheTemp;
        return this;
    }

    /**
     * 网络连接专用
     *
     * @param dockerMac 充电器的mac地址
     */
    public static TempConnectorManager getNetConnectInstance(String dockerMac) {
        DeviceBean deviceBean = new DeviceBean(NET_DEFAULT_PATCH_MAC, dockerMac);
        deviceBean.setConnectionType(ConnectionType.NET);
        return getInstance(deviceBean);
    }

    /**
     * @param context application context
     */
    public static void init(Context context) {
        init(context, null);
    }

    public static void init(Context context, MQTTConfig mqttConfig) {
        mContext = context;
        //初始化日志
        Logger.newBuilder()
                .tag("temp_connector")
                .showThreadInfo(false)
                .methodCount(1)
                .context(mContext)
                .saveFile(BuildConfig.DEBUG)
                .isDebug(BuildConfig.DEBUG)
                .build();
        BleConnector.init(context);
        setMQTTConfig(mqttConfig);
    }

    public static TempConnectorManager getInstance(DeviceBean deviceBean) {
        if (mContext == null) {
            throw new IllegalStateException("You should initialize TempConnectorManager before using,You can initialize in your Application class");
        }
        if (deviceBean == null || TextUtils.isEmpty(deviceBean.getMacaddress())) {
            throw new IllegalStateException("device is null");
        }
        if (!mTempManager.containsKey(deviceBean.getMacaddress())) {
            mTempManager.put(deviceBean.getMacaddress(), new TempConnectorManager(deviceBean));
        }
        return mTempManager.get(deviceBean.getMacaddress());
    }

    public static TempConnectorManager getInstance(String macaddress) {
        return getInstance(new DeviceBean(macaddress));
    }

    /**
     * 算法处理数据回调
     *
     * @param processTemp   算法处理温度
     * @param sample        采样
     * @param measureStatus 测量状态：0取下,1贴上未夹紧,2首次升温（1min倒计时开始),3稳定(倒计时结束),4降到异常温(弹窗,然后2min倒计时)
     */
    private void doAlgorithmCallback(float processTemp, int sample, int measureStatus, int percent, int gesture) {
        try {
            if (mCurrentTemp == null) return;
            mCurrentSize++;
            mCurrentTemp.setAlgorithmTemp(processTemp);
            mCurrentTemp.setMeasureStatus(measureStatus);
            mCurrentTemp.setGesture(gesture);
            mCurrentTemp.setPercent(percent);
            mCurrentTemp.setAlgorithmTemp(processTemp);
            mCurrentTemp.setAlgorithmVerType(getConnectionType() == ConnectionType.NET ? 1 : 0);
            mAllTemps.add(mCurrentTemp);

            //在这里接入sample和type，smaple和type数据和其他数据数组大小不一致

            Logger.w("当前算法温度:", mCurrentTemp.getAlgorithmTemp(), ",连接方式:", getConnectionType(), ",测量状态:", mCurrentTemp.getMeasureStatus(), ",mac:", patchMacaddress);
            mHighestTemp = Math.max(processTemp, mHighestTemp);
            mLowestTemp = Math.min(processTemp, mLowestTemp);

            mCurrentTempDataList.add(mCurrentTemp);
            boolean hasReceiveAllTemp = mCurrentSize >= mCurrentTempSize;
            for (DataListener connectorListener : mDataListeners) {
                connectorListener.receiveCurrentTemp(processTemp);
                connectorListener.receiveCurrentTemp(processTemp, mCurrentTempTime);
                if (hasReceiveAllTemp) {
                    connectorListener.receiveCurrentTemp(new ArrayList<>(mCurrentTempDataList));
                }
            }
            if (hasReceiveAllTemp) {
                mCurrentTempDataList.clear();
                mCurrentSize = 0;
            }

            //设置采样
            mConnector.setSampleRate(sample);

            if (algorithmStatusListeners == null) return;

            for (AlgorithmStatusListener listener : algorithmStatusListeners) {
                listener.receiveMeasureStatus(measureStatus);
                listener.receiveAlgorithmVersionType(getConnectionType() == ConnectionType.NET ? 1 : 0);
                listener.receiveGesture(gesture);
                listener.receiveMeasureStatusAndGesture(measureStatus, gesture);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * connect device by patchMacaddress
     */
    public void connect() {
        connect(null);
    }

    public void connect(ConnectStatusListener listener) {
        connect(listener, null);
    }

    public void connect(ConnectStatusListener listener, DataListener dataListener) {
        connect(listener, dataListener, isAutoReconnect);
    }

    /**
     * connect device and auto connect if disconnect
     */
    public void connect(ConnectStatusListener statusListener, DataListener dataListener, final boolean isAutoReconnect) {
        checkConnector();
        this.isAutoReconnect = isAutoReconnect;
        addConnectStatusListener(statusListener);
        addDataListener(dataListener);
        if (mConnector.isConnected()) {
            Logger.w("已经连接了:", patchMacaddress, ",监听器数量:", mDataListeners.size());
            if (statusListener != null) {
                statusListener.onConnectSuccess();
            } else {
                //重连
                for (ConnectStatusListener listener : connectStatusListeners) {
                    listener.onConnectSuccess();
                }
                return;
            }
        }
        mConnector.connect(connectStatusListener, mDataListener);
    }

    /**t
     * 重新连接设备
     */
    private void reconnect() {

        final List<ConnectStatusListener> listeners = new ArrayList<>(connectStatusListeners);

        if (isFirst) {
/*            new Handler(Looper.getMainLooper())
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (connectStatus == 1) {
                                for (ConnectStatusListener listener : listeners) {
                                    listener.showBeforeMeasureDisconnect();
                                }
                            }
                        }
                    }, 6000);*/

            if (connectStatus == 1) {
                for (ConnectStatusListener listener : listeners) {
                    listener.showBeforeMeasureDisconnect();
                }
            }
            isFirst = false;
        }


        for (ConnectStatusListener listener : listeners) {
            listener.onConnectFaild();
        }
        if (!isAutoReconnect) {
            for (ConnectStatusListener listener : listeners) {
                listener.onDisconnect(false);
            }
            return;
        }
        if (mCurrentReconnectCount >= mReconnectCount) {
            //不重新连接了
            Logger.w("重连失败,尝试次数:", mCurrentReconnectCount);
            for (ConnectStatusListener listener : listeners) {
                listener.onDisconnect(false);
            }
            disConnect();
            return;
        }

        if (mCurrentReconnectCount == 0) {
            firstDisConnectTime = System.currentTimeMillis();
        }

        if (firstDisConnectTime > 0) {
            totalReConnectTime = System.currentTimeMillis() - firstDisConnectTime;
        }

        mCurrentReconnectCount++;

        Logger.w("重连:", patchMacaddress, ",剩余重连次数:", (mReconnectCount - mCurrentReconnectCount));
        for (ConnectStatusListener listener : listeners) {
            listener.receiveReconnectTimes(mCurrentReconnectCount, mReconnectCount - mCurrentReconnectCount, totalReConnectTime);
        }
        connect();
    }

    /**
     * 网络连接传输的数据
     */
    private void doNetworkData(List<TempDataBean> temps) {
        for (TempDataBean temp : temps) {
            //获取到原温度保存起来
            for (DataListener connectorListener : mDataListeners) {
                connectorListener.receiveRawTemp(temp.getTemp());
            }
            mCurrentTemp = temp;
            doAlgorithmCallback(temp.getAlgorithmTemp(), temp.getSample(), temp.getMeasureStatus(), temp.getPercent(), temp.getGesture());
            //这里需要做判断，与doAlgorithmCallback里面的处理同步，避免数据异常
            if (mCurrentTemp != null) {
                mSamples.add(temp.getPackageNumber());
                mConnectionType.add(getConnectionType().ordinal());
            }
        }
    }

    /**
     * 是否已经连接
     */
    public boolean isConnected() {
        return mConnector.isConnected();
    }

    public Connector getmConnector() {
        return mConnector;
    }

    public void disConnect() {
        Logger.w("手动断开连接:", patchMacaddress);
        checkConnector();
        mCurrentTempDataList.clear();
        mSamples.clear();
        mConnectionType.clear();
        //取消重连
        mCurrentTemp = new TempDataBean();
        mAllTemps.clear();
        isAutoReconnect = false;
        for (ConnectStatusListener listener : connectStatusListeners) {
            listener.onDisconnect(true);
        }
        connectStatusListeners.clear();
        mDataListeners.clear();
        algorithmStatusListeners.clear();
        mConnector.disConnect();
        if (!isMQTTConnect()) {
            getAlgorithmManager().closeAlgorithm();
        }
        mTempManager.remove(patchMacaddress);
        mContext.registerReceiver(mNetReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mContext.unregisterReceiver(mNetReceiver);
    }

    public TempConnectorManager addConnectionTypeListener(ConnectionTypeListener listener) {
        if (listener == null) return this;
        this.connectTypeListener.add(listener);
        doConnectionTypeCallback();
        return this;
    }

    public TempConnectorManager removeConnectionTypeListener(ConnectionTypeListener listener) {
        connectTypeListener.remove(listener);
        return this;
    }

    public TempConnectorManager removeAllConnectionTypeListener() {
        connectTypeListener.clear();
        return this;
    }

    public TempConnectorManager addDataListener(DataListener dataListener) {
        if (dataListener != null && !mDataListeners.contains(dataListener)) {
            mDataListeners.add(dataListener);
        }
        return this;
    }

    public TempConnectorManager removeDataListener(DataListener listener) {
        mDataListeners.remove(listener);
        return this;
    }

    public TempConnectorManager addConnectStatusListener(ConnectStatusListener statusListener) {
        if (statusListener != null && !connectStatusListeners.contains(statusListener)) {
            connectStatusListeners.add(statusListener);
        }
        return this;
    }

    public TempConnectorManager removeConnectStatusListener(ConnectStatusListener statusListener) {
        connectStatusListeners.remove(statusListener);
        return this;
    }

    public TempConnectorManager addAlgorithmStatusListener(AlgorithmStatusListener algorithmListener) {
        if (algorithmListener != null && !algorithmStatusListeners.contains(algorithmListener)) {
            algorithmStatusListeners.add(algorithmListener);
        }
        return this;
    }

    public TempConnectorManager removeAlgorithmStatusListener(AlgorithmStatusListener listener) {
        algorithmStatusListeners.remove(listener);
        return this;
    }

    public TempConnectorManager removeAllDataListener() {
        mDataListeners.clear();
        return this;
    }

    public TempConnectorManager removeAllConnectStatusListener() {
        connectStatusListeners.clear();
        return this;
    }

    public TempConnectorManager removeAllAlgorithmStatusListener() {
        algorithmStatusListeners.clear();
        return this;
    }

    public TempConnectorManager removeListener(ConnectStatusListener connectorListener, DataListener dataListener, AlgorithmStatusListener algorithmStatusListener) {
        removeConnectStatusListener(connectorListener);
        removeDataListener(dataListener);
        removeAlgorithmStatusListener(algorithmStatusListener);
        return this;
    }

    /**
     * 设置是否启用算法
     */
    public TempConnectorManager setAlgorithEnable(boolean enable) {
        this.enableAlgorithm = enable;
        return this;
    }

    /**
     * 算法处理
     */
    private void doAlgorithm(TempDataBean currentTemp) {
        mCurrentTemp = currentTemp;
        for (DataListener connectorListener : mDataListeners) {
            connectorListener.receiveRawTemp(currentTemp.getTemp());
        }

        if (mCurrentTemp!=null) {
            if (getConnectionType() == ConnectionType.BLUETOOTH) {
                mSamples.add(currentTemp.getSample());
            } else {
                mSamples.add(currentTemp.getPackageNumber());
            }
            mConnectionType.add(getConnectionType().ordinal());
        }

        mCurrentTempTime = currentTemp.getTime();
        if (enableAlgorithm) {
            chooseAlgorithm(currentTemp);
        } else {
            //没有启用算法
            mHighestTemp = Math.max(mCurrentTemp.getTemp(), mHighestTemp);
            mLowestTemp = Math.min(mCurrentTemp.getTemp(), mLowestTemp);
            for (DataListener connectorListener : mDataListeners) {
                connectorListener.receiveCurrentTemp(mCurrentTemp.getTemp());
            }
            mAllTemps.add(mCurrentTemp);
        }
    }

    /**
     * 算法处理
     */
    private void doAlgorithm(List<TempDataBean> temps) {
        for (TempDataBean tempDataBean : temps) {
            doAlgorithm(tempDataBean);
        }
    }

    /**
     * 根据设备版本使用不同算法
     */
    private void chooseAlgorithm(TempDataBean currentTemp) {
        if (mDeviceBean.getDeviceType() == DeviceType.P02) {
            getAlgorithmManager().doAlgorithm1_0(currentTemp.getTemp());
        } else {
            if (getConnectionType() == ConnectionType.BLUETOOTH) {
                getAlgorithmManager().doAlgorithm1_5(currentTemp.getTemp(), currentTemp.getTime(), currentTemp.getSample(), getConnectionType().ordinal());
            } else {
                getAlgorithmManager().doAlgorithm1_5(currentTemp.getTemp(), currentTemp.getTime(), currentTemp.getPackageNumber(), getConnectionType().ordinal());
            }
        }
    }

    private AlgorithmManager getAlgorithmManager() {
        if (AlgorithmManager.getInstance(patchMacaddress).getAlgorithmListener() == null) {
            AlgorithmManager.getInstance(patchMacaddress).setAlgorithmListener(algorithmListener);
        }
        return AlgorithmManager.getInstance(patchMacaddress);
    }

    private void checkConnector() {
        if (mConnector == null) {
            throw new IllegalArgumentException("you must have a connector before you use TempConnectorManager");
        }
    }

    /**
     * 设置重连次数
     */
    public TempConnectorManager setReconnectCount(int mReconnectCount) {
        this.mReconnectCount = mReconnectCount;
        return this;
    }

    public TempConnectorManager setConnectTimeoutTime(long time) {
        checkConnector();
        mConnector.setConnectTimeoutTime(time);
        return this;
    }

    public TempConnectorManager setDisconnectTimeoutTime(long time) {
        checkConnector();
        mConnector.setDisconnectTimeoutTime(time);
        return this;
    }

    public int getBattery() {
        return mBattery;
    }

    /**
     * 获取所有的体温处理后数据
     */
    public List<TempDataBean> getAllTemps() {
        return new ArrayList<>(mAllTemps);
    }

    /**
     * 获取所有的采样频率
     */
    public List<Integer> getAllSample() {
        return mSamples;
    }

    /**
     * 获取所有的连接方式
     */
    public List<Integer> getAllConnectionType() {
        return mConnectionType;
    }

    public String getHardVersion() {
        return mHardVersion;
    }

    public float getCurrentTemp() {
        if (mCurrentTemp == null) return 0;
        return mCurrentTemp.getTemp();
    }

    public ConnectionType getConnectionType() {
        if (mConnector instanceof BleConnector) {
            return ConnectionType.BLUETOOTH;
        } else if (mConnector instanceof BroadcastConnector) {
            return ConnectionType.BROADCAST;
        } else {
            return ConnectionType.NET;
        }
    }

    /**
     * 是否是蓝牙连接
     */
    public boolean isBluetoothConnect() {
        return mConnector instanceof BleConnector;
    }

    /**
     * 是否是广播方式
     */
    public boolean isBroadcastConnect() {
        return mConnector instanceof BroadcastConnector;
    }

    /**
     * 是否是mqtt连接
     */
    public boolean isMQTTConnect() {
        return mConnector instanceof MQTTConnector;
    }

    /**
     * 取消连接TempConnectorManager
     */
    public void cancelConnect() {
        mConnector.cancelConnect();
        disConnect();
    }


    /**
     * 是否有设备连接
     */
    public static boolean hasConnectDevice() {
        return mTempManager != null && mTempManager.size() > 0;
    }

    /**
     * 是否有蓝牙设备连接
     */
    public static boolean hasBluetoothConnectDevice() {
        if (mTempManager != null && mTempManager.size() > 0) {
            for (String mac : mTempManager.keySet()) {
                if (mTempManager.get(mac).isBluetoothConnect()) return true;
            }
        }
        return false;
    }

    /**
     * 是否有广播设备
     */
    public static boolean hasBroadcastConnectDevice() {
        if (mTempManager != null && mTempManager.size() > 0) {
            for (String mac : mTempManager.keySet()) {
                if (mTempManager.get(mac).isBroadcastConnect()) return true;
            }
        }
        return false;
    }

    /**
     * 是否有wifi设备连接
     */
    public static boolean hasNetConnectDevice() {
        if (mTempManager != null && mTempManager.size() > 0) {
            for (String mac : mTempManager.keySet()) {
                if (mTempManager.get(mac).isMQTTConnect()) return true;
            }
        }
        return false;
    }

    /**
     * 断开所有连接
     */
    public static void close() {
        Map<String, TempConnectorManager> managerMap = new HashMap<>(mTempManager);
        for (String mac : managerMap.keySet()) {
            Objects.requireNonNull(mTempManager.get(mac)).disConnect();
        }
        managerMap.clear();
    }

    /**
     * 获取最高温
     */
    public float getHighestTemp() {
        return mHighestTemp;
    }

    /**
     * 获取最低温
     */
    public float getLowestTemp() {
        return mLowestTemp;
    }

    public static void setMQTTConfig(MQTTConfig config) {
        MQTTConnector.init(mContext, config);
    }


    /**
     * 获取本地算法版本号
     *
     * @return
     */
    public String getLocalAlgorithmVersion() {
        return getAlgorithmManager().getVersion();
    }
}
