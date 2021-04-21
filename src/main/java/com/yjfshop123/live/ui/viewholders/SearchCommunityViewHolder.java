package com.yjfshop123.live.ui.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.SearchDynamicResponse;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.adapter.SearchCommunityAdapter;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.FindUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class SearchCommunityViewHolder extends RecyclerView.ViewHolder{

    private SelectableRoundedImageView search_community_item_img_;
    private TextView search_community_item_label,
            search_community_item_title, search_community_item_content,
            search_community_item_comments, search_community_item_praise;

    public SearchCommunityViewHolder (View itemView, final SearchCommunityAdapter.MyItemClickListener mItemClickListener) {
        super(itemView);

        search_community_item_img_ =  itemView.findViewById(R.id.search_community_item_img_);
        search_community_item_label = itemView.findViewById(R.id.search_community_item_label);
        search_community_item_title = itemView.findViewById(R.id.search_community_item_title);
        search_community_item_content = itemView.findViewById(R.id.search_community_item_content);
        search_community_item_comments = itemView.findViewById(R.id.search_community_item_comments);
        search_community_item_praise = itemView.findViewById(R.id.search_community_item_praise);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v, getLayoutPosition());
            }
        });

        search_community_item_praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPraiseItemClick(v, getLayoutPosition());
            }
        });

    }

    public void bind(Context context, SearchDynamicResponse.ListBean listBean, String cont) {
        String circle_title = listBean.getCircle_title();
        if (TextUtils.isEmpty(circle_title)){
            search_community_item_label.setVisibility(View.GONE);
        }else {
            search_community_item_label.setVisibility(View.VISIBLE);
            search_community_item_label.setText("#" + circle_title + "#");
        }

        String title = listBean.getTitle();
        if (TextUtils.isEmpty(title)){
            search_community_item_title.setVisibility(View.GONE);
        }else {
            search_community_item_title.setVisibility(View.VISIBLE);
            search_community_item_title.setText(FindUtil.findSearch(Color.parseColor("#FFFFD102"), title, cont));
        }

        String content = listBean.getContent();
        if (TextUtils.isEmpty(content)){
            search_community_item_content.setVisibility(View.GONE);
        }else {
            search_community_item_content.setVisibility(View.VISIBLE);
            search_community_item_content.setText(FindUtil.findSearch(Color.parseColor("#FFFFD102"), content, cont));
        }

        search_community_item_comments.setText(listBean.getReply_num() + "");
        search_community_item_praise.setText(listBean.getLike_num() + "");
        if (listBean.getIs_like() == 0){
            Drawable drawable= context.getResources().getDrawable(R.drawable.community_item_icon_6);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            search_community_item_praise.setCompoundDrawables(drawable, null, null, null);
        }else {
            Drawable drawable= context.getResources().getDrawable(R.drawable.community_item_icon_7);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            search_community_item_praise.setCompoundDrawables(drawable, null, null, null);
        }

        String img = null;
        if (listBean.getVideo_list().size() > 0){
            //视频
            img = listBean.getVideo_list().get(0).getCover_img();
        }else if(listBean.getPicture_list().size() > 0){
            //图片
            img = listBean.getPicture_list().get(0).getThumb_img();
        }
        if (TextUtils.isEmpty(img)){
            search_community_item_img_.setVisibility(View.GONE);
        }else {
            search_community_item_img_.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.imageloader)
                    .error(R.drawable.imageloader)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(itemView.getContext())
                    .load(CommonUtils.getUrl(img))
                    .apply(options)// 参数  
                    .into(search_community_item_img_);
        }
    }

}

