package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.model.MyTeamMemberResponse;
import com.yjfshop123.live.model.TargetRewardResponse;
import com.yjfshop123.live.ui.activity.team.MyNextTeamActivity;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.NumUtil;

import java.util.List;

import butterknife.OnClick;

public class TargetRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<TargetRewardResponse> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public TargetRecordAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_target_record, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    String path;

    public void setCards(List<TargetRewardResponse> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    @OnClick(R.id.detail)
    public void onViewClicked() {
    }

    public class Vh extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView time;
        TextView status;
        TextView activity_num;
        TextView target_name;
        ImageView img;
        TextView target_activity_num;
        public Vh(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            activity_num = itemView.findViewById(R.id.activity_num);
            target_name = itemView.findViewById(R.id.target_name);
            img = itemView.findViewById(R.id.img);
            target_activity_num=itemView.findViewById(R.id.target_activity_num);
        }

        void setData(TargetRewardResponse bean) {
            time.setText(bean.date);
            target_activity_num.setText(bean.target_reward_need_activity_num);
            activity_num.setText(NumUtil.clearZero(bean.team_activity_num));
            target_name.setText(bean.target_reward_value);
            Glide.with(context).load(CommonUtils.getUrl(bean.target_reward_icon)).into(img);
            status.setText(bean.target_reward_is_get ? "已发放" : "未发放");
        }

        @Override
        public void onClick(View v) {

        }
    }
}
