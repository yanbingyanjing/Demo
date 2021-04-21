package com.yjfshop123.live.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.utils.SystemUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class TargetProgressView extends RelativeLayout {

    TextView progress;

    LinearLayout youbiao;
    private Context context;
    View progress_view;


    public TargetProgressView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TargetProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public TargetProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    private void init() {
        View view = View.inflate(context, R.layout.my_progress_newui, this);
        //获取子控件对象
        progress = view.findViewById(R.id.progress);

        youbiao = view.findViewById(R.id.youbiao);
        progress_view = view.findViewById(R.id.progress_view);

    }


    public void setProgress(float progress) {
        this.progress.setText((int)progress + "%");
        NLog.d("进度条", getWidth() + "");
        float wi = getWidth() * progress / 100;
        if (getWidth()-wi<youbiao.getWidth()) {
            youbiao.setBackgroundResource(R.mipmap.progress_pao_fan);
            youbiao.setTranslationX(wi-youbiao.getWidth());
        } else{
            youbiao.setBackgroundResource(R.mipmap.progress_pao);
            youbiao.setTranslationX(wi);
        }
        if(progress>=100){
            progress_view.setBackgroundResource(R.mipmap.progress_manle);
        }else  progress_view.setBackgroundResource(R.mipmap.progress_degress_new);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progress_view.getLayoutParams();
        //获取当前控件的布局对象
        params.height = MATCH_PARENT;//设置当前控件布局的高度
//        if(progress>=100)params.width = (int)(wi*1.3);
//        else
            params.width = (int)wi;
        progress_view.setLayoutParams(params);

    }


}