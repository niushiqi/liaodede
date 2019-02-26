package com.dyyj.idd.chatmore.weiget;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ArcPageTransformer implements ViewPager.PageTransformer {

    private static final float ANGLE_MAX = 30.0f;
    private float mAngle;


    @Override
    public void transformPage(@NonNull View view, float position) {
        if (position < -1) {
            view.setRotation(0);
        } else if (position <= 1) {
            // a页滑动至b页 ； a页从 0.0 ~ -1 ；b页从1 ~ 0.0
            if (position < 0) {
                mAngle = (ANGLE_MAX * position);
//                view.setPivotX(view.getMeasuredWidth() * 1f);
//                view.setPivotY(view.getMeasuredHeight());
                view.setRotation(mAngle);
            } else {
                mAngle = (ANGLE_MAX * position);
//                view.setPivotX(view.getMeasuredWidth() * 0);
//                view.setPivotY(view.getMeasuredHeight());
                view.setRotation(mAngle);
            }
        } else {
            view.setRotation(0);
        }
    }
}
