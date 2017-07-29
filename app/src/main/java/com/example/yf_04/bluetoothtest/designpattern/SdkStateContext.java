package com.example.yf_04.bluetoothtest.designpattern;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

/**
 * Created by YF-04 on 2017/7/28.
 */

public class SdkStateContext {

    private SdkState state;
    private BluetoothGattCharacteristic characteristic;

    public SdkStateContext(){
        init();

    }


    private void init() {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt>=21){
            state=new Sdk21();

        }else{
            state=new Sdk20();
        }

        this.setState(state);
    }



    public SdkState getState() {
        return state;
    }

    public void setState(SdkState state) {
        this.state = state;
    }
}
