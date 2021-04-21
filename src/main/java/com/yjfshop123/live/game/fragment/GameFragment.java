package com.yjfshop123.live.game.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.GlideImageLoader;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.BannerResponse;
import com.yjfshop123.live.net.response.NoticeResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.widget.MarqueeTextView;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.AccordionTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class GameFragment extends BaseFragment implements OnBannerListener {

    @BindView(R.id.game_banner)
    Banner bannerView;

    @BindView(R.id.live_list_notice_fl)
    FrameLayout live_list_notice_fl;
    @BindView(R.id.live_list_notice_content)
    MarqueeTextView live_list_notice_content;
    @BindView(R.id.live_list_notice_close)
    ImageView live_list_notice_close;

    @BindView(R.id.game_controls)
    LinearLayout mControls;

    private BannerResponse mBanners;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_game;
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initEvent() {
        live_list_notice_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                live_list_notice_fl.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        loadData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if (!hidden){
//            loadData();
//        }
    }

    private void loadData(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("scene_id", 11)//场景 1：首页顶部广告 2：社区顶部广告 3:APP开屏广告 4:直播顶部广告
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/ad/getBanner", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    try {
                        mBanners = JsonMananger.jsonToBean(result, BannerResponse.class);
                        loadBanner();
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        live_list_notice_fl.setVisibility(View.GONE);
        String body_ = "";
        try {
            body_ = new JsonBuilder()
                    .put("scene_id", 11)//场景（1：直播列表页顶部 2：直播房间页顶部）
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/ad/getNotice", body_, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                live_list_notice_fl.setVisibility(View.GONE);
            }
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    try {
                        NoticeResponse response = JsonMananger.jsonToBean(result, NoticeResponse.class);
                        String text = response.getContent().getText();
                        if (TextUtils.isEmpty(text)){
                            live_list_notice_fl.setVisibility(View.GONE);
                            return;
                        }
                        live_list_notice_fl.setVisibility(View.VISIBLE);
                        live_list_notice_content.setText(text);

                        final String link = response.getContent().getLink();
                        live_list_notice_content.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(link)) {
                                    Intent intent = new Intent("io.xchat.intent.action.webview");
                                    intent.setPackage(getContext().getPackageName());
                                    intent.putExtra("url", link);
                                    startActivity(intent);
                                }
                            }
                        });
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        OKHttpUtils.getInstance().getRequest("hg/hggame/getCategoryList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    try {
                        JSONObject data = new JSONObject(result);
                        JSONArray list = data.getJSONArray("list");
                        for (int i = 0; i < list.length(); i++) {
                            View view = View.inflate(mContext, R.layout.item_game_menu, null);
                            mControls.addView(view);

                            JSONObject jso = list.getJSONObject(i);

                            ImageView icon = view.findViewById(R.id.avatar);
                            TextView name = view.findViewById(R.id.name);
                            View line = view.findViewById(R.id.line);
                            Glide.with(mContext)
                                    .load(CommonUtils.getUrl(jso.getString("icon")))
                                    .into(icon);
                            name.setText(jso.getString("cn_name"));

                            final String category_id = jso.getString("category_id");

                            if (i == 0){
                                line.setVisibility(View.VISIBLE);
                                name.setTextColor(Color.parseColor("#fff83600"));
                                openFragment(category_id);
                            }else {
                                line.setVisibility(View.GONE);
                                name.setTextColor(getResources().getColor(R.color.color_333333));
                            }

                            final int position = i;

                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (int j = 0; j < mControls.getChildCount(); j++) {
                                        View view_ = mControls.getChildAt(j);
                                        TextView name = view_.findViewById(R.id.name);
                                        View line = view_.findViewById(R.id.line);
                                        if (j == position){
                                            line.setVisibility(View.VISIBLE);
                                            name.setTextColor(Color.parseColor("#fff83600"));
                                            openFragment(category_id);
                                        }else {
                                            line.setVisibility(View.GONE);
                                            name.setTextColor(getResources().getColor(R.color.color_333333));
                                        }
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private GameFragmentItem fragmentItem;

    private void openFragment(String category_id) {

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        if (null != fragmentItem) {
            fragmentTransaction.hide(fragmentItem);
        }

        fragmentItem = new GameFragmentItem();
        fragmentTransaction.add(R.id.game_fl, fragmentItem, category_id);

        Bundle bundle = new Bundle();
        bundle.putString("category_id", category_id);
        fragmentItem.setArguments(bundle);

        fragmentTransaction.show(fragmentItem);
        fragmentTransaction.commit();
    }

    private void loadBanner(){
        ViewGroup.LayoutParams params = bannerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = getResources().getDisplayMetrics().widthPixels * 38 / 75;
        bannerView.setLayoutParams(params);

        List<String> images = new ArrayList<>();
        if (mBanners.getList().size() > 0) {
            for (int i = 0; i < mBanners.getList().size(); i++) {
                images.add(CommonUtils.getUrl(mBanners.getList().get(i).getImg()));
            }
        }
        if (images.size() == 0){
            return;
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

    @Override
    protected void updateViews(boolean isRefresh) {
    }

    @Override
    public void OnBannerClick(int position) {
        String link = mBanners.getList().get(position).getLink();
        if (!TextUtils.isEmpty(link)) {
            Intent intent = new Intent("io.xchat.intent.action.webview");
            intent.setPackage(getContext().getPackageName());
            intent.putExtra("url", link);
            startActivity(intent);
        }
    }
}
