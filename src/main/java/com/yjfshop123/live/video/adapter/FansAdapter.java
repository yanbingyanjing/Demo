package com.yjfshop123.live.video.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.video.bean.FansResponse;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FansAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FansResponse.ListBean> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClickListener mOnClickListener;

    public FansAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        mOnClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_fans, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<FansResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    int type = 0;//0关注 1粉丝

    public void setType(int type) {
        this.type = type;
    }

    public class Vh extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mAvatar;
        TextView mName;
        public TextView mFollow;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.item_fans_icon);
            mName = itemView.findViewById(R.id.item_fans_name);
            mFollow = itemView.findViewById(R.id.item_fans_follow);
        }

        void setData(FansResponse.ListBean bean) {
            if (TextUtils.isEmpty(bean.getAvatar())) {
                Glide.with(context)
                        .load(R.drawable.splash_logo)
                        .into(mAvatar);
            } else
                Glide.with(context)
                        .load(CommonUtils.getUrl(bean.getAvatar()))
                        .into(mAvatar);
            mName.setText(bean.getNickname());
            int isFollow = bean.getIs_follow();
            if (type == 0) {
                mFollow.setText("取消关注");
                mFollow.setBackgroundResource(R.drawable.icon_focus_2);
                mFollow.setTextColor(context.getResources().getColor(R.color.color_999999));
            } else {
                if (isFollow > 0) {
                    mFollow.setText("取消关注");
                    mFollow.setBackgroundResource(R.drawable.icon_focus_2);
                    mFollow.setTextColor(context.getResources().getColor(R.color.color_999999));
                } else {
                    mFollow.setText("关注");
                    mFollow.setBackgroundResource(R.drawable.icon_focus_1);
                    mFollow.setTextColor(context.getResources().getColor(R.color.white));
                }
            }

            mFollow.setOnClickListener(this);
            mAvatar.setOnClickListener(this);
            mName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_fans_follow:
                    mOnClickListener.onItemClick("FOLLOW", getLayoutPosition());
                    break;
                case R.id.item_fans_icon:
                case R.id.item_fans_name:
                    mOnClickListener.onItemClick("NAME", getLayoutPosition());
                    break;
            }
        }
    }

}
