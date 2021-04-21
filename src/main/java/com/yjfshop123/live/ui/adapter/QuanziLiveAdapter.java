package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.live.list.LiveListAdapter;
import com.yjfshop123.live.live.response.LivingListResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.widget.VoisePlayingIcon;
import com.yjfshop123.live.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class QuanziLiveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LivingListResponse.LiveListBean> mList = new ArrayList<>();

    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnClickListener;
    private Context context;

    private static final int ITEM_DH = 1;
    private static final int ITEM = 0;


    public QuanziLiveAdapter(Context context,  OnItemClickListener onItemClickListener) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);

        mOnClickListener = onItemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new Vh(layoutInflater.inflate(R.layout.layout_live_item_quanzi, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((Vh) holder).setData(mList.get(position));

    }

    @Override
    public int getItemViewType(int position) {

        return ITEM;

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
     * 不带货的
     */
    class Vh extends RecyclerView.ViewHolder {

        private CircleImageView head;
        private LinearLayout layout_live_item_vpi_ll;
        private TextView name;
        private VoisePlayingIcon voisePlayingIcon;
        private RelativeLayout head_ll;

        public Vh(View itemView) {
            super(itemView);
            head_ll=itemView.findViewById(R.id.head_ll);
            layout_live_item_vpi_ll=itemView.findViewById(R.id.layout_live_item_vpi_ll);
            head = itemView.findViewById(R.id.head);
            name = itemView.findViewById(R.id.name);
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



            if (listBean.getVod_type() == 2) {
                //录播
                layout_live_item_vpi_ll.setVisibility(View.GONE);
                voisePlayingIcon.stop();

            } else {
                layout_live_item_vpi_ll.setVisibility(View.VISIBLE);
                voisePlayingIcon.start();
            }
           name.setText(listBean.getUser_nickname());
            head_ll.setVisibility(View.INVISIBLE);
            if (TextUtils.isEmpty(listBean.getAvatar())) {
                Glide.with(context)
                        .load(R.mipmap.moren_head)// 图片地址  
                        .into(head);// 需要显示的ImageView控件
            } else
                Glide.with(context)
                        .load(CommonUtils.getUrl(listBean.getCover_img()))
                        .into(head);
            head_ll.setVisibility(View.VISIBLE);
        }

    }


}
