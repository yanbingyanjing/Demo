package com.yjfshop123.live.video.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.NumberProgressBar;
import com.yjfshop123.live.live.live.common.widget.gift.view.AbsViewHolder;

public class VideoProcessViewHolder extends AbsViewHolder implements View.OnClickListener {

    private TextView mTitle;
    private String mTitleString;
    private NumberProgressBar mProgressBar;
    private ActionListener mActionListener;

    public VideoProcessViewHolder(Context context, ViewGroup parentView, String title) {
        super(context, parentView, title);
    }

    @Override
    protected void processArguments(Object... args) {
        mTitleString = (String) args[0];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_process;
    }

    @Override
    public void init() {
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText(mTitleString);
        mProgressBar = (NumberProgressBar) findViewById(R.id.progressbar);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            if (mActionListener != null) {
                mActionListener.onCancelProcessClick();
            }

        }
    }

    public void setProgress(int progress) {
        if (mProgressBar != null) {
            mProgressBar.setProgress(progress);
        }
    }

    public interface ActionListener {
        void onCancelProcessClick();
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }
}
