package com.yjfshop123.live.ui.videolist.holder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pandaq.emoticonlib.PandaEmoTranslator;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.PopularDynamicResponse;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.videolist.model.PicItem;
import com.yjfshop123.live.ui.widget.NewImageLoader;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.youth.banner.transformer.AccordionTransformer;

import java.util.Arrays;

import butterknife.BindView;

public class PicViewHolderGuanzhu extends BaseViewHolder<PicItem> {
    @BindView(R.id.banner)
    Banner banner;
    //
    @BindView(R.id.community_item_portrait)
    ImageView community_item_portrait;
    @BindView(R.id.community_item_nickname)
    TextView community_item_nickname;

    @BindView(R.id.community_item_title)
    TextView community_item_title;
    @BindView(R.id.community_item_content)
    TextView community_item_content;

    @BindView(R.id.community_item_comments)
    TextView community_item_comments;
    @BindView(R.id.community_item_praise)
    TextView community_item_praise;
    @BindView(R.id.zhankai)
    TextView zhankai;


    @BindView(R.id.create_time)
    TextView create_time;
    @BindView(R.id.pinglun)
    LinearLayout pinglun;
    @BindView(R.id.second)
    LinearLayout second;
    @BindView(R.id.first)
    LinearLayout first;


    @BindView(R.id.first_name)
    TextView first_name;
    @BindView(R.id.first_content)
    TextView first_content;

    @BindView(R.id.second_name)
    TextView second_name;
    @BindView(R.id.second_content)
    TextView second_content;

    private RequestOptions options_1;
    private RequestOptions options_2;
    private int width;
    private int width_2;

    public PicViewHolderGuanzhu(View itemView, int screenWidth) {
        super(itemView);


        width = (screenWidth - CommonUtils.dip2px(itemView.getContext(), 110)) / 3;
        width_2 = (screenWidth - CommonUtils.dip2px(itemView.getContext(), 104)) / 2;
        LinearLayout.LayoutParams paramsBanner = (LinearLayout.LayoutParams) banner.getLayoutParams();
        //获取当前控件的布局对象
        paramsBanner.width = SystemUtils.getScreenWidth(itemView.getContext()) - CommonUtils.dip2px(itemView.getContext(), 32);
        paramsBanner.height = paramsBanner.width;
        banner.setLayoutParams(paramsBanner);
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
        if (!TextUtils.isEmpty(iItem.getListBean().getAvatar()))
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

        String title = iItem.getListBean().getTitle();
        final String content = iItem.getListBean().getContent();
        if (TextUtils.isEmpty(title)) {
            community_item_title.setVisibility(View.GONE);
        } else {
            community_item_title.setVisibility(View.VISIBLE);
//            community_item_title.setText(getContent(title));

            community_item_title.setText(PandaEmoTranslator
                    .getInstance()
                    .makeEmojiSpannable(title));
        }
        if (iItem.getListBean().getPicture_list() != null && iItem.getListBean().getPicture_list().size() > 0) {
            banner.setVisibility(View.VISIBLE);
            banner.setImages(iItem.getListBean().getPicture_list())
                    //.setBannerTitles(App.titles)//标
                    //.setBannerAnimation(AccordionTransformer.class)//动画
                    .setIndicatorGravity(BannerConfig.CENTER)//指示器位置
                    .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)//指示器样式
                    .isAutoPlay(false)
                    .setImageLoader(new NewImageLoader() {
                        @Override
                        public void displayImage(Context context, Object path, SelectableRoundedImageView imageView) {
                            if (path != null)
                                Glide.with(context)
                                        .load(CommonUtils.getUrl(((PopularDynamicResponse.ListBean.PictureListBean) path).getThumb_img()))
                                        .into(imageView);
                        }

                    }).setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int index) {
                    mItemClickListener.onImgClick(itemView, position, index);
                }
            }).start()
            ;
        } else banner.setVisibility(View.GONE);
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
            if (content.length() > 94) {
                community_item_content.setMaxLines(1);
                zhankai.setVisibility(View.VISIBLE);
                community_item_content.setText(PandaEmoTranslator
                        .getInstance()
                        .makeEmojiSpannable(content));
                zhankai.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        community_item_content.setMaxLines(Integer.MAX_VALUE);
                        community_item_content.setText(PandaEmoTranslator
                                .getInstance()
                                .makeEmojiSpannable(content));
                        zhankai.setVisibility(View.GONE);
                    }
                });
            } else {
                zhankai.setVisibility(View.GONE);
                community_item_content.setMaxLines(10);
                community_item_content.setText(PandaEmoTranslator
                        .getInstance()
                        .makeEmojiSpannable(content));
            }
        }
        community_item_praise.setText(NumUtil.formatNum(iItem.getListBean().getLike_num() + "", false));
        community_item_comments.setText(NumUtil.formatNum(iItem.getListBean().getReply_num() + "", false));

        if (iItem.getListBean().getIs_like() == 0) {
            Drawable drawable = itemView.getContext().getResources().getDrawable(R.mipmap.xiaoshou);
            drawable.setTint(itemView.getContext().getResources().getColor(R.color.color_999999));
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            community_item_praise.setCompoundDrawables(drawable, null, null, null);
        } else {
            Drawable drawable = itemView.getContext().getResources().getDrawable(R.mipmap.xiaoshou);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            drawable.setTint(itemView.getContext().getResources().getColor(R.color.color_FAE2AE));
            community_item_praise.setCompoundDrawables(drawable, null, null, null);
        }

        goneView();

        if (iItem.getListBean().reply_list != null && iItem.getListBean().reply_list.getList()!=null&& iItem.getListBean().reply_list.getList().size() > 0) {
            pinglun.setVisibility(View.VISIBLE);
            first.setVisibility(View.GONE);
            second.setVisibility(View.GONE);
            if( iItem.getListBean().reply_list.getList().size() >=1){
                first.setVisibility(View.VISIBLE);
                first_name.setText( iItem.getListBean().reply_list.getList().get(0).getUser_nickname()+"：");
                first_content.setText( iItem.getListBean().reply_list.getList().get(0).getContent());
            }
            if( iItem.getListBean().reply_list.getList().size() >=2){
                second.setVisibility(View.VISIBLE);
                second_name.setText( iItem.getListBean().reply_list.getList().get(1).getUser_nickname()+"：");
                second_content.setText(iItem.getListBean().reply_list.getList().get(1).getContent());
            }
        }else {
            pinglun.setVisibility(View.GONE);
        }
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
