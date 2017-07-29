package com.example.yf_04.bluetoothtest.designpattern;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by YF-04 on 2017/7/28.
 */

public interface SdkState {
    void doAction(BluetoothGattCharacteristic characteristic, String order);
}
