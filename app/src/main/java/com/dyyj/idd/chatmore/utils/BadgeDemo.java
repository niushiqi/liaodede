package com.dyyj.idd.chatmore.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity;


/**
 * Created by wangbin on 2019/2/20.
 */

public class BadgeDemo {
    /*private MainActivity mSelfActivity = MainActivity.this;

    private TextView mShowBadge;

    // 模拟接收消息

    private Handler mBadgeHandler = new Handler();

    private int mBadgeCount = 1;

    private static final String CHANNEL_ID = "heliquan";

    private static final long[] VIBRATION_PATTERN = new long[]{ 0, 180, 80, 120};

    Runnable runnable = new Runnable() {

        @Override
        public void run(){

            int tempNUm = mBadgeCount++;

            mShowBadge.setText( "当前未读消息为："+ tempNUm);

            Log.e( "Love", "当前未读消息为："+ tempNUm);

            if(Build.MANUFACTURER.equalsIgnoreCase( "xiaomi")) {

                PackageManager pm = getPackageManager();

                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);

                PendingIntent pendingIntent = PendingIntent.getActivity(

                        mSelfActivity, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mSelfActivity, CHANNEL_ID)

                        .setSmallIcon(getApplicationInfo().icon)

                        .setContentTitle(pm.getApplicationLabel(getApplicationInfo()).toString())

                        .setTicker( "Ticker:"+ tempNUm)

                        .setContentText( "贺贺，我是第"+ tempNUm + "个")

                        .setWhen(System.currentTimeMillis())

                        .setAutoCancel( true)

                        .setContentIntent(pendingIntent);

                Notification notification = builder.build();

                BadgeUtils.getBadgeOfMINU(notification, tempNUm);

                notificationManager.notify( 0, notification);

            } else{

                BadgeUtils.setBadgeCount(mSelfActivity, tempNUm);

            }

            mBadgeHandler.postDelayed( this, 1500);

        }

    };

    private NotificationManager notificationManager;

    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initNotification();

        initView();

    }

    private void initNotification(){

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= 26) {

            // Create the notification channel for Android 8.0

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "test for He.", NotificationManager.IMPORTANCE_DEFAULT);

            channel.setVibrationPattern(VIBRATION_PATTERN);

            notificationManager.createNotificationChannel(channel);

        }

        packageName = getApplicationInfo().packageName;

    }

    private void initView(){

        mShowBadge = findViewById(R.id.showBadgeNum);

        initViewData();

    }

    private void initViewData(){

        mBadgeHandler.postDelayed(runnable, 1500);

    }*/
}
