package com.yjfshop123.live.utils.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjfshop123.live.R;

/**
 *
 * 日期:2019/2/20
 * 描述:
 **/
public class CommunityItemOptionDialog {

    private Dialog bottomDialog;
    private CommunityOptionDialogClickListener communityOptionDialogClickListener;

    public void setCommunityOptionDialogClickListener(CommunityOptionDialogClickListener communityOptionDialogClickListener) {
        this.communityOptionDialogClickListener = communityOptionDialogClickListener;
    }

    public CommunityItemOptionDialog(Activity activity) {
        bottomDialog = new Dialog(activity, R.style.BottomDialog);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_community_item_option_selecter, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = activity.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);

        contentView.findViewById(R.id.dialog_take_jubao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communityOptionDialogClickListener.itemJakeJuBao();
                bottomDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.dialog_select_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });

    }

    public void show() {
        bottomDialog.show();
    }

    public void dissmiss() {
        bottomDialog.dismiss();
    }


    public interface CommunityOptionDialogClickListener {
        void itemJakeJuBao();
    }

}
