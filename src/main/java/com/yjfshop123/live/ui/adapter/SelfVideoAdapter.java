package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.AlbumVideoResponse;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class SelfVideoAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<AlbumVideoResponse.VideoBean> lists;
    private String flag;
    private String option;
    private String checked;

    private OnCheckClickListener checkClickListener;

    public void setCheckClickListener(OnCheckClickListener checkClickListener) {
        this.checkClickListener = checkClickListener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public interface OnCheckClickListener {
        void checkItem(int position);
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public SelfVideoAdapter(Context context, ArrayList<AlbumVideoResponse.VideoBean> lists, String flag) {
        this.context = context;
        this.lists = lists;
        this.flag = flag;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.adapter_photo_and_video, viewGroup, false);
        ImageView imgs = view.findViewById(R.id.imgs);
        ImageView playBtn = view.findViewById(R.id.playBtn);
        final TextView checkImgs = view.findViewById(R.id.checkImgs);
        FrameLayout rootLayout = view.findViewById(R.id.rootLayout);
        if (flag.equals("video")) {
            playBtn.setVisibility(View.VISIBLE);
        }
        if (option != null) {
            if (option.equals("option")) {
                checkImgs.setVisibility(View.VISIBLE);
                rootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkImgs.isSelected()) {
                            checkImgs.setSelected(false);
                        } else {
                            checkImgs.setSelected(true);
                        }
                        checkClickListener.checkItem(i);
                    }
                });
            } else {
                checkImgs.setVisibility(View.GONE);
            }
        }
        if (checked != null) {
            if (checked.equals("checkAll")) {
                checkImgs.setSelected(true);
            }
        }
        Uri img = Uri.parse(lists.get(i).getCover_img());
        Glide.with(context).load(img).into(imgs);


//        checkImgs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    checkImgs.setChecked(true);
//                } else {
//                    checkImgs.setChecked(false);
//                }
//                checkClickListener.checkItem(i);
//            }
//        });
        return view;
    }
}
