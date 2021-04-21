package com.yjfshop123.live.ui;

import android.widget.ImageView;
import android.widget.TextView;

public interface IVideoView {
    void avatar(int position);
    void like(ImageView iv, TextView tv, int position);
    void reply(TextView tv, int position);
    void gift(int position);
    void follow(ImageView iv, int position);
    void share(int position);
    void opShop(int position);
}
