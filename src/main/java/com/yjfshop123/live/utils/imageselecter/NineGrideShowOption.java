package com.yjfshop123.live.utils.imageselecter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.CommunityReplyDetailResponse;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 *
 * 日期:2019/2/20
 * 描述:
 **/
public class NineGrideShowOption {

    private Activity itemView;
    private CommunityReplyDetailResponse.DetailBean detailBean;
    private int width;
    private int width_2;
    private RequestOptions options_2;

    private ImageView community_item_img_;
    private LinearLayout community_item_img_ll;
    private View[] views;

    public NineGrideShowOption(Activity itemView,CommunityReplyDetailResponse.DetailBean detailBean,int screenWidth) {
        this.itemView=itemView;
        this.detailBean=detailBean;

        width = (screenWidth - CommonUtils.dip2px(itemView, 32)) / 3;
        width_2 = (screenWidth - CommonUtils.dip2px(itemView, 26)) / 2;
        options_2 = new RequestOptions()
                .placeholder(R.drawable.imageloader)
                .error(R.drawable.imageloader)
//                .transforms(new CenterCrop(), new RoundedCorners(CommonUtils.dip2px(itemView, 4)))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    public void initView(){
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
                mItemClickListener.onImgClick(v, 0);
            }
        });
    }

    public void imageShort(){
        goneView();

        if (detailBean.getPicture_list().size() == 1){
            community_item_img_.setVisibility(View.VISIBLE);
            community_item_img_ll.setVisibility(View.GONE);

            params(community_item_img_, width_2 * 6 / 4);//*1.5

            Glide.with(itemView)
                    .load(CommonUtils.getUrl(detailBean.getPicture_list().get(0).getThumb_img()))
                    .apply(options_2)
                    .into(community_item_img_);
        }else if (detailBean.getPicture_list().size() == 4){
            community_item_img_.setVisibility(View.GONE);
            community_item_img_ll.setVisibility(View.VISIBLE);

            for (int i = 0; i < 4; i++) {
                View view;
                if (i == 2){
                    view = views[3];
                }else if (i == 3){
                    view = views[4];
                }else {
                    view = views[i];
                }
                view.setVisibility(View.VISIBLE);
                ImageView imgView = (ImageView) view;
                params(imgView, width_2);
                Glide.with(itemView)
                        .load(CommonUtils.getUrl(detailBean.getPicture_list().get(i).getThumb_img()))
                        .apply(options_2)
                        .into(imgView);

                final int pos = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onImgClick(v, pos);
                    }
                });
            }
        }else {
            community_item_img_.setVisibility(View.GONE);
            community_item_img_ll.setVisibility(View.VISIBLE);

            int size = detailBean.getPicture_list().size();
            for (int i = 0; i < size; i++) {
                if (i < 9){
                    views[i].setVisibility(View.VISIBLE);
                    ImageView imgView = (ImageView) views[i];
                    if (size == 2){
                        params(imgView, width_2);
                    }else {
                        params(imgView, width);
                    }
                    Glide.with(itemView)
                            .load(CommonUtils.getUrl(detailBean.getPicture_list().get(i).getThumb_img()))
                            .apply(options_2)
                            .into(imgView);

                    final int pos = i;
                    views[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItemClickListener.onImgClick(v, pos);
                        }
                    });
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

    private MyItemClickListener mItemClickListener;

    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface MyItemClickListener {
        void onImgClick(View view,  int index);
    }

}
