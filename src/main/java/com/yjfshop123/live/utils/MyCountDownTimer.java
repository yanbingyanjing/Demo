package com.yjfshop123.live.utils;

import android.os.CountDownTimer;
import android.widget.Button;

import com.yjfshop123.live.App;
import com.yjfshop123.live.R;

/**
 *
 * 日期:2018/10/27
 * 描述:验证码倒计时
 **/
public class MyCountDownTimer extends CountDownTimer {

    private Button timeButton;

    public MyCountDownTimer(long millisInFuture, long countDownInterval,Button btn) {
        super(millisInFuture, countDownInterval);
        timeButton = btn;
    }

    //计时过程
    @Override
    public void onTick(long l) {
        //防止计时过程中重复点击
        timeButton.setClickable(false);
        timeButton.setText(l / 1000 + "s" + App.getInstance().getString(R.string.login_code1));

    }

    //计时完毕的方法
    @Override
    public void onFinish() {
        //重新给Button设置文字
        timeButton.setText(App.getInstance().getString(R.string.login_code));
        //设置可点击
        timeButton.setClickable(true);
    }
}
