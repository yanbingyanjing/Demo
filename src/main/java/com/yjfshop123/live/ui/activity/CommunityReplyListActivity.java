package com.yjfshop123.live.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.CommunityDoLike;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.model.EventModel;
import com.yjfshop123.live.net.Config;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.CommunityReplyListResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.adapter.CommunityReplyListAdapter;
import com.yjfshop123.live.ui.widget.shimmer.EmptyLayout;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration3;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.yjfshop123.live.utils.dialog.CommunityItemOptionDialog;
import com.bumptech.glide.Glide;
import com.pandaq.emoticonlib.PandaEmoTranslator;

import org.json.JSONException;
import org.simple.eventbus.EventBus;

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
 *
 * 日期:2019/2/21
 * 描述:
 **/
public class CommunityReplyListActivity extends BaseActivity implements View.OnClickListener, CommunityItemOptionDialog.CommunityOptionDialogClickListener, CommunityReplyListAdapter.TextOnClickListener,EmptyLayout.OnRetryListener {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.moreOption)
    ImageView moreOption;
    @BindView(R.id.titleContent)
    TextView titleContent;

    @BindView(R.id.userHeadImg)
    CircleImageView userHeadImg;
    @BindView(R.id.userHeadName)
    TextView userHeadName;
    @BindView(R.id.contextText)
    TextView contextText;
    @BindView(R.id.communitySex)
    ImageView communitySex;
    @BindView(R.id.communityDaren)
    ImageView communityDaren;
    @BindView(R.id.communityVip)
    ImageView communityVip;

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
    @BindView(R.id.publishTime)
    TextView publishTime;
    @BindView(R.id.zanLayout)
    RelativeLayout zanLayout;
    @BindView(R.id.zanTxt)
    TextView zanTxt;
    @BindView(R.id.zanImg)
    ImageView zanImg;
    @BindView(R.id.huifuLayout)
    RelativeLayout huifuLayout;
    @BindView(R.id.noDataLayout)
    RelativeLayout noDataLayout;

    @BindView(R.id.inputLayout)
    LinearLayout inputLayout;
    @BindView(R.id.biaoqingImg)
    ImageView biaoqingImg;
    @BindView(R.id.aurora_rl_emoji_container)
    EmojiView mEmojiRl;
    @BindView(R.id.huifuEditText)
    EditText huifuEditText;

    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;

    @BindView(R.id.sendMessage)
    TextView sendMessage;

    private int preview_index;
    private int detail_index;

    private LinearLayoutManager mLinearLayoutManager;

    private CommunityReplyListResponse.DetailBean beanX;
    private List<CommunityReplyListResponse.ReplyListBean> itemList = new ArrayList<>();
    private int dynamic_id;//动态id
    private int replyId;
    private int parent_reply_id;//回复人id
    private int reviewed_user_id;//被回复人ID

    private CommunityItemOptionDialog communityItemOptionDialog;

    private CommunityReplyListAdapter adapter;
    private int page = 1;
    private boolean isLoadingMore = false;

    private String mi_tencentId;
    private int mType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isStatusBar = true;
        isShow = true;
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_community_reply_list);
        ButterKnife.bind(this);
        setHeadVisibility(View.GONE);

        mi_tencentId = UserInfoUtil.getMiTencentId();

        initView();
        setData();
    }


    private void setData() {
//        dynamic_id = getIntent().getIntExtra("dynamic_id", -1);
        replyId = getIntent().getIntExtra("replyId", -1);

        preview_index = getIntent().getIntExtra("preview_index", -1);
        detail_index = getIntent().getIntExtra("detail_index", -1);
        mType = getIntent().getIntExtra("TYPE", 1);

        replayList4Reply();
    }

    private void initView() {
        mEmojiRl.setAdapter(SimpleCommonUtils.getCommonAdapter(mContext, emoticonClickListener));
        huifuEditText.addTextChangedListener(tw);

        biaoqingImg.setOnClickListener(this);
        huifuEditText.setOnClickListener(this);
        zanLayout.setOnClickListener(this);
        back.setOnClickListener(this);
        moreOption.setOnClickListener(this);
        sendMessage.setOnClickListener(this);
        userHeadImg.setOnClickListener(this);
        userHeadName.setOnClickListener(this);

        shimmerRecycler.addItemDecoration(new PaddingItemDecoration3(mContext));

        mLinearLayoutManager = new LinearLayoutManager(this);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        communityItemOptionDialog = new CommunityItemOptionDialog(this);
        communityItemOptionDialog.setCommunityOptionDialogClickListener(this);


        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLinearLayoutManager.getItemCount();

                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        replayList4Reply();
                    }
                }
            }
        });

        adapter = new CommunityReplyListAdapter(this);
        shimmerRecycler.setAdapter(adapter);
        adapter.setTextOnClickListener(this);

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
                sendMessage.setBackground(CommunityReplyListActivity.this.getResources().getDrawable(R.drawable.shape_a5a5a5_20_button));
            } else {
                sendMessage.setClickable(true);
                sendMessage.setBackground(CommunityReplyListActivity.this.getResources().getDrawable(R.drawable.shape_ffd100_20_button));
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

    @Override
    public void onHeadLeftButtonClick(View v) {
        super.onHeadLeftButtonClick(v);
        hideKeyBord();
    }

    private void hideKeyBord() {
        try {
            ((InputMethodManager) CommunityReplyListActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).

                    hideSoftInputFromWindow(huifuEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initBiaoQing() {
        if (mEmojiRl.getVisibility() == View.VISIBLE) {
            dismissEmojiLayout();
//        } else if (isKeyboardVisible()) {//判断 键盘是否打开 TODO
        } else if (false) {//判断 键盘是否打开 TODO
            EmoticonsKeyboardUtils.closeSoftKeyboard(this);
            showEmojiLayout();
//            biaoqing.setImageResource(getResources().getDrawable(R.drawable.));
        } else {
//            showMenuLayout();
            showEmojiLayout();
        }
    }

    public void showEmojiLayout() {
        mEmojiRl.setVisibility(View.VISIBLE);
    }

    public void dismissEmojiLayout() {
        mEmojiRl.setVisibility(View.GONE);
    }

    private void addReplay(){
        String url = "";
        if (mType == 1){
            url = "app/forum/addReply4Dynamic";
        }else if (mType == 2){
            url = "app/shortvideo/addReply4Dynamic";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("dynamic_id", dynamic_id)
                    .put("parent_reply_id", parent_reply_id)
                    .put("reviewed_user_id", reviewed_user_id)
                    .put("content", huifuEditText.getText().toString())
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
                hideKeyBord();
                dismissEmojiLayout();
            }
            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "评论成功");

                replayList4Reply();
                huifuEditText.setText(null);
                EventModel em = new EventModel();
                em.setType("huifu");
                em.setMsg("sss");

                huifuEditText.clearFocus();
                huifuEditText.setFocusable(false);

//                        userHeadName.setFocusable(true);
//                        userHeadName.setFocusableInTouchMode(true);
//                        userHeadName.requestFocus(); // 初始不让EditText得焦点
//                        userHeadName.requestFocusFromTouch();

                dismissEmojiLayout();
                hideKeyBord();

                EventBus.getDefault().post(em,Config.EVENT_START);
            }
        });
    }

    private void replayList4Reply(){
        String url = "";
        if (mType == 1){
            url = "app/forum/replayList4Reply";
        }else if (mType == 2){
            url = "app/shortvideo/replayList4Reply";
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("reply_id", replyId)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (mEmptyLayout != null) {
                    mEmptyLayout.hide();
                }
                if (errCode == 2202) {
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
                    response = JsonMananger.jsonToBean(result, CommunityReplyListResponse.class);
                    if (page == 1) {
                        beanX = response.getDetail();
                        dynamic_id = beanX.getDynamic_id();
                        parent_reply_id = beanX.getReply_id();
                        Glide.with(mContext).load(CommonUtils.getUrl(beanX.getAvatar())).into(userHeadImg);
                        userHeadName.setText(beanX.getUser_nickname());
                        contextText.setText(PandaEmoTranslator
                                .getInstance()
                                .makeEmojiSpannable(beanX.getContent()));
                        titleContent.setText(beanX.getFloor_num() + "F");
                        publishTime.setText(beanX.getFloor_num() + "F " + beanX.getReply_time());
                        zanTxt.setText(beanX.getLike_num() + "");

                        if (beanX.getSex() == 1) {
                            communitySex.setImageResource(R.mipmap.boy);
                        } else if (beanX.getSex() == 2) {
                            communitySex.setImageResource(R.mipmap.girl);
                        } else {
                            communitySex.setVisibility(View.GONE);
                        }

                        if (beanX.getDaren_status() != 2) {
                            communityDaren.setVisibility(View.GONE);
                        }

                        if (beanX.getIs_vip() == 0) {
                            communityVip.setVisibility(View.GONE);
                        }

                        if (beanX.getIs_like() == 1) {
                            zanTxt.setTextColor(getResources().getColor(R.color.color_ffd100));
                            zanImg.setImageResource(R.drawable.community_item_icon_7);
                        }
                    }
                    for (int i = 0; i < response.getReply_list().size(); i++) {
                        if (!itemList.contains(response.getReply_list().get(i))) {
                            itemList.add(response.getReply_list().get(i));
                        }
                    }
//                        itemList.addAll(itemList.size(), response.getReply_list());
//                    }else{
//                        for(int i=0;i<response.getReply_list().size();i++){
//                            if(!itemList.contains(response.getReply_list().get(i))){
//                                itemList.add(response.getReply_list().get(i));
//                            }
//                        }
//                    }
                    isLoadingMore = false;
                    if (itemList.size() > 0) {
//                        adapter.notifyDataSetChanged();
                        huifuLayout.setVisibility(View.VISIBLE);
                        noDataLayout.setVisibility(View.GONE);
                        setInitHideTxt(detail_index);
//                        if (detail_index != -1) {
//                            itemList.get(detail_index).getUser_nickname();
//                    }


                    } else {
                        huifuLayout.setVisibility(View.GONE);
                        noDataLayout.setVisibility(View.VISIBLE);
                    }
                    adapter.setData(itemList);
                    adapter.notifyDataSetChanged();
//                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private CommunityReplyListResponse response;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.biaoqingImg:
                hideKeyBord();

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                initBiaoQing();
                huifuEditText.setFocusable(true);
                huifuEditText.setFocusableInTouchMode(true);
                huifuEditText.requestFocus();
                break;
            case R.id.huifuEditText:
                dismissEmojiLayout();

                huifuEditText.setFocusable(true);
                huifuEditText.setFocusableInTouchMode(true);
                huifuEditText.requestFocus();
                //显示软键盘
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                //如果上面的代码没有弹出软键盘 可以使用下面另一种方式
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(huifuEditText, 0);

                break;
            case R.id.zanLayout:
                EventModel em = new EventModel();
                em.setType("zan");
                if (beanX.getIs_like() == 0) {
                    beanX.setIs_like(1);
                    zanTxt.setTextColor(getResources().getColor(R.color.color_ffd100));
                    zanImg.setImageResource(R.drawable.community_item_icon_7);
                    em.setMsg("1");
                    EventBus.getDefault().post(em,Config.EVENT_START);
                    beanX.setLike_num(beanX.getLike_num() + 1);
                    if (mType == 2){
                        CommunityDoLike.getInstance().shortVideoDoLike(beanX.getReply_id(), true);
                    }else {
                        CommunityDoLike.getInstance().dynamicDoLike(beanX.getReply_id(), true);
                    }
                } else {
                    beanX.setIs_like(0);
                    zanTxt.setTextColor(getResources().getColor(R.color.color_636363));
                    zanImg.setImageResource(R.drawable.community_item_icon_6);
                    em.setMsg("0");
                    EventBus.getDefault().post(em,Config.EVENT_START);
                    beanX.setLike_num(beanX.getLike_num() - 1);
                    if (mType == 2){
                        CommunityDoLike.getInstance().shortVideoUndoLike(beanX.getReply_id(), true);
                    }else {
                        CommunityDoLike.getInstance().dynamicUndoLike(beanX.getReply_id(), true);
                    }
                }
                zanTxt.setText(beanX.getLike_num() + "");
                break;
            case R.id.moreOption:
                communityItemOptionDialog.show();
                break;
            case R.id.sendMessage:
                if (!TextUtils.isEmpty(huifuEditText.getText().toString().trim())) {
                    addReplay();
                } else {
                    NToast.shortToast(this, "评论内容不能为空");
                }
                break;
            case R.id.userHeadImg:
            case R.id.userHeadName:
                ActivityUtils.startUserHome(mContext, String.valueOf(beanX.getUser_id()));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mEmojiRl.getVisibility() != View.GONE) {
                dismissEmojiLayout();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setInitHideTxt(int position) {
        if (position == -1) {
            return;
        }
        CommunityReplyListResponse.ReplyListBean bean = itemList.get(position);
        huifuEditText.setHint("回复@" + bean.getUser_nickname() + ":");
//        dynamic_id = bean.getDynamic_id();
        reviewed_user_id = bean.getUser_id();

//        huifuEditText.setFocusable(true);
//        huifuEditText.setFocusableInTouchMode(true);
//        huifuEditText.requestFocus();
//        //显示软键盘
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        //如果上面的代码没有弹出软键盘 可以使用下面另一种方式
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(huifuEditText, 0);
    }

    @Override
    public void onTextClick(View view, int position) {
        setInitHideTxt(position);
    }

    @Override
    public void itemJakeJuBao() {
//        NToast.shortToast(CommunityReplyListActivity.this, "上报成功～");
        if (response != null){
            int be_user_id = response.getDetail().getUser_id();
            String link = Const.getDomain() + "/apph5/inform/index"
                    + "?user_id=" + mi_tencentId
                    + "&be_user_id=" + be_user_id;
            Intent intent = new Intent("io.xchat.intent.action.webview");
            intent.setPackage(getPackageName());
            intent.putExtra("url", link);
            startActivity(intent);
        }
    }

    @Override
    public void onRetry() {
        page = 1;
        replayList4Reply();

        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyStatus(EmptyLayout.STATUS_LOADING);
        }
    }
}
