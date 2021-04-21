package com.yjfshop123.live.ui.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.MessageListResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.adapter.CMessageAdapter;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pandaq.emoticonlib.PandaEmoTranslator;

public class CMessageViewHolder extends RecyclerView.ViewHolder{


    private final static int VIEW_ITEM_1 = 1;
    private final static int VIEW_ITEM_2 = 2;

    private CircleImageView cmessage_item_portrait;
    private TextView cmessage_item_nickname, cmessage_item_hint,
            cmessage_item_title, cmessage_item_time, cmessage_item_hint_2,
            cmessage_item_title_2, cmessage_item_hint_1;
    private SelectableRoundedImageView cmessage_item_img;
    private FrameLayout cmessage_item_fl;

    private CMessageAdapter.MyItemClickListener mItemClickListener;

    public CMessageViewHolder (View itemView, int viewType, CMessageAdapter.MyItemClickListener itemClickListener) {
        super(itemView);

        cmessage_item_fl =  itemView.findViewById(R.id.cmessage_item_fl);
        cmessage_item_portrait =  itemView.findViewById(R.id.cmessage_item_portrait);
        cmessage_item_nickname = itemView.findViewById(R.id.cmessage_item_nickname);
        cmessage_item_img = itemView.findViewById(R.id.cmessage_item_img);
        cmessage_item_hint = itemView.findViewById(R.id.cmessage_item_hint);
        cmessage_item_title = itemView.findViewById(R.id.cmessage_item_title);
        cmessage_item_time = itemView.findViewById(R.id.cmessage_item_time);

        if (viewType == VIEW_ITEM_2) {
            cmessage_item_hint_2 =  itemView.findViewById(R.id.cmessage_item_hint_2);
            cmessage_item_title_2 =  itemView.findViewById(R.id.cmessage_item_title_2);
        }else if (viewType == VIEW_ITEM_1) {
            cmessage_item_hint_1 =  itemView.findViewById(R.id.cmessage_item_hint_1);
        }

        this.mItemClickListener = itemClickListener;
        cmessage_item_fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v, getLayoutPosition());
            }
        });
        cmessage_item_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPortraitClick(v, getLayoutPosition());
            }
        });
        cmessage_item_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPortraitClick(v, getLayoutPosition());
            }
        });
    }

    public void bind(Context context, MessageListResponse.ListBean listBean, int viewType) {

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.imageloader)
                .error(R.drawable.imageloader)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context)
                .load(CommonUtils.getUrl(listBean.getBy_avatar()))
                .apply(options)
                .into(cmessage_item_portrait);

        cmessage_item_nickname.setText(listBean.getBy_user_nickname());
        cmessage_item_time.setText(listBean.getCreate_time());

        // 类型 11:社区动态的点赞 12:社区回复的点赞 31:社区动态的回复 32:社区回复的回
        int type_ = listBean.getType();
        if (type_ == 11 || type_ == 31){
            //动态
            String img = listBean.getVideo_cover();
            if (TextUtils.isEmpty(img)){
                img = listBean.getImg_thumb();
            }

            if (TextUtils.isEmpty(img)){
                cmessage_item_img.setVisibility(View.GONE);
                cmessage_item_hint.setVisibility(View.VISIBLE);
                cmessage_item_hint.setText(context.getString(R.string.myself_tiezi) + ":");
            }else {
                cmessage_item_img.setVisibility(View.VISIBLE);
                cmessage_item_hint.setVisibility(View.GONE);
                Glide.with(context)
                        .load(CommonUtils.getUrl(img))
                        .apply(options)
                        .into(cmessage_item_img);
            }
        }else if (type_ == 12 || type_ == 32){
            //评论
            cmessage_item_img.setVisibility(View.GONE);
            cmessage_item_hint.setVisibility(View.VISIBLE);
            cmessage_item_hint.setText(context.getString(R.string.comm_10) + ":");
        }
        cmessage_item_title.setText(PandaEmoTranslator
                .getInstance()
                .makeEmojiSpannable(listBean.getObject_title()));

        if (viewType == VIEW_ITEM_2) {
            cmessage_item_title_2.setText(PandaEmoTranslator
                    .getInstance()
                    .makeEmojiSpannable(listBean.getBy_content()));
            if (type_ == 11 || type_ == 31){
                //动态
                cmessage_item_hint_2.setText(context.getString(R.string.comm_11) + ":");
            }else if (type_ == 12 || type_ == 32){
                //评论
                cmessage_item_hint_2.setText(context.getString(R.string.comm_12) + ":");
            }
        }else if (viewType == VIEW_ITEM_1){
            if (type_ == 11 || type_ == 31){
                //动态
                cmessage_item_hint_1.setText(context.getString(R.string.comm_13));
            }else if (type_ == 12 || type_ == 32){
                //评论
                cmessage_item_hint_1.setText(context.getString(R.string.comm_14));
            }
        }
    }

}
