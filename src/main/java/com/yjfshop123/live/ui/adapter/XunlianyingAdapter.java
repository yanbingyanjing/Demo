package com.yjfshop123.live.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.taskcenter.UploadAdapter;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.activity.XinxiShangchuanActivity;
import com.yjfshop123.live.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class XunlianyingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<XinxiShangchuanActivity.Image> mList;
    private LayoutInflater layoutInflater;
    private Context context;
    View.OnClickListener listener;

    public XunlianyingAdapter(Context context, View.OnClickListener listener) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_xinxitijiao_upload, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ((Vh) holder).setData(null);
        } else {
            if (mList.size() - position >= 0)
                ((Vh) holder).setData(mList.get(mList.size() - position));
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() + 1 : 1;
    }

    public void setCards(ArrayList<XinxiShangchuanActivity.Image> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    public class Vh extends RecyclerView.ViewHolder {

        SelectableRoundedImageView imageView;
        ImageView close;
        public Vh(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.images);
            close = itemView.findViewById(R.id.close);
        }

        void setData(XinxiShangchuanActivity.Image bean) {
            if (getLayoutPosition() == 0) {
                close.setVisibility(View.GONE);
                Glide.with(context).load(R.mipmap.add_new_two).into(imageView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) listener.onClick(v);
                    }
                });
            } else {
                close.setVisibility(View.VISIBLE);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mList.remove(mList.size() - getLayoutPosition() );
                        notifyDataSetChanged();
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, XPicturePagerActivity.class);
                        intent.putExtra(Config.POSITION, mList.size() - getLayoutPosition());
                        List<String> pa=new ArrayList<>();
                        for(int i=0;i<mList.size();i++){
                            pa.add(mList.get(i).isHttp? CommonUtils.getUrl(mList.get(i).path):mList.get(i).path);
                        }
                        try {
                            intent.putExtra("Picture", JsonMananger.beanToJson(pa));
                        } catch (HttpException e) {
                            e.printStackTrace();
                        }
                        ((Activity) context).startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    }
                });

                Glide.with(context).load(bean.isHttp? CommonUtils.getUrl(bean.path):bean.path).into(imageView);
            }
        }

    }
}

