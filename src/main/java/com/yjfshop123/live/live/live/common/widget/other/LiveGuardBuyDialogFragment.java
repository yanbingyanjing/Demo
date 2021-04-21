package com.yjfshop123.live.live.live.common.widget.other;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.live.response.WatchTypeListsResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;

import org.json.JSONException;

public class LiveGuardBuyDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private RadioButton[] mRadioBtns;
    private TextView[] mPrices;
    private TextView mCoinNameTextView;
    private TextView mCoin;
    private View mBtnBuy;
    private long mCoinVal;//余额
    private String mLiveUid;//主播ID
    private String mLiveId;//直播间ID

    private ImageView mIcon2;
    private TextView mTitle2;
    private TextView mContent2;
    private int mColor1;
    private int mColor2;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_guard_buy;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog2;
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
        mColor1 = ContextCompat.getColor(mContext, R.color.textColor);
        mColor2 = ContextCompat.getColor(mContext, R.color.gray3);

        mRadioBtns = new RadioButton[3];
        mRadioBtns[0] = (RadioButton) mRootView.findViewById(R.id.btn_1);
        mRadioBtns[1] = (RadioButton) mRootView.findViewById(R.id.btn_2);
        mRadioBtns[2] = (RadioButton) mRootView.findViewById(R.id.btn_3);
        mPrices = new TextView[3];
        mPrices[0] = (TextView) mRootView.findViewById(R.id.price_1);
        mPrices[1] = (TextView) mRootView.findViewById(R.id.price_2);
        mPrices[2] = (TextView) mRootView.findViewById(R.id.price_3);
        mCoinNameTextView = (TextView) mRootView.findViewById(R.id.coin_name);
        mCoin = (TextView) mRootView.findViewById(R.id.coin);
        mBtnBuy = mRootView.findViewById(R.id.btn_buy);
        mRadioBtns[0].setOnClickListener(this);
        mRadioBtns[1].setOnClickListener(this);
        mRadioBtns[2].setOnClickListener(this);
        mBtnBuy.setOnClickListener(this);
        mCoin.setOnClickListener(this);

        mIcon2 = (ImageView) findViewById(R.id.icon_2);
        mTitle2 = (TextView) findViewById(R.id.title_2);
        mContent2 = (TextView) findViewById(R.id.content_2);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mLiveUid = bundle.getString("liveUid");
            mLiveId = bundle.getString("mLiveId");
        }
        mCoinNameTextView.setText("我的金蛋：");

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("user_id", mLiveUid)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/watch/getWatchTypeList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try{
                    watchTypeListsResponse = JsonMananger.jsonToBean(result, WatchTypeListsResponse.class);
                    mCoinVal = watchTypeListsResponse.getRest_coin();
                    mCoin.setText(String.valueOf(mCoinVal));

                    int buyListSize = watchTypeListsResponse.getList().size();
                    for (int i = 0, length = mPrices.length; i < length; i++) {
                        if (i < buyListSize) {
                            mRadioBtns[i].setText(watchTypeListsResponse.getList().get(i).getSubject());
                            mPrices[i].setText(String.valueOf(watchTypeListsResponse.getList().get(i).getCoin()));
                        }
                    }
                    refreshList(0);
                }catch (Exception e){
                }
            }
        });
    }

    private WatchTypeListsResponse watchTypeListsResponse;
    private int mIndex = 0;

    private void refreshList(int index) {
        if (watchTypeListsResponse == null){
            return;
        }

        if (index < 2){
            mIcon2.setImageResource(R.mipmap.icon_guard_l_1);
            mContent2.setTextColor(mColor2);
            mTitle2.setTextColor(mColor2);
        }else {
            mIcon2.setImageResource(R.mipmap.icon_guard_l_2);
            mContent2.setTextColor(mColor1);
            mTitle2.setTextColor(mColor1);
        }

        mIndex = index;
        mBtnBuy.setEnabled(mCoinVal >= watchTypeListsResponse.getList().get(mIndex).getCoin());
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_1) {
            refreshList(0);
        } else if (i == R.id.btn_2) {
            refreshList(1);
        } else if (i == R.id.btn_3) {
            refreshList(2);
        } else if (i == R.id.btn_buy) {
            clickBuyGuard();
        } else if (i == R.id.coin) {
            forwardMyCoin();
        }
    }

    /**
     * 跳转到我的钻石
     */
    private void forwardMyCoin() {
        dismiss();
        if (mContext instanceof TCLivePlayerActivity){
            ((TCLivePlayerActivity) mContext).getWallet();
        }

    }

    /**
     * 点击购买守护按钮
     */
    private void clickBuyGuard() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }

        DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.ac_select_friend_sure), getString(R.string.cancel),
                String.format("您将花费%1$s金蛋，为主播开通%2$s守护", watchTypeListsResponse.getList().get(mIndex).getCoin(),
                        watchTypeListsResponse.getList().get(mIndex).getSubject()),
                true, false,
                new DialogUitl.SimpleCallback2() {
                    @Override
                    public void onCancelClick() {

                    }
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        dialog.dismiss();

                        doBuyGuard();
                    }
                });
    }

    /**
     * 购买守护
     */
    private void doBuyGuard() {
        String day_time = String.valueOf(watchTypeListsResponse.getList().get(mIndex).getDay_time());

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("receive_uid", mLiveUid)//被守护用户uid
                    .put("day_time", day_time)//守护时间,天
                    .put("live_id", mLiveId)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/watch/addWatch", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, "开通守护成功");
                dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

