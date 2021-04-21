package com.yjfshop123.live.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.InviteImgResponse;
import com.yjfshop123.live.net.response.PromDataResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.widget.HaiBaoDialog;
import com.yjfshop123.live.ui.widget.ShowImgDialog;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhouwei.mzbanner.MZBannerView.dpToPx;

public class InviteFriendActivity extends BaseActivityForNewUi {
    @BindView(R.id.invite_code)
    TextView inviteCode;
    @BindView(R.id.invite_copy_btn)
    TextView inviteCopyBtn;
    @BindView(R.id.download_url)
    TextView downloadUrl;
    @BindView(R.id.download_url_btn)
    TextView downloadUrlBtn;
    @BindView(R.id.mm_details_banner)
    MZBannerView mmDetailsBanner;
    @BindView(R.id.crete_haibao)
    TextView creteHaibao;
    private String link;//分享链接
    private String title;//分享标题
    private String desc;//分享描述
    private String icon_link;//分享ICON图
    private String qr_url;//二维码 图片URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        ButterKnife.bind(this);
        mContext = this;
        setCenterTitleText(getString(R.string.invite_friend));

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mmDetailsBanner.getLayoutParams();
//获取当前控件的布局对象
        params.width = width;
        params.height = (params.width - dpToPx(108)) * 419 / 270;//设置当前控件布局的高度
        mmDetailsBanner.setLayoutParams(params);//将设置好的布局参数应用到控件中
        banner();
        OKHttpUtils.getInstance().getRequest("app/promotion/getPromData", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                try {
                    PromDataResponse response = JsonMananger.jsonToBean(result, PromDataResponse.class);
                    link = response.getShare_link().getLink();
                    title = response.getShare_link().getTitle();
                    desc = response.getShare_link().getDesc();
                    icon_link = CommonUtils.getUrl(response.getShare_link().getIcon_url());
                    qr_url = response.getShare_qr().getQr_url();
                    downloadUrl.setText(link);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        inviteCode.setText(UserInfoUtil.getInviteCode());

    }

    List<String> images = new ArrayList<>();

    private void banner() {
        OKHttpUtils.getInstance().getRequest("app/member/inviteImg", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                try {
                    InviteImgResponse response = JsonMananger.jsonToBean(result, InviteImgResponse.class);
                    if(response.list!=null&&response.list.size()>0){
                        images=response.list;
                        mmDetailsBanner.setPages(images, mzHolderCreator);
                        mmDetailsBanner.setIndicatorVisible(false);
                        mmDetailsBanner.setDelayedTime(5000);
                        // mmDetailsBanner.start();
                   }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private MZHolderCreator mzHolderCreator = new MZHolderCreator<BannerPaddingViewHolder>() {
        @Override
        public BannerPaddingViewHolder createViewHolder() {
            return new BannerPaddingViewHolder();
        }
    };

    @OnClick({R.id.invite_copy_btn, R.id.download_url_btn, R.id.crete_haibao})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.invite_copy_btn:
                if (!TextUtils.isEmpty(inviteCode.getText().toString()))
                    SystemUtils.setClipboard(this, inviteCode.getText().toString());
                break;
            case R.id.download_url_btn:
                if (!TextUtils.isEmpty(downloadUrl.getText().toString()))

                    SystemUtils.setClipboard(this, downloadUrl.getText().toString());
                break;
            case R.id.crete_haibao:
                if (TextUtils.isEmpty(link)) return;
                dialogFragment.setLogo(CommonUtils.getUrl(images.get(mmDetailsBanner.getCurrentPosition())));
                dialogFragment.setUrl(link);
                dialogFragment.show(getSupportFragmentManager(), "HaiBaoDialog");
                break;
        }
    }
    HaiBaoDialog dialogFragment = new HaiBaoDialog();

    public static class BannerPaddingViewHolder implements MZViewHolder<String> {
        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            // 返回页面布局文件
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item_padding, null);
            mImageView = (ImageView) view.findViewById(R.id.banner_image);
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int width = dm.widthPixels;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mImageView.getLayoutParams();
//获取当前控件的布局对象
            params.width = width - dpToPx(108);
            params.height = params.width * 419 / 270;//设置当前控件布局的高度
            mImageView.setLayoutParams(params);//将设置好的布局参数应用到控件中
            return view;
        }

        @Override
        public void onBind(Context context, int position, String data) {
            // 数据绑定


            Glide.with(context)
                    .load(CommonUtils.getUrl(data))
                    .into(mImageView);


        }


    }

    @Override
    public void onPause() {
        super.onPause();
        mmDetailsBanner.pause();//暂停轮播
    }

    @Override
    public void onResume() {
        super.onResume();
        mmDetailsBanner.start();//开始轮播
    }
}
