/**
 * zhouyong
 * 2016年8月15日上午10:45:13
 */
package com.dyyj.idd.chatmore.ui.user.photo.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Desc: 加载圆形图片的BitmapTransformation
 * Author :zhangcx
 * Data: 2018/6/6 11:31
*/
public class CircleBitmapTransformation extends BitmapTransformation {


    public CircleBitmapTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) {
            return null;
        }

        //得到原图的一个正方形图片
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

        //创建出一个空白的bitmap，将来可以在上面绘制圆角图形
        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }


        Paint paint = new Paint();
        //使用BitmapShader渲染
        // TileMode的渲染模式有三种
        // CLAMP  ：如果渲染器超出原始边界范围，会复制范围内边缘染色。
        // REPEAT ：横向和纵向的重复渲染器图片，平铺。
        // MIRROR ：横向和纵向的重复渲染器图片，这个和REPEAT 重复方式不一样，他是以镜像方式平铺。
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);

        //以result作为画布创建Canvas，选择原图的中心点，以中心点为中心绘制
        float r = size / 2f;
        Canvas canvas = new Canvas(result);
        canvas.drawCircle(r, r, r, paint);
        return result;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}