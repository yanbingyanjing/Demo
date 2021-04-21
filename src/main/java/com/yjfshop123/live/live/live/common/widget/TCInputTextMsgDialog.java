package com.yjfshop123.live.live.live.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.utils.SystemUtils;


public class TCInputTextMsgDialog extends Dialog {

    public interface OnTextSendListener {

       void onTextSend(String msg, boolean tanmuOpen);
    }
    private TextView confirmBtn;
    private LinearLayout mBarrageArea;
    private EditText messageTextView;
    private Context mContext;
    private InputMethodManager imm;
    private RelativeLayout rlDlg;
    private int mLastDiff = 0;
    private OnTextSendListener mOnTextSendListener;
    private boolean mDanmuOpen = false;
//    private final String reg = "[`~@#$%^&*()-_+=|{}':;,/.<>￥…（）—【】‘；：”“’。，、]";
//    private Pattern pattern = Pattern.compile(reg);

    public TCInputTextMsgDialog(final Context context, int theme) {
        super(context, theme);
        mContext = context;
        setContentView(R.layout.dialog_input_text);

        messageTextView = (EditText) findViewById(R.id.et_input_message);
        messageTextView.setInputType(InputType.TYPE_CLASS_TEXT);
        //修改下划线颜色
        messageTextView.getBackground().setColorFilter(context.getResources().getColor(R.color.transparent), PorterDuff.Mode.CLEAR);


        confirmBtn = (TextView) findViewById(R.id.confrim_btn);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = messageTextView.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {

                    mOnTextSendListener.onTextSend(msg, mDanmuOpen);
                    imm.showSoftInput(messageTextView, InputMethodManager.SHOW_FORCED);
                    imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                    messageTextView.setText("");
                    dismiss();
                } else {
                    NToast.shortToast(mContext, "输入内容不能为空哟!");
                }
                messageTextView.setText(null);
            }
        });

        final Button barrageBtn = (Button) findViewById(R.id.barrage_btn);
        barrageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDanmuOpen = !mDanmuOpen;
                if (mDanmuOpen) {
                    barrageBtn.setBackgroundResource(R.drawable.barrage_slider_on);
                } else {
                    barrageBtn.setBackgroundResource(R.drawable.barrage_slider_off);
                }
            }
        });

        mBarrageArea = (LinearLayout) findViewById(R.id.barrage_area);
        mBarrageArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDanmuOpen = !mDanmuOpen;
                if (mDanmuOpen) {
                    barrageBtn.setBackgroundResource(R.drawable.barrage_slider_on);
                } else {
                    barrageBtn.setBackgroundResource(R.drawable.barrage_slider_off);
                }
            }
        });

        messageTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case KeyEvent.KEYCODE_ENDCALL:
                    case KeyEvent.KEYCODE_ENTER:
                        if (messageTextView.getText().length() > 0) {
//                            mOnTextSendListener.onTextSend("" + messageTextView.getText(), mDanmuOpen);
                            //sendText("" + messageTextView.getText());
                            //imm.showSoftInput(messageTextView, InputMethodManager.SHOW_FORCED);
                            imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
//                            messageTextView.setText("");
                            dismiss();
                        } else {
                            NToast.shortToast(mContext, "输入内容不能为空哟!");
                        }
                        return true;
                    case KeyEvent.KEYCODE_BACK:
                        dismiss();
                        return false;
                    default:
                        return false;
                }
            }
        });

        messageTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.d("My test", "onKey " + keyEvent.getCharacters());
                return false;
            }
        });

        rlDlg = (RelativeLayout) findViewById(R.id.rl_outside_view);
        rlDlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() != R.id.rl_inputdlg_view)
                    dismiss();
            }
        });

        final LinearLayout rldlgview = (LinearLayout) findViewById(R.id.rl_inputdlg_view);

        rldlgview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                Rect r = new Rect();
                //获取当前界面可视部分
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
              //  int screenHeight =  getWindow().getDecorView().getRootView().getHeight();
                int screenHeight = SystemUtils.getScreenHeight(context,false);
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数


                int heightDifference = screenHeight - r.bottom;
              //  NLog.d("直播评论","屏幕高度"+screenHeight+"   "+ r.bottom+" "+heightDifference+" "+mLastDiff);
                if (heightDifference <= 0 && mLastDiff > 0){
                    //imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                    dismiss();
                }
                mLastDiff = heightDifference;
            }
        });
        rldlgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(messageTextView.getWindowToken(), 0);
                dismiss();
            }
        });

        //设置可获得焦点
        messageTextView.setFocusable(true);
        messageTextView.setFocusableInTouchMode(true);
        //请求获得焦点
        messageTextView.requestFocus();
        mHandler = new Handler();
        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showKeyboard();
                    }
                },300);
            }
        });
    }

    private Handler mHandler;

    private void showKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) messageTextView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(messageTextView, 0);
    }

    public void setmOnTextSendListener(OnTextSendListener onTextSendListener) {
        this.mOnTextSendListener = onTextSendListener;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //dismiss之前重置mLastDiff值避免下次无法打开
        mLastDiff = 0;
    }

    @Override
    public void show() {
        super.show();
    }
}
