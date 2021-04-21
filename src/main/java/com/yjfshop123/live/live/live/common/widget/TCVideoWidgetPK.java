package com.yjfshop123.live.live.live.common.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.bumptech.glide.Glide;
import com.tencent.rtmp.ui.TXCloudVideoView;

public class TCVideoWidgetPK {

    private FrameLayout frameLayout_pk;
    private TXCloudVideoView video_player_pk;
    private FrameLayout loading_background_pk;
    private ImageView loading_imageview_pk;
    private ImageView cover_imageview_pk;
    private Button btn_kick_out_pk;
    private boolean isPK;

    public interface OnRoomViewListener {
        boolean onKickUser();
    }

    public TCVideoWidgetPK(Activity context, int widthPixels, int topMargin, boolean isPK, final OnRoomViewListener l) {

        frameLayout_pk = context.findViewById(R.id.frameLayout_pk);
        video_player_pk = context.findViewById(R.id.video_player_pk);
        loading_background_pk = context.findViewById(R.id.loading_background_pk);
        loading_imageview_pk = context.findViewById(R.id.loading_imageview_pk);
        cover_imageview_pk = context.findViewById(R.id.cover_imageview_pk);
        btn_kick_out_pk = context.findViewById(R.id.btn_kick_out_pk);
        frameLayout_pk.setVisibility(View.VISIBLE);
        video_player_pk.setVisibility(View.VISIBLE);
        this.isPK = isPK;
        if (isPK){
            btn_kick_out_pk.setVisibility(View.INVISIBLE);
        }else {
            btn_kick_out_pk.setVisibility(View.VISIBLE);
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(widthPixels, (int)(widthPixels * Const.ratio));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.topMargin = topMargin;
        frameLayout_pk.setLayoutParams(layoutParams);

        btn_kick_out_pk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (l != null) {
                    if (!l.onKickUser()){
                        btn_kick_out_pk.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    public TXCloudVideoView getVideoView(){
        return video_player_pk;
    }

    public void startLoading() {
        loading_background_pk.setVisibility(View.VISIBLE);
        loading_imageview_pk.setVisibility(View.VISIBLE);
        cover_imageview_pk.setVisibility(View.GONE);
        if (!isPK){
            btn_kick_out_pk.setVisibility(View.INVISIBLE);
        }
        loading_imageview_pk.setImageResource(R.drawable.mm_loading_anim);
        AnimationDrawable ad = (AnimationDrawable) loading_imageview_pk.getDrawable();
        ad.start();
    }

    public void stopLoading() {
        loading_background_pk.setVisibility(View.GONE);
        loading_imageview_pk.setVisibility(View.GONE);
        cover_imageview_pk.setVisibility(View.GONE);
        if (!isPK){
            btn_kick_out_pk.setVisibility(View.VISIBLE);
        }
        AnimationDrawable ad = (AnimationDrawable) loading_imageview_pk.getDrawable();
        if (ad != null) {
            ad.stop();
        }
    }

    public void close() {
        frameLayout_pk.setVisibility(View.GONE);
        video_player_pk.setVisibility(View.GONE);
        loading_background_pk.setVisibility(View.GONE);
        loading_imageview_pk.setVisibility(View.GONE);
        cover_imageview_pk.setVisibility(View.GONE);
        btn_kick_out_pk.setVisibility(View.GONE);
        AnimationDrawable ad = (AnimationDrawable) loading_imageview_pk.getDrawable();
        if (ad != null) {
            ad.stop();
        }
    }

    public void audioLive(Context context, String avatar) {
        loading_background_pk.setVisibility(View.VISIBLE);
        cover_imageview_pk.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(avatar)
                .into(cover_imageview_pk);
    }

}
