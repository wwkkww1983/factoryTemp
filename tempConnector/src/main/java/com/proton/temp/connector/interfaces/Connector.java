package com.proton.temp.connector.interfaces;

/**
 * Created by wangmengsi on 2018/3/15.
 * 连接器
 */
public interface Connector {
    /**
     * 连接设备
     */
    void connect();

    void connect(ConnectStatusListener connectorListener, DataListener dataListener);

    /**
     * 断开连接设备
     */
    void disConnect();

    /**
     * 是否连接上了
     */
    boolean isConnected();

    /**
     * 取消连接
     */
    void cancelConnect();

    /**
     * 设置采样频率
     */
    void setSampleRate(int sampleRate);

    /**
     * 设置连接超时时间
     */
    void setConnectTimeoutTime(long time);

    /**
     * 设置接收数据超时时间
     */
    void setDisconnectTimeoutTime(long time);
}
