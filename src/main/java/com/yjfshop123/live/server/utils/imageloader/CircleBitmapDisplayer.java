package com.yjfshop123.live.server.utils.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 *
 * 日期:2018/12/10
 * 描述:
 **/
public class CircleBitmapDisplayer implements BitmapDisplayer {
    protected final Integer strokeColor;
    protected final float strokeWidth;

    public CircleBitmapDisplayer() {
        this((Integer) null);
    }

    public CircleBitmapDisplayer(Integer strokeColor) {
        this(strokeColor, 0.0F);
    }

    public CircleBitmapDisplayer(Integer strokeColor, float strokeWidth) {
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }

    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        } else {
            imageAware.setImageDrawable(new CircleBitmapDisplayer.CircleDrawable(bitmap, this.strokeColor, this.strokeWidth));
        }
    }

    public static class CircleDrawable extends Drawable {
        protected float radius;
        protected final RectF mRect = new RectF();
        protected final RectF mBitmapRect;
        protected final BitmapShader bitmapShader;
        protected final Paint paint;
        protected final Paint strokePaint;
        protected final float strokeWidth;
        protected float strokeRadius;

        public CircleDrawable(Bitmap bitmap, Integer strokeColor, float strokeWidth) {
            this.radius = (float) (Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2);
            this.bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            this.mBitmapRect = new RectF(0.0F, 0.0F, (float) bitmap.getWidth(), (float) bitmap.getHeight());
            this.paint = new Paint();
            this.paint.setAntiAlias(true);
            this.paint.setShader(this.bitmapShader);
            this.paint.setFilterBitmap(true);
            this.paint.setDither(true);
            if (strokeColor == null) {
                this.strokePaint = null;
            } else {
                this.strokePaint = new Paint();
                this.strokePaint.setStyle(Paint.Style.STROKE);
                this.strokePaint.setColor(strokeColor);
                this.strokePaint.setStrokeWidth(strokeWidth);
                this.strokePaint.setAntiAlias(true);
            }

            this.strokeWidth = strokeWidth;
            this.strokeRadius = this.radius - strokeWidth / 2.0F;
        }

        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            this.mRect.set(0.0F, 0.0F, (float) bounds.width(), (float) bounds.height());
            this.radius = (float) (Math.min(bounds.width(), bounds.height()) / 2);
            this.strokeRadius = this.radius - this.strokeWidth / 2.0F;
            Matrix shaderMatrix = new Matrix();
            shaderMatrix.setRectToRect(this.mBitmapRect, this.mRect, Matrix.ScaleToFit.FILL);
            this.bitmapShader.setLocalMatrix(shaderMatrix);
        }

        public void draw(Canvas canvas) {
            canvas.drawCircle(this.radius, this.radius, this.radius, this.paint);
            if (this.strokePaint != null) {
                canvas.drawCircle(this.radius, this.radius, this.strokeRadius, this.strokePaint);
            }

        }

        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        public void setAlpha(int alpha) {
            this.paint.setAlpha(alpha);
        }

        public void setColorFilter(ColorFilter cf) {
            this.paint.setColorFilter(cf);
        }
    }
}
