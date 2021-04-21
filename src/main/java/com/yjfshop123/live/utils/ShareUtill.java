package com.yjfshop123.live.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.sh.sdk.shareinstall.ShareInstall;
import com.tencent.qcloud.core.util.Base64Utils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.App;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.live.response.LivingListResponse;
import com.yjfshop123.live.model.KoulingData;
import com.yjfshop123.live.model.KoulingJiexiData;
import com.yjfshop123.live.model.ShareData;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.VideoDynamicResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.GSYVideoActivity;
import com.yjfshop123.live.ui.activity.PhoneRegisterActivity;
import com.yjfshop123.live.video.fragment.SmallVideoFragmentNew;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.yjfshop123.live.App.shareData;

public class ShareUtill {
    public static void checkIsShowShare(final Activity context) {
        //        ActivityUtils.startGSYVideo(mContext, 2, String.valueOf(mList.get(position).getDynamic_id()), "app/shortVideo/getVideoById");
//        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

        if (shareData != null) {
            NLog.e("TAGTAG", "ShareInstall_info = " + new Gson().toJson(shareData));
            try {
                // JSONObject jso = new JSONObject(info);
                // String jump=jso.getString("jump");
                if (TextUtils.isEmpty(shareData.time)) return;
                if (!TextUtils.isEmpty(UserInfoUtil.getShowShareId()) && UserInfoUtil.getShowShareId().equals(shareData.time))
                    return;
                UserInfoUtil.setShowShareId(shareData.time);
                if (shareData.type.equals("video")) {
                    showVideo(context, shareData.dynamic_id);
                } else if (shareData.type.equals("live")) {
                    showLive(context, shareData.dynamic_id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //此处可放 调用获取剪切板内容的代码
                    try {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                        //无数据时直接返回
                        if (!clipboard.hasPrimaryClip()) {
                            return;
                        }
                        if (clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            ClipData cdText = clipboard.getPrimaryClip();
                            ClipData.Item item = cdText.getItemAt(0);
                            if (item.getIntent() != null && !TextUtils.isEmpty(item.getIntent().getStringExtra("type")) && item.getIntent().getStringExtra("type").equals("from_dgj")) {
                                //此处说明是从大公鸡内部复制的口令不需要处理
                                return;
                            }
                            //此处是TEXT文本信息
                            if (item.getText() != null) {
                                String str = item.getText().toString();
                                if (TextUtils.isEmpty(str)) return;
                                koulingshareJiexi(context, str);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, 300);//1秒后执行Runnable中的run方法


        }
    }

    public static void koulingshareJiexi(final Activity context, String kouling) {
        String body = "";

        try {
            body = new JsonBuilder()
                    .put("text", kouling)
                    .build();
        } catch (JSONException e) {
        }
//        ActivityUtils.startGSYVideo(mContext, 2, String.valueOf(mList.get(position).getDynamic_id()), "app/shortVideo/getVideoById");
//        getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
        OKHttpUtils.getInstance().getRequest("app/share/decodeKouling", body, new com.yjfshop123.live.Interface.RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess(String result) {

                if (result == null) {
                    return;
                }
                NLog.d("homeActivity", result);
                try {
                    //清空粘贴板
                    ClipboardManager clipBoard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                    ClipData data = ClipData.newPlainText("", "");
                    clipBoard.setPrimaryClip(data);

                    KoulingJiexiData shareData = new Gson().fromJson(result, KoulingJiexiData.class);
                    if (shareData.content != null && !TextUtils.isEmpty(shareData.content.type)) {
                        if (shareData.content.type.equals("video")) {
                            showVideo(context, shareData.content.dynamic_id);
                        } else if (shareData.content.type.equals("live")) {
                            showLive(context, shareData.content.dynamic_id);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void showLive(final Activity context, final String dynamic_id) {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", 1)
                    .put("channel_id", 0)//频道ID
                    .put("scene_id", 1)//场景id，可不填（1:直播首页调用 2:连麦中调用）
                    .put("offset_id", dynamic_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/live/getLivingList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    try {
                        final LivingListResponse launchLiveResponse = JsonMananger.jsonToBean(result, LivingListResponse.class);
                        if (launchLiveResponse.getLive_list() != null && launchLiveResponse.getLive_list().size() > 0) {
                            if (launchLiveResponse.getLive_list().get(0).getLive_id() != Integer.valueOf(dynamic_id)) {
                                return;
                            }
                            //短视频分享
//                            new XPopup.Builder(context).asConfirm("发现一个有趣的直播间", launchLiveResponse.getLive_list().get(0).getTitle(),
//                                    new OnConfirmListener() {
//                                        @Override
//                                        public void onConfirm() {
                            startLivePlay(context, launchLiveResponse.getLive_list().get(0));
//                                        }
//                                    })
//                                    .show();
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    private static void showVideo(final Activity context, final String dynamic_id) {
        String body = "";
        try {

            body = new JsonBuilder()
                    .put("dynamic_id", dynamic_id)
                    .put("page", 1)
                    .build();

        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/shortVideo/getVideoById", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                try {
                    VideoDynamicResponse response = JsonMananger.jsonToBean(result, VideoDynamicResponse.class);

                    if (response.getList() != null && response.getList().size() > 0) {
                        //短视频分享
//                        new XPopup.Builder(context).asConfirm("发现有趣的视频啦", response.getList().get(0).getContent(),
//                                new OnConfirmListener() {
//                                    @Override
//                                    public void onConfirm() {
                        ActivityUtils.startGSYVideo(context, 2, dynamic_id, "app/shortVideo/getVideoById");
                        context.overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
//                                    }
//                                })
//                                .show();
                    }


                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void startLivePlay(final Activity mContext,
                                      final LivingListResponse.LiveListBean item) {
        if (item.getLive_id() == 0) {
            ActivityUtils.startUserHome(mContext, String.valueOf(item.getUser_id()));
            mContext.overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            return;
        }

        if (item.getType() == 2) {
            DialogUitl.showSimpleInputDialog(mContext, "请输入房间密码", DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, 8,
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            if (TextUtils.isEmpty(content)) {
                                NToast.shortToast(mContext, "请输入房间密码");
                            } else {
                                startLivePlay_(mContext, item, content);
                                dialog.dismiss();
                            }
                        }
                    });
        } else if (item.getType() == 3) {
            DialogUitl.showSimpleHintDialog(mContext, mContext.getString(R.string.prompt), "进入该直播间将收取" + item.getIn_coin() + mContext.getString(R.string.my_jinbi),
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                            startLivePlay_(mContext, item, null);
                        }
                    });
        } else {
            startLivePlay_(mContext, item, null);
        }
    }

    private static void startLivePlay_(final Activity mContext,
                                       final LivingListResponse.LiveListBean item, String in_password) {
        String url;
        if (item.getVod_type() == 2) {
            url = "app/live/joinLive4Lubo";
        } else {
            url = "app/live/joinLive";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("live_id", item.getLive_id())
                    .put("in_password", in_password)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                DialogUitl.showSimpleHintDialog(mContext, mContext.getString(R.string.prompt), errInfo,
                        true, new DialogUitl.SimpleCallback() {
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                            }
                        });
            }

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Intent intent = new Intent(mContext, TCLivePlayerActivity.class);

                    if (item.getLive_mode() == 2) {
                        intent.putExtra("pureAudio", true);
                    } else {
                        intent.putExtra("pureAudio", false);
                    }
                    intent.putExtra(TCConstants.ROOM_TITLE, item.getTitle());
                    intent.putExtra(TCConstants.COVER_PIC, CommonUtils.getUrl(item.getCover_img()));
                    intent.putExtra("LivePlay", result);
                    intent.putExtra("vod_type", item.getVod_type());
                    intent.putExtra("live_id", String.valueOf(item.getLive_id()));


                    intent.putExtra("zhubo_user_id", item.getUser_id() + "");
                    mContext.startActivity(intent);
                    mContext.overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                }
            }
        });
    }

}
