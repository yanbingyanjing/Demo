package com.yjfshop123.live.ui.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.VicinityUserResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.adapter.RecommendedAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /**
     * 顶部banner
     */
    private final static int VIEW_HEADER = 0;
    /**
     * 大图
     */
    private final static int VIEW_ITEM_1 = 1;
    /**
     * 中图
     */
    private final static int VIEW_ITEM_2 = 2;
    /**
     * 小图
     */
    private final static int VIEW_ITEM_3 = 3;

    //
    private SelectableRoundedImageView mIcon;
    private Button mStateBtn;
    private TextView mStateTv;
    private TextView mNicknameTv;
    private TextView mDescTv;
    //    private TextView mCostTv;
    private TextView mAgeTv;
    private LinearLayout mStateLl;
//    private ImageView mIv;

    //
    private SelectableRoundedImageView mIcon2;
    private TextView mNicknameTv2;
    //    private TextView mCostTv2;
    private TextView mLocationTv2;
    //    private ImageView mIv2;
    private Button mStateBtn2;
    private TextView mStateTv2;

    //
    private CircleImageView mIcon3;
    private TextView mNicknameTv3;
    private TextView mDescTv3;
    private TextView mAgeTv3;
    private TextView mLocationTv3;
    //    private ImageView mVIPIv3;
    private Context context;

    private RecommendedAdapter.MyItemClickListener mItemClickListener;

    public ItemHolder(Context context, View itemView, int viewType, RecommendedAdapter.MyItemClickListener mItemClickListener) {
        super(itemView);
        this.context = context;
        if (viewType == VIEW_HEADER) {
            //
        } else if (viewType == VIEW_ITEM_1) {
            mIcon = itemView.findViewById(R.id.recommended_item_icon);
            mStateBtn = itemView.findViewById(R.id.recommended_item_state_btn);
            mStateTv = itemView.findViewById(R.id.recommended_item_state_tv);
            mNicknameTv = itemView.findViewById(R.id.recommended_item_nickname_tv);
            mDescTv = itemView.findViewById(R.id.recommended_item_desc_tv);
//            mCostTv = itemView.findViewById(R.id.recommended_item_cost_tv);
            mStateLl = itemView.findViewById(R.id.recommended_item_state_ll);
            mAgeTv = itemView.findViewById(R.id.recommended_item_age_tv);
//            mIv =  itemView.findViewById(R.id.recommended_item_iv);
        } else if (viewType == VIEW_ITEM_2) {
            mIcon2 = itemView.findViewById(R.id.recommended_item2_icon);
            mNicknameTv2 = itemView.findViewById(R.id.recommended_item2_nickname_tv);
//            mCostTv2 =  itemView.findViewById(R.id.recommended_item2_cost_tv);
            mLocationTv2 = itemView.findViewById(R.id.recommended_item2_location_tv);
//            mIv2 =  itemView.findViewById(R.id.recommended_item2_iv);
            mStateBtn2 = itemView.findViewById(R.id.recommended_item2_state_btn);
            mStateTv2 = itemView.findViewById(R.id.recommended_item2_state_tv);
        } else if (viewType == VIEW_ITEM_3) {
            mIcon3 = itemView.findViewById(R.id.recommended_item3_icon);
            mNicknameTv3 = itemView.findViewById(R.id.recommended_item3_nickname_tv);
            mDescTv3 = itemView.findViewById(R.id.recommended_item3_desc_tv);
            mAgeTv3 = itemView.findViewById(R.id.recommended_item3_age_tv);
            mLocationTv3 = itemView.findViewById(R.id.recommended_item3_location_tv);
//            mVIPIv3 =  itemView.findViewById(R.id.recommended_item3_vip_iv);
        }

        this.mItemClickListener = mItemClickListener;
        itemView.setOnClickListener(this);
    }

    public void bind(Context context, VicinityUserResponse.ListBean listBean, int width, int viewType) {
        if (viewType == VIEW_HEADER) {
            //
        } else if (viewType == VIEW_ITEM_1) {
            mNicknameTv.setText(listBean.getUser_nickname());
            String desc = listBean.getSignature();
            if (desc.length() > 14) {
                desc = desc.substring(0, 13) + "...";
            }
            mDescTv.setText(desc);
//            mDescTv.setText(listBean.getCity_name() + "\t" + listBean.getDistrict_name());
//            mCostTv.setText(listBean.getVideo_cost() + context.getString(R.string.ql_cost));

            mStateLl.setVisibility(View.VISIBLE);
            int tint = Color.parseColor("#0aed06");
            switch (listBean.getOnline_state()) {
                case 0:
                    tint = Color.WHITE;
                    mStateTv.setText(context.getString(R.string.offline));
                    break;
                case 1:
                    tint = Color.parseColor("#0aed06");
                    mStateTv.setText(context.getString(R.string.online));
                    break;
                case 2:
                    tint = Color.parseColor("#ff6600");
                    mStateTv.setText("活跃");
                    break;
                case 3:
                    tint = Color.parseColor("#0090ff");
                    mStateTv.setText("直播中");
                    break;
                case 4:
                    tint = Color.parseColor("#ff0000");
                    mStateTv.setText("聊天中");
                    break;
                case 5:
                    tint = Color.WHITE;
                    mStateTv.setText("勿扰");
                    break;
                default:
                    break;
            }
            mStateBtn.getBackground().setColorFilter(tint, PorterDuff.Mode.SRC_ATOP);

            if (listBean.getSex() == 1) {
                Drawable drawable = context.getResources().getDrawable(R.drawable.male_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mAgeTv.setCompoundDrawables(drawable, null, null, null);
                mAgeTv.setText(" " + listBean.getAge());//♂♀
                mAgeTv.setEnabled(false);
            } else {
                Drawable drawable = context.getResources().getDrawable(R.drawable.female_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mAgeTv.setCompoundDrawables(drawable, null, null, null);
                mAgeTv.setText(" " + listBean.getAge());//♂♀
                mAgeTv.setEnabled(true);
            }

//            mIv.setColorFilter(ThemeColorUtils.getThemeColor());

            ViewGroup.LayoutParams params = mIcon.getLayoutParams();
            params.width = width;
            params.height = width;
            mIcon.setLayoutParams(params);

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.imageloader)// 正在加载中的图片  
                    .error(R.mipmap.moren_head)// 加载失败的图片  
                    .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略
            if (TextUtils.isEmpty(listBean.getShow_photo())) {
                Glide.with(context)
                        .load(R.mipmap.moren_head)// 图片地址  
                        .into(mIcon);// 需要显示的ImageView控件
            } else
                Glide.with(context)
                        .load(listBean.getShow_photo())// 图片地址  
                        .apply(options)// 参数  
                        .into(mIcon);// 需要显示的ImageView控件


        } else if (viewType == VIEW_ITEM_2) {
            mNicknameTv2.setText(listBean.getUser_nickname());
//            mCostTv2.setText(listBean.getVideo_cost() + context.getString(R.string.ql_cost));

            mLocationTv2.setText(listBean.getCity_name());
//            mIv2.setColorFilter(ThemeColorUtils.getThemeColor());

            ViewGroup.LayoutParams params = mIcon2.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = width;
            mIcon2.setLayoutParams(params);

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.imageloader)// 正在加载中的图片  
                    .error(R.mipmap.moren_head)// 加载失败的图片  
                    .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略
            if (TextUtils.isEmpty(listBean.getShow_photo())) {
                Glide.with(context)
                        .load(R.mipmap.moren_head)// 图片地址  
                        .into(mIcon2);// 需要显示的ImageView控件
            } else
                Glide.with(context)
                        .load(listBean.getShow_photo())// 图片地址  
                        .apply(options)// 参数  
                        .into(mIcon2);// 需要显示的ImageView控件

            int tint = Color.parseColor("#0aed06");
            switch (listBean.getOnline_state()) {
                case 0:
                    tint = Color.WHITE;
                    mStateTv2.setText(context.getString(R.string.offline));
                    break;
                case 1:
                    tint = Color.parseColor("#0aed06");
                    mStateTv2.setText(context.getString(R.string.online));
                    break;
                case 2:
                    tint = Color.parseColor("#ff6600");
                    mStateTv2.setText("活跃");
                    break;
                case 3:
                    tint = Color.parseColor("#0090ff");
                    mStateTv2.setText("直播中");
                    break;
                case 4:
                    tint = Color.parseColor("#ff0000");
                    mStateTv2.setText("聊天中");
                    break;
                case 5:
                    tint = Color.WHITE;
                    mStateTv2.setText("勿扰");
                    break;
                default:
                    break;
            }
            mStateBtn2.getBackground().setColorFilter(tint, PorterDuff.Mode.SRC_ATOP);

        } else if (viewType == VIEW_ITEM_3) {
            mNicknameTv3.setText(listBean.getUser_nickname());
            String desc = listBean.getSignature();
            if (desc.length() > 15) {
                desc = desc.substring(0, 14) + "...";
            }
            mDescTv3.setText(desc);

            if (listBean.getSex() == 1) {
                Drawable drawable = context.getResources().getDrawable(R.drawable.male_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mAgeTv3.setCompoundDrawables(drawable, null, null, null);
                mAgeTv3.setText(" " + listBean.getAge());//♂♀
                mAgeTv3.setEnabled(false);
            } else {
                Drawable drawable = context.getResources().getDrawable(R.drawable.female_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                mAgeTv3.setCompoundDrawables(drawable, null, null, null);
                mAgeTv3.setText(" " + listBean.getAge());//♂♀
                mAgeTv3.setEnabled(true);
            }

//            int distance = listBean.getDistance();
//            if (distance > 1000){
//                mLocationTv3.setText(distance / 1000 + context.getString(R.string.distance_km));
//            }else{
//                mLocationTv3.setText(distance + context.getString(R.string.distance_m));
//            }
            mLocationTv3.setText(listBean.getDistance());

//            if (listBean.getIs_vip() == 1) {
//                mVIPIv3.setVisibility(View.VISIBLE);
//            }else {
//                mVIPIv3.setVisibility(View.GONE);
//            }

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.imageloader)// 正在加载中的图片  
                    .error(R.mipmap.moren_head)// 加载失败的图片  
                    .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略
            if (TextUtils.isEmpty(listBean.getShow_photo())) {
                Glide.with(context)
                        .load(R.mipmap.moren_head)// 图片地址  
                        .into(mIcon3);// 需要显示的ImageView控件
            } else
                Glide.with(context)
                        .load(listBean.getShow_photo())// 图片地址  
                        .apply(options)// 参数  
                        .into(mIcon3);// 需要显示的ImageView控件

        }
    }

    @Override
    public void onClick(View v) {
        mItemClickListener.onItemClick(v, getLayoutPosition());
    }
}

