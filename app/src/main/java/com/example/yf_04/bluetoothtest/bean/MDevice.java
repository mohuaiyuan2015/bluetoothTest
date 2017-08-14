package com.example.yf_04.bluetoothtest.bean;

import android.bluetooth.BluetoothDevice;

public class MDevice {
    private BluetoothDevice device;
    private int rssi;

    private int connectStatus;

    public MDevice(){

    }

    public MDevice(BluetoothDevice dev, int rssi){
        this.device = dev;
        this.rssi = rssi;
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

    public int getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(int connectStatus) {
        this.connectStatus = connectStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MDevice) {
            return device.equals(((MDevice) o).getDevice());
        }
        return false;
    }
}
