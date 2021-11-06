package com.example.boundservice26082021;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {

    OnListenerCountChange onListenerCountChange;
    Notification notification;
    NotificationManager notificationManager;
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
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = createNotification(0);
        startForeground(1,notification);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("BBB","onUnbind");
        onListenerCountChange = null;
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("BBB","onRebind");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BBB","onStartCommand");
        if (!isRunning) {
            Handler handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    if (msg.what == 1){
                        notification = createNotification((Integer) msg.obj);
                        notificationManager.notify(1,notification);
                    }
                    return false;
                }
            });
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    isRunning = true;
                    for (int i = 1; i < 100; i++) {
                        if(onListenerCountChange != null){
                            onListenerCountChange.onChanged(i);
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.what = 1;
                        message.obj = i;
                        handler.dispatchMessage(message);
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

    public Notification createNotification(int count){
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");
        builder.setSmallIcon(android.R.drawable.ic_dialog_email);
        builder.setShowWhen(true);
        builder.setContentTitle("Xử lý data");
        builder.setContentText("Count " + count);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID", "CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        return builder.build();
    }
}
