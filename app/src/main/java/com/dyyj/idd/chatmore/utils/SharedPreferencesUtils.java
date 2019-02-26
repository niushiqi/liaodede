package com.dyyj.idd.chatmore.utils;

import android.content.Context;
import android.content.SharedPreferences;
/**
 *
 * @author  Allen Iverson
 * @date   2018/12/10
 */

public class SharedPreferencesUtils {
    private SharedPreferences share;
    private SharedPreferences.Editor editor;
    private String SHARED_NAME = "spname";//sp的文件名 自定义


    //初始化
    private SharedPreferencesUtils(Context context) {
        share = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        editor = share.edit();
    }

     //单例模式
    private static SharedPreferencesUtils instance;
    public static  SharedPreferencesUtils getInstance(Context context){
        if (instance == null){
            synchronized (SharedPreferencesUtils.class){
                if (instance==null){
                    instance= new SharedPreferencesUtils(context);
                }
            }
        }
        return instance;
    }


    /**
     * ------- Int ---------
     */
    public void putInt(String spName, int value) {
        editor.putInt(spName, value);
        editor.commit();
    }

    public int getInt(String spName, int defaultvalue) {
        return share.getInt(spName, defaultvalue);
    }

    /**
     * ------- String ---------
     */
    public void putString(String spName, String value) {
        editor.putString(spName, value);
        editor.commit();
    }

    public String getString(String spName, String defaultvalue) {
        return share.getString(spName, defaultvalue);
    }

    public String getString(String spName) {
        return share.getString(spName, "");
    }


    /**
     * ------- boolean ---------
     */
    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return share.getBoolean(key, defValue);
    }

    /**
     * ------- float ---------
     */
    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public float getFloat(String key, float defValue) {
        return share.getFloat(key, defValue);
    }


    /**
     * ------- long ---------
     */
    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String key, long defValue) {
        return share.getLong(key, defValue);
    }

    /**
     * 清空SP里所有数据 谨慎调用
     */
    public void clear() {
        editor.clear();//清空
        editor.commit();//提交
    }

    /**
     * 删除SP里指定key对应的数据项
     *
     * @param key
     */
    public void remove(String key) {
        editor.remove(key);//删除掉指定的值
        editor.commit();//提交
    }

    /**
     * 查看sp文件里面是否存在此 key
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return share.contains(key);
    }
//建议 所有的Key 以为常量的形式保存在此类里面

    public static final String USER_NAME = "name";//例如



}
