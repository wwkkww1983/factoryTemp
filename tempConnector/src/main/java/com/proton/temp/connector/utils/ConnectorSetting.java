package com.proton.temp.connector.utils;

public class ConnectorSetting {
    /**
     * MQTT连接超时时间
     */
    public static final long MQTT_CONNECT_TIME_OUT = 15000;
    /**
     * MQTT数据接收超时时间
     */
    public static final long MQTT_DISCONNECT_TIME_OUT = 15 * 60000;
    /**
     * MQTT数据接收超时时间,但不断开连接，为啥需要这样，问需求
     */
    public static final long DISCONNECT_TIME_OUT_NOT_DISCONNECT = 30000;
    /**
     * 没有连接蓝牙3秒重连，防止重连过快
     */
    public static final long NO_BLUETOOTH_RECONNECT_TIME = 3000;
    /**
     * 广播扫描连接超时时间
     */
    public static final long BROADCAST_CONNECT_TIMEOUT = 10000;
    /**
     * 广播扫描数据接收超时时间
     */
//    public static final long BROADCAST_DISCONNECT_TIMEOUT = 30000;
    public static final long BROADCAST_DISCONNECT_TIMEOUT = 6000;
}