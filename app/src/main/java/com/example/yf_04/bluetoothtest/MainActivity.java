package com.example.yf_04.bluetoothtest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
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

import com.example.yf_04.bluetoothtest.BlueToothLeService.BluetoothLeService;
import com.example.yf_04.bluetoothtest.Utils.GattAttributes;
import com.example.yf_04.bluetoothtest.Utils.Utils;
import com.example.yf_04.bluetoothtest.adapter.MyAdapter;
import com.example.yf_04.bluetoothtest.bean.MDevice;
import com.example.yf_04.bluetoothtest.bean.MService;

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

    private String currentDevAddress;
    private String currentDevName;




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

        //注册广播接收者，接收消息
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());


        Intent gattServiceIntent = new Intent(getApplicationContext(),BluetoothLeService.class);
        startService(gattServiceIntent);


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

        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

                hander.postDelayed(dismssDialogRunnable, 20000);
                connectDevice(list.get(position).getDevice());
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


    private Runnable dismssDialogRunnable = new Runnable() {
        @Override
        public void run() {

            disconnectDevice();
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
                   refreshBluetoothData();
                }
            });
        }
    };


    /**
     * refresh  the list of bluetooth data
     */
    private void refreshBluetoothData(){
        if (myAdapter != null) {
//            myAdapter.clear();
            myAdapter.notifyDataSetChanged();
        }
    }

    public void onRefresh() {
        // Prepare list view and initiate scanning
        Log.d(TAG, "onRefresh: ");

        refreshBluetoothData();

        //TODO  ask for Permission  ACCESS_COARSE_LOCATION   ACCESS_FINE_LOCATION

        String[] permissions=new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION    };
        //Android M Permission check
        Log.d(TAG, "Build.VERSION.SDK_INT: "+Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_COARSE_LOCATION )!=PackageManager.PERMISSION_GRANTED ){
            Log.d(TAG, "Android M Permission check ");
            Log.d(TAG, "ask for Permission... ");
            ActivityCompat.requestPermissions(this,permissions, PERMISSION_REQUEST_COARSE_LOCATION);

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
                }else {
                    Toast.makeText(context, "Bluetooth need some permisssions ,please grante permissions and try again !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }







    /**
     * 获得蓝牙适配器
     */
    private void checkBleSupportAndInitialize() {
        Log.d(TAG, "checkBleSupportAndInitialize: ");
        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d(TAG, "device_ble_not_supported ");
            Toast.makeText(this, R.string.device_ble_not_supported,Toast.LENGTH_SHORT).show();
            return;
        }
        // Initializes a Blue tooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            Log.d(TAG, "device_ble_not_supported ");
            Toast.makeText(this,R.string.device_ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }


        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "open bluetooth ");
            mBluetoothAdapter.enable();
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

    private void stopScan(){
        Log.d(TAG, "stopScan: ");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        hander.removeCallbacks(stopScanRunnable);
    }


    private void connectDevice(BluetoothDevice device) {
        currentDevAddress = device.getAddress();
        currentDevName = device.getName();
        //如果是连接状态，断开，重新连接
        if (BluetoothLeService.getConnectionState() != BluetoothLeService.STATE_DISCONNECTED){
            BluetoothLeService.disconnect();
        }

        BluetoothLeService.connect(currentDevAddress, currentDevName, context);
    }

    private void disconnectDevice() {
        BluetoothLeService.disconnect();
    }


    /**
     * BroadcastReceiver for receiving the GATT communication status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // Status received when connected to GATT Server
            //连接成功
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                System.out.println("--------------------->连接成功");

                //搜索服务
                BluetoothLeService.discoverServices();
            }
            // Services Discovered from GATT Server
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                hander.removeCallbacks(dismssDialogRunnable);
                prepareGattServices(BluetoothLeService.getSupportedGattServices());
            } else if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                //connect break (连接断开)
                //mohuaiyuan 201707 注释掉
//                showDialog(getString(R.string.conn_disconnected_home));
            }
        }
    };



    /**
     * Getting the GATT Services
     * 获得服务
     *
     * @param gattServices
     */
    private void prepareGattServices(List<BluetoothGattService> gattServices) {
        prepareData(gattServices);

        Intent intent = new Intent(this, ServicesActivity.class);
        intent.putExtra("dev_name",currentDevName);
        intent.putExtra("dev_mac",currentDevAddress);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /**
     * Prepare GATTServices data.
     *
     * @param gattServices
     */
    private void prepareData(List<BluetoothGattService> gattServices) {

        if (gattServices == null)
            return;

        List<MService> list = new ArrayList<>();

        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            if (uuid.equals(GattAttributes.GENERIC_ACCESS_SERVICE) || uuid.equals(GattAttributes.GENERIC_ATTRIBUTE_SERVICE))
                continue;
            String name = GattAttributes.lookup(gattService.getUuid().toString(), "UnkonwService");
            MService mService = new MService(name, gattService);
            list.add(mService);
        }

        ((MyApplication) getApplication()).setServices(list);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

}
