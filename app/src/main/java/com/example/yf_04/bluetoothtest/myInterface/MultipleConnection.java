package com.example.yf_04.bluetoothtest.myInterface;

import android.bluetooth.BluetoothGatt;

/**
 * Created by YF-04 on 2017/8/10.
 */

public interface MultipleConnection {
    void onConnectionStateChange(BluetoothGatt gatt, int status,int newState);
}
