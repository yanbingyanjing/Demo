package com.yjfshop123.live.utils.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;

/**
 *
 * 日期:2019/2/20
 * 描述:
 **/
public class CommunityOptionDialog {

    private Dialog bottomDialog;
    private CommunityOptionDialogClickListener communityOptionDialogClickListener;

    private TextView lookLouzhu;
    private TextView lookFlag;
    private LinearLayout delComm;

    public void setCommunityOptionDialogClickListener(CommunityOptionDialogClickListener communityOptionDialogClickListener) {
        this.communityOptionDialogClickListener = communityOptionDialogClickListener;
    }

    public CommunityOptionDialog(Activity activity) {
        bottomDialog = new Dialog(activity, R.style.BottomDialog);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_community_option_selecter, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = activity.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);

        lookLouzhu=contentView.findViewById(R.id.lookLouzhu);
        lookFlag=contentView.findViewById(R.id.lookFlag);

        contentView.findViewById(R.id.dialog_take_look).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communityOptionDialogClickListener.takeLook();
                bottomDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.dialog_select_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communityOptionDialogClickListener.takeType();
                bottomDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.dialog_select_jubao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communityOptionDialogClickListener.takeJubao();
                bottomDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.dialog_select_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communityOptionDialogClickListener.block();
                bottomDialog.dismiss();
            }
        });

        contentView.findViewById(R.id.dialog_select_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });

        delComm = contentView.findViewById(R.id.dialog_select_del_comm);
    }

    public void setDelComm(){
        if (delComm == null){
            return;
        }

        delComm.setVisibility(View.VISIBLE);
        delComm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communityOptionDialogClickListener.delComm();
                bottomDialog.dismiss();
            }
        });
    }

    public void setLookLouZhu(String str){
        lookLouzhu.setText(str);
    }

    public void setLookLouType(String str){
        lookFlag.setText(str);
    }

    public void show() {
        bottomDialog.show();
    }

    public void dissmiss() {
        bottomDialog.dismiss();
    }


    public interface CommunityOptionDialogClickListener{
        void takeLook();
        void takeType();
        void takeJubao();
        void delComm();
        void block();
    }

}
