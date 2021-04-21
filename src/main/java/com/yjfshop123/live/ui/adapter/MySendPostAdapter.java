package com.yjfshop123.live.ui.adapter;

import android.content.Context;

import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.MySendPostResponse;
import com.pandaq.emoticonlib.PandaEmoTranslator;

import java.util.List;

public class MySendPostAdapter extends BaseRecyclerViewAdapter {

    private ClickItemListener clickItemListener;

    public void setClickItemListener(ClickItemListener clickItemListener) {
        this.clickItemListener = clickItemListener;
    }

    public MySendPostAdapter(Context context, List data) {
        super(context, data, R.layout.adapter_my_send_post);
    }

    @Override
    protected void onBindData(RecyclerViewHolder holder, Object bean, int position) {
        MySendPostResponse.ListBean bean1 = (MySendPostResponse.ListBean) bean;
        TextView title = holder.itemView.findViewById(R.id.title);
        TextView time = holder.itemView.findViewById(R.id.time);
        title.setText(PandaEmoTranslator
                .getInstance()
                .makeEmojiSpannable(bean1.getDynamic_text().trim()));
        time.setText(bean1.getPublish_time().trim());

        holder.itemView.setTag(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickItemListener != null) {
                    clickItemListener.clickItem(view, (Integer) view.getTag());
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (clickItemListener != null) {
                    clickItemListener.deleteItem(v, (Integer) v.getTag());
                }
                return false;
            }
        });
    }

    public interface ClickItemListener {
        void deleteItem(View view, int position);

        void clickItem(View view, int position);
    }

}
