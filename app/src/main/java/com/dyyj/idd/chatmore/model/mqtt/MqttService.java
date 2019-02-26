package com.dyyj.idd.chatmore.model.mqtt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.dyyj.idd.chatmore.BuildConfig;
import com.dyyj.idd.chatmore.app.ChatApp;

import java.util.ArrayList;
import java.util.List;

public class MqttService extends Service implements MqttListener {

  private static ManageMqtt mMyMqtt;
  private static List<MqttListener> mMqttListenerList = new ArrayList<>();
  private static final String TAG = "MQTT";

  public static void start(Context context) {

    Intent lLinphoneServiceIntent = new Intent(Intent.ACTION_MAIN);
    lLinphoneServiceIntent.setClass(context, MqttService.class);
    context.startService(lLinphoneServiceIntent);
    //Compatibility.startService(context, lLinphoneServiceIntent);
    //Intent intent = new Intent();
    //intent.setClassName(context.getPackageName(), "com.dyyj.idd.chatmore.receiver.BootReceiver");
    //context.sendBroadcast(intent);
    //Compatibility.startService(context, starter);
    //Intent starter = new Intent(context, MqttService.class);
    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    //  context.startForegroundService(starter);
    //} else {
    //  context.startService(starter);
    //}
  }

  public static void stop(Context context) {
    Intent starter = new Intent(context, MqttService.class);
    context.stopService(starter);
  }

  @Override public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "onCreate: ");
    }

    int NOTIFICATION_ID = (int) (System.currentTimeMillis()%10000);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel notificationChannel = new NotificationChannel(getPackageName(), "Channel 1", NotificationManager.IMPORTANCE_HIGH);
      notificationChannel.enableLights(true);
      notificationChannel.setLightColor(Color.RED);
      notificationChannel.setShowBadge(true);
      notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
      NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      manager.createNotificationChannel(notificationChannel);
      Notification notification = new Notification.Builder(this).setChannelId(getPackageName()).build();
      startForeground(NOTIFICATION_ID, notification);
    }

    if (mMyMqtt == null) {
      mMyMqtt = new ManageMqtt(this);
    }

    mMyMqtt.connectMqtt();
    mMyMqtt.connect();
  }

  @Override public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override public void onDestroy() {
    super.onDestroy();
    ChatApp.Companion.getInstance().getDataRepository().stopHeardSocket();
    mMyMqtt.disConnectMqtt();
    mMqttListenerList.clear();
    mMyMqtt = null;

  }

  public static ManageMqtt getMyMqtt() {
    return mMyMqtt;
  }

  public static void addMqttListener(MqttListener listener) {
    if (!mMqttListenerList.contains(listener)) {
      mMqttListenerList.add(listener);
    }
  }

  public static void removeMqttListener(MqttListener listener) {
    mMqttListenerList.remove(listener);
  }

  @Override public void onConnected() {
    for (MqttListener mqttListener : mMqttListenerList) {
      mqttListener.onConnected();
    }
  }

  @Override public void onFail() {
    new Thread(() -> {
      if (mMyMqtt != null) {
        mMyMqtt.connectMqtt();
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

    }).start();
    for (MqttListener mqttListener : mMqttListenerList) {
      mqttListener.onFail();
    }
  }

  @Override public void onLost() {
    new Thread(() -> {
      if (mMyMqtt != null) {
        mMyMqtt.connectMqtt();
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

    }).start();
    for (MqttListener mqttListener : mMqttListenerList) {
      mqttListener.onLost();
    }
  }

  @Override public void onReceive(String message) {
    for (MqttListener mqttListener : mMqttListenerList) {
      mqttListener.onReceive(message);
    }
  }

  @Override public void onSendSucc() {
    for (MqttListener mqttListener : mMqttListenerList) {
      mqttListener.onSendSucc();
    }
  }


}
