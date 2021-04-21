package com.yjfshop123.live.server.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.yjfshop123.live.R;


public class  LoadDialog extends Dialog {

    /**
     * LoadDialog
     */
    private static LoadDialog loadDialog;
    /**
     * canNotCancel, the mDialogTextView dimiss or undimiss flag
     */
    private boolean canNotCancel;

    private ImageView mm_hint_iv;
    private static AnimationDrawable frameAnimation;

    /**
     * the LoadDialog constructor
     *
     * @param ctx          Context
     * @param canNotCancel boolean
     */
    public LoadDialog(final Context ctx, boolean canNotCancel) {
        super(ctx);

        this.canNotCancel = canNotCancel;
        this.getContext().setTheme(android.R.style.Theme_InputMethod);
        setContentView(R.layout.layout_dialog_loading);
        mm_hint_iv = findViewById(R.id.mm_hint_iv);

        frameAnimation = (AnimationDrawable) mm_hint_iv.getBackground();
        frameAnimation.start();

        Window window = getWindow();
        WindowManager.LayoutParams attributesParams = window.getAttributes();
        attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        attributesParams.dimAmount = 0.5f;
        window.setAttributes(attributesParams);

        window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (canNotCancel) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * show the mDialogTextView
     *
     * @param context Context
     */
    public static void show(Context context) {
        show(context, false);
    }

    /**
     * show the mDialogTextView
     *
     * @param context  Context
     * @param isCancel boolean, true is can't dimiss，false is can dimiss
     */
    private static void show(Context context, boolean isCancel) {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        if (loadDialog != null && loadDialog.isShowing()) {
            return;
        }
        loadDialog = new LoadDialog(context, isCancel);
        loadDialog.show();
    }



    /**
     * dismiss the mDialogTextView
     */
    public static void dismiss(Context context) {
        try {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    loadDialog = null;
                    return;
                }
            }
            if (frameAnimation != null) {
                frameAnimation.stop();
                frameAnimation = null;
            }
            if (loadDialog != null && loadDialog.isShowing()) {
                Context loadContext = loadDialog.getContext();
                if (loadContext != null && loadContext instanceof Activity) {
                    if (((Activity) loadContext).isFinishing()) {
                        loadDialog = null;
                        return;
                    }
                }
                loadDialog.dismiss();
                loadDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadDialog = null;
        }
    }
}
