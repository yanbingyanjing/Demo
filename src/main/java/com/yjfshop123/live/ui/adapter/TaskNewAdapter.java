package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.model.TaskNewResponse;
import com.yjfshop123.live.net.response.ChatTaskListResponse;
import com.yjfshop123.live.ui.viewholders.TaskHolder;
import com.yjfshop123.live.ui.viewholders.TaskNewItemHolder;
import com.yjfshop123.live.ui.viewholders.TaskNoDataHolder;
import com.yjfshop123.live.ui.viewholders.TaskTitleHolder;
import com.yjfshop123.live.ui.viewholders.TaskTopHolder;

import java.util.ArrayList;
import java.util.List;

public class TaskNewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TaskNewResponse.TaskCenterData> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;


    public TaskNewAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View itemView = layoutInflater.inflate(R.layout.item_task_top, parent, false);
            return new TaskTopHolder(itemView);
        }
        if (viewType == 2) {
            View itemView = layoutInflater.inflate(R.layout.item_task_title, parent, false);
            return new TaskTitleHolder(itemView);
        }
        if (viewType == 3) {
            View itemView = layoutInflater.inflate(R.layout.item_task_item, parent, false);
            return new TaskNewItemHolder(itemView);
        }
        if (viewType == 4) {
            View itemView = layoutInflater.inflate(R.layout.item_task_no_data, parent, false);
            return new TaskNoDataHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (mList.get(position).type == 1 && mList.get(position).data instanceof String) {
            ((TaskTopHolder) holder).bind(context, (String) mList.get(position).data);
        }
        if (mList.get(position).type == 2 && mList.get(position).data instanceof String) {
            ((TaskTitleHolder) holder).bind(context, (String) mList.get(position).data);

        }
        if (mList.get(position).type == 3 && mList.get(position).data instanceof TaskNewResponse.TaskItem) {
            ((TaskNewItemHolder) holder).bind(context, (TaskNewResponse.TaskItem) mList.get(position).data);
        }
        if (mList.get(position).type == 4 && mList.get(position).data instanceof String) {
            ((TaskNoDataHolder) holder).bind(context, (String) mList.get(position).data);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<TaskNewResponse.TaskCenterData> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    private MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onItemClickVideo(View view, int position);

        void onItemClickShare(View view, int position);

        void onItemClickQuanzi(View view, int position);

        void onItemClickShortVideo(View view, int position);
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

}
