package com.yjfshop123.live.video.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.CommonCallback;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.video.adapter.VideoClassAdapter;
import com.yjfshop123.live.video.bean.CircleListBean;

public class VideoClassDialogFragment extends AbsDialogFragment implements OnItemClickListener<CircleListBean.ListBean> {

    private RecyclerView mRecyclerView;
    private VideoClassAdapter mAdapter;
    private CommonCallback<CircleListBean.ListBean> mCallback;
    private String classId = "";
    private CircleListBean circleListBean;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_video_type;
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
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = CommonUtils.dip2px(mContext, 280);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        classId = bundle.getString(TCConstants.CLASS_ID);
        mAdapter = new VideoClassAdapter(mContext);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        if (circleListBean == null){
            OKHttpUtils.getInstance().getRequest("app/shortVideo/getCircleList", "", new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                    NToast.shortToast(mContext, errInfo);
                }
                @Override
                public void onSuccess(String result) {
                    try{
                        circleListBean = JsonMananger.jsonToBean(result, CircleListBean.class);
                        mAdapter.addData(circleListBean, classId);
                    }catch (Exception e){
                    }
                }
            });
        }else {
            mAdapter.addData(circleListBean, classId);
        }

    }

    @Override
    public void onItemClick(CircleListBean.ListBean bean, int position) {
        dismiss();
        if (mCallback != null) {
            mCallback.callback(bean);
        }
    }

    public void setCallback(CommonCallback<CircleListBean.ListBean> callback) {
        mCallback = callback;
    }

    @Override
    public void onDestroy() {
        mCallback = null;
        super.onDestroy();
    }
}
