package com.yjfshop123.live.live.live.list;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.widget.gift.utils.LiveTextRender;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class TCChatMsgListAdapter extends BaseAdapter implements AbsListView.OnScrollListener, View.OnClickListener {

    private static String TAG = TCChatMsgListAdapter.class.getSimpleName();
    private static final int ITEMCOUNT = 12;
    private List<TCChatEntity> listMessage = null;
    private LayoutInflater inflater;
    private LinearLayout layout;
    private int mTotalHeight;
    public static final int TYPE_TEXT_SEND = 0;
    public static final int TYPE_TEXT_RECV = 1;
    private Context context;
    private ListView mListView;
    private ArrayList<TCChatEntity> myArray = new ArrayList<>();

//    private boolean  mBLiveAnimator = false;

    class AnimatorInfo {
        long createTime;

        public AnimatorInfo(long uTime) {
            createTime = uTime;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }

    private static final int MAXANIMATORCOUNT = 8;
    private static final int MAXLISTVIEWHEIGHT = 650;
    private static final int ANIMATORDURING = 8000;
    private static final int MAXITEMCOUNT = 50;
    private LinkedList<AnimatorSet> mAnimatorSetList;
    private LinkedList<AnimatorInfo> mAnimatorInfoList;
    private boolean mScrolling = false;
//    private boolean mCreateAnimator = false;

    private TextView imNewMsgTv;

    public TCChatMsgListAdapter(Context context, ListView listview, List<TCChatEntity> objects, View imNewMsgTv) {
        this.context = context;
        mListView = listview;
        inflater = LayoutInflater.from(context);
        this.listMessage = objects;

        this.imNewMsgTv = (TextView) imNewMsgTv;

        mAnimatorSetList = new LinkedList<>();
        mAnimatorInfoList = new LinkedList<>();

        mListView.setOnScrollListener(this);
        this.imNewMsgTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // ?????????????????????
        mListView.post(new Runnable() {
            @Override
            public void run() {
                mListView.setSelection(mListView.getCount() - 1);
                mScrolling = false;
                imNewMsgTv.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getCount() {
        return listMessage.size();
    }

    @Override
    public Object getItem(int position) {
        return listMessage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.listview_msg_item, null);
            holder.sendContext = (TextView) convertView.findViewById(R.id.sendcontext);
            convertView.setTag(R.id.tag_first, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.tag_first);
        }

        TCChatEntity item = listMessage.get(position);

//        if (mCreateAnimator && mBLiveAnimator) {
//            playViewAnimator(convertView, position, item);
//        }

        holder.sendContext.setTextColor(context.getResources().getColor(R.color.colorTextWhite));
        switch (item.getType()) {
            case TCConstants.IMCMD_ENTER_LIVE://??????????????????
            case TCConstants.IMCMD_EXIT_LIVE://??????????????????
            case TCConstants.IMCMD_PRAISE://????????????
            case TCConstants.IMCMD_PAILN_TEXT://????????????
            case TCConstants.IMCMD_DANMU://????????????
            case TCConstants.IMCMD_GIFT://????????????
            case TCConstants.IMCMD_GUARDIAN://????????????
                String identityType = item.getIdentityType();
                if (identityType == null){
                    LiveTextRender.renderOrdinaryMsg(holder.sendContext, context, "0", "0", item.getUserName(), "0", item.getContent(), "0");
                }else if (identityType.equals("1")){
                    LiveTextRender.renderHostMsg(holder.sendContext, context, "??????", item.getContent());
                }else {
                    LiveTextRender.renderOrdinaryMsg(holder.sendContext, context, item.getIsVip(), item.getIsGuard(), item.getUserName(), item.getUser_level(), item.getContent(), identityType);
                }
                break;
            case TCConstants.IMCMD_SYSTEM://?????? ?????? ??????
                String content = item.getContent();
                if (TextUtils.isEmpty(content)){
                    content = context.getString(R.string.msg_2);
                }
                LiveTextRender.renderSystemMsg(holder.sendContext, context, "", content);

//                String userName = context.getString(R.string.msg_1);
//                String content = context.getString(R.string.msg_2);
//                LiveTextRender.renderSystemMsg(holder.sendContext, context, userName, content);
                break;
            default:
                break;
        }

//        ?????????????????????????????????????????????????????????
//        holder.sendContext.fixViewWidth(mListView.getWidth());
        return convertView;
    }


    static class ViewHolder {
        public TextView sendContext;

    }

    /**
     * ??????View????????????
     *
     * @param itemView ??????????????????View
     */
    private void stopViewAnimator(View itemView) {
        AnimatorSet aniSet = (AnimatorSet) itemView.getTag(R.id.tag_second);
        if (null != aniSet) {
            aniSet.cancel();
            mAnimatorSetList.remove(aniSet);
        }
    }

    /**
     * ??????View????????????
     *
     * @param itemView   ????????????View
     * @param startAlpha ???????????????
     * @param duringTime ????????????
     */
    private void playViewAnimator(View itemView, float startAlpha, long duringTime) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(itemView, "alpha", startAlpha, 0f);
        AnimatorSet aniSet = new AnimatorSet();
        aniSet.setDuration(duringTime);
        aniSet.play(animator);
        aniSet.start();
        mAnimatorSetList.add(aniSet);
        itemView.setTag(R.id.tag_second, aniSet);
    }

    /**
     * ??????????????????
     *
     * @param pos ??????
     * @param view ????????????View
     */
    public void playDisappearAnimator(int pos, View view) {
        int firstVisable = mListView.getFirstVisiblePosition();
        if (firstVisable <= pos) {
            playViewAnimator(view, 1f, ANIMATORDURING);
        } else {
            Log.d(TAG, "playDisappearAnimator->unexpect pos: " + pos + "/" + firstVisable);
        }
    }

    /**
     * ????????????????????????
     *
     * @param itemView ????????????View
     * @param position ??????
     * @param item
     */
    private void continueAnimator(View itemView, int position, final TCChatEntity item) {
        int animatorIdx = listMessage.size() - 1 - position;

        if (animatorIdx < MAXANIMATORCOUNT) {
            float startAlpha = 1f;
            long during = ANIMATORDURING;

            stopViewAnimator(itemView);

            // ????????????
            if (position < mAnimatorInfoList.size()) {
                AnimatorInfo info = mAnimatorInfoList.get(position);
                long time = info.getCreateTime();  //  ????????????????????????????????????
                during = during - (System.currentTimeMillis() - time);     // ????????????????????????
                startAlpha = 1f * during / ANIMATORDURING;                    // ???????????????????????????
                if (during < 0) {   // ??????????????????0????????????????????????0?????????
                    itemView.setAlpha(0f);
                    Log.v(TAG, "continueAnimator->already end animator:" + position + "/" + item.getContent() + "-" + during);
                    return;
                }
            }

            // ???????????????????????????
            Log.v(TAG, "continueAnimator->pos: " + position + "/" + listMessage.size() + ", alpha:" + startAlpha + ", dur:" + during);
            playViewAnimator(itemView, startAlpha, during);
        } else {
            Log.v(TAG, "continueAnimator->ignore pos: " + position + "/" + listMessage.size());
        }
    }

    /**
     * ??????????????????
     */
    private void playDisappearAnimator() {
        for (int i = 0; i < mListView.getChildCount(); i++) {
            View itemView = mListView.getChildAt(i);
            if (null == itemView) {
                Log.w(TAG, "playDisappearAnimator->view not found: " + i + "/" + mListView.getCount());
                break;
            }

            // ????????????????????????
            int position = mListView.getFirstVisiblePosition() + i;
            if (position < mAnimatorInfoList.size()) {
                mAnimatorInfoList.get(position).setCreateTime(System.currentTimeMillis());
            } else {
                Log.e(TAG, "playDisappearAnimator->error: " + position + "/" + mAnimatorInfoList.size());
            }

            playViewAnimator(itemView, 1f, ANIMATORDURING);
        }
    }

    /**
     * ?????????????????????
     *
     * @param itemView ???????????????????????????
     * @param position ??????????????????
     * @param item     ????????????
     */
    private void playViewAnimator(View itemView, int position, final TCChatEntity item) {
        if (!myArray.contains(item)) {  // ??????????????????????????????
            myArray.add(item);
            mAnimatorInfoList.add(new AnimatorInfo(System.currentTimeMillis()));
        }

        if (mScrolling) {  // ?????????????????????????????????????????????1
            itemView.setAlpha(1f);
        } else {
            continueAnimator(itemView, position, item);
        }
    }

    /**
     * ??????????????????(MAXITEMCOUNT)????????????
     */
    private void clearFinishItem() {
        // ????????????????????????
        while (listMessage.size() > MAXITEMCOUNT) {
            listMessage.remove(0);
            if (mAnimatorInfoList.size() > 0) {
                mAnimatorInfoList.remove(0);
            }
        }

        // ????????????????????????
        while (myArray.size() > (MAXITEMCOUNT << 1)) {
            myArray.remove(0);
        }

        while (mAnimatorInfoList.size() >= listMessage.size()) {
            Log.e(TAG, "clearFinishItem->error size: " + mAnimatorInfoList.size() + "/" + listMessage.size());
            if (mAnimatorInfoList.size() > 0) {
                mAnimatorInfoList.remove(0);
            } else {
                break;
            }
        }
    }

    /**
     * ????????????ITEMCOUNT????????????????????????????????????ListView?????????
     */
    private void redrawListViewHeight() {

        int totalHeight = 0;
        int start = 0, lineCount = 0;

        if (listMessage.size() <= 0) {
            return;
        }

        if (mTotalHeight >= MAXLISTVIEWHEIGHT) {
            return;
        }

        // ????????????ITEMCOUNT??????????????????
//        mCreateAnimator = false;    // ????????????????????????????????????
        for (int i = listMessage.size() - 1; i >= start && lineCount < ITEMCOUNT; i--, lineCount++) {
            View listItem = getView(i, null, mListView);

            listItem.measure(View.MeasureSpec.makeMeasureSpec(MAXLISTVIEWHEIGHT, View.MeasureSpec.AT_MOST)
                    ,View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            // add item height
            totalHeight += listItem.getMeasuredHeight();
            if(totalHeight > MAXLISTVIEWHEIGHT) {
                totalHeight = MAXLISTVIEWHEIGHT;
                break;
            }
        }
//        mCreateAnimator = true;



        mTotalHeight = totalHeight;
        // ??????ListView??????
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = totalHeight + (mListView.getDividerHeight() * (lineCount - 1));
        mListView.setLayoutParams(params);
    }

    /**
     * ????????????????????????????????????????????????
     */
    private void stopAnimator() {
        // ????????????
        for (AnimatorSet anSet : mAnimatorSetList) {
            anSet.cancel();
        }
        mAnimatorSetList.clear();
    }

    /**
     * ???????????????
     */
    private void resetAlpha() {
        for (int i = 0; i < mListView.getChildCount(); i++) {
            View view = mListView.getChildAt(i);
            view.setAlpha(1f);
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void continueAllAnimator() {
//        int startPos = mListView.getFirstVisiblePosition();
//
//        for (int i = 0; i < mListView.getChildCount(); i++) {
//            View view = mListView.getChildAt(i);
//            if (null != view && startPos + i < listMessage.size()) {
//                continueAnimator(view, startPos + i, listMessage.get(startPos + i));
//            }
//        }
    }

    /**
     * ??????notifyDataSetChanged???????????????????????????????????????ListView??????
     */
    @Override
    public void notifyDataSetChanged() {
        Log.v(TAG, "notifyDataSetChanged->scroll: " + mScrolling);
        if (mScrolling) {
            imNewMsgTv.setVisibility(View.VISIBLE);
            // ????????????????????????
            super.notifyDataSetChanged();
            return;
        }
//
//        // ???????????????
//        clearFinishItem();
//
//        if (mBLiveAnimator) {
//            // ??????????????????
//            stopAnimator();
//
//            // ????????????
//            mAnimatorSetList.clear();
//        }

        super.notifyDataSetChanged();

        // ??????ListView??????
        redrawListViewHeight();

//        if (mBLiveAnimator && listMessage.size() >= MAXITEMCOUNT) {
//            continueAllAnimator();
//        }

        mListView.post(new Runnable() {
            @Override
            public void run() {
                mListView.setSelection(mListView.getCount() - 1);
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_FLING:
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
//                if (mBLiveAnimator) {
//                    // ???????????????????????????????????????
//                    stopAnimator();
//                    resetAlpha();
//                }
//                mScrolling = true;
                break;
            case SCROLL_STATE_IDLE:
//                mScrolling = false;
//                if (mBLiveAnimator) {
//                    // ?????????????????????????????????
//                    playDisappearAnimator();
//                }
                if (view.getLastVisiblePosition() == (view.getCount() - 1)){
                    mScrolling = false;
                    imNewMsgTv.setVisibility(View.GONE);
                }else {
                    mScrolling = true;
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
