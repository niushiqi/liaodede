package com.dyyj.idd.chatmore.utils;

import com.dyyj.idd.chatmore.eventtracking.EventBeans;
import com.dyyj.idd.chatmore.eventtracking.Manager;

/**
 * Created by wangbin on 2018/11/22.
 * 对外方法,点击事件埋点
 */

public class EventTrackingUtils {
    /**
     * 将点击事件的数值写入到内存／数据库
     */
    public static void joinPoint(EventBeans eventBeans) {
        //传入这三个值"ch_home_task", "extra"

        //写入数据库/数据结构
        Manager.getInstance().addEvent(eventBeans);
    }

    /**
     * 将页面事件的数值写入到内存／数据库
     */
    /*public static void joinViewPoint(EventBeans eventBeans) {
        //传入这三个值"ch_home_task", "extra"

        //写入数据库/数据结构
        Manager.getInstance().addEvent(eventBeans);
    }*/
}
