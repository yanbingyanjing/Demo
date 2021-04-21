package com.yjfshop123.live.live.live.common.widget.prompt;

import android.content.Context;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LivePromptPresenter implements PromptViewHolder.ActionListener {

    private Context mContext;
    private ViewGroup mDanmuContainer;
    private boolean[] mLines;//弹幕的轨道
    private List<PromptViewHolder> mList;
    private ConcurrentLinkedQueue<LivePromptBean> mQueue;

    private boolean IS_RELEASE = false;

    public LivePromptPresenter(Context context, ViewGroup danmuContainer) {
        mContext = context;
        mDanmuContainer = danmuContainer;
        mLines = new boolean[]{true/*, true, true*/};
        mList = new LinkedList<>();
        mQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * 显示弹幕的方法
     */
    public void showDanmu(LivePromptBean bean) {
        if (IS_RELEASE){
            return;
        }

        int lineNum = -1;
        for (int i = 0; i < mLines.length; i++) {
            if (mLines[i]) {
                mLines[i] = false;
                lineNum = i;
                break;
            }
        }
        if (lineNum == -1) {
            mQueue.offer(bean);
            return;
        }
        PromptViewHolder danmuHolder = null;
        for (PromptViewHolder holder : mList) {
            if (holder.isIdle()) {
                holder.setIdle(false);
                danmuHolder = holder;
                break;
            }
        }
        if (danmuHolder == null) {
            danmuHolder = new PromptViewHolder(mContext, mDanmuContainer);
            danmuHolder.setActionListener(this);
            mList.add(danmuHolder);
        }
        danmuHolder.show(bean, lineNum);
    }

    /**
     * 获取下一个弹幕
     */
    private void getNextDanmu() {
        LivePromptBean bean = mQueue.poll();
        if (bean != null) {
            showDanmu(bean);
        }
    }

    public void reset() {
        if (mLines != null) {
            for (boolean line : mLines) {
                line = true;
            }
        }
    }

    public void release() {
        IS_RELEASE = true;
        if (mList != null) {
            for (PromptViewHolder vh : mList) {
                vh.release();
            }
            mList.clear();
        }
        if (mQueue != null) {
            mQueue.clear();
        }
    }

    @Override
    public void onCanNext(int lineNum) {
        mLines[lineNum] = true;
        getNextDanmu();
    }

    @Override
    public void onAnimEnd(PromptViewHolder vh) {
        if (IS_RELEASE){
            return;
        }
        if (mQueue.size() == 0) {
            if (vh != null) {
                vh.release();
                if (mList != null) {
                    mList.remove(vh);
                }
            }
        }
    }

}

