package com.example.yf_04.bluetoothtest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
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
import android.view.LayoutInflater;
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
import java.util.UUID;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private Button scan;
    private Button stop;
    private RecyclerView recyclerView;
    private Context context;

    private  List<MDevice> list = new ArrayList<MDevice>();

    private Handler hander;
    /**
     * BluetoothAdapter for handling connections
     * 连接蓝牙都需要，用来管理手机上的蓝牙
     */
    public static BluetoothAdapter mBluetoothAdapter;

    private MyAdapter myAdapter;

    private String currentDevAddress;
    private String currentDevName;



    private MaterialDialog alarmDialog;
    private MaterialDialog progressDialog;

    boolean isShowingDialog = false;
    private boolean scaning;

    private MyApplication myApplication;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        context=this;
        myApplication=(MyApplication)getApplication();

        initUI();

        hander = new Handler();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        myAdapter = new MyAdapter(context,list);
        recyclerView.setAdapter(myAdapter);


        initListener();

        checkBleSupportAndInitialize();

        //注册广播接收者，接收消息
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());

        Log.d(TAG, " prepare init BluetoothLeService--------->");
        Intent gattServiceIntent = new Intent(context,BluetoothLeService.class);
       ComponentName componentName= context.startService(gattServiceIntent);
//        Log.d(TAG, "componentName==null: "+(componentName==null));
//        if(componentName!=null){
//            Log.d(TAG, "componentName:"+componentName.toString());
//        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
        //mohuaiyuan 201707  update: to be done at onResume method
//        isShowingDialog = false;
//        disconnectDevice();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        isShowingDialog=false;
        disconnectDevice();
    }

    private void initListener() {
        Log.d(TAG, "initListener: ");

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "scan onClick: ");
                searchDevice();
                onRefresh();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "stop onClick: ");
                stopSearching();

            }
        });

        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.d(TAG, "myAdapter onItemClick: ");
                Log.d(TAG, "scanning: "+scaning);

//                if (!scaning) {
////                    isShowingDialog = true;
//                    showProgressDialog();
//                    hander.postDelayed(dismssDialogRunnable, 20000);
//                    connectDevice(list.get(position).getDevice());
//                }else {
//                    Toast.makeText(context, "Scanning bluetooth device", Toast.LENGTH_SHORT).show();
//                }

                isShowingDialog=true;
                showProgressDialog();
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
            Log.d(TAG, "Runnable stopScanRunnable: ");
            if(scaning){
                scaning=false;
            }

            if (mBluetoothAdapter != null){
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }

        }
    };


    private Runnable dismssDialogRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Runnable dismssDialogRunnable... ");
            if(progressDialog !=null){
                progressDialog.dismiss();
            }
            //mohuaiyuan 201707  暂时注释
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
//                    if(list==null){
//                        list = new ArrayList<>();
//                    }

                    Log.d(TAG, "device name: "+mDev.getDevice().getName());
                    Log.d(TAG, "device Mac: "+mDev.getDevice().getAddress());
                    list.add(mDev);
                   refreshBluetoothData();
                }
            });
        }
    };



    public void searchDevice() {
        Log.d(TAG, "searchDevice: ");
        if (!scaning) {
            scaning = true;
            //如果有连接先关闭连接
            disconnectDevice();
//            searchAnimate();
        }
    }

    public void stopSearching(){
        Log.d(TAG, "stopSearching: ");
        scaning = false;
        stopScan();
    }


    public void onRefresh() {
        // Prepare list view and initiate scanning
        Log.d(TAG, "onRefresh: ");

        if (myAdapter != null) {
            myAdapter.clear();
            myAdapter.notifyDataSetChanged();
        }

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

    /**
     * refresh  the list of bluetooth data
     */
    private void refreshBluetoothData(){
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
    }


    //add API 23 Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: "+requestCode);

        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                Log.d(TAG, "grantResults.length: "+grantResults.length);
                if(grantResults.length>0){
                    Log.d(TAG, "grantResults[0]: "+grantResults[0]);
                }


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
        Log.d(TAG, "connectDevice: ");
        currentDevAddress = device.getAddress();
        currentDevName = device.getName();

        Log.d(TAG, "connectDevice name: "+currentDevName);
        Log.d(TAG, "connectDevice Mac: "+currentDevAddress);

        //mohuaiyuan 201707
        //如果是连接状态，断开，重新连接
//        if (BluetoothLeService.getConnectionState() != BluetoothLeService.STATE_DISCONNECTED){
//                BluetoothLeService.disconnect();
//        }

        BluetoothLeService.connect(currentDevAddress, currentDevName, context);
    }

    private void disconnectDevice() {
        Log.d(TAG, "disconnectDevice()...");
        isShowingDialog=false;
        BluetoothLeService.disconnect();
    }


    /**
     * BroadcastReceiver for receiving the GATT communication status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "BroadcastReceiver mGattUpdateReceiver action: "+action);
            // Status received when connected to GATT Server
            //连接成功
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                System.out.println("--------------------->连接成功");
                Log.d(TAG, "connnected --------------------->connected success");

                //mohuaiyuan 201707  多此一举  注释
//                //搜索服务
//                BluetoothLeService.discoverServices();
            }
            // Services Discovered from GATT Server
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG, "discovered services------------------>discovered services ");
                hander.removeCallbacks(dismssDialogRunnable);
                progressDialog.dismiss();
                prepareGattServices(BluetoothLeService.getSupportedGattServices());
            } else if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                Log.d(TAG, "disconnected----------------------->connected fail ");
                progressDialog.dismiss();
                //connect break (连接断开)
//                mohuaiyuan 201707
                showDialog(context.getString(R.string.conn_disconnected_home));
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
        Log.d(TAG, "prepareGattServices: ");
        prepareData(gattServices);

        //mohuaiyuan 201707
//        Intent intent = new Intent(this, ServicesActivity.class);
//        intent.putExtra("dev_name",currentDevName);
//        intent.putExtra("dev_mac",currentDevAddress);
//        startActivity(intent);
//        overridePendingTransition(0, 0);

        //直接跳转到 CharacteristicsActivity

            //mohuaiyuan 201707  暂时注释
        List<MService> services =myApplication.getServices();
        Log.d(TAG, "services.size(): "+services.size());
        jumpToCharacteristicActivity(services);


    }

    private void jumpToCharacteristicActivity(List<MService> list){
        Log.d(TAG, "itemClick: ");

        if(list.isEmpty()){
            Toast.makeText(context, "There is not SERVICE,please try another device!", Toast.LENGTH_SHORT).show();
            return;
        }

        int position=0;
        boolean isContains=false;
        for(int i=0;i<list.size();i++){
            MService mService = list.get(i);
            BluetoothGattService service = mService.getService();

            UUID serviceUuid = service.getUuid();
            if (serviceUuid.toString().equals(GattAttributes.USR_SERVICE)) {
                position=i;
                isContains=true;
                break;
            }
        }

        //mohuaiyuan 201707  暂时注释
//        if (!isContains){
//            Toast.makeText(context, "There is not USR_SERVICE,please try another device!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        MService mService = list.get(position);
        BluetoothGattService service = mService.getService();
        myApplication.setCharacteristics(service.getCharacteristics());

        MyApplication.serviceType = MyApplication.SERVICE_TYPE.TYPE_USR_DEBUG;

        // jump to CharacteristicsActivity
//        Intent intent = new Intent(context, CharacteristicsActivity.class);
//        intent.putExtra("is_usr_service", true);
//        context.startActivity(intent);

        jumpToCommunicate();

    }

    private void jumpToCommunicate() {
        List<BluetoothGattCharacteristic> characteristics = myApplication.getCharacteristics();
        BluetoothGattCharacteristic usrVirtualCharacteristic =
                new BluetoothGattCharacteristic(UUID.fromString(GattAttributes.USR_SERVICE),-1,-1);
        characteristics.add(usrVirtualCharacteristic);

        String write = context.getString(R.string.gatt_services_write);
        int position=0;
        boolean isHave=false;
        for(int i=0;i<characteristics.size();i++){
            BluetoothGattCharacteristic characteristic=characteristics.get(i);
            String porperty= Utils.getPorperties(context,characteristic);
            if(porperty.contains(write)){
                position=i;
                isHave=true;
                break;
            }
        }

        if(!isHave){
            Toast.makeText(context, "This device can not be writed,please try another device!", Toast.LENGTH_SHORT).show();
            return;
        }

        myApplication.setCharacteristic(characteristics.get(position));
        Intent intent = new Intent(context,Communicate.class);
        startActivity(intent);

    }


    /**
     * Prepare GATTServices data.
     *
     * @param gattServices
     */
    private void prepareData(List<BluetoothGattService> gattServices) {

        Log.d(TAG, "prepareData: ");
        if (gattServices == null)
            return;

        List<MService> list = new ArrayList<>();

        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            if (uuid.equals(GattAttributes.GENERIC_ACCESS_SERVICE) || uuid.equals(GattAttributes.GENERIC_ATTRIBUTE_SERVICE)){
                continue;
            }

            String name = GattAttributes.lookup(gattService.getUuid().toString(), "UnkonwService");
            MService mService = new MService(name, gattService);
            list.add(mService);
        }

        myApplication.setServices(list);
    }


    private void showDialog(String info) {
        if (!isShowingDialog){
            return;
        }
        if (alarmDialog != null){
            return;
        }

        alarmDialog = new MaterialDialog(this);
        alarmDialog.setTitle(getString(R.string.alert))
                .setMessage(info)
                .setPositiveButton(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmDialog.dismiss();
                        alarmDialog = null;
                    }
                });
        alarmDialog.show();
    }

    private void showProgressDialog() {
        Log.d(TAG, "showProgressDialog: ");
        progressDialog = new MaterialDialog(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.progressbar_item,
                        null);
        progressDialog.setView(view).show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
        if(progressDialog!=null){
            progressDialog.dismiss();
        }

    }

}
