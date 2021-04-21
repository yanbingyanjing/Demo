package com.yjfshop123.live.ui.videolist.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bytedance.sdk.openadsdk.preload.geckox.model.UpdatePackage;
import com.pandaq.emoticonlib.PandaEmoTranslator;
import com.yjfshop123.live.R;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.videolist.model.PicItem;
import com.yjfshop123.live.ui.videolist.model.VideoItem;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;

import butterknife.BindView;

public class PicViewHolderNew extends BaseViewHolder<PicItem> {

    @BindView(R.id.community_item_img)
    SelectableRoundedImageView community_item_img;
    @BindView(R.id.community_item_title)
    TextView community_item_title;
    @BindView(R.id.community_item_portrait)
    CircleImageView community_item_portrait;
    @BindView(R.id.community_item_img_ll)
    RelativeLayout community_item_img_ll;
    @BindView(R.id.video)
    ImageView video;
    @BindView(R.id.community_item_nickname)
    TextView community_item_nickname;
    @BindView(R.id.zan_ll)
    LinearLayout zan_ll;
    @BindView(R.id.zan)
    ImageView zan;
    @BindView(R.id.zan_count)
    TextView zan_count;
    @BindView(R.id.pinglun_ll)
    LinearLayout pinglun_ll;
    @BindView(R.id.pinglun_count)
    TextView pinglun_count;
    @BindView(R.id.wenzi)
    ImageView wenzi;

    private RequestOptions options_1;
    private RequestOptions options_2;

    public PicViewHolderNew(View view, Context context) {
        super(view);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        //获取当前控件的布局对象

        params.width = (SystemUtils.getScreenWidth(context) - SystemUtils.dip2px(context, 25)) / 2;
        view.setLayoutParams(params);

        RelativeLayout.LayoutParams paramsImg = (RelativeLayout.LayoutParams) community_item_img.getLayoutParams();
        //获取当前控件的布局对象

        paramsImg.width = (SystemUtils.getScreenWidth(context) - SystemUtils.dip2px(context, 25)) / 2;
        paramsImg.height = paramsImg.width * 198 / 168;//设置当前控件布局的高度
        community_item_img.setLayoutParams(paramsImg);
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

        community_item_nickname.setText(iItem.getListBean().getUser_nickname());
        String title = iItem.getListBean().getTitle();
        if (TextUtils.isEmpty(title)) {
            community_item_title.setVisibility(View.GONE);
        } else {
            community_item_title.setVisibility(View.VISIBLE);
//            community_item_title.setText(getContent(title));
            community_item_title.setMaxLines(2);
            community_item_title.setText(PandaEmoTranslator
                    .getInstance()
                    .makeEmojiSpannable(title));
        }
        zan_count.setText(NumUtil.formatNum(iItem.getListBean().getLike_num() + "", false));
        pinglun_count.setText(NumUtil.formatNum(iItem.getListBean().getReply_num() + "", false));
        if (iItem.getListBean().getIs_like() == 0) {
            Glide.with(itemView.getContext()).load(R.mipmap.xiaoxinxin).into(zan);
        } else {
            Glide.with(itemView.getContext()).load(R.mipmap.xiaoxinxin_red).into(zan);
        }

        if (iItem.getListBean().getPicture_list().size() > 0) {
            community_item_img_ll.setVisibility(View.VISIBLE);
            wenzi.setVisibility(View.GONE);
            Glide.with(itemView.getContext())
                    .load(CommonUtils.getUrl(iItem.getListBean().getPicture_list().get(0).getThumb_img()))
                    //// .apply(options_2)
                    .into(community_item_img);
        } else {
            community_item_img_ll.setVisibility(View.GONE);
            wenzi.setVisibility(View.VISIBLE);
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

        zan_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onPraiseClick(v, position);
            }
        });
    }
}
