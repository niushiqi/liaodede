package com.dyyj.idd.chatmore.weiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.dyyj.idd.chatmore.R;

public class HorizontalProgressBar extends View {

    /**
     * 画笔
     */
    private Paint paint;


    /**
     * 长方形的颜色
     */
    private int horizontalColor;


    /**
     * 长方形进度的颜色
     */
    private int horizontalProgressColor;


    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;


    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;


    /**
     * 长方形的宽度
     */
    private float horizontalWidth;


    /**
     * 最大进度
     */
    private int max;


    /**
     * 当前进度
     */
    private int progress;
    private int mProgress;


    /**
     * 是否显示百分比
     */
    private boolean isShowText;


    /**
     * 是否 是圆角矩形
     */
    private boolean isRoundRect;


    /**
     * 圆角半径
     */
    private int radius;


    public HorizontalProgressBar(Context context) {
        this(context, null);
    }


    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public HorizontalProgressBar(Context context, AttributeSet attrs,
                                 int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.HorizontalProgressBar);
        // 获取自定义属性和默认值
        horizontalColor = mTypedArray.getColor(
                R.styleable.HorizontalProgressBar_horizontalColor, Color.parseColor("#D5D5D5"));
        horizontalProgressColor = mTypedArray.getColor(
                R.styleable.HorizontalProgressBar_horizontalProgressColor,
                Color.parseColor("#F8A2AA"));
        textColor = mTypedArray.getColor(
                R.styleable.HorizontalProgressBar_horizontalTextColor,
                Color.BLACK);
        textSize = mTypedArray.getDimension(
                R.styleable.HorizontalProgressBar_horizontalTextSize, 20);
        horizontalWidth = mTypedArray.getDimension(
                R.styleable.HorizontalProgressBar_horizontalWidth, 20);
        max = mTypedArray.getInteger(
                R.styleable.HorizontalProgressBar_horizontalMax, 100);
        if (max == 0) {
            max = 100;
        }
        isShowText = mTypedArray.getBoolean(
                R.styleable.HorizontalProgressBar_isShowText, false);
        isRoundRect = mTypedArray.getBoolean(
                R.styleable.HorizontalProgressBar_isRoundRect, true);
        radius = mTypedArray.getInteger(
                R.styleable.HorizontalProgressBar_radius, 10);
        mTypedArray.recycle();
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(horizontalColor); // 设置长方形的颜色
        paint.setStyle(Paint.Style.FILL); // 设置空心
        paint.setStrokeWidth(horizontalWidth); // 设置长方形宽度
        paint.setAntiAlias(true); // 消除锯齿
        drawRect(canvas, paint, 0, 0, getWidth(), horizontalWidth);


        /**
         * 画进度
         */
        paint.setStrokeWidth(horizontalWidth); // 设置圆环的宽度
        paint.setColor(horizontalProgressColor); // 设置进度的颜色
        drawRect(canvas, paint, 0, 0, getWidth() * progress / max,
                horizontalWidth);


        /**
         * 画进度百分比
         */
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置字体
        int percent = (int) (((float) progress / (float) max) * 100); // 中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = paint.measureText(percent + "%"); // 测量字体宽度

        if (isShowText && percent != 0) {
            canvas.drawText(mProgress + "/" + max, (getWidth() - textWidth) / 2,
                    (horizontalWidth + textSize) / 2, paint); // 画出进度百分比
        }
    }


    /**
     * 绘制矩形
     *
     * @param canvas
     * @param paint
     * @param left   左
     * @param top    上
     * @param right  右
     * @param bottom 下
     */
    private void drawRect(Canvas canvas, Paint paint, float left, float top,
                          float right, float bottom) {
        RectF rectF = new RectF(left, top, right, bottom);
        if (isRoundRect) {
            canvas.drawRoundRect(rectF, radius, radius, paint);
        } else {
            canvas.drawRect(rectF, paint);
        }
    }


    public synchronized int getMax() {
        return max;
    }


    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }


    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }


    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        mProgress = progress;
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }


    public int getHorizontalColor() {
        return horizontalColor;
    }


    public void setHorizontalColor(int horizontalColor) {
        this.horizontalColor = horizontalColor;
    }


    public int getHorizontalProgressColor() {
        return horizontalProgressColor;
    }


    public void setHorizontalProgressColor(int horizontalProgressColor) {
        this.horizontalProgressColor = horizontalProgressColor;
    }


    public int getTextColor() {
        return textColor;
    }


    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }


    public float getTextSize() {
        return textSize;
    }


    public void setTextSize(float textSize) {
        if (textSize > horizontalWidth) {
            textSize = horizontalWidth;
        }
        this.textSize = textSize;
    }


    public float getHorizontalWidth() {
        return horizontalWidth;
    }


    public void setHorizontalWidth(float horizontalWidth) {
        this.horizontalWidth = horizontalWidth;
    }


    public boolean isShowText() {
        return isShowText;
    }


    public void setShowText(boolean isShowText) {
        this.isShowText = isShowText;
    }


    public boolean isRoundRect() {
        return isRoundRect;
    }


    public void setRoundRect(boolean isRoundRect) {
        this.isRoundRect = isRoundRect;
    }


    public int getRadius() {
        return radius;
    }


    public void setRadius(int radius) {
        if (radius > horizontalWidth) {
            radius = (int) (horizontalWidth / 2);
        }
        this.radius = radius;
    }
}