package com.yjfshop123.live.ui.animation;

import android.view.animation.Interpolator;

import com.amap.api.maps.model.animation.Animation;
import com.autonavi.amap.mapcore.animation.GLScaleAnimation;

public class XScaleAnimation extends Animation {

    public XScaleAnimation(float var1, float var2, float var3, float var4) {
        this.glAnimation = new GLScaleAnimation(var1, var2, var3, var4);
//        this.glAnimation.setRepeatCount(-1);
    }

    public void setDuration(long var1) {
        this.glAnimation.setDuration(var1);
    }

    public void setInterpolator(Interpolator var1) {
        this.glAnimation.setInterpolator(var1);
    }
}
