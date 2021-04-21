package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.CommunityReplyListResponse;
import com.pandaq.emoticonlib.PandaEmoTranslator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * 日期:2019/2/21
 * 描述:
 **/
public class CommunityReplyListAdapter extends RecyclerView.Adapter<CommunityReplyListAdapter.MyViewHolder> {

    private final static int VIEW_ITEM_1 = 1;
    private final static int VIEW_ITEM_BOTTOM = 2;

    private Context context;
    private List<CommunityReplyListResponse.ReplyListBean> list = new ArrayList<>();

    private TextOnClickListener textOnClickListener;

    public TextOnClickListener getTextOnClickListener() {
        return textOnClickListener;
    }

    public void setTextOnClickListener(TextOnClickListener textOnClickListener) {
        this.textOnClickListener = textOnClickListener;
    }

    public CommunityReplyListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.size() == position) {
            return VIEW_ITEM_BOTTOM;
        } else {
            return VIEW_ITEM_1;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_ITEM_1) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_community_reply_list, parent, false);
        } else if (viewType == VIEW_ITEM_BOTTOM) {
            view = LayoutInflater.from(context).inflate(R.layout.community_item_bottom, parent, false);
        }
        MyViewHolder videoViewHolder = new MyViewHolder(view, viewType);
        view.setTag(videoViewHolder);
        return new MyViewHolder(view, viewType);
    }

    public void setData(List<CommunityReplyListResponse.ReplyListBean> lists) {
        if (lists == null) {
            return;
        }
        this.list = lists;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (getItemViewType(position) == VIEW_ITEM_BOTTOM) {
            return;
        }
        CommunityReplyListResponse.ReplyListBean bean = list.get(position);
        //是否是楼主
        if (bean.getIs_lz() == 0) {
            holder.isLouZhu.setVisibility(View.GONE);
        }

        holder.louzhuName1.setText(bean.getUser_nickname());
        holder.firstTxt.setText(PandaEmoTranslator
                .getInstance()
                .makeEmojiSpannable(bean.getContent()));
        if (bean.getReviewed_user_id() == 0) {
            holder.aite.setVisibility(View.GONE);
            holder.replyName.setVisibility(View.GONE);
        } else {
            holder.aite.setVisibility(View.VISIBLE);
            holder.replyName.setVisibility(View.VISIBLE);
            holder.replyName.setText(bean.getReviewed_user_nickname());
        }
        holder.firstTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textOnClickListener.onTextClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.firstLouLay)
        LinearLayout firstLouLay;
        @BindView(R.id.louzhuName1)
        TextView louzhuName1;
        @BindView(R.id.aite)
        TextView aite;
        @BindView(R.id.replyName)
        TextView replyName;
        @BindView(R.id.isLouZhu)
        TextView isLouZhu;
        @BindView(R.id.firstTxt)
        TextView firstTxt;

        public MyViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_ITEM_BOTTOM) {
                return;
            }
            ButterKnife.bind(this, itemView);
        }
    }

    public interface TextOnClickListener {
        void onTextClick(View view, int position);
    }

}
