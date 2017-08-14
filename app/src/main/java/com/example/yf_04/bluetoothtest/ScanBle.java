package com.example.yf_04.bluetoothtest;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.example.yf_04.bluetoothtest.Utils.MyLog;
import com.example.yf_04.bluetoothtest.myabstractclass.DiscoveredResult;

import java.util.List;

/**
 * Created by YF-04 on 2017/8/11.
 */

public class ScanBle {

    private static final String TAG = "ScanBle";

    /**
     * Scan cycle:(ms)
     */
    private static final long SCAN_CYCLE = 10*1000;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private int sdkInt=-1;

    private Context context;
    private Handler hander;
    private MyScanCallback myScanCallback;


    private BluetoothLeScanner bleScanner;

    private DiscoveredResult discoveredResult;



    public ScanBle(Context context){
       this.context=context;
        init();

    }

//    public ScanBle(Context context, BluetoothAdapter bluetoothAdapter){
//        this.context=context;
//        if(bluetoothAdapter==null){
//            Log.e(TAG, "bluetoothAdapter is null ,please check you device !" );
//            return;
//        }
//
//        this.bluetoothAdapter=bluetoothAdapter;
//        init();
//
//    }


    private void init(){
        if(bluetoothAdapter==null){
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
        if(bluetoothAdapter==null){
            Log.e(TAG, "bluetoothAdapter is null ,please check you device! " );
            return;
        }

        hander=new Handler();
        sdkInt=Build.VERSION.SDK_INT;
        MyLog.debug(TAG, "sdkInt: "+sdkInt);
        if(sdkInt>=21){
            myScanCallback =new MyScanCallback();
        }

    }

    public DiscoveredResult getDiscoveredResult() {
        return discoveredResult;
    }

    public void setDiscoveredResult(DiscoveredResult discoveredResult) {
        this.discoveredResult = discoveredResult;
    }

    public void start(){
        if (sdkInt< Build.VERSION_CODES.LOLLIPOP) {
            scanPrevious21Version();
        } else {
            scanAfter21Version();
        }

    }

    @SuppressLint("NewApi")
    public void stop(){
        Log.d(TAG, "stop: ");
        if(sdkInt< Build.VERSION_CODES.LOLLIPOP){
            bluetoothAdapter.stopLeScan(mLeScanCallback);
            hander.removeCallbacks(stopScanRunnable);
        }else{
            bleScanner.stopScan(myScanCallback);
            hander.removeCallbacks(stopScanRunnableNew);
        }
    }



    //扫描设备
    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            MyLog.debug(TAG, "Runnable stopScanRunnable: ");


            //mohuaiyuan 201708
//            mBluetoothAdapter.startLeScan(mLeScanCallback);

            bluetoothAdapter.stopLeScan(mLeScanCallback);


        }
    };


    @SuppressLint("NewApi")
    private Runnable stopScanRunnableNew=new Runnable() {
        @Override
        public void run() {
            MyLog.debug(TAG, "Runnable stopScanRunnableNew: ");

            if (bleScanner == null){
                bleScanner = bluetoothAdapter.getBluetoothLeScanner();
            }
            bleScanner.stopScan(myScanCallback);

        }
    };


    /**
     * Call back for BLE Scan
     * This call back is called when a BLE device is found near by.
     * 发现设备时回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,byte[] scanRecord) {
            discoveredResult.getLeScanCallback().onLeScan(device,rssi,scanRecord);

        }
    };


    @SuppressLint("NewApi")
    private  class MyScanCallback extends ScanCallback {

        public MyScanCallback(){

        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            discoveredResult.getScanCallback().onScanResult(callbackType,result);

        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            // 批量回调，一般不推荐使用，使用上面那个会更灵活
            discoveredResult.getScanCallback().onBatchScanResults(results);
        }
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            // 扫描失败，并且失败原因
            discoveredResult.getScanCallback().onScanFailed(errorCode);
        }
    }

    /**
     * 版本号21之前的调用该方法搜索
     */
    private void scanPrevious21Version() {
        MyLog.debug(TAG, "scanPrevious21Version: ");
        //10秒后停止扫描
        hander.postDelayed( stopScanRunnable , SCAN_CYCLE );
        bluetoothAdapter.stopLeScan(mLeScanCallback);
        bluetoothAdapter.startLeScan(mLeScanCallback);
    }

    /**
     * 版本号21及之后的调用该方法扫描，该方法不是特别管用,因此demo中不再使用，仅供参考
     */
    @SuppressLint("NewApi")
    private void scanAfter21Version() {
        MyLog.debug(TAG, "scanAfter21Version: ");

        hander.postDelayed(stopScanRunnableNew ,SCAN_CYCLE );

        if (bleScanner == null){
            bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        }

        bleScanner.stopScan(myScanCallback);
        bleScanner.startScan(myScanCallback);
    }


}
