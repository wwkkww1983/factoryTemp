package com.proton.carepatchtemp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.proton.carepatchtemp.BuildConfig;
import com.proton.carepatchtemp.bean.ShareTempBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.socailauth.util.NetUtils;
import com.proton.temp.connector.bean.DockerDataBean;
import com.proton.temp.connector.bean.TempDataBean;
import com.proton.temp.connector.bluetooth.utils.BleUtils;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.internal.Util;

/**
 * Created by wangmengsi on 2018/3/15.
 * 共享温度
 */
@SuppressLint("StaticFieldLeak")
public class MQTTShareManager {
    /**
     * 数据接收超时时间
     */
    private static final long DISCONNECT_TIME_OUT = 10 * 60000;
    private static MQTTShareManager mInstance;
    private static Context mContext;
    /**
     * 缓存的主题，防止mqtt服务没连接成功，调用订阅导致订阅失败
     */
    private Map<String, List<MQTTShareListener>> mCacheTopics = new HashMap<>();
    /**
     * 缓存的发布，防止mqtt服务没连接成功
     */
    private Map<String, String> mCachePublish = new HashMap<>();
    private MqttAndroidClient mMQTTClient;
    /**
     * 连接器回调
     */
    private Map<String, List<MQTTShareListener>> mConnectListeners = new HashMap<>();
    /**
     * 订阅的主题的mac地址
     */
    private List<String> hasSubscribe = new ArrayList<>();
    private ShareTempBean mShareTempBean = new ShareTempBean();
    private Gson mGson = new Gson();
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
    private IMqttActionListener mMqttConnectCallback = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            dealWithCacheTopic();
            dealWithCachePublish();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Logger.i("mqtt共享连接失败:" + exception);
            if (((MqttException) exception).getReasonCode() == 32100) {
                //已经连接直接订阅
                dealWithCacheTopic();
                dealWithCachePublish();
            }
        }
    };
    /**
     * 数据接收回调
     */
    private MqttCallback mMQTTDataCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            //断线了，重连
            Logger.w("mqtt共享服务掉线了:", cause != null ? cause.getMessage() : "");
            reConnect();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            String data = new String(message.getPayload());
            if (mConnectListeners.containsKey(topic)) {
                if (mConnectListeners.get(topic) != null) {
                    for (MQTTShareListener listener : mConnectListeners.get(topic)) {
                        listener.receiveMQTTData(data);
                    }
                }
            }
            if (topic.contains(Utils.PREFIX)) {
                DockerDataBean dockerDataBean = Utils.parseDockerData(data);
                if (dockerDataBean == null) return;
                String currentConnectMac = mCurrentConnectMacaddress.get(topic);
                if (!TextUtils.isEmpty(currentConnectMac) && !currentConnectMac.equals(dockerDataBean.getMacaddress())) {
                    Logger.w("mqtt共享两次体温贴不一致,上一次mac地址:", currentConnectMac, ",新的mac地址:", dockerDataBean.getMacaddress());
                    return;
                }
                mCurrentConnectMacaddress.put(topic, dockerDataBean.getMacaddress());
                if (mConnectListeners.containsKey(topic)) {
                    List<TempDataBean> temps = BleUtils.parseMqttTemp(dockerDataBean);
                    if (temps != null && temps.size() > 0) {
                        mShareTempBean.setCode(201);
                        mShareTempBean.setCurrentTemp(temps.get(temps.size() - 1).getAlgorithmTemp());
                        if (mConnectListeners.get(topic) != null) {
                            for (MQTTShareListener listener : mConnectListeners.get(topic)) {
                                listener.receiveMQTTData(mShareTempBean);
                            }
                        }
                    }
                }
            } else {
                try {
                    if (mConnectListeners.containsKey(topic)) {
                        if (mConnectListeners.get(topic) != null) {
                            for (MQTTShareListener listener : mConnectListeners.get(topic)) {
                                listener.receiveMQTTData(mGson.fromJson(data, ShareTempBean.class));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mLastReceiveDataTime.put(topic, System.currentTimeMillis());
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }
    };

    public static void init(Context context) {
        mContext = context;
        Logger.w("mqtt共享onCreate");
    }

    public static MQTTShareManager getInstance() {
        if (mContext == null) {
            throw new IllegalStateException("You should initialize MQTTConnector before using,You can initialize in your Application class");
        }
        if (mInstance == null) {
            mInstance = new MQTTShareManager();
        }
        return mInstance;
    }

    /**
     * 连接MQTT服务器
     */
    private void connectMQTTServer() {
        // 服务器地址（协议+地址+端口号）
        Logger.w("mqtt共享clientId:proton" + App.get().getApiUid());
        if (mMQTTClient == null) {
            // 服务器地址（协议+地址+端口号）
            mMQTTClient = new MqttAndroidClient(mContext, BuildConfig.MQTT_SERVER, "proton" + App.get().getApiUid());
            // 设置MQTT监听并且接受消息
            mMQTTClient.setCallback(mMQTTDataCallback);
        }

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        // 用户名
        mqttConnectOptions.setUserName(BuildConfig.MQTT_USERNAME);
        // 密码
        mqttConnectOptions.setPassword(BuildConfig.MQTT_PWD.toCharArray());

        if (mMQTTClient.isConnected()) {
            Logger.w("mqtt已经连接");
            return;
        }
        try {
            String content = "{\"uid\": " + App.get().getApiUid() + ", \"code\": 206}";
            mqttConnectOptions.setWill("clients/" + App.get().getApiUid(), content.getBytes(), 0, false);
            mMQTTClient.connect(mqttConnectOptions, null, mMqttConnectCallback);
        } catch (Exception e) {
            e.printStackTrace();
            reConnect();
        }
    }

    private void dealWithCacheTopic() {
        if (mCacheTopics.size() <= 0 || mMQTTClient == null || !mMQTTClient.isConnected()) return;
        for (String topic : mCacheTopics.keySet()) {
            if (mCacheTopics.get(topic) != null) {
                for (MQTTShareListener listener : mCacheTopics.get(topic)) {
                    subscribe(topic, listener);
                }
            }
        }
        mCacheTopics.clear();
    }

    private void dealWithCachePublish() {
        Logger.w("mqtt共享缓存publish");
        if (mCachePublish.size() <= 0 || mMQTTClient == null || !mMQTTClient.isConnected()) return;
        for (String topic : mCachePublish.keySet()) {
            publish(topic, mCachePublish.get(topic));
        }
        mCachePublish.clear();
    }

    /**
     * 发布消息
     *
     * @param topic    主题
     * @param messeage 发布的消息
     */
    public void publish(String topic, String messeage) {
        if (mMQTTClient == null || !mMQTTClient.isConnected()) {
            mCachePublish.put(topic, messeage);
            connectMQTTServer();
            return;
        }

        if (TextUtils.isEmpty(messeage) || TextUtils.isEmpty(topic)) return;

        try {
            mMQTTClient.publish(topic, messeage.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, Object object) {
        publish(topic, mGson.toJson(object));
    }

    /**
     * 订阅
     */
    public void subscribe(String topic, MQTTShareListener shareListener) {
        try {
            //记录连接时间
            Logger.w("mqtt共享连接:" + topic);
            //判断mqtt是否连接
            if (mMQTTClient == null || !mMQTTClient.isConnected()) {
                //没有连接
                if (mCacheTopics.get(topic) == null) {
                    mCacheTopics.put(topic, new ArrayList<>());
                }
                mCacheTopics.get(topic).add(shareListener);
                connectMQTTServer();
                return;
            }
            //存储连接器
            if (shareListener != null) {
                if (mConnectListeners.get(topic) == null) {
                    mConnectListeners.put(topic, new ArrayList<>());
                }
                if (!mConnectListeners.get(topic).contains(shareListener)) {
                    mConnectListeners.get(topic).add(shareListener);
                }
            }
            if (isSubscribe(topic)) {
                return;
            }
            mMQTTClient.subscribe(topic, 0);
            if (mConnectListeners.containsKey(topic)) {
                if (mConnectListeners.get(topic) != null) {
                    for (MQTTShareListener listener : mConnectListeners.get(topic)) {
                        listener.onSubscribeSuccess();
                    }
                }
            }
            Logger.w("mqtt共享订阅mqtt成功:" + topic);
            //存储订阅的主题
            hasSubscribe.add(topic);
            //开始计时器
            initTimer();
        } catch (Exception e) {
            Logger.w("mqtt共享订阅mqtt失败:" + topic, e.getMessage());
            if (shareListener != null) {
                shareListener.onConnectFaild();
            }
        }
    }

    public void unsubscribe(String topic) {
        unsubscribeInternal(topic);
    }

    private void unsubscribeInternal(String topic) {
        unsubscribeInternal(topic, true);
    }

    /**
     * 断开
     */
    private void unsubscribeInternal(String topic, boolean clearListener) {
        try {
            if (TextUtils.isEmpty(topic)) return;
            if (clearListener) {
                mConnectListeners.remove(topic);
            }
            mLastReceiveDataTime.remove(topic);
            hasSubscribe.remove(topic);
            if (hasSubscribe.size() <= 0) {
                close();
            }
            if (mMQTTClient != null && mMQTTClient.isConnected()) {
                mMQTTClient.unsubscribe(topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.w("mqtt共享取消mqtt订阅失败:" + topic);
        }
    }

    /**
     * 是否订阅
     */
    public boolean isSubscribe(String topic) {
        return hasSubscribe.contains(topic);
    }

    private void reConnect() {
        try {
            Map<String, List<MQTTShareListener>> listenerMap;
            if (mConnectListeners != null && mConnectListeners.size() > 0) {
                listenerMap = new HashMap<>(mConnectListeners);
            } else {
                listenerMap = new HashMap<>(mCacheTopics);
            }
            Logger.w("mqtt共享监听器数量:", listenerMap.size(), ",是否联网:", Utils.isConnected(mContext));
            for (String mac : listenerMap.keySet()) {
                unsubscribeInternal(mac);
            }
            for (String topic : listenerMap.keySet()) {
                if (Utils.isConnected(mContext)) {
                    for (MQTTShareListener listener : listenerMap.get(topic)) {
                        subscribe(topic, listener);
                    }
                } else {
                    Logger.w("mqtt没联网，不连接");
                    mCacheTopics.put(topic, listenerMap.get(topic));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    Logger.w("mqtt共享定时器被冻结了");
                    mLastTimerTime = System.currentTimeMillis();
                    return;
                }
                checkDisconnect();
                mLastTimerTime = System.currentTimeMillis();
            }
        }, 0, 5000);
    }


    /**
     * 定时检测没有数据接收，默认十分钟收不到数据则回调断开
     */
    private void checkDisconnect() {
        /*for (final String topic : mLastReceiveDataTime.keySet()) {
            if (System.currentTimeMillis() - mLastReceiveDataTime.get(topic) > DISCONNECT_TIME_OUT) {
                Logger.w("mqtt共享数据接收超时了:" + topic);
                mHandler.post(() -> {
                    unsubscribeInternal(topic, false);
                    if (mConnectListeners.get(topic) != null) {
                        if (mConnectListeners.get(topic) != null) {
                            for (MQTTShareListener listener : mConnectListeners.get(topic)) {
                                listener.onDisconnect();
                            }
                        }
                    }
                });
            }
        }
*/
        Set<String> keySet = mLastReceiveDataTime.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String topic = iterator.next();
            if (System.currentTimeMillis() - mLastReceiveDataTime.get(topic) > DISCONNECT_TIME_OUT) {
                Logger.w("mqtt共享数据接收超时了:" + topic);

                mHandler.post(() -> {
//                    unsubscribeInternal(topic, false);
                    try {
                        if (TextUtils.isEmpty(topic)) return;
                        //特别注意：不能使用map.remove(name)  否则会报同样的错误
                        iterator.remove();

                        hasSubscribe.remove(topic);
                        if (hasSubscribe.size() <= 0) {
                            close();
                        }
                        if (mMQTTClient != null && mMQTTClient.isConnected()) {
                            mMQTTClient.unsubscribe(topic);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.w("mqtt共享取消mqtt订阅失败:" + topic);
                    }

                    if (mConnectListeners.get(topic) != null) {
                        if (mConnectListeners.get(topic) != null) {
                            for (MQTTShareListener listener : mConnectListeners.get(topic)) {
                                listener.onDisconnect();
                            }
                        }
                    }
                });

            }

        }



    }

    /**
     * 关闭mqtt
     */
    public void close() {
        Logger.w("mqtt共享准备关闭");
        try {
            mLastReceiveDataTime.clear();
            mLastTimerTime = System.currentTimeMillis();
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
            if (mMQTTClient != null) {
                mMQTTClient.unregisterResources();
                mMQTTClient.close();
                mMQTTClient.disconnect();
                mMQTTClient = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mInstance = null;
        Logger.w("mqtt共享服务销毁");
    }

    public static class MQTTShareListener {
        /**
         * 接收mqtt共享温度
         */
        public void receiveMQTTData(ShareTempBean shareTemp) {
        }

        /**
         * 接收mqtt原始数据
         */
        public void receiveMQTTData(String data) {
        }

        /**
         * 订阅成功
         */
        public void onSubscribeSuccess() {
        }

        public void onDisconnect() {
        }

        public void onConnectFaild() {
        }
    }
}
