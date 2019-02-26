package com.dyyj.idd.chatmore.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.dyyj.idd.chatmore.R;
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil;

import java.util.UUID;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/12
 * desc   :
 */
public class DeviceUtils {

//    public static String getDeviceID(Context context) {
//        TelephonyManager manager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
//        Class clazz = manager.getClass();
//        String imei = "imei";
//        try {
//            Method getImei = clazz.getDeclaredMethod("getImei");//(int slotId)
//            getImei.setAccessible(true);
//            imei = (String) getImei.invoke(manager);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return imei;
//    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static int getScreenWidth(Resources resources) {
        return resources.getDisplayMetrics().widthPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    private static String mDeviceId;

    public static String getDeviceID(Context context) {
        if (mDeviceId != null) {
            return mDeviceId;
        }
        String deviceId = PreferenceUtil.getString("deviceId", "");
        if (!TextUtils.isEmpty(mDeviceId)) {
            mDeviceId = deviceId;
            return mDeviceId;
        }

        try {
            if (checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
                TelephonyManager tm = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
                mDeviceId = tm.getDeviceId();
            }
            if (TextUtils.isEmpty(mDeviceId)) {
                mDeviceId = getMacAddress(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //都取不到 考虑取系统某一个固定文件的md5值
        }
        if (TextUtils.isEmpty(mDeviceId)) {
            mDeviceId = UUID.randomUUID().toString();
        }
        PreferenceUtil.commitString("deviceId", mDeviceId);
        return mDeviceId;
    }

    /**
     * 由于获取IMEI权限之前的设备ID为Mac地址
     * 所以获取权限之后要重置设备ID
     */
    public static void clearDeviceID() {
        if (mDeviceId != null) {
            mDeviceId = null;
        }
        String deviceId = PreferenceUtil.getString("deviceId", "");
        if (!TextUtils.isEmpty(deviceId)) {
            PreferenceUtil.removeKey("deviceId");
        }
    }

    /**
     * 获取当前设备的MAC地址
     */
    public static String getMacAddress(Context context) {
        String mac = null;
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wm.getConnectionInfo();
            mac = info.getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac;
    }

    /**
     * 检测权限
     *
     * @param context
     * @param permission
     * @return true or false
     */
    public static boolean checkPermissions(Context context, String permission) {
        if (context == null) {
            return false;
        }
        PackageManager localPackageManager = context.getPackageManager();
        return localPackageManager.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 判断有无网络，或网络是否可用
     */

    /*
     * 判断网络连接是否已开
     * true 已打开  false 未打开
     * */
    public static boolean isConn(Context context){
        boolean bisConnFlag=false;
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network!=null){
            bisConnFlag=conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }

    /**
     * 当判断当前手机没有网络时选择是否打开网络设置
     * @param context
     */
    public static void showNoNetWorkDlg(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_launcher)         //
                .setTitle(R.string.app_name)            //
                .setMessage("当前无网络").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 跳转到系统的网络设置界面
                Intent intent = null;
                // 先判断当前系统版本
                if(android.os.Build.VERSION.SDK_INT > 10){  // 3.0以上
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                }else{
                    intent = new Intent();
                    intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
                }
                context.startActivity(intent);

            }
        }).setNegativeButton("知道了", null).show();
    }
}
