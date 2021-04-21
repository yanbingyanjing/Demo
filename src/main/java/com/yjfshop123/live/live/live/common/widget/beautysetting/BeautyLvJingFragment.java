package com.yjfshop123.live.live.live.common.widget.beautysetting;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.yjfshop123.live.R;
import com.faceunity.entity.Filter;

import java.util.ArrayList;

public class BeautyLvJingFragment extends DialogFragment implements TextSeekBar.OnSeekChangeListener {

    public static final int BEAUTYPARAM_FILTER = 5;//滤镜

    static public class BeautyParams{
        public int mFilterProgress = 40;
        public String mFilter = Filter.Key.ORIGIN;
    }

    public interface OnBeautyFilterChangeListener{
        void onBeautyFilterChange(BeautyParams params, int key);
    }

    private TextSeekBar seekFilter;

    private TCHorizontalScrollView mFilterPicker;
    private ArrayList<Integer> mFilterIDList;
    private ArrayList<String> mFilterNameList;
    private ArrayAdapter<Integer> mFilterAdapter;

    private BeautyParams mBeautyParams;
    private OnBeautyFilterChangeListener mBeautyFilterChangeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog2);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_beauty_lvjing);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消

        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);

        mFilterPicker = dialog.findViewById(R.id.filterPicker);

        seekFilter = dialog.findViewById(R.id.seek_filter);
        seekFilter.setOnSeekChangeListener(this);
        seekFilter.setProgress(mBeautyParams.mFilterProgress);

        mFilterIDList = new ArrayList<>();
        mFilterIDList.add(R.drawable.icon_filter_orginal);//origin
        mFilterIDList.add(R.drawable.icon_filter_fennen);//fennen1
        mFilterIDList.add(R.drawable.icon_filter_qingxin);//xiaoqingxin1
        mFilterIDList.add(R.drawable.icon_filter_bailan);//bailiang1
        mFilterIDList.add(R.drawable.icon_filter_rixi);//lengsediao1
        mFilterIDList.add(R.drawable.icon_filter_landiao);//nuansediao1
        mFilterIDList.add(R.drawable.icon_filter_huaijiu);//heibai1
        mFilterIDList.add(R.drawable.icon_filter_xiangfen);//gexing1
        mFilterNameList = new ArrayList<>();
        mFilterNameList.add(Filter.Key.ORIGIN);
        mFilterNameList.add(Filter.Key.FENNEN_1);
        mFilterNameList.add(Filter.Key.XIAOQINGXIN_1);
        mFilterNameList.add(Filter.Key.BAILIANG_1);
        mFilterNameList.add(Filter.Key.LENGSEDIAO_1);
        mFilterNameList.add(Filter.Key.NUANSEDIAO_1);
        mFilterNameList.add(Filter.Key.HEIBAI_1);
        mFilterNameList.add(Filter.Key.GEXING_1);

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
                        mBeautyParams.mFilter = mFilterNameList.get(index);
                        selectFilter(index);
                        if(mBeautyFilterChangeListener instanceof OnBeautyFilterChangeListener){
                            mBeautyFilterChangeListener.onBeautyFilterChange(mBeautyParams, BEAUTYPARAM_FILTER);
                        }
                    }
                });
                return convertView;

            }
        };
        mFilterPicker.setAdapter(mFilterAdapter);
        if (!TextUtils.isEmpty(mBeautyParams.mFilter)) {
            int index = getIndex(mBeautyParams.mFilter);
            mFilterPicker.setClicked(index);
            selectFilter(index);
        } else {
            mFilterPicker.setClicked(0);
        }
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        IBeauty iBeauty = (IBeauty) getActivity();
        if(iBeauty != null){
            iBeauty.closeLvJing(mBeautyParams);
        }
    }

    @Override
    public void onProgressChanged(View view, int progress) {
        switch (view.getId()){
            case R.id.seek_filter:
                mBeautyParams.mFilterProgress = progress;
                if(mBeautyFilterChangeListener instanceof OnBeautyFilterChangeListener){
                    mBeautyFilterChangeListener.onBeautyFilterChange(mBeautyParams, BEAUTYPARAM_FILTER);
                }
                break;
            default:
                break;
        }
    }

    private int getIndex(String filter){
        int index = 0;
        for (int i = 0; i < mFilterNameList.size(); i++) {
            if (mFilterNameList.get(i).equals(filter)){
                index = i;
                break;
            }
        }
        return index;
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

    public void setBeautyParamsListner(BeautyParams params, OnBeautyFilterChangeListener listener){
        mBeautyParams = params;
        mBeautyFilterChangeListener = listener;
        if (mBeautyFilterChangeListener instanceof OnBeautyFilterChangeListener){
            mBeautyFilterChangeListener.onBeautyFilterChange(mBeautyParams, BEAUTYPARAM_FILTER);
        }
    }
}
