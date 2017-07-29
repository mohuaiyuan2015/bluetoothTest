package com.example.yf_04.bluetoothtest;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.example.yf_04.bluetoothtest.BlueToothLeService.BluetoothLeService;
import com.example.yf_04.bluetoothtest.Utils.GattAttributes;
import com.example.yf_04.bluetoothtest.Utils.Utils;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class Communicate extends AppCompatActivity {

    private static final String TAG = "Communicate";

    private Button playBasketball;


    private BluetoothGattCharacteristic writeCharacteristic;
    private MyApplication myApplication;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.communicate_layout);

        myApplication = (MyApplication) getApplication();
        initCharacteristics();

        initUI();
        initListener();


    }

    private void initListener() {
        playBasketball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeOption();
            }


        });
    }

    private void initUI() {
        playBasketball= (Button) findViewById(R.id.playBasketball);
    }

    private void writeOption(){

        //play basket ball
        String text = "01 10 20 00 00 0F 1E 00 00 00 79 00 06 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 48 DE";
        if (TextUtils.isEmpty(text)){
            return;
        }

        //对十六进制的数据进行处理
        text = text.replace(" ","");
        if (!Utils.isRightHexStr(text)){
            return;
        }
        byte[] array = Utils.hexStringToByteArray(text);
        writeCharacteristic(writeCharacteristic, array);
    }

    private void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        // Writing the hexValue to the characteristics
        try {
            BluetoothLeService.writeCharacteristicGattDb(characteristic, bytes);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void initCharacteristics(){
        BluetoothGattCharacteristic characteristic = myApplication.getCharacteristic();
        if (characteristic.getUuid().toString().equals(GattAttributes.USR_SERVICE)){

            List<BluetoothGattCharacteristic> characteristics = ((MyApplication)getApplication()).getCharacteristics();

            for (BluetoothGattCharacteristic c :characteristics){
                if (Utils.getPorperties(this,c).equals("Write")){
                    writeCharacteristic = c;
                    continue;
                }
            }

//            properties = "Notify & Write";

        }else {
//            properties = Utils.getPorperties(this, characteristic);
//
//            notifyCharacteristic = characteristic;
//            readCharacteristic = characteristic;
            writeCharacteristic = characteristic;
//            indicateCharacteristic = characteristic;
        }
    }


}
