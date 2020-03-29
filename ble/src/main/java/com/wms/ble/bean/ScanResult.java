package com.wms.ble.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by 王梦思 on 2017/7/8.
 */
public class ScanResult {
    private BluetoothDevice device;
    private int rssi;
    private byte[] scanRecord;
    private String macaddress;
    private String name;

    public ScanResult() {
    }

    public ScanResult(BluetoothDevice device) {
        this(device, -1, new byte[]{});
    }

    public ScanResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
        this.macaddress = device != null ? device.getAddress() : "";
        this.name = device != null ? device.getName() : "";
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public ScanResult setMacaddress(String macaddress) {
        this.macaddress = macaddress;
        return this;
    }

    public String getName() {
        return name;
    }

    public ScanResult setName(String name) {
        this.name = name;
        return this;
    }
}
