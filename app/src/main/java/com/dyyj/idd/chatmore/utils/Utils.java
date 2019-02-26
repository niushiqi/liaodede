package com.dyyj.idd.chatmore.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.zhihu.matisse.internal.utils.UIUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Timer;
import java.util.TimerTask;

import static net.sourceforge.pinyin4j.format.HanyuPinyinCaseType.UPPERCASE;
import static net.sourceforge.pinyin4j.format.HanyuPinyinToneType.WITHOUT_TONE;

public class Utils {

    public static SpannableStringBuilder getCText(String content, String color, int start, int end) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor(color));
        builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 如果字符串的首字符为汉字，则返回该汉字的拼音大写首字母
     * 如果字符串的首字符为字母，也转化为大写字母返回
     * 其他情况均返回'#'
     *
     * @param str 字符串
     * @return 首字母
     */
    public static char getHeadChar(String str) {
        if (str != null && str.trim().length() != 0) {
            char[] strChar = str.toCharArray();
            char headChar = strChar[0];
            //如果是大写字母则直接返回
            if (Character.isUpperCase(headChar)) {
                return headChar;
            } else if (Character.isLowerCase(headChar)) {
                return Character.toUpperCase(headChar);
            }
            // 汉语拼音格式输出类
            HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
            hanYuPinOutputFormat.setCaseType(UPPERCASE);
            hanYuPinOutputFormat.setToneType(WITHOUT_TONE);
            if (String.valueOf(headChar).matches("[\\u4E00-\\u9FA5]+")) {
                try {
                    String[] stringArray = PinyinHelper.toHanyuPinyinStringArray(headChar, hanYuPinOutputFormat);
                    if (stringArray != null && stringArray[0] != null) {
                        return stringArray[0].charAt(0);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    return '#';
                }
            }
        }
        return '#';
    }

    /**
     * 隐藏键盘
     */
    public static void closeBoardIfShow(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 打开软键盘
     */
    public static void showKeyboard(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
            }
        }, 200);


    }
}
