package com.yjfshop123.live.live.live.common.widget.other;

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
import com.yjfshop123.live.live.response.LivingUserResponse;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ListPKAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LivingUserResponse.LiveListBean> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClickListener mOnClickListener;

    public ListPKAdapter(Context context, OnItemClickListener onItemClickListener){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        mOnClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_live_pk, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<LivingUserResponse.LiveListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mSex;
        TextView mBtnInvite;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mSex = (ImageView) itemView.findViewById(R.id.sex);
            mBtnInvite = (TextView) itemView.findViewById(R.id.btn_invite);

            mBtnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onItemClick(null, getLayoutPosition());
                }
            });
        }

        void setData(LivingUserResponse.LiveListBean bean) {
            if(!TextUtils.isEmpty(bean.getAvatar())) {
                Glide.with(context)
                        .load(CommonUtils.getUrl(bean.getAvatar()))
                        .into(mAvatar);
            }else {
                Glide.with(context)
                        .load(R.drawable.splash_logo)
                        .into(mAvatar);
            }
            mName.setText(bean.getUser_nickname());
            if (bean.getSex().equals("1")){
                mSex.setImageResource(R.drawable.ic_voice_sex_man);
            }else {
                mSex.setImageResource(R.drawable.ic_voice_sex_women);
            }
            mBtnInvite.setText("邀请连麦");
        }
    }

}

