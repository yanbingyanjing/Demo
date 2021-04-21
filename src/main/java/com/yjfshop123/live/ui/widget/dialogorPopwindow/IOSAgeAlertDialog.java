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

import java.util.ArrayList;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

public class IOSAgeAlertDialog implements OnWheelChangedListener {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_title;
    private String mContent;

    private WheelView agewheel;

    private TextView btn_cancel;
    private TextView btn_confirm;
    private Display display;

    private static int provincePosition = 0;

    private ArrayList<String> ageDatas = new ArrayList<>();
    private String[] ages;

    public IOSAgeAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public IOSAgeAlertDialog builder(String content) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_age_alert_dialog, null);

        lLayout_bg = view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        agewheel = (WheelView) view.findViewById(R.id.agewheel);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_confirm = view.findViewById(R.id.btn_confirm);

        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);

        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.80), ViewGroup.LayoutParams.WRAP_CONTENT));

        mContent = content;
        initDatas();
        setView();

        return this;
    }

    public IOSAgeAlertDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)){
            txt_title.setText(title);
        }
        return this;
    }

    public IOSAgeAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public IOSAgeAlertDialog setPositiveButton(String text, final View.OnClickListener listener) {
        if ("".equals(text)) {
            btn_confirm.setText(R.string.other_ok);
        } else {
            btn_confirm.setText(text);
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public IOSAgeAlertDialog setNegativeButton(String text, final View.OnClickListener listener) {
        if ("".equals(text)) {
            btn_cancel.setText(R.string.other_cancel);
        } else {
            btn_cancel.setText(text);
        }
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private void initDatas() {
        for (int i = 18; i < 70; i++) {
            ageDatas.add(i + "");
        }
        ages = new String[ageDatas.size()];
        for (int i = 0; i < ageDatas.size(); i++) {
            ages[i] = ageDatas.get(i);
        }
    }

    private void setView() {
        ArrayWheelAdapter arrayWheelAdapter = new ArrayWheelAdapter<String>(context, ages, R.layout.wheel_layout, R.id.wheel_layout_tv);
//        arrayWheelAdapter.setItemResource();
        agewheel.setViewAdapter(arrayWheelAdapter);

        int pos = -1;
        for (int i = 0; i < ages.length; i++) {
            if (mContent.equals(ages[i])){
                pos = i;
                break;
            }
        }
        if (pos == -1 && ages.length > 1){
            agewheel.setCurrentItem(1);
            ageStr = ages[1];
        }else {
            agewheel.setCurrentItem(pos);
            ageStr = ages[pos];
        }

        //监听
        agewheel.addChangingListener(this);
        //显示的单元格数目
        agewheel.setVisibleItems(3);
    }

    public void show() {
        dialog.show();
    }

    private String ageStr;

    public String getAgeStr() {
        return ageStr;
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == agewheel) {
            provincePosition = agewheel.getCurrentItem();
            ageStr = ages[provincePosition];
        }
    }
}
