package com.example.yf_04.bluetoothtest;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.example.yf_04.bluetoothtest.adapter.ActionAdapter;
import com.example.yf_04.bluetoothtest.bean.BasicAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.login.LoginException;

public class Communicate extends AppCompatActivity {

    private static final String TAG = "Communicate";

   /* private Button standInSitu;
    private Button treadOnTheGround;
    private Button walkForward;
    private Button walkBackwards;

    private Button theSideWalk;
    private Button inSituSquatDown;
    private Button fromSquatDownToStand;
    private Button placeToSitDown;

    private Button fromSittingToStanding;
    private Button placeToLieDown;
    private Button fromLieDownToStand;
    private Button putDown;

    private Button fromTheGroundToTheStation;
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
    private Button toBeContinue;*/




    private BluetoothGattCharacteristic writeCharacteristic;
    private MyApplication myApplication;
    private Context context;

    private int sdkInt;

    private List<BasicAction> list=new ArrayList<BasicAction>();

    private RecyclerView recyclerView ;
    private ActionAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.communicate_recycle_layout);

        Log.d(TAG, "onCreate: ");
        context=this;

        myApplication = (MyApplication) getApplication();
        initCharacteristics();

        initUI();

        initData();

//        LinearLayoutManager manager=new LinearLayoutManager(context);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter=new ActionAdapter(context,list);
        recyclerView.setAdapter(adapter);


        initListener();

        requestMtu();

    }

    private void initData()  {
        Log.d(TAG, "initData: ");

        InputStream is=null;
        BufferedReader br=null;

        try {
            is= context.getAssets().open("actionInfo");
            br=new BufferedReader(new InputStreamReader(is));
            String line="";
            while ((line=br.readLine())!=null){
                String[] array=line.split(",");
                Log.d(TAG, "array: "+ Arrays.toString(array));
                BasicAction basicAction=new BasicAction();
                basicAction.setId(Integer.valueOf(array[0].trim()));
                basicAction.setName(array[1].trim());
                basicAction.setTextId(array[2].trim());
                basicAction.setOrderId(array[3].trim());
                list.add(basicAction);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void initListener() {
        Log.d(TAG, "initListener: ");
       /* standInSitu.setOnClickListener(myOnClickListener);
        treadOnTheGround.setOnClickListener(myOnClickListener);
        walkForward.setOnClickListener(myOnClickListener);
        walkBackwards.setOnClickListener(myOnClickListener);

        theSideWalk.setOnClickListener(myOnClickListener);
        inSituSquatDown.setOnClickListener(myOnClickListener);
        fromSquatDownToStand.setOnClickListener(myOnClickListener);
        placeToSitDown.setOnClickListener(myOnClickListener);

        fromSittingToStanding.setOnClickListener(myOnClickListener);
        placeToLieDown.setOnClickListener(myOnClickListener);
        fromLieDownToStand.setOnClickListener(myOnClickListener);
        putDown.setOnClickListener(myOnClickListener);

        fromTheGroundToTheStation.setOnClickListener(myOnClickListener);
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
        toBeContinue.setOnClickListener(myOnClickListener);*/

       adapter.setMyOnItemClickListener(new ActionAdapter.MyOnItemClickListener() {
           @Override
           public void OnItemClickListener(View view, int position) {
               Log.d(TAG, "onItemClick: ");
               BasicAction basicAction= list.get(position);
               String name=basicAction.getOrderId();
               Log.d(TAG, "name: "+name);
               String order=Utils.getStringByName(context,name);
               Log.d(TAG, "order: "+order);

               writeOption(order);
           }
       });


    }

    private void initUI() {
        Log.d(TAG, "initUI: ");

  /*      standInSitu= (Button) findViewById(R.id.standInSitu);
        treadOnTheGround= (Button) findViewById(R.id.treadOnTheGround);
        walkForward= (Button) findViewById(R.id.walkForward);
        walkBackwards= (Button) findViewById(R.id.walkBackwards);

        theSideWalk= (Button) findViewById(R.id.theSideWalk);
        inSituSquatDown= (Button) findViewById(R.id.inSituSquatDown);
        fromSquatDownToStand= (Button) findViewById(R.id.fromSquatDownToStand);
        placeToSitDown= (Button) findViewById(R.id.placeToSitDown);

        fromSittingToStanding=(Button) findViewById(R.id.fromSittingToStanding);
        placeToLieDown=(Button) findViewById(R.id.placeToLieDown);
        fromLieDownToStand=(Button) findViewById(R.id.fromLieDownToStand);
        putDown=(Button) findViewById(R.id.putDown);

        fromTheGroundToTheStation=(Button) findViewById(R.id.fromTheGroundToTheStation);
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
        toBeContinue=(Button) findViewById(R.id.toBeContinue);*/

        recyclerView= (RecyclerView) findViewById(R.id.actionRecycleView);

    }

/*    private View.OnClickListener myOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "myOnClickListener onClick: ");

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


                case R.id.theSideWalk:
                    writeOption(Orders.THE_SIDE_WALK);
                    break;

                case R.id.inSituSquatDown:
                    writeOption(Orders.IN_SITU_SQUAT_DOWN);
                    break;

                case R.id.fromSquatDownToStand:
                    writeOption(Orders.FROM_SQAT_DOWN_TO_STAND);
                    break;

                case R.id.placeToSitDown:
                    writeOption(Orders.PLACE_TO_SIT_DOWN);
                    break;


                case R.id.fromSittingToStanding:
                    writeOption(Orders.FROM_SITTING_TO_STANDING);
                    break;

                case R.id.placeToLieDown:
                    writeOption(Orders.PLACE_TO_LIE_DOWN);
                    break;

                case R.id.fromLieDownToStand:
                    writeOption(Orders.FROM_LIE_DOWN_TO_STAND);
                    break;

                case R.id.putDown:
                    writeOption(Orders.PUT_DOWN);
                    break;


                case R.id.fromTheGroundToTheStation:
                    writeOption(Orders.FROM_THE_GROUND_TO_THE_STATION);
                    break;

                case R.id.bowOnesHead:
                    writeOption(Orders.BOW_ONES_HEAD);
                    break;

                case R.id.aWordHorse:
                    writeOption(Orders.A_WORD_HORSE);
                    break;

                case R.id.stance:
                    writeOption(Orders.STANCE);
                    break;


                case R.id.beforeTheLegPress:
                    writeOption(Orders.BEFORE_THE_LEG_PRESS);
                    break;

                case R.id.sideLegPress:
                    writeOption(Orders.SIDE_LEG_PRESS);
                    break;

                case R.id.chestOut:
                    writeOption(Orders.CHEST_OUT);
                    break;

                case R.id.stoop:
                    writeOption(Orders.STOOP);
                    break;


                case R.id.lookUp:
                    writeOption(Orders.LOOK_UP);
                    break;

                case R.id.inSituTurning:
                    writeOption(Orders.IN_SITU_TURNING);
                    break;

                case R.id.takeARightTurn:
                    writeOption(Orders.TAKE_A_RIGHT_TURN);
                    break;

                case R.id.lieOnYourStomachAndDoPushUps:
                    writeOption(Orders.LIE_ON_YOU_STOMACH_AND_DO_PUSH_UPS);
                    break;


                case R.id.liftMyLeftArm:
                    writeOption(Orders.LIFT_MY_LEFT_ARM);
                    break;

                case R.id.liftMyRightArm:
                    writeOption(Orders.LIFT_MY_RIGHT_ARM);
                    break;

                case R.id.wavingYourLeftArm:
                    writeOption(Orders.WAVING_YOU_LEFT_ARM);
                    break;

                case R.id.wavingYouRightArm:
                    writeOption(Orders.WAVING_YOU_RIGHT_ARM);
                    break;


                case R.id.stretchYouLeftArm:
                    writeOption(Orders.STRETCH_YOU_LEFT_ARM);
                    break;

                case R.id.stretchYouRightArm:
                    writeOption(Orders.STRETCH_YOU_RIGHT_ARM);
                    break;

                case R.id.playBasketball:
                    writeOption(Orders.PLAY_BASKETBALL);
                    break;

                case R.id.toBeContinue:
                    Toast.makeText(context, context.getString(R.string.to_be_continue), Toast.LENGTH_SHORT).show();
                    break;

                default:

            }
        }
    };*/
    
    
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

        if (sdkInt >= 21) {
            byte[] array = Utils.hexStringToByteArray(order);
            writeCharacteristic(writeCharacteristic, array);
        } else {
            MyRunnable myRunnable=new MyRunnable(order,writeCharacteristic);
            Thread thread = new Thread(myRunnable);
            thread.start();

        }


    }

    /**
     * Send data to bluetooth
     */
    class MyRunnable implements Runnable{
        private volatile String data;
        private volatile BluetoothGattCharacteristic characteristic;
        /**
         * the time interval of send data(ms)
         */
        private static final long SEND_INTERVAL=40;

        /**
         *
         */
        private static final int DATA_UNIT=20;

        public MyRunnable(){

        }
        public MyRunnable(String data,BluetoothGattCharacteristic characteristic){
            this.data=data;
            this.characteristic=characteristic;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
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

            synchronized (this){

                if(!isLegal()){
                    Log.e(TAG, "myRunnable init illegal" );
                    return;
                }
                data=data.replace(" ","");

                int length =data.length();
                int sendCount=length / DATA_UNIT;
                int remainde=length % DATA_UNIT;
                if(remainde!=0){
                    sendCount++;
                }
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
                    try {
                        BluetoothLeService.writeCharacteristicGattDb(characteristic, array);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(SEND_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }


            }

        }
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        // Writing the hexValue to the characteristics
        try {
            BluetoothLeService.writeCharacteristicGattDb(characteristic, bytes);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void initCharacteristics(){
        Log.d(TAG, "initCharacteristics: ");
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
        Log.d(TAG, "requestMtu: ");
         sdkInt = Build.VERSION.SDK_INT;
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
