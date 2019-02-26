/*******************************************************************************
 * Copyright 2016 stfalcon.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.dyyj.idd.chatmore.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateFormatter {
    private DateFormatter() {
        throw new AssertionError();
    }

    public static String format(Date date, Template template) {
        return format(date, template.get());
    }

    public static String format(Date date, String format) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     */
    public static String timeToDate(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) format = "MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
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
        String format = "MM月dd日 HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("Dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("Dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(
                Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isSameYear(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("Dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(
                Calendar.YEAR));
    }

    public static boolean isToday(Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }

    public enum Template {
        STRING_DAY_MONTH_YEAR("d MMMM yyyy"), STRING_DAY_MONTH("d MMMM"), TIME("HH:mm"), FORMAT_YMD("yyyy-MM-dd");

        private String template;

        Template(String template) {
            this.template = template;
        }

        public String get() {
            return template;
        }
    }

    /**
     * String型时间戳格式化
     */
    public static String Long2Time(Long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * String型时间戳格式化
     */
    public static String LongFormatTime(String time) {
        if (TextUtils.isEmpty(time)) {
            time = new Date().toString();
        }
        //转换为日期
        Date date = new Date(timestamp2Date(time));
        //date.setTime(Long.parseLong(time));
        if (isThisYear(date)) {//今年
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            if (isToday(date)) { //今天
                int minute = minutesAgo(timestamp2Date(time));
                if (minute < 60) {//1小时之内
                    if (minute <= 1) {//一分钟之内，显示刚刚
                        return "刚刚";
                    } else {
                        return minute + "分钟前";
                    }
                } else {
                    return simpleDateFormat.format(date);
                }
            } else {
                if (isYestYesterday(date)) {//昨天，显示昨天
                    return "昨天 " + simpleDateFormat.format(date);
                } else if (isThisWeek(date)) {//本周,显示周几
                    String weekday = null;
                    if (date.getDay() == 1) {
                        weekday = "周一";
                    }
                    if (date.getDay() == 2) {
                        weekday = "周二";
                    }
                    if (date.getDay() == 3) {
                        weekday = "周三";
                    }
                    if (date.getDay() == 4) {
                        weekday = "周四";
                    }
                    if (date.getDay() == 5) {
                        weekday = "周五";
                    }
                    if (date.getDay() == 6) {
                        weekday = "周六";
                    }
                    if (date.getDay() == 0) {
                        weekday = "周日";
                    }
                    return weekday + " " + simpleDateFormat.format(date);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                    return sdf.format(date);
                }
            }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(date);
        }
    }

    /**
     * String型时间戳格式化
     */
    public static String LongFormatTime2(String time) {
        if (TextUtils.isEmpty(time)) {
            time = new Date().toString();
        }
        //转换为日期
        Date date = new Date(timestamp2Date(time));
        if (isToday(date)) { //今天
            int minute = minutesAgo(timestamp2Date(time));
            if (minute < 60) {//1小时之内
                if (minute <= 1) {//一分钟之内，显示刚刚
                    return "刚刚";
                } else {
                    return minute + "分钟前";
                }
            } else {
                return new Date().getHours() - date.getHours() + "小时前";
            }
        } else {
            if (isYestYesterday(date)) {//昨天，显示昨天
                return "昨天";
            } else if (isThisWeek(date)) {//本周,显示周几
                String weekday = null;
                if (date.getDay() == 1) {
                    weekday = "周一";
                }
                if (date.getDay() == 2) {
                    weekday = "周二";
                }
                if (date.getDay() == 3) {
                    weekday = "周三";
                }
                if (date.getDay() == 4) {
                    weekday = "周四";
                }
                if (date.getDay() == 5) {
                    weekday = "周五";
                }
                if (date.getDay() == 6) {
                    weekday = "周六";
                }
                if (date.getDay() == 0) {
                    weekday = "周日";
                }
                return weekday;
            } else {
                return "7天前";
            }
        }
    }


    private static boolean isThisYear(Date date) {
        Date now = new Date();
        return date.getYear() == now.getYear();
    }

    private static boolean isThisMonth(Date date) {
        Date now = new Date();
        return date.getMonth() == now.getMonth();
    }


    private static int minutesAgo(long time) {
        return (int) ((System.currentTimeMillis() - time) / (60000));
    }

    private static boolean isYestYesterday(Date date) {
        Date now = new Date();
        return (date.getYear() == now.getYear())
                && (date.getMonth() == now.getMonth())
                && (date.getDate() + 1 == now.getDate());
    }

    private static boolean isThisWeek(Date date) {
        Date now = new Date();
        if ((date.getYear() == now.getYear()) && (date.getMonth() == now.getMonth())) {
            if (now.getDay() - date.getDay() < now.getDay()
                    && now.getDate() - date.getDate() > 0
                    && now.getDate() - date.getDate() < 7) {
                return true;
            }
        }
        return false;
    }


    /*
     * 将10 or 13 位时间戳转为时间字符串
     * convert the number 1407449951 1407499055617 to date/time format timestamp
     */
    @SuppressLint("LongLogTag")
    public static long timestamp2Date(String str_num) {
        if (TextUtils.isEmpty(str_num)) {
            return 0;
        }
        if (str_num.length() == 13) {
            return Long.parseLong(str_num);
        } else {
            return Integer.parseInt(str_num) * 1000L;
        }
    }

    /*
     * 判断两个时间戳是否是同一天
     */
    public static boolean isSameDay(Long time1, Long time2) {
            Date date = new Date(time1);
            Date date2 = new Date(time2);
            return isSameDay(date, date2);
    }
}

