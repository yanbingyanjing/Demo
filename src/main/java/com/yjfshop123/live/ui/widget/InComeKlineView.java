package com.yjfshop123.live.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.yjfshop123.live.R;
import com.yjfshop123.live.model.PartnerDetailResponse;
import com.yjfshop123.live.utils.MyUtils;
import com.yjfshop123.live.utils.SystemUtils;

import java.math.BigDecimal;
import java.util.Observable;

public class InComeKlineView extends View {

    //字画笔
    Paint textPaint;
    //通用画笔
    Paint mPaint;
    //曲线画笔
    Paint linePaint;
    PartnerDetailResponse.MonthPartnerData[] data;

    //y刻度最高 最低
    private float priceHigh = 0;
    private float priceLow = 0;

    //网格线 宽度
    protected float gridLineWidth = SystemUtils.dip2px(getContext(), 0.5f);
    protected float gridXYLineWidth = SystemUtils.dip2px(getContext(), 1f);
    protected float lineWidth = SystemUtils.dip2px(getContext(), 1.5f);
    //十字标颜色
    //坐标点颜色
    protected int pointColor = getResources().getColor(R.color.white);
    protected int pointBgColor = getResources().getColor(R.color.color_54FFFFFF);
    //坐标点半径
    protected float pointRaduis = SystemUtils.dip2px(getContext(), 2f);
    protected float pointBgRaduis = SystemUtils.dip2px(getContext(), 4f);
    protected int color_cross = getResources().getColor(R.color.color_17FFFFFF);
    protected int color_crossXY = getResources().getColor(R.color.color_6f6f6f);

    protected int color_crossLine = getResources().getColor(R.color.color_6f6f6f);
    protected int color_Text = getResources().getColor(R.color.color_C9FFFFFF);
    protected float textSize = SystemUtils.dip2px(getContext(), 12f);
    protected float textSizeT = SystemUtils.dip2px(getContext(), 10f);
    //背景颜色
    protected int color_bg = getResources().getColor(R.color.color_0B0A08);
    protected int color_line = getResources().getColor(R.color.color_CEAF82);
    protected float crossX;

    //坐标轴x起点
    int startX;
    //坐标轴y结束点
    int endY;
    //x轴刻度起点距离x远点的距离
    int xValueStart = MyUtils.dp2px(16);
    //x轴刻度小竖线高度
    int keduH = MyUtils.dp2px(4);
    //网格y轴刻度起点
    int yValueStart = MyUtils.dp2px(14);
    //网格y间距
    int gridYDistance;
    //x轴上K线的长度
    int lineXlong;
    //y方向多少个刻度
    int yCount = 5;
    int yValueDistance;
    //最大值
    String highValue = "";
    //最大值背景颜色
    protected int color_HighValue_bg = getResources().getColor(R.color.color_6687DC);
    //最小值
    String lowValue = "";
    //最小值背景颜色
    protected int color_LowValue_bg = getResources().getColor(R.color.color_DC6666);

    public InComeKlineView(Context context) {
        super(context);
        init();
    }

    public InComeKlineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        textPaint = new Paint();
        textPaint.setColor(color_Text);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        mPaint = new Paint();
        linePaint = new Paint();
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setAntiAlias(true);
        linePaint.setColor(color_line);
        setBackgroundColor(color_bg);
    }


    public void setData(PartnerDetailResponse.MonthPartnerData[] data) {
        this.data = data;
        if (this.data == null || this.data.length == 0) {
            return;
        }
        BigDecimal high = new BigDecimal(0);
        BigDecimal low = new BigDecimal(0);
        for (int i = 0; i < this.data.length; i++) {
            if (i == 0) {
                high = new BigDecimal(this.data[i].income);
                low = new BigDecimal(this.data[i].income);

            } else {
                if (new BigDecimal(this.data[i].income).compareTo(high) >= 0)
                    high = new BigDecimal(this.data[i].income);
                if (new BigDecimal(this.data[i].income).compareTo(low) < 0)
                    low = new BigDecimal(this.data[i].income);
            }
        }
        highValue = high.toPlainString();
        lowValue = low.toPlainString();
        priceHigh =new BigDecimal(highValue).multiply(new BigDecimal(1.3)).floatValue();
        priceLow = Float.parseFloat(lowValue) / 4 > 1 ? Float.parseFloat(lowValue) / 4 :Float.parseFloat(lowValue);
        invalidate();
    }

    //x轴每个点之间的间距
    private float getXPostion(int i) {
        return this.data.length > 1 ? (i) * 1f * lineXlong / (this.data.length - 1) : lineXlong * 1f / 2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculat();
        drawXY(canvas);
        if (data == null || data.length == 0) {
            return;
        }
        drawXYText(canvas);
        drawLine(canvas);
    }


    //計算一些起始点
    private void calculat() {
        // startX = MyUtils.getTextWidth(changNumToKM(priceHigh + ""), textPaint) + MyUtils.dp2px(6);
        startX = 0;

        endY = getHeight() - MyUtils.getTextHeight(priceHigh + "", textPaint) - MyUtils.dp2px(8);
        gridYDistance = (endY - yValueStart) / 5;
        // yValueDistance = (priceHigh - priceLow) / 4;
        lineXlong = getWidth() - startX - xValueStart * 2;
    }

    //画xy轴
    private void drawXY(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(gridXYLineWidth);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color_crossXY);

        float[] floats = {
                startX, 0, startX, endY, startX, endY, getWidth(), endY
        };
        canvas.drawLines(floats, mPaint);
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(gridLineWidth);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color_cross);
        float[] floatss = new float[20];
        for (int i = 0; i < yCount; i++) {

            floatss[i * 4] = startX;
            floatss[i * 4 + 1] = yValueStart + gridYDistance * i;
            floatss[i * 4 + 2] = getWidth();
            floatss[i * 4 + 3] = floatss[i * 4 + 1];


        }
        canvas.drawLines(floatss, mPaint);

        //画x轴的刻度小竖线
        mPaint.setStrokeWidth(gridXYLineWidth);
        mPaint.setColor(color_crossXY);
        float[] floatsss = {
                startX + xValueStart, endY, startX + xValueStart, endY + keduH,
                getWidth() - xValueStart, endY, getWidth() - xValueStart, endY + keduH
                , getWidth()*1f/2 ,endY
                , getWidth()*1f/2 ,endY+ keduH

        };
        canvas.drawLines(floatsss, mPaint);
    }

    public static String changNumToKM(String str) {
        BigDecimal kBig = new BigDecimal("1000");
        BigDecimal mBig = new BigDecimal("1000000");
        BigDecimal value = BigDecimal.ZERO;
        String unit = "";
        if (TextUtils.isEmpty(str)) return clearZero(str);

        if (new BigDecimal(str).compareTo(kBig) < 0) {
            value = new BigDecimal(str);
        }
        if (new BigDecimal(str).compareTo(kBig) >= 0 && new BigDecimal(str).compareTo(mBig) < 0) {
            value = new BigDecimal(str).divide(kBig, 0, BigDecimal.ROUND_HALF_DOWN);
            unit = "K";

        }
        if (new BigDecimal(str).compareTo(mBig) >= 0) {
            value = new BigDecimal(str).divide(kBig, 0, BigDecimal.ROUND_HALF_DOWN);
            unit = "M";

        }
        return clearZero(value.toPlainString()) + unit;
//        if (str.length() > 5) {
//            return clearZero(value.toPlainString().substring(0, 5)) + unit;
//        } else {
//            return clearZero(value.toPlainString()) + unit;
//        }
    }

    public static String clearZero(String str) {
        if (TextUtils.isEmpty(str)) return "0";

        return new BigDecimal(str).compareTo(BigDecimal.ZERO) == 0 ? "0" : new BigDecimal(str).stripTrailingZeros().toPlainString();
    }

    //画xy轴刻度
    private void drawXYText(Canvas canvas) {
        textPaint.setColor(color_crossXY);
        textPaint.setTextSize(textSizeT);
        int y = 0;
        String yvalue = "0000";
        //画Y刻度
        for (int i = 0; i < yCount; i++) {
            yvalue = String.valueOf(priceHigh - i * (priceHigh - priceLow) / 4);
            y = yValueStart + gridYDistance * i - (int) gridLineWidth - MyUtils.dp2px(1);
            canvas.drawText(yvalue, startX + MyUtils.dp2px(2), y,
                    textPaint);
        }
        textPaint.setColor(color_Text);
        textPaint.setTextSize(textSize);
        //画X刻度
        if (!TextUtils.isEmpty(data[0].create_date)) {

            canvas.drawText(data[0].create_date.length() > 5 ? data[0].create_date.substring(5) : data[0].create_date, startX + MyUtils.dp2px(4), getHeight(),
                    textPaint);
        }
        if (!TextUtils.isEmpty(data[data.length - 1].create_date)) {
            String a = data[data.length - 1].create_date.length() > 5 ? data[data.length - 1].create_date.substring(5) : data[data.length - 1].create_date;
            canvas.drawText(a,
                    getWidth() - MyUtils.getTextWidth(a, textPaint)-MyUtils.dp2px(1),
                    getHeight(),
                    textPaint);
        }
        if (data.length >= 3) {
            if (!TextUtils.isEmpty(data[data.length / 2].create_date)) {
                String a = data[data.length / 2].create_date.length() > 5 ? data[data.length / 2].create_date.substring(5) : data[data.length / 2].create_date;

                canvas.drawText(a,
                        getWidth() * 1f / 2 - MyUtils.getTextWidth(a, textPaint) * 1f / 2,
                        getHeight(),
                        textPaint);
            }

        }
    }

    boolean isMaxDraw = false;
    boolean isMinDraw = false;

    //画k线
    private void drawLine(Canvas canvas) {
        isMaxDraw = false;
        isMinDraw = false;
        int start = startX + xValueStart;
        for (int i = 0; i < this.data.length; i++) {
            if (i > 0) {
                float xNow = start + getXPostion(i);
                float xPre = start + getXPostion(i - 1);
                float yNow = doubleToY(data[i].income);
                float yPre = doubleToY(data[i - 1].income);
                Path path1 = new Path();
                linePaint.reset();
                linePaint.setAntiAlias(true);
                linePaint.setStrokeWidth(lineWidth);
                linePaint.setStyle(Paint.Style.FILL);
                linePaint.setShader(new LinearGradient(0, getHeight(), 0, 0, 0x00ffffff, 0x30CEAF82, Shader.TileMode.MIRROR));
                path1.moveTo(xPre, yPre);
                path1.lineTo(xNow, yNow);
                path1.lineTo(xNow, getHeight());
                path1.lineTo(xPre, getHeight());

                path1.close();//封闭
                canvas.drawPath(path1, linePaint);
                linePaint.reset();
                linePaint.setStrokeWidth(lineWidth);
                linePaint.setAntiAlias(true);
                linePaint.setColor(color_line);
                linePaint.setStyle(Paint.Style.FILL);
                canvas.drawLine(xPre, yPre, xNow, yNow, linePaint);


            } else {

                float xNow = start + getXPostion(i);
                canvas.drawLine(xNow, doubleToY(data[i].income), xNow, doubleToY(data[i].income), linePaint);
            }
        }
        //画坐标点
        for (int i = 0; i < this.data.length; i++) {
            textPaint.setTextSize(MyUtils.dp2px(14));
            textPaint.setColor(pointColor);
            float xNow = start + getXPostion(i);
            float yNow = doubleToY(data[i].income);
            if (!isMaxDraw && new BigDecimal(data[i].income).compareTo(new BigDecimal(highValue)) == 0) {
                isMaxDraw = true;
                float left, right, top, bottom;
                bottom = yNow - MyUtils.dp2px(14);
                top = bottom - MyUtils.getTextHeight(data[i].income, textPaint) - MyUtils.dp2px(4);
                //画虚线
                linePaint.reset();
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setColor(getResources().getColor(R.color.white));
                linePaint.setShader(null);
                linePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
                if (xNow - MyUtils.getTextWidth(data[i].income, textPaint) - MyUtils.dp2px(18) < startX) {
                    //最高点数据显示区域达到了坐左边，则往右显示
                    right = xNow + MyUtils.getTextWidth(data[i].income, textPaint) * 1f + MyUtils.dp2px(18);
                    left = xNow;
                    canvas.drawLine(xNow, yNow, xNow + (right - left) / 2, bottom, linePaint);
                } else {
                    //最高点数据显示区域没有达到了坐左边，则往左显示
                    left = xNow - MyUtils.getTextWidth(data[i].income, textPaint) * 1f - MyUtils.dp2px(18);
                    right = xNow;
                    canvas.drawLine(xNow, yNow, xNow - (right - left) / 2, bottom, linePaint);
                }
                linePaint.reset();
                linePaint.setStyle(Paint.Style.FILL);
                linePaint.setColor(color_HighValue_bg);
                //画最高点显示框
                RectF r1 = new RectF();
                r1.left = left;
                r1.right = right;
                r1.top = top;
                r1.bottom = bottom;
                canvas.drawRoundRect(r1, MyUtils.dp2px(2), MyUtils.dp2px(2), linePaint);

                canvas.drawText(data[i].income, left + MyUtils.dp2px(9), bottom - MyUtils.dp2px(2),
                        textPaint);


                linePaint.reset();
                linePaint.setStyle(Paint.Style.FILL);
                linePaint.setColor(pointBgColor);
                linePaint.setShader(null);
                canvas.drawCircle(xNow
                        , yNow, pointBgRaduis, linePaint);
                linePaint.reset();
                linePaint.setStyle(Paint.Style.FILL);
                linePaint.setColor(pointColor);
                linePaint.setShader(null);
                canvas.drawCircle(xNow
                        , yNow, pointRaduis, linePaint);

            }
            if (!isMinDraw && new BigDecimal(data[i].income).compareTo(new BigDecimal(lowValue)) == 0 && this.data.length > 1) {
                isMinDraw = true;

                float left, right, top, bottom;
                RectF r1 = new RectF();
                top = yNow + MyUtils.dp2px(14);
                bottom = top + MyUtils.getTextHeight(data[i].income, textPaint) + MyUtils.dp2px(4);

                //画虚线
                linePaint.reset();
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setColor(getResources().getColor(R.color.white));
                linePaint.setShader(null);
                linePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
                if (xNow + MyUtils.getTextWidth(data[i].income, textPaint) + MyUtils.dp2px(18) <= getWidth()) {
                    //最低点数据显示区域没有达到了最右边，则往右显示
                    right = xNow + MyUtils.getTextWidth(data[i].income, textPaint) * 1f + MyUtils.dp2px(18);
                    left = xNow;
                    canvas.drawLine(xNow, yNow, xNow + (right - left) / 2, top, linePaint);
                } else {
                    //最低点数据显示区域达到了最右边，则往左显示
                    left = xNow - MyUtils.getTextWidth(data[i].income, textPaint) * 1f - MyUtils.dp2px(18);
                    right = xNow;
                    canvas.drawLine(xNow, yNow, xNow - (right - left) / 2, top, linePaint);
                }
                linePaint.reset();
                linePaint.setStyle(Paint.Style.FILL);
                linePaint.setColor(color_LowValue_bg);
                //画最低点显示框
                r1.left = left;
                r1.right = right;
                r1.top = top;
                r1.bottom = bottom;
                canvas.drawRoundRect(r1, MyUtils.dp2px(2), MyUtils.dp2px(2), linePaint);
                canvas.drawText(data[i].income, left + MyUtils.dp2px(9), bottom - MyUtils.dp2px(2),
                        textPaint);


                linePaint.reset();
                linePaint.setStyle(Paint.Style.FILL);
                linePaint.setColor(pointBgColor);
                linePaint.setShader(null);
                canvas.drawCircle(xNow
                        , yNow, pointBgRaduis, linePaint);
                linePaint.reset();
                linePaint.setStyle(Paint.Style.FILL);
                linePaint.setColor(pointColor);
                linePaint.setShader(null);
                canvas.drawCircle(xNow
                        , yNow, pointRaduis, linePaint);
            }
            if (i > 0 && i < data.length - 1) {

                linePaint.reset();
                linePaint.setStyle(Paint.Style.FILL);
                linePaint.setColor(pointColor);
                linePaint.setShader(null);
                canvas.drawCircle(xNow
                        , yNow, pointRaduis, linePaint);


            }
        }
    }

    protected float doubleToY(String f) {
        //  if (type == Type.TYPE_K) {
        if (priceHigh - Float.parseFloat(lowValue) == 0) {
            return yValueStart;
        }
        return (float) ((priceHigh - Float.parseFloat(f)) / (priceHigh - priceLow) * (4 * gridYDistance) + yValueStart);
        // } else return (float) ((mMax - f) / (mMax - mMin) * chartHeight);
    }


}
