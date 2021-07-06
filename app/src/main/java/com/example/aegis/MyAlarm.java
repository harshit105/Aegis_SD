package com.example.aegis;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyAlarm extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        MediaPlayer mediaPlayer=MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();
        String Title= intent.getStringExtra("title");
        String Description=intent.getStringExtra("Description");
//        String ID=intent.getStringExtra("id");
//        int ID2=intent.getIntExtra("id",11);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=
                    new NotificationChannel("MyNotifications","MyNotifications",NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager=context.getSystemService(NotificationManager.class);
            try {
                manager.createNotificationChannel(channel);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("NOTIFICATION MANAGER",e.getMessage());
            }

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"MyNotifications")
                .setContentTitle(Title)
                .setTicker("ticker")
                .setSmallIcon(R.drawable.ic_daily_schedule)
                .setAutoCancel(true)
                .setContentText(Description);

        NotificationManagerCompat manager=NotificationManagerCompat.from(context);
        manager.notify(999,builder.build());

    }
}
