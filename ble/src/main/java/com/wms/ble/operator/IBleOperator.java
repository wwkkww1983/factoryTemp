package com.wms.ble.operator;

import com.wms.ble.callback.OnConnectListener;
import com.wms.ble.callback.OnReadCharacterListener;
import com.wms.ble.callback.OnScanListener;
import com.wms.ble.callback.OnSubscribeListener;
import com.wms.ble.callback.OnUnSubscribeListener;
import com.wms.ble.callback.OnWriteCharacterListener;

/**
 * Created by wangmengsi on 2017/10/26.
 */

public interface IBleOperator {

    /**
     * 设置连接超时时间
     */
    void setConnectTimeoutTime(long time);

    /**
     * set connect listener
     */
    void setConnectListener(OnConnectListener listener);

    /**
     * connect device by device's mac address
     *
     * @param mac mac address
     */
    void connect(final String mac);

    /**
     * scan ble device by device name
     *
     * @param name     device name
     * @param scanTime scan time
     * @param listener scan listener
     */
    void scanDevice(final OnScanListener listener, int scanTime, String... name);

    /**
     * scan first and get scanrecord to connect device
     */
    void scanAndConnect(int scanTime, String mac);

    /**
     * scan first and get scanrecord to connect device
     */
    void scanAndConnect(String mac);

    /**
     * read charactor
     *
     * @param mac                     device's mac address
     * @param uuid_server             server's uuid
     * @param uuid_charactor          charactor's uuid
     * @param onReadCharacterListener read listener
     */
    void read(String mac, String uuid_server, String uuid_charactor, final OnReadCharacterListener onReadCharacterListener);

    /**
     * read charactor
     *
     * @param mac                      device's mac address
     * @param uuid_service             service's uuid
     * @param uuid_charactor           charactor's uuid
     * @param onWriteCharacterListener write listener
     */
    void write(String mac, String uuid_service, String uuid_charactor, byte[] value, final OnWriteCharacterListener onWriteCharacterListener);

    /**
     * read charactor
     *
     * @param mac                      device's mac address
     * @param uuid_service             service's uuid
     * @param uuid_charactor           charactor's uuid
     * @param onWriteCharacterListener write listener
     */
    void writeNoRsp(String mac, String uuid_service, String uuid_charactor, byte[] value, final OnWriteCharacterListener onWriteCharacterListener);

    /**
     * read charactor
     *
     * @param mac                      device's mac address
     * @param uuid_service             service's uuid
     * @param uuid_charactor           charactor's uuid
     * @param onWriteCharacterListener write listener
     */
    void writeDescriptor(String mac, String uuid_service, String uuid_charactor, String uuid_descriptor, byte[] value, final OnWriteCharacterListener onWriteCharacterListener);

    /**
     * disconnect ble device
     *
     * @param mac mac address
     */
    void disConnect(String mac);

    /**
     * Check if the device is connected
     *
     * @param mac mac address
     * @return true is connected
     */
    boolean isConnected(String mac);

    /**
     * cancel connect
     */
    void cancelConnect(String mac);

    /**
     * open bluetooth
     *
     * @return true is open, false is close
     */
    void openBluetooth();

    /**
     * close bluetooth
     *
     * @return true is success, false is fail
     */
    void closeBluetooth();

    /**
     * whether or not bluetooth opens successfully
     *
     * @return true is sucess,false is fail
     */
    boolean isBluetoothOpened();

    /**
     * check device is support ble
     */
    boolean isSupportBle();

    /**
     * subscribe notification
     *
     * @param mac            mac address
     * @param uuid_service   service's uuid
     * @param uuid_charactor charactor's uuid
     */
    void subscribeNotification(String mac, String uuid_service, final String uuid_charactor);

    /**
     * subscribe notification
     *
     * @param mac            mac address
     * @param uuid_service   service's uuid
     * @param uuid_charactor charactor's uuid
     */
    void subscribeNotification(String mac, String uuid_service, final String uuid_charactor, OnSubscribeListener listener);

    /**
     * unsubscribe notification
     *
     * @param mac            mac address
     * @param uuid_service   service's uuid
     * @param uuid_charactor charactor's uuid
     */
    void unsubscribeNotification(String mac, String uuid_service, String uuid_charactor);

    /**
     * unsubscribe notification
     *
     * @param mac            mac address
     * @param uuid_service   service's uuid
     * @param uuid_charactor charactor's uuid
     */
    void unsubscribeNotification(String mac, String uuid_service, String uuid_charactor, OnUnSubscribeListener listener);

    /**
     * stop search ble device
     */
    void stopScan();

    /**
     * set mtu
     */
    void setMTU(String mac, int mtu);
}
