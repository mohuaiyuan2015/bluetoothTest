package com.example.yf_04.bluetoothtest.myabstractclass;

import android.bluetooth.BluetoothGattCallback;
import android.util.Log;

import com.example.yf_04.bluetoothtest.Utils.MyLog;

/**
 * Created by YF-04 on 2017/8/16.
 */

public class MultipleConnectionAdapter extends BluetoothGattCallback {
    private static final String TAG = "MultipleConnectionAdapt";

    public MultipleConnectionAdapter(){
        Log.d(TAG, "MultipleConnectionAdapter: ");

    }
}
