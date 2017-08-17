package com.example.yf_04.bluetoothtest;

/**
 * Created by YF-04 on 2017/8/4.
 */

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.example.yf_04.bluetoothtest.BlueToothLeService.BluetoothLeService;
import com.example.yf_04.bluetoothtest.Utils.Utils;

/**
 * Send data to bluetooth
 */
class SendOrderRunnable implements Runnable{
    private static final String TAG = "MyRunnable";

    /**
     * the time interval of send data(ms)
     */
    private static final long SEND_INTERVAL_MILLIS= 0;
    /**
     * the time interval of send data(ns)
     */
    private static final int SEND_INTERVAL_NANOS= 100*1000;


    /**
     *The maximum number of repetitions of the command
     */
    private static final int DATA_UNIT=40;

    /**
     * the max time of repeat sending orders while
     */
    private static final int REPEAT_TIMES=3;
    /**
     *the time between repeat send orders
     */
    private static final int REPEAT_INTERVAL=1000;


    private  String data;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic characteristic;

    boolean sendResult=true;

    private  SendOrderResult sendOrderResult;


    public SendOrderRunnable(String data, BluetoothGattCharacteristic characteristic){
        this.data=data;
        this.characteristic=characteristic;
    }

    public SendOrderRunnable(String data, BluetoothGattCharacteristic characteristic,BluetoothGatt bluetoothGatt){
        this.data=data;
        this.bluetoothGatt=bluetoothGatt;
        this.characteristic=characteristic;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public SendOrderResult getSendOrderResult() {
        return sendOrderResult;
    }

    public void setSendOrderResult(SendOrderResult sendOrderResult) {
        this.sendOrderResult = sendOrderResult;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    private boolean isLegal(){
        boolean result=false;
        if(characteristic==null){
            Log.e(TAG, "characteristic==null: "+(characteristic==null) );
            return result;
        }
        if(data==null || data.length()==0){
            Log.e(TAG, "data==null :"+(data==null) );
            Log.e(TAG, " data.length()==0:"+(data.length()==0) );

            return result;
        }

        result=true;
        return result;
    }

    @Override
    public void run() {
        Log.d(TAG, "MyRunnable run: ");

        if(!isLegal()){
            Log.e(TAG, "myRunnable init illegal" );
            sendResult=false;
            return;
        }
        data=data.replace(" ","");

        int length =data.length();
        int sendCount=length / DATA_UNIT;
        int remainde=length % DATA_UNIT;
        if(remainde!=0){
            sendCount++;
        }

        int repeat=0;

        for ( repeat=0;repeat<REPEAT_TIMES;repeat++){
            sendResult=true;
            for (int i=0;i<sendCount;i++){
                String currentData="";
                int beginIndex=i*DATA_UNIT;
                int endIndex=beginIndex+DATA_UNIT;
                if(endIndex>length){
                    endIndex=length;
                }
                currentData=data.substring(beginIndex,endIndex);
                Log.d(TAG, "currentData: "+currentData);

                byte[] array = Utils.hexStringToByteArray(currentData);
                boolean result=false;
                try {
                    result= BluetoothLeService.writeCharacteristicGattDb(bluetoothGatt,characteristic, array);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "result: "+result);
                sendResult=result && sendResult;

                if(!result){
                    break;
                }

                //mohuaiyuan 201708
                try {
                    Thread.sleep(SEND_INTERVAL_MILLIS,SEND_INTERVAL_NANOS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            Log.d(TAG, "sendResult: "+sendResult);
            if(sendResult){
                break;
            }
            try {
                Thread.sleep(REPEAT_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        Log.d(TAG, "repeat: "+repeat);
        Log.d(TAG, "sendResult: "+sendResult);
        if(repeat==REPEAT_TIMES && !sendResult){
            if(sendOrderResult!=null){
                sendOrderResult.getResult(Communicate.SEND_ORDER_FAILED);
            }

        }else{
            if(sendOrderResult!=null){
                sendOrderResult.getResult(Communicate.SEND_ORDER_SUCCESS);
            }
        }


    }

    public interface SendOrderResult {
        void getResult(int responseCode);

    }


}
