/*
package com.yjfshop123.live.server.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.utils.ThemeColorUtils;

public class CatDialogHint1 extends Dialog {

    private int type;
    private Context context;

    public CatDialogHint1(Context context, int type) {
        super(context, R.style.dialogFullscreen2);
        this.type = type;
        this.context = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_cat_1);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(layoutParams);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        FrameLayout dialog_cat_1_fl = findViewById(R.id.dialog_cat_1_fl);
        TextView dialog_cat_1_cz = findViewById(R.id.dialog_cat_1_cz);

        FrameLayout dialog_cat_1_cancel_fl = findViewById(R.id.dialog_cat_1_cancel_fl);
        ImageView dialog_cat_1_cancel_iv = findViewById(R.id.dialog_cat_1_cancel_iv);
        TextView dialog_cat_1_cancel_tv = findViewById(R.id.dialog_cat_1_cancel_tv);

        if (type == 201){
            dialog_cat_1_cz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRechargeListener != null) {
                        mRechargeListener.onClick();
                    }
                }
            });

            dialog_cat_1_cancel_fl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            dialog_cat_1_fl.setBackgroundResource(R.drawable.cat_hint_1);
            dialog_cat_1_cz.setVisibility(View.VISIBLE);
            dialog_cat_1_cancel_fl.setVisibility(View.VISIBLE);
            GradientDrawable controlsAdd_CZ = (GradientDrawable) dialog_cat_1_cz.getBackground();
            controlsAdd_CZ.setColor(ThemeColorUtils.getThemeColor());
            dialog_cat_1_cancel_tv.setTextColor(ThemeColorUtils.getThemeColor());
            dialog_cat_1_cancel_iv.setColorFilter(ThemeColorUtils.getThemeColor());
        }else if (type == 1){
            dialog_cat_1_fl.setBackgroundResource(R.drawable.cat_hint_3);
            dialog_cat_1_cz.setVisibility(View.GONE);
            dialog_cat_1_cancel_fl.setVisibility(View.GONE);
        }else if (type == 202){
            dialog_cat_1_fl.setBackgroundResource(R.drawable.cat_hint_2);
            dialog_cat_1_cz.setVisibility(View.GONE);
            dialog_cat_1_cancel_fl.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (type != 201){
            dismiss();
        }
        return true;
    }

    private RechargeListener mRechargeListener;

    public interface RechargeListener {
        void onClick();
    }

    public void setRechargeListener(RechargeListener listener){
        this.mRechargeListener = listener;
    }

}
*/
