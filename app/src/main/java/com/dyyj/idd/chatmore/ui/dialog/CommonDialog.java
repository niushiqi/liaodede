package com.dyyj.idd.chatmore.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.dyyj.idd.chatmore.R;
import com.othershe.nicedialog.ViewHolder;
import com.zhihu.matisse.internal.utils.UIUtils;

import butterknife.ButterKnife;


/**
 * describe: dialog封装类
 * author: zhangcx
 * time 2018/2/3 下午4:06
 * <p>
 * 使用方法
 * CommonDialog.init()
 * .setLayoutId(R.layout.dialog)     //设置dialog布局文件
 * .setConvertListener(new ViewConvertListener() {     //进行相关View操作的回调
 *
 * @Override public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
 * <p>
 * }
 * })
 * .setDimAmount(0.3f)     //调节灰色背景透明度[0-1]，默认0.5f
 * .setShowBottom(true)     //是否在底部显示dialog，默认flase
 * .setMargin()     //dialog左右两边到屏幕边缘的距离（单位：dp），默认0dp
 * .setWidth()     //dialog宽度（单位：dp），默认为屏幕宽度，-1代表WRAP_CONTENT
 * .setHeight()     //dialog高度（单位：dp），默认为WRAP_CONTENT
 * .setOutCancel(false)     //点击dialog外是否可取消，默认true
 * .setAnimStyle(R.style.EnterExitAnimation)     //设置dialog进入、退出的动画style(底部显示的dialog有默认动画)
 * .show(getSupportFragmentManager());     //显示dialog
 * <p>
 * PS
 * 也可以继承 此基类dialog  实现自己的具体样式
 * MyDialog.newInstance("1")
 * .setMargin(60)
 * .setOutCancel(false)
 * .show(getSupportFragmentManager());
 */
public abstract class CommonDialog extends DialogFragment {
    private static final String MARGIN = "margin";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String DIM = "dim_amount";
    private static final String BOTTOM = "show_bottom";
    private static final String CANCEL = "out_cancel";
    private static final String ANIM = "anim_style";
    private static final String LAYOUT = "layout_id";

    private int margin;//左右边距
    private int width;//宽度
    private int height;//高度
    private float dimAmount = 0.5f;//灰度深浅
    private boolean showBottom;//是否底部显示
    private boolean outCancel = true;//是否点击外部取消
    @StyleRes
    private int animStyle;
    @LayoutRes
    protected int layoutId;

    public abstract int intLayoutId();

    public abstract void convertView(ViewHolder holder, CommonDialog dialog);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.NiceDialog);
        layoutId = intLayoutId();

        //恢复保存的数据
        if (savedInstanceState != null) {
            margin = savedInstanceState.getInt(MARGIN);
            width = savedInstanceState.getInt(WIDTH);
            height = savedInstanceState.getInt(HEIGHT);
            dimAmount = savedInstanceState.getFloat(DIM);
            showBottom = savedInstanceState.getBoolean(BOTTOM);
            outCancel = savedInstanceState.getBoolean(CANCEL);
            animStyle = savedInstanceState.getInt(ANIM);
            layoutId = savedInstanceState.getInt(LAYOUT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);
        convertView(ViewHolder.create(view), this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initParams();
    }

    /**
     * 屏幕旋转等导致DialogFragment销毁后重建时保存数据
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MARGIN, margin);
        outState.putInt(WIDTH, width);
        outState.putInt(HEIGHT, height);
        outState.putFloat(DIM, dimAmount);
        outState.putBoolean(BOTTOM, showBottom);
        outState.putBoolean(CANCEL, outCancel);
        outState.putInt(ANIM, animStyle);
        outState.putInt(LAYOUT, layoutId);
    }

    private void initParams() {
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            //调节灰色背景透明度[0-1]，默认0.5f
            lp.dimAmount = dimAmount;
            //是否在底部显示
            if (showBottom) {
                lp.gravity = Gravity.BOTTOM;
                if (animStyle == 0) {
                    animStyle = R.style.DefaultAnimation;
                }
            }

            //设置dialog宽度
            if (width == 0) {
                lp.width = getScreenWidth(getContext()) - 2 * dp2px(margin);
            } else if (width == -1) {
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                lp.width = dp2px(width);
            }

            //设置dialog高度
            if (height == 0) {
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                lp.height = dp2px(height);
            }

            //设置dialog进入、退出的动画
            window.setWindowAnimations(animStyle);
            window.setAttributes(lp);
        }
        setCancelable(outCancel);
    }

    public CommonDialog setMargin(int margin) {
        this.margin = margin;
        return this;
    }

    public CommonDialog setWidth(int width) {
        this.width = width;
        return this;
    }

    public CommonDialog setHeight(int height) {
        this.height = height;
        return this;
    }

    public CommonDialog setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
        return this;
    }

    public CommonDialog setShowBottom(boolean showBottom) {
        this.showBottom = showBottom;
        return this;
    }

    public CommonDialog setOutCancel(boolean outCancel) {
        this.outCancel = outCancel;
        return this;
    }

    public CommonDialog setAnimStyle(@StyleRes int animStyle) {
        this.animStyle = animStyle;
        return this;
    }

    public CommonDialog show(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        if (this.isAdded()) {
            ft.remove(this).commit();
        }
        ft.add(this, String.valueOf(System.currentTimeMillis()));
        ft.commitAllowingStateLoss();
        return this;
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * dip转换px
     */
    public int dp2px(int dip) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

}
