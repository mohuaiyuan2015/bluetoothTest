package com.example.yf_04.bluetoothtest.Utils;

import android.util.Log;

/**
 * Created by YF-04 on 2017/8/7.
 */

public class MyLog {

    private static int DISPLAY_LEVEL=3;

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;



    public void v(String tag,String msg){
        if(DISPLAY_LEVEL<=VERBOSE){
            Log.v(tag,msg);
        }
    }
    public static void d(String tag,String msg){
        if(DISPLAY_LEVEL<=DEBUG){
            Log.d(tag,msg);
        }
    }
    public static void i(String tag,String msg){
        if(DISPLAY_LEVEL<=INFO){
            Log.i(tag,msg);
        }
    }
    public static void w(String tag,String msg){
        if(DISPLAY_LEVEL<=WARN){
            Log.w(tag,msg);
        }
    }
    public static void e(String tag,String msg){
        if(DISPLAY_LEVEL<=ERROR){
            Log.e(tag,msg);
        }
    }


}
