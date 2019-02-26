/**
 * zhouyong
 * 2016年8月15日上午10:43:43
 */
package com.dyyj.idd.chatmore.ui.user.photo.transformation;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Desc: 加载圆角图片的BitmapTransformation
 * Author :zhangcx
 * Data: 2018/6/6 11:31
*/
public class RoundBitmapTransformation extends BitmapTransformation {

	private static float radius = 0f;

	// 默认为四个弧度的圆角
	public RoundBitmapTransformation(Context context) {
		this(context, 4);
	}

	public RoundBitmapTransformation(Context context, int dp) {
		super(context);
		this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
	}

	@Override
	protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
		return roundCrop(pool, toTransform);
	}

	private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
		if (source == null)
			return null;

		Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		if (result == null) {
			result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
		paint.setAntiAlias(true);
		RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
		canvas.drawRoundRect(rectF, radius, radius, paint);
		return result;
	}

	@Override
	public String getId() {
		return getClass().getName() + Math.round(radius);
	}
}