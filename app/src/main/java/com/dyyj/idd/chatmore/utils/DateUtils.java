package com.dyyj.idd.chatmore.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/24
 * desc   :
 */
public class DateUtils {
    /**
     * 获取精确到秒的时间戳
     *
     * @param date
     * @return
     */
    public static int getSecondTimestampTwo(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.valueOf(timestamp);
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     */
    public static String timeToDate(String seconds) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     */
    public static String timeToDate(Long seconds) {
        if (seconds == null || seconds.equals("null")) {
            return "";
        }
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(seconds + "000"));
    }


    public static String getStrTime(String simpleFor,String cc_time) {
        if (TextUtils.isEmpty(cc_time)) {
            return "";
        }
        String re_StrTime = null;        //同理也可以转为其它样式的时间格式.例如："yyyy/MM/dd HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat(simpleFor);

        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));

        return re_StrTime;
    }



}
