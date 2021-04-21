package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.utils.FileUtil;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * 日期:2019/2/15
 * 描述:
 **/
public class SheQuPublishContentAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<String> list = new ArrayList<>();

    private OnImageClickListener onImageClickListener;

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    public SheQuPublishContentAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setData(ArrayList<String> list){
        this.list=list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.item_shequ_publish_content, viewGroup, false);
        SelectableRoundedImageView images = view.findViewById(R.id.images);
        ImageView deleteImg = view.findViewById(R.id.deleteImg);
        ImageView videoFlag=view.findViewById(R.id.videoFlag);
        if (list.get(i).equals("")) {
            deleteImg.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.ic_product_evaluating_add).into(images);
        } else {
            if(list.get(i).endsWith(".mp4")){
                videoFlag.setVisibility(View.VISIBLE);
                String imgPath=firstImage(list.get(i));
//                list.clear();
//                list.add(imgPath);
                Glide.with(context).load(imgPath).into(images);
            }else {
                videoFlag.setVisibility(View.GONE);
                Glide.with(context).load(list.get(i)).into(images);
            }
        }

        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageClickListener.onImageClick(view,i);
            }
        });

        return view;
    }


    private String firstImage(String filePath){
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);
        String snapshotPath = FileUtil.createFile(thumb, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
        return snapshotPath;
    }

    public interface OnImageClickListener{
        void onImageClick(View view,int position);
    }

}
