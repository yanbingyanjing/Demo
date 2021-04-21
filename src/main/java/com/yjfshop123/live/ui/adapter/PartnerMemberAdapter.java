package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.TextView;

import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.GuanzhuTongzhi;
import com.yjfshop123.live.model.MyTeamMemberResponse;
import com.yjfshop123.live.model.PartnerMemberResponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.activity.team.PartnerMemberDetailActivity;
import com.yjfshop123.live.utils.NumUtil;

import org.json.JSONException;
import org.simple.eventbus.EventBus;

import java.util.List;

public class PartnerMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PartnerMemberResponse.PartnerMember> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public PartnerMemberAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_partner_member, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<PartnerMemberResponse.PartnerMember> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    public class Vh extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView user;
        TextView level;
        TextView team_activity;
        TextView person_activity;
        TextView mm_details_controls_add;
        TextView    max_medaling;
        public Vh(View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.user_name);
            level = itemView.findViewById(R.id.level);
            team_activity = itemView.findViewById(R.id.team_activity);
            person_activity = itemView.findViewById(R.id.person_activity);
            max_medaling = itemView.findViewById(R.id.max_medaling);
            mm_details_controls_add = itemView.findViewById(R.id.mm_details_controls_add);
        }

        void setData(final PartnerMemberResponse.PartnerMember bean) {
            max_medaling.setText(bean.max_medaling);
            user.setText(bean.name+" ID:"+bean.user_id);
            level.setText(bean.user_level);
            team_activity.setText(NumUtil.clearZero(bean.user_team_activity_num));
            person_activity.setText(NumUtil.clearZero(bean.user_activity_num));
            itemView.setOnClickListener(this);
            if (bean.is_follow > 0) {
                mm_details_controls_add.setText(context.getString(R.string.follow));
            } else {
                mm_details_controls_add.setText(context.getString(R.string.mm_dedails_1));
            }
            mm_details_controls_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGuanzhu(bean);
                }
            });
        }

        private void onGuanzhu(final PartnerMemberResponse.PartnerMember bean) {
            if (bean == null) return;
            if (bean.is_follow > 0) {
                //取消关注
                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("be_user_id", bean.user_id)
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/follow/cancel", body, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        NToast.shortToast(context, errInfo);
                    }

                    @Override
                    public void onSuccess(String result) {
                        bean.is_follow = 0;
                        GuanzhuTongzhi guanzhuTongzhi = new GuanzhuTongzhi();
                        guanzhuTongzhi.isGuanzhu = 0;
                        guanzhuTongzhi.user_id = Integer.valueOf(bean.user_id);
                        EventBus.getDefault().post(guanzhuTongzhi, Config.EVENT_GUANZHU);
                        mm_details_controls_add.setText(context.getString(R.string.mm_dedails_1));
                    }
                });
            } else {
                //关注
                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("be_user_id", bean.user_id)
                            .build();
                } catch (JSONException e) {
                }
                OKHttpUtils.getInstance().getRequest("app/follow/add", body, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        NToast.shortToast(context, errInfo);
                    }

                    @Override
                    public void onSuccess(String result) {
                        bean.is_follow = 1;
                        GuanzhuTongzhi guanzhuTongzhi = new GuanzhuTongzhi();
                        guanzhuTongzhi.isGuanzhu = 1;
                        guanzhuTongzhi.user_id = Integer.valueOf(bean.user_id);
                        EventBus.getDefault().post(guanzhuTongzhi, Config.EVENT_GUANZHU);
                        mm_details_controls_add.setText(context.getString(R.string.follow));
                        NToast.shortToast(context, "已关注成功");
                    }
                });
            }

        }


        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(context, PartnerMemberDetailActivity.class);
//            intent.putExtra("phone", mList.get(getLayoutPosition()).phone);
//            context.startActivity(intent);
        }
    }

}

