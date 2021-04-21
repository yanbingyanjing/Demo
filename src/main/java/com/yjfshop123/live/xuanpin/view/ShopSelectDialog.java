package com.yjfshop123.live.xuanpin.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cjt2325.cameralibrary.util.ScreenUtils;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.model.XuanPInResopnse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.xuanpin.adapter.SelectShopAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

//用于视频发布的时候的选择
public class ShopSelectDialog extends AbsDialogFragment implements View.OnClickListener {


    private String title;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_select_shop;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog;
    }


    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = ScreenUtils.getScreenHeight(getContext()) * 2 / 3;

        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    RecyclerView list;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list = mRootView.findViewById(R.id.list);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        list.setLayoutManager(mLinearLayoutManager);
        priceAdapter = new SelectShopAdapter(mContext);
        priceAdapter.setOnclick(onclick);
        list.setAdapter(priceAdapter);
        list.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        loadData(false);
                    }
                }
            }
        });
        initData();
    }

    String medalId = "";
    SelectShopAdapter priceAdapter;

    private void initData() {
        loadData(true);
    }


    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        dismiss();
        int i = v.getId();
        if (i == R.id.confir) {

            return;

        }
    }

    private String user_id;

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    List<XuanPInResopnse.ItemData> data = new ArrayList<>();
    boolean isLoadingMore = false;
    int page = 1;

    private void loadData(final boolean isFirst) {
        String body = "";

        try {
            if (TextUtils.isEmpty(user_id)) {
                body = new JsonBuilder()
                        .put("page", page)
                        .build();
            }else {
                body = new JsonBuilder()
                        .put("page", page)
                        .put("uid", user_id)
                        .build();
            }
        } catch (JSONException e) {
        }

        if (isFirst) {
            LoadDialog.show(mContext);
        }
        OKHttpUtils.getInstance().getRequest("app/medal/goods", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (isFirst) {
                    LoadDialog.dismiss(mContext);
                    NToast.shortToast(mContext, errInfo);
                    dismiss();
                }
                isLoadingMore = false;

                if (page > 1) {
                    page--;
                }
            }

            @Override
            public void onSuccess(String result) {
                if (isFirst) {
                    LoadDialog.dismiss(mContext);
                }
                XuanPInResopnse datas = new Gson().fromJson(result, XuanPInResopnse.class);
                isLoadingMore = false;
                if (page == 1 && datas.list != null && datas.list.size() > 0) {
                    data.clear();
                } else {
                    if (datas.list == null || datas.list.size() == 0) {
                        page--;
                        return;
                    }

                }
                data.addAll(datas.list);
                priceAdapter.setCards(data);
                priceAdapter.notifyDataSetChanged();

            }
        });
    }

    public class ResultData {
        public String order;
    }

    Onclick onclick;

    public void setOnclick(Onclick onclick) {
        this.onclick = onclick;
    }

    public interface Onclick {
        void OnClick(XuanPInResopnse.ItemData itemData);
    }

}
