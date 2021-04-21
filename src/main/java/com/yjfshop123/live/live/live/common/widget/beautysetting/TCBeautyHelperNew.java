package com.yjfshop123.live.live.live.common.widget.beautysetting;

import android.app.FragmentManager;

import com.yjfshop123.live.live.live.common.utils.TCUtils;
import com.faceunity.FURenderer;
import com.faceunity.OnFUControlListener;
import com.faceunity.entity.Effect;

public class TCBeautyHelperNew implements BeautyMainFragment.OnMainChangeListener,
        BeautyMeiYanFragment.OnBeautyMeiYanChangeListener,
        BeautyTieZhiFragment.OnBeautyTieZhiChangeListener,
        BeautyLvJingFragment.OnBeautyFilterChangeListener {

    public static int controlPos = 0;
    //自定义美颜
    private FURenderer mFURenderer;

    public TCBeautyHelperNew(FURenderer fuRenderer) {
        mFURenderer = fuRenderer;
        initMain();
    }

    private BeautyMainFragment mBeautyMainFragment;

    private void initMain(){
        mBeautyMainFragment = new BeautyMainFragment();
        mBeautyMainFragment.setMainListener(this);
    }

    @Override
    public void onMainChange(int id) {
        switch (id){
            case BeautyMainFragment.TIE_ZHI:
                if (mBeautyMainFragment == null){
                    break;
                }

                controlPos = 1;
                mBeautyTieZhiFragment = new BeautyTieZhiFragment();
                mBeautyTieZhiFragment.setBeautyParamsListner(beautyParamsTieZhi,this);
                mBeautyTieZhiFragment.show(mBeautyMainFragment.getActivity().getFragmentManager(), "BeautyTieZhiFragment");

                dismissMain();
                break;
            case BeautyMainFragment.MEI_YAN:
                if (mBeautyMainFragment == null){
                    break;
                }

                controlPos = 2;
                mBeautyMeiYanFragment = new BeautyMeiYanFragment();
                mBeautyMeiYanFragment.setBeautyParamsListner(mBeautyMainFragment.getActivity().getApplicationContext(), proMeiFu, proMeiXing,this);
                mBeautyMeiYanFragment.show(mBeautyMainFragment.getActivity().getFragmentManager(), "BeautyMeiYanFragment");

                dismissMain();
                break;
            case BeautyMainFragment.LV_JING:
                if (mBeautyMainFragment == null){
                    break;
                }

                controlPos = 3;
                mBeautyLvJingFragment = new BeautyLvJingFragment();
                mBeautyLvJingFragment.setBeautyParamsListner(beautyParamsLvJing,this);
                mBeautyLvJingFragment.show(mBeautyMainFragment.getActivity().getFragmentManager(), "BeautyLvJingFragment");

                dismissMain();
                break;
        }
    }

    public void showMain(FragmentManager manager,
                         String tag) {
        mBeautyMainFragment.show(manager, tag);
        controlPos = 0;
    }

    public void dismissMain() {
        mBeautyMainFragment.dismiss();
    }

    public boolean isAddedMain() {
        return mBeautyMainFragment.isAdded();
    }

    private BeautyMeiYanFragment mBeautyMeiYanFragment;
    @Override
    public void onBeautyMeiYanChange(BeautyMeiYanFragment.BeautyParams params, int key, int pos) {
        switch (key){
            case BeautyMeiYanFragment.BEAUTYPARAM_TYPE_INIT:
                if (mFURenderer != null) {
                    //TODO
                    /*mFURenderer.onBlurLevelSelected(TCUtils.filtNumberF(1, 100, params.mListMeiFu_.get(1).progress));//磨皮
                    mFURenderer.onColorLevelSelected(TCUtils.filtNumberF(1, 100, params.mListMeiFu_.get(2).progress));//美白
                    mFURenderer.onRedLevelSelected(TCUtils.filtNumberF(1, 100, params.mListMeiFu_.get(3).progress));//红润
                    mFURenderer.onEyeBrightSelected(TCUtils.filtNumberF(1, 100, params.mListMeiFu_.get(4).progress));//亮眼
                    mFURenderer.onToothWhitenSelected(TCUtils.filtNumberF(1, 100, params.mListMeiFu_.get(5).progress));//美牙

                    mFURenderer.onCheekThinningSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(1).progress));//瘦脸
                    mFURenderer.onCheekVSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(2).progress));//v脸
                    mFURenderer.onCheekNarrowSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(3).progress));//窄脸
                    mFURenderer.onCheekSmallSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(4).progress));//小脸
                    mFURenderer.onEyeEnlargeSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(5).progress));//大眼
                    mFURenderer.onIntensityChinSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(6).progress));//下巴
                    mFURenderer.onIntensityForeheadSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(7).progress));//额头
                    mFURenderer.onIntensityNoseSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(8).progress));//瘦鼻
                    mFURenderer.onIntensityMouthSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(9).progress));//嘴型*/
                }
                break;
            case BeautyMeiYanFragment.BEAUTYPARAM_TYPE_MEIFU:
                if (pos == 0){
                    //清空美肤
                    mFURenderer.onBlurLevelSelected(TCUtils.filtNumberF(1, 100, 0));//磨皮
                    mFURenderer.onColorLevelSelected(TCUtils.filtNumberF(1, 100, 0));//美白
                    mFURenderer.onRedLevelSelected(TCUtils.filtNumberF(1, 100, 0));//红润
                    mFURenderer.onEyeBrightSelected(TCUtils.filtNumberF(1, 100, 0));//亮眼
                    mFURenderer.onToothWhitenSelected(TCUtils.filtNumberF(1, 100, 0));//美牙
                }else if (pos == 1){
                    mFURenderer.onBlurLevelSelected(TCUtils.filtNumberF(1, 100, params.mListMeiFu_.get(1).progress));//磨皮
                }else if (pos == 2){
                    mFURenderer.onColorLevelSelected(TCUtils.filtNumberF(1, 100, params.mListMeiFu_.get(2).progress));//美白
                }else if (pos == 3){
                    mFURenderer.onRedLevelSelected(TCUtils.filtNumberF(1, 100, params.mListMeiFu_.get(3).progress));//红润
                }else if (pos == 4){
                    mFURenderer.onEyeBrightSelected(TCUtils.filtNumberF(1, 100, params.mListMeiFu_.get(4).progress));//亮眼
                }else if (pos == 5){
                    mFURenderer.onToothWhitenSelected(TCUtils.filtNumberF(1, 100, params.mListMeiFu_.get(5).progress));//美牙
                }
                break;
            case BeautyMeiYanFragment.BEAUTYPARAM_TYPE_MEIXING:
                if (pos == 0){
                    //清空美型
                    mFURenderer.onCheekThinningSelected(TCUtils.filtNumberF(1, 100, 0));//瘦脸
                    mFURenderer.onCheekVSelected(TCUtils.filtNumberF(1, 100, 0));//v脸
                    mFURenderer.onCheekNarrowSelected(TCUtils.filtNumberF(1, 100, 0));//窄脸
                    mFURenderer.onCheekSmallSelected(TCUtils.filtNumberF(1, 100, 0));//小脸
                    mFURenderer.onEyeEnlargeSelected(TCUtils.filtNumberF(1, 100, 0));//大眼
                    mFURenderer.onIntensityChinSelected(TCUtils.filtNumberF(1, 100, 0));//下巴
                    mFURenderer.onIntensityForeheadSelected(TCUtils.filtNumberF(1, 100, 0));//额头
                    mFURenderer.onIntensityNoseSelected(TCUtils.filtNumberF(1, 100, 0));//瘦鼻
                    mFURenderer.onIntensityMouthSelected(TCUtils.filtNumberF(1, 100, 0));//嘴型
                }else if (pos == 1){
                    mFURenderer.onCheekThinningSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(1).progress));//瘦脸
                }else if (pos == 2){
                    mFURenderer.onCheekVSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(2).progress));//v脸
                }else if (pos == 3){
                    mFURenderer.onCheekNarrowSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(3).progress));//窄脸
                }else if (pos == 4){
                    mFURenderer.onCheekSmallSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(4).progress));//小脸
                }else if (pos == 5){
                    mFURenderer.onEyeEnlargeSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(5).progress));//大眼
                }else if (pos == 6){
                    mFURenderer.onIntensityChinSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(6).progress));//下巴
                }else if (pos == 7){
                    mFURenderer.onIntensityForeheadSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(7).progress));//额头
                }else if (pos == 8){
                    mFURenderer.onIntensityNoseSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(8).progress));//瘦鼻
                }else if (pos == 9){
                    mFURenderer.onIntensityMouthSelected(TCUtils.filtNumberF(1, 100, params.mListMeiXing_.get(9).progress));//嘴型
                }
                break;
            default:
                break;
        }
    }

    private BeautyTieZhiFragment mBeautyTieZhiFragment;
    @Override
    public void onBeautyTieZhiChange(BeautyTieZhiFragment.BeautyParams params, int key) {
        switch (key){
            case BeautyTieZhiFragment.BEAUTYPARAM_MOTION_TMPL:
                //动效 贴图
                if (mFURenderer != null && params.mVideoMaterialMetaData != null) {
                    OnFUControlListener mOnFUControlListener = mFURenderer;
                    if (params.mVideoMaterialMetaData.id.equals("video_none")){
                        //取消贴纸
                        mOnFUControlListener.onEffectSelected(new Effect("none", 1, Effect.EFFECT_TYPE_NONE));
                    }else {
                        mOnFUControlListener.onEffectSelected(new Effect(params.mVideoMaterialMetaData.path, 2, Effect.EFFECT_TYPE_NORMAL));
                    }
                }
                break;
            default:
                break;
        }
    }

    private BeautyLvJingFragment mBeautyLvJingFragment;
    @Override
    public void onBeautyFilterChange(BeautyLvJingFragment.BeautyParams params, int key) {
        switch (key){
            case BeautyLvJingFragment.BEAUTYPARAM_FILTER:
                if (mFURenderer != null) {
                    mFURenderer.onFilterNameSelected(params.mFilter);
                    mFURenderer.onFilterLevelSelected(TCUtils.filtNumberF(1, 100, params.mFilterProgress));
                }
                break;
            default:
                break;
        }
    }

    private BeautyTieZhiFragment.BeautyParams beautyParamsTieZhi;
    private String proMeiFu;
    private String proMeiXing;
    private BeautyLvJingFragment.BeautyParams beautyParamsLvJing;

    public void init(BeautyTieZhiFragment.BeautyParams beautyParamsTieZhi,
                      String proMeiFu,
                      String proMeiXing,
                      BeautyLvJingFragment.BeautyParams beautyParamsLvJing){

        this.beautyParamsTieZhi = beautyParamsTieZhi;
        this.proMeiFu = proMeiFu;
        this.proMeiXing = proMeiXing;
        this.beautyParamsLvJing = beautyParamsLvJing;

        if (mFURenderer == null) {
            return;
        }

        if (beautyParamsTieZhi.mVideoMaterialMetaData != null) {
            OnFUControlListener mOnFUControlListener = mFURenderer;
            if (beautyParamsTieZhi.mVideoMaterialMetaData.id.equals("video_none")){
                //取消贴纸
                mOnFUControlListener.onEffectSelected(new Effect("none", 1, Effect.EFFECT_TYPE_NONE));
            }else {
                mOnFUControlListener.onEffectSelected(new Effect(beautyParamsTieZhi.mVideoMaterialMetaData.path, 2, Effect.EFFECT_TYPE_NORMAL));
            }
        }

        mFURenderer.onFilterNameSelected(beautyParamsLvJing.mFilter);
        mFURenderer.onFilterLevelSelected(TCUtils.filtNumberF(1, 100, beautyParamsLvJing.mFilterProgress));

        String [] proMeiFus = proMeiFu.split("@@");
        String [] proMeiXings = proMeiXing.split("@@");
        mFURenderer.onBlurLevelSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiFus[1])));//磨皮
        mFURenderer.onColorLevelSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiFus[2])));//美白
        mFURenderer.onRedLevelSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiFus[3])));//红润
        mFURenderer.onEyeBrightSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiFus[4])));//亮眼
        mFURenderer.onToothWhitenSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiFus[5])));//美牙

        mFURenderer.onCheekThinningSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiXings[1])));//瘦脸
        mFURenderer.onCheekVSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiXings[2])));//v脸
        mFURenderer.onCheekNarrowSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiXings[3])));//窄脸
        mFURenderer.onCheekSmallSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiXings[4])));//小脸
        mFURenderer.onEyeEnlargeSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiXings[5])));//大眼
        mFURenderer.onIntensityChinSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiXings[6])));//下巴
        mFURenderer.onIntensityForeheadSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiXings[7])));//额头
        mFURenderer.onIntensityNoseSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiXings[8])));//瘦鼻
        mFURenderer.onIntensityMouthSelected(TCUtils.filtNumberF(1, 100, Integer.parseInt(proMeiXings[9])));//嘴型
    }
}
