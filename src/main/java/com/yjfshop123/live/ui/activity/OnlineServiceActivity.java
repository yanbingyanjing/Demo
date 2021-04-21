package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.utils.NToast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnlineServiceActivity extends BaseActivityForNewUi implements View.OnClickListener {
    private static final int MAX_NUM = 300;

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.yiJian)
    EditText yiJian;
    @BindView(R.id.currentTxt)
    TextView currentTxt;
    @BindView(R.id.submitBtn)
    Button submitBtn;
//    @BindView(R.id.callTxt)
//    EditText callTxt;
//
//    @BindView(R.id.online_name)
//    TextView online_name;
//
//    @BindView(R.id.online_content)
//    TextView online_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_service);
        ButterKnife.bind(this);
        setHeadLayout();
        initView();
    }

    private void initView() {
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText(R.string.online_fankui);

        submitBtn.setOnClickListener(this);

        yiJian.addTextChangedListener(watcher);

        getContact();
    }


    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //只要编辑框内容有变化就会调用该方法，s为编辑框变化后的内容
            Log.i("onTextChanged", s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //编辑框内容变化之前会调用该方法，s为编辑框内容变化之前的内容
            Log.i("beforeTextChanged", s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            //编辑框内容变化之后会调用该方法，s为编辑框内容变化后的内容
            Log.i("afterTextChanged", s.toString());
            if (s.length() > MAX_NUM) {
                s.delete(MAX_NUM, s.length());
            }
            int num = MAX_NUM - s.length();
            currentTxt.setText(s.length() + "/" + MAX_NUM + getString(R.string.online_zi));
        }
    };

    private void getContact(){
//        OKHttpUtils.getInstance().getRequest("app/public/getContact", "", new RequestCallback() {
//            @Override
//            public void onError(int errCode, String errInfo) {
//            }
//            @Override
//            public void onSuccess(String result) {
//                try {
//                    JSONObject data = new JSONObject(result);
//                    online_name.setText(data.getString("type"));
//                    String content = data.getString("content");
//                    if (TextUtils.isEmpty(content)){
//                        online_content.setText("暂无");
//                    }else {
//                        online_content.setText(content);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitBtn:
//                if (yiJian.getText().toString().trim().equals("") || callTxt.getText().toString().equals("")) {
//                    NToast.shortToast(this, R.string.online_err);
//                    return;
//                }
                if (yiJian.getText().toString().trim().equals("") ) {
                    NToast.shortToast(this, R.string.online_err);
                    return;
                }
                feedbackPost();
                break;
        }
    }

    private void feedbackPost(){
        String body = "";
        try {
//            body = new JsonBuilder()
//                    .put("content", callTxt.getText().toString() + ";" + yiJian.getText().toString())
//                    .build();
            body = new JsonBuilder()
                   .put("content", yiJian.getText().toString())
                   .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/user/feedbackPost", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                NToast.shortToast(mContext, R.string.online_success);
                finish();
            }
        });
    }


}
