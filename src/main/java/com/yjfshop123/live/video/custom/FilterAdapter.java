package com.yjfshop123.live.video.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.Constants;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.video.bean.FilterBean;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.Vh> {

    private List<FilterBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<FilterBean> mOnItemClickListener;
    private int mCheckedPosition;

    public FilterAdapter(Context context) {
        mList = new ArrayList<>();
        mList.add(new FilterBean(R.drawable.icon_filter_orginal, 0, true));
        mList.add(new FilterBean(R.drawable.icon_filter_langman, R.drawable.filter_langman));
        mList.add(new FilterBean(R.drawable.icon_filter_qingxin, R.drawable.filter_qingxin));
        mList.add(new FilterBean(R.drawable.icon_filter_weimei, R.drawable.filter_weimei));
        mList.add(new FilterBean(R.drawable.icon_filter_fennen, R.drawable.filter_fennen));
        mList.add(new FilterBean(R.drawable.icon_filter_huaijiu, R.drawable.filter_huaijiu));
        mList.add(new FilterBean(R.drawable.icon_filter_qingliang, R.drawable.filter_qingliang));
        mList.add(new FilterBean(R.drawable.icon_filter_landiao, R.drawable.filter_landiao));
        mList.add(new FilterBean(R.drawable.icon_filter_rixi, R.drawable.filter_rixi));
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mCheckedPosition == position) {
                        return;
                    }
                    if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
                        mList.get(mCheckedPosition).setChecked(false);
                        notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
                    }
                    mList.get(position).setChecked(true);
                    notifyItemChanged(position, Constants.PAYLOAD);
                    mCheckedPosition = position;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<FilterBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    @Override
    public Vh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_list_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(Vh holder, int position) {

    }

    @Override
    public void onBindViewHolder(Vh vh, int position, List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;
        ImageView mCheckImg;

        public Vh(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.img);
            mCheckImg = (ImageView) itemView.findViewById(R.id.check_img);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(FilterBean bean, int position, Object payload) {
            itemView.setTag(position);
            if (payload == null) {
                mImg.setImageResource(bean.getImgSrc());
            }
            if (bean.isChecked()) {
                if (mCheckImg.getVisibility() != View.VISIBLE) {
                    mCheckImg.setVisibility(View.VISIBLE);
                }
            } else {
                if (mCheckImg.getVisibility() == View.VISIBLE) {
                    mCheckImg.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
