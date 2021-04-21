package com.yjfshop123.live.utils.update;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.NumberProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InstallExchangeDialog  {
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_update_info)
    TextView tv_update_info;
    @BindView(R.id.btn_ok)
    Button btn_ok;
    @BindView(R.id.npb)
      NumberProgressBar npb;
    @BindView(R.id.contentLay)
    RelativeLayout contentLay;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.ll_close)
    LinearLayout ll_close;

    @BindView(R.id.lLayout_bg)
    LinearLayout lLayout_bg;
    private Context context;
    private static Dialog dialog;
    private Display display;





    public InstallExchangeDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public InstallExchangeDialog builder() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_install_exchange, null);
        ButterKnife.bind(this, view);


        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
//        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return this;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public InstallExchangeDialog setTitle(String title) {
        if ("".equals(title)) {
            tv_title.setText(R.string.other_title);
        } else {
            tv_title.setText(title);
        }
        return this;
    }

    public InstallExchangeDialog setMsg(String msg) {
        if ("".equals(msg)) {
            tv_update_info.setText("内容");
        } else {
            tv_update_info.setText(msg);
        }
        return this;
    }


    public  void setProgress(int current) {
        if (npb == null) {
            return;
        }
        npb.setProgress(current);
        if (npb.getProgress() >= npb.getMax()) {
            dialog.dismiss();
            npb = null;
        }
    }

    public InstallExchangeDialog setFocus(boolean focus) {
        if (focus) {
            dialog.setCancelable(false);
            ll_close.setVisibility(View.GONE);
        } else {
            dialog.setCancelable(true);
            ll_close.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public  void setProgress(long current) {
        if (npb == null) {
            return;
        }
        npb.setProgress(((int) current) / (1024 * 1024));
        if (npb.getProgress() >= npb.getMax()) {
            dialog.dismiss();
            npb = null;
        }
    }

    public InstallExchangeDialog setPositiveButton(String text, final View.OnClickListener listener) {
        if ("".equals(text)) {
            btn_ok.setText(R.string.other_ok);
        } else {
            btn_ok.setText(text);
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_ok.setVisibility(View.GONE);
                ll_close.setVisibility(View.GONE);
                npb.setVisibility(View.VISIBLE);
                listener.onClick(v);
            }
        });
        return this;
    }
    public InstallExchangeDialog setPositiveButtonNormal(String text, final View.OnClickListener listener) {
        if ("".equals(text)) {
            btn_ok.setText(R.string.other_ok);
        } else {
            btn_ok.setText(text);
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
            }
        });
        return this;
    }
    public InstallExchangeDialog setNegativeButton(final View.OnClickListener listener) {
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public static void show() {
        dialog.show();
    }

    public static void cancel() {
        dialog.dismiss();
    }



    @OnClick({R.id.btn_ok, R.id.ll_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                break;
            case R.id.ll_close:
                dialog.dismiss();
                break;
        }
    }
}
