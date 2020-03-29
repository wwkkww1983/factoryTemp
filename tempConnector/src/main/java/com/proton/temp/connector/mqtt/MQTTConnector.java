package com.proton.temp.connector.mqtt;

import android.annotation.SuppressLint;
import android.content.Context;

import com.proton.temp.connector.bean.MQTTConfig;
import com.proton.temp.connector.interfaces.ConnectStatusListener;
import com.proton.temp.connector.interfaces.Connector;
import com.proton.temp.connector.interfaces.DataListener;
import com.wms.logger.Logger;

import java.util.HashMap;

/**
 * Created by wangmengsi on 2018/3/15.
 */
public class MQTTConnector implements Connector {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    /**
     * mqtt管理
     */
    private MQTTManager mMqttManager = MQTTManager.getInstance();
    /**
     * 当前设备管理的mac地址
     */
    private String macaddress;
    /**
     * 设备管理器存储
     */
    private static HashMap<String, MQTTConnector> mqttConnectorMap = new HashMap<>();

    /**
     * @param context application context
     */
    public static void init(Context context, MQTTConfig mqttConfig) {
        mContext = context;
        MQTTManager.init(mContext, mqttConfig);
    }

    private MQTTConnector(String macaddress) {
        this.macaddress = macaddress;
    }

    public static MQTTConnector getInstance(String macaddress) {

        if (mContext == null) {
            throw new IllegalStateException("You should initialize MQTTConnector before using,You can initialize in your Application class");
        }

        if (!mqttConnectorMap.containsKey(macaddress)) {
            mqttConnectorMap.put(macaddress, new MQTTConnector(macaddress));
        }

        return mqttConnectorMap.get(macaddress);
    }

    @Override
    public void connect() {
        connect(null, null);
    }

    @Override
    public void connect(ConnectStatusListener connectStatusListener, DataListener dataListener) {
        if (!mMqttManager.isConnected(macaddress)) {
            mMqttManager.connect(macaddress, connectStatusListener, dataListener);
        }
    }

    @Override
    public void disConnect() {
        mMqttManager.disConnect(macaddress);
        mqttConnectorMap.remove(macaddress);
        Logger.w("mqtt连接器:", mqttConnectorMap.size());
    }

    @Override
    public boolean isConnected() {
        return mMqttManager.isConnected(macaddress);
    }

    @Override
    public void cancelConnect() {
        Logger.w("取消连接");
        disConnect();
    }

    @Override
    public void setSampleRate(int sampleRate) {
    }

    @Override
    public void setConnectTimeoutTime(long time) {
        mMqttManager.setConnectTimeoutTime(time);
    }

    @Override
    public void setDisconnectTimeoutTime(long time) {
        mMqttManager.setDisconnectTimeoutTime(time);
    }
}
