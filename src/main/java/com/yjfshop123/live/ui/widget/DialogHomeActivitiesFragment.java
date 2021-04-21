package com.yjfshop123.live.ui.widget;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.MainStartChooseCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.model.UserStatusResponse;
import com.yjfshop123.live.ui.activity.HomeActivity;
import com.yjfshop123.live.ui.adapter.AdditionAdapter;
import com.yjfshop123.live.ui.widget.adapter.DialogHomeActivitysAdapter;
import com.yjfshop123.live.utils.CommonUtils;

import java.util.List;

public class DialogHomeActivitiesFragment extends AbsDialogFragment implements View.OnClickListener {

    private MainStartChooseCallback mCallback;
    private RecyclerView list;
    private DialogHomeActivitysAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_home_activities;
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
        params.width = CommonUtils.getScreenWidth(mContext) - CommonUtils.dip2px(mContext, 40);
        params.height = params.width * 725 / 662+ CommonUtils.dip2px(mContext, 56);
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
        list = mRootView.findViewById(R.id.list);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        list.setLayoutManager(mLinearLayoutManager);

        adapter = new DialogHomeActivitysAdapter(mContext);
        adapter.setHome(homeActivity);
        list.setAdapter(adapter);
        Log.d("2活动数据", new Gson().toJson(listData));
        adapter.setCards(listData);
        mRootView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                dismiss();
            }
        });


    }

    private List<UserStatusResponse.home_activitiesData> listData;

    public void setCards(List<UserStatusResponse.home_activitiesData> listData) {

        if (listData == null) {
            return;
        }

        this.listData = listData;
    }

    @Override
    public void onClick(View v) {

    }

}
