package com.yjfshop123.live.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yjfshop123.live.net.utils.NLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StarWhitePanel2 extends View{
    private BlockingQueue sendQueue = new LinkedBlockingQueue();//单向链表实现的阻塞队列。（先进先出）
    private Boolean onPublish = false;//是否为输入方-主播
    private Map groupMap = new HashMap();//多人集合map
    private String selfTag;
    private int currentColor = 0xff000000;
    private Boolean isLaserOn = false;
    private Point laserPoint;
    private int laserColor = 0xff000000;
    private Bitmap imgBitmap;
//    private IXHRealtimeDataSender sender;//TODO
    private int theWidth = -1;
    private int theHeight = -1;
    private Timer sendTimer;
    private TimerTask sendTimerTask;
    private OnTouchListenerImp onTouchListenerImp;
    private Paint paint = new Paint();

    public StarWhitePanel2(Context var1) {
        super(var1);
        if (!this.isInEditMode()) {
            this.init();
        }
    }

    public StarWhitePanel2(Context var1, @Nullable AttributeSet var2) {
        super(var1, var2);
        if (!this.isInEditMode()) {
            this.init();
        }
    }

    private void init() {
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setColor(this.currentColor);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(3.0F);
        this.post(new Runnable() {
            public void run() {
                theWidth = getMeasuredWidth();
                theHeight = getMeasuredHeight();
            }
        });
    }

    //TODO
    public void publish(/*IXHRealtimeDataSender var1,*/ String var2) {
//        this.sender = var1;//TODO
        if (TextUtils.isEmpty(this.selfTag)) {
            this.selfTag = var2;
            this.groupMap.put(this.selfTag, new ArrayList());
        }

        if (this.onTouchListenerImp == null) {
            this.onTouchListenerImp = new OnTouchListenerImp();
            super.setOnTouchListener(new OnTouchListenerImp());
        }

        if (this.sendTimer != null) {
            this.sendTimer.cancel();
            this.sendTimer = null;
            this.sendTimerTask.cancel();
            this.sendTimerTask = null;
        }

        this.onPublish = true;
        this.sendTimer = new Timer();
        this.sendTimerTask = new TimerTask() {
            public void run() {
                byte[] var1 = getRealtimeData();
                //TODO
//                if (var1 != null) {
//                    StarWhitePanel2.this.sender.sendRealtimeData(var1);
//                }

            }
        };
        this.sendTimer.schedule(this.sendTimerTask, 100L, 100L);
    }

    public void pause() {
        this.onPublish = false;
        if (this.sendTimer != null) {
            this.sendTimer.cancel();
            this.sendTimer = null;
            this.sendTimerTask.cancel();
            this.sendTimerTask = null;
        }

    }

    /**
     * 更换画笔颜色
     *
     * @param color
     */
    public void setSelectColor(int color) {
        this.currentColor = color;
    }

    /**
     * 开启激光笔
     */
    public void laserPenOn() {
        this.isLaserOn = true;
    }

    /**
     * 关闭激光笔
     */
    public void laserPenOff() {
        this.isLaserOn = false;
        this.laserPoint = null;
        String str = ACTION.LASER_PEN_END.ordinal() + ":0,0";
        //{"account":"1233","data":"13:0,0"}
        NLog.e("TAGTAG__PANEL_laserPenOff", str);
        this.sendQueue.add(str);
        this.postInvalidate();
    }

    /**
     * 撤销
     */
    public void revoke() {
        if (!this.groupMap.containsKey(this.selfTag)) {
            this.groupMap.put(this.selfTag, new ArrayList());
        }

        if (((ArrayList)this.groupMap.get(this.selfTag)).size() > 0) {
            ((ArrayList)this.groupMap.get(this.selfTag)).remove(((ArrayList)this.groupMap.get(this.selfTag)).size() - 1);
        }

        this.postInvalidate();
        if (this.onPublish) {
            this.sendQueue.clear();
            JSONObject jso = new JSONObject();

            try {
                jso.put("account", this.selfTag);
                jso.put("data", ACTION.REVOKE.ordinal() + ":0,0");
            } catch (JSONException var3) {
                var3.printStackTrace();
            }
            //{"account":"1233","data":"4:0,0"}
            NLog.e("TAGTAG__PANEL_revoke", jso.toString());

            byte[] var2 = jso.toString().getBytes();
//            TODO
//            if (var2 != null) {
//                this.sender.sendRealtimeData(var2);
//            }
        }

    }

    /**
     * 清空画板数据
     */
    public void clean() {
        Iterator iterator = this.groupMap.values().iterator();

        while(iterator.hasNext()) {
            ArrayList list = (ArrayList)iterator.next();
            list.clear();
        }

        this.postInvalidate();
        if (this.onPublish) {
            this.sendQueue.clear();

            JSONObject jso = new JSONObject();
            try {
                jso.put("account", this.selfTag);
                jso.put("data", ACTION.CLEAR.ordinal() + ":0,0");
            } catch (JSONException var3) {
                var3.printStackTrace();
            }
            //{"account":"1233","data":"6:0,0"}
            NLog.e("TAGTAG__PANEL_clean", jso.toString());

            byte[] var5 = jso.toString().getBytes();
//            TODO
//            if (var5 != null) {
//                this.sender.sendRealtimeData(var5);
//            }
        }

    }

    private byte[] getRealtimeData() {
        ArrayList list = new ArrayList();
        this.sendQueue.drainTo(list);//移除此队列中所有可用的元素到ArrayList
        if (list.size() == 0) {
            return null;
        } else {
            String str = "";

            for(int i = 0; i < list.size(); ++i) {
                str = str + list.get(i);
                if (i != list.size() - 1) {
                    str = str + ";";
                }
            }

            JSONObject jso = new JSONObject();
            try {
                jso.put("account", this.selfTag);
                jso.put("data", str);
            } catch (JSONException var5) {
                var5.printStackTrace();
            }

            //{"account":"1233","data":"1:0.24316712,0.17548175,0;2:0.24316712,0.17548175,0;2:0.24570204,0.1780559,0"}
            //{"account":"1233","data":"2:0.25656226,0.1878416,0;2:0.2753017,0.20308383,0;2:0.32257345,0.23937926,0;2:0.34762588,0.25951898,0;2:0.37676045,0.2811303,0"}
            //{"account":"1233","data":"3:0.3524776,0.8033347,-16777216"}
            //{"account":"1233","data":"12:0.3433832,0.7353336,-16777216;12:0.32978994,0.7172554,-16777216;12:0.32524273,0.7131488,-16777216;12:0.32524273,0.7131488,-16777216"}
            NLog.e("TAGTAG__PANEL_getRealtimeData", jso.toString());
            return jso.toString().getBytes();
        }
    }

    public void setPaintData(byte[] data_byte, String fromID) {
        if (data_byte != null) {
            String realtimeData = new String(data_byte);
            NLog.e("TAGTAG__PANEL_setPaintData", "fromID " + fromID);
            NLog.e("TAGTAG__PANEL_setPaintData", "RealtimeData " + realtimeData);
            if (TextUtils.isEmpty(realtimeData)) {
                return;
            }

            try {
                JSONObject jso = new JSONObject(realtimeData);
                if (jso.has("account")) {
                    String account = jso.getString("account");
                    if (TextUtils.isEmpty(account)) {
                        return;
                    }

                    if (!this.groupMap.containsKey(account)) {
                        this.groupMap.put(account, new ArrayList());
                    }

                    if (jso.has("data")) {
                        String data = jso.getString("data");
                        if (TextUtils.isEmpty(data)) {
                            return;
                        }

                        //1:0.24316712,0.17548175,0
                        String[] var = data.split(";");
                        for(int i = 0; i < var.length; ++i) {
                            String item = var[i];
                            if (!TextUtils.isEmpty(item)) {
                                int type = Integer.parseInt(item.split(":")[0]);
                                String[] var1 = item.split(":")[1].split(",");
                                Point point = null;
                                int color = 0;
                                if (var1.length > 1) {
                                    point = new Point((int)((float)this.theWidth * Float.parseFloat(var1[0])), (int)((float)this.theHeight * Float.parseFloat(var1[1])));
                                }

                                if (var1.length > 2) {
                                    color = Integer.parseInt(var1[2]);
                                }

                                if (type == ACTION.START.ordinal()) {//开始滑动
                                    DrawLineData drawLineData = new DrawLineData();
                                    drawLineData.color = color;
                                    if (point != null) {
                                        drawLineData.list.add(point);
                                    }

                                    ((ArrayList)this.groupMap.get(account)).add(drawLineData);
                                } else if (type == ACTION.MOVE.ordinal()) {//滑动
                                    if (point != null) {
                                        ((DrawLineData)((ArrayList)this.groupMap.get(account)).get(((ArrayList)this.groupMap.get(account)).size() - 1)).list.add(point);
                                    }
                                } else if (type == ACTION.END.ordinal()) {//结束
                                    if (point != null) {
                                        ((DrawLineData)((ArrayList)this.groupMap.get(account)).get(((ArrayList)this.groupMap.get(account)).size() - 1)).list.add(point);
                                    }
                                } else if (type == ACTION.REVOKE.ordinal()) {//撤回
                                    if (((ArrayList)this.groupMap.get(account)).size() > 0) {
                                        ((ArrayList)this.groupMap.get(account)).remove(((ArrayList)this.groupMap.get(account)).size() - 1);
                                    }
                                } else if (type == ACTION.CLEAR.ordinal()) {//清空
                                    this.groupMap.clear();
                                } else if (type == ACTION.LASER_PEN.ordinal()) {//开启激光
                                    this.laserPoint = point;
                                    this.laserColor = color;
                                } else if (type == ACTION.LASER_PEN_END.ordinal()) {//关闭激光
                                    this.laserPoint = null;
                                } else if (type == ACTION.FILE.ordinal()) {//文件
                                    if (!"0".equals(var1[0]) && !"0".equals(var1[1])) {
                                        String var18 = URLDecoder.decode(var1[3], "UTF-8");//资源字符串
//                                        TODO
//                                        ReadHttpImg var19 = new ReadHttpImg();
//                                        var19.addListener(new ReadHttpImgCallback() {
//                                            public void callback(boolean var1, Bitmap var2, String var3) {
//                                                StarWhitePanel2.this.imgBitmap = var2;
//                                                StarWhitePanel2.this.postInvalidate();
//                                            }
//                                        });
//                                        var19.getBitmapFromNet(var18);
                                    } else if (this.imgBitmap != null) {
                                        this.imgBitmap.recycle();
                                        this.imgBitmap = null;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception var20) {
                var20.printStackTrace();
            }
            this.postInvalidate();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.imgBitmap != null) {
            new Point(this.theWidth / 2, this.theHeight / 2);
            //取宽高中的最小值
            float width = Math.min((float)this.theWidth / (float)this.imgBitmap.getWidth(), (float)this.theHeight / (float)this.imgBitmap.getHeight());
            Matrix matrix = new Matrix();
            matrix.postScale(width, width);
            matrix.postTranslate(((float)this.theWidth - (float)this.imgBitmap.getWidth() * width) / 2.0F, ((float)this.theHeight - (float)this.imgBitmap.getHeight() * width) / 2.0F);
            canvas.drawBitmap(this.imgBitmap, matrix, this.paint);
        }

        Iterator iterator = this.groupMap.keySet().iterator();
        while(true) {
            ArrayList list;
            do {
                if (!iterator.hasNext()) {
                    if (this.laserPoint != null) {//开启激光笔状态
                        this.paint.setAntiAlias(true);
                        this.paint.setColor(this.laserColor);
                        canvas.drawCircle((float)this.laserPoint.x, (float)this.laserPoint.y, 10.0F, this.paint);
                    }
                    return;
                }
                String key = (String)iterator.next();
                list = (ArrayList)this.groupMap.get(key);
            } while(list.size() <= 0);

            Iterator iterator1 = list.iterator();
            while(iterator1.hasNext()) {
                DrawLineData drawLineData = (DrawLineData)iterator1.next();
                this.paint.setColor(drawLineData.color);

                ArrayList list1 = (ArrayList)drawLineData.list.clone();//克隆
                Iterator iterator2 = list1.iterator();
                Point pointStart = null;
                Point pointEnd = null;
                while(iterator2.hasNext()) {
                    if (pointStart == null) {
                        pointStart = (Point)iterator2.next();
                    } else {
                        if (pointEnd != null) {
                            pointStart = pointEnd;
                        }
                        pointEnd = (Point)iterator2.next();
                        if (pointEnd.x > 0) {
                            canvas.drawLine((float)pointStart.x, (float)pointStart.y, (float)pointEnd.x, (float)pointEnd.y, this.paint);
                        } else {
                            pointStart = null;
                            pointEnd = null;
                        }
                    }
                }
            }
        }
    }

    class DrawLineData {
        public ArrayList list = new ArrayList();
        public int color;

        DrawLineData() {
        }
    }

    private class OnTouchListenerImp implements View.OnTouchListener {
        private OnTouchListenerImp() {
        }

        public boolean onTouch(View var1, MotionEvent event) {
            if (!onPublish) {
                return false;
            } else {
                Point point = new Point((int)event.getX(), (int)event.getY());
                String sendContent = "" + event.getX() / (float)theWidth + "," + event.getY() / (float)theHeight + "," + currentColor;
                ACTION action = null;
                if (isLaserOn) {
                    //激光笔
                    if (event.getAction() != event.ACTION_DOWN && event.getAction() != event.ACTION_MOVE) {
                        if (event.getAction() == event.ACTION_UP) {
                            sendContent = ACTION.LASER_PEN.ordinal() + ":" + sendContent;
                        }
                    } else {
                        sendContent = ACTION.LASER_PEN.ordinal() + ":" + sendContent;
                    }

                    sendQueue.add(sendContent);
                    laserPoint = point;
                    laserColor = currentColor;
                    postInvalidate();
                } else {
                    if (event.getAction() == event.ACTION_DOWN) {
                        sendContent = ACTION.START.ordinal() + ":" + sendContent;
                        action = ACTION.START;
                    } else if (event.getAction() == event.ACTION_UP) {
                        sendContent = ACTION.END.ordinal() + ":" + sendContent;
                        action = ACTION.END;
                    } else if (event.getAction() == event.ACTION_MOVE) {
                        sendContent = ACTION.MOVE.ordinal() + ":" + sendContent;
                        action = ACTION.MOVE;
                    }

                    if (event.getAction() == event.ACTION_DOWN ||
                            event.getAction() == event.ACTION_UP ||
                            event.getAction() == event.ACTION_MOVE) {
                        sendQueue.add(sendContent);
                        if (!groupMap.containsKey(selfTag)) {
                            groupMap.put(selfTag, new ArrayList());
                        }
                        DrawLineData drawLineData;
                        if (action == ACTION.START) {
                            drawLineData = new DrawLineData();
                            drawLineData.color = currentColor;
                            if (point != null) {
                                drawLineData.list.add(point);
                            }
                            ((ArrayList)groupMap.get(selfTag)).add(drawLineData);
                        } else if (action == ACTION.MOVE) {
                            if (((ArrayList)groupMap.get(selfTag)).size() == 0) {
                                drawLineData = new DrawLineData();
                                drawLineData.color = currentColor;
                                ((ArrayList)groupMap.get(selfTag)).add(drawLineData);
                            }
                            ((DrawLineData)((ArrayList)groupMap.get(selfTag)).get(((ArrayList)groupMap.get(selfTag)).size() - 1)).list.add(point);
                        } else if (action == ACTION.END && ((ArrayList)groupMap.get(selfTag)).size() > 0 && point != null) {
                            ((DrawLineData)((ArrayList)groupMap.get(selfTag)).get(((ArrayList)groupMap.get(selfTag)).size() - 1)).list.add(point);
                        }
                        postInvalidate();
                    }
                }

                return true;
            }
        }
    }

    public static enum ACTION {
        EMPTY,// 0
        START,//开始 1
        MOVE,//移动 2
        END,//结束 3
        REVOKE,//撤回 4
        SERIAL,// 5
        CLEAR,//清空 6
        CLEAR_ACK,// 7
        SYNC_REQUEST,// 8
        SYNC,// 9
        SYNC_PREPARE,// 10
        SYNC_PREPARE_ACK,// 11
        LASER_PEN,//开启激光 12
        LASER_PEN_END,//关闭激光 13
        FILE;//文件>图片 14

        private ACTION() {
        }
    }
}
