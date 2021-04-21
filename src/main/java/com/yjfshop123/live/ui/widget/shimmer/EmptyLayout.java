package com.yjfshop123.live.ui.widget.shimmer;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmptyLayout extends FrameLayout {

    private static final int STATUS_HIDE = 1000;//隐藏
    public static final int STATUS_LOADING = 1001;//加载
    public static final int STATUS_NO_NET = 1002;//无网络
    public static final int STATUS_NO_DATA = 1003;//无数据

    private Context mContext;
    private OnRetryListener mOnRetryListener;
    private int mEmptyStatus = STATUS_LOADING;

    @BindView(R.id.empty_fl_layout)
    FrameLayout mEmptyLayout;

    @BindView(R.id.rl_empty_container)
    View mRlEmptyContainer;

    @BindView(R.id.tv_net_error)
    TextView mTvEmptyMessage;

    @BindView(R.id.iv_net_error)
    ImageView mIvEmptyIcon;

    @BindView(R.id.empty_loading)
    ImageView mEmptyLoading;

    public EmptyLayout(Context context) {
        this(context, null);
    }

    public EmptyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.layout_empty_loading, this);
        ButterKnife.bind(this);
       // mEmptyLayout.setBackgroundColor(getResources().getColor(R.color.color_theme));
        _switchEmptyView();
    }

    /**
     * 隐藏视图
     */
    public void hide() {
        mEmptyStatus = STATUS_HIDE;
        _switchEmptyView();
    }

    /**
     * 设置状态
     *
     * @param emptyStatus
     */
    public void setEmptyStatus(@EmptyStatus int emptyStatus) {
        mEmptyStatus = emptyStatus;
        _switchEmptyView();
    }

    /**
     * 获取状态
     *
     * @return  状态
     */
    public int getEmptyStatus() {
        return mEmptyStatus;
    }

    public void setEmptyMessage(String msg) {
        mTvEmptyMessage.setText(msg);
    }

    public void hideErrorIcon() {
        mTvEmptyMessage.setCompoundDrawables(null, null, null, null);
    }

    private void _switchEmptyView() {
        switch (mEmptyStatus) {
            case STATUS_LOADING:
                setVisibility(VISIBLE);
                mRlEmptyContainer.setVisibility(GONE);
                startAnimation();
                break;
            case STATUS_NO_DATA:
                setVisibility(VISIBLE);
                stopAnimation();
                mRlEmptyContainer.setVisibility(VISIBLE);
                setEmptyMessage(mContext.getString(R.string.nodata_2));
                mIvEmptyIcon.setImageResource(R.drawable.nodate);
                break;
            case STATUS_NO_NET:
                setVisibility(VISIBLE);
                stopAnimation();
                mRlEmptyContainer.setVisibility(VISIBLE);
                setEmptyMessage(mContext.getString(R.string.nonet_1));
                mIvEmptyIcon.setImageResource(R.drawable.nowifi);
                break;
            case STATUS_HIDE:
                setVisibility(GONE);
                stopAnimation();
                break;
        }
    }

    @OnClick(R.id.rl_empty_container)
    public void onClick() {
        if (mOnRetryListener != null) {
            mOnRetryListener.onRetry();
        }
    }

    public void setRetryListener(OnRetryListener retryListener) {
        this.mOnRetryListener = retryListener;
    }

    public interface OnRetryListener {
        void onRetry();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_LOADING, STATUS_NO_NET, STATUS_NO_DATA})
    public @interface EmptyStatus{}

    private AnimationDrawable frameAnimation;

    private void startAnimation(){
        stopAnimation();

        mEmptyLoading.setVisibility(VISIBLE);
        frameAnimation = (AnimationDrawable) mEmptyLoading.getBackground();
        frameAnimation.start();
    }

    private void stopAnimation(){
        if (frameAnimation != null){
            frameAnimation.stop();
            frameAnimation = null;
        }
        mEmptyLoading.setVisibility(GONE);
    }
}

