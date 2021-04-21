package com.yjfshop123.live.live.live.common.widget.beautysetting;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
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

import java.util.ArrayList;

public class BeautyMeiYanFragment extends DialogFragment implements TextSeekBar.OnSeekChangeListener{

//    private var titles = ["美肤", "美型", "美发", "美体"]
//    private var meifu = ["磨皮", "美白", "红润" , "亮眼", "美牙"]
//    private var meixing = ["瘦脸", "v脸", "窄脸", "小脸", "大眼", "下巴", "额头", "瘦鼻", "嘴型"]
//    private var meiti = ["瘦身", "长腿", "细腰", "美肩", "美臀"]

    public static final int BEAUTYPARAM_TYPE_MEIFU = 0;//美肤
    public static final int BEAUTYPARAM_TYPE_MEIXING = 1;//美型
    public static final int BEAUTYPARAM_TYPE_INIT = 2;//初始化
    private int selePos = 0;
    private int type = 0;//0 美肤 1 美型

    static public class BeautyParams{
        public ArrayList<MeiYanMode> mListMeiFu_;
        public ArrayList<MeiYanMode> mListMeiXing_;
    }

    public interface OnBeautyMeiYanChangeListener{
        void onBeautyMeiYanChange(BeautyParams params, int key, int pos);
    }

    private TextSeekBar seekMeiYan;
    private TCHorizontalScrollView mMeiYanPicker;
    private ArrayAdapter<MeiYanMode> mMeiYanAdapter;
    private Dialog dialog;
    private TextView tvMeiFu;
    private TextView tvMeiXing;

    private BeautyParams mBeautyParams;
    private OnBeautyMeiYanChangeListener mBeautyMeiYanChangeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = new Dialog(getActivity(), R.style.BottomDialog2);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_beauty_meiyan);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消

        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);

        mMeiYanPicker = dialog.findViewById(R.id.meiyanPicker);

        seekMeiYan = dialog.findViewById(R.id.seek_meiyan_);
        seekMeiYan.setOnSeekChangeListener(this);

        tvMeiFu = dialog.findViewById(R.id.tv_face_meifu);
        tvMeiXing = dialog.findViewById(R.id.tv_face_meixing);
        tvMeiFu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvMeiFu.setTextColor(getResources().getColor(R.color.color_style));
                tvMeiXing.setTextColor(Color.WHITE);
                loadMeiFu();
            }
        });
        tvMeiXing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvMeiFu.setTextColor(Color.WHITE);
                tvMeiXing.setTextColor(getResources().getColor(R.color.color_style));
                loadMeiXing();
            }
        });

        loadMeiFu();
        return dialog;
    }

    private void loadMeiFu(){
        seekMeiYan.setVisibility(View.GONE);
        selePos = 0;
        type = 0;
        mMeiYanAdapter = new ArrayAdapter<MeiYanMode>(dialog.getContext(),0, mBeautyParams.mListMeiFu_){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.meifu_layout,null);
                }
                ImageView view = convertView.findViewById(R.id.meifu_layout_image);
                TextView txt = convertView.findViewById(R.id.meifu_layout_txt);
                view.setImageDrawable(getResources().getDrawable(getItem(position).imgid));
                txt.setText(getItem(position).name);

                view.setColorFilter(Color.WHITE);
                txt.setTextColor(Color.WHITE);

                view.setTag(position);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = (int) view.getTag();
                        selectFilter(index);
                    }
                });
                return convertView;

            }
        };
        mMeiYanPicker.setAdapter(mMeiYanAdapter);
    }

    private void loadMeiXing(){
        seekMeiYan.setVisibility(View.GONE);
        selePos = 0;
        type = 1;
        mMeiYanAdapter = new ArrayAdapter<MeiYanMode>(dialog.getContext(),0, mBeautyParams.mListMeiXing_){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.meifu_layout,null);
                }
                ImageView view = convertView.findViewById(R.id.meifu_layout_image);
                TextView txt = convertView.findViewById(R.id.meifu_layout_txt);
                view.setImageDrawable(getResources().getDrawable(getItem(position).imgid));
                txt.setText(getItem(position).name);

                view.setColorFilter(Color.WHITE);
                txt.setTextColor(Color.WHITE);

                view.setTag(position);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = (int) view.getTag();
                        selectFilter(index);
                    }
                });
                return convertView;

            }
        };
        mMeiYanPicker.setAdapter(mMeiYanAdapter);
    }

    private void selectFilter(int index) {
        selePos = index;
        if (type == 0){
            //美肤
            if (selePos == 0){
                seekMeiYan.setVisibility(View.GONE);
                for (int i = 0; i < mBeautyParams.mListMeiFu_.size(); i++) {
                    mBeautyParams.mListMeiFu_.get(i).progress = 0;
                }

                if(mBeautyMeiYanChangeListener instanceof OnBeautyMeiYanChangeListener){
                    mBeautyMeiYanChangeListener.onBeautyMeiYanChange(mBeautyParams, BEAUTYPARAM_TYPE_MEIFU, selePos);
                }
            }else {
                seekMeiYan.setVisibility(View.VISIBLE);
                seekMeiYan.setProgress(mBeautyParams.mListMeiFu_.get(selePos).progress);
            }
        }else if (type == 1){
            //美型
            if (index == 0){
                seekMeiYan.setVisibility(View.GONE);
                for (int i = 0; i < mBeautyParams.mListMeiXing_.size(); i++) {
                    mBeautyParams.mListMeiXing_.get(i).progress = 0;
                }

                if(mBeautyMeiYanChangeListener instanceof OnBeautyMeiYanChangeListener){
                    mBeautyMeiYanChangeListener.onBeautyMeiYanChange(mBeautyParams, BEAUTYPARAM_TYPE_MEIXING, selePos);
                }
            }else {
                seekMeiYan.setVisibility(View.VISIBLE);
                seekMeiYan.setProgress(mBeautyParams.mListMeiXing_.get(selePos).progress);
            }
        }

        ViewGroup group = (ViewGroup)mMeiYanPicker.getChildAt(0);
        for (int i = 0; i < mMeiYanAdapter.getCount(); i++) {
            View v = group.getChildAt(i);
            ImageView img = v.findViewById(R.id.meifu_layout_image);
            TextView txt = v.findViewById(R.id.meifu_layout_txt);
            if (i == index) {
                img.setColorFilter(getResources().getColor(R.color.color_style));
                txt.setTextColor(getResources().getColor(R.color.color_style));
            } else {
                img.setColorFilter(Color.WHITE);
                txt.setTextColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        IBeauty iBeauty = (IBeauty) getActivity();
        if(iBeauty != null){
            if (mBeautyParams == null){
                return;
            }
            StringBuffer proMeiFu = new StringBuffer();
            StringBuffer proMeiXing = new StringBuffer();
            int size_ = mBeautyParams.mListMeiFu_.size();
            for (int i = 0; i < size_; i++) {
                proMeiFu.append(String.valueOf(mBeautyParams.mListMeiFu_.get(i).progress));
                if (i != size_ - 1){
                    proMeiFu.append("@@");
                }
            }
            int size = mBeautyParams.mListMeiXing_.size();
            for (int i = 0; i < size; i++) {
                proMeiXing.append(String.valueOf(mBeautyParams.mListMeiXing_.get(i).progress));
                if (i != size - 1){
                    proMeiXing.append("@@");
                }
            }

            iBeauty.closeMeiYan(proMeiFu.toString(), proMeiXing.toString());
        }
    }

    @Override
    public void onProgressChanged(View view, int progress) {
        switch (view.getId()){
            case R.id.seek_meiyan_:
                if (selePos == 0){
                    return;
                }
                if (type == 0){
                    //美肤
                    mBeautyParams.mListMeiFu_.get(selePos).progress = progress;
                    if(mBeautyMeiYanChangeListener instanceof OnBeautyMeiYanChangeListener){
                        mBeautyMeiYanChangeListener.onBeautyMeiYanChange(mBeautyParams, BEAUTYPARAM_TYPE_MEIFU, selePos);
                    }
                }else if (type == 1){
                    //美型
                    mBeautyParams.mListMeiXing_.get(selePos).progress = progress;
                    if(mBeautyMeiYanChangeListener instanceof OnBeautyMeiYanChangeListener){
                        mBeautyMeiYanChangeListener.onBeautyMeiYanChange(mBeautyParams, BEAUTYPARAM_TYPE_MEIXING, selePos);
                    }
                }
                break;
            default:
                break;
        }
    }

    public void setBeautyParamsListner(Context context,
                                       String proMeiFu,
                                       String proMeiXing,
                                       OnBeautyMeiYanChangeListener listener){

        ArrayList<MeiYanMode> mListMeiFu = new ArrayList<>();
        mListMeiFu.add(new MeiYanMode(0, context.getString(R.string.beauty_name_1), 0, R.drawable.live_beauty_replay));
        mListMeiFu.add(new MeiYanMode(1, context.getString(R.string.beauty_name_2), 0, R.drawable.live_beauty_mopi));
        mListMeiFu.add(new MeiYanMode(2, context.getString(R.string.beauty_name_3), 0, R.drawable.live_beauty_meibai));
        mListMeiFu.add(new MeiYanMode(3, context.getString(R.string.beauty_name_4), 0, R.drawable.live_beauty_hongrun));
        mListMeiFu.add(new MeiYanMode(4, context.getString(R.string.beauty_name_5), 0, R.drawable.live_beauty_liangyan));
        mListMeiFu.add(new MeiYanMode(5, context.getString(R.string.beauty_name_6), 0, R.drawable.live_beauty_meiya));

        ArrayList<MeiYanMode> mListMeiXing = new ArrayList<>();
        mListMeiXing.add(new MeiYanMode(0, context.getString(R.string.beauty_name_1), 0, R.drawable.live_beauty_replay));
        mListMeiXing.add(new MeiYanMode(1, context.getString(R.string.beauty_name_v_1), 0, R.drawable.live_beauty_shoulian));
        mListMeiXing.add(new MeiYanMode(2, context.getString(R.string.beauty_name_v_2), 0, R.drawable.live_beauty_vlian));
        mListMeiXing.add(new MeiYanMode(3, context.getString(R.string.beauty_name_v_3), 0, R.drawable.live_beauty_zhailian));
        mListMeiXing.add(new MeiYanMode(4, context.getString(R.string.beauty_name_v_4), 0, R.drawable.live_beauty_xiaolian));
        mListMeiXing.add(new MeiYanMode(5, context.getString(R.string.beauty_name_v_5), 0, R.drawable.live_beauty_dayan));
        mListMeiXing.add(new MeiYanMode(6, context.getString(R.string.beauty_name_v_6), 0, R.drawable.live_beauty_xiaba));
        mListMeiXing.add(new MeiYanMode(7, context.getString(R.string.beauty_name_v_7), 0, R.drawable.live_beauty_etou));
        mListMeiXing.add(new MeiYanMode(8, context.getString(R.string.beauty_name_v_8), 0, R.drawable.live_beauty_shoubi));
        mListMeiXing.add(new MeiYanMode(9, context.getString(R.string.beauty_name_v_9), 0, R.drawable.live_beauty_zuixing));

        String [] proMeiFus = proMeiFu.split("@@");
        for (int i = 0; i < proMeiFus.length; i++) {
            mListMeiFu.get(i).progress = Integer.parseInt(proMeiFus[i]);
        }

        String [] proMeiXings = proMeiXing.split("@@");
        for (int i = 0; i < proMeiXings.length; i++) {
            mListMeiXing.get(i).progress = Integer.parseInt(proMeiXings[i]);
        }

        mBeautyParams = new BeautyParams();
        mBeautyParams.mListMeiFu_ = mListMeiFu;
        mBeautyParams.mListMeiXing_ = mListMeiXing;
        mBeautyMeiYanChangeListener = listener;

        if (mBeautyMeiYanChangeListener instanceof OnBeautyMeiYanChangeListener){
            mBeautyMeiYanChangeListener.onBeautyMeiYanChange(mBeautyParams, BEAUTYPARAM_TYPE_INIT, 0);
        }
    }

    class MeiYanMode{
        public String name;
        public int progress;
        public int imgid;
        public int type;
        public MeiYanMode(int type, String name, int progress, int imgid) {
            this.type = type;
            this.name = name;
            this.progress = progress;
            this.imgid = imgid;
        }
    }
}
