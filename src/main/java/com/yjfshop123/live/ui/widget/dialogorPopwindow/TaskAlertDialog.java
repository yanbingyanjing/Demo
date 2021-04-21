package com.yjfshop123.live.ui.widget.dialogorPopwindow;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;

import java.util.ArrayList;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

public class TaskAlertDialog implements OnWheelChangedListener {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;

    private WheelView agewheel;

    private TextView btn_cancel;
    private TextView btn_confirm;
    private Display display;

    private static int provincePosition = 0;

    private String[] ages;

    public TaskAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public TaskAlertDialog builder() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.task_alert_dialog, null);

        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        agewheel = (WheelView) view.findViewById(R.id.agewheel);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_confirm = view.findViewById(R.id.btn_confirm);

        TextView task_dia_title = view.findViewById(R.id.task_dia_title);
        task_dia_title.setText(mTitle);

        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);

        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.80), ViewGroup.LayoutParams.WRAP_CONTENT));

        setView();
        return this;
    }

    private String mTitle = "";
    public TaskAlertDialog setTiTle(String title) {
        mTitle = title;
        return this;
    }

    public TaskAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public TaskAlertDialog setPositiveButton(String text, final View.OnClickListener listener) {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public TaskAlertDialog setNegativeButton(String text, final View.OnClickListener listener) {
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public void setData(ArrayList<Integer> prices){
        ages = new String[prices.size()];
        for (int i = 0; i < prices.size(); i++) {
            ages[i] = String.valueOf(prices.get(i));
        }
    }

    public void setData2(){
        ages = new String[24];
        for (int i = 0; i < 24; i++) {
            int data = i * 5 + 5;
            ages[i] = String.valueOf(data);
        }
    }

    private void setView() {
        if (ages == null){
            return;
        }
        ArrayWheelAdapter arrayWheelAdapter = new ArrayWheelAdapter<String>(context, ages, R.layout.wheel_layout, R.id.wheel_layout_tv);
//        arrayWheelAdapter.setItemResource();
        agewheel.setViewAdapter(arrayWheelAdapter);
        agewheel.setCurrentItem(0);

        //监听
        agewheel.addChangingListener(this);
        //显示的单元格数目
        agewheel.setVisibleItems(3);
        priceStr = ages[0];
    }

    public void show() {
        dialog.show();
    }

    private String priceStr;

    public String getPriceStr() {
        return priceStr;
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == agewheel) {
            provincePosition = agewheel.getCurrentItem();
            priceStr = ages[provincePosition];
        }
    }
}
