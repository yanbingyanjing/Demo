package com.yjfshop123.live.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.model.TurnOutConfigResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.ShowImgDialog;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CheckInstalledUtil;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TurnOutEggActivity extends BaseActivityForNewUi {
    @BindView(R.id.tips)
    TextView tips;
    @BindView(R.id.can_turn_outtitle)
    TextView canTurnOuttitle;
    @BindView(R.id.can_turn_out)
    TextView canTurnOut;
    @BindView(R.id.send_egg_title)
    TextView sendEggTitle;
    @BindView(R.id.send_num)
    EditText sendNum;
    @BindView(R.id.all)
    TextView all;
    @BindView(R.id.fee)
    TextView fee;

    @BindView(R.id.send)
    TextView send;
    @BindView(R.id.swipe_refresh)
    VerticalSwipeRefreshLayout mSwipeRefresh;
    int type = -1;//1金蛋  2银蛋
    public static int GOLD_TYPE = 1;
    public static int SILVER_TYPE = 2;
    String goldType = "";
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.go_update)
    LinearLayout goUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_out_egg);
        ButterKnife.bind(this);
        initSwipeRefresh();
        type = getIntent().getIntExtra("type", -1);
        initView();
    }

    private void initView() {
        if (type < 0) return;
        if (type == GOLD_TYPE) {
            setCenterTitleText(getString(R.string.turn_out_egg));
            goldType = getString(R.string.gold_egg);
            logo.setImageResource(R.mipmap.gold_egg_logo);
        }
        if (type == SILVER_TYPE) {
            goldType = getString(R.string.yin_egg);
            setCenterTitleText(getString(R.string.turn_out_silver_egg));
            logo.setImageResource(R.mipmap.silver_egg_logo);
        }
        tips.setText(getString(R.string.turn_out_tips, goldType));
        canTurnOuttitle.setText(getString(R.string.can_turn_out_title, goldType));
        sendEggTitle.setText(getString(R.string.send_egg_title, goldType));
        sendNum.setHint(getString(R.string.hint_send_egg_num, goldType));
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });
        }
    }

    private void loadData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("type", type)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/user/turnoutconfig", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
            }

            @Override
            public void onSuccess(String result) {
                initDatas(result);
            }
        });
    }

    private void send() {
        LoadDialog.show(this);
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("type", type)
                    .put("num", sendNum.getText().toString())
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/user/turnout", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(TurnOutEggActivity.this);
                NToast.shortToast(mContext, errInfo);
            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(TurnOutEggActivity.this);
                NToast.shortToast(mContext, getString(R.string.send_success));

            }
        });
    }

    TurnOutConfigResponse myEggResponse;

    private void initDatas(String result) {
        finishRefresh();
        if (TextUtils.isEmpty(result)) return;
        myEggResponse = new Gson().fromJson(result, TurnOutConfigResponse.class);
        if (myEggResponse == null) return;
        sendNum.setEnabled(true);
        canTurnOut.setText(myEggResponse.can_turn_out);
        fee.setText(String.format("%s\n%s", getString(R.string.fee), myEggResponse.fee));
    }

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }


    @OnClick({R.id.all, R.id.go_update, R.id.send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.all:
                if (myEggResponse != null)
                    sendNum.setText(myEggResponse.can_turn_out);
                break;
            case R.id.go_update:
                startActivity(new Intent(this, LevelActivity.class));
                break;
            case R.id.send:
                if (myEggResponse == null) {
                    NToast.shortToast(this, getString(R.string.tips_turn_out_nodata));
                    return;
                }
                if (TextUtils.isEmpty(sendNum.getText().toString())) {
                    NToast.shortToast(this, sendNum.getHint().toString());
                    return;
                }
                ShowImgDialog dialogFragment = new ShowImgDialog();
                dialogFragment.setOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        send();
                    }
                });
                dialogFragment.setTitle( "发送" +sendNum.getText().toString() + "个" + goldType + "到交易所");
                dialogFragment.setLogo(type == SILVER_TYPE?R.mipmap.tip_silver_egg:R.mipmap.to_exchange_logo);
                dialogFragment.show(getSupportFragmentManager(), "ShowImgDialog");
//                dialog = DialogUitl.showSimpleHintDialog(mContext, getString(R.string.prompt), getString(R.string.confirm), getString(R.string.cancel),
//                        "发送" + sendNum.getText().toString() + "个" + goldType + "到交易所", true, false,
//                        new DialogUitl.SimpleCallback2() {
//                            @Override
//                            public void onCancelClick() {
//
//                            }
//
//                            @Override
//                            public void onConfirmClick(Dialog dialog, String content) {
//                                dialog.dismiss();
//                                send();
//                            }
//                        });
//                dialog.show();

                break;
        }
    }

    Dialog dialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(true);
        }
        loadData();
    }
}
