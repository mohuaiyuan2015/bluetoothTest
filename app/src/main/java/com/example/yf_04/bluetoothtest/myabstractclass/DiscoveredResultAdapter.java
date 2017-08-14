package com.example.yf_04.bluetoothtest.myabstractclass;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;

/**
 * Created by YF-04 on 2017/8/11.
 */

public class DiscoveredResultAdapter extends DiscoveredResult {

    public DiscoveredResultAdapter(){

    }

    @Override
    public BluetoothAdapter.LeScanCallback getLeScanCallback() {
        return super.getLeScanCallback();
    }

    @Override
    public void setLeScanCallback(BluetoothAdapter.LeScanCallback leScanCallback) {
        super.setLeScanCallback(leScanCallback);
    }

    @Override
    public ScanCallback getScanCallback() {
        return super.getScanCallback();
    }

    @Override
    public void setScanCallback(ScanCallback scanCallback) {
        super.setScanCallback(scanCallback);
    }
}
