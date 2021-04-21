package com.yjfshop123.live.video.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.net.response.VideoDynamicResponse;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.xchat.Glide;

import java.util.ArrayList;
import java.util.List;

public class LoveVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VideoDynamicResponse.ListBean> mList = new ArrayList<>();
    private int width;
    private OnItemClickListener mOnClickListener;

    public LoveVideoAdapter(int width, OnItemClickListener onItemClickListener){
        this.width = width;
        mOnClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video_item, parent, false);
        ItemVideoHolder itemHolder = new ItemVideoHolder(itemView);
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemVideoHolder)holder).bind(mList.get(position), width);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<VideoDynamicResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    class ItemVideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private SelectableRoundedImageView mIcon;
        private TextView mYj;
        private TextView mPraiseTv;
        //private ImageView mPraiseIv;

        public ItemVideoHolder (View itemView) {
            super(itemView);

            mIcon =  itemView.findViewById(R.id.video_item_icon);
            mYj = itemView.findViewById(R.id.video_list_tv_yj);
            mPraiseTv = itemView.findViewById(R.id.video_item_praise_tv);
            //mPraiseIv = itemView.findViewById(R.id.video_item_praise_iv);
            itemView.setOnClickListener(this);
        }

        void bind(VideoDynamicResponse.ListBean bean, int width) {

            //mGiftTv.setText(card.getGiftCount());
            int likeNum = bean.getLike_num();
            if (likeNum >= 10000){
                mPraiseTv.setText(likeNum / 10000 + "w");
            }else {
                mPraiseTv.setText(String.valueOf(likeNum));
            }
//            if (bean.getIs_like() > 0){
//                mPraiseIv.setImageResource(R.drawable.video_list_btn_praise_on);
//            }else {
//                mPraiseIv.setImageResource(R.drawable.video_list_btn_praise_off);
//            }

            int lookNum = bean.getLike_num();
            if (lookNum >= 10000){
                mYj.setText(String.valueOf(lookNum / 10000) + "w");
            }else {
                mYj.setText(String.valueOf(lookNum));
            }

            ViewGroup.LayoutParams params = mIcon.getLayoutParams();
            params.width = width;
            params.height = width;
            mIcon.setLayoutParams(params);

            Glide.with(itemView.getContext())
                    .load(CommonUtils.getUrl(bean.getVideo_list().get(0).getCover_img()))
                    .into(mIcon);
        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onItemClick(null, getLayoutPosition());
        }
    }

}
