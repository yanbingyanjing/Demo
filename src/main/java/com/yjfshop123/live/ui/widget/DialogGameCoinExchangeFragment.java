package com.yjfshop123.live.ui.widget;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.google.common.graph.ImmutableNetwork;
import com.google.gson.Gson;
import com.mgc.leto.game.base.mgc.thirdparty.MintageResult;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.model.GameListResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.ui.activity.BadEggActivity;
import com.yjfshop123.live.ui.activity.HomeActivity;
import com.yjfshop123.live.utils.CommonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DialogGameCoinExchangeFragment extends AbsDialogFragment {


    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.egg_1)
    ImageView egg1;
    @BindView(R.id.egg_1_count)
    TextView egg1Count;
    @BindView(R.id.egg_1_count_gou)
    ImageView egg1CountGou;
    @BindView(R.id.egg_2)
    ImageView egg2;
    @BindView(R.id.egg_2_count)
    TextView egg2Count;
    @BindView(R.id.egg_2_count_gou)
    ImageView egg2CountGou;
    @BindView(R.id.egg_3)
    ImageView egg3;
    @BindView(R.id.egg_3_count)
    TextView egg3Count;
    @BindView(R.id.egg_3_count_gou)
    ImageView egg3CountGou;
    @BindView(R.id.egg_4)
    ImageView egg4;
    @BindView(R.id.egg_4_count)
    TextView egg4Count;
    @BindView(R.id.egg_4_count_gou)
    ImageView egg4CountGou;
    @BindView(R.id.bg_ll)
    LinearLayout bgLl;
    @BindView(R.id.egg_5)
    ImageView egg5;
    @BindView(R.id.egg_5_count)
    TextView egg5Count;
    @BindView(R.id.egg_5_count_gou)
    ImageView egg5CountGou;
    @BindView(R.id.egg_6)
    ImageView egg6;
    @BindView(R.id.egg_6_count)
    TextView egg6Count;
    @BindView(R.id.egg_6_count_gou)
    ImageView egg6CountGou;
    @BindView(R.id.egg_7)
    ImageView egg7;
    @BindView(R.id.egg_7_count)
    TextView egg7Count;
    @BindView(R.id.egg_7_count_gou)
    ImageView egg7CountGou;
    @BindView(R.id.egg_8)
    ImageView egg8;
    @BindView(R.id.egg_8_count)
    TextView egg8Count;
    @BindView(R.id.egg_8_count_gou)
    ImageView egg8CountGou;
    @BindView(R.id.exchange)
    TextView exchange;
    @BindView(R.id.quan_count)
    TextView quanCount;
    @BindView(R.id.shengyu_count)
    TextView shengyuCount;
    @BindView(R.id.shuoming)
    TextView shuoming;
    Unbinder unbinder;
    @BindView(R.id.egg_1_LL)
    RelativeLayout egg1LL;
    @BindView(R.id.egg_2_LL)
    RelativeLayout egg2LL;
    @BindView(R.id.egg_3_LL)
    RelativeLayout egg3LL;
    @BindView(R.id.egg_4_LL)
    RelativeLayout egg4LL;
    @BindView(R.id.egg_5_LL)
    RelativeLayout egg5LL;
    @BindView(R.id.egg_6_LL)
    RelativeLayout egg6LL;
    @BindView(R.id.egg_7_LL)
    RelativeLayout egg7LL;
    @BindView(R.id.egg_8_LL)
    RelativeLayout egg8LL;
    List<RelativeLayout> relativeLayouts = new ArrayList<>();
    List<ImageView> gous = new ArrayList<>();
BadEggActivity badEggActivity;
public void setBadEggActivity(BadEggActivity badEggActivity){
    this.badEggActivity=badEggActivity;
}
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_game_coin_exchange;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = CommonUtils.getScreenWidth(mContext) * 320 / 375;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    HomeActivity homeActivity;

    public void setHome(HomeActivity home) {
        homeActivity = home;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        unbinder = ButterKnife.bind(this, mRootView);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                dismiss();
            }
        });
        shuoming.setMovementMethod(ScrollingMovementMethod.getInstance());
        relativeLayouts.add(egg1LL);
        relativeLayouts.add(egg2LL);
        relativeLayouts.add(egg3LL);
        relativeLayouts.add(egg4LL);
        relativeLayouts.add(egg5LL);
        relativeLayouts.add(egg6LL);
        relativeLayouts.add(egg7LL);
        relativeLayouts.add(egg8LL);

        gous.add(egg1CountGou);
        gous.add(egg2CountGou);
        gous.add(egg3CountGou);
        gous.add(egg4CountGou);
        gous.add(egg5CountGou);
        gous.add(egg6CountGou);
        gous.add(egg7CountGou);
        gous.add(egg8CountGou);

        exchange.setSelected(false);
        loadGameData();
    }

    private void loadGameData() {
        OKHttpUtils.getInstance().getRequest("app/game/config", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {

            }

            @Override
            public void onSuccess(String result) {
                if (TextUtils.isEmpty(result)) return;
                gameListResponse = new Gson().fromJson(result, GameListResponse.class);
                shuoming.setText(gameListResponse.desc);
                quanCount.setText(gameListResponse.exchange_coupon + "");
                shengyuCount.setText(gameListResponse.today_exchange_times + "");
                if (gameListResponse.exchange_coupon <= 0 || gameListResponse.today_exchange_times <= 0) {
                    exchange.setSelected(false);
                } else {
                    exchange.setSelected(true);
                }
                if (gameListResponse.coinList != null && gameListResponse.coinList.length > 0) {
                    if (gameListResponse.coinList.length > 0) {
                        Glide.with(getContext()).load(R.mipmap.exchange_egg).into(egg1);
                        egg1Count.setText(gameListResponse.coinList[0] + "金蛋");
                    }

                    if (gameListResponse.coinList.length > 1) {
                        Glide.with(getContext()).load(R.mipmap.exchange_egg).into(egg2);
                        egg2Count.setText(gameListResponse.coinList[1] + "金蛋");
                    }
                    if (gameListResponse.coinList.length > 2) {
                        Glide.with(getContext()).load(R.mipmap.exchange_egg).into(egg3);
                        egg3Count.setText(gameListResponse.coinList[2] + "金蛋");
                    }
                    if (gameListResponse.coinList.length > 3) {
                        Glide.with(getContext()).load(R.mipmap.exchange_egg).into(egg4);
                        egg4Count.setText(gameListResponse.coinList[3] + "金蛋");
                    }
                    if (gameListResponse.coinList.length > 4) {
                        Glide.with(getContext()).load(R.mipmap.exchange_egg).into(egg5);
                        egg5Count.setText(gameListResponse.coinList[4] + "金蛋");
                    }
                    if (gameListResponse.coinList.length > 5) {
                        Glide.with(getContext()).load(R.mipmap.exchange_egg).into(egg6);
                        egg6Count.setText(gameListResponse.coinList[5] + "金蛋");
                    }
                    if (gameListResponse.coinList.length > 6) {
                        Glide.with(getContext()).load(R.mipmap.exchange_egg).into(egg7);
                        egg7Count.setText(gameListResponse.coinList[6] + "金蛋");
                    }
                    if (gameListResponse.coinList.length > 7) {
                        Glide.with(getContext()).load(R.mipmap.exchange_egg).into(egg8);
                        egg8Count.setText(gameListResponse.coinList[7] + "金蛋");
                    }
                }


            }
        });
    }

    private GameListResponse gameListResponse;




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    int select = -1;

    @OnClick({R.id.exchange,R.id.egg_1_LL, R.id.egg_2_LL, R.id.egg_3_LL, R.id.egg_4_LL, R.id.egg_5_LL, R.id.egg_6_LL, R.id.egg_7_LL, R.id.egg_8_LL})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.exchange:
                if(!exchange.isSelected())return;
                if(select==-1){
                    NToast.shortToast(getContext(),"请选择要兑换的金蛋数");
                    return;
                }

//                if(gameListResponse.exchange_coupon<=0){
//                    NToast.shortToast(getContext(),"无可用兑换券");
//                    return;
//                }
//                if(gameListResponse.today_exchange_times<=0){
//                    NToast.shortToast(getContext(),"今日无剩余兑换次数");
//                    return;
//                }
                String body = "";
                try {
                    body = new JsonBuilder()
                            .put("eggs", gameListResponse.coinList[select])
                            .build();
                } catch (JSONException e) {
                }
                final MintageResult resultGame = new MintageResult();
                OKHttpUtils.getInstance().getRequest("app/game/exChangeGoldEgg", body, new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        NToast.shortToast(getContext(),errInfo);

                    }

                    @Override
                    public void onSuccess(String result) {
                        //发币结束后 回调通知
                        NToast.shortToast(getContext(),"兑换成功");
                        if(badEggActivity!=null)badEggActivity.loadData();
                        loadGameData();
                    }
                });
                break;
            case R.id.egg_1_LL:
                initEgg(0);
                break;
            case R.id.egg_2_LL:
                initEgg(1);
                break;
            case R.id.egg_3_LL:
                initEgg(2);
                break;
            case R.id.egg_4_LL:
                initEgg(3);
                break;
            case R.id.egg_5_LL:
                initEgg(4);
                break;
            case R.id.egg_6_LL:
                initEgg(5);
                break;
            case R.id.egg_7_LL:
                initEgg(6);
                break;
            case R.id.egg_8_LL:
                initEgg(7);
                break;
        }
    }

    private void initEgg(int index) {
        if (gameListResponse.coinList.length > index) {
            if (select != -1) {
                relativeLayouts.get(select).setSelected(false);
                gous.get(select).setVisibility(View.INVISIBLE);
            }
            select = index;
            relativeLayouts.get(select).setSelected(true);
            gous.get(select).setVisibility(View.VISIBLE);
        }
    }
}
