package com.example.yf_04.bluetoothtest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.example.yf_04.bluetoothtest.BlueToothLeService.BluetoothLeService;
import com.example.yf_04.bluetoothtest.Utils.Constants;
import com.example.yf_04.bluetoothtest.Utils.GattAttributes;
import com.example.yf_04.bluetoothtest.Utils.MyLog;
import com.example.yf_04.bluetoothtest.Utils.Utils;
import com.example.yf_04.bluetoothtest.adapter.MyAdapter;
import com.example.yf_04.bluetoothtest.bean.MDevice;
import com.example.yf_04.bluetoothtest.bean.MService;
import com.example.yf_04.bluetoothtest.bean.MacAddressInfo;
import com.example.yf_04.bluetoothtest.myInterface.MultipleConnection;
import com.example.yf_04.bluetoothtest.myabstractclass.DiscoveredResultAdapter;
import com.example.yf_04.bluetoothtest.myabstractclass.MultipleConnectionAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    private Button scan;
    private Button stop;

    private Button showDebugMsg;
    private Button sendOrders;
    private Button connectAll;


    private RecyclerView recyclerView;
    private Context context;

    private  List<MDevice> list = new ArrayList<MDevice>();

    private ScanBle scanBle;
    private DiscoveredResultAdapter myDiscoveredResult;

    private boolean scaning;
    private BluetoothLeScanner bleScanner;

    private Handler hander;

    /**
     * BluetoothAdapter for handling connections
     * 连接蓝牙都需要，用来管理手机上的蓝牙
     */
    public  BluetoothAdapter mBluetoothAdapter;

    private MyAdapter myAdapter;

    private String currentDevAddress;
    private String currentDevName;

    private MaterialDialog alarmDialog;
    private MaterialDialog progressDialog;

    boolean isShowingDialog = false;

    private MyApplication myApplication;
    private int sdkInt=-1;

    private  MyScanCallback myScanCallback;

    private ExecutorService installPool=null;

    //mohuaiyuan 201708
//    private Map<String,BluetoothGatt> bluetoothGattMap=new HashMap<String, BluetoothGatt>();

    private MacAddressInfo macAddressInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyLog.debug(TAG, "onCreate: ");

        context=this;
        myApplication=(MyApplication)getApplication();

        installPool = Executors. newSingleThreadExecutor();

        initUI();
        initMacAddressInfo();

        hander = new Handler();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        myAdapter = new MyAdapter(context,list);
        recyclerView.setAdapter(myAdapter);

        sdkInt=Build.VERSION.SDK_INT;
        MyLog.debug(TAG, "sdkInt: "+sdkInt);
        if(sdkInt>=21){
            myScanCallback =new MyScanCallback();
        }

        initListener();

        checkBleSupportAndInitialize();

        //mohuaiyuan  prepare scan bluetooth device and get results of scan
        scanBle=new ScanBle(context);
        myDiscoveredResult =new DiscoveredResultAdapter();
        myDiscoveredResult.setScanCallback(myScanCallback);
        myDiscoveredResult.setLeScanCallback(mLeScanCallback);
        scanBle.setDiscoveredResult(myDiscoveredResult);


        //注册广播接收者，接收消息
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());

        MyLog.debug(TAG, " prepare init BluetoothLeService--------->");
        Intent gattServiceIntent = new Intent(context,BluetoothLeService.class);
       ComponentName componentName= context.startService(gattServiceIntent);
//        MyLog.debug(TAG, "componentName==null: "+(componentName==null));
//        if(componentName!=null){
//            MyLog.debug(TAG, "componentName:"+componentName.toString());
//        }

    }

    private void initMacAddressInfo() {
        Log.d(TAG, "initMacAddressInfo: ");

        AssetManager am = context.getAssets();
        InputStream inputStream=null;
        BufferedReader bufferedReader=null;
        try {
            inputStream=am.open("macAddress");
            bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            while ((line=bufferedReader.readLine())!=null){
                String[] temp=line.split(",");
                Log.d(TAG, "temp: "+temp.length);
                if(temp!=null && temp.length==2){
                    long id=Long.valueOf(temp[0].trim());
                    String mac=temp[1].trim();
                    Log.d(TAG, "macAddressInfo id: "+id);
                    Log.d(TAG, "macAddressInfo mac: "+mac);
                    if(macAddressInfo==null){
                        macAddressInfo=new MacAddressInfo();
                    }
                    macAddressInfo.setId(id);
                    macAddressInfo.setMac(mac);
                }else{
                    Toast.makeText(context,"Please Check assets/macAddress file!",Toast.LENGTH_SHORT).show();
                }
            }

            inputStream.close();
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    @Override
    protected void onStop() {
        super.onStop();
        MyLog.debug(TAG, "onStop: ");
        isShowingDialog=false;

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        MyLog.debug(TAG, "onRestart: ");
        //mohuaiyuan 201707  update: to be done at onResume method
//        isShowingDialog = false;
//        disconnectDevice();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.debug(TAG, "onResume: ");
        isShowingDialog=false;

        //mohuaiyuan  201708 暂时注释
//        disconnectDevice();

        //mohuaiyuan 201708
        disconnectDeviceAll();

    }

    /**
     * init Listener
     */
    private void initListener() {

        MyLog.debug(TAG, "initListener: ");

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.debug(TAG, "scan onClick: ");
                searchDevice();
                onRefresh();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.debug(TAG, "stop onClick: ");
                stopSearching();

            }
        });

        showDebugMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.debug(TAG, "showDebugMsg.setOnClickListener  onClick: ");
                MyLog.debug(TAG, "ConnectionInfoCollector CurrentCharacteristic size:"+ConnectionInfoCollector.getCurrentCharacteristic().size());
                MyLog.debug(TAG, "ConnectionInfoCollector: "+ConnectionInfoCollector.print());
            }
        });

        sendOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectionInfoCollector.getCurrentCharacteristic().isEmpty()){
                    Toast.makeText(context, "There is not connection,please connect a device first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(context, Communicate.class);
                context.startActivity(intent);
            }
        });

        connectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list==null || list.isEmpty()||list.size()<1){
                    Toast.makeText(context, "Please scan bluetooth first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                isShowingDialog=true;
                showProgressDialog();
                hander.postDelayed(dismssDialogRunnable, Constants.CONNECT_TIME_OUT);

                List<MDevice> connectList=getConnectDeviceByMac();
                connectDevice(connectList);

            }
        });

        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                MyLog.debug(TAG, "myAdapter onItemClick: ");
                MyLog.debug(TAG, "scanning: "+scaning);

//                if (!scaning) {
////                    isShowingDialog = true;
//                    showProgressDialog();
//                    hander.postDelayed(dismssDialogRunnable, 20000);
//                    connectDevice(list.get(position).getDevice());
//                }else {
//                    Toast.makeText(context, "Scanning bluetooth device", Toast.LENGTH_SHORT).show();
//                }

                BluetoothDevice device=list.get(position).getDevice();
                if(device.getAddress().equals(macAddressInfo.getMac())){
                    isShowingDialog=true;
                    showProgressDialog();
                    hander.postDelayed(dismssDialogRunnable, Constants.CONNECT_TIME_OUT);
                    connectDevice(device);
                }else {
                    Toast.makeText(context,"Can not to connect the device!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        myAdapter.setOnDisconnectListener(new MyAdapter.OnDisconnectListener() {
            @Override
            public void onDisconnect(View view, int position) {
                MyLog.debug(TAG, "myAdapter.setOnDisconnectListener  onDisconnect: ");
                String deviceAddress=list.get(position).getDevice().getAddress();
                //mohuaiyuan 201708
//                BluetoothGatt bluetoothGatt=bluetoothGattMap.get(deviceAddress);
                BluetoothGatt bluetoothGatt=ConnectionInfoCollector.getBluetoothGattMap().get(deviceAddress);
                disconnectDevice(bluetoothGatt);
                //refresh data
                refreshBluetoothData();

            }
        });

        myAdapter.setOnShowStatusListener(new MyAdapter.OnShowStatusListener() {
            @Override
            public void onShow(View view, int position) {
                MyLog.debug(TAG, "myAdapter.setOnShowStatusListener  onShow:------------------------> ");
                int status=list.get(position).getConnectStatus();
                MyLog.debug(TAG, "device getConnectStatus: "+status);
            }
        });

        BluetoothLeService.setMultipleConnectionAdapter(new MultipleConnectionAdapter(){

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                MyLog.debug(TAG, " BluetoothLeService.setMultipleConnection  onConnectionStateChange: ");
                MyLog.debug(TAG,"status:"+status);
                MyLog.debug(TAG,"newState:"+newState);

                BluetoothDevice device = gatt.getDevice();
                String deviceAddress = device.getAddress();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getDevice().getAddress().equals(deviceAddress)) {
                        MyLog.debug(TAG, "position: "+i);
                        list.get(i).setConnectStatus(newState);
                        break;
                    }
                }

                updateBluetoothDeviceData();

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    //mohuaiyuan 201708
                    //connected
//                    if (!bluetoothGattMap.containsKey(deviceAddress)) {
//                        bluetoothGattMap.put(deviceAddress, gatt);
//                    }
                    if (!ConnectionInfoCollector.getBluetoothGattMap().containsKey(deviceAddress)) {
                        ConnectionInfoCollector.putBluetoothGatt(deviceAddress, gatt);
                    }

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    //mohuaiyuan 201708
                    //disconnected
//                    bluetoothGattMap.remove(deviceAddress);
                    ConnectionInfoCollector.remove(deviceAddress);

                }

            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    MyLog.debug(TAG, gatt.getDevice().getAddress() + "---------------onServices Discovered------------->GATT_SUCCESS ");

                    hander.removeCallbacks(dismssDialogRunnable);
                    if(progressDialog !=null){
                        progressDialog.dismiss();
                    }
                    List<BluetoothGattService> gattServices=BluetoothLeService.getSupportedGattServices(gatt);
                    prepareGattServices(gatt,gattServices);

                } else {
                    MyLog.debug(TAG, gatt.getDevice().getAddress() + "--------------onServicesDiscovered-------------->fail  ");
                    MyLog.debug(TAG, "onServicesDiscovered status: " + status);
                }

            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                MyLog.debug(TAG, "onCharacteristicChanged------------------------>");
                MyLog.debug(TAG, "deviceAddress:"+gatt.getDevice().getAddress().toString());
                MyLog.debug(TAG, "characteristic UUID: " + characteristic.getUuid().toString());
                MyLog.debug(TAG, "characteristic  data: " + Utils.byteToASCII(characteristic.getValue()));

            }
        });

    }



    private void initUI() {
        MyLog.debug(TAG, "initUI: ");
        scan= (Button) findViewById(R.id.scan);
        stop= (Button) findViewById(R.id.stop);
        showDebugMsg= (Button) findViewById(R.id.showDebugMsg);
        sendOrders= (Button) findViewById(R.id.sendOrders);
        connectAll= (Button) findViewById(R.id.connectAll);
        recyclerView= (RecyclerView) findViewById(R.id.recycleview);
    }

    private List<MDevice>getConnectDeviceByMac(){
        Log.d(TAG, "getConnectDeviceByMac: ");
        List<MDevice> result=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            String macAddrss=list.get(i).getDevice().getAddress();
            if(macAddressInfo!=null && macAddrss.equals(macAddressInfo.getMac())){
                result.add(list.get(i));
            }
        }
        return result;
    }

    private static final int ADD_BLUETOOTH_DEVICE=2;
    private static final int UPDATE_BLUETOOTH_DEVICE_DATA=10;
    private Handler myUpdateUiHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_BLUETOOTH_DEVICE_DATA:
                    refreshBluetoothData();
                    break;

                case ADD_BLUETOOTH_DEVICE:

                    Bundle bundle=msg.getData();
                    BluetoothDevice device= bundle.getParcelable("device");
                    int rssi=bundle.getInt("rssi");

                    addBluetoothDevice(device, rssi);

                    updateBluetoothDeviceData();
                    break;

                default:

            }
        }
    };

    private void addBluetoothDevice(BluetoothDevice device,int rssi){

        MDevice mDev = new MDevice(device, rssi);

        if (list.contains(mDev)){
            return;
        }

        MyLog.debug(TAG, "device name: "+mDev.getDevice().getName());
        MyLog.debug(TAG, "device Mac: "+mDev.getDevice().getAddress());
        list.add(mDev);
        //mohuaiyuan 201708
//        refreshBluetoothData();

    }

    private void updateBluetoothDeviceData(){
        Message message=new Message();
        message.what=UPDATE_BLUETOOTH_DEVICE_DATA;
        myUpdateUiHandler.sendMessage(message);
    }

    private Runnable dismssDialogRunnable = new Runnable() {
        @Override
        public void run() {
            MyLog.debug(TAG, "Runnable dismssDialogRunnable... ");
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

            Message message=new Message();
            message.what=ADD_BLUETOOTH_DEVICE;

            Bundle bundle=new Bundle();
            bundle.putParcelable("device",device);
            bundle.putInt("rssi",rssi);
            bundle.putByteArray("scanRecord",scanRecord);
            message.setData(bundle);

            myUpdateUiHandler.sendMessage(message);

        }
    };



    public void searchDevice() {
        MyLog.debug(TAG, "searchDevice: ");
        MyLog.debug(TAG, "scaning: "+scaning);
        if (!scaning) {
            scaning = true;
            //mohuaiyuan 201708
            //如果有连接先关闭连接
//            disconnectDevice();
            disconnectDeviceAll();
        }
    }

    public void stopSearching(){
        MyLog.debug(TAG, "stopSearching: ");
        scaning = false;
        stopScan();
    }


    public void onRefresh() {
        // Prepare list view and initiate scanning
        MyLog.debug(TAG, "onRefresh: ");

        if (myAdapter != null) {
            myAdapter.clear();
            myAdapter.notifyDataSetChanged();
        }

        //TODO  ask for Permissions:  ACCESS_COARSE_LOCATION   ACCESS_FINE_LOCATION

        String[] permissions=new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION    };
        //Android M Permission check
        MyLog.debug(TAG, "Build.VERSION.SDK_INT: "+Build.VERSION.SDK_INT);
        if(sdkInt>= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_COARSE_LOCATION )!=PackageManager.PERMISSION_GRANTED ){
            MyLog.debug(TAG, "Android M Permission check ");
            MyLog.debug(TAG, "ask for Permission... ");
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

        MyLog.debug(TAG, "onRequestPermissionsResult: "+requestCode);

        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                MyLog.debug(TAG, "grantResults.length: "+grantResults.length);
                if(grantResults.length>0){
                    MyLog.debug(TAG, "grantResults[0]: "+grantResults[0]);
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
     * get bluetoothAdapter and open bluetooth if the bluetooth is disabled
     */
    private void checkBleSupportAndInitialize() {
        MyLog.debug(TAG, "checkBleSupportAndInitialize: ");
        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            MyLog.debug(TAG, "device_ble_not_supported ");
            Toast.makeText(this, R.string.device_ble_not_supported,Toast.LENGTH_SHORT).show();
            return;
        }
        // Initializes a Blue tooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            MyLog.debug(TAG, "device_ble_not_supported ");
            Toast.makeText(this,R.string.device_ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }


        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            MyLog.debug(TAG, "open bluetooth ");
            mBluetoothAdapter.enable();
        }
    }

    private void startScan() {
        MyLog.debug(TAG, "startScan: ");

        scanBle.start();

        hander.postDelayed(new Runnable() {
            @Override
            public void run() {
                scaning=false;
            }
        },Constants.SCAN_CYCLE);
    }

    @SuppressLint("NewApi")
    private class MyScanCallback extends ScanCallback{

        public MyScanCallback(){

        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            MDevice mDev = new MDevice(result.getDevice(), result.getRssi());
            if (list.contains(mDev)){
                return;
            }

            MyLog.debug(TAG, "device name: "+mDev.getDevice().getName());
            MyLog.debug(TAG, "device Mac: "+mDev.getDevice().getAddress());

            list.add(mDev);
            refreshBluetoothData();
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            // 批量回调，一般不推荐使用，使用上面那个会更灵活
        }
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            // 扫描失败，并且失败原因
        }
    }

    @SuppressLint("NewApi")
    private void stopScan(){
        MyLog.debug(TAG, "stopScan: ");

        scanBle.stop();
    }


    private void connectDevice(BluetoothDevice device) {
        MyLog.debug(TAG, "connectDevice: ");

        if (device == null) {
            MyLog.error(TAG, "device==null ");
            return;
        }

        String deviceAddress = device.getAddress();

        if (deviceAddress == null || deviceAddress.isEmpty()) {
            MyLog.error(TAG, " deviceAddress == null || deviceAddress.isEmpty() " );
            return;
        }

        String deviceName = device.getName();

        currentDevAddress = deviceAddress;
        currentDevName = deviceName;

        MyLog.debug(TAG, "connectDevice name: " + currentDevName);
        MyLog.debug(TAG, "connectDevice Mac: " + currentDevAddress);

        //mohuaiyuan 201708  add :stop scan
//        stopSearching();

        //mohuaiyuan 201707
        //如果是连接状态，断开，重新连接
//        if (BluetoothLeService.getConnectionState() != BluetoothLeService.STATE_DISCONNECTED){
//                BluetoothLeService.disconnect();
//        }

        BluetoothLeService.connect(deviceAddress, deviceName, context);
    }

    private void connectDevice(List<MDevice> mDevices) {
        MyLog.debug(TAG, "connectDevice: ");

        if(mDevices==null || mDevices.isEmpty()){
            Log.e(TAG, "mDevices==null || mDevices.isEmpty()" );
            return;
        }
        for(int i=0;i<mDevices.size();i++){
            final BluetoothDevice device=mDevices.get(i).getDevice();
            installPool.execute(new Runnable() {
                @Override
                public void run() {
                    connectDevice(device);
                }
            });

        }
    }

    /**
     * disconnect the bluetooth while is connected
     */
    private void disconnectDevice() {
        MyLog.debug(TAG, "disconnectDevice: ");
        isShowingDialog=false;
        BluetoothLeService.disconnect();
    }

    /**
     * disconnect the bluetooth by the bluetoothGatt while is connected
     * @param bluetoothGatt
     */
    private void disconnectDevice(BluetoothGatt bluetoothGatt) {
        MyLog.debug(TAG, "disconnectDevice(BluetoothGatt bluetoothGatt)..");
        isShowingDialog=false;
        BluetoothLeService.disconnect(bluetoothGatt);
    }

    /**
     * disconnect all the bluetooth while is connected
     */
    private void disconnectDeviceAll(){
        MyLog.debug(TAG, "disconnectDeviceAll");
        Map<String ,BluetoothGatt>map=  ConnectionInfoCollector.getBluetoothGattMap();
        if(map==null || map.isEmpty()){
            return;
        }
        Iterator<String>iterator=map.keySet().iterator();
        List<BluetoothGatt> gatts=new ArrayList<>();
        while (iterator.hasNext()){
            String deviceAddress=iterator.next();
            BluetoothGatt bluetoothGatt=map.get(deviceAddress);
            gatts.add(bluetoothGatt);
//            disconnectDevice(bluetoothGatt);
        }

        for(int i=0;i<gatts.size();i++){
            disconnectDevice(gatts.get(i));
        }


    }



    /**
     * BroadcastReceiver for receiving the GATT communication status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            MyLog.debug(TAG, "BroadcastReceiver mGattUpdateReceiver action: "+action);
            // Status received when connected to GATT Server
            //连接成功
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                System.out.println("--------------------->连接成功");
                MyLog.debug(TAG, "connnected --------------------->connected success");

                //mohuaiyuan 201707  多此一举  注释
//                //搜索服务
//                BluetoothLeService.discoverServices();
            }
            // Services Discovered from GATT Server
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                MyLog.debug(TAG, "discovered services------------------>discovered services ");
                hander.removeCallbacks(dismssDialogRunnable);
                if(progressDialog !=null){
                    progressDialog.dismiss();
                }
                prepareGattServices(BluetoothLeService.getSupportedGattServices());

            } else if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {

                MyLog.debug(TAG, "disconnected----------------------->connected fail ");
                if(progressDialog!=null){
                    progressDialog.dismiss();
                }

                //connect break (连接断开)
//                mohuaiyuan 201707
                showDialog(context.getString(R.string.conn_disconnected_home));

            }else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
                MyLog.debug(TAG, "ACTION_ACL_DISCONNECTED: ");
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceAddress=device.getAddress();
                ConnectionInfoCollector.remove(deviceAddress);

                for(int i=0;i<list.size();i++){
                    String temp=list.get(i).getDevice().getAddress();
                    if(temp.equals(deviceAddress)){
                        list.get(i).setConnectStatus(0);
                        break;
                    }
                }
                refreshBluetoothData();

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
        MyLog.debug(TAG, "prepareGattServices: ");

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
        MyLog.debug(TAG, "services.size(): "+services.size());
        jumpToCharacteristicActivity(services);


    }

    private void prepareGattServices(BluetoothGatt bluetoothGatt,List<BluetoothGattService> gattServices) {
        MyLog.debug(TAG, "prepareGattServices: ");

        prepareData(bluetoothGatt, gattServices);

        //mohuaiyuan 201707
//        Intent intent = new Intent(this, ServicesActivity.class);
//        intent.putExtra("dev_name",currentDevName);
//        intent.putExtra("dev_mac",currentDevAddress);
//        startActivity(intent);
//        overridePendingTransition(0, 0);

        //直接跳转到 CharacteristicsActivity

        //mohuaiyuan 201707  暂时注释
        List<MService> services = myApplication.getServices();
        MyLog.debug(TAG, "services.size(): " + services.size());

        //mohuaiyuan 201708
//        jumpToCharacteristicActivity(services);
        jumpToCharacteristicActivity(bluetoothGatt, services);

    }

    private void jumpToCharacteristicActivity(List<MService> list){
        MyLog.debug(TAG, "jumpToCharacteristicActivity: ");

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
            MyLog.debug(TAG, "serviceUuid: "+serviceUuid);
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
        List<BluetoothGattCharacteristic> characteristics=service.getCharacteristics();
        myApplication.setCharacteristics(characteristics);

        //mohuaiyuan 201708
        String deviceAddress=null;
        try {
            deviceAddress=BluetoothLeService.mBluetoothGatt.getDevice().getAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(deviceAddress!=null){
            ConnectionInfoCollector.getCharacteristicsMap().put(deviceAddress,characteristics);
        }else {
            MyLog.error(TAG, "deviceAddress!=null" );
        }

        MyApplication.serviceType = MyApplication.SERVICE_TYPE.TYPE_USR_DEBUG;

        // jump to CharacteristicsActivity
//        Intent intent = new Intent(context, CharacteristicsActivity.class);
//        intent.putExtra("is_usr_service", true);
//        context.startActivity(intent);

        jumpToCommunicate();

    }

    private void jumpToCharacteristicActivity(BluetoothGatt bluetoothGatt,List<MService> list) {
        MyLog.debug(TAG, "jumpToCharacteristicActivity: ");

        if (list.isEmpty()) {
            Toast.makeText(context, "There is not SERVICE,please try another device!", Toast.LENGTH_SHORT).show();
            return;
        }

        int position = 0;
        boolean isContains = false;
        for (int i = 0; i < list.size(); i++) {
            MService mService = list.get(i);
            BluetoothGattService service = mService.getService();

            UUID serviceUuid = service.getUuid();
            MyLog.debug(TAG, "serviceUuid: " + serviceUuid);
            if (serviceUuid.toString().equals(GattAttributes.USR_SERVICE)) {
                position = i;
                isContains = true;
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
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        myApplication.setCharacteristics(characteristics);

        //mohuaiyuan 201708
        String deviceAddress = null;
        try {
            deviceAddress = bluetoothGatt.getDevice().getAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (deviceAddress != null) {
            ConnectionInfoCollector.getCharacteristicsMap().put(deviceAddress, characteristics);
        } else {
            MyLog.error(TAG, "deviceAddress!=null");
        }

        MyApplication.serviceType = MyApplication.SERVICE_TYPE.TYPE_USR_DEBUG;

        // jump to CharacteristicsActivity
//        Intent intent = new Intent(context, CharacteristicsActivity.class);
//        intent.putExtra("is_usr_service", true);
//        context.startActivity(intent);

        //mohuaiyuan 201708
//        jumpToCommunicate();
        jumpToCommunicate(bluetoothGatt);

    }

    private void jumpToCommunicate() {
        MyLog.debug(TAG, "jumpToCommunicate: ");
        List<BluetoothGattCharacteristic> characteristics = myApplication.getCharacteristics();
        BluetoothGattCharacteristic usrVirtualCharacteristic =
                new BluetoothGattCharacteristic(UUID.fromString(GattAttributes.USR_SERVICE),-1,-1);
        characteristics.add(usrVirtualCharacteristic);

        String write = context.getString(R.string.gatt_services_write);
        String notify= context.getString(R.string.gatt_services_notify);
        int position=0;
        boolean isHave=false;
        for(int i=0;i<characteristics.size();i++){
            BluetoothGattCharacteristic characteristic=characteristics.get(i);
            String porperty= Utils.getProperties(context,characteristic);
//            //mohuaiyuan 201708
//            if(porperty.contains(notify)){
//                position=i;
//                isHave=true;
//                break;
//            }

            //原来的代码
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
        BluetoothGattCharacteristic writeCharacteristic=characteristics.get(position);
        myApplication.setCharacteristic(writeCharacteristic);

        //mohuaiyuan 201708
        String deviceAddress=null;
        try {
            deviceAddress=BluetoothLeService.mBluetoothGatt.getDevice().getAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<Integer,BluetoothGattCharacteristic> map=new HashMap<>();
        map.put(Communicate.CHARACTERISTIC_TYPE_WRITE,writeCharacteristic);
        if(deviceAddress!=null){
            ConnectionInfoCollector.getCurrentCharacteristic().put(deviceAddress,map);
        }else {
            MyLog.error(TAG, "deviceAddress!=null" );
        }

        //mohuaiyuan 201708  暂时注释
//        Intent intent = new Intent(context,Communicate.class);
//        startActivity(intent);


    }


    private void jumpToCommunicate(BluetoothGatt bluetoothGatt) {
        MyLog.debug(TAG, "jumpToCommunicate: ");
        List<BluetoothGattCharacteristic> characteristics = myApplication.getCharacteristics();
        BluetoothGattCharacteristic usrVirtualCharacteristic =
                new BluetoothGattCharacteristic(UUID.fromString(GattAttributes.USR_SERVICE),-1,-1);
        characteristics.add(usrVirtualCharacteristic);

        String write = context.getString(R.string.gatt_services_write);
        String notify= context.getString(R.string.gatt_services_notify);
        int position=0;
        boolean isHave=false;
        for(int i=0;i<characteristics.size();i++){
            BluetoothGattCharacteristic characteristic=characteristics.get(i);
            String porperty= Utils.getProperties(context,characteristic);
//            //mohuaiyuan 201708
//            if(porperty.contains(notify)){
//                position=i;
//                isHave=true;
//                break;
//            }

            //原来的代码
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
        BluetoothGattCharacteristic writeCharacteristic=characteristics.get(position);
        myApplication.setCharacteristic(writeCharacteristic);

        //mohuaiyuan 201708
        String deviceAddress=null;
        try {
            deviceAddress=bluetoothGatt.getDevice().getAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<Integer,BluetoothGattCharacteristic> map=new HashMap<>();
        map.put(Communicate.CHARACTERISTIC_TYPE_WRITE,writeCharacteristic);
        if(deviceAddress!=null){
            ConnectionInfoCollector.getCurrentCharacteristic().put(deviceAddress,map);
        }else {
            MyLog.error(TAG, "deviceAddress!=null" );
        }




        //mohuaiyuan 201708  暂时注释
//        Intent intent = new Intent(context,Communicate.class);
//        startActivity(intent);


    }

    /**
     * Prepare GATTServices data.
     *
     * @param gattServices
     */
    private void prepareData(List<BluetoothGattService> gattServices) {

        MyLog.debug(TAG, "prepareData: ");
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

        //mohuaiyuan 201708
        String deviceAddress =null;
        try {
            deviceAddress= BluetoothLeService.mBluetoothGatt.getDevice().getAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(deviceAddress!=null){
            ConnectionInfoCollector.getServicesMap().put(deviceAddress ,list);
        }else {
            MyLog.error(TAG, "deviceAddress==null " );
        }


    }

    private void prepareData(BluetoothGatt bluetoothGatt,List<BluetoothGattService> gattServices) {

        MyLog.debug(TAG, "prepareData: ");
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

        //mohuaiyuan 201708
        String deviceAddress =null;
        try {
            deviceAddress= bluetoothGatt.getDevice().getAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(deviceAddress!=null){
            ConnectionInfoCollector.getServicesMap().put(deviceAddress ,list);
        }else {
            MyLog.error(TAG, "deviceAddress==null " );
        }

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
        MyLog.debug(TAG, "showProgressDialog: ");
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
        if(installPool!=null){
            installPool.shutdown();
        }


    }

}
