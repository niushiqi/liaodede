package com.dyyj.idd.chatmore.weiget;

import android.view.animation.Interpolator;

public class OverShootInterpolation implements Interpolator {
    //弹性因数
    private double factor = 3.0;

    public void SpringScaleInterpolator(double factor) {
        this.factor = factor;
    }

    @Override
    public float getInterpolation(float input) {
        input -= 1.0;
        return (float) (input * input * ((factor + 1) * input + factor) + 1.0);
    }


}
