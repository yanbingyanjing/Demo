package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.net.response.UserHomeResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MMAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserHomeResponse.UserInfoBean.VideoBean> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private int viewType;
    private String avatarUrl;
    private String name;
    private View vHead;

    public MMAdapter(Context context, int viewType, View vHead){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.viewType = viewType;
        this.vHead = vHead;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 2) {
            return new Vh(layoutInflater.inflate(R.layout.mm2_item, parent, false));
        }else if (viewType == 1){
            return new Vh(vHead);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (viewType == 2) {
            if (holder instanceof Vh) {
                ((Vh) holder).setData(mList.get(position));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public int getItemCount() {
        if (viewType == 2) {
            return mList.size();
        }else {
            return 1;
        }
    }

    public void setCards(UserHomeResponse response) {
        mList = response.getUser_info().getVideo();
        this.avatarUrl = response.getUser_info().getAvatar();
        this.name = response.getUser_info().getUser_nickname();
    }

    class Vh extends RecyclerView.ViewHolder {

        SelectableRoundedImageView mm2_item_fm;
        CircleImageView mAvatar;
        TextView mm2_item_name;
        View mm2_item_bottom_b;

        public Vh(View itemView) {
            super(itemView);
            if (viewType == 2) {
                mm2_item_fm = itemView.findViewById(R.id.mm2_item_fm);
                mAvatar = itemView.findViewById(R.id.mm2_item_icon);
                mm2_item_name = itemView.findViewById(R.id.mm2_item_name);
                mm2_item_bottom_b = itemView.findViewById(R.id.mm2_item_bottom_b);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v.getId(), getLayoutPosition());
                    }
                });
            }
        }

        void setData(UserHomeResponse.UserInfoBean.VideoBean bean) {
            Glide.with(context)
                    .load(CommonUtils.getUrl(avatarUrl))
                    .into(mAvatar);

            Glide.with(context)
                    .load(CommonUtils.getUrl(bean.getCover_img()))
                    .into(mm2_item_fm);

            mm2_item_name.setText(name);

            if (mList.size() == (getLayoutPosition() + 1)){
                mm2_item_bottom_b.setVisibility(View.VISIBLE);
            }else {
                mm2_item_bottom_b.setVisibility(View.GONE);
            }
        }
    }

    private OnItemClickListener<Integer> mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<Integer> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}

