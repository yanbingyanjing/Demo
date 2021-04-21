package com.yjfshop123.live.utils.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;

/**
 *
 * 日期:2019/5/7
 * 描述:
 **/
public class GongYueDialog implements View.OnClickListener {

    private Context context;
    private Display display;
    private Dialog dialog;

    private LinearLayout rootLayout;
    private TextView text;
    private TextView cancelText;
    private TextView okText;

    private GongYueInterface gongYueInterface;

    public void setGongYueInterface(GongYueInterface gongYueInterface) {
        this.gongYueInterface = gongYueInterface;
    }

    public GongYueDialog(Activity context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public GongYueDialog buidle() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_gong_yue, null);

        rootLayout = view.findViewById(R.id.rootLayout);
        text = view.findViewById(R.id.text);
        cancelText = view.findViewById(R.id.cancelText);
        okText = view.findViewById(R.id.okText);

        cancelText.setOnClickListener(this);
        okText.setOnClickListener(this);

        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.80), (int) (display
                .getHeight() * 0.60)));

        return this;
    }

    private void setData() {
        String str1 = "    社区成员须遵守";
        String str2 = "《平台公约》";
        String str3 = "，共同建设绿色健康的社区环境！";

        SpannableString spanableInfo = new SpannableString(str1 + str2 + str3);
        spanableInfo.setSpan(new Clickable(this), str1.length(), str1.length() + str2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setText(spanableInfo);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void show() {
        setData();
        dialog.show();
    }

    public void dissmiss() {
        dialog.dismiss();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelText:
                dissmiss();
                break;
            case R.id.okText:
                gongYueInterface.okButton(view);
                break;
            case R.id.text:
                gongYueInterface.textClick(view);
                break;
        }
    }

    class Clickable extends ClickableSpan {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener l) {
            mListener = l;
        }

        /**
         * 重写父类点击事件
         */
        @Override
        public void onClick(View v) {
            avoidHintColor(v);
            mListener.onClick(v);
        }

        /**
         * 重写父类updateDrawState方法  我们可以给TextView设置字体颜色,背景颜色等等...
         */
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(context.getResources().getColor(R.color.color_style));
        }
    }

    private void avoidHintColor(View view){
        if(view instanceof TextView)
            ((TextView)view).setHighlightColor(context.getResources().getColor(android.R.color.transparent));
    }

    public interface GongYueInterface {
        void okButton(View view);
        void textClick(View view);
    }

}
