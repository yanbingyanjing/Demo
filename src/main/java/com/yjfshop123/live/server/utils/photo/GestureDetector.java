package com.yjfshop123.live.server.utils.photo;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.view.MotionEvent;

public interface GestureDetector {
    boolean onTouchEvent(MotionEvent var1);

    boolean isScaling();

    boolean isDragging();

    void setOnGestureListener(OnGestureListener var1);
}
