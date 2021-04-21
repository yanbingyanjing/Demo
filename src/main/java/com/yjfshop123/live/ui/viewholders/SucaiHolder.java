package com.yjfshop123.live.ui.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
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
import com.yjfshop123.live.model.ShareSucaiResopnse;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.ui.activity.XPicturePagerActivity;
import com.yjfshop123.live.ui.videolist.CommunityClickListener;
import com.yjfshop123.live.ui.videolist.holder.BaseViewHolder;
import com.yjfshop123.live.ui.videolist.model.PicItem;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SucaiHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.community_item_title)
    TextView community_item_title;
    @BindView(R.id.community_item_content)
    TextView community_item_content;


    @BindView(R.id.save_pic)
    TextView save_pic;
    @BindView(R.id.copy)
    TextView copy;
    @BindView(R.id.share_url)
    TextView share_url;
    @BindView(R.id.download_url)
    TextView download_url;
    @BindView(R.id.download_url_btn)
    TextView download_url_btn;
    @BindView(R.id.select)
    CircleImageView select;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.tuiguang)
    LinearLayout tuiguang;


    private RequestOptions options_1;
    private RequestOptions options_2;
    private int width;
    private int width_2;

    public SucaiHolder(View itemView, int screenWidth) {
        super(itemView);
        ButterKnife.bind(this, itemView);


        width = (screenWidth - CommonUtils.dip2px(itemView.getContext(), 70)) / 3;
        width_2 = (screenWidth - CommonUtils.dip2px(itemView.getContext(), 74)) / 2;

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

    LinearLayoutManager mLinearLayoutManager;
    UploadAdapter uploadAdapter;

    public void onBind(final Context context, final int position, final ShareSucaiResopnse.Sucai iItem, final CommunityClickListener mItemClickListener) {
        if (iItem.pic_list != null) {
            list.setVisibility(View.VISIBLE);
            mLinearLayoutManager = new LinearLayoutManager(context);
            mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
            list.setLayoutManager(mLinearLayoutManager);
            uploadAdapter = new UploadAdapter(context, null, iItem.share_url);
            list.setAdapter(uploadAdapter);
            uploadAdapter.setCards(Arrays.asList(iItem.pic_list));
        } else list.setVisibility(View.GONE);

        if (iItem.isSelect) {
            Glide.with(context).load(R.mipmap.yellow_gou).into(select);
        } else {
            Glide.with(context).load(R.mipmap.not_gou).into(select);
        }
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iItem.isSelect) {
                    return;
                }
                if (mItemClickListener != null)
                    mItemClickListener.onPraiseClick(v, position);
            }
        });
        if (TextUtils.isEmpty(iItem.title)) {
            community_item_title.setVisibility(View.GONE);
        } else {
            community_item_title.setVisibility(View.VISIBLE);
//            community_item_title.setText(getContent(title));
            if (!TextUtils.isEmpty(iItem.title)) {
                community_item_title.setMaxLines(2);
                community_item_title.setText("标题：" + PandaEmoTranslator
                        .getInstance()
                        .makeEmojiSpannable(iItem.title));
            } else {
                community_item_title.setMaxLines(10);
                SystemUtils.toggleEllipsize(itemView.getContext(), community_item_title,
                        94, "标题：" + PandaEmoTranslator
                                .getInstance()
                                .makeEmojiSpannable(iItem.title).toString(), itemView.getContext().getString(R.string.all__wen),
                        R.color.color_2D9DD8, false);
            }
        }
        download_url_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.setClipboard(context, iItem.share_url);
            }
        });
//        if (TextUtils.isEmpty(iItem.share_url)) {
//            tuiguang.setVisibility(View.GONE);
//        } else {
//            tuiguang.setVisibility(View.VISIBLE);
//        }
        download_url.setText(iItem.share_url);
        if (TextUtils.isEmpty(iItem.content)) {
            community_item_content.setVisibility(View.GONE);
        } else {
            community_item_content.setVisibility(View.VISIBLE);
            SystemUtils.toggleEllipsize(itemView.getContext(), community_item_content,
                    94, "内容：" + PandaEmoTranslator
                            .getInstance()
                            .makeEmojiSpannable(iItem.content).toString(), itemView.getContext().getString(R.string.all__wen),
                    R.color.color_2D9DD8, false);
        }
        save_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null)
                    mItemClickListener.onPortraitClick(v, position);
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null)
                    mItemClickListener.onContentClick(v, position);
            }
        });
        share_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null)
                    mItemClickListener.onPraiseClick(v, position);
            }
        });

    }


    public class UploadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        String shareUrl;
        private List<String> mList;
        private LayoutInflater layoutInflater;
        private Context context;
        View.OnClickListener listener;

        public UploadAdapter(Context context, View.OnClickListener listener, String shareUrl) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.listener = listener;
            this.shareUrl = shareUrl;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Vh(layoutInflater.inflate(R.layout.item_upload_sucai, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((Vh) holder).setData(mList.get(position));

        }

        @Override
        public int getItemCount() {
            return mList != null ? mList.size() : 0;
        }

        public void setCards(List<String> list) {
            if (list == null) {
                return;
            }
            mList = list;
            notifyDataSetChanged();
        }

        public class Vh extends RecyclerView.ViewHolder {

            SelectableRoundedImageView imageView;

            public Vh(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.images);

            }

            void setData(final String bean) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(context, XPicturePagerActivity.class);
                            intent.putExtra(Config.POSITION, getLayoutPosition());
                            if (!TextUtils.isEmpty(shareUrl))
                                intent.putExtra("erweima", shareUrl);

                            intent.putExtra("Picture", JsonMananger.beanToJson(mList));
                            ((Activity) context).startActivity(intent);
                            ((Activity) context).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        } catch (HttpException e) {
                            e.printStackTrace();
                        }
                    }
                });
                com.bumptech.xchat.Glide.with(context).load(bean).into(imageView);
            }

        }
    }


}
