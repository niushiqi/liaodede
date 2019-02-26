/**
 * zhouyong
 * 2016年8月15日上午11:07:55
 */
package com.dyyj.idd.chatmore.ui.user.photo.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Desc: 旋转处理
 * Author :zhangcx
 * Data: 2018/6/6 11:32
*/
public class RotateTransformation extends BitmapTransformation {

	private float rotateAngle = 0f;

	public RotateTransformation(Context context, float rotateRotationAngle) {
		super(context);

		this.rotateAngle = rotateRotationAngle;
	}

	@Override
	protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
		//使用矩阵旋转位图
		Matrix matrix = new Matrix();
		matrix.postRotate(rotateAngle);
		return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
	}

	@Override
	public String getId() {
		return "rotate" + rotateAngle;
	}
}