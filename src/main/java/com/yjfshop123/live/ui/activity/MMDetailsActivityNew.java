package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.GlideImageLoader;
import com.yjfshop123.live.R;
import com.yjfshop123.live.SealAppContext;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.widget.gift.utils.ClickUtil;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.live.response.LivingListResponse;
import com.yjfshop123.live.message.ChatPresenter;
import com.yjfshop123.live.message.ConversationFactory;
import com.yjfshop123.live.message.MessageFactory;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.message.interf.ChatViewIF;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.message.ui.models.MediaMessage;
import com.yjfshop123.live.model.GuanzhuTongzhi;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.LaunchChatResponse;
import com.yjfshop123.live.net.response.UserHomeResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.fragment.MMFragment2New;
import com.yjfshop123.live.ui.widget.VoisePlayingIcon;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.IPermissions;
import com.yjfshop123.live.utils.PermissionsUtils;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.PromptPopupDialog;
import com.yjfshop123.live.ui.fragment.MMFragment;
import com.yjfshop123.live.ui.fragment.MMFragment2;
import com.yjfshop123.live.ui.fragment.MMFragment3;
import com.yjfshop123.live.ui.fragment.MMFragment4;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.jpeng.jptabbar.PagerSlidingTabStrip;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMMessage;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.AccordionTransformer;

import org.json.JSONException;
import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.yjfshop123.live.net.utils.json.JsonMananger.jsonToBean;

/**
 * 个人中心
 */

public class MMDetailsActivityNew extends BaseActivity implements
        OnBannerListener, ChatViewIF, IPermissions, ViewPager.OnPageChangeListener {

    private PagerSlidingTabStrip mSlidingTabLayout;
    private ViewPager mViewPager;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;

    private Context mContext;
    private Banner bannerView;
    LinearLayout layout_live_item_vpi_ll;
    VoisePlayingIcon layout_live_item_vpi;
    private String user_id;
    private UserHomeResponse mResponse;
    private TextView nicknameTv, nicknameTv2;
    private TextView ageTv;
    private TextView descTv, idTv, mm_details_city;
    private TextView controlsAdd;
    private TextView followNumTv;
    private CircleImageView detailsCiv;
    private ImageView darenStatusIv;
    private ImageView mmDetailsVip;
    FrameLayout mm_top_fl;
    private String mi_tencentId;
    private List<String> images;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isShow = true;
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_mm_details_new);
        setHeadVisibility(View.GONE);

        PermissionsUtils.initPermission(mContext);
        init();
        loadAnimation();
        getUserHome();
    }

    private void init() {
        mi_tencentId = UserInfoUtil.getMiTencentId();
        user_id = getIntent().getStringExtra("USER_ID");
        from = getIntent().getStringExtra("from");
        findViewById(R.id.mm_back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout_live_item_vpi_ll = findViewById(R.id.layout_live_item_vpi_ll);
        layout_live_item_vpi = findViewById(R.id.bodong);
        mm_details_city = findViewById(R.id.mm_details_city);
        bannerView = findViewById(R.id.mm_details_banner);
        nicknameTv = findViewById(R.id.mm_details_nickname_tv);
        nicknameTv2 = findViewById(R.id.mm_details_nickname_tv2);
        ageTv = findViewById(R.id.mm_details_age_tv);
        descTv = findViewById(R.id.mm_details_desc_tv);
        idTv = findViewById(R.id.mm_details_id_tv);
        followNumTv = findViewById(R.id.mm_details_be_follow_num);
        detailsCiv = findViewById(R.id.mm_details_civ);
        darenStatusIv = findViewById(R.id.mm_details_daren_status);
        mmDetailsVip = findViewById(R.id.mm_details_vip);
        mm_top_fl = findViewById(R.id.mm_top_fl);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mm_top_fl.getLayoutParams();
        //获取当前控件的布局对象
        params.topMargin = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        mm_top_fl.setLayoutParams(params);
        controlsAdd = findViewById(R.id.mm_details_controls_add);

        controlsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mResponse == null) {
                    return;
                }
                if (mResponse.getUser_info().getIs_follow() > 0) {
                    //取消关注
                    String body = "";
                    try {
                        body = new JsonBuilder()
                                .put("be_user_id", user_id)
                                .build();
                    } catch (JSONException e) {
                    }
                    OKHttpUtils.getInstance().getRequest("app/follow/cancel", body, new RequestCallback() {
                        @Override
                        public void onError(int errCode, String errInfo) {
                            NToast.shortToast(mContext, errInfo);
                        }

                        @Override
                        public void onSuccess(String result) {
                            mResponse.getUser_info().setIs_follow(0);
                            controlsAdd.setText(getString(R.string.mm_dedails_1));
                            GuanzhuTongzhi guanzhuTongzhi = new GuanzhuTongzhi();
                            guanzhuTongzhi.isGuanzhu = 0;
                            guanzhuTongzhi.user_id = mResponse.getUser_info().getUser_id();
                            EventBus.getDefault().post(guanzhuTongzhi, Config.EVENT_GUANZHU);

                        }
                    });
                } else {
                    //关注
                    String body = "";
                    try {
                        body = new JsonBuilder()
                                .put("be_user_id", user_id)
                                .build();
                    } catch (JSONException e) {
                    }
                    OKHttpUtils.getInstance().getRequest("app/follow/add", body, new RequestCallback() {
                        @Override
                        public void onError(int errCode, String errInfo) {
                            NToast.shortToast(mContext, errInfo);
                        }

                        @Override
                        public void onSuccess(String result) {
                            mResponse.getUser_info().setIs_follow(1);
                            GuanzhuTongzhi guanzhuTongzhi = new GuanzhuTongzhi();
                            guanzhuTongzhi.isGuanzhu = 1;
                            guanzhuTongzhi.user_id = mResponse.getUser_info().getUser_id();
                            EventBus.getDefault().post(guanzhuTongzhi, Config.EVENT_GUANZHU);
                            controlsAdd.setText(getString(R.string.follow));
                            NToast.shortToast(mContext, "已关注成功");
                        }
                    });
                }
            }
        });

        findViewById(R.id.mm_details_lt_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChat(0);
            }
        });

        findViewById(R.id.mm_details_yy_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }

                type_tt = 10;
                PermissionsUtils.onResume(MMDetailsActivityNew.this, MMDetailsActivityNew.this);
            }
        });

        findViewById(R.id.mm_details_sp_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }

                type_tt = 11;
                PermissionsUtils.onResume(MMDetailsActivityNew.this, MMDetailsActivityNew.this);
            }
        });

        findViewById(R.id.mm_details_gift_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChat(3);
            }
        });

        findViewById(R.id.mm_details_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMore();
            }
        });

        ((AppBarLayout) findViewById(R.id.mm_abl)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    //展开
                    nicknameTv2.setVisibility(View.GONE);
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    //折叠
                    nicknameTv2.setVisibility(View.VISIBLE);
                } else {
                    //中间状态
                    nicknameTv2.setVisibility(View.GONE);
                }
            }
        });

        mSlidingTabLayout = findViewById(R.id.mm_psts);
        mViewPager = findViewById(R.id.mm_vp);
        mContentFragments = new SparseArray<>();
        mTitles = getResources().getStringArray(R.array.mm_titles);
        titlesSize = mTitles.length;
        if (ActivityUtils.IS1V1()) {
            titlesSize = titlesSize - 1;
        }
        mViewPager.addOnPageChangeListener(this);
    }

    private String[] mTitles;
    private int titlesSize;
    private int type_tt = 10;

    @Override
    public void allPermissions() {
        if (type_tt == 10) {
            startChat(1);
        } else if (type_tt == 11) {
            startChat(2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this, this, true);
    }

    private boolean canClick() {
        return ClickUtil.canClick();
    }

    private void loadData() {
        if (mResponse == null) {
            return;
        }
        if (mResponse.getUser_info().live.live_id != 0) {
            layout_live_item_vpi_ll.setVisibility(View.VISIBLE);
            layout_live_item_vpi.start();
        } else {
            layout_live_item_vpi_ll.setVisibility(View.GONE);
        }
        chatPresenter = new ChatPresenter(this, String.valueOf(mResponse.getUser_info().getProm_custom_uid()), TIMConversationType.C2C);

        //banner
        images = new ArrayList<>();
        List<UserHomeResponse.UserInfoBean.AlbumBean> list_banner = mResponse.getUser_info().getAlbum();
        if (list_banner.size() > 0) {
            for (int i = 0; i < list_banner.size(); i++) {
                images.add(list_banner.get(i).getFull_url());
            }
            bannerView.setImages(images)
                    //.setBannerTitles(App.titles)//标
                    .setBannerAnimation(AccordionTransformer.class)//动画
                    .setIndicatorGravity(BannerConfig.CENTER)//指示器位置
                    .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)//指示器样式
                    .setDelayTime(3000)//延迟4S
                    .setImageLoader(new GlideImageLoader())
                    .setOnBannerListener(this)
                    .start();
        }
        mm_details_city.setText(mResponse.getUser_info().getCity_name());
        nicknameTv.setText(mResponse.getUser_info().getUser_nickname());
        nicknameTv2.setText(mResponse.getUser_info().getUser_nickname());
        descTv.setText(mResponse.getUser_info().getSignature());
        idTv.setText("" + mResponse.getUser_info().getUser_id());
        if (mResponse.getUser_info().getSex() == 1) {
            Drawable drawable = getResources().getDrawable(R.drawable.male_icon);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            ageTv.setCompoundDrawables(drawable, null, null, null);
            ageTv.setText(" " + mResponse.getUser_info().getAge());//♂♀
            ageTv.setEnabled(false);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.female_icon);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            ageTv.setCompoundDrawables(drawable, null, null, null);
            ageTv.setText(" " + mResponse.getUser_info().getAge());//♂♀
            ageTv.setEnabled(true);
        }

        if (mResponse.getUser_info().getIs_follow() > 0) {
            controlsAdd.setText(getString(R.string.follow));
        } else {
            controlsAdd.setText(getString(R.string.mm_dedails_1));
        }

        followNumTv.setText(" " + mResponse.getUser_info().getBe_follow_num());

        if (mResponse.getUser_info().getDaren_status() == 2) {
            darenStatusIv.setVisibility(View.VISIBLE);
        } else {
            darenStatusIv.setVisibility(View.GONE);
        }

        if (mResponse.getUser_info().getIs_vip() == 1) {
            mmDetailsVip.setVisibility(View.VISIBLE);
        } else {
            mmDetailsVip.setVisibility(View.GONE);
        }
//        RequestOptions options_1 = new RequestOptions()
//                .placeholder(R.drawable.splash_logo)
//                .error(R.drawable.splash_logo)
////                .transforms(new CenterCrop(), new CircleCrop())
////                .circleCropTransform()
//                .dontAnimate()
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        if (!TextUtils.isEmpty(mResponse.getUser_info().getAvatar()))
            Glide.with(mContext)
                    .load(mResponse.getUser_info().getAvatar())
                    .into(detailsCiv);
        detailsCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mResponse.getUser_info().live.live_id != 0) {
                    startLivePlay(mResponse.getUser_info().live.live_type,mResponse.getUser_info().live.live_id);
                } else {
                    try {
                        List<String> list = new ArrayList<>();
                        list.add(mResponse.getUser_info().getAvatar());
                        Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                        intent.putExtra(Config.POSITION, 0);
                        intent.putExtra("Picture", JsonMananger.beanToJson(list));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //
        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(fAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
        if (!TextUtils.isEmpty(from) && from.equals("dongtai")) {
            mViewPager.setCurrentItem(2);
        } else
            mViewPager.setCurrentItem(0);
    }

    private void startLivePlay(final int type, final int live_id) {


        if (type == 2) {
            DialogUitl.showSimpleInputDialog(MMDetailsActivityNew.this, "请输入房间密码", DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, 8,
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            if (TextUtils.isEmpty(content)) {
                                NToast.shortToast(MMDetailsActivityNew.this, "请输入房间密码");
                            } else {
                                startLivePlay_(type, live_id, content);
                                dialog.dismiss();
                            }
                        }
                    });
        } else if (type == 3) {
            DialogUitl.showSimpleHintDialog(MMDetailsActivityNew.this, getString(R.string.prompt), "进入该直播间将收取" + mResponse.getUser_info().live.getIn_coin() + getString(R.string.my_jinbi),
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                            startLivePlay_(type, live_id, null);
                        }
                    });
        } else {
            startLivePlay_(type, live_id, null);
        }
    }

    private void startLivePlay_(int type, int live_id, String in_password) {
        String url;
        url = "app/live/joinLive";
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("live_id", live_id)
                    .put("in_password", in_password)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                DialogUitl.showSimpleHintDialog(MMDetailsActivityNew.this, getString(R.string.prompt), errInfo,
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
                    Intent intent = new Intent(MMDetailsActivityNew.this, TCLivePlayerActivity.class);

                    if (mResponse.getUser_info().live.getLive_mode() == 2) {
                        intent.putExtra("pureAudio", true);
                    } else {
                        intent.putExtra("pureAudio", false);
                    }
                    intent.putExtra(TCConstants.ROOM_TITLE, mResponse.getUser_info().live.getTitle());
                    intent.putExtra(TCConstants.COVER_PIC, CommonUtils.getUrl(mResponse.getUser_info().live.getCover_img()));
                    intent.putExtra("LivePlay", result);
                    intent.putExtra("vod_type", mResponse.getUser_info().live.getVod_type());
                    intent.putExtra("live_id", String.valueOf(mResponse.getUser_info().live.getLive_id()));
                    intent.putExtra("zhubo_user_id", mResponse.getUser_info().getUser_id() + "");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void OnBannerClick(int position) {
        if (images.size() > 0) {
            try {
                Intent intent = new Intent(mContext, XPicturePagerActivity.class);
                intent.putExtra(Config.POSITION, position);
                intent.putExtra("Picture", JsonMananger.beanToJson(images));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            } catch (HttpException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * type 0聊天 1 语音  2 视频
     */
    private void startChat(int type) {
        if (mResponse == null) {
            return;
        }

        String platformId = UserInfoUtil.getMiPlatformId();
        int prom_custom_uid = mResponse.getUser_info().getProm_custom_uid();
        int user_id = mResponse.getUser_info().getUser_id();

        if (mi_tencentId.equals(String.valueOf(prom_custom_uid))) {
            //自己不能跟自己聊天
            NToast.shortToast(mContext, getString(R.string.not_me_chat));
            return;
        }

        if (prom_custom_uid == user_id) {
            //真人
        }

        imConversation = new IMConversation();
        imConversation.setType(0);// 0 单聊  1 群聊  2 系统消息

        imConversation.setUserIMId(mi_tencentId);
        imConversation.setUserId(platformId);

        imConversation.setOtherPartyIMId(String.valueOf(prom_custom_uid));
        imConversation.setOtherPartyId(String.valueOf(user_id));

        imConversation.setUserName(UserInfoUtil.getName());
        imConversation.setUserAvatar(UserInfoUtil.getAvatar());

        imConversation.setOtherPartyName(mResponse.getUser_info().getUser_nickname());
        imConversation.setOtherPartyAvatar(mResponse.getUser_info().getAvatar());

        imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());

        if (type == 0) {
            Intent intent = new Intent(mContext, MessageListActivity.class);
            intent.putExtra("IMConversation", imConversation);
            startActivity(intent);
            return;
        }

        if (type == 3) {
            Intent intent = new Intent(mContext, MessageListActivity.class);
            intent.putExtra("IMConversation", imConversation);
            intent.putExtra("GIFT", "GIFT");
            startActivity(intent);
            return;
        }

        user_id_v_ = prom_custom_uid;
        robot_id_ = user_id;
        if (type == 1) {
            type_v_ = 2;
            launchChat();
            return;
        }

        if (type == 2) {
            type_v_ = 3;
            launchChat();
            return;
        }
    }

    private ChatPresenter chatPresenter;
    private IMConversation imConversation;
    private int user_id_v_;
    private int robot_id_;
    private int type_v_;

    public void response_(LaunchChatResponse response) {
        //-1 无限聊天 >0剩余可聊时间
        //0 没钱了
        RoomActivity.order_no = null;
        RoomActivity.rest_time = 0;
        int rest_time = response.getRest_time();
//        if (rest_time == 0) {
//            shoeHintDialog("金蛋不足", 201);
//        } else {
        RoomActivity.order_no = response.getOrder_no();
        RoomActivity.rest_time = rest_time;
        MediaMessage imageMessage;
        //roomdbId 存入数据库,判断本次通话是否保存 正常 传时间戳 唯一
        //roomId 云通信的房间号
        //type 1 语音  2 视频
        //isHangup 默认 0 接收通话  1 挂断通话
        if (type_v_ == 2) {
            imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), response.getHome_id(),
                    1, 0, response.getSpeech_cost(), imConversation);
            chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
        } else if (type_v_ == 3) {
            imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), response.getHome_id(),
                    2, 0, response.getVideo_cost(), imConversation);
            chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
        }
        //  }

            /*RoomActivity.order_no = response.getData().getOrder_no();
            MediaMessage imageMessage;
            int home_id = response.getData().getHome_id();
            //roomdbId 存入数据库,判断本次通话是否保存 正常 传时间戳 唯一
            //roomId 云通信的房间号
            //type 1 语音  2 视频
            //isHangup 默认 0 接收通话  1 挂断通话
            if (type_v_ == 2){
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), home_id,
                        1,0, 0, imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }else if (type_v_ == 3){
                imageMessage = new MediaMessage(String.valueOf(System.currentTimeMillis()), home_id,
                        2,0, 0, imConversation);
                chatPresenter.sendMessage(imageMessage.getMessage(), null, false);
            }*/
    }

    private void shoeHintDialog(String msg, int code) {
        if (code == 201) {
            PromptPopupDialog.newInstance(this,
                    "",
                    msg,
                    getString(R.string.recharge))
                    .setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            Intent intent = new Intent(mContext, MyWalletActivity1.class);
                            intent.putExtra("currentItem", 0);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        }
                    }).setLayoutRes(R.layout.recharge_popup_dialog).show();
        } else {
            DialogUitl.showSimpleHintDialog(this, getString(R.string.prompt), msg,
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                        }
                    });
        }
    }

    @Override
    public void updateMessage(MessageDB messageDB) {

    }

    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
//        IMConversationDB imConversationDB = ConversationFactory.getMessage(message, true);
        MessageDB messageDB = MessageFactory.getMessage(message, true);

        if (messageDB != null && messageDB.getType() == 10) {
            NToast.shortToast(mContext, "发送失败 请检测您的网络~");
            return;
        }
    }

    @Override
    public void onSuccess(TIMMessage message) {
        IMConversationDB imConversationDB = ConversationFactory.getMessage(message, true);
        MessageDB messageDB = MessageFactory.getMessage(message, true);

        if (messageDB != null && messageDB.getType() == 10) {
            SealAppContext.getInstance().mediaMessage(imConversationDB, messageDB, true);
            return;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getUserHome() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("user_id", user_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/user/getUserHomeByUid", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                closeAnimation();
                DialogUitl.showSimpleHintDialog(mContext,
                        getString(R.string.prompt),
                        getString(R.string.ac_select_friend_sure),
                        getString(R.string.cancel),
                        errInfo,
                        true,
                        false,
                        new DialogUitl.SimpleCallback2() {
                            @Override
                            public void onCancelClick() {
                                finish();
                            }

                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                                finish();
                            }
                        });
            }

            @Override
            public void onSuccess(String result) {
                closeAnimation();
                try {
                    mResponse = jsonToBean(result, UserHomeResponse.class);
                    loadData();
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void launchChat() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("accept_uid", user_id_v_)
                    .put("type", type_v_)
                    .put("robot_id", robot_id_)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/chat/launchChat", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                shoeHintDialog(errInfo, errCode);
            }

            @Override
            public void onSuccess(String result) {
                try {
                    LaunchChatResponse response = JsonMananger.jsonToBean(result, LaunchChatResponse.class);
                    response_(response);
                } catch (HttpException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void dialogMore() {

        final Dialog bottomDialog = new Dialog(MMDetailsActivityNew.this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_c_video, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        contentView.findViewById(R.id.dialog_c_video_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(user_id)) {
                    String link = Const.getDomain() + "/apph5/inform/index"
                            + "?user_id=" + mi_tencentId
                            + "&be_user_id=" + user_id;
                    Intent intent = new Intent("io.xchat.intent.action.webview");
                    intent.setPackage(getPackageName());
                    intent.putExtra("url", link);
                    startActivity(intent);
                }

                bottomDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.dialog_c_video_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
                blockUser();
            }
        });

        contentView.findViewById(R.id.dialog_c_video_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });
    }

    private void blockUser() {
        DialogUitl.showSimpleHintDialog(this,
                getString(R.string.prompt),
                getString(R.string.other_ok),
                getString(R.string.other_cancel),
                "拉黑该用户？",
                true, true,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {
                    }

                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        dialog.dismiss();

                        String body = "";
                        try {
                            body = new JsonBuilder()
                                    .put("user_id", user_id)
                                    .build();
                        } catch (JSONException e) {
                        }
                        OKHttpUtils.getInstance().getRequest("app/user/blockUser", body, new RequestCallback() {
                            @Override
                            public void onError(int errCode, String errInfo) {
                                NToast.shortToast(mContext, errInfo);
                            }

                            @Override
                            public void onSuccess(String result) {
                                NToast.shortToast(mContext, "拉黑成功");
                            }
                        });

                    }
                });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            mContent = mContentFragments.get(position);

            if (position == 0) {
                if (mContent == null) {
                    mContent = new MMFragment();
                    mContentFragments.put(position, mContent);
                }
                MMFragment fragment = (MMFragment) mContentFragments.get(position);
                fragment.setResponse(mResponse, user_id);
                return fragment;
            } else if (position == 1) {
//                if (mContent == null) {
//                    mContent = new MMFragment2();
//                    mContentFragments.put(position, mContent);
//                }
//                MMFragment2 fragment = (MMFragment2) mContentFragments.get(position);
//                fragment.setResponse(mResponse);
                if (mContent == null) {
                    mContent = new MMFragment2New();
                    mContentFragments.put(position, mContent);
                }
                MMFragment2New fragment = (MMFragment2New) mContentFragments.get(position);
                fragment.setUserId(user_id);


                return fragment;
            } else if (position == 2) {
                if (mContent == null) {
                    mContent = new MMFragment3();
                    mContentFragments.put(position, mContent);
                }
                MMFragment3 fragment = (MMFragment3) mContentFragments.get(position);
                fragment.setResponse(user_id);
                return fragment;
            } else {
                if (mContent == null) {
                    mContent = new MMFragment4();
                    mContentFragments.put(position, mContent);
                }
                MMFragment4 fragment = (MMFragment4) mContentFragments.get(position);
                fragment.setResponse(user_id);
                return fragment;
            }

        }

        @Override
        public int getCount() {
            return titlesSize;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

    private View hintView;
    private AnimationDrawable frameAnimation;

    private void loadAnimation() {
        hintView = findViewById(R.id.mm_hint_ll);
        ImageView mmHintIv = findViewById(R.id.mm_hint_iv);
        frameAnimation = (AnimationDrawable) mmHintIv.getBackground();
        frameAnimation.start();
    }

    private void closeAnimation() {
        if (frameAnimation != null) {
            frameAnimation.stop();
            frameAnimation = null;
        }
        hintView.setVisibility(View.GONE);
    }
}
