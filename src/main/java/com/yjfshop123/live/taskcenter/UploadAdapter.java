package com.yjfshop123.live.taskcenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;

import java.util.ArrayList;

public class UploadAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<String> mList;
    private LayoutInflater layoutInflater;
    private Context context;
    View.OnClickListener listener;

    public UploadAdapter(Context context, View.OnClickListener listener) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_upload_sucai, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ((Vh) holder).setData(null);
        } else {
            if(mList.size()-position>=0)
                ((Vh) holder).setData(mList.get(mList.size()-position));
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() + 1 : 1;
    }

    public void setCards(ArrayList<String> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    public class Vh extends RecyclerView.ViewHolder {

        SelectableRoundedImageView imageView;

        public Vh(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.images);

        }

        void setData(String bean) {
            if (getLayoutPosition() == 0) {
                Glide.with(context).load(R.mipmap.add_new_two).into(imageView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) listener.onClick(v);
                    }
                });
            } else {
                Glide.with(context).load(bean).into(imageView);
            }
        }

    }
}

