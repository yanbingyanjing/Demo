package com.yjfshop123.live.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.SearchUserResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.adapter.SearchAdapter;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.SystemUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SearchActivity extends BaseActivity{

    private EditText searchEt;
    private ImageView searchDele;

    private List<SearchUserResponse.ListBean> mList = new ArrayList<>();


    private RecyclerView shimmerRecycler;
    private VerticalSwipeRefreshLayout mSwipeRefresh;
    private LinearLayout searchHintLl;

    private SearchAdapter searchAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private String content;
    private int page = 1;
    private int pageSize = 1;

    private boolean isLoadingMore = false;
private View top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_search);
        setHeadVisibility(View.GONE);
top=findViewById(R.id.top);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)  top.getLayoutParams();
        //获取当前控件的布局对象
        params.height = SystemUtils.getStatusBarHeight(this);//设置当前控件布局的高度
        params.width = MATCH_PARENT;
        top.setLayoutParams(params);
        searchDele = findViewById(R.id.search_dele);
        searchEt = findViewById(R.id.search_et);

        findViewById(R.id.search_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKeyboard();
                finish();
            }
        });

        searchDele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.setText("");
                searchDele.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.search_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(content)){
                    hintKeyboard();
                    page = 1;
                    getSearchUser();
                }
            }
        });

        searchEt.addTextChangedListener(mSearchEtWatcher);

        mSwipeRefresh = findViewById(R.id.search_swipe_refresh);
        shimmerRecycler = findViewById(R.id.search_shimmer_recycler_view);
        searchHintLl = findViewById(R.id.search_hint_ll);

//        shimmerRecycler.addItemDecoration(new PaddingItemDecoration(mContext, 3));
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        searchAdapter = new SearchAdapter();
        shimmerRecycler.setAdapter(searchAdapter);
        initSwipeRefresh();

        searchAdapter.setOnItemClickListener(new SearchAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                ActivityUtils.startUserHome(mContext, String.valueOf(mList.get(postion).getUser_id()));
                overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });

        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (pageSize > page) {
                    int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                    int totalItemCount = mLinearLayoutManager.getItemCount();
                    //表示剩下4个item自动加载，各位自由选择
                    // dy>0 表示向下滑动
                    if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                        if (!isLoadingMore) {
                            isLoadingMore = true;
                            page++;
                            getSearchUser();
                        }
                    }
                }
            }
        });
    }

    public void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (TextUtils.isEmpty(content)){
                        NToast.shortToast(mContext, "请输入搜索内容~");
                        finishRefresh();
                        return;
                    }
                    hintKeyboard();
                    page = 1;
                    getSearchUser();
                }
            });
        }
    }

    private TextWatcher mSearchEtWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            content = charSequence.toString().trim();
            if (content.length() > 0) {
                searchDele.setVisibility(View.VISIBLE);
            } else {
                searchDele.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void getSearchUser(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("keyword", content)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/index/searchUser", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                if (page == 1) {
                    searchHintLl.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                try {
                    SearchUserResponse response = JsonMananger.jsonToBean(result, SearchUserResponse.class);
                    pageSize = response.getTotal_page();
                    isLoadingMore = false;

                    if (page == 1 && mList.size() > 0) {
                        mList.clear();
                    }

                    mList.addAll(response.getList());
                    searchAdapter.setCards(mList);
                    searchAdapter.notifyDataSetChanged();

                    if (page == 1 && mList.size() == 0) {
                        searchHintLl.setVisibility(View.VISIBLE);
                    }else {
                        searchHintLl.setVisibility(View.GONE);
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    if (page == 1) {
                        searchHintLl.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void hintKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
