package com.proton.temp.connector.mqtt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.proton.temp.connector.bean.ConnectorListener;
import com.proton.temp.connector.bean.DockerDataBean;
import com.proton.temp.connector.bean.MQTTConfig;
import com.proton.temp.connector.interfaces.ConnectStatusListener;
import com.proton.temp.connector.interfaces.DataListener;
import com.proton.temp.connector.utils.ConnectorSetting;
import com.proton.temp.connector.utils.Utils;
import com.wms.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangmengsi on 2018/3/15.
 */
@SuppressLint("StaticFieldLeak")
public class MQTTManager {
    /**
     * 连接超时时间
     */
    private long connectTimeOut = ConnectorSetting.MQTT_CONNECT_TIME_OUT;
    /**
     * 数据接收超时时间(15分钟)，此时断开连接
     */
    private long disconnectTimeOut = ConnectorSetting.MQTT_DISCONNECT_TIME_OUT;
    /**
     * 数据接收超时时间(30s)，此时断开连接
     */
    private static final long DISCONNECT_TIME_OUT_NOT_DISCONNECT = ConnectorSetting.DISCONNECT_TIME_OUT_NOT_DISCONNECT;
    /**
     * 客户端id
     */
    private static final String CLIENTID = "android:" + Build.BRAND + System.currentTimeMillis() * Math.random();
    private static MQTTManager mInstance;
    private static Context mContext;
    private static MQTTConfig mMQTTConfig;
    /**
     * 缓存的主题，防止mqtt服务没连接成功，调用订阅导致订阅失败
     */
    private Map<String, ConnectorListener> mCacheTopics = new HashMap<>();
    private MqttAndroidClient mMQTTClient;
    /**
     * 连接器回调
     */
    private Map<String, ConnectorListener> mConnectListeners = new HashMap<>();
    /**
     * 订阅的主题的mac地址
     */
    private List<String> hasSubscribe = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    /**
     * 连接器监测计时器
     */
    private Timer mTimer;
    /**
     * 上一次收到数据时间
     */
    private Map<String, Long> mLastReceiveDataTime = new HashMap<>();
    /**
     * 防止设备底座连上了其他贴，然后回传数据
     */
    private Map<String, String> mCurrentConnectMacaddress = new HashMap<>();
    private long mLastTimerTime;
    private Timer mNetTimer;
    /**
     * 数据接收回调
     */
    private MqttCallback mMQTTDataCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            //断线了，重连
            Logger.w("mqtt服务掉线了:", cause != null ? cause.getMessage() : "");
            reConnect();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            String mac = Utils.getMacAddressByTopic(topic);
            String data = new String(message.getPayload());
            Logger.w("收到数据:", data, ",mac = ", mac);
            if (TextUtils.isEmpty(data)) return;
            ConnectorListener connectorListener = mConnectListeners.get(mac);
            if (connectorListener == null) return;
            if (data.contains("online")) {
                //底座电源被拔，或者和贴断开连接
                connectorListener.getConnectStatusListener().receiveDockerOffline(!Utils.getJSONBoolean(data, "online"));
                return;
            }
            DockerDataBean dockerDataBean;
            try {
                dockerDataBean = Utils.parseDockerData(data);
                if (dockerDataBean == null) return;
                //当前连接的设备mac地址
                String currentConnectMac = mCurrentConnectMacaddress.get(mac);
                if (!TextUtils.isEmpty(currentConnectMac) && !currentConnectMac.equals(dockerDataBean.getMacaddress())) {
                    connectorListener.getConnectStatusListener().receiveNotSampleDevice(currentConnectMac, dockerDataBean.getMacaddress());
                    return;
                }
                mCurrentConnectMacaddress.put(mac, dockerDataBean.getMacaddress());
                //记录当前收到数据的时间
                mLastReceiveDataTime.put(mac, System.currentTimeMillis());
                if (!TextUtils.isEmpty(data)) {
                    connectorListener.getDataListener().receiveSourceData(data);
                }
                connectorListener.getConnectStatusListener().receiveDockerOffline(false);
                Utils.parseData(mac, dockerDataBean, mConnectListeners);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    private IMqttActionListener mMqttConnectCallback = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            dealWithCacheTopic();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Logger.w("mqtt连接失败:", exception, ",缓存topic:", mCacheTopics.size());

            if (exception instanceof MqttException && ((MqttException) exception).getReasonCode() == 32100) {
                //已经连接直接订阅
                dealWithCacheTopic();
            } else {
                reConnect();
            }
        }
    };

    static {
        System.loadLibrary("tempconnector");
        initDefaultMQTTConfig();
    }

    /**
     * @param context application context
     */
    public static void init(Context context, MQTTConfig mqttConfig) {
        mContext = context;
        if (mqttConfig != null) {
            mMQTTConfig = mqttConfig;
        }
    }

    private MQTTManager() {
        initNetTimer();
    }

    public static MQTTManager getInstance() {
        if (mContext == null) {
            throw new IllegalStateException("You should initialize MQTTConnector before using,You can initialize in your Application class");
        }
        if (mInstance == null) {
            mInstance = new MQTTManager();
        }
        return mInstance;
    }

    /**
     * 连接MQTT服务器
     */
    private void connectMQTTServer() {
        Logger.w("clientId:", CLIENTID);
        if (mMQTTConfig == null || TextUtils.isEmpty(mMQTTConfig.getServerUrl())) {
            throw new IllegalArgumentException("mqtt server can not be null");
        }
        if (mMQTTClient == null) {
            // 服务器地址（协议+地址+端口号）
            mMQTTClient = new MqttAndroidClient(mContext, mMQTTConfig.getServerUrl(), CLIENTID);
            // 设置MQTT监听并且接受消息
            mMQTTClient.setCallback(mMQTTDataCallback);
        }

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        // 用户名
        mqttConnectOptions.setUserName(mMQTTConfig.getUsername());
        // 密码
        mqttConnectOptions.setPassword(mMQTTConfig.getPassword().toCharArray());

        if (mMQTTClient.isConnected()) {
            Logger.w("mqtt已经连接");
            return;
        }
        try {
            Logger.w("mqtt准备连接");
            mMQTTClient.connect(mqttConnectOptions, null, mMqttConnectCallback);
        } catch (Exception e) {
            Logger.w("连接异常:", e.getMessage());
            reConnect();
        }
    }

    private void dealWithCacheTopic() {
        if (mCacheTopics.size() <= 0 || mMQTTClient == null || !mMQTTClient.isConnected()) return;
        for (String mac : mCacheTopics.keySet()) {
            connect(mac, mCacheTopics.get(mac).getConnectStatusListener(), mCacheTopics.get(mac).getDataListener());
        }
        mCacheTopics.clear();
    }

    public void connect(String macaddress) {
        connect(macaddress, null, null);
    }

    public void connect(String macaddress, ConnectStatusListener connectStatusListener, DataListener dataListener) {
        try {
            //记录连接时间
            if (!macaddress.contains(":")) {
                macaddress = Utils.parseBssid2Mac(macaddress);
            }
            macaddress = macaddress.toUpperCase();
            Logger.w("准备订阅:", macaddress);
            if (isConnected(macaddress)) {
                Logger.w("mqtt已经连接了:", macaddress);
                return;
            }
            //判断mqtt是否连接
            if (mMQTTClient == null || !mMQTTClient.isConnected()) {
                //没有连接
                mCacheTopics.put(macaddress, new ConnectorListener(connectStatusListener, dataListener));
                Logger.w("mqtt没有连接,缓存topic:", mCacheTopics.size());
                connectMQTTServer();
                return;
            }
            String topic = Utils.getTopicByMacAddress(macaddress);
            mMQTTClient.subscribe(topic, 0);
            //订阅状态
            mMQTTClient.subscribe(Utils.getWillTopicByMacAddress(macaddress), 0);
            mMQTTClient.subscribe(Utils.getPatchDisconnectTopicByMacAddress(macaddress), 0);
            Logger.w("mqtt订阅成功:", topic);
            //存储订阅的主题
            hasSubscribe.add(macaddress);
            //存储连接器
            if (connectStatusListener != null) {
                mConnectListeners.put(macaddress, new ConnectorListener(connectStatusListener, dataListener));
                connectStatusListener.onConnectSuccess();
            }

            if (mLastReceiveDataTime == null || mLastReceiveDataTime.get(macaddress) == null || mLastReceiveDataTime.get(macaddress) <= 0) {
                mLastReceiveDataTime.put(macaddress, System.currentTimeMillis());
            }

            //开始计时器
            initTimer();
        } catch (MqttException e) {
            Logger.w("mqtt订阅失败:", e.getMessage());
            if (connectStatusListener != null) {
                connectStatusListener.onConnectFaild();
            }
        }
    }

    private void reConnect() {
        Map<String, ConnectorListener> listenerMap;
        if (mConnectListeners != null && mConnectListeners.size() > 0) {
            listenerMap = new HashMap<>(mConnectListeners);
        } else {
            listenerMap = new HashMap<>(mCacheTopics);
        }
        Logger.w("监听器数量:", listenerMap.size(), ",是否联网:", Utils.isConnected(mContext));
        for (String mac : listenerMap.keySet()) {
            disConnectInternal(mac, true, false);
        }
        for (String mac : listenerMap.keySet()) {
            if (Utils.isConnected(mContext)) {
                connect(mac, listenerMap.get(mac).getConnectStatusListener(), listenerMap.get(mac).getDataListener());
            } else {
                Logger.w("mqtt没联网，不连接");
                mCacheTopics.put(mac, listenerMap.get(mac));
            }
        }
    }

    /**
     * 断开
     */
    public void disConnect(String macaddress) {
        disConnectInternal(macaddress, true, true);
        if (hasSubscribe.size() <= 0) {
            Logger.w("关闭网络定时器");
            if (mNetTimer != null) {
                mNetTimer.cancel();
            }
        }
    }

    /**
     * @param macaddress
     * @param clearListener
     * @param isResetTime   新增条件  判断是否需要重置上次接收数据的时间，主要是为了判断10分钟未收到数据，不加这个判断会出现每次重连的时候都会重置时间，永远都不可能出现10分钟
     */
    private void disConnectInternal(String macaddress, boolean clearListener, boolean isResetTime) {
        try {
            if (clearListener) {
                mConnectListeners.remove(macaddress);
            }
            hasSubscribe.remove(macaddress);
            if (mMQTTClient != null && mMQTTClient.isConnected()) {
                mMQTTClient.unsubscribe(Utils.getTopicByMacAddress(macaddress));
                mMQTTClient.unsubscribe(Utils.getWillTopicByMacAddress(macaddress));
                mMQTTClient.unsubscribe(Utils.getPatchDisconnectTopicByMacAddress(macaddress));
            }
            if (isResetTime) {
                resetTime(macaddress);
            }
            Logger.w("取消mqtt订阅成功:", macaddress, ",hasSubscribe大小:", hasSubscribe.size(), ",清除监听器:", clearListener);
            if (hasSubscribe.size() <= 0) {
                clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.w("取消mqtt订阅失败:", macaddress);
        }
    }

    private void initNetTimer() {
        if (mNetTimer == null) {
            mNetTimer = new Timer();
            mNetTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    boolean isConnect = Utils.isConnected(mContext);
                    if (isConnect) {
                        for (String mac : mCacheTopics.keySet()) {
                            connect(mac, mCacheTopics.get(mac).getConnectStatusListener(), mCacheTopics.get(mac).getDataListener());
                        }
                    }
                }
            }, 0, 5000);
        }
    }

    /**
     * 定时检测mqtt连接和是否能收到数据
     */
    private void initTimer() {
        if (mTimer != null) return;
        //连接成功，则检测能否获取到数据
        mLastTimerTime = System.currentTimeMillis();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - mLastTimerTime > 20000) {
                    Logger.w("定时器被冻结了");
                    mLastTimerTime = System.currentTimeMillis();
                    return;
                }
                Logger.w("mqtt定时器,网络是否连接", Utils.isConnected(mContext), ",监听器个数:", mConnectListeners.size());
                checkDisconnect();
                mLastTimerTime = System.currentTimeMillis();
            }
        }, 0, 5000);
    }

    /**
     * 定时检测没有数据接收，默认十分钟收不到数据则回调断开
     */
    private void checkDisconnect() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //解决hashmap的ConcurrentModificationException异常
                Map<String, Long> tempHash = new HashMap<>();
                tempHash = mLastReceiveDataTime;
                for (final String macaddress : tempHash.keySet()) {
                    long timeInterval = System.currentTimeMillis() - tempHash.get(macaddress);
                    Logger.w("timeInterval is : ", timeInterval);
                    if (mConnectListeners.get(macaddress) != null) {
                        if (timeInterval > disconnectTimeOut) {
                            Logger.w("mqtt数据接收超时了:", macaddress, ",上次接收数据时间:", tempHash.get(macaddress), ",监听器:", mConnectListeners.size());
                            disConnectInternal(macaddress, false, true);
                            mConnectListeners.get(macaddress).getConnectStatusListener().onDisconnect(false);
                        } else if (timeInterval >= DISCONNECT_TIME_OUT_NOT_DISCONNECT) {
                            mConnectListeners.get(macaddress).getConnectStatusListener().receiveDockerOffline(true);
                        }
                    }
                }
            }
        });
    }

    private void resetTime(String macaddress) {
        mLastReceiveDataTime.remove(macaddress);
    }

    public void setConnectTimeoutTime(long time) {
        if (time > 0) {
            this.connectTimeOut = time;
        }
    }

    public void setDisconnectTimeoutTime(long time) {
        if (time > 0) {
            this.disconnectTimeOut = time;
        }
    }

    /**
     * 是否订阅
     */
    public boolean isConnected(String macaddress) {
        return hasSubscribe.contains(macaddress);
    }

    private void clear() {
        mLastReceiveDataTime.clear();
        mLastTimerTime = System.currentTimeMillis();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        try {
            if (mMQTTClient != null) {
                mMQTTClient.unregisterResources();
                mMQTTClient.close();
                mMQTTClient.disconnect();
                mMQTTClient = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e);
        }
        mInstance = null;
        Logger.w("mqtt服务销毁");
    }

    private static native void initDefaultMQTTConfig();
}