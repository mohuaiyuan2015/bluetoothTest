package com.example.yf_04.bluetoothtest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.example.yf_04.bluetoothtest.adapter.MyAdapter;
import com.example.yf_04.bluetoothtest.bean.MDevice;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private Button scan;
    private Button stop;
    RecyclerView recyclerView;
    private Context context;

    private  List<MDevice> list = new ArrayList<>();

    private Handler hander;
    /**
     * BluetoothAdapter for handling connections
     * 连接蓝牙都需要，用来管理手机上的蓝牙
     */
    public static BluetoothAdapter mBluetoothAdapter;

    private MyAdapter myAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        context=this;


        initUI();
        initListener();


        hander = new Handler();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        myAdapter = new MyAdapter(context,list);
        recyclerView.setAdapter(myAdapter);



        checkBleSupportAndInitialize();


    }

    private void initListener() {
        Log.d(TAG, "initListener: ");

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "scan onClick: ");
                onRefresh();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "stop onClick: ");
                stopScan();

            }
        });



    }

    private void initUI() {
        Log.d(TAG, "initUI: ");
        scan= (Button) findViewById(R.id.scan);
        stop= (Button) findViewById(R.id.stop);
        recyclerView= (RecyclerView) findViewById(R.id.recycleview);
    }


    //扫描设备
    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothAdapter != null)
                mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    };

    /**
     * Call back for BLE Scan
     * This call back is called when a BLE device is found near by.
     * 发现设备时回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MDevice mDev = new MDevice(device, rssi);
                    if (list.contains(mDev)){
                        return;
                    }
                    if(list==null){
                        list = new ArrayList<>();
                    }
                    list.add(mDev);
                    //TODO delete the view element
//                    tvSearchDeviceCount.setText(getString(R.string.search_device_count, list.size()));
                }
            });
        }
    };


    public void onRefresh() {
        // Prepare list view and initiate scanning
        Log.d(TAG, "onRefresh: ");
        if (myAdapter != null) {
//            myAdapter.clear();
            myAdapter.notifyDataSetChanged();
        }


        //TODO  ask for Permission  ACCESS_COARSE_LOCATION   ACCESS_FINE_LOCATION

        String[] permissions=new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION
    };
        //Android M Permission check
        Log.d(TAG, "Build.VERSION.SDK_INT: "+Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Log.d(TAG, "Android M Permission check ");
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,permissions, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }else{

            startScan();
        }

    }

    //add API 23 Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        Log.d(TAG, "onRequestPermissionsResult: "+requestCode);

        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                Log.d(TAG, "grantResults.length: "+grantResults.length);
                Log.d(TAG, "grantResults[0]: "+grantResults[0]);

                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO request success

                    startScan();
                }
                break;
        }
    }


    private void startScan() {
        Log.d(TAG, "startScan: ");
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        scanPrevious21Version();
//        } else {
//            scanAfter21Version();
//        }
    }

    /**
     * 版本号21之前的调用该方法搜索
     */
    private void scanPrevious21Version() {
        Log.d(TAG, "scanPrevious21Version: ");
        //10秒后停止扫描
        hander.postDelayed(stopScanRunnable,10000);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }




    /**
     * 获得蓝牙适配器
     */
    private void checkBleSupportAndInitialize() {
        Log.d(TAG, "checkBleSupportAndInitialize: ");
        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d(TAG, "device_ble_not_supported ");
//            Toast.makeText(this, R.string.device_ble_not_supported,Toast.LENGTH_SHORT).show();
            return;
        }
        // Initializes a Blue tooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth

            Log.d(TAG, "device_ble_not_supported ");
//            Toast.makeText(this,R.string.device_ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }


        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "open bluetooth ");
            mBluetoothAdapter.enable();
        }
    }


    private void stopScan(){
        Log.d(TAG, "stopScan: ");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        hander.removeCallbacks(stopScanRunnable);
    }



}
