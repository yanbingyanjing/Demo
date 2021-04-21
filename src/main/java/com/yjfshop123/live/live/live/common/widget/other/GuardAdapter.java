package com.yjfshop123.live.live.live.common.widget.other;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.response.WULUResponse;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class GuardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WULUResponse.ListBean> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;

    private static final int HEAD = 1;
    private static final int NORMAL = 0;

    public GuardAdapter(Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEAD) {
            return new HeadVh(layoutInflater.inflate(R.layout.guard_list_head, parent, false));
        }
        return new Vh(layoutInflater.inflate(R.layout.guard_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeadVh) {
            ((HeadVh) holder).setData(mList.get(position));
        } else {
            ((Vh) holder).setData(mList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEAD;
        }
        return NORMAL;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<WULUResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    class HeadVh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mSex;
        TextView mLevel;
        TextView mVotes;

        public HeadVh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mSex = (ImageView) itemView.findViewById(R.id.sex);
            mLevel = (TextView) itemView.findViewById(R.id.level);
            mVotes = (TextView) itemView.findViewById(R.id.votes);
        }

        void setData(WULUResponse.ListBean bean) {
            Glide.with(context)
                    .load(CommonUtils.getUrl(bean.getAvatar()))
                    .into(mAvatar);

            mName.setText(bean.getUser_nickname());
            if (bean.getSex() == 1){
                mSex.setImageResource(R.drawable.ic_voice_sex_man);
            }else {
                mSex.setImageResource(R.drawable.ic_voice_sex_women);
            }
            mLevel.setText(bean.getUser_level());
            mVotes.setText(Html.fromHtml("贡献" + "  <font color='#ffa800'>" + bean.getCost_coin() + "</font>  " + context.getString(R.string.my_jinbi)));
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mAvatar;
        TextView mName;
        ImageView mSex;
        TextView mLevel;
        TextView mVotes;


        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mSex = (ImageView) itemView.findViewById(R.id.sex);
            mLevel = (TextView) itemView.findViewById(R.id.level);
            mVotes = (TextView) itemView.findViewById(R.id.votes);
        }

        void setData(WULUResponse.ListBean bean) {
            Glide.with(context)
                    .load(CommonUtils.getUrl(bean.getAvatar()))
                    .into(mAvatar);

            mName.setText(bean.getUser_nickname());
            if (bean.getSex() == 1){
                mSex.setImageResource(R.drawable.ic_voice_sex_man);
            }else {
                mSex.setImageResource(R.drawable.ic_voice_sex_women);
            }
            mLevel.setText(bean.getUser_level());
            mVotes.setText(bean.getCost_coin() + " " + context.getString(R.string.my_jinbi));
        }
    }



}

