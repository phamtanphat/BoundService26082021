package com.example.boundservice26082021;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyService extends Service {

    OnListenerCountChange onListenerCountChange;

    class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    boolean isRunning;
    Thread thread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("BBB","onBind");
        return new MyBinder();
    }


    @Override
    public void onCreate() {
        Log.d("BBB","onCreate");
        super.onCreate();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Log.d("BBB","unBindservice");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BBB","onStartCommand");
        if (!isRunning) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    isRunning = true;
                    for (int i = 0; i < 50; i++) {
                        if(onListenerCountChange != null){
                            onListenerCountChange.onChanged(i);
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            thread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("BBB","onDestroy");
        super.onDestroy();
    }

    interface OnListenerCountChange{
        void onChanged(int count);
    }

    public void setOnListenerCountChange(OnListenerCountChange onListenerCountChange){
        this.onListenerCountChange = onListenerCountChange;
    }
}
