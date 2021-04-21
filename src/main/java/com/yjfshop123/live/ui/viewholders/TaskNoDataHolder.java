package com.yjfshop123.live.ui.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.adapter.TaskAdapter;

public class TaskNoDataHolder  extends RecyclerView.ViewHolder {

    private TextView title;


    private TaskAdapter.MyItemClickListener mItemClickListener;

    public TaskNoDataHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.tv_net_error);
    }

    public void bind(Context context, String titleTx) {
        title.setText(titleTx);
    }
}
