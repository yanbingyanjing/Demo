package com.yjfshop123.live.ui.viewholders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.PopularDynamicResponse;
import com.yjfshop123.live.ui.adapter.CommunityAdapter;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.pandaq.emoticonlib.PandaEmoTranslator;

public class CommunityHolder extends RecyclerView.ViewHolder {

    //类型（1:视频 2:图片）
    private final static int VIEW_ITEM_1 = 1;
    private final static int VIEW_ITEM_2 = 2;
    private final static int VIEW_ITEM_BOTTOM = 3;

    private ImageView community_item_portrait;
    private TextView community_item_nickname, community_item_location, community_item_title,
            community_item_content, community_item_label, community_item_comments,
            community_item_praise/*, community_item_time*/;
    private ImageView community_item_sex, community_item_talent, community_item_vip;

    private ImageView community_item_video;

    private ImageView community_item_img_;
    private LinearLayout community_item_img_ll;
    private View[] views;

    private CommunityAdapter.MyItemClickListener mItemClickListener;

    public CommunityHolder (View itemView, int viewType, CommunityAdapter.MyItemClickListener itemClickListener) {
        super(itemView);
        if (viewType == VIEW_ITEM_BOTTOM){
            return;
        }

        //通用
        community_item_portrait =  itemView.findViewById(R.id.community_item_portrait);
        community_item_nickname =  itemView.findViewById(R.id.community_item_nickname);
        community_item_sex =  itemView.findViewById(R.id.community_item_sex);
        community_item_talent =  itemView.findViewById(R.id.community_item_talent);
        community_item_vip =  itemView.findViewById(R.id.community_item_vip);
        community_item_location =  itemView.findViewById(R.id.community_item_location);
        community_item_title =  itemView.findViewById(R.id.community_item_title);
        community_item_content =  itemView.findViewById(R.id.community_item_content);
        community_item_label =  itemView.findViewById(R.id.community_item_label);
        community_item_comments =  itemView.findViewById(R.id.community_item_comments);
        community_item_praise =  itemView.findViewById(R.id.community_item_praise);
//        community_item_time =  itemView.findViewById(R.id.community_item_time);

        if (viewType == VIEW_ITEM_1){
            community_item_video = itemView.findViewById(R.id.community_item_video);
        } else if (viewType == VIEW_ITEM_2) {
            community_item_img_ =  itemView.findViewById(R.id.community_item_img_);
            community_item_img_ll =  itemView.findViewById(R.id.community_item_img_ll);
            View community_item_img_1 =  itemView.findViewById(R.id.community_item_img_1);
            View community_item_img_2 =  itemView.findViewById(R.id.community_item_img_2);
            View community_item_img_3 =  itemView.findViewById(R.id.community_item_img_3);
            View community_item_img_4 =  itemView.findViewById(R.id.community_item_img_4);
            View community_item_img_5 =  itemView.findViewById(R.id.community_item_img_5);
            View community_item_img_6 =  itemView.findViewById(R.id.community_item_img_6);
            View community_item_img_7 =  itemView.findViewById(R.id.community_item_img_7);
            View community_item_img_8 =  itemView.findViewById(R.id.community_item_img_8);
            View community_item_img_9 =  itemView.findViewById(R.id.community_item_img_9);
            views = new View[]{community_item_img_1,
                    community_item_img_2, community_item_img_3, community_item_img_4,
                    community_item_img_5, community_item_img_6, community_item_img_7,
                    community_item_img_8, community_item_img_9};

            community_item_img_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onImgClick(v, getLayoutPosition(), 0);
                }
            });
        }

        mItemClickListener = itemClickListener;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onContentClick(v, getLayoutPosition());
            }
        });

        community_item_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPortraitClick(v, getLayoutPosition());
            }
        });

        community_item_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPortraitClick(v, getLayoutPosition());
            }
        });

        community_item_praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPraiseClick(v, getLayoutPosition());
            }
        });

//        options_1 = new RequestOptions()
//                .placeholder(R.drawable.loadding)
//                .error(R.drawable.loadding)
//                .transforms(new CenterCrop(), new CircleCrop())
//                .circleCropTransform()
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

//        options_2 = new RequestOptions()
//                .placeholder(R.drawable.imageloader)
//                .error(R.drawable.imageloader)
//                .transforms(new CenterCrop(), new RoundedCorners(CommonUtils.dip2px(itemView.getContext(), 4)))
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

//    private RequestOptions options_1;
//    private RequestOptions options_2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void bind(Context context, PopularDynamicResponse.ListBean listBean, int width, int viewType,
                     int width_2, int height_video) {

        Glide.with(context)
                .load(CommonUtils.getUrl(listBean.getAvatar()))
//                .apply(options_1)
                .into(community_item_portrait);

        community_item_nickname.setText(listBean.getUser_nickname());
        if(TextUtils.isEmpty(listBean.getCity_name())){
            community_item_location.setVisibility(View.GONE);
        }else community_item_location.setVisibility(View.VISIBLE);
        community_item_location.setText(listBean.getCity_name());
        String title = listBean.getTitle();
        if (TextUtils.isEmpty(title)){
            community_item_title.setVisibility(View.GONE);
        }else {
            community_item_title.setVisibility(View.VISIBLE);
//            community_item_title.setText(getContent(title));
            community_item_title.setText(PandaEmoTranslator
                    .getInstance()
                    .makeEmojiSpannable(title));
        }
        String content = listBean.getContent();
        if (TextUtils.isEmpty(content)){
            community_item_content.setVisibility(View.GONE);
        }else {
            community_item_content.setVisibility(View.VISIBLE);
//            community_item_content.setText(getContent(content));
            community_item_content.setText(PandaEmoTranslator
                    .getInstance()
                    .makeEmojiSpannable(content));
        }
        if (listBean.getSex() == 1){
            community_item_sex.setImageResource(R.mipmap.boy);
        }else {
            community_item_sex.setImageResource(R.mipmap.girl);
        }
        if (listBean.getDaren_status() == 2){
            community_item_talent.setVisibility(View.VISIBLE);
        }else {
            community_item_talent.setVisibility(View.GONE);
        }
        if (listBean.getIs_vip() == 1){
            community_item_vip.setVisibility(View.VISIBLE);
        }else {
            community_item_vip.setVisibility(View.GONE);
        }

        String circle_title = listBean.getCircle_title();
        if (TextUtils.isEmpty(circle_title)){
            community_item_label.setVisibility(View.GONE);
        }else {
            community_item_label.setVisibility(View.VISIBLE);
            community_item_label.setText("#" + circle_title + "#");
        }

        community_item_comments.setText(listBean.getReply_num() + "");
        community_item_praise.setText(listBean.getLike_num() + "");
        if (listBean.getIs_like() == 0){
            Drawable drawable= itemView.getContext().getResources().getDrawable(R.mipmap.zan_black);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            //   drawable.setTint(itemView.getContext().getResources().getColor(R.color.color_999999));
            community_item_praise.setCompoundDrawables(drawable, null, null, null);
        }else {
            Drawable drawable= itemView.getContext().getResources().getDrawable(R.mipmap.zan_black);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            drawable.setTint( itemView.getContext().getResources().getColor(R.color.color_FAE2AE));
            community_item_praise.setCompoundDrawables(drawable, null, null, null);
        }
//        if (listBean.getIs_like() == 0){
//            Drawable drawable= context.getResources().getDrawable(R.drawable.community_item_icon_6);
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//            community_item_praise.setCompoundDrawables(drawable, null, null, null);
//        }else {
//            Drawable drawable= context.getResources().getDrawable(R.drawable.community_item_icon_7);
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//            community_item_praise.setCompoundDrawables(drawable, null, null, null);
//        }
//        community_item_time.setText(listBean.getPublish_time());

        if (viewType == VIEW_ITEM_1){

            Glide.with(context)
                    .load(CommonUtils.getUrl(listBean.getVideo_list().get(0).getCover_img()))
//                    .apply(options_2)
                    .into(community_item_video);

            ViewGroup.LayoutParams params_i = community_item_video.getLayoutParams();
            params_i.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params_i.height = height_video * 5 / 9;
            community_item_video.setLayoutParams(params_i);
        } else if (viewType == VIEW_ITEM_2) {

            goneView();

            if (listBean.getPicture_list().size() == 1){
                community_item_img_.setVisibility(View.VISIBLE);
                community_item_img_ll.setVisibility(View.GONE);

                params(community_item_img_, width_2 * 6 / 4);//*1.5

                Glide.with(context)
                        .load(CommonUtils.getUrl(listBean.getPicture_list().get(0).getThumb_img()))
//                        .apply(options_2)
                        .into(community_item_img_);
            }else if (listBean.getPicture_list().size() == 4){
                community_item_img_.setVisibility(View.GONE);
                community_item_img_ll.setVisibility(View.VISIBLE);

                for (int i = 0; i < 4; i++) {
                    View view_;
                    if (i == 2){
                        view_ = views[3];
                    }else if (i == 3){
                        view_ = views[4];
                    }else {
                        view_ = views[i];
                    }
                    view_.setVisibility(View.VISIBLE);
                    ImageView imgView = (ImageView) view_;
                    params(imgView, width_2);
                    Glide.with(context)
                            .load(CommonUtils.getUrl(listBean.getPicture_list().get(i).getThumb_img()))
//                            .apply(options_2)
                            .into(imgView);

                    final int pos = i;
                    view_.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItemClickListener.onImgClick(v, getLayoutPosition(), pos);
                        }
                    });
                }
            }else {
                community_item_img_.setVisibility(View.GONE);
                community_item_img_ll.setVisibility(View.VISIBLE);

                int size = listBean.getPicture_list().size();
                for (int i = 0; i < size; i++) {
                    if (i < 9){
                        views[i].setVisibility(View.VISIBLE);
                        ImageView imgView = (ImageView) views[i];
                        if (size == 2){
                            params(imgView, width_2);
                        }else {
                            params(imgView, width);
                        }
                        Glide.with(context)
                                .load(CommonUtils.getUrl(listBean.getPicture_list().get(i).getThumb_img()))
//                                .apply(options_2)
                                .into(imgView);

                        final int pos = i;
                        views[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mItemClickListener.onImgClick(v, getLayoutPosition(), pos);
                            }
                        });
                    }
                }
            }
        }
    }

    private void goneView(){
        gone(community_item_img_);
        gone(community_item_img_ll);
        for (int i = 0; i < views.length; i++) {
            gone(views[i]);
        }
    }

    private void gone(View view){
        view.setVisibility(View.GONE);
    }

    private void params(ImageView imgView, int width){
        ViewGroup.LayoutParams params = imgView.getLayoutParams();
        params.width = width;
        params.height = width;
        imgView.setLayoutParams(params);
    }

    /*private String getContent(String content){
        int indexStart = content.indexOf("[");
        int indexEnd = content.indexOf("]");
        if (indexStart == -1 || indexEnd == -1){
            return content;
        }else {
            String content_s = content.substring(0, indexStart);
            int length = content.length();
            if (indexEnd < length - 1){
                String content_e = content.substring(indexEnd + 1, length);
                return getContent(content_s + content_e);
            }else {
                return getContent(content_s);
            }
        }
    }*/
}

