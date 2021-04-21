package com.yjfshop123.live.ui.widget.dialogorPopwindow;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.activity.ShiMingRenZhengActivity;

import static com.yjfshop123.video_shooting.utils.UIUtils.getResources;

public class VerfyTipsDialog extends Dialog implements View.OnClickListener {
    private Activity context;
    private Button confir;
    private ImageView close;
private int display;
    public VerfyTipsDialog(Activity context) {
        super(context, R.style.customDialog);
        this.context = context;

        Resources resources = context.getResources();
        //获取屏幕数据
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        //获取屏幕宽高，单位是像素
        display = displayMetrics.widthPixels;
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_verfy_id);
        confir = findViewById(R.id.right_now_verfy);
        close = findViewById(R.id.close);
        confir.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                context.finish();
                break;
            case R.id.right_now_verfy:
                Intent intent=new Intent();
                intent.setClass(context, ShiMingRenZhengActivity.class);
                context.startActivity(intent);
                break;
        }
    }
    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = display*4/5;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        getWindow().setAttributes(layoutParams);
        setCancelable(false);//点击弹窗以外的无效果

    }
}
