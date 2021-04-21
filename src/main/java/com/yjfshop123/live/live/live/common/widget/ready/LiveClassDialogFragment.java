package com.yjfshop123.live.live.live.common.widget.ready;

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
import com.yjfshop123.live.live.response.ChannelListResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.CommonCallback;
import com.yjfshop123.live.utils.CommonUtils;

public class LiveClassDialogFragment extends AbsDialogFragment implements OnItemClickListener<ChannelListResponse.ChannelListBean> {

    private RecyclerView mRecyclerView;
    private LiveClassAdapter mAdapter;
    private CommonCallback<ChannelListResponse.ChannelListBean> mCallback;
    private int classId;
    private ChannelListResponse channelListResponse;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_room_type;
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
        classId = bundle.getInt(TCConstants.CLASS_ID, -1);
        mAdapter = new LiveClassAdapter(mContext);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        if (channelListResponse == null){
            OKHttpUtils.getInstance().getRequest("app/live/getChannelList", "", new RequestCallback() {
                @Override
                public void onError(int errCode, String errInfo) {
                }
                @Override
                public void onSuccess(String result) {
                    try{
                        channelListResponse = JsonMananger.jsonToBean(result, ChannelListResponse.class);
                        mAdapter.addData(channelListResponse, classId);
                    }catch (Exception e){
                    }
                }
            });
        }else {
            mAdapter.addData(channelListResponse, classId);
        }

    }

    @Override
    public void onItemClick(ChannelListResponse.ChannelListBean bean, int position) {
        dismiss();
        if (mCallback != null) {
            mCallback.callback(bean);
        }
    }

    public void setCallback(CommonCallback<ChannelListResponse.ChannelListBean> callback) {
        mCallback = callback;
    }

    @Override
    public void onDestroy() {
        mCallback = null;
        super.onDestroy();
    }
}