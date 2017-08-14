package com.example.yf_04.bluetoothtest.myabstractclass;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;

/**
 * Created by YF-04 on 2017/8/11.
 */

public abstract class DiscoveredResult {
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private ScanCallback scanCallback;

    public BluetoothAdapter.LeScanCallback getLeScanCallback() {
        return leScanCallback;
    }

    public void setLeScanCallback(BluetoothAdapter.LeScanCallback leScanCallback) {
        this.leScanCallback = leScanCallback;
    }

    public ScanCallback getScanCallback() {
        return scanCallback;
    }

    public void setScanCallback(ScanCallback scanCallback) {
        this.scanCallback = scanCallback;
    }
}
