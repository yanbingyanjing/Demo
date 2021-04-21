package com.yjfshop123.live.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.adapter.ConversationAdapter;
import com.yjfshop123.live.utils.TimeUtil;
import com.bumptech.glide.Glide;
import com.pandaq.emoticonlib.PandaEmoTranslator;

public class ConversationVideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    private SelectableRoundedImageView c_it_icon;
    private ImageView c_it_vip_iv;
    private TextView c_it_name;
    private TextView c_it_last_message;
    private TextView c_it_message_time;
    private TextView c_it_unread_num;

    private ConversationAdapter.MyItemClickListener mItemClickListener;
    private ConversationAdapter.MyLongClickListener myLongClickListener;

    public ConversationVideoHolder (View itemView, ConversationAdapter.MyItemClickListener mItemClickListener,
                                    ConversationAdapter.MyLongClickListener myLongClickListener, int viewType) {
        super(itemView);
        if (viewType == 100){
            return;
        }

        c_it_icon =  itemView.findViewById(R.id.c_it_icon);
        c_it_vip_iv =  itemView.findViewById(R.id.c_it_vip_iv);
        c_it_name = itemView.findViewById(R.id.c_it_name);
        c_it_last_message = itemView.findViewById(R.id.c_it_last_message);
        c_it_message_time = itemView.findViewById(R.id.c_it_message_time);
        c_it_unread_num = itemView.findViewById(R.id.c_it_unread_num);

        this.mItemClickListener = mItemClickListener;
        this.myLongClickListener = myLongClickListener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void bind(IMConversationDB conversation, int viewType, int is_vip) {
        if (viewType == 100){
            return;
        }

        String lastMessage = conversation.getLastMessage();
        if (lastMessage == null){
            return;
        }

//        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
//        stringBuilder.append(lastMessage);
        c_it_last_message.setText(PandaEmoTranslator
                .getInstance()
                .makeEmojiSpannable(lastMessage));

        c_it_name.setText(conversation.getOtherPartyName());
        c_it_message_time.setText(TimeUtil.getTimeStr(conversation.getTimestamp()));

        long unRead = conversation.getUnreadCount();
        if (unRead <= 0){
            c_it_unread_num.setVisibility(View.INVISIBLE);
        }else{
            c_it_unread_num.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unRead);
            if (unRead < 10){
                c_it_unread_num.setBackgroundResource(R.drawable.point1);
            }else{
                c_it_unread_num.setBackgroundResource(R.drawable.point2);
                if (unRead > 99){
                    unReadStr = "99+";
                }
            }
            c_it_unread_num.setText(unReadStr);
        }
        if(!TextUtils.isEmpty(conversation.getOtherPartyAvatar())) {
            Glide.with(itemView.getContext())
                    .load(conversation.getOtherPartyAvatar())
                    .into(c_it_icon);
        }else {
            Glide.with(itemView.getContext())
                    .load(R.drawable.splash_logo)
                    .into(c_it_icon);
        }

        if (is_vip == 0) {
            c_it_vip_iv.setVisibility(View.GONE);
        }else {
            c_it_vip_iv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        mItemClickListener.onItemClick(v, getPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        myLongClickListener.onLongClick(v, getPosition());
        return true;
    }
}
