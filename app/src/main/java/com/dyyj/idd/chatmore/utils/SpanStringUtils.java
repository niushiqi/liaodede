/**
 * 
 */
package com.dyyj.idd.chatmore.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * @author : zejian
 * @time : 2016年1月5日 上午11:30:39
 * @email : shinezejian@163.com
 * @description :文本中的emojb字符处理为表情图片
 */
public class SpanStringUtils {

	private static Bitmap cropBitmap(Bitmap bitmap) {
		int w = bitmap.getWidth(); // 得到图片的宽，高
		int h = bitmap.getHeight();
		int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
		cropWidth /= 1.8;
		int cropHeight = (int) (cropWidth);
		return Bitmap.createBitmap(bitmap, (w - cropWidth) / 2, (h - cropWidth) / 2,
				cropWidth, cropHeight, null, false);
	}
	
	public static SpannableString getEmotionContent(int emotion_map_type,final Context context, final TextView tv, String source) {
		SpannableString spannableString = new SpannableString(source);
		Resources res = context.getResources();

		//String regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]";
		String regexEmotion = "\\[#\\d+\\]";
		Pattern patternEmotion = Pattern.compile(regexEmotion);
		Matcher matcherEmotion = patternEmotion.matcher(spannableString);

		while (matcherEmotion.find()) {
			// 获取匹配到的具体字符
			String key = matcherEmotion.group();
			// 匹配字符串的开始位置
			int start = matcherEmotion.start();
			// 利用表情名字获取到对应的图片
			Integer imgRes = EmotionUtils.getImgByName(emotion_map_type,key);
			if (imgRes != null) {
				// 压缩表情图片
				int size = (int) tv.getTextSize()*25/10;
				Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
				Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
				scaleBitmap = cropBitmap(scaleBitmap);

				ImageSpan span = new ImageSpan(context, scaleBitmap);
				spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return spannableString;
	}

	/**
	 * 将系统消息文字格式转化为带URL跳转的文字格式,只支持一个字符串里面包含一个链接
	 *
	 * @return
	 */
	public static SpannableString getTextWithURLContent(String source, ClickableSpan clickableSpan) {
		SpannableString spannableString = new SpannableString(source);

		//判断字符串是否有链接
		String regexURL = "%\\[[^\\[\\]]+\\]%";
		Pattern patternURL = Pattern.compile(regexURL);
		Matcher matcherURL = patternURL.matcher(spannableString);
		if(!matcherURL.find()) {
			return spannableString;
		}

		//截去链接中的标志符号 %[ 和 ]%
		String key = matcherURL.group();
		int start = matcherURL.start();
		String sourceVirgin = spannableString.toString().substring(0, start)
				+ spannableString.toString().substring(start + 2, start + key.length() - 2)
				+ spannableString.toString().substring(start + key.length(), spannableString.toString().length());

		//设置链接的点击事件
		SpannableString spannableStringVirgin = new SpannableString(sourceVirgin);
		String regexVirginURL = key.substring(2, key.length() - 2);
		Pattern patternVirginURL = Pattern.compile(regexVirginURL);
		Matcher matcherVirginURL = patternVirginURL.matcher(spannableStringVirgin);
		while (matcherVirginURL.find()) {
			// 获取匹配到的具体字符
			String keyVirgin = matcherVirginURL.group();
			// 匹配字符串的开始位置
			int startVirgin = matcherVirginURL.start();

			//ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#CC0000"));
			//spannableString.setSpan(colorSpan, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannableStringVirgin.setSpan(clickableSpan, startVirgin, startVirgin + keyVirgin.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spannableStringVirgin;
	}
}
