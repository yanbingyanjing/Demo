package com.yjfshop123.live.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.UmengText;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.KoulingData;
import com.yjfshop123.live.model.ShareData;
import com.yjfshop123.live.net.response.VideoDynamicResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.socket.SocketHandler;
import com.yjfshop123.live.socket.SocketUtil;
import com.yjfshop123.live.video.fragment.SmallVideoFragmentNew;

import org.json.JSONException;

public class VideoShare {
    private VideoShare() {
    }

    protected static VideoShare sInstance;

    public static VideoShare getInstance() {
        synchronized (SocketUtil.class) {
            if (sInstance == null) {
                sInstance = new VideoShare();
            }
            return sInstance;
        }
    }

    VideoDynamicResponse.ListBean data;
    AlertDialog mDialog;
    Activity activity;

    public void showShareDialog(Activity activity, VideoDynamicResponse.ListBean data) {
        this.activity = activity;
        this.data = data;
        View view = activity.getLayoutInflater().inflate(R.layout.share_dialog2, null);
        mDialog = new AlertDialog.Builder(activity, R.style.BottomDialog2).create();
        mDialog.show();

        Window window = mDialog.getWindow();
        if (window != null) {
            window.setContentView(view);
            window.setWindowAnimations(R.style.BottomDialog_Animation);
            WindowManager.LayoutParams mParams = window.getAttributes();
            mParams.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
            mParams.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            window.setGravity(Gravity.BOTTOM);
            window.setAttributes(mParams);
        }

        TextView btn_wx = view.findViewById(R.id.btn_share_wx);
        TextView btn_circle = view.findViewById(R.id.btn_share_circle);
        TextView btn_qq = view.findViewById(R.id.btn_share_qq);
        TextView btn_qzone = view.findViewById(R.id.btn_share_qzone);
        TextView btn_wb = view.findViewById(R.id.btn_share_wb);
        TextView btn_cancel = view.findViewById(R.id.btn_share_cancle);
        TextView btn_share_kouling = view.findViewById(R.id.btn_share_kouling);
        btn_wx.setOnClickListener(mShareBtnClickListen);

        btn_circle.setOnClickListener(mShareBtnClickListen);
        btn_qq.setOnClickListener(mShareBtnClickListen);
        btn_qzone.setOnClickListener(mShareBtnClickListen);
        btn_wb.setOnClickListener(mShareBtnClickListen);
        btn_share_kouling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                koulingshare();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }

    private void koulingshare() {
        String body = "";
        LoadDialog.show(activity);
        try {
            body = new JsonBuilder()
                    .put("title", "大公鸡直播视商")
                    .put("type", "video")
                    .put("dynamic_id", data.getDynamic_id() + "")
                    .build();
        } catch (JSONException e) {
        }
//        ActivityUtils.startGSYVideo(mContext, 2, String.valueOf(mList.get(position).getDynamic_id()), "app/shortVideo/getVideoById");
//        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
        OKHttpUtils.getInstance().getRequest("app/share/kouling", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(activity, errInfo);
                LoadDialog.dismiss(activity);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(activity);
                if (result == null) {
                    return;
                }
                try {
                    KoulingData urlData = new Gson().fromJson(result,KoulingData.class);
                    SystemUtils.setClipboardForKouling(activity,urlData.text);
                    Intent share_intent = new Intent();
                    share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                    share_intent.setType("text/plain");  //设置分享内容的类型
                    share_intent.putExtra(Intent.EXTRA_TEXT, urlData.text);//分享出去的内容
                    activity.startActivity( Intent.createChooser(share_intent, "大公鸡直播视商"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private View.OnClickListener mShareBtnClickListen = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_share_wx:
                    mShare_meidia = SHARE_MEDIA.WEIXIN;
                    break;
                case R.id.btn_share_circle:
                    mShare_meidia = SHARE_MEDIA.WEIXIN_CIRCLE;
                    break;
                case R.id.btn_share_qq:
                    mShare_meidia = SHARE_MEDIA.QQ;
                    break;
                case R.id.btn_share_qzone:
                    mShare_meidia = SHARE_MEDIA.QZONE;
                    break;
                case R.id.btn_share_wb:
                    mShare_meidia = SHARE_MEDIA.SINA;
                    break;
                default:
                    break;
            }

            share();
        }
    };

    private void share() {
        String body = "";
        LoadDialog.show(activity);
        ShareData shareData = new ShareData();
        shareData.type = "video";
        shareData.dynamic_id = data.getDynamic_id() + "";
        shareData.avatar = data.getVideo_list().get(0).getCover_img();
        shareData.content = data.getContent();
        try {
            body = new JsonBuilder()
                    .put("jump", 1)
                    .put("type", "video")
                    .put("dynamic_id", shareData.dynamic_id)
                    .build();
        } catch (JSONException e) {
        }
//        ActivityUtils.startGSYVideo(mContext, 2, String.valueOf(mList.get(position).getDynamic_id()), "app/shortVideo/getVideoById");
//        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
        OKHttpUtils.getInstance().getRequest("app/share/index", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(activity, errInfo);
                LoadDialog.dismiss(activity);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(activity);
                if (result == null) {

                    return;
                }
                try {
                    SmallVideoFragmentNew.UrlData urlData = new Gson().fromJson(result, SmallVideoFragmentNew.UrlData.class);
                    startShare(UserInfoUtil.getName(), urlData.url, data.getVideo_list().get(0).getCover_img(), data.getContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private SHARE_MEDIA mShare_meidia = SHARE_MEDIA.WEIXIN;

    private void startShare(String title, String link, String icon_link, String desc) {
        ShareAction shareAction = new ShareAction(activity);
        String shareUrl = link;
        UMWeb web = new UMWeb(shareUrl);
        web.setThumb(new UMImage(activity, icon_link));
        web.setTitle(title);
        web.setDescription(desc);
//        shareAction.withText(desc);
        shareAction.withMedia(web);
        shareAction.setCallback(umShareListener);
        shareAction.setPlatform(mShare_meidia).share();
    }

    private UMShareListener umShareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA platform) {
            // LoadDialog.show(getContext());
            //   NToast.shortToast(mContext, platform + " onStart");
            if (mDialog != null) mDialog.dismiss();
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            //NToast.shortToast(mContext, platform + " 分享成功啦");
            if (mDialog != null) mDialog.dismiss();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            NToast.shortToast(mContext, "分享失败");
            if (mDialog != null) mDialog.dismiss();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            NToast.shortToast(mContext, "分享取消了");
            if (mDialog != null) mDialog.dismiss();
        }
    };

}
