package com.yjfshop123.live.live.live.common.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yjfshop123.live.R;
import com.bumptech.glide.Glide;
import com.tencent.rtmp.ui.TXCloudVideoView;

public class TCVideoWidget {

    public TXCloudVideoView videoView;
    FrameLayout loadingBkg;
    ImageView loadingImg;
    ImageView coverImg;
    Button kickButton;
    public String userID;
    boolean          isUsed;

    public interface OnRoomViewListener {
        void onKickUser(String userId);
    }

    public TCVideoWidget(TXCloudVideoView view, Button button, FrameLayout loadingBkg, ImageView loadingImg, final OnRoomViewListener l, ImageView coverImg) {
        this.videoView = view;
        this.videoView.setVisibility(View.GONE);
        this.loadingBkg = loadingBkg;
        this.loadingImg = loadingImg;
        this.coverImg = coverImg;
        this.isUsed = false;
        this.kickButton = button;
        this.kickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kickButton.setVisibility(View.INVISIBLE);
                String userID = TCVideoWidget.this.userID;
                if (userID != null && l != null) {
                    l.onKickUser(userID);
                }
            }
        });
    }

    public void startLoading() {
        coverImg.setVisibility(View.GONE);
        kickButton.setVisibility(View.INVISIBLE);
        loadingBkg.setVisibility(View.VISIBLE);
        loadingImg.setVisibility(View.VISIBLE);
        loadingImg.setImageResource(R.drawable.mm_loading_anim);
        AnimationDrawable ad = (AnimationDrawable) loadingImg.getDrawable();
        ad.start();
    }

    public void stopLoading(boolean showKickoutBtn) {
        coverImg.setVisibility(View.GONE);
        kickButton.setVisibility(showKickoutBtn ? View.VISIBLE : View.GONE);
        loadingBkg.setVisibility(View.GONE);
        loadingImg.setVisibility(View.GONE);
        AnimationDrawable ad = (AnimationDrawable) loadingImg.getDrawable();
        if (ad != null) {
            ad.stop();
        }
    }

    public void stopLoading() {
        coverImg.setVisibility(View.GONE);
        kickButton.setVisibility(View.GONE);
        loadingBkg.setVisibility(View.GONE);
        loadingImg.setVisibility(View.GONE);
        AnimationDrawable ad = (AnimationDrawable) loadingImg.getDrawable();
        if (ad != null) {
            ad.stop();
        }
    }

    public void setUsed(boolean used){
        videoView.setVisibility(used ? View.VISIBLE : View.GONE);
        if (used == false) {
            stopLoading(false);
        }
        this.isUsed = used;
    }

    public void audioLive(Context context, String avatar) {
        loadingBkg.setVisibility(View.VISIBLE);
        coverImg.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(avatar)
                .into(coverImg);
    }
}

