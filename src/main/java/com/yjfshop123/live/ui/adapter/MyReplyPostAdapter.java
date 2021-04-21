package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.MyReplyPostResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.xchat.Glide;
import com.pandaq.emoticonlib.PandaEmoTranslator;

import java.util.List;


public class MyReplyPostAdapter extends BaseRecyclerViewAdapter {

    private Context context;
    private ClickItemListener clickItemListener;

    public void setClickItemListener(ClickItemListener clickItemListener) {
        this.clickItemListener = clickItemListener;
    }

    public MyReplyPostAdapter(Context context, List data) {
        super(context, data, R.layout.adapter_my_reply_post);
        this.context = context;
    }

    @Override
    protected void onBindData(RecyclerViewHolder holder, Object bean, int position) {
        CircleImageView autorImage = holder.itemView.findViewById(R.id.autorImage);
        TextView title = holder.itemView.findViewById(R.id.title);
        TextView time = holder.itemView.findViewById(R.id.time);
        TextView content = holder.itemView.findViewById(R.id.content);
        TextView replyPost = holder.itemView.findViewById(R.id.replyPost);

        MyReplyPostResponse.ListBean bean1 = (MyReplyPostResponse.ListBean) bean;

        autorImage.setTag(null);//需要清空tag，否则报错
        Glide.with(context).load(CommonUtils.getUrl(bean1.getAvatar())).into(autorImage);
        title.setText(bean1.getUser_nickname().trim());
        time.setText(bean1.getPublish_time().trim());
        content.setText(PandaEmoTranslator
                .getInstance()
                .makeEmojiSpannable(bean1.getContent().trim()));
        replyPost.setText(PandaEmoTranslator
                .getInstance()
                .makeEmojiSpannable(bean1.getDynamic_text().trim()));

        autorImage.setTag(position);
        holder.itemView.setTag(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickItemListener != null) {
                    clickItemListener.clickItem(view, (Integer) view.getTag());
                }
            }
        });

        autorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickItemListener != null) {
                    clickItemListener.clickImage(view,(Integer) view.getTag());
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

        void clickImage(View view,int position);
    }


}
