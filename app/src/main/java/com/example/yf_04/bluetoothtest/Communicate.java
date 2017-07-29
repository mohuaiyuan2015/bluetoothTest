package com.example.yf_04.bluetoothtest;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.yf_04.bluetoothtest.BlueToothLeService.BluetoothLeService;
import com.example.yf_04.bluetoothtest.Utils.GattAttributes;
import com.example.yf_04.bluetoothtest.Utils.Orders;
import com.example.yf_04.bluetoothtest.Utils.Utils;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class Communicate extends AppCompatActivity {

    private static final String TAG = "Communicate";

    private Button standInSitu;
    private Button treadOnTheGround;
    private Button walkForward;
    private Button walkBackwards;
    private Button playBasketball;
    private Button stoop;


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

        requestMtu();

    }

    private void initListener() {
        standInSitu.setOnClickListener(myOnClickListener);
        treadOnTheGround.setOnClickListener(myOnClickListener);
        walkForward.setOnClickListener(myOnClickListener);
        walkBackwards.setOnClickListener(myOnClickListener);

        playBasketball.setOnClickListener(myOnClickListener);
        stoop.setOnClickListener(myOnClickListener);
    }

    private void initUI() {
        standInSitu= (Button) findViewById(R.id.standInSitu);
        treadOnTheGround= (Button) findViewById(R.id.treadOnTheGround);
        walkForward= (Button) findViewById(R.id.walkForward);
        walkBackwards= (Button) findViewById(R.id.walkBackwards);

        playBasketball= (Button) findViewById(R.id.playBasketball);
        stoop= (Button) findViewById(R.id.stoop);
    }

    private View.OnClickListener myOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.standInSitu:
                    writeOption(Orders.STAND_IN_SITU);
                    break;

                case R.id.treadOnTheGround:
                    writeOption(Orders.TREAD_ON_THE_GROUND);
                    break;

                case R.id.walkForward:
                    writeOption(Orders.WALK_FORWARD);
                    break;

                case R.id.walkBackwards:
                    writeOption(Orders.WALK_BACKWARDS);
                    break;

                case R.id.stoop:
                    writeOption(Orders.STOOP);
                    break;

                case R.id.playBasketball:
                    writeOption(Orders.PLAY_BASKETBALL);
                    break;

                default:

            }
        }
    };
    
    
    private void writeOption(String order){

        Log.d(TAG, "writeOption: ");


        if (TextUtils.isEmpty(order)){
            Log.e(TAG, "writeOption:order is empty!!!" );
            return;
        }

        //对十六进制的数据进行处理
        order = order.replace(" ","");
        if (!Utils.isRightHexStr(order)){
            return;
        }
        byte[] array = Utils.hexStringToByteArray(order);
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

    private void requestMtu(){
        int sdkInt = Build.VERSION.SDK_INT;
        Log.d(TAG, "sdkInt------------>"+sdkInt);
        if (sdkInt>=21){
            //设置最大发包、收包的长度为512个字节
            if(BluetoothLeService.requestMtu(512)){
                Log.d(TAG, "Max transmittal data is 512 ");
//                Toast.makeText(this,getString(R.string.transmittal_length,"512"),Toast.LENGTH_LONG).show();
            }else{
                Log.d(TAG, "Max transmittal data is 20 ");
//                Toast.makeText(this,getString(R.string.transmittal_length,"20"),Toast.LENGTH_LONG).show();
            }

        }else {
            Log.d(TAG, "Max transmittal data is 20 ");
//            Toast.makeText(this,getString(R.string.transmittal_length,"20"),Toast.LENGTH_LONG).show();
        }

    }




}
