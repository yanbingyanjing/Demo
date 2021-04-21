package com.yjfshop123.live.live.live.common.widget.chat;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.live.push.camera.TCLivePublisherActivity;
import com.yjfshop123.live.message.db.IMConversationDB;
import com.yjfshop123.live.utils.CommonUtils;

public class LiveChatListDialogFragment extends AbsDialogFragment {

    private ChatListViewHolder mChatListViewHolder;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_empty;
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
        params.height = CommonUtils.dip2px(mContext, 300);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mChatListViewHolder = new ChatListViewHolder(mContext, (ViewGroup) mRootView);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String liveUid = bundle.getString("mLiveUid");
            mChatListViewHolder.setLiveUid(liveUid);
        }
        mChatListViewHolder.setActionListener(new ChatListViewHolder.ActionListener() {
            @Override
            public void onCloseClick() {
                dismiss();
            }

            @Override
            public void onItemClick(IMConversationDB mIMConversationDB) {
                ((TCLivePublisherActivity) mContext).openChatRoomWindow(mIMConversationDB);
                dismiss();
            }
        });
        mChatListViewHolder.addToParent();
        mChatListViewHolder.loadData();
    }

    @Override
    public void onDestroy() {
        if (mChatListViewHolder != null) {
            mChatListViewHolder.release();
        }
        super.onDestroy();
    }
}
