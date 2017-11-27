package com.example.yf_04.bluetoothtest.Utils;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by YF-04 on 2017/10/16.
 */

public class MyUtils {
    private static final String TAG = "MyUtils";

    public MyUtils(){

    }


    public void setDiscoverableTimeout(int timeout) {
        Log.d(TAG, "setDiscoverableTimeout: ");
        BluetoothAdapter adapter= BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()){
            adapter.enable();
        }
        adapter.getScanMode();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, timeout);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDiscoverableTimeout() {
        Log.d(TAG, "closeDiscoverableTimeout: ");
        BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, 1);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
