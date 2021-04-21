package com.yjfshop123.live.video.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.VideoDynamicResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.IVideoView;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.pandaq.emoticonlib.PandaEmoTranslator;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.List;

public class HomeSmallVideoAdapter extends RecyclerView.Adapter<HomeSmallVideoAdapter.ViewHolder> {

    private Context mContext;
    private List<VideoDynamicResponse.ListBean> datas;
    private int mScreenWidth;
    private IVideoView iVideoView;

    public HomeSmallVideoAdapter(Context mContext, List<VideoDynamicResponse.ListBean> datas, int screenWidth) {
        this.mContext = mContext;
        this.datas = datas;
        this.mScreenWidth = screenWidth;
    }

    public void setListener(IVideoView iVideoView){
        this.iVideoView = iVideoView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.video_home_content, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (datas.get(position).getVideo_list() == null || datas.get(position).getVideo_list().size() == 0){
            return;
        }

        holder.mCover.setVisibility(View.VISIBLE);
        int width = datas.get(position).getVideo_list().get(0).getExtra_info().getWidth();
        int height = datas.get(position).getVideo_list().get(0).getExtra_info().getHeight();
        if (width > height){
//            params(holder.mCover, mScreenWidth, (mScreenWidth * height / width));
            holder.mCover.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }else {
            holder.mCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        Glide.with(mContext)
                .load(CommonUtils.getUrl(datas.get(position).getVideo_list().get(0).getCover_img()))
                .into(holder.mCover);


        Glide.with(mContext)
                .load(CommonUtils.getUrl(datas.get(position).getAvatar()))
                .into(holder.video_avatar);

        holder.video_nickname.setText("@" + datas.get(position).getUser_nickname());
        String city_name = datas.get(position).getCity_name();
        if (!TextUtils.isEmpty(city_name)){
            holder.video_city_name.setVisibility(View.VISIBLE);
            holder.video_city_name.setText(city_name);
        }else {
            holder.video_city_name.setVisibility(View.GONE);
        }
        String content = datas.get(position).getContent();
        if (TextUtils.isEmpty(content)){
            content = datas.get(position).getTitle();
            if (TextUtils.isEmpty(content)){
                holder.video_content.setVisibility(View.GONE);
            }else {
                holder.video_content.setVisibility(View.VISIBLE);
                holder.video_content.setText(PandaEmoTranslator
                        .getInstance()
                        .makeEmojiSpannable(content));
            }
        }else {
            holder.video_content.setVisibility(View.VISIBLE);
            holder.video_content.setText(PandaEmoTranslator
                    .getInstance()
                    .makeEmojiSpannable(content));
        }
        if (datas.get(position).getIs_like() == 0){
            holder.video_like_num_iv.setImageResource(R.drawable.ic_short_video_dianzan_p);
        }else {
            holder.video_like_num_iv.setImageResource(R.drawable.ic_short_video_yidianzan_p);
        }
        holder.video_like_num.setText(datas.get(position).getLike_num() + "");
        holder.video_reply_num.setText(datas.get(position).getReply_num() + "");

        if (datas.get(position).getIs_follow() == 0){
            //否
            holder.video_follow.setVisibility(View.VISIBLE);
        }else {
            //是
            holder.video_follow.setVisibility(View.GONE);
        }
    }

    private void params(ImageView imgView, int with, int height){
        ViewGroup.LayoutParams params = imgView.getLayoutParams();
        params.width = with;
        params.height = height;
        imgView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mCover;
//        public ImageView mPause;
        public TXCloudVideoView mVideo;
        public View mVPause;

        private TextView
                video_nickname,
                video_city_name,
                video_content,
                video_like_num,
                video_reply_num;
        private CircleImageView video_avatar;
        private ImageView
                video_like_num_iv,
                video_gift,
                video_share,
                video_follow;
        private LinearLayout
                video_reply_ll,
                video_like_num_ll;

        public ViewHolder(View itemView) {
            super(itemView);
            mCover = itemView.findViewById(R.id.player_iv_cover);
//            mPause = itemView.findViewById(R.id.player_iv_pause);
            mVideo = itemView.findViewById(R.id.player_cloud_view);
            mVPause = itemView.findViewById(R.id.player_v_pause);

            video_nickname = itemView.findViewById(R.id.video_nickname);
            video_city_name = itemView.findViewById(R.id.video_city_name);
            video_content = itemView.findViewById(R.id.video_content);
            video_avatar = itemView.findViewById(R.id.video_avatar);
            video_like_num_iv = itemView.findViewById(R.id.video_like_num_iv);
            video_like_num = itemView.findViewById(R.id.video_like_num);
            video_reply_num = itemView.findViewById(R.id.video_reply_num);
            video_follow = itemView.findViewById(R.id.video_follow);
            video_gift = itemView.findViewById(R.id.video_gift);
            video_share = itemView.findViewById(R.id.video_share);
            video_reply_ll = itemView.findViewById(R.id.video_reply_ll);
            video_like_num_ll = itemView.findViewById(R.id.video_like_num_ll);

            video_avatar.setOnClickListener(this);
            video_like_num_ll.setOnClickListener(this);
            video_reply_ll.setOnClickListener(this);
            video_gift.setOnClickListener(this);
            video_follow.setOnClickListener(this);
            video_share.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (iVideoView == null){
                return;
            }
            switch (v.getId()){
                case R.id.video_avatar:
                    iVideoView.avatar(getLayoutPosition());
                    break;
                case R.id.video_like_num_ll:
                    iVideoView.like(video_like_num_iv, video_like_num, getLayoutPosition());
                    break;
                case R.id.video_reply_ll:
                    iVideoView.reply(video_reply_num, getLayoutPosition());
                    break;
                case R.id.video_gift:
                    iVideoView.gift(getLayoutPosition());
                    break;
                case R.id.video_follow:
                    iVideoView.follow(video_follow, getLayoutPosition());
                    break;
                case R.id.video_share:
                    iVideoView.share(getLayoutPosition());
                    break;
            }
        }
    }
}
