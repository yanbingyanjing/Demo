package com.yjfshop123.live.live.live.common.widget.music;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.response.MusicNameListResponse;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MusicNameListResponse.ListBean> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;

    public MusicAdapter(Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.music_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<MusicNameListResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    private OnItemClickListener<MusicNameListResponse.ListBean> mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener<MusicNameListResponse.ListBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class Vh extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mName;
        TextView mDownload;

        public Vh(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.music_list_name);
            mDownload = (TextView) itemView.findViewById(R.id.music_list_download);
            mDownload.setOnClickListener(this);
        }

        void setData(MusicNameListResponse.ListBean bean) {
            mName.setText(Html.fromHtml(bean.getMusic_name() + "<font color='#666666'>" + "(" + bean.getSinger() + ")" + "</font>"));
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mList.get(getLayoutPosition()), getLayoutPosition());
            }
        }
    }



}


