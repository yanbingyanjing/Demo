package com.yjfshop123.live.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.pandaq.emoticonlib.PandaEmoTranslator;

import cn.jiguang.imui.chatinput.emoji.Constants;
import cn.jiguang.imui.chatinput.emoji.EmojiBean;
import cn.jiguang.imui.chatinput.emoji.EmojiView;
import cn.jiguang.imui.chatinput.emoji.data.EmoticonEntity;
import cn.jiguang.imui.chatinput.emoji.listener.EmoticonClickListener;
import cn.jiguang.imui.chatinput.utils.SimpleCommonUtils;

public class ReplyDialogActivity extends Activity {

    private EmojiView mEmojiView;
    private EditText mEditText;
    private ImageView mEmojiIv;
    private TextView mSend;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reply_dialog_activity);

        mContext = getApplicationContext();

        findViewById(R.id.reply_dialog_a_fl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFinish(1);
            }
        });

        String replyContent = getIntent().getStringExtra("replyContent");

        mEmojiView = findViewById(R.id.reply_dialog_a_emoji_container);

        mEditText = findViewById(R.id.reply_dialog_a_et);
        mEmojiIv = findViewById(R.id.reply_dialog_a_emoji_iv);
        mSend = findViewById(R.id.reply_dialog_a_send);

        mEmojiView.setAdapter(SimpleCommonUtils.getCommonAdapter(mContext, emoticonClickListener));

        mEmojiIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKeyboard();
                initBiaoQing();
            }
        });

        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissEmojiLayout();
            }
        });

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFinish(2);
            }
        });

        findViewById(R.id.reply_dialog_a_fl_).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mEditText.addTextChangedListener(mTextWatcher);
        mEditText.setText(replyContent);
        mEditText.setSelection(replyContent.length());

        //设置可获得焦点
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        //请求获得焦点
        mEditText.requestFocus();
    }

    private EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {

            if (isDelBtn) {
                SimpleCommonUtils.delClick(mEditText);
            } else {
                if (o == null) {
                    return;
                }
                if (actionType == Constants.EMOTICON_CLICK_BIGIMAGE) {
                    // if(o instanceof EmoticonEntity){
                    // OnSendImage(((EmoticonEntity)o).getIconUri());
                    // }
                } else {
                    String content_ = null;
                    if (o instanceof EmojiBean) {
                        content_ = ((EmojiBean) o).emoji;
                    } else if (o instanceof EmoticonEntity) {
                        content_ = ((EmoticonEntity) o).getContent();
                    }

                    if (TextUtils.isEmpty(content_)) {
                        return;
                    }

                    /*int index = mEditText.getSelectionStart();
                    Editable editable = mEditText.getText();
                    editable.insert(index, content_);*/

                    Editable editable = mEditText.getText();
                    int start = mEditText.getSelectionStart();
                    int end = mEditText.getSelectionEnd();
                    start = (start < 0 ? 0 : start);
                    end = (start < 0 ? 0 : end);
                    editable.replace(start, end, content_);
                    int editEnd = mEditText.getSelectionEnd();
                    PandaEmoTranslator.getInstance().replaceEmoticons(editable, 0, editable.toString().length());
                    mEditText.setSelection(editEnd);
                }
            }
        }
    };

    private void hintKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initBiaoQing() {
        if (mEmojiView.getVisibility() == View.VISIBLE) {
            dismissEmojiLayout();
        } else {
            handler.sendEmptyMessageDelayed(10, 100);
        }
    }

    public void showEmojiLayout() {
        mEmojiView.setVisibility(View.VISIBLE);
    }

    public void dismissEmojiLayout() {
        mEmojiView.setVisibility(View.GONE);
    }

    private void getFinish(int type){
        Intent intent = new Intent();
        intent.putExtra("replyContent", mEditText.getText().toString());
        intent.putExtra("type", type);
        setResult(222, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mEmojiView.getVisibility() != View.GONE) {
                dismissEmojiLayout();
            }else {
                getFinish(1);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10){
                showEmojiLayout();
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (TextUtils.isEmpty(editable.toString())) {
                mSend.setClickable(false);
                mSend.setBackground(getResources().getDrawable(R.drawable.shape_a5a5a5_20_button));
            } else {
                mSend.setClickable(true);
                mSend.setBackground(getResources().getDrawable(R.drawable.shape_ffd100_20_button));
            }
        }
    };
}
