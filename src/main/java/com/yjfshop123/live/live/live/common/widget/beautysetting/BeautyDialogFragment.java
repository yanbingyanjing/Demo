package com.yjfshop123.live.live.live.common.widget.beautysetting;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.tencent.trtc.TRTCCloudDef;

import java.util.ArrayList;


public class BeautyDialogFragment extends DialogFragment implements TextSeekBar.OnSeekChangeListener {

    public static final int BEAUTYPARAM_BEAUTY = 1;//美颜
    public static final int BEAUTYPARAM_WHITE = 2;//美白
    public static final int BEAUTYPARAM_Ruddy = 3;//红润

    public static final int BEAUTYPARAM_FILTER = 5;//滤镜

    static public class BeautyParams{
        public int mBeautyStyle = TRTCCloudDef.TRTC_BEAUTY_STYLE_SMOOTH;//颜风格，三种美颜风格：0 ：光滑；1：自然；2：朦胧
        public int mBeautyProgress = 0;//美颜  0 - 9
        public int mWhiteProgress = 0;//美白  0 - 9
        public int mRuddyProgress = 0;//红润 0 - 9

        public int mFilterIdx;
    }

    public interface OnBeautyParamsChangeListener{
        void onBeautyParamsChange(BeautyParams params, int key);
    }

    public interface OnDismissListener{
        void onDismiss(BeautyParams beautyParams);
    }

    private View mLayoutBeauty;

    private TCHorizontalScrollView mFilterPicker;
    private ArrayList<Integer> mFilterIDList;
    private ArrayAdapter<Integer> mFilterAdapter;

    private TextSeekBar seekMeiyan;
    private TextSeekBar seekMeibai;
    private TextSeekBar seekHongrun;

    private BeautyParams    mBeautyParams;
    private OnBeautyParamsChangeListener mBeautyParamsChangeListener;
    private OnDismissListener mOnDismissListener;

    private TextView mTVBeauty;
    private TextView mTVFilter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog2);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_beauty_area);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消

        mLayoutBeauty = dialog.findViewById(R.id.layoutFaceBeauty);
        mFilterPicker = dialog.findViewById(R.id.filterPicker);

        mFilterPicker.setVisibility(View.GONE);

        seekMeiyan = dialog.findViewById(R.id.seek_meiyan);
        seekMeibai = dialog.findViewById(R.id.seek_meibai);
        seekHongrun = dialog.findViewById(R.id.seek_hongrun);

        seekMeiyan.setOnSeekChangeListener(this);
        seekMeibai.setOnSeekChangeListener(this);
        seekHongrun.setOnSeekChangeListener(this);

        seekMeiyan.setProgress(mBeautyParams.mBeautyProgress);
        seekMeibai.setProgress(mBeautyParams.mWhiteProgress);
        seekHongrun.setProgress(mBeautyParams.mRuddyProgress);

        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);

        mFilterIDList = new ArrayList<>();
        mFilterIDList.add(R.drawable.icon_filter_orginal);
        mFilterIDList.add(R.drawable.icon_filter_biaozhun);
        mFilterIDList.add(R.drawable.icon_filter_yinghong);
        mFilterIDList.add(R.drawable.icon_filter_yunshang);
        mFilterIDList.add(R.drawable.icon_filter_chunzhen);
        mFilterIDList.add(R.drawable.icon_filter_bailan);
        mFilterIDList.add(R.drawable.icon_filter_yuanqi);
        mFilterIDList.add(R.drawable.icon_filter_chaotuo);
        mFilterIDList.add(R.drawable.icon_filter_xiangfen);
        mFilterIDList.add(R.drawable.icon_filter_langman);
        mFilterIDList.add(R.drawable.icon_filter_qingxin);
        mFilterIDList.add(R.drawable.icon_filter_weimei);
        mFilterIDList.add(R.drawable.icon_filter_fennen);
        mFilterIDList.add(R.drawable.icon_filter_huaijiu);
        mFilterIDList.add(R.drawable.icon_filter_landiao);
        mFilterIDList.add(R.drawable.icon_filter_qingliang);
        mFilterIDList.add(R.drawable.icon_filter_rixi);
        mFilterAdapter = new ArrayAdapter<Integer>(dialog.getContext(),0, mFilterIDList){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.filter_layout,null);
                }
                ImageView view = (ImageView) convertView.findViewById(R.id.filter_image);
                if (position == 0) {
                    ImageView view_tint = (ImageView) convertView.findViewById(R.id.filter_image_tint);
                    if (view_tint != null)
                        view_tint.setVisibility(View.VISIBLE);
                }
                view.setTag(position);
                view.setImageDrawable(getResources().getDrawable(getItem(position)));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = (int) view.getTag();
                        mBeautyParams.mFilterIdx = index;
                        selectFilter(mBeautyParams.mFilterIdx);
                        if(mBeautyParamsChangeListener instanceof OnBeautyParamsChangeListener){
                            mBeautyParamsChangeListener.onBeautyParamsChange(mBeautyParams, BEAUTYPARAM_FILTER);
                        }
                    }
                });
                return convertView;

            }
        };
        mFilterPicker.setAdapter(mFilterAdapter);
        if (mBeautyParams.mFilterIdx >=0 && mBeautyParams.mFilterIdx < mFilterAdapter.getCount()) {
            mFilterPicker.setClicked(mBeautyParams.mFilterIdx);
            selectFilter(mBeautyParams.mFilterIdx);
        } else {
            mFilterPicker.setClicked(0);
        }

        mTVBeauty = dialog.findViewById(R.id.tv_face_beauty);
        mTVFilter = dialog.findViewById(R.id.tv_face_filter);

        mTVBeauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTVBeauty.setTextColor(Color.parseColor("#ffdd00"));
                mTVFilter.setTextColor(Color.parseColor("#ffffff"));

                mLayoutBeauty.setVisibility(View.VISIBLE);
                mFilterPicker.setVisibility(View.GONE);

                seekMeiyan.setProgress(mBeautyParams.mBeautyProgress);
                seekMeibai.setProgress(mBeautyParams.mWhiteProgress);
                seekHongrun.setProgress(mBeautyParams.mRuddyProgress);
            }
        });

        mTVFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTVBeauty.setTextColor(Color.parseColor("#ffffff"));
                mTVFilter.setTextColor(Color.parseColor("#ffdd00"));

                mLayoutBeauty.setVisibility(View.GONE);
                mFilterPicker.setVisibility(View.VISIBLE);
            }
        });

        return dialog;
    }

    private void selectFilter(int index) {
        ViewGroup group = (ViewGroup)mFilterPicker.getChildAt(0);
        for (int i = 0; i < mFilterAdapter.getCount(); i++) {
            View v = group.getChildAt(i);
            ImageView IVTint = v.findViewById(R.id.filter_image_tint);
            if (IVTint != null) {
                if (i == index) {
                    IVTint.setVisibility(View.VISIBLE);
                } else {
                    IVTint.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mOnDismissListener != null){
            mOnDismissListener.onDismiss(mBeautyParams);
        }
    }

    public void setmOnDismissListener(OnDismissListener onDismissListener){
        mOnDismissListener = onDismissListener;
    }

    @Override
    public void onProgressChanged(View view, int progress) {
        switch (view.getId()){
            case R.id.seek_meiyan:
                mBeautyParams.mBeautyProgress = progress;
                if(mBeautyParamsChangeListener instanceof OnBeautyParamsChangeListener){
                    mBeautyParamsChangeListener.onBeautyParamsChange(mBeautyParams, BEAUTYPARAM_BEAUTY);
                }
                break;
            case R.id.seek_meibai:
                mBeautyParams.mWhiteProgress = progress;
                if(mBeautyParamsChangeListener instanceof OnBeautyParamsChangeListener){
                    mBeautyParamsChangeListener.onBeautyParamsChange(mBeautyParams, BEAUTYPARAM_WHITE);
                }
                break;
            case R.id.seek_hongrun:
                mBeautyParams.mRuddyProgress = progress;
                if(mBeautyParamsChangeListener instanceof OnBeautyParamsChangeListener){
                    mBeautyParamsChangeListener.onBeautyParamsChange(mBeautyParams, BEAUTYPARAM_Ruddy);
                }
                break;
            default:
                break;
        }
    }

    public void setBeautyParamsListner(BeautyParams params, OnBeautyParamsChangeListener listener){
        mBeautyParams = params;
        mBeautyParamsChangeListener = listener;
        //当BeautyDialogFragment重置时，先刷新一遍配置
        if (mBeautyParamsChangeListener instanceof OnBeautyParamsChangeListener){
            mBeautyParamsChangeListener.onBeautyParamsChange(mBeautyParams, BEAUTYPARAM_BEAUTY);
            mBeautyParamsChangeListener.onBeautyParamsChange(mBeautyParams, BEAUTYPARAM_WHITE);
            mBeautyParamsChangeListener.onBeautyParamsChange(mBeautyParams, BEAUTYPARAM_Ruddy);

            mBeautyParamsChangeListener.onBeautyParamsChange(mBeautyParams, BEAUTYPARAM_FILTER);
        }
    }
}
