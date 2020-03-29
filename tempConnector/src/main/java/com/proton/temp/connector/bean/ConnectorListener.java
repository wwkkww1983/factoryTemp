package com.proton.temp.connector.bean;

import com.proton.temp.connector.interfaces.ConnectStatusListener;
import com.proton.temp.connector.interfaces.DataListener;

/**
 * Created by wangmengsi on 2018/3/15.
 * 包含mqtt连接监听器和数据监听器
 */
public class ConnectorListener {
    private ConnectStatusListener connectStatusListener;
    private DataListener dataListener;

    public ConnectorListener(ConnectStatusListener connectStatusListener, DataListener dataListener) {
        this.connectStatusListener = connectStatusListener;
        this.dataListener = dataListener;
    }

    public ConnectStatusListener getConnectStatusListener() {
        return connectStatusListener;
    }

    public void setConnectStatusListener(ConnectStatusListener connectStatusListener) {
        this.connectStatusListener = connectStatusListener;
    }

    public DataListener getDataListener() {
        return dataListener;
    }

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }
}
