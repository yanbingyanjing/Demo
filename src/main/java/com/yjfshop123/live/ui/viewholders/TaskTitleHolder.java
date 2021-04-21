package com.yjfshop123.live.ui.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yjfshop123.live.R;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.adapter.TaskAdapter;
import com.yjfshop123.live.utils.CommonUtils;

public class TaskTitleHolder extends RecyclerView.ViewHolder {

    private TextView title;


    private TaskAdapter.MyItemClickListener mItemClickListener;

    public TaskTitleHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
    }

    public void bind(Context context, String titleTx) {
        title.setText(titleTx);

    }
}
