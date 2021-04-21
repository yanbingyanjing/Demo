package com.yjfshop123.live.ui.videolist.holder;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.videolist.model.PicItem;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pandaq.emoticonlib.PandaEmoTranslator;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;


public class PicViewHolder extends BaseViewHolder<PicItem> {

    //
    @BindView(R.id.community_item_portrait)
    ImageView community_item_portrait;
    @BindView(R.id.community_item_nickname)
    TextView community_item_nickname;
    @BindView(R.id.community_item_location)
    TextView community_item_location;
    @BindView(R.id.community_item_title)
    TextView community_item_title;
    @BindView(R.id.community_item_content)
    TextView community_item_content;
    @BindView(R.id.community_item_label)
    TextView community_item_label;
    @BindView(R.id.community_item_comments)
    TextView community_item_comments;
    @BindView(R.id.community_item_praise)
    TextView community_item_praise;
    @BindView(R.id.community_item_sex)
    ImageView community_item_sex;
    @BindView(R.id.community_item_talent)
    ImageView community_item_talent;
    @BindView(R.id.community_item_vip)
    ImageView community_item_vip;

    //
    @BindView(R.id.community_item_img_)
    ImageView community_item_img_;
    @BindView(R.id.community_item_img_ll)
    LinearLayout community_item_img_ll;
    @BindView(R.id.community_item_img_1)
    ImageView community_item_img_1;
    @BindView(R.id.community_item_img_2)
    ImageView community_item_img_2;
    @BindView(R.id.community_item_img_3)
    ImageView community_item_img_3;
    @BindView(R.id.community_item_img_4)
    ImageView community_item_img_4;
    @BindView(R.id.community_item_img_5)
    ImageView community_item_img_5;
    @BindView(R.id.community_item_img_6)
    ImageView community_item_img_6;
    @BindView(R.id.community_item_img_7)
    ImageView community_item_img_7;
    @BindView(R.id.community_item_img_8)
    ImageView community_item_img_8;
    @BindView(R.id.community_item_img_9)
    ImageView community_item_img_9;

    @BindView(R.id.create_time)
    TextView create_time;
    private ImageView[] imageViews;

    private RequestOptions options_1;
    private RequestOptions options_2;
    private int width;
    private int width_2;

    public PicViewHolder(View itemView, int screenWidth) {
        super(itemView);
        imageViews = new ImageView[]{community_item_img_1,
                community_item_img_2, community_item_img_3, community_item_img_4,
                community_item_img_5, community_item_img_6, community_item_img_7,
                community_item_img_8, community_item_img_9};

        width = (screenWidth - CommonUtils.dip2px(itemView.getContext(), 110)) / 3;
        width_2 = (screenWidth - CommonUtils.dip2px(itemView.getContext(), 104)) / 2;

        options_1 = new RequestOptions()
                .placeholder(R.drawable.loadding)
                .error(R.drawable.loadding)
//                .transforms(new CenterCrop(), new CircleCrop())
//                .circleCropTransform()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        options_2 = new RequestOptions()
                .placeholder(R.drawable.imageloader)
                .error(R.drawable.imageloader)
//                .transforms(new CenterCrop(), new RoundedCorners(CommonUtils.dip2px(itemView.getContext(), 4)))
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBind(final int position, PicItem iItem, final CommunityClickListener mItemClickListener) {
        if(!TextUtils.isEmpty(iItem.getListBean().getAvatar()))
            Glide.with(itemView.getContext())
                    .load(CommonUtils.getUrl(iItem.getListBean().getAvatar()))
                    .apply(options_1)
                    .into(community_item_portrait);
        else {
            Glide.with(itemView.getContext())
                    .load(R.drawable.splash_logo)
                    .apply(options_1)
                    .into(community_item_portrait);
        }
        create_time.setText(iItem.getListBean().getPublish_time());

        community_item_nickname.setText(iItem.getListBean().getUser_nickname());
        if(TextUtils.isEmpty(iItem.getListBean().getCity_name())){
            community_item_location.setVisibility(View.GONE);
        }else   community_item_location.setVisibility(View.VISIBLE);
        community_item_location.setText(iItem.getListBean().getCity_name());
        String title = iItem.getListBean().getTitle();
        String content = iItem.getListBean().getContent();
        if (TextUtils.isEmpty(title)) {
            community_item_title.setVisibility(View.GONE);
        } else {
            community_item_title.setVisibility(View.VISIBLE);
//            community_item_title.setText(getContent(title));
            if (!TextUtils.isEmpty(content)) {
                community_item_title.setMaxLines(2);
                community_item_title.setText(PandaEmoTranslator
                        .getInstance()
                        .makeEmojiSpannable(title));
            } else {
                community_item_title.setMaxLines(10);
                SystemUtils.toggleEllipsize(itemView.getContext(), community_item_title,
                        94, PandaEmoTranslator
                                .getInstance()
                                .makeEmojiSpannable(title).toString(), itemView.getContext().getString(R.string.all__wen),
                        R.color.color_2D9DD8, false);
            }
        }

        if (TextUtils.isEmpty(content)) {
            community_item_content.setVisibility(View.GONE);
        } else {
            community_item_content.setVisibility(View.VISIBLE);
//            community_item_content.setText(getContent(content));
//            community_item_content.setText(PandaEmoTranslator
//                            .getInstance()
//                            .makeEmojiSpannable(content)
//                    /*.makeGifSpannable("PicViewHolder", content, new AnimatedGifDrawable.RunGifCallBack() {
//                        @Override
//                        public void run() {
//                            community_item_content.postInvalidate();
//                        }
//                    })*/);
            SystemUtils.toggleEllipsize(itemView.getContext(),community_item_content,
                    94,PandaEmoTranslator
                            .getInstance()
                            .makeEmojiSpannable(content).toString(),itemView.getContext().getString(R.string.all__wen),
                    R.color.color_2D9DD8,false);
        }
        if (iItem.getListBean().getSex() == 1) {
            community_item_sex.setImageResource(R.mipmap.boy);
        } else {
            community_item_sex.setImageResource(R.mipmap.girl);
        }
        if (iItem.getListBean().getDaren_status() == 2) {
            community_item_talent.setVisibility(View.VISIBLE);
        } else {
            community_item_talent.setVisibility(View.GONE);
        }
        if (iItem.getListBean().getIs_vip() == 1) {
            community_item_vip.setVisibility(View.VISIBLE);
        } else {
            community_item_vip.setVisibility(View.GONE);
        }

        String circle_title = iItem.getListBean().getCircle_title();
        if (TextUtils.isEmpty(circle_title)) {
            community_item_label.setVisibility(View.GONE);
        } else {
            community_item_label.setVisibility(View.VISIBLE);
            community_item_label.setText("#" + circle_title + "#");
        }

       // community_item_comments.setText(iItem.getListBean().getReply_num() + "");
       // community_item_praise.setText(iItem.getListBean().getLike_num() + "");
        if (iItem.getListBean().getIs_like() == 0) {
            Drawable drawable = itemView.getContext().getResources().getDrawable(R.mipmap.xiaoshou);
            drawable.setTint(itemView.getContext().getResources().getColor(R.color.color_999999));
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            community_item_praise.setCompoundDrawables(drawable, null, null, null);
        } else {
            Drawable drawable = itemView.getContext().getResources().getDrawable(R.mipmap.xiaoshou);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            drawable.setTint( itemView.getContext().getResources().getColor(R.color.color_FAE2AE));
            community_item_praise.setCompoundDrawables(drawable, null, null, null);
        }

        goneView();

        if (iItem.getListBean().getPicture_list().size() == 1) {
            community_item_img_.setVisibility(View.VISIBLE);
            community_item_img_ll.setVisibility(View.GONE);

            //params(community_item_img_, width_2 * 6 / 4);//*1.5
            params(community_item_img_, width_2*10/5);//*1.5

            Glide.with(itemView.getContext())
                    .load(CommonUtils.getUrl(iItem.getListBean().getPicture_list().get(0).getThumb_img()))
                   //// .apply(options_2)
                    .into(community_item_img_);

            community_item_img_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onImgClick(v, position, 0);
                }
            });
        } else if (iItem.getListBean().getPicture_list().size() == 4) {
            community_item_img_.setVisibility(View.GONE);
            community_item_img_ll.setVisibility(View.VISIBLE);

            for (int i = 0; i < 4; i++) {
                ImageView imgView;
                if (i == 2) {
                    imgView = imageViews[3];
                } else if (i == 3) {
                    imgView = imageViews[4];
                } else {
                    imgView = imageViews[i];
                }
                imgView.setVisibility(View.VISIBLE);
                params(imgView, width_2);
                Glide.with(itemView.getContext())
                        .load(CommonUtils.getUrl(iItem.getListBean().getPicture_list().get(i).getThumb_img()))
                        .apply(options_2)
                        .into(imgView);

                final int pos = i;
                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onImgClick(v, position, pos);
                    }
                });
            }
        } else {
            community_item_img_.setVisibility(View.GONE);
            community_item_img_ll.setVisibility(View.VISIBLE);

            int size = iItem.getListBean().getPicture_list().size();
            for (int i = 0; i < size; i++) {
                if (i < 9) {
                    imageViews[i].setVisibility(View.VISIBLE);
                    ImageView imgView = imageViews[i];
                    if (size == 2) {
                        params(imgView, width_2);
                    } else {
                        params(imgView, width);
                    }
                    Glide.with(itemView.getContext())
                            .load(CommonUtils.getUrl(iItem.getListBean().getPicture_list().get(i).getThumb_img()))
                            .apply(options_2)
                            .into(imgView);

                    final int pos = i;
                    imageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItemClickListener.onImgClick(v, position, pos);
                        }
                    });
                }
            }
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onContentClick(v, position);
            }
        });

        community_item_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPortraitClick(v, position);
            }
        });

        community_item_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPortraitClick(v, position);
            }
        });

        community_item_praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPraiseClick(v, position);
            }
        });
    }

    private void goneView() {
        gone(community_item_img_);
        gone(community_item_img_ll);
        for (int i = 0; i < imageViews.length; i++) {
            gone(imageViews[i]);
        }
    }

    private void gone(View view) {
        view.setVisibility(View.GONE);
    }

    private void params(ImageView imgView, int width) {
        ViewGroup.LayoutParams params = imgView.getLayoutParams();
        params.width = width;
        params.height = width;
        imgView.setLayoutParams(params);
    }

    //过滤表情
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
