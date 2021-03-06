package com.dyyj.idd.chatmore.receiver;

/*
BootReceiver.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.dyyj.idd.chatmore.model.mqtt.MqttService;

/**
 * @author yuhang
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//LinphonePreferences.instance().setContext(context);
		//if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SHUTDOWN)) {
		//	android.util.Log.d("LinphoneBootReceiver", "Device is shutting down, destroying LinphoneCore to unregister");
		//	//LinphoneManager.destroy();
		//} else {
		//	//String path = context.getFilesDir().getAbsolutePath() + "/.linphonerc";
		//	//LpConfig lpConfig = LinphoneCoreFactory.instance().createLpConfig(path);
		//	//boolean autostart = lpConfig.getBool("app", "auto_start", false);
		//	//android.util.Log.i("LinphoneBootReceiver", "Device is starting, auto_start is " + autostart);
		//	//if (autostart) {
		//	//	Intent lLinphoneServiceIntent = new Intent(Intent.ACTION_MAIN);
		//	//	lLinphoneServiceIntent.setClass(context, LinphoneService.class);
		//	//	Compatibility.startService(context, lLinphoneServiceIntent);
		//	//}
    //
		//	//打开MQTT
     //   //Class<? extends Activity> mqttsrevice = (Class<? extends Activity>) Class.forName("com.dyyj.idd.chatmore.model.mqtt.MqttService");
     //   Intent lLinphoneServiceIntent = new Intent(Intent.ACTION_MAIN);
     //   lLinphoneServiceIntent.setClass(context, MqttService.class);
     //   Compatibility.startService(context, lLinphoneServiceIntent);
    //
		//}

    Intent lLinphoneServiceIntent = new Intent(Intent.ACTION_MAIN);
    lLinphoneServiceIntent.setClass(context, MqttService.class);
    //Compatibility.startService(context, lLinphoneServiceIntent);
	}
}
