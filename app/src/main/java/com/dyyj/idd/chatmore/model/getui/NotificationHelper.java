/*
* Copyright 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.dyyj.idd.chatmore.model.getui;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.dyyj.idd.chatmore.R;

/**
 * Helper class to manage notification channels, and create notifications.
 */
public class NotificationHelper extends ContextWrapper {
  private NotificationManager manager;
  public static final String PRIMARY_CHANNEL = "default";
  public static final String SECONDARY_CHANNEL = "second";
  private NotificationManagerCompat compat;

  /**
   * Registers notification channels, which can be used later by individual notifications.
   *
   * @param ctx The application context
   */
  @SuppressLint("WrongConstant") public NotificationHelper(Context ctx) {
    super(ctx);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel chan1 = null;
      chan1 = new NotificationChannel(PRIMARY_CHANNEL, "Primary Channel",
          NotificationManager.IMPORTANCE_DEFAULT);
      chan1.setLightColor(Color.GREEN);
      chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
      getManager().createNotificationChannel(chan1);
    }

    NotificationChannel chan2 = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      chan2 = new NotificationChannel(SECONDARY_CHANNEL, "Primary Channel",
          NotificationManager.IMPORTANCE_HIGH);
      chan2.setLightColor(Color.BLUE);
      chan2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
      getManager().createNotificationChannel(chan2);
    }

    compat = NotificationManagerCompat.from(this);
  }

  /**
   * Send Intent to load system Notification Settings for this app.
   */
  public void goToNotificationSettings() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Intent i = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
      i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
      i.putExtra(Settings.EXTRA_CHANNEL_ID, NotificationHelper.PRIMARY_CHANNEL);
      startActivity(i);
    } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Intent intent = new Intent();
      intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
      intent.putExtra("app_package", getPackageName());
      intent.putExtra("app_uid", getApplicationInfo().uid);
      startActivity(intent);
    } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
      Intent intent = new Intent();
      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
      intent.addCategory(Intent.CATEGORY_DEFAULT);
      intent.setData(Uri.parse("package:" + getPackageName()));
      startActivity(intent);
    }
  }

  /**
   * Send intent to load system Notification Settings UI for a particular channel.
   *
   * @param channel Name of channel to configure
   */
  public void goToNotificationSettings(String channel) {
    Intent i = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
    i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
    i.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
    startActivity(i);
  }

  public NotificationCompat.Builder getNotification(String title, String body) {
    return new NotificationCompat.Builder(this, PRIMARY_CHANNEL).setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
  }

  /**
   * Get a notification of type 1
   * <p>
   * Provide the builder rather than the notification it's self as useful for making notification
   * changes.
   *
   * @param title the title of the notification
   * @param body the body text for the notification
   * @return the builder as it keeps a reference to the notification (since API 24)
   */
  @RequiresApi(api = Build.VERSION_CODES.O) public Notification.Builder getNotification1(
      String title, String body) {
    return new Notification.Builder(getApplicationContext(), PRIMARY_CHANNEL).setContentTitle(title)
        .setContentText(body)
        .setSmallIcon(getSmallIcon())
        .setAutoCancel(true);
  }

  /**
   * Build notification for secondary channel.
   *
   * @param title Title for notification.
   * @param body Message for notification.
   * @return A Notification.Builder configured with the selected channel and details
   */
  @RequiresApi(api = Build.VERSION_CODES.O) public Notification.Builder getNotification2(
      String title, String body) {
    return new Notification.Builder(getApplicationContext(), SECONDARY_CHANNEL).setContentTitle(
        title).setContentText(body).setSmallIcon(getSmallIcon()).setAutoCancel(true);
  }

  /**
   * Send a notification.
   *
   * @param id The ID of the notification
   * @param notification The notification object
   */
  public void notify(int id, Notification.Builder notification) {
    getManager().notify(id, notification.build());
  }

  public void notify(int id, NotificationCompat.Builder notification) {
    compat.notify(id, notification.build());
  }

  /**
   * Get the small icon for this app
   *
   * @return The small icon resource id
   */
  private int getSmallIcon() {
    return android.R.drawable.stat_notify_chat;
  }

  /**
   * Get the notification manager.
   * <p>
   * Utility method as this helper works with it a lot.
   *
   * @return The system service NotificationManager
   */
  private NotificationManager getManager() {
    if (manager == null) {
      manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
    return manager;
  }
}
