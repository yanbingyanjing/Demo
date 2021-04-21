package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.GuanzhuTongzhi;
import com.yjfshop123.live.model.MyTeamMemberResponse;
import com.yjfshop123.live.model.MyXunZhangResponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.RoundImageView;
import com.yjfshop123.live.ui.activity.team.MyNextTeamActivity;
import com.yjfshop123.live.utils.CommonUtils;

import org.json.JSONException;
import org.simple.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MyTeamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<MyTeamMemberResponse.MyTeamMemberData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public MyTeamAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_team, parent, false));
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

    public void setCards(List<MyTeamMemberResponse.MyTeamMemberData> list, String path) {
        if (list == null) {
            return;
        }
        this.path = path;
        mList = list;
        notifyDataSetChanged();
    }

    @OnClick(R.id.detail)
    public void onViewClicked() {
    }

    public class Vh extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myUsername;
        TextView person_activity_num;
        TextView dengji;
        TextView promotionCount;
        LinearLayout detail;
        TextView mm_details_controls_add;
        TextView team_activity_num;
        CircleImageView head;
        ImageView hearo;
        public Vh(View itemView) {
            super(itemView);
            head= itemView.findViewById(R.id.head);
            hearo = itemView.findViewById(R.id.hearo);
            myUsername = itemView.findViewById(R.id.my_username);
            person_activity_num = itemView.findViewById(R.id.person_activity_num);
            dengji = itemView.findViewById(R.id.dengji);
            promotionCount = itemView.findViewById(R.id.promotion_count);
            team_activity_num = itemView.findViewById(R.id.team_activity_num);
            detail = itemView.findViewById(R.id.detail);
            mm_details_controls_add = itemView.findViewById(R.id.mm_details_controls_add);
        }

        void setData(final MyTeamMemberResponse.MyTeamMemberData bean) {
            myUsername.setText(bean.user_name);
            person_activity_num.setText(bean.user_activity_num);
            dengji.setText(bean.user_vip_level+bean.level_title);
            team_activity_num.setText(bean.user_team_activity_num);
            promotionCount.setText(bean.user_promotion_num);
            if(TextUtils.isEmpty(bean.avatar)){
                Glide.with(context).load(R.drawable.splash_logo).into(head);
            }else   Glide.with(context).load(CommonUtils.getUrl(bean.avatar)).into(head);
            detail.setOnClickListener(this);
            if(bean.is_hero==1){
                hearo.setVisibility(View.VISIBLE);
            }else {
                hearo.setVisibility(View.GONE);
            }
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

        private void onGuanzhu(final MyTeamMemberResponse.MyTeamMemberData bean) {
            if(bean==null)return;
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
            Intent intent = new Intent(context, MyNextTeamActivity.class);
            intent.putExtra("path", path);
            intent.putExtra("user_name", mList.get(getLayoutPosition()).user_name);
            intent.putExtra("mobile", mList.get(getLayoutPosition()).mobile);
            context.startActivity(intent);
        }
    }
}
