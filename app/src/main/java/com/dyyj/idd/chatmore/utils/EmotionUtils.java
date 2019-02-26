package com.dyyj.idd.chatmore.utils;

import android.support.v4.util.ArrayMap;

import com.dyyj.idd.chatmore.R;

/**
 * @author : zejian
 * @time : 2016年1月5日 上午11:32:33
 * @email : shinezejian@163.com
 * @description :表情加载类,可自己添加多种表情，分别建立不同的map存放和不同的标志符即可
 */
public class EmotionUtils {

	/**
	 * 表情类型标志符
	 */
	public static final int EMOTION_CLASSIC_TYPE=0x0001;//经典表情

	/**
	 * key-表情文字;
	 * value-表情图片资源
	 */
	public static ArrayMap<String, Integer> EMPTY_MAP;
	public static ArrayMap<String, Integer> EMOTION_CLASSIC_MAP;


	static {
		EMPTY_MAP = new ArrayMap<>();
		EMOTION_CLASSIC_MAP = new ArrayMap<>();

		EMOTION_CLASSIC_MAP.put("[#1]", R.drawable.ic_emotion_picture1);
		EMOTION_CLASSIC_MAP.put("[#2]", R.drawable.ic_emotion_picture2);
		EMOTION_CLASSIC_MAP.put("[#3]", R.drawable.ic_emotion_picture3);
		EMOTION_CLASSIC_MAP.put("[#4]", R.drawable.ic_emotion_picture4);
		EMOTION_CLASSIC_MAP.put("[#5]", R.drawable.ic_emotion_picture5);
		EMOTION_CLASSIC_MAP.put("[#6]", R.drawable.ic_emotion_picture6);
		EMOTION_CLASSIC_MAP.put("[#7]", R.drawable.ic_emotion_picture7);
		EMOTION_CLASSIC_MAP.put("[#8]", R.drawable.ic_emotion_picture8);
		EMOTION_CLASSIC_MAP.put("[#9]", R.drawable.ic_emotion_picture9);
		EMOTION_CLASSIC_MAP.put("[#10]", R.drawable.ic_emotion_picture10);
		EMOTION_CLASSIC_MAP.put("[#11]", R.drawable.ic_emotion_picture11);
		EMOTION_CLASSIC_MAP.put("[#12]", R.drawable.ic_emotion_picture12);
		EMOTION_CLASSIC_MAP.put("[#13]", R.drawable.ic_emotion_picture13);
		EMOTION_CLASSIC_MAP.put("[#14]", R.drawable.ic_emotion_picture14);
		EMOTION_CLASSIC_MAP.put("[#15]", R.drawable.ic_emotion_picture15);
		EMOTION_CLASSIC_MAP.put("[#16]", R.drawable.ic_emotion_picture16);
		EMOTION_CLASSIC_MAP.put("[#17]", R.drawable.ic_emotion_picture17);
		EMOTION_CLASSIC_MAP.put("[#18]", R.drawable.ic_emotion_picture18);
		EMOTION_CLASSIC_MAP.put("[#19]", R.drawable.ic_emotion_picture19);
		EMOTION_CLASSIC_MAP.put("[#20]", R.drawable.ic_emotion_picture20);
		EMOTION_CLASSIC_MAP.put("[#21]", R.drawable.ic_emotion_picture21);
		EMOTION_CLASSIC_MAP.put("[#22]", R.drawable.ic_emotion_picture22);
		EMOTION_CLASSIC_MAP.put("[#23]", R.drawable.ic_emotion_picture23);
		EMOTION_CLASSIC_MAP.put("[#24]", R.drawable.ic_emotion_picture24);
		EMOTION_CLASSIC_MAP.put("[#25]", R.drawable.ic_emotion_picture25);
		EMOTION_CLASSIC_MAP.put("[#26]", R.drawable.ic_emotion_picture26);
		EMOTION_CLASSIC_MAP.put("[#27]", R.drawable.ic_emotion_picture27);
		EMOTION_CLASSIC_MAP.put("[#28]", R.drawable.ic_emotion_picture28);
		EMOTION_CLASSIC_MAP.put("[#29]", R.drawable.ic_emotion_picture29);
		EMOTION_CLASSIC_MAP.put("[#30]", R.drawable.ic_emotion_picture30);
		EMOTION_CLASSIC_MAP.put("[#31]", R.drawable.ic_emotion_picture31);
		EMOTION_CLASSIC_MAP.put("[#32]", R.drawable.ic_emotion_picture32);
		EMOTION_CLASSIC_MAP.put("[#33]", R.drawable.ic_emotion_picture33);
		EMOTION_CLASSIC_MAP.put("[#34]", R.drawable.ic_emotion_picture34);
		EMOTION_CLASSIC_MAP.put("[#35]", R.drawable.ic_emotion_picture35);
		EMOTION_CLASSIC_MAP.put("[#36]", R.drawable.ic_emotion_picture36);
		EMOTION_CLASSIC_MAP.put("[#37]", R.drawable.ic_emotion_picture37);
		EMOTION_CLASSIC_MAP.put("[#38]", R.drawable.ic_emotion_picture38);
		EMOTION_CLASSIC_MAP.put("[#39]", R.drawable.ic_emotion_picture39);
		EMOTION_CLASSIC_MAP.put("[#40]", R.drawable.ic_emotion_picture40);
		EMOTION_CLASSIC_MAP.put("[#41]", R.drawable.ic_emotion_picture41);
		EMOTION_CLASSIC_MAP.put("[#42]", R.drawable.ic_emotion_picture42);
	}

	/**
	 * 根据名称获取当前表情图标R值
	 * @param EmotionType 表情类型标志符
	 * @param imgName 名称
	 * @return
	 */
	public static int getImgByName(int EmotionType,String imgName) {
		Integer integer=null;
		switch (EmotionType){
			case EMOTION_CLASSIC_TYPE:
				integer = EMOTION_CLASSIC_MAP.get(imgName);
				break;
			default:
				break;
		}
		return integer == null ? -1 : integer;
	}

	/**
	 * 根据类型获取表情数据
	 * @param EmotionType
	 * @return
	 */
	public static ArrayMap<String, Integer> getEmojiMap(int EmotionType){
		ArrayMap EmojiMap=null;
		switch (EmotionType){
			case EMOTION_CLASSIC_TYPE:
				EmojiMap=EMOTION_CLASSIC_MAP;
				break;
			default:
				EmojiMap=EMPTY_MAP;
				break;
		}
		return EmojiMap;
	}
}
