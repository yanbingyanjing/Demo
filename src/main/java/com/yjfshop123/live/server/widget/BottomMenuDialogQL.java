package com.yjfshop123.live.server.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;

public class BottomMenuDialogQL extends Dialog {

    private TextView costTv;
    private int type;
    private Context context;
    private String defaultCost;

    /**
     * @param context
     */
    public BottomMenuDialogQL(Context context, int type, String defaultCost) {
        super(context, R.style.dialogFullscreen2);
        this.type = type;
        this.context = context;
        this.defaultCost = defaultCost;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_bottom_ql);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        TextView ldbq_title = findViewById(R.id.ldbq_title);
        if (type == 3){
            ldbq_title.setText(context.getString(R.string.sp_ql));
        }else {
            ldbq_title.setText(context.getString(R.string.yy_ql));
        }

        findViewById(R.id.ldbq_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        costTv = findViewById(R.id.ldbq_cost_tv);
        costTv.setText(defaultCost);

        Button ldbq_ql = findViewById(R.id.ldbq_ql);
        ldbq_ql.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConfirmListener != null) {
                    mConfirmListener.onClick(costTv.getText().toString());
                }
            }
        });

        RelativeLayout ldbq_cost_rl = findViewById(R.id.ldbq_cost_rl);
        ldbq_cost_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCostListener != null) {
                    mCostListener.onClick(costTv);
                }
            }
        });

//        GradientDrawable controlsAdd_GD = (GradientDrawable) ldbq_ql.getBackground();
//        controlsAdd_GD.setColor(ThemeColorUtils.getThemeColor());
//        GradientDrawable voiceTv_GD = (GradientDrawable) ldbq_cost_rl.getBackground();
//        voiceTv_GD.setColor(ThemeColorUtils.getThemeColor());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        dismiss();
        return true;
    }

    private ConfirmListener mConfirmListener;

    public interface ConfirmListener {
        void onClick(String cost);
    }

    public void setConfirmListener(ConfirmListener listener){
        this.mConfirmListener = listener;
    }

    private CostListener mCostListener;

    public interface CostListener {
        void onClick(TextView costTv);
    }

    public void setCostListener(CostListener listener){
        this.mCostListener = listener;
    }



}
