package com.yjfshop123.live.ui.fragment;

import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.live.common.widget.gift.adapter.LiveGiftCountAdapter;
import com.yjfshop123.live.live.live.common.widget.gift.adapter.LiveGiftPagerAdapter;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveGiftBean;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveReceiveGiftBean;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveReceiveGiftBeanItem;
import com.yjfshop123.live.live.live.common.widget.gift.utils.GifCacheUtil;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.response.GiftListResp;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.CommonCallback;
import com.yjfshop123.live.ui.activity.RoomActivity;
import com.yjfshop123.live.utils.CommonUtils;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RoomGiftDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener<String>, LiveGiftPagerAdapter.ActionListener {

    private String restCoin;

    private TextView mCoin;
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private View mLoading;
    private View mArrow;
    private View mBtnSend;
    private View mBtnSendGroup;
    private View mBtnSendLian;
    private TextView mBtnChooseCount;
    private PopupWindow mGiftCountPopupWindow;//选择分组数量的popupWindow
    private Drawable mDrawable1;
    private Drawable mDrawable2;
    private LiveGiftPagerAdapter mLiveGiftPagerAdapter;
    private LiveGiftBean mLiveGiftBean;
    private static final String DEFAULT_COUNT = "1";
    private String mCount = DEFAULT_COUNT;
    private Handler mHandler;
    private int mLianCountDownCount;//连送倒计时的数字
    private TextView mLianText;
    private static final int WHAT_LIAN = 100;
    private boolean mShowLianBtn;//是否显示了连送按钮

    private String mLiveUid;//主播ID
    private String mIsGuard = "0";// 1 开通了守护 0未开启

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_gift;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLiveUid = bundle.getString("liveUid");
            mIsGuard = bundle.getString("isGuard");
        }

        mCoin = (TextView) mRootView.findViewById(R.id.coin);
        mLoading = mRootView.findViewById(R.id.loading);
        mArrow = mRootView.findViewById(R.id.arrow);
        mBtnSend = mRootView.findViewById(R.id.btn_send);
        mBtnSendGroup = mRootView.findViewById(R.id.btn_send_group);
        mBtnSendLian = mRootView.findViewById(R.id.btn_send_lian);
        mBtnChooseCount = (TextView) mRootView.findViewById(R.id.btn_choose);
        mLianText = (TextView) mRootView.findViewById(R.id.lian_text);
        mDrawable1 = ContextCompat.getDrawable(mContext, R.drawable.bg_live_gift_send);
        mDrawable2 = ContextCompat.getDrawable(mContext, R.drawable.bg_live_gift_send_2);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mRadioGroup != null) {
                    ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.radio_group);
        mBtnSend.setOnClickListener(this);
        mBtnSendLian.setOnClickListener(this);
        mBtnChooseCount.setOnClickListener(this);
        mCoin.setOnClickListener(this);
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mLianCountDownCount--;
                if (mLianCountDownCount == 0) {
                    hideLianBtn();
                } else {
                    if (mLianText != null) {
                        mLianText.setText(mLianCountDownCount + "s");
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
                        }
                    }
                }
            }
        };
        loadData();
    }

    private void loadData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("type", "2")//场景类型 1:社区聊天场景 2:直播场景
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/gift/getGiftList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                try{
                    GiftListResp giftListResp = JsonMananger.jsonToBean(result, GiftListResp.class);
                    restCoin = giftListResp.getRest_coin();
                    mCoin.setText(String.valueOf(restCoin));

                    List<LiveGiftBean> list = new ArrayList<>();
                    for (int i = 0; i < giftListResp.getList().size(); i++) {
                        LiveGiftBean liveGiftBean = new LiveGiftBean();
                        liveGiftBean.setGift_uni_code(giftListResp.getList().get(i).getGift_uni_code());
                        liveGiftBean.setName(giftListResp.getList().get(i).getName());
                        liveGiftBean.setIcon_img(giftListResp.getList().get(i).getIcon_img());
                        liveGiftBean.setEffect_img(giftListResp.getList().get(i).getEffect_img());
                        liveGiftBean.setStyle(giftListResp.getList().get(i).getStyle());
                        liveGiftBean.setCh_cat_id(giftListResp.getList().get(i).getCh_cat_id());
                        liveGiftBean.setCoin(giftListResp.getList().get(i).getCoin());
                        list.add(liveGiftBean);
                    }
                    showGiftList(list);

                    if (mLoading != null) {
                        mLoading.setVisibility(View.INVISIBLE);
                    }
                }catch (Exception e){
                }
            }
        });
    }

    private String getJson(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = mContext.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void showGiftList(List<LiveGiftBean> list) {
        mLiveGiftPagerAdapter = new LiveGiftPagerAdapter(mContext, list);
        mLiveGiftPagerAdapter.setActionListener(this);
        mViewPager.setAdapter(mLiveGiftPagerAdapter);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0, size = mLiveGiftPagerAdapter.getCount(); i < size; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_gift_indicator, mRadioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            mRadioGroup.addView(radioButton);
        }
    }


    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mGiftCountPopupWindow != null) {
            mGiftCountPopupWindow.dismiss();
        }
        //cancel 网络请求
        if (mLiveGiftPagerAdapter != null) {
            mLiveGiftPagerAdapter.release();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_send || i == R.id.btn_send_lian) {
            sendGift();

        } else if (i == R.id.btn_choose) {
            showGiftCount();

        } else if (i == R.id.btn_close) {
            dismiss();

        } else if (i == R.id.coin) {
            forwardMyCoin();
        }
    }

    /**
     * 跳转到我的钻石
     */
    private void forwardMyCoin() {
        dismiss();
        if (mContext instanceof RoomActivity){
            ((RoomActivity) mContext).getWallet();
        }
    }

    /**
     * 显示分组数量
     */
    private void showGiftCount() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.view_gift_count, null);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true));
        LiveGiftCountAdapter adapter = new LiveGiftCountAdapter(mContext);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        mGiftCountPopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mGiftCountPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mGiftCountPopupWindow.setOutsideTouchable(true);
        mGiftCountPopupWindow.showAtLocation(mBtnChooseCount, Gravity.BOTTOM | Gravity.RIGHT, CommonUtils.dip2px(mContext,70), CommonUtils.dip2px(mContext,40));
    }

    /**
     * 隐藏分组数量
     */
    private void hideGiftCount() {
        if (mGiftCountPopupWindow != null) {
            mGiftCountPopupWindow.dismiss();
        }
    }


    @Override
    public void onItemClick(String bean, int position) {
        mCount = bean;
        mBtnChooseCount.setText(bean);
        hideGiftCount();
    }

    @Override
    public void onItemChecked(LiveGiftBean bean) {
        mLiveGiftBean = bean;
        hideLianBtn();
        mBtnSend.setEnabled(true);
        if (!DEFAULT_COUNT.equals(mCount)) {
            mCount = DEFAULT_COUNT;
            mBtnChooseCount.setText(DEFAULT_COUNT);
        }
        if (bean.getStyle() == 2 || bean.getStyle() == 3) {//豪华礼物 不可连发//礼物样式 1:图片 2:gif动图 3:svga动图
            if (mBtnChooseCount != null && mBtnChooseCount.getVisibility() == View.VISIBLE) {
                mBtnChooseCount.setVisibility(View.INVISIBLE);
                mArrow.setVisibility(View.INVISIBLE);
                mBtnSend.setBackground(mDrawable2);
            }
        } else {
            if (mBtnChooseCount != null && mBtnChooseCount.getVisibility() != View.VISIBLE) {
                mBtnChooseCount.setVisibility(View.VISIBLE);
                mArrow.setVisibility(View.VISIBLE);
                mBtnSend.setBackground(mDrawable1);
            }
        }
    }

    private boolean issendgift = false;
    /**
     * 赠送礼物
     */
    public void sendGift() {
        if (mLiveGiftBean == null) {
            return;
        }
        //此礼物需要开通守护 用户未开启守护
        if (mLiveGiftBean.getCh_cat_id() != 0 && mIsGuard.equals("0")) {
            NToast.shortToast(mContext, "守护专属礼物");
            return;
        }

        final int count = Integer.parseInt(mCount);
        String price = mLiveGiftBean.getCoin();
        final String votes = new BigDecimal(count).multiply(new BigDecimal(price)).stripTrailingZeros().toPlainString();

        if (issendgift){
            return;
        }
        issendgift = true;

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("receive_uid", mLiveUid)//接收者uid
                    .put("robot_id", mLiveUid)//机器人id ，如果非机器人，则该值与receive_uid相同
                    .put("gift_uni_code", mLiveGiftBean.getGift_uni_code())//礼包唯一码
                    .put("gift_num", count)//礼物数量，不传默认为1
                    .put("live_id", "0")//房间ID，默认为0
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/gift/sendGift", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                issendgift = false;
                //关闭连发
                hideLianBtn();
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                issendgift = false;
                if (mContext != null) {
                    mBean = new LiveReceiveGiftBean();
                    LiveReceiveGiftBeanItem item = new LiveReceiveGiftBeanItem();

                    item.setGiftCount(count);
                    item.setVotes(String.valueOf(votes));

                    item.setGiftId(mLiveGiftBean.getGift_uni_code());
                    item.setGiftName(mLiveGiftBean.getName());
                    item.setGiftIcon(mLiveGiftBean.getIcon_img());
                    item.setGifUrl(mLiveGiftBean.getEffect_img());
                    item.setStyle(mLiveGiftBean.getStyle());

                    mBean.setLiveReceiveGiftBeanItem(item);

                    if (item.getStyle() == 2 || item.getStyle() == 3) {
                        LoadDialog.show(getActivity());
                        GifCacheUtil.getFile(item.getGifUrl(), mDownloadGifCallback);
                    }else {
                        ((RoomActivity) mContext).sendGift(mBean);
                    }
                }

                if (mLiveGiftBean.getStyle() == 1) {//普通礼物显示连发
                    showLianBtn();
                }
                restCoin = new BigDecimal(restCoin).subtract(new BigDecimal(votes)).stripTrailingZeros().toPlainString();
                mCoin.setText(String.valueOf(restCoin));
            }
        });
    }

    //#################################################################
    //先下载下
    private LiveReceiveGiftBean mBean;
    private CommonCallback<File> mDownloadGifCallback = new CommonCallback<File>() {
        @Override
        public void callback(File file) {
            LoadDialog.dismiss(getActivity());
            if (file != null) {
                mBean.setGifPath(file.getPath());
                ((RoomActivity) mContext).sendGift(mBean);
            } else {
                NToast.shortToast(mContext, "下载失败");
            }
        }
    };
    //#################################################################

    /**
     * 隐藏连送按钮
     */
    private void hideLianBtn() {
        mShowLianBtn = false;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() == View.VISIBLE) {
            mBtnSendLian.setVisibility(View.INVISIBLE);
        }
        if (mBtnSendGroup != null && mBtnSendGroup.getVisibility() != View.VISIBLE) {
            mBtnSendGroup.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示连送按钮
     */
    private void showLianBtn() {
        if (mLianText != null) {
            mLianText.setText("5s");
        }
        mLianCountDownCount = 5;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
        }
        if (mShowLianBtn) {
            return;
        }
        mShowLianBtn = true;
        if (mBtnSendGroup != null && mBtnSendGroup.getVisibility() == View.VISIBLE) {
            mBtnSendGroup.setVisibility(View.INVISIBLE);
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() != View.VISIBLE) {
            mBtnSendLian.setVisibility(View.VISIBLE);
        }
    }
}
