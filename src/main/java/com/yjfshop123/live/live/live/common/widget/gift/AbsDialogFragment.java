package com.yjfshop123.live.live.live.common.widget.gift;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.yjfshop123.live.live.live.common.widget.gift.utils.ClickUtil;

public abstract class AbsDialogFragment extends DialogFragment {

    protected Context mContext;
    protected View mRootView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = LayoutInflater.from(mContext).inflate(getLayoutId(), null);
        Dialog dialog = new Dialog(mContext, getDialogStyle());
        dialog.setContentView(mRootView);
        dialog.setCancelable(canCancel());
        dialog.setCanceledOnTouchOutside(canCancel());
        setWindowAttributes(dialog.getWindow());
        return dialog;
    }


    protected abstract int getLayoutId();

    protected abstract int getDialogStyle();

    protected abstract boolean canCancel();

    protected abstract void setWindowAttributes(Window window);

    protected View findViewById(int id) {
        if (mRootView != null) {
            return mRootView.findViewById(id);
        }
        return null;
    }

    protected boolean canClick() {
        return ClickUtil.canClick();
    }


}
