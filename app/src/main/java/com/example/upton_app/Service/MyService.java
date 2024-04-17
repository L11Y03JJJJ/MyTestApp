package com.example.upton_app.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MyService extends Service {
    private final  String TAG="LLL3";
    private Socket socket;



    @Override
    public void onCreate() {
        super.onCreate();
        //Log.d(TAG,"onCreate");

    }

    @Override
    public void onStart(Intent intent, int startId) {//过期的
        super.onStart(intent, startId);
        //Log.d(TAG,"onStart");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //相当于代替了onstart函数
        //Log.d(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");

        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"onUnbind");

        return super.onUnbind(intent);
    }

}
