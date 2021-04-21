package com.yjfshop123.live.ui.widget.dialogorPopwindow;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

public class IOSDateTotalDialog implements OnWheelChangedListener {

    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_title;

    private WheelView yearwheel;
    private WheelView monthwheel;
    private WheelView daywheel;
    private TextView btn_cancel;
    private TextView btn_confirm;

    private Display display;

    private String[] years;
    private String[] months;
    private String[] days;
    private ArrayList<Integer> yearDatas = new ArrayList<>();
    private ArrayList<Integer> monthDatas = new ArrayList<>();
    private ArrayList<Integer> daysDatas = new ArrayList<>();
    private String dateStr = "";
    private int yearPosition = 49;
    private int monthPosition = -1;
    private int dayPosition = -1;

    public String getDateStr() {
        return dateStr;
    }

    public IOSDateTotalDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public IOSDateTotalDialog builder() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_date_total_alert_dialog, null);

        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        yearwheel = (WheelView) view.findViewById(R.id.yearwheel);
        monthwheel = (WheelView) view.findViewById(R.id.monthwheel);
        daywheel = (WheelView) view.findViewById(R.id.daywheel);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_confirm = view.findViewById(R.id.btn_confirm);

        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);

        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.80), ViewGroup.LayoutParams.WRAP_CONTENT));

        initDatas();
        setView();

        return this;
    }

    public IOSDateTotalDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            txt_title.setText(title);
        }
        return this;
    }

    public IOSDateTotalDialog setPositiveButton(String text, final View.OnClickListener listener) {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public IOSDateTotalDialog setNegativeButton(String text, final View.OnClickListener listener) {
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    /**
     * 设置显示
     */
    private void setLayout() {
        dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private void initDatas() {
        for (int i = 0; i <= 100; i++) {
            yearDatas.add(1970 + i);
        }
        years = new String[yearDatas.size()];
        for (int i = 0; i < yearDatas.size(); i++) {
            years[i] = yearDatas.get(i) + context.getString(R.string.year);
        }
        for (int i = 1; i <= 12; i++) {
            monthDatas.add(i);
        }
        months = new String[monthDatas.size()];
        for (int i = 0; i < monthDatas.size(); i++) {
            months[i] = monthDatas.get(i) + context.getString(R.string.month);
        }

        for (int i = 1; i <= getCurrentMonthLastDay(); i++) {
            daysDatas.add(i);
        }
        days = new String[daysDatas.size()];
        for (int i = 0; i < daysDatas.size(); i++) {
            days[i] = daysDatas.get(i) + context.getString(R.string.date);
        }
    }

    private void updateAreaWheel() {
        daysDatas = new ArrayList<>();
        for (int i = 1; i <= getMonthLastDay(yearDatas.get(yearPosition), monthDatas.get(monthPosition)); i++) {
            daysDatas.add(i);
        }
        days = new String[daysDatas.size()];
        for (int i = 0; i < daysDatas.size(); i++) {
            days[i] = daysDatas.get(i) + context.getString(R.string.date);
        }
        daywheel.setViewAdapter(new ArrayWheelAdapter<String>(context, days, R.layout.wheel_layout, R.id.wheel_layout_tv));
        daywheel.setCurrentItem(0);
    }

    private void setView() {
        ArrayWheelAdapter arrayWheelAdapter = new ArrayWheelAdapter<String>(context, years, R.layout.wheel_layout, R.id.wheel_layout_tv);
//        arrayWheelAdapter.setItemResource();
        yearwheel.setViewAdapter(arrayWheelAdapter);
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        for (int i = 0; i < yearDatas.size(); i++) {
            if (yearDatas.get(i) == year)
                yearwheel.setCurrentItem(i);
        }
//        yearwheel.setCurrentItem(49);

        ArrayWheelAdapter monthWheelAdapter = new ArrayWheelAdapter<String>(context, months, R.layout.wheel_layout, R.id.wheel_layout_tv);
        monthwheel.setViewAdapter(monthWheelAdapter);
        int month = date.get(Calendar.MONTH) + 1;
        for (int i = 0; i < monthDatas.size(); i++) {
            if (monthDatas.get(i) == month)
                monthwheel.setCurrentItem(i);
        }
        ArrayWheelAdapter daysWheelAdapter = new ArrayWheelAdapter<String>(context, days, R.layout.wheel_layout, R.id.wheel_layout_tv);
        daywheel.setViewAdapter(daysWheelAdapter);
        int day = date.get(Calendar.DATE) ;
        for (int i = 0; i < daysDatas.size(); i++) {
            if (daysDatas.get(i) == day)
                daywheel.setCurrentItem(i);
        }
        //监听
        yearwheel.addChangingListener(this);
        monthwheel.addChangingListener(this);
        daywheel.addChangingListener(this);
        //显示的单元格数目
        yearwheel.setVisibleItems(3);
        monthwheel.setVisibleItems(3);
        daywheel.setVisibleItems(3);
        yearPosition=yearwheel.getCurrentItem();
        monthPosition=monthwheel.getCurrentItem();
        dayPosition=daywheel.getCurrentItem();
    }

    public void show() {
        setLayout();
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private boolean year = false;
    private boolean month = false;

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == yearwheel) {
            yearPosition = yearwheel.getCurrentItem();
            monthPosition = monthwheel.getCurrentItem();
            dateStr = yearDatas.get(yearPosition) + "-" + (monthDatas.get(monthPosition) >= 10 ? monthDatas.get(monthPosition) : "0" + monthDatas.get(monthPosition)) + "-01";
            updateAreaWheel();
        } else if (wheel == monthwheel) {
            monthPosition = monthwheel.getCurrentItem();
            dateStr = yearDatas.get(yearPosition) + "-" + (monthDatas.get(monthPosition) >= 10 ? monthDatas.get(monthPosition) : "0" + monthDatas.get(monthPosition)) + "-01";
            updateAreaWheel();
        }
        else if (wheel == daywheel) {
            dayPosition = daywheel.getCurrentItem();
            dateStr = yearDatas.get(yearPosition) + "-"
                    + (monthDatas.get(monthPosition) >= 10 ? monthDatas.get(monthPosition) : "0" + monthDatas.get(monthPosition))+ "-"
                    + (daysDatas.get(dayPosition) >= 10 ? daysDatas.get(dayPosition) : "0" + daysDatas.get(dayPosition)) ;
        }
    }

    /**
     * 得到指定月的天数
     */
    public static int getMonthLastDay(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 取得当月天数
     */
    public static int getCurrentMonthLastDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }
}
