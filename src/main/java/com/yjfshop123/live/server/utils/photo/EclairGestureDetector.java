package com.yjfshop123.live.server.utils.photo;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.annotation.TargetApi;
import android.content.Context;
import android.view.MotionEvent;

@TargetApi(5)
public class EclairGestureDetector extends CupcakeGestureDetector {
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = -1;
    private int mActivePointerIndex = 0;

    public EclairGestureDetector(Context context) {
        super(context);
    }

    float getActiveX(MotionEvent ev) {
        try {
            return ev.getX(this.mActivePointerIndex);
        } catch (Exception var3) {
            return ev.getX();
        }
    }

    float getActiveY(MotionEvent ev) {
        try {
            return ev.getY(this.mActivePointerIndex);
        } catch (Exception var3) {
            return ev.getY();
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch(action & 255) {
            case 0:
                this.mActivePointerId = ev.getPointerId(0);
                break;
            case 1:
            case 3:
                this.mActivePointerId = -1;
            case 2:
            case 4:
            case 5:
            default:
                break;
            case 6:
                int pointerIndex = Compat.getPointerIndex(ev.getAction());
                int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == this.mActivePointerId) {
                    int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    this.mActivePointerId = ev.getPointerId(newPointerIndex);
                    this.mLastTouchX = ev.getX(newPointerIndex);
                    this.mLastTouchY = ev.getY(newPointerIndex);
                }
        }

        this.mActivePointerIndex = ev.findPointerIndex(this.mActivePointerId != -1 ? this.mActivePointerId : 0);

        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException var6) {
            return true;
        }
    }
}
