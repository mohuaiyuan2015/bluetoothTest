package com.example.yf_04.bluetoothtest.designpattern;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.example.yf_04.bluetoothtest.BlueToothLeService.BluetoothLeService;

/**
 * Created by YF-04 on 2017/7/28.
 */

public class Sdk21 implements SdkState {
    private static final String TAG = "Sdk21";
    @Override
    public void doAction(BluetoothGattCharacteristic characteristic, String order) {
        if(BluetoothLeService.requestMtu(512)){
            Log.d(TAG, "Max transmittal data is 512 ");
//                Toast.makeText(this,getString(R.string.transmittal_length,"512"),Toast.LENGTH_LONG).show();
        }else{
            Log.d(TAG, "Max transmittal data is 20 ");
//                Toast.makeText(this,getString(R.string.transmittal_length,"20"),Toast.LENGTH_LONG).show();
        }

        //send order

    }
}
