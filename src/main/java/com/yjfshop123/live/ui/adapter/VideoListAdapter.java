package com.yjfshop123.live.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.VideoDynamicResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.IVideoView;
import com.yjfshop123.live.ui.viewholders.TaskTitleHolder;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.pandaq.emoticonlib.PandaEmoTranslator;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.UserInfoUtil;

import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<VideoDynamicResponse.ListBean> datas;
    private IVideoView iVideoView;
    private int mScreenWidth;

    public VideoListAdapter(Context mContext, List<VideoDynamicResponse.ListBean> datas,
                            IVideoView iVideoView, int screenWidth) {
        this.mContext = mContext;
        this.datas = datas;
        this.iVideoView = iVideoView;
        this.mScreenWidth = screenWidth;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        Log.d("视频位置——加载",datas.get(viewType).is_ad +"");
        if (datas.get(viewType).is_ad == 0) {
            //非广告视频
            holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.video_layout_item, parent, false));
        } else {
            //广告视频
            holder = new AdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.ad_video_layout_item, parent, false));
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (datas.get(position).is_ad == 0) {
            if (datas.get(position).getVideo_list() == null || datas.get(position).getVideo_list().size() == 0) {
                return;
            }
            //非广告视频
            ((ViewHolder) holder).bind(mContext, datas.get(position));
        } else {
            //广告视频
            ((AdViewHolder) holder).bind(mContext, datas.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private void params(ImageView imgView, int with, int height) {
        ViewGroup.LayoutParams params = imgView.getLayoutParams();
        params.width = with;
        params.height = height;
        imgView.setLayoutParams(params);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout right_ll;
        public ImageView mCover;
        //        public ImageView mPause;
        public TXCloudVideoView mVideo;
        public View mVPause;
        public ImageView shop_img,share;
        public LinearLayout gouwuche, gouwuche_detail;
        private TextView shopTitle, shopTitleTwo, shop_title_new, shop_price;
        private TextView /*community_video_circle_title,*/
                community_video_nickname, community_video_city_name,
                community_video_content, community_video_like_num,
                community_video_reply_num;
        private CircleImageView community_video_avatar;
        public ImageView community_video_like_num_iv, community_video_follow;
        private LinearLayout community_video_gift, community_video_reply_ll,
                community_video_like_num_ll;

        public ViewHolder(View itemView) {
            super(itemView);
            share=itemView.findViewById(R.id.share);
            gouwuche_detail = itemView.findViewById(R.id.gouwuche_big);
            shop_title_new = itemView.findViewById(R.id.shop_title_new);
            shop_price = itemView.findViewById(R.id.shop_price);
            shopTitleTwo = itemView.findViewById(R.id.shop_title_two);
            shop_img = itemView.findViewById(R.id.shop_img);

            shopTitle = itemView.findViewById(R.id.shop_title);
            mCover = itemView.findViewById(R.id.player_iv_cover);
//            mPause = itemView.findViewById(R.id.player_iv_pause);
            gouwuche = itemView.findViewById(R.id.gouwuche);
            mVideo = itemView.findViewById(R.id.player_cloud_view);
            mVPause = itemView.findViewById(R.id.player_v_pause);
            right_ll = itemView.findViewById(R.id.right_ll);
//            community_video_circle_title = itemView.findViewById(R.id.community_video_circle_title_);
            community_video_nickname = itemView.findViewById(R.id.community_video_nickname_);
            community_video_city_name = itemView.findViewById(R.id.community_video_city_name_);
            community_video_content = itemView.findViewById(R.id.community_video_content_);
            community_video_avatar = itemView.findViewById(R.id.community_video_avatar_);
            community_video_like_num_iv = itemView.findViewById(R.id.community_video_like_num_iv_);
            community_video_like_num = itemView.findViewById(R.id.community_video_like_num_);
            community_video_reply_num = itemView.findViewById(R.id.community_video_reply_num_);
            community_video_follow = itemView.findViewById(R.id.community_video_follow_);
            community_video_gift = itemView.findViewById(R.id.community_video_gift_);
            community_video_reply_ll = itemView.findViewById(R.id.community_video_reply_ll_);
            community_video_like_num_ll = itemView.findViewById(R.id.community_video_like_num_ll_);
            shopTitleTwo.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            community_video_avatar.setOnClickListener(this);
            community_video_like_num_ll.setOnClickListener(this);
            community_video_reply_ll.setOnClickListener(this);
            community_video_gift.setOnClickListener(this);
            community_video_follow.setOnClickListener(this);
            gouwuche.setOnClickListener(this);
            share.setOnClickListener(this);

            gouwuche_detail.setOnClickListener(this);
        }

        public void bind(final Context context, VideoDynamicResponse.ListBean data) {
            right_ll.setVisibility(View.VISIBLE);
            mCover.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(data.goods_url)) {
                Glide.with(mContext).load(data.goods_pic).into(shop_img);
                shop_title_new.setText(data.goods_title);
                shop_price.setText(NumUtil.clearZero(data.goods_egg_price));
                gouwuche.setVisibility(View.GONE);
                gouwuche_detail.setVisibility(View.VISIBLE);
                shopTitle.setText(data.goods_title + "金蛋" + data.goods_egg_price);
            } else {
                gouwuche.setVisibility(View.GONE);
                gouwuche_detail.setVisibility(View.GONE);
            }

            int width = data.getVideo_list().get(0).getExtra_info().getWidth();
            int height = data.getVideo_list().get(0).getExtra_info().getHeight();
            if (width > height) {
//            params(holder.mCover, mScreenWidth, (mScreenWidth * height / width));
                mCover.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                mCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            Glide.with(mContext)
                    .load(CommonUtils.getUrl(data.getVideo_list().get(0).getCover_img()))
                    .into(mCover);

            if (TextUtils.isEmpty(data.getAvatar())) {
                Glide.with(mContext)
                        .load(R.drawable.splash_logo)
                        .into(community_video_avatar);

            } else
                Glide.with(mContext)
                        .load(CommonUtils.getUrl(data.getAvatar()))
                        .into(community_video_avatar);

//        String circle_title = datas.get(position).getCircle_title();
//        if (TextUtils.isEmpty(circle_title)){
//            holder.community_video_circle_title.setVisibility(View.GONE);
//        }else {
//            holder.community_video_circle_title.setText("#" + circle_title + "#");
//        }
            community_video_nickname.setText(data.getUser_nickname());
            String city_name = data.getCity_name();
            if (!TextUtils.isEmpty(city_name)) {
                community_video_city_name.setVisibility(View.VISIBLE);
                community_video_city_name.setText(city_name);
            } else {
                community_video_city_name.setVisibility(View.GONE);
            }
            String content = data.getContent();
            if (TextUtils.isEmpty(content)) {
                content = data.getTitle();
                if (TextUtils.isEmpty(content)) {
                    community_video_content.setVisibility(View.GONE);
                } else {
                    community_video_content.setVisibility(View.VISIBLE);
//                holder.community_video_content.setText(getContent(content));
                    community_video_content.setText(PandaEmoTranslator
                            .getInstance()
                            .makeEmojiSpannable(content));
                }
            } else {
                community_video_content.setVisibility(View.VISIBLE);
//            holder.community_video_content.setText(getContent(content));
                community_video_content.setText(PandaEmoTranslator
                        .getInstance()
                        .makeEmojiSpannable(content));
            }
            if (data.getIs_like() == 0) {
                community_video_like_num_iv.setImageResource(R.mipmap.unselext_xin);
            } else {
                community_video_like_num_iv.setImageResource(R.drawable.ic_short_video_yidianzan_p);
            }
            community_video_like_num.setText(data.getLike_num() + "");
            community_video_reply_num.setText(data.getReply_num() + "");
            if (data.getUser_id() == Integer.valueOf(UserInfoUtil.getMiPlatformId())) {
                community_video_follow.setVisibility(View.INVISIBLE);
            } else {
                community_video_follow.setVisibility(View.VISIBLE);
            }
            if (data.getIs_follow() == 0) {
                //否
                community_video_follow.setImageResource(R.drawable.ic_add_follow);
            } else {
                //是
                community_video_follow.setImageResource(R.drawable.ic_add_follow_2);
            }


        }

        @Override
        public void onClick(View v) {
            if (iVideoView == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.share:
                    NLog.d("点击分享了","2");
                    iVideoView.share(getLayoutPosition());
                    break;
                case R.id.gouwuche_big:
                    iVideoView.opShop(getLayoutPosition());
//
//                    ScaleAnimation animation = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0f, 1, 1f);
//                    animation.setDuration(1000);
//                    //设置持续时间
//                    animation.setFillAfter(false);
//                    //设置动画结束之后的状态是否是动画的最终状态，true，表示是保持动画结束时的最终状态
//                    animation.setRepeatCount(0);
//                    animation.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            gouwuche_detail.setVisibility(View.GONE);
//                            gouwuche.setVisibility(View.VISIBLE);
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//
//                        }
//                    });
//
//                    gouwuche_detail.startAnimation(animation);


                    break;
                case R.id.community_video_avatar_:
                    NLog.d("点击分享了","1");
                    iVideoView.avatar(getLayoutPosition());
                    break;
                case R.id.community_video_like_num_ll_:
                    iVideoView.like(community_video_like_num_iv, community_video_like_num, getLayoutPosition());
                    break;
                case R.id.community_video_reply_ll_:
                    iVideoView.reply(community_video_reply_num, getLayoutPosition());
                    break;
                case R.id.community_video_gift_:
                    iVideoView.gift(getLayoutPosition());
                    break;
                case R.id.community_video_follow_:
                    iVideoView.follow(community_video_follow, getLayoutPosition());
                    break;
            }
        }


    }

    public class AdViewHolder extends RecyclerView.ViewHolder {
        //        public ImageView mPause;
        public FrameLayout mVideo;
        public View mVPause;


        public AdViewHolder(View itemView) {
            super(itemView);

            mVideo = itemView.findViewById(R.id.player_cloud_view);
            mVPause = itemView.findViewById(R.id.player_v_pause);

        }

        public void bind(Context context, VideoDynamicResponse.ListBean data) {
            if (data.ad != null) {
                View view = ((TTNativeExpressAd) (data.ad)).getExpressAdView();
                Log.d("广告View", view == null ? "false" : "true");

                mVideo.removeAllViews();
                if (view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);

                }
                mVideo.addView(view);
            }

        }
    }
}

