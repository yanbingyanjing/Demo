package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.ChatTaskListResponse;
import com.yjfshop123.live.ui.viewholders.TaskHolder;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {

    private List<ChatTaskListResponse.ListBean> mList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;

    private String mi_tencentId;

    public TaskAdapter(Context context, String mi_tencentId){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.mi_tencentId = mi_tencentId;
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.task_adapter_item, parent, false);
        return new TaskHolder(itemView, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {
        holder.bind(context, mList.get(position), mi_tencentId);
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCards(List<ChatTaskListResponse.ListBean> list) {
        if (list == null) {
            return;
        }
        mList = list;
    }

    private MyItemClickListener mItemClickListener;

    public interface MyItemClickListener {
        void onItemClickP(View view, int position);
        void onItemClickC(View view, int position);
        void onItemClickQ(View view, int position);
    }

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

}
