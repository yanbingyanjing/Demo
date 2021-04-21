package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.model.LevelResponse;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.video.adapter.FansAdapter;
import com.yjfshop123.live.video.bean.FansResponse;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class LevelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LevelResponse.LevelData[] mList;
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClickListener mOnClickListener;

    public LevelAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        mOnClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_level, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList[position]);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.length : 0;
    }

    public void setCards(LevelResponse.LevelData[] list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    public class Vh extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon, vip_level_icon;
        TextView des_two;
        public LinearLayout detail_one;
        TextView vip_level_fee;
        TextView vip_level_lower,level_name;
        TextView vip_level_update_need;
        ImageView vip_icon;
        TextView vip_level;
        View long_line, short_line;

        public Vh(View itemView) {
            super(itemView);
            level_name= itemView.findViewById(R.id.level_name);
            vip_level_icon = itemView.findViewById(R.id.vip_level_icon);
            short_line = itemView.findViewById(R.id.short_line);
            long_line = itemView.findViewById(R.id.long_line);
            vip_level = itemView.findViewById(R.id.vip_level);
            vip_icon = itemView.findViewById(R.id.vip_icon);
            icon = itemView.findViewById(R.id.icon);
            detail_one = itemView.findViewById(R.id.detail_one);
            vip_level_fee = itemView.findViewById(R.id.vip_level_fee);
            vip_level_lower = itemView.findViewById(R.id.vip_level_lower);
            vip_level_update_need = itemView.findViewById(R.id.vip_level_update_need);
            des_two = itemView.findViewById(R.id.des_two);
        }

        void setData(LevelResponse.LevelData bean) {
            if (bean == null) return;
            if (getLayoutPosition() == mList.length - 1) {
                long_line.setVisibility(View.GONE);
                short_line.setVisibility(View.VISIBLE);
            } else {
                short_line.setVisibility(View.GONE);
                long_line.setVisibility(View.VISIBLE);
            }

            if (getLayoutPosition() == 0) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) long_line.getLayoutParams();
                //获取当前控件的布局对象
                params.topMargin = SystemUtils.dip2px(context, 15);//设置当前控件布局的高度
                long_line.setLayoutParams(params);
            } else {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) long_line.getLayoutParams();
                //获取当前控件的布局对象
                params.topMargin = SystemUtils.dip2px(context, 0);//设置当前控件布局的高度
                long_line.setLayoutParams(params);
            }
//            Glide.with(context)
//                    .load(CommonUtils.getUrl(bean.vip_level_icon))
//                    .into(icon);
//            Glide.with(context)
//                    .load(R.mipmap.dengji)
//                    .into(icon);
            level_name.setText(bean.vip_level_title);
            if (bean.vip_level==0) {
               // level_name.setText("流量用户");
                Glide.with(context)
                        .load(R.mipmap.huangguan1)
                        .into(vip_level_icon);
                Glide.with(context)
                        .load(R.mipmap.vip_one)
                        .into(vip_icon);
            }
            if (bean.vip_level==1) {
               // level_name.setText("助理选品师");
                Glide.with(context)
                        .load(R.mipmap.huangguan2)
                        .into(vip_level_icon);
                Glide.with(context)
                        .load(R.mipmap.vip_two_one)
                        .into(vip_icon);
            }
            if (bean.vip_level==2) {
                //level_name.setText("初级选品师");
                Glide.with(context)
                        .load(R.mipmap.huangguan3)
                        .into(vip_level_icon);
                Glide.with(context)
                        .load(R.mipmap.vip_two)
                        .into(vip_icon);
            }
            if (bean.vip_level==3) {
              //  level_name.setText("中级选品师");
                Glide.with(context)
                        .load(R.mipmap.huangguan4)
                        .into(vip_level_icon);
                Glide.with(context)
                        .load(R.mipmap.vip_three)
                        .into(vip_icon);
            }
            if (bean.vip_level==4) {
               // level_name.setText("高级选品师");
                Glide.with(context)
                        .load(R.mipmap.huangguan5)
                        .into(vip_level_icon);
                Glide.with(context)
                        .load(R.mipmap.vip_four)
                        .into(vip_icon);
            }
            if (bean.vip_level==5) {
               // level_name.setText("资深选品师");
                Glide.with(context)
                        .load(R.mipmap.huangguan6)
                        .into(vip_level_icon);
                Glide.with(context)
                        .load(R.mipmap.vip_five)
                        .into(vip_icon);
            }
            if (bean.vip_level==6) {
              //  level_name.setText("核心选品师");
                Glide.with(context)
                        .load(R.mipmap.huangguan7)
                        .into(vip_level_icon);
                Glide.with(context)
                        .load(R.mipmap.vip_six)
                        .into(vip_icon);
            }
            if (bean.vip_level==7) {
               // level_name.setText("高级选品总监");
                Glide.with(context)
                        .load(R.mipmap.huangguan8)
                        .into(vip_level_icon);
                Glide.with(context)
                        .load(R.mipmap.vip_seven)
                        .into(vip_icon);
            }
            if (bean.vip_level==8) {
              //  level_name.setText("资深选品总监");
                Glide.with(context)
                        .load(R.mipmap.huangguan9)
                        .into(vip_level_icon);
                Glide.with(context)
                        .load(R.mipmap.vip_eight)
                        .into(vip_icon);
            }
            if (bean.vip_level==9) {
               // level_name.setText("首席选品官");
                Glide.with(context)
                        .load(R.mipmap.huangguan10)
                        .into(vip_level_icon);
                Glide.with(context)
                        .load(R.mipmap.vip_nine)
                        .into(vip_icon);
            }
            vip_level.setText(bean.vip_level_name);
            vip_level_fee.setText(bean.vip_level_fee);
            vip_level_lower.setText(bean.vip_level_lower);
            vip_level_update_need.setText(bean.vip_level_update_need);
            des_two.setText(bean.vip_level_des);
            //  show_more.setOnClickListener(this);
            //  update_btn.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            switch (v.getId()){
//                case R.id.show_more:
//                    if(detail_one.getVisibility()==View.VISIBLE){
//                        detail_one.setVisibility(View.GONE);
//                        show_more.setRotation(0);
//                    }else {
//                        detail_one.setVisibility(View.VISIBLE);
//                        show_more.setRotation(180);
//                    }
//                    break;
//                case R.id.update:
//                    if(mOnClickListener!=null&&mList!=null){
//                        mOnClickListener.onItemClick(mList[getLayoutPosition()],getLayoutPosition());
//                    }
//
//                    break;
//            }
        }
    }

}
