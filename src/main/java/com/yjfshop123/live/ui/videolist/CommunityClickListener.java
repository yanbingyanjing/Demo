package com.yjfshop123.live.ui.videolist;

import android.view.View;
import android.widget.ImageView;

import com.waynell.videolist.widget.TextureVideoView;

public interface CommunityClickListener {
    //跳转详情
    void onContentClick(View view, int position);
    //头像点击
    void onPortraitClick(View view, int position);
    //点赞
    void onPraiseClick(View view, int position);
    //图片点击
    void onImgClick(View view, int position, int index);
    //视频点击
    void onVideo(TextureVideoView videoView, ImageView videoCover, ImageView videoBtn);
}
