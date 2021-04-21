package com.yjfshop123.live.ui.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.cretin.www.wheelsruflibrary.listener.RotateListener;
import com.cretin.www.wheelsruflibrary.view.WheelSurfView;
import com.opensource.svgaplayer.SVGAImageView;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.model.PriceResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.utils.imageloader.CircleBitmapDisplayer;
import com.yjfshop123.live.server.utils.imageloader.DisplayImageOptions;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.HomeActivity;
import com.yjfshop123.live.ui.fragment.MyFragmentNewThree;
import com.yjfshop123.live.ui.fragment.MyFragmentNewTwo;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.NumUtil;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ChoujiangFragment extends AbsDialogFragment implements View.OnClickListener {

    ImageView dajiang_tip;
    ImageView one;
    ImageView two;
    ImageView three;
    ImageView four;
    ImageView five;
    ImageView six, logo, close;
    GifImageView gift_gif;
    TextView name;
    private PriceResponse mResponse;
    RelativeLayout zuihou, roo_view;
    View rootL;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_choujiang;
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
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        roo_view = mRootView.findViewById(R.id.roo_view);
        rootL = mRootView.findViewById(R.id.rootL);
        logo = mRootView.findViewById(R.id.logo);
        close = mRootView.findViewById(R.id.close);
        name = mRootView.findViewById(R.id.name);
        dajiang_tip = mRootView.findViewById(R.id.dajiang_tip);
        zuihou = mRootView.findViewById(R.id.zuihou);
        one = mRootView.findViewById(R.id.one);
        two = mRootView.findViewById(R.id.two);
        three = mRootView.findViewById(R.id.three);
        four = mRootView.findViewById(R.id.four);
        five = mRootView.findViewById(R.id.five);
        six = mRootView.findViewById(R.id.six);
        gift_gif = mRootView.findViewById(R.id.gift_gif);
        int with = (CommonUtils.getScreenWidth(getContext()) - SystemUtils.dip2px(getContext(), 130)) / 3;
        int heigh = with * 140 / 104;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) one.getLayoutParams();
        //获取当前控件的布局对象
        params.height = heigh;//设置当前控件布局的高度
        params.width = with;
        one.setLayoutParams(params);

        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) two.getLayoutParams();
        //获取当前控件的布局对象
        params1.height = heigh;//设置当前控件布局的高度
        params1.width = with;
        two.setLayoutParams(params);
        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) three.getLayoutParams();
        //获取当前控件的布局对象
        params2.height = heigh;//设置当前控件布局的高度
        params2.width = with;
        three.setLayoutParams(params2);
        LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) four.getLayoutParams();
        //获取当前控件的布局对象
        params3.height = heigh;//设置当前控件布局的高度
        params3.width = with;
        four.setLayoutParams(params3);
        LinearLayout.LayoutParams params4 = (LinearLayout.LayoutParams) five.getLayoutParams();
        //获取当前控件的布局对象
        params4.width = with;
        params4.height = heigh;//设置当前控件布局的高度
        five.setLayoutParams(params4);
        LinearLayout.LayoutParams params5 = (LinearLayout.LayoutParams) six.getLayoutParams();
        //获取当前控件的布局对象

        params5.width = with;
        params5.height = heigh;//设置当前控件布局的高度
        six.setLayoutParams(params5);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rootL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        gifDrawable = (GifDrawable) gift_gif.getDrawable();
        //gifDrawable.stop();
        gifDrawable.addAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationCompleted(int loopNumber) {
                if (isChoujiangIng) {
                    gifDrawable.reset();
                    gifDrawable.start();
                } else {
                    gift_gif.setVisibility(View.INVISIBLE);
                    // zuihou.setVisibility(View.VISIBLE);
                    if (selectData != null) {
                        //弹出抽到的奖品
                        name.setText(target_reward_value);
                        Glide.with(getActivity()).load(CommonUtils.getUrl(selectData.prize_icon)).into(logo);
                        zuihou.setVisibility(View.VISIBLE);
                    } else {
                        //抽奖失败 暂不用处理
                    }
                }
            }
        });

        loadData();
    }

    boolean isChoujiangIng = false;
    GifDrawable gifDrawable;

    @Override
    public void onClick(View v) {
        if (isChoujiangIng) {
            return;
        }
        if (zuihou.getVisibility() == View.VISIBLE) return;
        switch (v.getId()) {
            case R.id.one:
                if (gift_gif.getVisibility() != View.VISIBLE) {
                    go();
                    //Glide.with(getActivity()).load(R.mipmap.dansui).into(one);
                    gift_gif.setVisibility(View.VISIBLE);
                    gifDrawable.reset();
                    gifDrawable.start();

                }
                break;
            case R.id.two:
                if (gift_gif.getVisibility() != View.VISIBLE) {
                    go();
                    // Glide.with(getActivity()).load(R.mipmap.dansui).into(two);
                    gift_gif.setVisibility(View.VISIBLE);
                    gifDrawable.reset();
                    gifDrawable.start();
                }
                break;
            case R.id.three:
                if (gift_gif.getVisibility() != View.VISIBLE) {
                    go();
                    // Glide.with(getActivity()).load(R.mipmap.dansui).into(three);
                    gift_gif.setVisibility(View.VISIBLE);
                    gifDrawable.reset();
                    gifDrawable.start();
                }
                break;
            case R.id.four:
                if (gift_gif.getVisibility() != View.VISIBLE) {
                    go();
                    // Glide.with(getActivity()).load(R.mipmap.dansui).into(four);
                    gift_gif.setVisibility(View.VISIBLE);
                    gifDrawable.reset();
                    gifDrawable.start();
                }
                break;
            case R.id.five:
                if (gift_gif.getVisibility() != View.VISIBLE) {
                    go();
                    //  Glide.with(getActivity()).load(R.mipmap.dansui).into(five);
                    gift_gif.setVisibility(View.VISIBLE);
                    gifDrawable.reset();
                    gifDrawable.start();
                }
                break;
            case R.id.six:
                if (gift_gif.getVisibility() != View.VISIBLE) {
                    go();
                    // Glide.with(getActivity()).load(R.mipmap.dansui).into(six);
                    gift_gif.setVisibility(View.VISIBLE);
                    gifDrawable.reset();
                    gifDrawable.start();
                }
                break;

        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (selectData != null) {
            if (myFragmentNewTwo != null) myFragmentNewTwo.choujiangSuccess();
            if (myFragmentNewThree != null) myFragmentNewThree.choujiangSuccess();
        }
    }

    private void loadData() {
        if (!com.yjfshop123.live.server.utils.CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, getString(R.string.net_error));
            return;
        }

        // LoadDialog.show(getActivity());
        OKHttpUtils.getInstance().getRequest("app/prize/getList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(getActivity());
                dismiss();
//                try {
//                    mResponse = JsonMananger.jsonToBean(testData, PriceResponse.class);
//                    loadData_();
//                } catch (Exception e) {
//                    LoadDialog.dismiss(getActivity());
//                }
            }

            @Override
            public void onSuccess(String result) {
                try {
                    mResponse = JsonMananger.jsonToBean(result, PriceResponse.class);
                    loadData_();
                } catch (Exception e) {
                    LoadDialog.dismiss(getActivity());
                }
            }
        });
    }

    private void loadData_() {

        size = mResponse.list.size();
        if (size == 0) {
            dismiss();
            return;
        }
        roo_view.setVisibility(View.VISIBLE);

    }

    List<Bitmap> mListBitmap = new ArrayList<>();//图标


    private int size;


    HomeActivity homeActivity;

    public void setHomeActivity(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    MyFragmentNewTwo myFragmentNewTwo;

    public void setmyFragmentNewTwo(MyFragmentNewTwo myFragmentNewTwo) {
        this.myFragmentNewTwo = myFragmentNewTwo;
    }
    MyFragmentNewThree myFragmentNewThree;

    public void setmyFragmentNewThree(MyFragmentNewThree myFragmentNewThree) {
        this.myFragmentNewThree = myFragmentNewThree;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private DisplayImageOptions options;

    String target_reward_value;

    private void go() {
        isChoujiangIng = true;
        OKHttpUtils.getInstance().getRequest("app/prize/selectPrize", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
                isChoujiangIng = false;
            }

            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject data = new JSONObject(result);
                    int id = data.getInt("id");
                    target_reward_value = data.getString("target_reward_value");
                    for (int i = 0; i < size; i++) {
                        int id_ = mResponse.list.get(i).id;
                        if (id == id_) {
                            selectIndex = i;

                            if (homeActivity != null) homeActivity.loadData();
                            selectData = mResponse.list.get(i);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    isChoujiangIng = false;
                }
            }
        });
    }

    PriceResponse.AdditionData selectData;
    int selectIndex = -1;
}

