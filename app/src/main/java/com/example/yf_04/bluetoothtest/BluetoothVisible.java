package com.example.yf_04.bluetoothtest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.yf_04.bluetoothtest.Utils.MyUtils;

import java.lang.reflect.Method;

public class BluetoothVisible extends Activity {

    private static final String TAG = "BluetoothVisible";

    private Context context;

    private Button open;
    private Button close;

    private MyUtils myUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bluetooth_visible);
        context=this;

        myUtils=new MyUtils();
        
        initUI();
        
        initListener();


    }

    private void initListener() {
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "open onClick: ");
                myUtils.setDiscoverableTimeout(120);

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "close onClick: ");
                myUtils.closeDiscoverableTimeout();

            }
        });

    }

    private void initUI() {
        open= (Button) findViewById(R.id.open);
        close= (Button) findViewById(R.id.close);
    }



}
