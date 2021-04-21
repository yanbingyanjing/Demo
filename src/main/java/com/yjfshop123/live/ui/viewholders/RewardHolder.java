package com.yjfshop123.live.ui.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.MyRewardResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;

public class RewardHolder extends RecyclerView.ViewHolder {

    private Context context;
    private CircleImageView reward_item_icon;
    private TextView reward_item_number;
    private TextView reward_item_name;
    private TextView reward_item_describe;

    public RewardHolder (Context context, View itemView) {
        super(itemView);
        this.context=context;

        reward_item_icon =  itemView.findViewById(R.id.reward_item_icon);
        reward_item_number =  itemView.findViewById(R.id.reward_item_number);
        reward_item_name =  itemView.findViewById(R.id.reward_item_name);
        reward_item_describe =  itemView.findViewById(R.id.reward_item_describe);
    }

    public void bind(MyRewardResponse.ListBean listBean, int type) {
        String color;
        if (ActivityUtils.IS_VIDEO()){
            color = "#ffffff";
        }else {
            color = "#333333";
        }
        if (type == 1){
            reward_item_describe.setText(Html.fromHtml("共邀请" + "  <font color='" + color + "'>" + listBean.getTotal() + "</font>  " + "人"));
        }else {
            reward_item_describe.setText(Html.fromHtml("共奖励" + "  <font color='" + color + "'>" + listBean.getTotal() + "</font>  " + context.getString(R.string.my_jinbi)));
        }
        reward_item_name.setText(listBean.getUser_nickname());
        reward_item_number.setText(String.valueOf(getLayoutPosition() + 1));

        Glide.with(context)
                .load(CommonUtils.getUrl(listBean.getAvatar()))
                .into(reward_item_icon);
    }

}

