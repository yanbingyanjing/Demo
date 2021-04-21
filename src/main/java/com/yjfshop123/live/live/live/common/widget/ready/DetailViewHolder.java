package com.yjfshop123.live.live.live.common.widget.ready;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.view.AbsViewHolder;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.bumptech.glide.Glide;

public class DetailViewHolder extends AbsViewHolder implements View.OnClickListener{

    public DetailViewHolder(Context context, ViewGroup parentView, String name_, String avatar_, String time_, String totalMemberCount_, String gold) {
        super(context, parentView);

        TextView name = (TextView) findViewById(R.id.name);
        TextView btn_back = (TextView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        TextView duration = (TextView) findViewById(R.id.duration);
        TextView watch_num = (TextView) findViewById(R.id.watch_num);
        TextView view_live_end_gold = (TextView) findViewById(R.id.view_live_end_gold);
        CircleImageView avatar = (CircleImageView) findViewById(R.id.avatar);

        Glide.with(mContext)
                .load(avatar_)
                .into(avatar);

        name.setText(name_);
        duration.setText(time_);
        watch_num.setText(totalMemberCount_);
        view_live_end_gold.setText(gold);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_end;
    }

    @Override
    public void init() {
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_back) {
            ((TCLiveBasePublisherActivity) mContext).exit();
        }
    }
}