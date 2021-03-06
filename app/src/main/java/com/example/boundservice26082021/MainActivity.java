package com.example.boundservice26082021;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button mBtnStartService,mBtnunBindService;
    TextView mTvCount;
    boolean mBound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnStartService = findViewById(R.id.buttonStartService);
        mBtnunBindService = findViewById(R.id.buttonUnBindService);
        mTvCount = findViewById(R.id.textViewCount);

        mBtnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MyService.class);
                startService(intent);
                bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });

        mBtnunBindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound){
                    unbindService(serviceConnection);
                    mBound = false;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isMyServiceRunning(MyService.class)){
            Intent intent = new Intent(MainActivity.this,MyService.class);
            bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            MyService myService = myBinder.getService();
            myService.setOnListenerCountChange(new MyService.OnListenerCountChange() {
                @Override
                public void onChanged(int count) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvCount.setText("Count = " + count);
                        }
                    });
                }
            });
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };
}