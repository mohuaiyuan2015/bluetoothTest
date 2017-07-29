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
import com.example.yf_04.bluetoothtest.designpattern.SdkStateContext;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class Communicate extends AppCompatActivity {

    private static final String TAG = "Communicate";

    private Button standInSitu;
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
    private Button toBeContinue;




    private BluetoothGattCharacteristic writeCharacteristic;
    private MyApplication myApplication;
    private Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.communicate_layout);

        context=this;

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
        toBeContinue.setOnClickListener(myOnClickListener);

    }

    private void initUI() {

        standInSitu= (Button) findViewById(R.id.standInSitu);
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
        toBeContinue=(Button) findViewById(R.id.toBeContinue);


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
    };
    
    
    private void writeOption(String order){

        //TODO
        SdkStateContext stateContext=new SdkStateContext();
        stateContext.getState().doAction(writeCharacteristic,order);

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

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] bytes) {
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
