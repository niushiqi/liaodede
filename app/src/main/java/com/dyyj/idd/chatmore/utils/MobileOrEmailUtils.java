package com.dyyj.idd.chatmore.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by malibo on 2018/10/5.
 */

public class MobileOrEmailUtils {
    //判断手机格式是否正确
    public static boolean isMobileNO(String mobiles) {
        String str = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
        return mobiles !=null&& mobiles.length() > 0 && mobiles.matches(str);
    }
    //判断email格式是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
       return email !=null&& email.length() >0&& email.matches(str);
    }
    //判断是否全是数字
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
