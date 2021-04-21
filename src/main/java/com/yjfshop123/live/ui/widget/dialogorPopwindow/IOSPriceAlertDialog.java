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

public class IOSPriceAlertDialog implements OnWheelChangedListener {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_title;
    private WheelView agewheel;

    private TextView btn_cancel;
    private TextView btn_confirm;
    private Display display;

    private static int provincePosition = 0;

    private ArrayList<String> ageDatas = new ArrayList<>();
    private String[] ages;

    public IOSPriceAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public IOSPriceAlertDialog builder() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_age_alert_dialog, null);

        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        agewheel = (WheelView) view.findViewById(R.id.agewheel);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_confirm = view.findViewById(R.id.btn_confirm);

        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);

        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.80), ViewGroup.LayoutParams.WRAP_CONTENT));

        setView();
        return this;
    }

    public void setData(ArrayList<Integer> prices){
        ageDatas.clear();
        ageDatas.add(context.getString(R.string.other_mianfei));
        for(int i : prices){
            ageDatas.add(i+"");
        }
        ages = new String[ageDatas.size()];
        for (int i = 0; i < ageDatas.size(); i++) {
            ages[i] = ageDatas.get(i);
        }
    }

    public IOSPriceAlertDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)){
            txt_title.setText(title + "(" + context.getString(R.string.my_jinbi) + ")");
        }
        return this;
    }

    public IOSPriceAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public IOSPriceAlertDialog setPositiveButton(String text, final View.OnClickListener listener) {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public IOSPriceAlertDialog setNegativeButton(String text, final View.OnClickListener listener) {
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setView() {
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
