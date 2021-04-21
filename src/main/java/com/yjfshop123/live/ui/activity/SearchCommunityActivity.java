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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.CommunityDoLike;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.SearchDynamicResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.adapter.SearchCommunityAdapter;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SearchCommunityActivity extends BaseActivity{

    private EditText searchEt;
    private ImageView searchDele;

    private List<SearchDynamicResponse.ListBean> mList = new ArrayList<>();


    private RecyclerView shimmerRecycler;
    private VerticalSwipeRefreshLayout mSwipeRefresh;
    private LinearLayout searchHintLl;

    private SearchCommunityAdapter searchAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private String content;
    private int page = 1;

    private boolean isLoadingMore = false;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isShow = true;
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_community_search);
        setHeadVisibility(View.GONE);

        type = getIntent().getStringExtra("TYPE");

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
                    searchContent();
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

        searchAdapter = new SearchCommunityAdapter(mContext);
        shimmerRecycler.setAdapter(searchAdapter);
        initSwipeRefresh();

        searchAdapter.setOnItemClickListener(new SearchCommunityAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mList.get(position).getVideo_list().size() > 0){
                    //视频
                    if (type.equals("short_video")){
                        ActivityUtils.startGSYVideo(mContext, 2, String.valueOf(mList.get(position).getDynamic_id()), "app/shortVideo/getPopularVideoList");
                    }else {
                        ActivityUtils.startGSYVideo(mContext, 1, String.valueOf(mList.get(position).getDynamic_id()), "app/forum/videoDynamic");
                    }
                }else {
                    //图片
                    Intent intent = new Intent(mContext, CommunityReplyDetailActivity.class);
                    intent.putExtra("dynamic_id", mList.get(position).getDynamic_id());
                    startActivity(intent);
                }
                overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }

            @Override
            public void onPraiseItemClick(View view, int position) {
                if (mList.get(position).getIs_like() == 0){
                    //点赞 异步走接口
                    if (type.equals("short_video")){
                        CommunityDoLike.getInstance().shortVideoDoLike(mList.get(position).getDynamic_id(), false);
                    }else {
                        CommunityDoLike.getInstance().dynamicDoLike(mList.get(position).getDynamic_id(), false);
                    }

                    mList.get(position).setIs_like(1);
                    mList.get(position).setLike_num(mList.get(position).getLike_num() + 1);
                }else {
                    //取消点赞 异步走接口
                    if (type.equals("short_video")){
                        CommunityDoLike.getInstance().shortVideoUndoLike(mList.get(position).getDynamic_id(), false);
                    }else {
                        CommunityDoLike.getInstance().dynamicUndoLike(mList.get(position).getDynamic_id(), false);
                    }

                    mList.get(position).setIs_like(0);
                    mList.get(position).setLike_num(mList.get(position).getLike_num() - 1);
                }

                searchAdapter.notifyDataSetChanged();
            }
        });

        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        searchContent();
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
                    searchContent();
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

    private void searchContent(){
        String url;
        String body = "";
        if (type.equals("short_video")){
            url = "app/shortvideo/searchDynamic";
            try {
                body = new JsonBuilder()
                        .put("title", content)
                        .put("page", page)
                        .build();
            } catch (JSONException e) {
            }
        }else {
            url = "app/forum/searchDynamic";
            try {
                body = new JsonBuilder()
                        .put("keyword", content)
                        .put("page", page)
                        .build();
            } catch (JSONException e) {
            }
        }

        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
                if (page == 1) {
                    searchHintLl.setVisibility(View.VISIBLE);
                }
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                try {
                    SearchDynamicResponse response = JsonMananger.jsonToBean(result, SearchDynamicResponse.class);
                    isLoadingMore = false;
                    if (page == 1) {
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                    }

                    mList.addAll(response.getList());
                    searchAdapter.setCards(mList, content);

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