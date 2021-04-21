package com.yjfshop123.live.live.live.common.widget.beautysetting;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yjfshop123.live.R;

public class BeautyMainFragment extends DialogFragment {

    public static final int TIE_ZHI = 0;//贴纸
    public static final int MEI_YAN = 1;//美颜
    public static final int LV_JING = 2;//滤镜

    private OnMainChangeListener mOnMainChangeListener;

    public interface OnMainChangeListener {
        void onMainChange(int id);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog2);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_beauty_main);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消

        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);

        dialog.findViewById(R.id.ll_face_tiezhi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mOnMainChangeListener.onMainChange(TIE_ZHI);
            }
        });

        dialog.findViewById(R.id.ll_face_meiyan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mOnMainChangeListener.onMainChange(MEI_YAN);
            }
        });

        dialog.findViewById(R.id.ll_face_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mOnMainChangeListener.onMainChange(LV_JING);
            }
        });
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        IBeauty iBeauty = (IBeauty) getActivity();
        if(iBeauty != null && TCBeautyHelperNew.controlPos == 0){
            iBeauty.closeMain();
        }
    }

    public void setMainListener(OnMainChangeListener listener) {
        mOnMainChangeListener = listener;
    }

}