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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.EggListDataResponse;
import com.yjfshop123.live.model.MyXunZhangResponse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.broadcast.BroadcastManager;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.server.widget.RoundImageView;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.activity.EggOrderDetailActivity;
import com.yjfshop123.live.ui.activity.SettingActivity;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.GlideRoundImage;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.RouterUtil;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.List;

public class XunZhangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MyXunZhangResponse.MyXunZhangData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public XunZhangAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // if(viewType==0){
        return new Vh(layoutInflater.inflate(R.layout.item_my_xunzhang_one, parent, false));
//        }
//        if(viewType==1){
//            return new Vh(layoutInflater.inflate(R.layout.item_my_xunzhang_two, parent, false));
//        }
//        if(viewType==2){
//            return new Vh(layoutInflater.inflate(R.layout.item_my_xunzhang_three, parent, false));
//        }
//        if(viewType==3){
//            return new Vh(layoutInflater.inflate(R.layout.item_my_xunzhang_four, parent, false));
//        }
//        if(viewType==4){
//            return new Vh(layoutInflater.inflate(R.layout.item_my_xunzhang_five, parent, false));
//        }
//        if(viewType==5){
//            return new Vh(layoutInflater.inflate(R.layout.item_my_xunzhang_six, parent, false));
//        }
//        if(viewType==6){
//            return new Vh(layoutInflater.inflate(R.layout.item_my_xunzhang_seven, parent, false));
//        }
//        if(viewType==7){
//            return new Vh(layoutInflater.inflate(R.layout.item_my_xunzhang_eight, parent, false));
//        }
//        if(viewType==8){
//            return new Vh(layoutInflater.inflate(R.layout.item_my_xunzhang_nine, parent, false));
//        }
//        return new Vh(layoutInflater.inflate(R.layout.item_my_xunzhang, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return position % 9;
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<MyXunZhangResponse.MyXunZhangData> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    RequestOptions userAvatarOptions = new RequestOptions()
            .transform(new GlideRoundImage(8));

    public class Vh extends RecyclerView.ViewHolder {

        RoundImageView icon;
        ImageView bg;
        ImageView jihuijia;
        TextView xuanpin_id;
        TextView released_days;
        TextView egg_count;
        TextView xuanpin_count;
        TextView total_released_days;
        TextView medal_income_percent;
        TextView effective_time,jisong_title;
        TextView buy_limit;
        public Vh(View itemView) {
            super(itemView);
            jihuijia = itemView.findViewById(R.id.jihuijia);
            xuanpin_id = itemView.findViewById(R.id.xuanpin_id);
            released_days = itemView.findViewById(R.id.released_days);
            buy_limit=itemView.findViewById(R.id.buy_limit);
            icon = itemView.findViewById(R.id.icon);
            bg = itemView.findViewById(R.id.bg);
            total_released_days = itemView.findViewById(R.id.total_released_days);
            egg_count = itemView.findViewById(R.id.egg_count);
            xuanpin_count = itemView.findViewById(R.id.xuanpin_count);
            medal_income_percent = itemView.findViewById(R.id.medal_income_percent);
            effective_time = itemView.findViewById(R.id.effective_time);
            jisong_title= itemView.findViewById(R.id.jisong_title);
        }

        void setData(final MyXunZhangResponse.MyXunZhangData bean) {
            released_days.setText(bean.medal_released_status);
            if(!TextUtils.isEmpty(bean.medal_total_release_day)&&!TextUtils.isEmpty(bean.medal_released_day)){
                if(new BigDecimal(bean.medal_released_day).compareTo(new BigDecimal(bean.medal_total_release_day))>0){
                    jihuijia.setVisibility(View.GONE);
                    jisong_title.setVisibility(View.GONE);

                  //  released_days.setText("已进行" + bean.medal_released_day + "天");
                }else {
                    jihuijia.setVisibility(View.VISIBLE);
                    jisong_title.setVisibility(View.VISIBLE);
                }
            }
            if(bean.medal_level==0){
                jihuijia.setVisibility(View.GONE);
                jisong_title.setVisibility(View.GONE);
            }
            if (bean.is_buy == 0) {
                jihuijia.setSelected(false);
                Glide.with(context).load(R.mipmap.huangse_kuangzi).into(jihuijia);
            } else {
                jihuijia.setSelected(true);
                Glide.with(context).load(R.mipmap.huangse_gouzi).into(jihuijia);
            }
            buy_limit.setText(bean.limit_count);
            jihuijia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(jihuijia.isSelected()){
                        //取消选中
                        LoadDialog.show(context);
                        String body = "";
                        try {
                            body = new JsonBuilder()
                                    .put("id", bean.id)
                                    .put("is_buy", 0)
                                    .build();
                        } catch (JSONException e) {
                        }
                        OKHttpUtils.getInstance().getRequest("app/medal/changeType", body, new RequestCallback() {
                            @Override
                            public void onError(int errCode, String errInfo) {
                                LoadDialog.dismiss(context);
                                NToast.shortToast(context, errInfo);

                            }

                            @Override
                            public void onSuccess(String result) {
                                LoadDialog.dismiss(context);
                                bean.is_buy=0;
                                jihuijia.setSelected(false);
                                Glide.with(context).load(R.mipmap.huangse_kuangzi).into(jihuijia);
                            }
                        });
                    }else {
                        //勾选选中
                        LoadDialog.show(context);
                        String body = "";
                        try {
                            body = new JsonBuilder()
                                    .put("id", bean.id)
                                    .put("is_buy", 1)
                                    .build();
                        } catch (JSONException e) {
                        }
                        OKHttpUtils.getInstance().getRequest("app/medal/changeType", body, new RequestCallback() {
                            @Override
                            public void onError(int errCode, String errInfo) {
                                LoadDialog.dismiss(context);
                                NToast.shortToast(context, errInfo);

                            }

                            @Override
                            public void onSuccess(String result) {
                                LoadDialog.dismiss(context);
                                bean.is_buy=1;
                                jihuijia.setSelected(true);
                                Glide.with(context).load(R.mipmap.huangse_gouzi).into(jihuijia);
                            }
                        });
                    }
                }
            });
            Glide.with(context)
                    .load(CommonUtils.getUrl(bean.xuanpin_pic))
                    .into(icon);
            if (bean.medal_level % 3 == 0) {
                Glide.with(context)
                        .load(R.mipmap.xuanpin_one_new).apply(userAvatarOptions)
                        .into(bg);
            }
            if (bean.medal_level % 3 == 1) {
                Glide.with(context)
                        .load(R.mipmap.xuanpin_two_new).apply(userAvatarOptions)
                        .into(bg);
            }
            if (bean.medal_level % 3 == 2) {
                Glide.with(context)
                        .load(R.mipmap.xuanpin_three_new).apply(userAvatarOptions)
                        .into(bg);
            }
//            if(bean.medal_level==3){
//                Glide.with(context)
//                        .load(R.mipmap.bg_config_three) .apply(userAvatarOptions)
//                        .into(bg);
//            }
//            if(bean.medal_level==4){
//                Glide.with(context)
//                        .load(R.mipmap.bg_config_four) .apply(userAvatarOptions)
//                        .into(bg);
//            }
//            if(bean.medal_level==5){
//                Glide.with(context)
//                        .load(R.mipmap.bg_config_five) .apply(userAvatarOptions)
//                        .into(bg);
//            } if(bean.medal_level==6){
//                Glide.with(context)
//                        .load(R.mipmap.bg_config_six) .apply(userAvatarOptions)
//                        .into(bg);
//            } if(bean.medal_level==7){
//                Glide.with(context)
//                        .load(R.mipmap.bg_config_seven) .apply(userAvatarOptions)
//                        .into(bg);
//            }
//            if(bean.medal_level==8){
//                Glide.with(context)
//                        .load(R.mipmap.bg_config_eight) .apply(userAvatarOptions)
//                        .into(bg);
//            }
//            if(bean.medal_level==9){
//                Glide.with(context)
//                        .load(R.mipmap.bg_config_nine) .apply(userAvatarOptions)
//                        .into(bg);
//            }

            xuanpin_id.setText(bean.xuanpin_name);

            total_released_days.setText(bean.medal_total_release_day + "天");
            egg_count.setText(bean.medal_exchange + "个" + (bean.medal_level == 0 ? "(体验)" : ""));
            xuanpin_count.setText(NumUtil.clearZero(bean.xuanpin_count) + "个");
            medal_income_percent.setText(bean.medal_income_percent);
            effective_time.setText(bean.effective_time);
        }
    }

}
