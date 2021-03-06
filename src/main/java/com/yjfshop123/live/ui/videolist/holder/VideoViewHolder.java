package com.yjfshop123.live.ui.videolist.holder;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.videolist.VideoListGlideModule;
import com.yjfshop123.live.ui.videolist.model.VideoItem;
import com.yjfshop123.live.ui.videolist.model.VideoLoadMvpView;
import com.yjfshop123.live.ui.videolist.target.VideoLoadTarget;
import com.yjfshop123.live.ui.videolist.target.VideoProgressTarget;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.pandaq.emoticonlib.PandaEmoTranslator;
import com.waynell.videolist.visibility.items.ListItem;
import com.waynell.videolist.widget.TextureVideoView;
import com.yjfshop123.live.utils.SystemUtils;

import java.io.File;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoViewHolder extends BaseViewHolder<VideoItem>
        implements VideoLoadMvpView, ViewPropertyAnimatorListener, ListItem {

    //
    @BindView(R.id.community_item_portrait)
    ImageView community_item_portrait;
    @BindView(R.id.community_item_nickname)
    TextView community_item_nickname;
    @BindView(R.id.community_item_location)
    TextView community_item_location;
    @BindView(R.id.community_item_title)
    TextView community_item_title;
    @BindView(R.id.community_item_content)
    TextView community_item_content;
    @BindView(R.id.community_item_label)
    TextView community_item_label;
    @BindView(R.id.community_item_comments)
    TextView community_item_comments;
    @BindView(R.id.community_item_praise)
    TextView community_item_praise;
    @BindView(R.id.community_item_sex)
    ImageView community_item_sex;
    @BindView(R.id.community_item_talent)
    ImageView community_item_talent;
    @BindView(R.id.community_item_vip)
    ImageView community_item_vip;
    @BindView(R.id.create_time)
    TextView create_time;

    //
    @BindView(R.id.community_item_cardview)
    CardView community_item_cardview;
    @BindView(R.id.community_item_video)
    TextureVideoView videoView;
    @BindView(R.id.community_item_video_cover)
    ImageView videoCover;
    @BindView(R.id.community_item_progress)
    CircularProgressBar progressBar;
    @BindView(R.id.community_item_video_btn)
    ImageView community_item_video_btn;

    private int videoState = STATE_IDLE;
    private String videoLocalPath;

    private final VideoProgressTarget progressTarget;
    private final VideoLoadTarget videoTarget;

    private static final int STATE_IDLE = 0;
    private static final int STATE_ACTIVED = 1;
    private static final int STATE_DEACTIVED = 2;

    private RequestOptions options_1;
    private RequestOptions options;
    private int height_video;

    private CommunityClickListener mItemClickListener;

    public VideoViewHolder(View view, int screenWidth) {
        super(view);

        videoView.setAlpha(0);
        videoTarget = new VideoLoadTarget(this);
        progressTarget = new VideoProgressTarget(videoTarget, progressBar);
        height_video = screenWidth;

        options_1 = new RequestOptions()
                .placeholder(R.drawable.loadding)
                .error(R.drawable.loadding)
//                .transforms(new CenterCrop(), new CircleCrop())
//                .circleCropTransform()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        options = new RequestOptions()
                .placeholder(R.drawable.imageloader)
                .error(R.drawable.imageloader)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

//        videoView.mute();
//        videoView.unMute();
//        videoView.isMute()
//        videoView.isHasAudio()
    }

    private void reset() {
        videoState = STATE_IDLE;
        videoView.stop();
        videoLocalPath = null;
        videoStopped();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBind(final int position, VideoItem iItem, CommunityClickListener itemClickListener) {
        mItemClickListener = itemClickListener;

        reset();
        if(!TextUtils.isEmpty(iItem.getListBean().getAvatar()))
        Glide.with(itemView.getContext())
                .load(CommonUtils.getUrl(iItem.getListBean().getAvatar()))
                .apply(options_1)
                .into(community_item_portrait);
        else {
            Glide.with(itemView.getContext())
                    .load(R.drawable.splash_logo)
                    .apply(options_1)
                    .into(community_item_portrait);
        }
        create_time.setText(iItem.getListBean().getPublish_time());
        community_item_nickname.setText(iItem.getListBean().getUser_nickname());
        if (TextUtils.isEmpty(iItem.getListBean().getCity_name())) {
            community_item_location.setVisibility(View.GONE);
        } else community_item_location.setVisibility(View.VISIBLE);
        community_item_location.setText(iItem.getListBean().getCity_name());
        String title = iItem.getListBean().getTitle();
        String content = iItem.getListBean().getContent();
        if (TextUtils.isEmpty(title)) {
            community_item_title.setVisibility(View.GONE);
        } else {
            community_item_title.setVisibility(View.VISIBLE);
//            community_item_title.setText(getContent(title));
            if (!TextUtils.isEmpty(content)) {
                community_item_title.setMaxLines(2);
                community_item_title.setText(PandaEmoTranslator
                        .getInstance()
                        .makeEmojiSpannable(title));
            } else {
                community_item_title.setMaxLines(10);
                SystemUtils.toggleEllipsize(itemView.getContext(), community_item_title,
                        94, PandaEmoTranslator
                                .getInstance()
                                .makeEmojiSpannable(title).toString(), itemView.getContext().getString(R.string.all__wen),
                        R.color.color_2D9DD8, false);
            }
        }

        if (TextUtils.isEmpty(content)) {
            community_item_content.setVisibility(View.GONE);
        } else {
            community_item_content.setVisibility(View.VISIBLE);
//            community_item_content.setText(getContent(content));
//            community_item_content.setText(PandaEmoTranslator
//                    .getInstance()
//                    .makeEmojiSpannable(content));
            SystemUtils.toggleEllipsize(itemView.getContext(), community_item_content,
                    94, PandaEmoTranslator
                            .getInstance()
                            .makeEmojiSpannable(content).toString(), itemView.getContext().getString(R.string.all__wen),
                    R.color.color_2D9DD8, false);
        }
        if (iItem.getListBean().getSex() == 1) {
            community_item_sex.setImageResource(R.mipmap.boy);
        } else {
            community_item_sex.setImageResource(R.mipmap.girl);
        }
        if (iItem.getListBean().getDaren_status() == 2) {
            community_item_talent.setVisibility(View.VISIBLE);
        } else {
            community_item_talent.setVisibility(View.GONE);
        }
        if (iItem.getListBean().getIs_vip() == 1) {
            community_item_vip.setVisibility(View.VISIBLE);
        } else {
            community_item_vip.setVisibility(View.GONE);
        }

        String circle_title = iItem.getListBean().getCircle_title();
        if (TextUtils.isEmpty(circle_title)) {
            community_item_label.setVisibility(View.GONE);
        } else {
            community_item_label.setVisibility(View.VISIBLE);
            community_item_label.setText("#" + circle_title + "#");
        }

        //community_item_comments.setText(iItem.getListBean().getReply_num() + "");
        //community_item_praise.setText(iItem.getListBean().getLike_num() + "");
        if (iItem.getListBean().getIs_like() == 0) {
            Drawable drawable = itemView.getContext().getResources().getDrawable(R.mipmap.xiaoshou);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
             drawable.setTint(itemView.getContext().getResources().getColor(R.color.color_999999));
            community_item_praise.setCompoundDrawables(drawable, null, null, null);
        } else {
            Drawable drawable = itemView.getContext().getResources().getDrawable(R.mipmap.xiaoshou);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            drawable.setTint(itemView.getContext().getResources().getColor(R.color.color_FAE2AE));
            community_item_praise.setCompoundDrawables(drawable, null, null, null);
        }

        //
        int width_ = iItem.getListBean().getVideo_list().get(0).getExtra_info().getWidth();
        int height_ = iItem.getListBean().getVideo_list().get(0).getExtra_info().getHeight();
        if (width_ >= height_) {//????????? ????????????
            //??????
            ViewGroup.LayoutParams params_c = community_item_cardview.getLayoutParams();
            params_c.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params_c.height = height_video * 5 / 9;
            community_item_cardview.setLayoutParams(params_c);

            ViewGroup.LayoutParams params_p = videoView.getLayoutParams();
            params_p.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params_p.height = ViewGroup.LayoutParams.MATCH_PARENT;
            videoView.setLayoutParams(params_p);
        } else {
            //??????
            ViewGroup.LayoutParams params_c = community_item_cardview.getLayoutParams();
            params_c.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params_c.height = height_video * 5 / 6;
            community_item_cardview.setLayoutParams(params_c);

            ViewGroup.LayoutParams params_p = videoView.getLayoutParams();
            params_p.width = (height_video * 5 / 6) * width_ / height_;
            params_p.height = height_video * 5 / 6;
            videoView.setLayoutParams(params_p);
        }

        //???????????? ????????????
        videoView.setAlpha(0);
        videoCover.setAlpha(1.f);
        community_item_video_btn.setVisibility(View.VISIBLE);

        Glide.with(itemView.getContext())
                .load(CommonUtils.getUrl(iItem.getListBean().getVideo_list().get(0).getCover_img()))
                .apply(options)
                .into(videoCover);

        progressTarget.setModel(CommonUtils.getUrl(iItem.getListBean().getVideo_list().get(0).getObject()));

        com.bumptech.xchat.Glide.with(itemView.getContext())
                .using(VideoListGlideModule.getOkHttpUrlLoader(), InputStream.class)
                .load(new com.bumptech.xchat.load.model.GlideUrl(CommonUtils.getUrl(iItem.getListBean().getVideo_list().get(0).getObject())))
                .as(File.class)
                .diskCacheStrategy(com.bumptech.xchat.load.engine.DiskCacheStrategy.SOURCE)
                .into(progressTarget);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onContentClick(v, position);
            }
        });

        community_item_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPortraitClick(v, position);
            }
        });

        community_item_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPortraitClick(v, position);
            }
        });

        community_item_praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPraiseClick(v, position);
            }
        });
    }

    private void cancelAlphaAnimate(View v) {
        ViewCompat.animate(v).cancel();
    }

    private void startAlphaAnimate(View v) {
        ViewCompat.animate(v).setListener(this).alpha(0.3f);
    }

    @Override
    public TextureVideoView getVideoView() {
        return videoView;
    }

    @Override
    public void videoBeginning() {
        //???????????????????????? ??????0.3??????
        videoView.setAlpha(1.f);
        cancelAlphaAnimate(videoCover);
        startAlphaAnimate(videoCover);
        community_item_video_btn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void videoStopped() {
        //???????????????????????? ????????????
        videoView.setAlpha(0);
        cancelAlphaAnimate(videoCover);
        videoCover.setAlpha(1.f);
        videoCover.setVisibility(View.VISIBLE);
        community_item_video_btn.setVisibility(View.VISIBLE);
    }

    @Override
    public void videoPrepared(MediaPlayer player) {

    }

    @Override
    public void videoResourceReady(String videoPath) {
        videoLocalPath = videoPath;
        if (videoLocalPath != null) {
            videoView.setVideoPath(videoPath);
            if (videoState == STATE_ACTIVED) {
                videoView.start();

                if (mItemClickListener != null) {
                    mItemClickListener.onVideo(videoView, videoCover, community_item_video_btn);
                }
            }
        }
    }

    @Override
    public void setActive(View newActiveView, int newActiveViewPosition) {
        videoState = STATE_ACTIVED;
        if (videoLocalPath != null) {
            videoView.setVideoPath(videoLocalPath);
            videoView.start();

            if (mItemClickListener != null) {
                mItemClickListener.onVideo(getVideoView(), videoCover, community_item_video_btn);
            }
        }
    }

    @Override
    public void deactivate(View currentView, int position) {
        videoState = STATE_DEACTIVED;
        videoView.stop();
        videoStopped();
    }

    @Override
    public void onAnimationStart(View view) {
    }

    @Override
    public void onAnimationEnd(View view) {
//        view.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationCancel(View view) {
    }

    @OnClick(R.id.community_item_video_btn)
    void cliclVideoView() {
        videoState = STATE_ACTIVED;
        if (videoLocalPath != null) {
            videoView.setVideoPath(videoLocalPath);
            videoView.start();

            if (mItemClickListener != null) {
                mItemClickListener.onVideo(getVideoView(), videoCover, community_item_video_btn);
            }
        }
    }

    /*private String getContent(String content){
        int indexStart = content.indexOf("[");
        int indexEnd = content.indexOf("]");
        if (indexStart == -1 || indexEnd == -1){
            return content;
        }else {
            String content_s = content.substring(0, indexStart);
            int length = content.length();
            if (indexEnd < length - 1){
                String content_e = content.substring(indexEnd + 1, length);
                return getContent(content_s + content_e);
            }else {
                return getContent(content_s);
            }
        }
    }*/
}
