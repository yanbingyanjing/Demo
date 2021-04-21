package com.yjfshop123.live.ctc.weituo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.ctc.adapter.WeiTuoMangerAdapter;
import com.yjfshop123.live.ctc.adapter.WeituoAdapter;
import com.yjfshop123.live.ctc.view.BuyEggDialog;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.CtcListResopnse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.widget.ShowCommonDialog;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
//我的委托列表
public class GuaDanManagerFragment extends BaseFragment {
    @BindView(R.id.list)
    RecyclerView shimmerRecycler;
    Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_order_otc;
    }


    LinearLayoutManager mLinearLayoutManager;
    WeiTuoMangerAdapter xunZhangAdapter;

    @Override
    protected void initAction() {

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        xunZhangAdapter = new WeiTuoMangerAdapter(mContext);
        xunZhangAdapter.setListener(new WeiTuoMangerAdapter.OnItemClickListener() {

            @Override
            public void onclick(CtcListResopnse.CtcListData data) {
                BuyEggDialog dialogFragment = new BuyEggDialog();
                dialogFragment.setData(data);
                if (getFragmentManager() != null)
                    dialogFragment.show(getFragmentManager(), "BuyEggDialog");
            }

            @Override
            public void onCancelWeituoclick(final CtcListResopnse.CtcListData data, final int position) {
                dialogFragment.setTitle("确定取消该委托单？");
                dialogFragment.setOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelWeituo(data.order, position);
                    }
                });
                dialogFragment.show(getFragmentManager(),"ShowCommonDialog");
            }
        });
        shimmerRecycler.setAdapter(xunZhangAdapter);
        showNotData();
        showLoading();
        getData(false);
    }

    ShowCommonDialog dialogFragment = new ShowCommonDialog();

    boolean isLoadingMore = false;
    int page = 1;

    @Override
    protected void initEvent() {
        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem;
                int totalItemCount;
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                totalItemCount = mLinearLayoutManager.getItemCount();

                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        getMoreData();
                    }
                }
            }
        });
    }

    public void cancelWeituo(String order, final int index) {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("order", order)
                    .build();
        } catch (JSONException e) {
        }
        LoadDialog.show(getContext());

        OKHttpUtils.getInstance().getRequest("app/trade/c2cCancelSell", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(getContext());
                NToast.shortToast(getContext(), errInfo);

            }

            @Override
            public void onSuccess(String result) {
                LoadDialog.dismiss(getContext());
                NToast.shortToast(getContext(), "委托单取消成功");
                //list.remove(index);
                list.get(index).status=3;
                list.get(index).desc="已取消";
                xunZhangAdapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * 获取数据
     */
    public void getData(final boolean isFromRefresh) {
        page = 1;
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", 1)
                    .build();
        } catch (JSONException e) {
        }
        if (!isFromRefresh) {
            showLoading();
        }

        OKHttpUtils.getInstance().getRequest("app/trade/sellEggSelfList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                isLoadingMore = false;

                if (mEmptyLayout != null && mEmptyLayout.getVisibility() == View.VISIBLE) {
                    showNotData();
                } else hideLoading();
                //测试数据
                //loadData(testData);
            }

            @Override
            public void onSuccess(String result) {
                hideLoading();
                isLoadingMore = false;
                //模拟数据
                // loadData(testData);
                loadData(result);
            }
        });

    }

    List<CtcListResopnse.CtcListData> list;

    public void loadData(String result) {
        hideLoading();
        if (TextUtils.isEmpty(result)) return;
        CtcListResopnse data = new Gson().fromJson(result, CtcListResopnse.class);
        if (data.list == null) return;
        if (data.list.size() > 0) {
            page = 1;
        } else showNotData();
        this.list = data.list;
        xunZhangAdapter.setCards(this.list);

    }

    /**
     * 获取数据
     */
    public void getMoreData() {
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }

        OKHttpUtils.getInstance().getRequest("app/trade/sellEggList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                page--;
                //模拟数据
                isLoadingMore = false;
            }

            @Override
            public void onSuccess(String result) {
                loadMoreData(result);
                isLoadingMore = false;
                //模拟数据
                //loadMoreData(testData);

            }
        });

    }

    public void loadMoreData(String result) {
        CtcListResopnse data = new Gson().fromJson(result, CtcListResopnse.class);
        if (data.list == null || data.list.size() == 0) {
            page--;
            return;
        }
        this.list.addAll(data.list);
        xunZhangAdapter.notifyDataSetChanged();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        getData(isRefresh);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
