package com.example.yf_04.bluetoothtest;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.yf_04.bluetoothtest.BlueToothLeService.BluetoothLeService;
import com.example.yf_04.bluetoothtest.Utils.Constants;
import com.example.yf_04.bluetoothtest.Utils.GattAttributes;
import com.example.yf_04.bluetoothtest.Utils.MyLog;
import com.example.yf_04.bluetoothtest.Utils.Utils;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Communicate extends AppCompatActivity {

    private static final String TAG = "Communicate";
    public static final String CONNECT_MODEL="connect_model";
    public static final Integer CONNECT_MODEL_SINGLE=1;
    public static final Integer CONNECT_MODEL_MULTIPLe=2;
    public static final Integer CONNECT_MODEL_DEFAULT=CONNECT_MODEL_SINGLE;
    public static final Integer CHARACTERISTIC_TYPE_WRITE=1;
    public static final Integer CHARACTERISTIC_TYPE_NOTIFY=2;


//    private Integer connectModel=CONNECT_MODEL_DEFAULT;

    private Button standInSitu;
    private Button treadOnTheGround;
    private Button walkForward;
    private Button walkBackwards;

    private Button theSideWalk;
    private Button inSituSquatDown;
//    private Button fromSquatDownToStand;
    private Button placeToSitDown;

//    private Button fromSittingToStanding;
    private Button placeToLieDown;
//    private Button fromLieDownToStand;
    private Button putDown;

//    private Button fromTheGroundToTheStation;
    private Button bowOnesHead;
    private Button aWordHorse;
    private Button stance;

    private Button beforeTheLegPress;
    private Button sideLegPress;
    private Button chestOut;
    private Button stoop;

    private Button lookUp;
    private Button inSituTurning;
    private Button takeARightTurn;
    private Button lieOnYourStomachAndDoPushUps;

    private Button liftMyLeftArm;
    private Button liftMyRightArm;
    private Button wavingYourLeftArm;
    private Button wavingYouRightArm;

    private Button stretchYouLeftArm;
    private Button stretchYouRightArm;
    private Button playBasketball;
    private Button btnNotify;



    private Boolean isSquatDown=false;
    private Boolean isSitDown=false;
    private Boolean isLieDown=false;
    private Boolean isPutDown=false;

    private Boolean nofityEnable=false;


    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic readCharacteristic;
    private MyApplication myApplication;
    private Context context;

    private int sdkInt;

    private MyHandler myHandler;


    private  Boolean isDebugMode=false;

    //mohuaiyuan 201707  Temporary annotation
    //mode 二
   /* private List<BasicAction> list=new ArrayList<BasicAction>();

    private RecyclerView recyclerView ;
    private ActionAdapter adapter;*/


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mode 一
        setContentView(R.layout.communicate_layout);

        MyLog.debug(TAG, "onCreate: ");

        //mohuaiyuan 201708
//        Intent intent =getIntent();
//        Bundle bundle=intent.getExtras();
//        try {
//            connectModel=bundle.getInt(CONNECT_MODEL);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        context=this;
        myHandler=new MyHandler(context);

        myApplication = (MyApplication) getApplication();

        //mohuaiyuan 201708   Original code
//        initCharacteristics();

        //mohuaiyuan 201708 Original code
//        initCharacteristicWithSingle();
        initCharacteristicWithMultiple();



        MyLog.debug(TAG, "USR_SERVICE charac...: "+GattAttributes.USR_SERVICE);
        MyLog.debug(TAG, "write charac...: "+writeCharacteristic.getUuid().toString());
        MyLog.debug(TAG, "notify charac...: "+notifyCharacteristic.getUuid().toString());


        initUI();

        initListener();

        //mohuaiyuan 201707  暂时注释  仅仅为了测试 发送数据的情况
        sdkInt = Build.VERSION.SDK_INT;
        requestMtu(512);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    public static final int SEND_ORDER_SUCCESS=0;
    public static final int SEND_ORDER_FAILED=1;

    class MyHandler extends android.os.Handler{

        private Context context;
        public MyHandler(Context context){
            this.context=context;
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEND_ORDER_FAILED:
                    Toast.makeText(context, context.getResources().getString(R.string.send_failed), Toast.LENGTH_SHORT).show();
                    break;

                default:

            }

        }
    }



    private void initListener() {
        MyLog.debug(TAG, "initListener: ");
        //mode 一
        standInSitu.setOnClickListener(myOnClickListener);
        treadOnTheGround.setOnClickListener(myOnClickListener);
        walkForward.setOnClickListener(myOnClickListener);
        walkBackwards.setOnClickListener(myOnClickListener);

        theSideWalk.setOnClickListener(myOnClickListener);
        inSituSquatDown.setOnClickListener(myOnClickListener);
//        fromSquatDownToStand.setOnClickListener(myOnClickListener);
        placeToSitDown.setOnClickListener(myOnClickListener);

//        fromSittingToStanding.setOnClickListener(myOnClickListener);
        placeToLieDown.setOnClickListener(myOnClickListener);
//        fromLieDownToStand.setOnClickListener(myOnClickListener);
        putDown.setOnClickListener(myOnClickListener);

//        fromTheGroundToTheStation.setOnClickListener(myOnClickListener);
        bowOnesHead.setOnClickListener(myOnClickListener);
        aWordHorse.setOnClickListener(myOnClickListener);
        stance.setOnClickListener(myOnClickListener);

        beforeTheLegPress.setOnClickListener(myOnClickListener);
        sideLegPress.setOnClickListener(myOnClickListener);
        chestOut.setOnClickListener(myOnClickListener);
        stoop.setOnClickListener(myOnClickListener);

        lookUp.setOnClickListener(myOnClickListener);
        inSituTurning.setOnClickListener(myOnClickListener);
        takeARightTurn.setOnClickListener(myOnClickListener);
        lieOnYourStomachAndDoPushUps.setOnClickListener(myOnClickListener);

        liftMyLeftArm.setOnClickListener(myOnClickListener);
        liftMyRightArm.setOnClickListener(myOnClickListener);
        wavingYourLeftArm.setOnClickListener(myOnClickListener);
        wavingYouRightArm.setOnClickListener(myOnClickListener);

        stretchYouLeftArm.setOnClickListener(myOnClickListener);
        stretchYouRightArm.setOnClickListener(myOnClickListener);
        playBasketball.setOnClickListener(myOnClickListener);
        btnNotify.setOnClickListener(myOnClickListener);


    }

    private void initUI() {
        MyLog.debug(TAG, "initUI: ");

        standInSitu= (Button) findViewById(R.id.standInSitu);
        treadOnTheGround= (Button) findViewById(R.id.treadOnTheGround);
        walkForward= (Button) findViewById(R.id.walkForward);
        walkBackwards= (Button) findViewById(R.id.walkBackwards);

        theSideWalk= (Button) findViewById(R.id.theSideWalk);
        inSituSquatDown= (Button) findViewById(R.id.inSituSquatDown);
//        fromSquatDownToStand= (Button) findViewById(R.id.fromSquatDownToStand);
        placeToSitDown= (Button) findViewById(R.id.placeToSitDown);

//        fromSittingToStanding=(Button) findViewById(R.id.fromSittingToStanding);
        placeToLieDown=(Button) findViewById(R.id.placeToLieDown);
//        fromLieDownToStand=(Button) findViewById(R.id.fromLieDownToStand);
        putDown=(Button) findViewById(R.id.putDown);

//        fromTheGroundToTheStation=(Button) findViewById(R.id.fromTheGroundToTheStation);
        bowOnesHead=(Button) findViewById(R.id.bowOnesHead);
        aWordHorse=(Button) findViewById(R.id.aWordHorse);
        stance=(Button) findViewById(R.id.stance);

        beforeTheLegPress=(Button) findViewById(R.id.beforeTheLegPress);
        sideLegPress=(Button) findViewById(R.id.sideLegPress);
        chestOut=(Button) findViewById(R.id.chestOut);
        stoop=(Button) findViewById(R.id.stoop);

        lookUp=(Button) findViewById(R.id.lookUp);
        inSituTurning=(Button) findViewById(R.id.inSituTurning);
        takeARightTurn=(Button) findViewById(R.id.takeARightTurn);
        lieOnYourStomachAndDoPushUps=(Button) findViewById(R.id.lieOnYourStomachAndDoPushUps);

        liftMyLeftArm=(Button) findViewById(R.id.liftMyLeftArm);
        liftMyRightArm=(Button) findViewById(R.id.liftMyRightArm);
        wavingYourLeftArm=(Button) findViewById(R.id.wavingYourLeftArm);
        wavingYouRightArm=(Button) findViewById(R.id.wavingYouRightArm);

        stretchYouLeftArm=(Button) findViewById(R.id.stretchYouLeftArm);
        stretchYouRightArm=(Button) findViewById(R.id.stretchYouRightArm);
        playBasketball=(Button) findViewById(R.id.playBasketball);
        btnNotify=(Button) findViewById(R.id.btnNotify);

    }

    private String getStringById(int id ){
        MyLog.debug(TAG, "getStringById: ");

        String order=context.getResources().getString(id);
        MyLog.debug(TAG, "getStringById string: "+order);
        return order;
    }

    private SendOrderRunnable.SendOrderResult sendOrderResult= new SendOrderRunnable.SendOrderResult() {
        @Override
        public void getResult(int responseCode) {
            android.os.Message message=new android.os.Message();
            message.what=responseCode;
            myHandler.sendMessage(message);
        }
    };

    private View.OnClickListener myOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MyLog.debug(TAG, "myOnClickListener onClick: ");
            String order=null;

            switch (v.getId()){

                case R.id.standInSitu:
                    order=getStringById(R.string.STAND_IN_SITU);
                    break;

                case R.id.treadOnTheGround:
                    order=getStringById(R.string.TREAD_ON_THE_GROUND);
                    break;

                case R.id.walkForward:
                    order=getStringById(R.string.WALK_FORWARD);
                    break;

                case R.id.walkBackwards:
                    order=getStringById(R.string.WALK_BACKWARDS);
                    break;


                case R.id.theSideWalk:
                    order=getStringById(R.string.THE_SIDE_WALK);
                    break;

                case R.id.inSituSquatDown:
                    isSquatDown=!isSquatDown;
                    if(isSquatDown){
                        inSituSquatDown.setText(R.string.from_squat_down_to_stand);
                        order=getStringById(R.string.IN_SITU_SQUAT_DOWN);
                    }else{
                        inSituSquatDown.setText(R.string.in_situ_squat_down);
                        order=getStringById(R.string.FROM_SQAT_DOWN_TO_STAND);
                    }
                    break;

//                case R.id.fromSquatDownToStand:
//                    order=getStringById(R.string.FROM_SQAT_DOWN_TO_STAND);
//                    break;

                case R.id.placeToSitDown:
                    isSitDown=!isSitDown;
                    if(isSitDown){
                        placeToSitDown.setText(R.string.from_sitting_to_standing);
                        order=getStringById(R.string.PLACE_TO_SIT_DOWN);
                    }else {
                        placeToSitDown.setText(R.string.place_to_sit_down);
                        order=getStringById(R.string.FROM_SITTING_TO_STANDING);
                    }
                    break;


//                case R.id.fromSittingToStanding:
//                    order=getStringById(R.string.FROM_SITTING_TO_STANDING);
//                    break;

                case R.id.placeToLieDown:
                    isLieDown=!isLieDown;
                    if(isLieDown){
                        placeToLieDown.setText(R.string.from_lie_down_to_stand);
                        order=getStringById(R.string.PLACE_TO_LIE_DOWN);
                    }else {
                        placeToLieDown.setText(R.string.place_to_lie_down);
                        order=getStringById(R.string.FROM_LIE_DOWN_TO_STAND);
                    }
                    break;

//                case R.id.fromLieDownToStand:
//                    order=getStringById(R.string.FROM_LIE_DOWN_TO_STAND));
//                    break;

                case R.id.putDown:
                    isPutDown=!isPutDown;
                    if(isPutDown){
                        putDown.setText(R.string.from_the_ground_to_the_station);
                        order=getStringById(R.string.PUT_DOWN);
                    }else {
                        putDown.setText(R.string.put_down);
                        order=getStringById(R.string.FROM_THE_GROUND_TO_THE_STATION );
                    }
                    break;


//                case R.id.fromTheGroundToTheStation:
//                    order=getStringById(R.string.FROM_THE_GROUND_TO_THE_STATION );
//                    break;

                case R.id.bowOnesHead:
                    order=getStringById(R.string.BOW_ONES_HEAD );
                    break;

                case R.id.aWordHorse:
                    order=getStringById(R.string.A_WORD_HORSE );
                    break;

                case R.id.stance:
                    order=getStringById(R.string.STANCE );
                    break;


                case R.id.beforeTheLegPress:
                    order=getStringById(R.string.BEFORE_THE_LEG_PRESS );
                    break;

                case R.id.sideLegPress:
                    order=getStringById(R.string.SIDE_LEG_PRESS );
                    break;

                case R.id.chestOut:
                    order=getStringById(R.string.CHEST_OUT );
                    break;

                case R.id.stoop:
                    order=getStringById(R.string.STOOP );
                    break;


                case R.id.lookUp:
                    order=getStringById(R.string.LOOK_UP );
                    break;

                case R.id.inSituTurning:
                    order=getStringById(R.string.IN_SITU_TURNING );
                    break;

                case R.id.takeARightTurn:
                    order=getStringById(R.string.TAKE_A_RIGHT_TURN );
                    break;

                case R.id.lieOnYourStomachAndDoPushUps:
                    order=getStringById(R.string.LIE_ON_YOU_STOMACH_AND_DO_PUSH_UPS );
                    break;


                case R.id.liftMyLeftArm:
                    order=getStringById(R.string.LIFT_MY_LEFT_ARM );
                    break;

                case R.id.liftMyRightArm:
                    order=getStringById(R.string.LIFT_MY_RIGHT_ARM );
                    break;

                case R.id.wavingYourLeftArm:
                    order=getStringById(R.string.WAVING_YOU_LEFT_ARM );
                    break;

                case R.id.wavingYouRightArm:
                    order=getStringById(R.string.WAVING_YOU_RIGHT_ARM );
                    break;


                case R.id.stretchYouLeftArm:
                    order=getStringById(R.string.STRETCH_YOU_LEFT_ARM );
                    break;

                case R.id.stretchYouRightArm:
                    order=getStringById(R.string.STRETCH_YOU_RIGHT_ARM );
                    break;

                case R.id.playBasketball:
                    order=getStringById(R.string.PLAY_BASKETBALL );
                    break;

                case R.id.btnNotify:
                    notifyOption();

                    break;

                default:

            }

            writeOption(order);

        }

    };
    


//    private void writeOption(String order){
//        MyLog.debug(TAG, "writeOption: ");
//
//        if (TextUtils.isEmpty(order)){
//            MyLog.error(TAG, "writeOption:order is empty!!!" );
//            return;
//        }
//
//        //对十六进制的数据进行处理
//        order = order.replace(" ","");
//        if (!Utils.isRightHexStr(order)){
//            return;
//        }
//
//        //mohuaiyuan
////        writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
//
//
//        MyLog.debug(TAG, "sdkInt: "+sdkInt);
//        if (sdkInt >= 21) {
//            byte[] array = Utils.hexStringToByteArray(order);
//            writeCharacteristic(writeCharacteristic, array);
//        } else {
//            SendOrderRunnable myRunnable = new SendOrderRunnable(order, writeCharacteristic);
//            myRunnable.setSendOrderResult(sendOrderResult);
//            Thread thread = new Thread(myRunnable);
//            thread.start();
//        }
//
//
////        Iterator<String> iterator = ConnectionInfoCollector.getCurrentCharacteristic().keySet().iterator();
////        while (iterator.hasNext()) {
////            String deviceAddress = iterator.next();
////            Map<Integer, BluetoothGattCharacteristic> map = ConnectionInfoCollector.getCurrentCharacteristic().get(deviceAddress);
////            writeCharacteristic = map.get(CHARACTERISTIC_TYPE_WRITE);
////
////            if (sdkInt >= 21) {
////                byte[] array = Utils.hexStringToByteArray(order);
////                writeCharacteristic(writeCharacteristic, array);
////            } else {
////                SendOrderRunnable myRunnable = new SendOrderRunnable(order, writeCharacteristic);
////                myRunnable.setSendOrderResult(sendOrderResult);
////                Thread thread = new Thread(myRunnable);
////                thread.start();
////
////            }
////        }
//
//    }


    private void writeOption(BluetoothGatt bluetoothGatt,BluetoothGattCharacteristic characteristic, String order){
        MyLog.debug(TAG, "writeOption: ");

        if (TextUtils.isEmpty(order)){
            MyLog.error(TAG, "writeOption:order is empty!!!" );
            return;
        }

        //对十六进制的数据进行处理
        order = order.replace(" ","");
        if (!Utils.isRightHexStr(order)){
            return;
        }

        //mohuaiyuan
//        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);


        MyLog.debug(TAG, "sdkInt: "+sdkInt);
        if (sdkInt >= 21) {
            byte[] array = Utils.hexStringToByteArray(order);
            writeCharacteristic(bluetoothGatt,characteristic, array);
        } else {
//            SendOrderRunnable myRunnable = new SendOrderRunnable(order, characteristic);
            SendOrderRunnable myRunnable = new SendOrderRunnable(order, characteristic,bluetoothGatt);
            myRunnable.setSendOrderResult(sendOrderResult);
            Thread thread = new Thread(myRunnable);
            thread.start();
        }

    }

    private void writeOption( String order) {
        MyLog.debug(TAG, "writeOption: ");

        if (TextUtils.isEmpty(order)) {
            MyLog.error(TAG, "writeOption:order is empty!!!");
            return;
        }

        //对十六进制的数据进行处理
        order = order.replace(" ", "");
        if (!Utils.isRightHexStr(order)) {
            return;
        }

        //mohuaiyuan
//        writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

        Map<String,Map<Integer,BluetoothGattCharacteristic>> currentCharacteristic =ConnectionInfoCollector.getCurrentCharacteristic();
        Iterator<String> iterator = currentCharacteristic.keySet().iterator();
        BluetoothGatt bluetoothGatt;
        BluetoothGattCharacteristic bluetoothGattCharacteristic;
        while (iterator.hasNext()) {
            String deviceAddress = iterator.next();
            Log.d(TAG, "deviceAddress: "+deviceAddress);
            bluetoothGatt=ConnectionInfoCollector.getBluetoothGattMap().get(deviceAddress);
            Map<Integer, BluetoothGattCharacteristic> map = currentCharacteristic.get(deviceAddress);
            bluetoothGattCharacteristic = map.get(CHARACTERISTIC_TYPE_WRITE);
            writeOption(bluetoothGatt,bluetoothGattCharacteristic,order);

        }
    }

    private void notifyOption(){
        MyLog.debug(TAG, "notifyOption: ");
        nofityEnable=!nofityEnable;

        if (!nofityEnable){
            //mohuaiyuan 201708
//            stopBroadcastDataNotify(notifyCharacteristic);
            stopBroadcastDataNotify();
        }else {
            //mohuaiyuan 201708
//            prepareBroadcastDataNotify(notifyCharacteristic);
            prepareBroadcastDataNotify();
        }
        updateNotifyText(nofityEnable);
    }

    private void updateNotifyText(boolean isNotify){
        if (!isNotify){
            btnNotify.setText(getStringById(R.string.notify));
        }else {
            btnNotify.setText(getStringById(R.string.stop_notify));
        }
    }


    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    private void prepareBroadcastDataNotify(BluetoothGattCharacteristic characteristic) {
        MyLog.debug(TAG, "prepareBroadcastDataNotify: ");
        boolean response=false;
        final int charaProp = characteristic.getProperties();
        MyLog.debug(TAG, "charaProp: "+charaProp);
        int temp=charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY;
        MyLog.debug(TAG, "temp: "+temp);
        if ( temp> 0) {
            response=  BluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
        MyLog.debug(TAG, "response: "+response);

    }

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics.
     *
     * @param characteristic
     */
    private void prepareBroadcastDataNotify(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic) {
        MyLog.debug(TAG, "prepareBroadcastDataNotify: ");
        boolean response=false;
        final int charaProp = characteristic.getProperties();
        MyLog.debug(TAG, "charaProp: "+charaProp);
        int temp=charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY;
        MyLog.debug(TAG, "temp: "+temp);
        if ( temp> 0) {
            response=  BluetoothLeService.setCharacteristicNotification(bluetoothGatt,characteristic, true);
        }
        MyLog.debug(TAG, "response: "+response);

    }

    private void prepareBroadcastDataNotify(){
        MyLog.debug(TAG, "prepareBroadcastDataNotify: ");

        Map<String,Map<Integer,BluetoothGattCharacteristic>> currentCharacteristic =ConnectionInfoCollector.getCurrentCharacteristic();
        Iterator<String> iterator = currentCharacteristic.keySet().iterator();
        BluetoothGatt bluetoothGatt;
        BluetoothGattCharacteristic bluetoothGattCharacteristic;
        while (iterator.hasNext()) {
            String deviceAddress = iterator.next();
            Log.d(TAG, "deviceAddress: "+deviceAddress);
            bluetoothGatt=ConnectionInfoCollector.getBluetoothGattMap().get(deviceAddress);
            Map<Integer, BluetoothGattCharacteristic> map = currentCharacteristic.get(deviceAddress);
            bluetoothGattCharacteristic = map.get(CHARACTERISTIC_TYPE_NOTIFY);
            prepareBroadcastDataNotify(bluetoothGatt,bluetoothGattCharacteristic);
        }

    }

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    private void stopBroadcastDataNotify(BluetoothGattCharacteristic characteristic) {
        MyLog.debug(TAG, "stopBroadcastDataNotify: ");
        boolean response = false;
        final int charaProp = characteristic.getProperties();
        MyLog.debug(TAG, "charaProp: " + charaProp);
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            response = BluetoothLeService.setCharacteristicNotification(characteristic, false);
        }
        MyLog.debug(TAG, "result: " + response);
    }

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    private void stopBroadcastDataNotify(BluetoothGatt bluetoothGatt,BluetoothGattCharacteristic characteristic) {
        MyLog.debug(TAG, "stopBroadcastDataNotify: ");
        boolean response = false;
        final int charaProp = characteristic.getProperties();
        MyLog.debug(TAG, "charaProp: " + charaProp);
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            response = BluetoothLeService.setCharacteristicNotification(bluetoothGatt,characteristic, false);
        }
        MyLog.debug(TAG, "result: " + response);
    }

    private void stopBroadcastDataNotify(){

        Map<String,Map<Integer,BluetoothGattCharacteristic>> currentCharacteristic =ConnectionInfoCollector.getCurrentCharacteristic();
        Iterator<String> iterator = currentCharacteristic.keySet().iterator();
        BluetoothGatt bluetoothGatt;
        BluetoothGattCharacteristic bluetoothGattCharacteristic;
        while (iterator.hasNext()) {
            String deviceAddress = iterator.next();
            Log.d(TAG, "deviceAddress: "+deviceAddress);
            bluetoothGatt=ConnectionInfoCollector.getBluetoothGattMap().get(deviceAddress);
            Map<Integer, BluetoothGattCharacteristic> map = currentCharacteristic.get(deviceAddress);
            bluetoothGattCharacteristic = map.get(CHARACTERISTIC_TYPE_NOTIFY);
            stopBroadcastDataNotify(bluetoothGatt,bluetoothGattCharacteristic);
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic){
        BluetoothLeService.readCharacteristic(characteristic);
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        Log.d(TAG, "writeCharacteristic: ");
        // Writing the hexValue to the characteristics
        try {
            BluetoothLeService.writeCharacteristicGattDb(characteristic, bytes);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void writeCharacteristic(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic, byte[] bytes) {
        Log.d(TAG, "writeCharacteristic: ");
        // Writing the hexValue to the characteristics
        try {
            BluetoothLeService.writeCharacteristicGattDb(bluetoothGatt,characteristic, bytes);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }



    private void initCharacteristicWithSingle(){
        MyLog.debug(TAG, "initCharacteristic: ");

        BluetoothGattCharacteristic characteristic = myApplication.getCharacteristic();

        List<BluetoothGattCharacteristic> characs = ((MyApplication)getApplication()).getCharacteristics();
        for(int i=0;i<characs.size();i++){
            BluetoothGattCharacteristic charac=characs.get(i);

            MyLog.debug(TAG, "characteristic UUID: "+charac.getUuid().toString());
            MyLog.debug(TAG, "characteristic Type: "+charac.getProperties());
        }

        List<BluetoothGattCharacteristic> characteristics = ((MyApplication)getApplication()).getCharacteristics();
        for (BluetoothGattCharacteristic c :characteristics){
            if (Utils.getProperties(context,c).equals("Notify")){
                MyLog.debug(TAG, "there is a notify characteristics............ : ");
                notifyCharacteristic = c;
                continue;
            }

            if (Utils.getProperties(context,c).equals("Write")){
                MyLog.debug(TAG, "there is a write characteristics............ : ");
                writeCharacteristic = c;
                continue;
            }
        }

        if(notifyCharacteristic==null ){
            notifyCharacteristic = characteristic;
        }
        if(writeCharacteristic==null){
            writeCharacteristic=characteristic;
        }


    }

    //mohuaiyuan 201708   Original code
//    private void initCharacteristics(){
//        MyLog.debug(TAG, "initCharacteristics: ");
//        BluetoothGattCharacteristic characteristic = myApplication.getCharacteristic();
//        List<BluetoothGattCharacteristic> characs = ((MyApplication)getApplication()).getCharacteristics();
//        for(int i=0;i<characs.size();i++){
//            BluetoothGattCharacteristic charac=characs.get(i);
//
//            MyLog.debug(TAG, "characteristic UUID: "+charac.getUuid().toString());
//            MyLog.debug(TAG, "characteristic Type: "+charac.getProperties());
//        }
//
//        if (characteristic.getUuid().toString().equals(GattAttributes.USR_SERVICE)){
//            isDebugMode=true;
//
//            List<BluetoothGattCharacteristic> characteristics = ((MyApplication)getApplication()).getCharacteristics();
//
//            for (BluetoothGattCharacteristic c :characteristics){
//                if (Utils.getProperties(context,c).equals("Notify")){
//                    MyLog.debug(TAG, "there is a notify characteristics............ : ");
//                    notifyCharacteristic = c;
//                    continue;
//                }
//
//                if (Utils.getProperties(context,c).equals("Write")){
//                    MyLog.debug(TAG, "there is a write characteristics............ : ");
//                    writeCharacteristic = c;
//                    continue;
//                }
//            }
//
////            properties = "Notify & Write";
//
//        }else {
////            properties = Utils.getProperties(this, characteristic);
//            MyLog.debug(TAG, "write  and notify  are the same characteristics............ : ");
//            notifyCharacteristic = characteristic;
//            readCharacteristic = characteristic;
//            writeCharacteristic = characteristic;
////            indicateCharacteristic = characteristic;
//        }
//    }

    private void initCharacteristicWithMultiple(){
        MyLog.debug(TAG, "initCharacteristicWithMultiple: ");

        Map<String, List<BluetoothGattCharacteristic>> map=ConnectionInfoCollector.getCharacteristicsMap();
        Iterator<String>iterator=map.keySet().iterator();

        while (iterator.hasNext()) {
            String deviceAddress = iterator.next();
            BluetoothGattCharacteristic characteristic = ConnectionInfoCollector.getCurrentCharacteristic().get(deviceAddress).get(CHARACTERISTIC_TYPE_WRITE);
            List<BluetoothGattCharacteristic> characteristics = map.get(deviceAddress);
            MyLog.debug(TAG, "deviceAddress : "+deviceAddress);

            for (BluetoothGattCharacteristic c : characteristics) {
                if (Utils.getProperties(context, c).equals("Notify")) {
                    MyLog.debug(TAG, "there is a notify characteristics............ : ");
                    MyLog.debug(TAG, "uuid : "+c.getUuid().toString()+" property:"+c.getProperties());
                    notifyCharacteristic = c;
                    ConnectionInfoCollector.getCurrentCharacteristic().get(deviceAddress).put(CHARACTERISTIC_TYPE_NOTIFY,c);
                    continue;
                }

                if (Utils.getProperties(context, c).equals("Write")) {
                    MyLog.debug(TAG, "there is a write characteristics............ : ");
                    MyLog.debug(TAG, "uuid : "+c.getUuid().toString()+" property:"+c.getProperties());
                    writeCharacteristic = c;
                    ConnectionInfoCollector.getCurrentCharacteristic().get(deviceAddress).put(CHARACTERISTIC_TYPE_WRITE,c);
                    continue;
                }
            }

            if (notifyCharacteristic == null) {
                MyLog.error(TAG, "notifyCharacteristic == null" );
                notifyCharacteristic = characteristic;
                ConnectionInfoCollector.getCurrentCharacteristic().get(deviceAddress).put(CHARACTERISTIC_TYPE_NOTIFY,characteristic);
            }
            if (writeCharacteristic == null) {
                MyLog.error(TAG, "writeCharacteristic == null " );
                writeCharacteristic = characteristic;
                ConnectionInfoCollector.getCurrentCharacteristic().get(deviceAddress).put(CHARACTERISTIC_TYPE_WRITE,characteristic);
            }

        }

    }

    private void requestMtu(int mtu) {
        MyLog.debug(TAG, "requestMtu: ");

        MyLog.debug(TAG, "sdkInt------------>" + sdkInt);

        Map<String,BluetoothGatt> bluetoothGattMap=ConnectionInfoCollector.getBluetoothGattMap();
        Iterator<String> iterator =bluetoothGattMap.keySet().iterator();
        BluetoothGatt bluetoothGatt;

        while (iterator.hasNext()) {
            String deveceAddress = iterator.next();
            bluetoothGatt=bluetoothGattMap.get(deveceAddress);

            requestMtu(bluetoothGatt,mtu);

        }

    }

    private void requestMtu(BluetoothGatt bluetoothGatt,int mtu ){
        MyLog.debug(TAG, "requestMtu: ");
        MyLog.debug(TAG, "sdkInt------------>"+sdkInt);
        if (sdkInt>=21){
            //设置最大发包、收包的长度为512个字节
            if(BluetoothLeService.requestMtu(bluetoothGatt,mtu)){
                MyLog.debug(TAG, "Max transmittal data is 512 ");
            }else{
                MyLog.debug(TAG, "Max transmittal data is 20 ");
            }

        }else {
            MyLog.debug(TAG, "Max transmittal data is 20 ");
//            Toast.makeText(this,getString(R.string.transmittal_length,"20"),Toast.LENGTH_LONG).show();
        }

    }

    private void stopNotifyOrIndicate(){
        if (nofityEnable){
            stopBroadcastDataNotify(notifyCharacteristic);
        }

//        if (indicateEnable)
//            stopBroadcastDataIndicate(indicateCharacteristic);
    }


    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            MyLog.debug(TAG, "Communicate mGattUpdateReceiver action: "+action);
            Bundle extras = intent.getExtras();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Data Received
                if (extras.containsKey(Constants.EXTRA_BYTE_VALUE)) {
                    if (extras.containsKey(Constants.EXTRA_BYTE_UUID_VALUE)) {
                        if (myApplication != null) {
                            BluetoothGattCharacteristic requiredCharacteristic = myApplication.getCharacteristic();
                            String uuidRequired = requiredCharacteristic.getUuid().toString();
                            String receivedUUID = intent.getStringExtra(Constants.EXTRA_BYTE_UUID_VALUE);

                            if (isDebugMode){
                                byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                                MyLog.debug(TAG, "array : "+array.toString());
                            }else if (uuidRequired.equalsIgnoreCase(receivedUUID)) {
                                byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                                MyLog.debug(TAG, "array : "+array.toString());
                            }
                        }
                    }
                }

            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopNotifyOrIndicate();
        unregisterReceiver(mGattUpdateReceiver);
    }
}
