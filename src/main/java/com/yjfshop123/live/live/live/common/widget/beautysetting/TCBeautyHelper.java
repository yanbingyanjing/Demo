package com.yjfshop123.live.live.live.common.widget.beautysetting;

import android.app.FragmentManager;
import android.content.Context;

import com.yjfshop123.live.live.live.common.utils.TCUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.tencent.liteav.beauty.TXBeautyManager;
import com.tencent.trtc.TRTCCloud;


public class TCBeautyHelper implements BeautyDialogFragment.OnBeautyParamsChangeListener {

    private BeautyDialogFragment mBeautyDialogFragment;
    private BeautyDialogFragment.BeautyParams   mBeautyParams  = new BeautyDialogFragment.BeautyParams();
    private TRTCCloud mTrtcCloud;
    private Context mContext;

    public TCBeautyHelper(TRTCCloud trtcCloud, Context context) {
        mTrtcCloud = trtcCloud;
        mContext = context;
        mBeautyDialogFragment = new BeautyDialogFragment();

        mBeautyParams.mBeautyProgress = UserInfoUtil.getBeault();
        mBeautyParams.mWhiteProgress = UserInfoUtil.getWhite();
        mBeautyParams.mRuddyProgress = UserInfoUtil.getRuddy();
        mBeautyParams.mFilterIdx = UserInfoUtil.getFilterId();

        mBeautyDialogFragment.setBeautyParamsListner(mBeautyParams,this);

        if (trtcCloud != null){
            //设置默认的滤镜参数
            trtcCloud.setFilterConcentration(1f);
        }
    }

    public void show(FragmentManager manager, String tag) {
        mBeautyDialogFragment.show(manager, tag);
    }

    public void dismiss() {
        mBeautyDialogFragment.dismiss();
    }

    public boolean isAdded() {
        return mBeautyDialogFragment.isAdded();
    }

    public void setmOnDismissListener(BeautyDialogFragment.OnDismissListener onDismissListener){
        mBeautyDialogFragment.setmOnDismissListener(onDismissListener);
    }

    public BeautyDialogFragment.BeautyParams getParams() {
        return mBeautyParams;
    }

    @Override
    public void onBeautyParamsChange(BeautyDialogFragment.BeautyParams params, int key) {
        switch (key){
            case BeautyDialogFragment.BEAUTYPARAM_BEAUTY:
            case BeautyDialogFragment.BEAUTYPARAM_WHITE:
            case BeautyDialogFragment.BEAUTYPARAM_Ruddy:
                if (mTrtcCloud != null) {
//                    mTrtcCloud.setBeautyStyle(params.mBeautyStyle,
//                            TCUtils.filtNumber(9, 100, params.mBeautyProgress),
//                            TCUtils.filtNumber(9, 100, params.mWhiteProgress),
//                            TCUtils.filtNumber(9, 100, params.mRuddyProgress));
                    TXBeautyManager txBeautyManager = mTrtcCloud.getBeautyManager();
                    txBeautyManager.setBeautyStyle(params.mBeautyStyle);
                    txBeautyManager.setBeautyLevel(TCUtils.filtNumber(9, 100, params.mBeautyProgress));
                    txBeautyManager.setWhitenessLevel(TCUtils.filtNumber(9, 100, params.mWhiteProgress));
                    txBeautyManager.setRuddyLevel(TCUtils.filtNumber(9, 100, params.mRuddyProgress));
                }
                break;
            case BeautyDialogFragment.BEAUTYPARAM_FILTER:
                if (mTrtcCloud != null) {
//                    mTrtcCloud.setFilter(TCUtils.getFilterBitmap(mContext.getResources(), params.mFilterIdx));
                    TXBeautyManager txBeautyManager = mTrtcCloud.getBeautyManager();
                    txBeautyManager.setFilter(TCUtils.getFilterBitmap(mContext.getResources(), params.mFilterIdx));
                }
                break;
            default:
                break;
        }
    }
}
