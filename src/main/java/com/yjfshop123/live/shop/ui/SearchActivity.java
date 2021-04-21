package com.yjfshop123.live.shop.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;

import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.shop.adapter.ShopAdapter;
import com.yjfshop123.live.shop.model.ShopList;
import com.yjfshop123.live.shop.util.HttpUtil;
import com.yjfshop123.live.ui.activity.BaseAppCompatActivity;
import com.yjfshop123.live.utils.StatusBarUtil;
import com.yjfshop123.live.utils.SystemUtils;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SearchActivity extends BaseAppCompatActivity {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.search_tx)
    EditText searchTx;
    @BindView(R.id.search)
    TextView search;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.refrash)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.back)
    ImageView back;

    private GridLayoutManager mLinearLayoutManager;
    private ShopAdapter adapter;
    ZLoadingDialog dialog = new ZLoadingDialog(SearchActivity.this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_search);
        ButterKnife.bind(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.status_bar_view).getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        findViewById(R.id.status_bar_view).setLayoutParams(params);
        StatusBarUtil.StatusBarLightMode(this);
        StatusBarUtil.setStatusBarColor(this,R.color.white);
        mLinearLayoutManager = new GridLayoutManager(this, 2);
        list.setLayoutManager(mLinearLayoutManager);
        adapter = new ShopAdapter(this);
        list.setAdapter(adapter);
        dialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)//设置类型
                .setLoadingColor(getResources().getColor(R.color.color_0786fb))//颜色
                .setHintText("Loading...")
        ;
        refreshLayout.setNoMoreData(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshLayout.setNoMoreData(false);
                getData(true);

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                if (TextUtils.isEmpty(pageId)) {
                    refreshlayout.finishLoadMore(1000/*,false*/);//传入false表示加载失败

                } else {
                    getMoreData();
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(searchTx.getText().toString())) {
                    Toast.makeText(SearchActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                SystemUtils.hideKeyBord(SearchActivity.this);
                if (data != null) data.clear();
                if (adapter != null) adapter.notifyDataSetChanged();
                getData(false);
            }
        });

        searchTx.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = searchTx.getText().toString().trim();

                    //  下面就是大家的业务逻辑
                    if (TextUtils.isEmpty(searchTx.getText().toString())) {
                        Toast.makeText(SearchActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    SystemUtils.hideKeyBord(SearchActivity.this);
                    if (data != null) data.clear();
                    if (adapter != null) adapter.notifyDataSetChanged();
                    getData(false);
                    //  这里记得一定要将键盘隐藏了
                    return true;
                }
                return false;
            }
        });


    }


    public void getData(final boolean form) {
        if(!form)
        dialog.show();
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("version", "v2.1.2");
        paraMap.put("appKey", Const.appKey);
        paraMap.put("pageId", "1");
        paraMap.put("pageSize", "100");
        paraMap.put("keyWords", searchTx.getText().toString());

        HttpUtil.getInstance().getAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {

                if(!form) dialog.dismiss();
                Log.d("获取的数据", response);
                refreshLayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
                initData(response);

            }

            @Override
            public void onFailure(int what, String error) {
                if(!form)dialog.dismiss();
                refreshLayout.finishRefresh(1000, false, true);//传入false表示刷新失败
                Log.d("1获取的数据", error);

            }
        }, HttpUtil.search_shop_url, paraMap);
    }

    public List<ShopList.ShopData> data;
    String pageId = "";

    private void initData(String result) {
        if (TextUtils.isEmpty(result)) return;
        ShopList homeFenlei = new Gson().fromJson(result, ShopList.class);
        if (homeFenlei == null) return;
        if(homeFenlei.code!=0){
            NToast.shortToast(this,homeFenlei.msg);
            return;
        }
        if (homeFenlei.data == null) return;
        if (homeFenlei.data.list == null) return;
        data = homeFenlei.data.list;
        pageId = homeFenlei.data.pageId;
        adapter.setCards(data);
    }


    public void getMoreData() {
        TreeMap<String, String> paraMap = new TreeMap<>();
        paraMap.put("version", "v2.1.2");
        paraMap.put("appKey", Const.appKey);
        paraMap.put("pageId", pageId);
        paraMap.put("pageSize", "100");
        paraMap.put("keyWords", searchTx.getText().toString());
        HttpUtil.getInstance().getAsynHttp(1, new HttpUtil.HttpCallBack() {
            @Override
            public void onResponse(int what, String response) {
                Log.d("获取更多的数据", response);
                refreshLayout.finishLoadMore(1000/*,false*/);//传入false表示加载失败
                initMoreData(response);
            }

            @Override
            public void onFailure(int what, String error) {
                Log.d("获取更多的数据", error);
                refreshLayout.finishLoadMore(1000, false, true);//传入false表示加载失败
            }
        }, HttpUtil.search_shop_url, paraMap);
    }

    private void initMoreData(String result) {
        if (TextUtils.isEmpty(result)) return;
        ShopList homeFenlei = new Gson().fromJson(result, ShopList.class);
        if (homeFenlei == null) return;
        if (homeFenlei.data == null) return;
        if (homeFenlei.data.list == null) return;
        data.addAll(homeFenlei.data.list);
        if (homeFenlei.data.list.size() < 100) {
            refreshLayout.setNoMoreData(true);
        }
        pageId = homeFenlei.data.pageId;
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }
}