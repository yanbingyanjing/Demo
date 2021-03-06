package com.yjfshop123.live.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.CommunityDoLike;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.model.EventModel;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.CommunityReplyDetailResponse;
import com.yjfshop123.live.net.response.CommunityReplyItemDetailResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.adapter.CommunityReplyDetailAdapter;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration3;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.NoScrollGridView;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.dialog.CommunityItemOptionDialog;
import com.yjfshop123.live.utils.dialog.CommunityOptionDialog;
import com.yjfshop123.live.utils.imageselecter.NineGrideShowOption;
import com.bumptech.glide.Glide;
import com.pandaq.emoticonlib.PandaEmoTranslator;

import org.json.JSONException;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jiguang.imui.chatinput.emoji.Constants;
import cn.jiguang.imui.chatinput.emoji.EmojiBean;
import cn.jiguang.imui.chatinput.emoji.EmojiView;
import cn.jiguang.imui.chatinput.emoji.EmoticonsKeyboardUtils;
import cn.jiguang.imui.chatinput.emoji.data.EmoticonEntity;
import cn.jiguang.imui.chatinput.emoji.listener.EmoticonClickListener;
import cn.jiguang.imui.chatinput.utils.SimpleCommonUtils;

/**
 * ??????:2019/2/18
 * ??????:
 **/
public class CommunityReplyDetailActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        CommunityReplyDetailAdapter.OptionClickListener, CommunityItemOptionDialog.CommunityOptionDialogClickListener,
        CommunityOptionDialog.CommunityOptionDialogClickListener, NineGrideShowOption.MyItemClickListener, EmptyLayout.OnRetryListener {

    @BindView(R.id.userHeadImg)
    CircleImageView userHeadImg;

    @BindView(R.id.userHeadName)
    TextView userHeadName;
    @BindView(R.id.dateAndAddress)
    TextView dateAndAddress;
    @BindView(R.id.guanzhuImg)
    ImageView guanzhuImg;
    @BindView(R.id.back)
    ImageView back;

    @BindView(R.id.detail_title)
    TextView detail_title;
    @BindView(R.id.detail_content)
    TextView detail_content;
    @BindView(R.id.photosList)
    NoScrollGridView photosList;

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;
//    @BindView(R.id.scrollView)
//    ScrollView scrollView;

    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;

    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout swipe_refresh;

    @BindView(R.id.community_appbarLayout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.biaoqingImg)
    ImageView biaoqingImg;
    @BindView(R.id.aurora_rl_emoji_container)
    EmojiView mEmojiRl;
    @BindView(R.id.huifuEditText)
    EditText huifuEditText;

    @BindView(R.id.inputLayout)
    LinearLayout inputLayout;
    @BindView(R.id.sendMessage)
    TextView sendMessage;
    @BindView(R.id.moreOption)
    ImageView moreOption;
    @BindView(R.id.sendGift)
    TextView sendGift;
    @BindView(R.id.huifuText1)
    TextView huifuText1;

    @BindView(R.id.huifuText)
    TextView huifuText;
    @BindView(R.id.huifuNum)
    TextView huifuNum;
    @BindView(R.id.zanNum)
    TextView zanNum;
    @BindView(R.id.sendGift1)
    LinearLayout sendGift1;

    @BindView(R.id.zanIcon)
    ImageView zanIcon;
    @BindView(R.id.circleTitle)
    TextView circleTitle;

    @BindView(R.id.communitySex)
    ImageView communitySex;
    @BindView(R.id.communityDaren)
    ImageView communityDaren;
    @BindView(R.id.communityVip)
    ImageView communityVip;

    @BindView(R.id.optionLayout)
    RelativeLayout optionLayout;
    @BindView(R.id.defalutLayout)
    RelativeLayout defalutLayout;
    @BindView(R.id.noDataLayout)
    RelativeLayout noDataLayout;

    @BindView(R.id.zanFrameLayout)
    LinearLayout zanFrameLayout;
    @BindView(R.id.pinglunLayout)
    LinearLayout pinglunLayout;

    private int dynamic_id;
    private int screenWidth;

    private boolean isLoadingMore = false;
    private int page = 1;
    private int type = 1;

    private CommunityReplyDetailResponse bean = new CommunityReplyDetailResponse();

    private ArrayList<CommunityReplyItemDetailResponse.ListBeanX> lists = new ArrayList<>();

    private LinearLayoutManager mLinearLayoutManager;

    private CommunityOptionDialog communityOptionDialog;
    private CommunityItemOptionDialog communityItemOptionDialog;

    private NineGrideShowOption nineGrideShowOption;

    private CommunityReplyDetailAdapter communityReplyDetailAdapter;

    private String userName;
    private String mUserid;
    private int sex;
    private int daren_status;
    private int vip;
    private String avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isStatusBar = true;
        isShow = true;
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_community_reply_detail);
        ButterKnife.bind(this);
        setHeadVisibility(View.GONE);
        ViewCompat.setNestedScrollingEnabled(photosList, true);

        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }

        userName = UserInfoUtil.getName();
        mUserid = UserInfoUtil.getMiTencentId();
        sex = UserInfoUtil.getSex();
        daren_status = UserInfoUtil.getDarenStatus();
        vip = UserInfoUtil.getIsVip();
        avatar = UserInfoUtil.getAvatar();
        StatusBarUtil.setStatusBarColor(this, R.color.color_FFFDF8);
        initData();
        initView();
    }

    private void initData() {

        screenWidth = CommonUtils.getScreenWidth(this);
        EventBus.getDefault().register(this);

        swipe_refresh.setRefreshing(false);
        swipe_refresh.setEnabled(false);

    }

    private void initView() {
        biaoqingImg.setOnClickListener(this);
        huifuEditText.setOnClickListener(this);
        sendMessage.setOnClickListener(this);
        moreOption.setOnClickListener(this);
        guanzhuImg.setOnClickListener(this);
        sendGift.setOnClickListener(this);
        huifuText.setOnClickListener(this);
        sendGift1.setOnClickListener(this);
        zanFrameLayout.setOnClickListener(this);
        pinglunLayout.setOnClickListener(this);
        back.setOnClickListener(this);
        userHeadImg.setOnClickListener(this);
        userHeadName.setOnClickListener(this);

        dynamic_id = getIntent().getIntExtra("dynamic_id", 0);

        shimmerRecycler.addItemDecoration(new PaddingItemDecoration3(mContext));

        mLinearLayoutManager = new LinearLayoutManager(this);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

//        swipe_refresh.setRefreshing(false);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                Log.e("zty",verticalOffset+"");
//                if (verticalOffset >= 0) {
//                    swipe_refresh.setEnabled(true);
//                } else {
//                    swipe_refresh.setEnabled(false);
//                }
            }
        });

        photosList.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mEmojiRl.setAdapter(SimpleCommonUtils.getCommonAdapter(mContext, emoticonClickListener));

        huifuEditText.addTextChangedListener(tw);

        communityOptionDialog = new CommunityOptionDialog(this);
        communityOptionDialog.setCommunityOptionDialogClickListener(this);

        communityItemOptionDialog = new CommunityItemOptionDialog(this);
        communityItemOptionDialog.setCommunityOptionDialogClickListener(this);

        page = 1;
        detailDynamic();

        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLinearLayoutManager.getItemCount();

                //????????????4???item?????????????????????????????????
                // dy>0 ??????????????????
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        replayList();
                    }
                }
            }
        });

        communityReplyDetailAdapter = new CommunityReplyDetailAdapter(this);
        shimmerRecycler.setAdapter(communityReplyDetailAdapter);
        communityReplyDetailAdapter.setOptionClickListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                hideKeyBord();
            }
        });
    }

    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().equals("")) {
                sendMessage.setClickable(false);
                sendMessage.setBackground(CommunityReplyDetailActivity.this.getResources().getDrawable(R.drawable.shape_a5a5a5_20_button));
            } else {
                sendMessage.setClickable(true);
                sendMessage.setBackground(CommunityReplyDetailActivity.this.getResources().getDrawable(R.drawable.shape_ffd100_20_button));
            }
        }
    };

    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {

            if (isDelBtn) {
                SimpleCommonUtils.delClick(huifuEditText);
            } else {
                if (o == null) {
                    return;
                }
                if (actionType == Constants.EMOTICON_CLICK_BIGIMAGE) {
                    // if(o instanceof EmoticonEntity){
                    // OnSendImage(((EmoticonEntity)o).getIconUri());
                    // }
                } else {
                    String content_ = null;
                    if (o instanceof EmojiBean) {
                        content_ = ((EmojiBean) o).emoji;
                    } else if (o instanceof EmoticonEntity) {
                        content_ = ((EmoticonEntity) o).getContent();
                    }

                    if (TextUtils.isEmpty(content_)) {
                        return;
                    }

                    /*int index = huifuEditText.getSelectionStart();
                    Editable editable = huifuEditText.getText();
                    editable.insert(index, content_);*/

                    Editable editable = huifuEditText.getText();
                    int start = huifuEditText.getSelectionStart();
                    int end = huifuEditText.getSelectionEnd();
                    start = (start < 0 ? 0 : start);
                    end = (start < 0 ? 0 : end);
                    editable.replace(start, end, content_);
                    int editEnd = huifuEditText.getSelectionEnd();
                    PandaEmoTranslator.getInstance().replaceEmoticons(editable, 0, editable.toString().length());
                    huifuEditText.setSelection(editEnd);
                }
            }
        }
    };

    private void hideKeyBord() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(rootLayout.getWindowToken(), 0);
        try {
//            ((InputMethodManager) CommunityReplyDetailActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        guanzhuImg.setFocusable(true);
        guanzhuImg.setFocusableInTouchMode(true);
        guanzhuImg.requestFocus(); // ????????????EditText?????????
        guanzhuImg.requestFocusFromTouch();
    }

    private boolean isKeyboardVisible() {
        //??????????????????????????????
        int screenHeight = this.getWindow().getDecorView().getHeight();
        //??????View???????????????bottom
        Rect rect = new Rect();
        //DecorView??????activity?????????view
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //???????????????????????????????????????????????????????????????screenHeight = rect.bottom + ????????????????????????
        //??????screenHeight*2/3????????????
        return screenHeight * 2 / 3 > rect.bottom;
    }

    private void initBiaoQing() {
        if (mEmojiRl.getVisibility() == View.VISIBLE) {
            dismissEmojiLayout();
//        } else if (isKeyboardVisible()) {//?????? ??????????????????
        } else if (false) {//?????? ??????????????????

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            EmoticonsKeyboardUtils.closeSoftKeyboard(this);
            showEmojiLayout();
//            biaoqing.setImageResource(getResources().getDrawable(R.drawable.));
        } else {
//            showMenuLayout();

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            showEmojiLayout();
        }
    }

    public void showEmojiLayout() {
        mEmojiRl.setVisibility(View.VISIBLE);
    }

    public void dismissEmojiLayout() {
        mEmojiRl.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyBord();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyBord();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(mContext, XPicturePagerActivity.class);
        intent.putExtra(Config.POSITION, i);
        try {
            intent.putExtra("Picture", JsonMananger.beanToJson(bean.getDetail().getPicture_list()));
        } catch (HttpException e) {
            e.printStackTrace();
        }
        startActivity(intent);
        dismissEmojiLayout();
        hideKeyBord();
        guanzhuImg.setFocusable(true);
        guanzhuImg.setFocusableInTouchMode(true);
        guanzhuImg.requestFocus(); // ????????????EditText?????????
        guanzhuImg.requestFocusFromTouch();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.biaoqingImg:
                huifuEditText.setFocusable(true);
                huifuEditText.setFocusableInTouchMode(true);
                huifuEditText.requestFocus();
                hideKeyBord();

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                initBiaoQing();
                break;
            case R.id.huifuEditText:
                dismissEmojiLayout();
                break;
            case R.id.moreOption:
                communityOptionDialog.show();
                break;
            case R.id.guanzhuImg:
                if (bean.getExtra().getIs_follow() == 1) {
                    startGift(false);
                } else {
                    String body = "";
                    try {
                        body = new JsonBuilder()
                                .put("be_user_id", String.valueOf(bean.getDetail().getUser_id()))
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
                            guanzhuImg.setImageResource(R.drawable.ic_person_bottom_tab_chat);
                            bean.getExtra().setIs_follow(1);
                        }
                    });
                }
                break;
            case R.id.zanFrameLayout:
                int num = bean.getDetail().getLike_num();
                if (bean.getDetail().getIs_like() == 0) {
                    zanIcon.setImageResource(R.drawable.community_item_icon_7);
                    zanNum.setTextColor(getResources().getColor(R.color.color_ffd100));
                    zanNum.setText((num + 1) + "");
                    bean.getDetail().setIs_like(1);
                    bean.getDetail().setLike_num(num + 1);
                    CommunityDoLike.getInstance().dynamicDoLike(bean.getDetail().getDynamic_id(), false);
                } else {
                    zanIcon.setImageResource(R.drawable.community_item_icon_6);
                    zanNum.setTextColor(getResources().getColor(R.color.color_cac9c9));
                    zanNum.setText((num - 1) + "");
                    bean.getDetail().setIs_like(0);
                    bean.getDetail().setLike_num(num - 1);
                    CommunityDoLike.getInstance().dynamicUndoLike(bean.getDetail().getDynamic_id(), false);
                }
//                EventBus.getDefault().post("100001");
                break;
            case R.id.huifuText:
                dismissEmojiLayout();
                optionLayout.setVisibility(View.VISIBLE);
                defalutLayout.setVisibility(View.GONE);

                huifuEditText.setFocusable(true);
                huifuEditText.setFocusableInTouchMode(true);
                huifuEditText.requestFocus();
                //???????????????
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                //?????????????????????????????????????????? ?????????????????????????????????
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(huifuEditText, 0);

                break;
            case R.id.sendGift:
            case R.id.sendGift1:
                startGift(true);
                break;
            case R.id.sendMessage:
                if (!TextUtils.isEmpty(huifuEditText.getText().toString().trim())) {
                    addReplay();
                } else {
                    NToast.shortToast(this, "????????????????????????");
                }
                break;
            case R.id.pinglunLayout:
                int hhh = mAppBarLayout.getHeight();
                handler.sendEmptyMessage(1);
                break;
            case R.id.userHeadImg:
            case R.id.userHeadName:
                ActivityUtils.startUserHome(mContext, String.valueOf(bean.getDetail().getUser_id()));
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            scrollView.smoothScrollTo(0, 400);
        }
    };

    private void startGift(boolean isGift) {
        if (bean == null) {
            return;
        }

        String mi_tencentId = UserInfoUtil.getMiTencentId();
        int prom_custom_uid = bean.getExtra().getProm_custom_uid();
        int user_id = bean.getDetail().getUser_id();

        if (mi_tencentId.equals(String.valueOf(prom_custom_uid))) {
            NToast.shortToast(mContext, "????????????????????????~");
            return;
        }

        if (prom_custom_uid == user_id) {
            //??????
        }

        IMConversation imConversation = new IMConversation();
        imConversation.setType(0);// 0 ??????  1 ??????  2 ????????????

        imConversation.setUserIMId(mi_tencentId);
        imConversation.setUserId(UserInfoUtil.getMiPlatformId());

        imConversation.setOtherPartyIMId(String.valueOf(prom_custom_uid));
        imConversation.setOtherPartyId(String.valueOf(user_id));

        imConversation.setUserName(UserInfoUtil.getName());
        imConversation.setUserAvatar(UserInfoUtil.getAvatar());

        imConversation.setOtherPartyName(bean.getDetail().getUser_nickname());
        imConversation.setOtherPartyAvatar(getUrl(bean.getDetail().getAvatar()));

        imConversation.setConversationId(imConversation.getUserId() + "@@" + imConversation.getOtherPartyId());

        Intent intent;
        intent = new Intent(mContext, MessageListActivity.class);

        intent.putExtra("IMConversation", imConversation);
        if (isGift) {
            intent.putExtra("GIFT", "GIFT");
        }
        startActivity(intent);
    }

    private String getUrl(String url) {
        if (!url.contains("http")) {
            url = bean.getExtra().getObject_domain() + url;
        }
        return url;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mEmojiRl.getVisibility() != View.GONE) {

                dismissEmojiLayout();
                return true;
            }
            if (optionLayout.getVisibility() == View.VISIBLE) {
                optionLayout.setVisibility(View.GONE);
                defalutLayout.setVisibility(View.VISIBLE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void replayList() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("dynamic_id", dynamic_id)
                    .put("type", type)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/forum/replayList4Dynamic", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                isLoadingMore = false;
                if (page == 1) {
                    noDataLayout.setVisibility(View.VISIBLE);
                    shimmerRecycler.setVisibility(View.GONE);
                }

            }

            @Override
            public void onSuccess(String result) {
                isLoadingMore = false;
                try {
                    CommunityReplyItemDetailResponse response = JsonMananger.jsonToBean(result, CommunityReplyItemDetailResponse.class);
                    if (page == 1) {
                        if (lists.size() > 0) {
                            lists.clear();
                        }
                    }

                    hideKeyBord();
                    isLoadingMore = false;
                    lists.addAll(lists.size(), response.getList());

                    if (lists.size() == 0) {
                        noDataLayout.setVisibility(View.VISIBLE);
                        shimmerRecycler.setVisibility(View.GONE);
                    } else {
                        noDataLayout.setVisibility(View.GONE);
                        shimmerRecycler.setVisibility(View.VISIBLE);
                    }
                    communityReplyDetailAdapter.setData(lists);
                    communityReplyDetailAdapter.notifyDataSetChanged();
                } catch (HttpException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void addReplay() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("dynamic_id", dynamic_id)
                    .put("parent_reply_id", 0)
                    .put("reviewed_user_id", 0)
                    .put("content", huifuEditText.getText().toString())
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/forum/addReply4Dynamic", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);

                hideKeyBord();
                dismissEmojiLayout();
            }

            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "????????????");
                CommunityReplyItemDetailResponse.ListBeanX beanX = new CommunityReplyItemDetailResponse.ListBeanX();

                beanX.setUser_nickname(userName);
                beanX.setUser_id(Integer.parseInt(mUserid));
                beanX.setSex(sex);
                beanX.setDaren_status(daren_status);
                beanX.setIs_vip(vip);
                beanX.setAvatar(avatar);
                beanX.setContent(huifuEditText.getText().toString());
                beanX.setFloor_num(lists.size() == 0 ? 1 : lists.get(lists.size() - 1).getFloor_num() + 1);
                beanX.setReply_time("??????");

                CommunityReplyItemDetailResponse.ListBeanX.ReplyListBean replyListBean = new CommunityReplyItemDetailResponse.ListBeanX.ReplyListBean();
                ArrayList<CommunityReplyItemDetailResponse.ListBeanX.ReplyListBean.ListBean> lis = new ArrayList();
                replyListBean.setTotal(0);
                replyListBean.setList(lis);
                beanX.setReply_list(replyListBean);

                lists.add(lists.size(), beanX);

                if (lists.size() != 0) {
                    noDataLayout.setVisibility(View.GONE);
                    shimmerRecycler.setVisibility(View.VISIBLE);
                } else {
                    noDataLayout.setVisibility(View.VISIBLE);
                    shimmerRecycler.setVisibility(View.GONE);
                }

                communityReplyDetailAdapter.notifyDataSetChanged();

                hideKeyBord();
                huifuEditText.setText(null);

                userHeadName.setFocusable(true);
                userHeadName.setFocusableInTouchMode(true);
                userHeadName.requestFocus(); // ????????????EditText?????????
                userHeadName.requestFocusFromTouch();
                //EventBus.getDefault().post("100001");

                hideKeyBord();
                dismissEmojiLayout();
            }
        });
    }

    private void deleteDynamic() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("dynamic_id", dynamic_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/forum/myDynamicDelete", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "????????????");
                finish();
                hideKeyBord();
            }
        });
    }

    private void detailDynamic() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("dynamic_id", dynamic_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/forum/detailDynamic", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (mEmptyLayout != null) {
                    mEmptyLayout.hide();
                }
                if (errCode == 2201) {
                    NToast.shortToast(mContext, errInfo);
                    finish();
                }
            }

            @Override
            public void onSuccess(String result) {
                if (mEmptyLayout != null) {
                    mEmptyLayout.hide();
                }
                try {
                    bean = JsonMananger.jsonToBean(result, CommunityReplyDetailResponse.class);
                    CommunityReplyDetailResponse.DetailBean detailBean = bean.getDetail();

                    if (String.valueOf(detailBean.getUser_id()).equals(mUserid)) {
                        communityOptionDialog.setDelComm();
                    }

                    nineGrideShowOption = new NineGrideShowOption(CommunityReplyDetailActivity.this, detailBean, screenWidth);
                    nineGrideShowOption.initView();
                    nineGrideShowOption.imageShort();
                    nineGrideShowOption.setOnItemClickListener(CommunityReplyDetailActivity.this);
                    RequestOptions options_1 = new RequestOptions()
                            .placeholder(R.drawable.loadding)
                            .error(R.drawable.loadding)
//                .transforms(new CenterCrop(), new CircleCrop())
//                .circleCropTransform()
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                    Glide.with(mContext).load(CommonUtils.getUrl(detailBean.getAvatar())).apply(options_1).into(userHeadImg);
                    userHeadName.setText(detailBean.getUser_nickname());
                    dateAndAddress.setText(detailBean.getCity_name());
                    if (!detailBean.getCircle_title().equals("")) {
                        circleTitle.setText("# " + detailBean.getCircle_title() + " #");
                    } else {
                        circleTitle.setVisibility(View.GONE);
                    }

                    String title = detailBean.getTitle();
                    if (TextUtils.isEmpty(title)) {
                        detail_title.setVisibility(View.GONE);
                    } else {
                        detail_title.setVisibility(View.VISIBLE);
//                            detail_title.setText(getContent(title));
                        detail_title.setText(PandaEmoTranslator
                                .getInstance()
                                .makeEmojiSpannable(title));
                    }
                    String content = detailBean.getContent();
                    if (TextUtils.isEmpty(content)) {
                        detail_content.setVisibility(View.GONE);
                    } else {
                        detail_content.setVisibility(View.VISIBLE);
//                            detail_content.setText(getContent(content));
                        detail_content.setText(PandaEmoTranslator
                                .getInstance()
                                .makeEmojiSpannable(content));
                    }

                    if (detailBean.getSex() == 1) {
                        communitySex.setImageResource(R.mipmap.boy);
                    } else if (detailBean.getSex() == 2) {
                        communitySex.setImageResource(R.mipmap.girl);
                    } else {
                        communitySex.setVisibility(View.GONE);
                    }

                    if (detailBean.getDaren_status() != 2) {
                        communityDaren.setVisibility(View.GONE);
                    }

                    if (detailBean.getIs_vip() == 0) {
                        communityVip.setVisibility(View.GONE);
                    }

                    huifuNum.setText(detailBean.getReply_num() + "");
                    zanNum.setText(detailBean.getLike_num() + "");
                    if (detailBean.getIs_like() == 1) {
                        zanIcon.setImageResource(R.drawable.community_item_icon_7);
                    }

                    if (bean.getExtra().getIs_follow() == 1) {
                        guanzhuImg.setImageResource(R.drawable.ic_person_bottom_tab_chat);
                    }

                    replayList();
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void takeLook() {//????????????
        if (type == 1 || type == 2) {
            type = 3;
            communityOptionDialog.setLookLouZhu("????????????");
        } else {
            type = 1;
            communityOptionDialog.setLookLouZhu("????????????");
        }
        lists.clear();
        page = 1;
        replayList();
        hideKeyBord();
    }

    @Override
    public void takeType() {//????????????
        String str = "";
        if (type == 2) {
            type = 1;
            str = "????????????";
        } else {
            type = 2;
            str = "????????????";
        }
        communityOptionDialog.setLookLouType(str.equals("????????????") ? "????????????" : "????????????");
        lists.clear();
        page = 1;
        communityOptionDialog.setLookLouZhu("????????????");
        replayList();
        hideKeyBord();
    }

    @Override
    public void takeJubao() {
//        NToast.shortToast(CommunityReplyDetailActivity.this, "???????????????");
        if (bean != null) {
            int be_user_id = bean.getDetail().getUser_id();
            String link = Const.getDomain() + "/apph5/inform/index"
                    + "?user_id=" + mUserid
                    + "&be_user_id=" + be_user_id;
            Intent intent = new Intent("io.xchat.intent.action.webview");
            intent.setPackage(getPackageName());
            intent.putExtra("url", link);
            startActivity(intent);
        }
    }

    @Override
    public void delComm() {
        deleteDynamic();
    }

    @Override
    public void block() {
        blockUser();
    }

    private void blockUser() {
        DialogUitl.showSimpleHintDialog(this,
                getString(R.string.prompt),
                getString(R.string.other_ok),
                getString(R.string.other_cancel),
                "??????????????????",
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
                                    .put("user_id", bean.getDetail().getUser_id())
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
                                NToast.shortToast(mContext, "????????????");
                            }
                        });

                    }
                });
    }

    private int itemOption = 0;

    @Override
    public void optionClick(int option) {
        itemOption = option;
        communityItemOptionDialog.show();
    }

    @Override
    public void headAndNameClick(View view, int option) {
        CommunityReplyItemDetailResponse.ListBeanX beanX = lists.get(option);
        ActivityUtils.startUserHome(mContext, String.valueOf(beanX.getUser_id()));
    }

    private int position = -1;

    @Override
    public void itemClick(View view, int option, int index) {

        position = option;
        CommunityReplyItemDetailResponse.ListBeanX beanX = lists.get(option);
        Intent intent = new Intent(this, CommunityReplyListActivity.class);

        if (index == -1) {

        } else {
            intent.putExtra("detail_index", index);
            intent.putExtra("preview_index", option);
        }

        dismissEmojiLayout();
        hideKeyBord();

        intent.putExtra("replyId", beanX.getReply_id());
//        intent.putExtra("dynamic_id", bean.getDetail().getDynamic_id());
        startActivity(intent);


//        guanzhuImg.setFocusable(true);
//        guanzhuImg.setFocusableInTouchMode(true);
//        guanzhuImg.requestFocus(); // ????????????EditText?????????
//        guanzhuImg.requestFocusFromTouch();
    }

    @Override
    public void itemJakeJuBao() {
//        NToast.shortToast(CommunityReplyDetailActivity.this, "???????????????");

        if (lists != null && lists.size() > itemOption) {
            int be_user_id = lists.get(itemOption).getUser_id();
            String link = Const.getDomain() + "/apph5/inform/index"
                    + "?user_id=" + mUserid
                    + "&be_user_id=" + be_user_id;
            Intent intent = new Intent("io.xchat.intent.action.webview");
            intent.setPackage(getPackageName());
            intent.putExtra("url", link);
            startActivity(intent);
        }
    }

    @Override
    public void onImgClick(View view, int index) {
        ArrayList<String> imgs = new ArrayList<>();
        List<CommunityReplyDetailResponse.DetailBean.PictureListBean> lists = bean.getDetail().getPicture_list();
        for (int i = 0; i < lists.size(); i++) {
            imgs.add(CommonUtils.getUrl(lists.get(i).getObject()));
        }
        Intent intent = new Intent(mContext, XPicturePagerActivity.class);
        intent.putExtra(Config.POSITION, index);
        try {
            intent.putExtra("Picture", JsonMananger.beanToJson(imgs));
        } catch (HttpException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = Config.EVENT_START)
    public void getEvent(EventModel model) {
        if (model.getType().equals("zan")) {
            int like = Integer.parseInt(model.getMsg());
            lists.get(position).setIs_like(like);
        } else if (model.getType().equals("huifu")) {
            if (lists.get(position).getReply_list().getTotal() <= 2) {
                lists.clear();
                page = 1;
                replayList();
            } else {
                lists.get(position).getReply_list().setTotal(lists.get(position).getReply_list().getTotal() + 1);
            }
        }
        communityReplyDetailAdapter.notifyDataSetChanged();
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

    @Override
    public void onRetry() {
        page = 1;
        detailDynamic();

        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }
    }
}
