package com.yjfshop123.live.live.live.common.widget.function;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.utils.CommonUtils;

public class LiveFunctionDialogFragment extends AbsDialogFragment implements OnItemClickListener<Integer> {

    private LiveFunctionClickListener mFunctionClickListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_function;
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
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        params.y = CommonUtils.dip2px(mContext, 50);
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView recyclerView = mRootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 5, GridLayoutManager.VERTICAL, false));
        boolean pureAudio = false;
        if (mContext instanceof TCLiveBasePublisherActivity){
            pureAudio = ((TCLiveBasePublisherActivity) mContext).pureAudio;
        }
        LiveFunctionAdapter adapter = new LiveFunctionAdapter(mContext, pureAudio);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    public void setFunctionClickListener(LiveFunctionClickListener functionClickListener) {
        mFunctionClickListener = functionClickListener;
    }

    @Override
    public void onItemClick(Integer bean, int position) {
        dismiss();
        if (mFunctionClickListener != null) {
            mFunctionClickListener.onClick(bean);
        }
    }

    @Override
    public void onDestroy() {
        mFunctionClickListener = null;
        ((TCLiveBasePublisherActivity) mContext).setBtnFunctionDark();
        super.onDestroy();
    }
}
