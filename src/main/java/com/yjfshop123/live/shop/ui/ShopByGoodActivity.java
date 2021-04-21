package com.yjfshop123.live.shop.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.shop.adapter.ShopDetailAdapter;
import com.yjfshop123.live.shop.model.ShopDeatail;
import com.yjfshop123.live.shop.model.ShopList;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.ui.activity.BaseAppCompatActivity;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;
import com.youth.banner.transformer.AccordionTransformer;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ShopByGoodActivity extends BaseAppCompatActivity {


    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.mm_abl)
    AppBarLayout mmAbl;
    @BindView(R.id.list)
    RecyclerView list;


    ShopList.ShopData data;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.back_round)
    ImageView backRound;
    @BindView(R.id.back_t)
    ImageView backT;
    @BindView(R.id.shop_one)
    TextView shopOne;
    @BindView(R.id.one_bottom)
    View oneBottom;
    @BindView(R.id.shop_two)
    TextView shopTwo;
    @BindView(R.id.two_bottom)
    View twoBottom;
    @BindView(R.id.shop_three)
    TextView shopThree;
    @BindView(R.id.three_bottom)
    View threeBottom;

    @BindView(R.id.tool_bar)
    RelativeLayout toolBar;
    @BindView(R.id.current_count)
    TextView currentCount;

    private LinearLayoutManager mLinearLayoutManager;
    private ShopDetailAdapter adapter;

    boolean sIsScrolling = false;
    ZLoadingDialog dialog = new ZLoadingDialog(ShopByGoodActivity.this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(uiOptions);
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_shop_detail);
        ButterKnife.bind(this);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        LinearLayout.LayoutParams paramsBanner = (LinearLayout.LayoutParams) banner.getLayoutParams();
        //获取当前控件的布局对象
        paramsBanner.height = SystemUtils.getScreenWidth(this);//设置当前控件布局的高度
        paramsBanner.width =  SystemUtils.getScreenWidth(this);
        banner.setLayoutParams(paramsBanner);
        StatusBarUtil.StatusBarLightMode(this);
        dialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
                .setLoadingColor(getResources().getColor(R.color.color_0786fb))//颜色
                .setHintText("跳转中");
        setTopStatus(1);
        mLinearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLinearLayoutManager);
        adapter = new ShopDetailAdapter(this);
        adapter.setZLoadingDialog(dialog);
        list.setAdapter(adapter);


        mmAbl.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int sss = -verticalOffset;
                float a = 0f;
                if (sss * 1f / 80 >= 1) {
                    a = 1f;
                } else {
                    a = (sss % 80) * 1f / 80;
                }
                Log.d("透明度", a + " " + sss);
                if (verticalOffset >= 0) {
                    statusBarView.setAlpha(0);
                    backRound.setVisibility(View.VISIBLE);
                    toolBar.setVisibility(View.INVISIBLE);
                } else {
                    backRound.setVisibility(View.INVISIBLE);
                    toolBar.setVisibility(View.VISIBLE);
                    toolBar.setAlpha(a);
                    statusBarView.setAlpha(a);

                }
            }
        });
        if (getIntent() != null) {
            goodId = getIntent().getStringExtra("goodId");
            if (!TextUtils.isEmpty(goodId)) {
                getData(false);
            } else finish();
        }

    }

    String goodId = "";

    private void setTopStatus(int index) {
        if (index == 1) {
            shopOne.setSelected(true);
            oneBottom.setVisibility(View.INVISIBLE);
            shopTwo.setSelected(false);
            twoBottom.setVisibility(View.INVISIBLE);
            shopThree.setSelected(false);
            threeBottom.setVisibility(View.INVISIBLE);
        }
        if (index == 2) {
            shopOne.setSelected(false);
            oneBottom.setVisibility(View.INVISIBLE);
            shopTwo.setSelected(true);
            twoBottom.setVisibility(View.VISIBLE);
            shopThree.setSelected(false);
            threeBottom.setVisibility(View.INVISIBLE);
        }
        if (index == 3) {
            shopOne.setSelected(false);
            oneBottom.setVisibility(View.INVISIBLE);
            shopTwo.setSelected(false);
            twoBottom.setVisibility(View.INVISIBLE);
            shopThree.setSelected(true);
            threeBottom.setVisibility(View.VISIBLE);
        }
    }

    public void getData(boolean form) {
        // if (!form)
        // dialog.show();
        LoadDialog.show(this);
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("version", "v1.2.3");
        paraMap.put("appKey", Const.appKey);
        paraMap.put("goodsId", goodId);


        HttpUtil.getInstance().getAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                LoadDialog.dismiss(ShopByGoodActivity.this);
                // if (!form) dialog.dismiss();
                initData(response);
            }

            @Override
            public void onFailure(int what, String error) {
                LoadDialog.dismiss(ShopByGoodActivity.this);

                //  if (!form) dialog.dismiss();
            }
        }, HttpUtil.detail_shop_url, paraMap);
    }


    String[] bannerData;
    List<ShopList.Img> detailPic;

    private void initData(String result) {
        if (TextUtils.isEmpty(result)) return;
        ShopDeatail homeFenlei = new Gson().fromJson(result, ShopDeatail.class);
        data = homeFenlei.data;
        adapter.setShopDate(data);
        if (homeFenlei == null) return;
        if (homeFenlei.code != 0) {
            NToast.shortToast(this, homeFenlei.msg);
            return;
        }
        if (!TextUtils.isEmpty(homeFenlei.data.detailPics)) {
            detailPic = new Gson().fromJson(homeFenlei.data.detailPics, new TypeToken<List<ShopList.Img>>() {
            }.getType());
            adapter.setDetailPic(detailPic);
        }
        if (!TextUtils.isEmpty(homeFenlei.data.imgs)) {
            bannerData = homeFenlei.data.imgs.split(",");
            currentCount.setText((1) + "/" + (bannerData.length));
            banner.setImages(Arrays.asList(bannerData))
                    //.setBannerTitles(App.titles)//标
                    .setBannerAnimation(AccordionTransformer.class)//动画
                    .setIndicatorGravity(BannerConfig.RIGHT)//指示器位置
                    .setBannerStyle(BannerConfig.NUM_INDICATOR)//指示器样式
                    .isAutoPlay(false)
                    .setImageLoader(new ImageLoader() {
                        @Override
                        public void displayImage(Context context, Object path, ImageView imageView) {
                            if (((String) path).contains("http"))
                                Glide.with(context)
                                        .load(((String) path))
                                        .into(imageView);
                            else {
                                Glide.with(context)
                                        .load("http:" + ((String) path))
                                        .into(imageView);

                            }
                        }
                    }).start()
                    .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int i, float v, int i1) {

                        }

                        @Override
                        public void onPageSelected(int i) {
                            currentCount.setText((i + 1) + "/" + (bannerData.length));
                        }

                        @Override
                        public void onPageScrollStateChanged(int i) {

                        }
                    })
            ;

        }

    }


    @OnClick({R.id.back_round, R.id.back_t, R.id.shop_one, R.id.shop_two, R.id.shop_three})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_round:
                finish();
                break;
            case R.id.back_t:
                finish();
                break;
            case R.id.shop_one:
                break;
            case R.id.shop_two:
                break;
            case R.id.shop_three:
                break;
        }
    }
}
