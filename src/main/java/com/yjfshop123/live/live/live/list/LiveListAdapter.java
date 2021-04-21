package com.yjfshop123.live.live.live.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.ui.widget.VoisePlayingIcon;
import com.bumptech.glide.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.response.LivingListResponse;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class LiveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LivingListResponse.LiveListBean> mList = new ArrayList<>();
    private int width;
    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnClickListener;
    private Context context;

    private static final int ITEM_DH = 1;
    private static final int ITEM = 0;
    private boolean isHB = false;

    public LiveListAdapter(Context context, int width, OnItemClickListener onItemClickListener, boolean isHB) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.width = width;
        mOnClickListener = onItemClickListener;
        this.isHB = isHB;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_DH) {
            return new DaiHuoVh(layoutInflater.inflate(R.layout.layout_live_item_dh, parent, false));
        }
        return new Vh(layoutInflater.inflate(R.layout.layout_live_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DaiHuoVh) {
            ((DaiHuoVh) holder).setData(mList.get(position));
        } else {
            ((Vh) holder).setData(mList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mList.get(position).getItem_url())) {
            return ITEM;
        } else {
            return ITEM_DH;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<LivingListResponse.LiveListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }


    /**
     * 带货的
     */
    class DaiHuoVh extends RecyclerView.ViewHolder {

        private FrameLayout layout_live_item_fl;
        private SelectableRoundedImageView layout_live_item_icon, layout_live_item_icon2;
        private TextView layout_live_item_nickname, layout_live_item_state;
        private TextView layout_live_item_look;
        private Button layout_live_item_btn;

        private TextView layout_live_item_title;
        private TextView layout_live_item_content;
        private TextView layout_live_item_price;
        private VoisePlayingIcon voisePlayingIcon;

        public DaiHuoVh(View itemView) {
            super(itemView);
            layout_live_item_fl = itemView.findViewById(R.id.layout_live_item_fl);
            layout_live_item_icon = itemView.findViewById(R.id.layout_live_item_icon);
            layout_live_item_state = itemView.findViewById(R.id.layout_live_item_state);
            layout_live_item_nickname = itemView.findViewById(R.id.layout_live_item_nickname);
            layout_live_item_btn = itemView.findViewById(R.id.layout_live_item_btn);
            layout_live_item_look = itemView.findViewById(R.id.layout_live_item_look);

            layout_live_item_title = itemView.findViewById(R.id.layout_live_item_title);
            layout_live_item_content = itemView.findViewById(R.id.layout_live_item_content);
            layout_live_item_price = itemView.findViewById(R.id.layout_live_item_price);
            layout_live_item_icon2 = itemView.findViewById(R.id.layout_live_item_icon2);

            voisePlayingIcon = itemView.findViewById(R.id.layout_live_item_vpi);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onItemClick(null, getLayoutPosition());
                }
            });
        }

        void setData(LivingListResponse.LiveListBean listBean) {
            ViewGroup.LayoutParams params = layout_live_item_fl.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) (width * 1.1);
            layout_live_item_fl.setLayoutParams(params);

            layout_live_item_nickname.setText(listBean.getTitle());
            layout_live_item_look.setText(listBean.getViewer() + "人");

            if (listBean.getVod_type() == 2) {
                voisePlayingIcon.setVisibility(View.GONE);
                voisePlayingIcon.stop();
                layout_live_item_btn.setVisibility(View.GONE);
            } else {
                voisePlayingIcon.setVisibility(View.VISIBLE);
                voisePlayingIcon.start();
                layout_live_item_btn.setVisibility(View.VISIBLE);
            }
            if (listBean.getType() == 2) {
                layout_live_item_state.setText(context.getString(R.string.room_2));
            } else if (listBean.getType() == 3) {
                layout_live_item_state.setText(context.getString(R.string.room_3));
            } else {
                layout_live_item_state.setText(context.getString(R.string.room_1));
            }

            Glide.with(context)
                    .load(CommonUtils.getUrl(listBean.getCover_img()))
                    .into(layout_live_item_icon);

            layout_live_item_title.setText(listBean.getTitle());
            layout_live_item_content.setText(listBean.getItem_title());
            layout_live_item_price.setText("￥" + listBean.getZk_final_price());
            Glide.with(context)
                    .load(CommonUtils.getUrl(listBean.getItem_url()))
                    .into(layout_live_item_icon2);
        }
    }

    /**
     * 不带货的
     */
    class Vh extends RecyclerView.ViewHolder {

        private FrameLayout layout_live_item_fl;
        private SelectableRoundedImageView layout_live_item_icon;
        private TextView layout_live_item_nickname, layout_live_item_state;
        private TextView layout_live_item_look;
        private Button layout_live_item_btn;
        private VoisePlayingIcon voisePlayingIcon;
        private LinearLayout live_ll;

        public Vh(View itemView) {
            super(itemView);
            live_ll= itemView.findViewById(R.id.live_ll);
            layout_live_item_fl = itemView.findViewById(R.id.layout_live_item_fl);
            layout_live_item_icon = itemView.findViewById(R.id.layout_live_item_icon);
            layout_live_item_state = itemView.findViewById(R.id.layout_live_item_state);
            layout_live_item_nickname = itemView.findViewById(R.id.layout_live_item_nickname);
            layout_live_item_btn = itemView.findViewById(R.id.layout_live_item_btn);
            layout_live_item_look = itemView.findViewById(R.id.layout_live_item_look);
            voisePlayingIcon = itemView.findViewById(R.id.layout_live_item_vpi);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onItemClick(null, getLayoutPosition());
                }
            });
        }

        void setData(LivingListResponse.LiveListBean listBean) {
            voisePlayingIcon.start();

            ViewGroup.LayoutParams params = layout_live_item_fl.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) (width * 1.1);
            layout_live_item_fl.setLayoutParams(params);

            layout_live_item_nickname.setText(listBean.getTitle());
            layout_live_item_look.setText(listBean.getViewer() + "人");

            if (listBean.getVod_type() == 2) {
                live_ll.setVisibility(View.GONE);
                voisePlayingIcon.setVisibility(View.GONE);
                voisePlayingIcon.stop();
                layout_live_item_btn.setVisibility(View.GONE);
            } else {
                live_ll.setVisibility(View.VISIBLE);
                voisePlayingIcon.setVisibility(View.VISIBLE);
                voisePlayingIcon.start();
                layout_live_item_btn.setVisibility(View.VISIBLE);
            }
            if (listBean.getType() == 2) {
                layout_live_item_state.setText(context.getString(R.string.room_2));
            } else if (listBean.getType() == 3) {
                layout_live_item_state.setText(context.getString(R.string.room_3));
            } else {
                layout_live_item_state.setText(context.getString(R.string.room_1));
            }
            if (TextUtils.isEmpty(listBean.getCover_img())) {
                Glide.with(context)
                        .load(R.mipmap.moren_head)// 图片地址  
                        .into(layout_live_item_icon);// 需要显示的ImageView控件
            } else
                Glide.with(context)
                        .load(CommonUtils.getUrl(listBean.getCover_img()))
                        .into(layout_live_item_icon);
        }
    }


}
