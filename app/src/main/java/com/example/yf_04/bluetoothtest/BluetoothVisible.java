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

import java.lang.reflect.Method;

public class BluetoothVisible extends Activity {

    private static final String TAG = "BluetoothVisible";

    private Context context;

    private Button open;
    private Button close;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bluetooth_visible);
        context=this;
        
        initUI();
        
        initListener();


    }

    private void initListener() {
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "open onClick: ");
                setDiscoverableTimeout(120);

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "close onClick: ");
                closeDiscoverableTimeout();

            }
        });

    }

    private void initUI() {
        open= (Button) findViewById(R.id.open);
        close= (Button) findViewById(R.id.close);
    }

    public void setDiscoverableTimeout(int timeout) {
        Log.d(TAG, "setDiscoverableTimeout: ");
        BluetoothAdapter adapter= BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, timeout);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDiscoverableTimeout() {
        Log.d(TAG, "closeDiscoverableTimeout: ");
        BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, 1);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
