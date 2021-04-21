package com.yjfshop123.live.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.widget.dialogorPopwindow.IOSPriceAlertDialog;
import com.yjfshop123.live.ui.widget.switchbutton.SwitchButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatPriceActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.remind_switch_video)
    SwitchButton remind_switch_video;
    @BindView(R.id.remind_switch_voice)
    SwitchButton remind_switch_voice;

    @BindView(R.id.videoPrice)
    TextView videoPrice;
    @BindView(R.id.voicePrice)
    TextView voicePrice;

    private SpeechMode speechMode;
    private ArrayList<Integer> priceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_price);
        ButterKnife.bind(this);
        setHeadLayout();
        speechMode = new SpeechMode();

        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText("聊天价格");

        videoPrice.setOnClickListener(this);
        voicePrice.setOnClickListener(this);

        remind_switch_video.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    videoPrice.setText(speechMode.getVideo_cost() == 0 ? getString(R.string.mime_mianfei) : speechMode.getVideo_cost() + getString(R.string.ql_cost));
                    speechMode.setOpen_video(1);
                    videoSpeech();
                } else {
                    speechMode.setOpen_video(0);
                    videoPrice.setText(getString(R.string.mime_close));
                    videoSpeech();
                }
            }
        });

        remind_switch_voice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    voicePrice.setText(speechMode.getSpeech_cost() == 0 ? getString(R.string.mime_mianfei) : speechMode.getSpeech_cost() + getString(R.string.ql_cost));
                    speechMode.setOpen_speech(1);
                    videoSpeech();
                } else {
                    speechMode.setOpen_speech(0);
                    voicePrice.setText(getString(R.string.mime_close));
                    videoSpeech();
                }
            }
        });

        getUserInfo();
        getCostList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.videoPrice:
                if (remind_switch_video.isChecked()) {
                    final IOSPriceAlertDialog priceAlertDialog = new IOSPriceAlertDialog(this);
                    priceAlertDialog.setData(priceList);
                    priceAlertDialog.builder().setTitle(mContext.getString(R.string.other_choose_price));
                    priceAlertDialog.setNegativeButton(mContext.getString(R.string.other_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    priceAlertDialog.setPositiveButton(mContext.getString(R.string.other_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String city = priceAlertDialog.getPriceStr();
                            try {
                                if (city.equals("免费")) {
                                    videoPrice.setText(city);
                                    speechMode.setVideo_cost(0);
                                } else {
                                    videoPrice.setText(city + getString(R.string.ql_cost));
                                    speechMode.setVideo_cost(Integer.parseInt(city));
                                }
                            } catch (Exception ex) {
                                videoPrice.setText(city);
                                speechMode.setVideo_cost(0);
                            }
                            videoSpeech();
                        }
                    });
                    priceAlertDialog.show();
                }
                break;
            case R.id.voicePrice:
                if (remind_switch_voice.isChecked()) {
                    final IOSPriceAlertDialog alertDialog = new IOSPriceAlertDialog(this);
                    alertDialog.setData(priceList);
                    alertDialog.builder().setTitle(mContext.getString(R.string.other_choose_price));
                    alertDialog.setNegativeButton(mContext.getString(R.string.other_cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                    alertDialog.setPositiveButton(mContext.getString(R.string.other_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String city = alertDialog.getPriceStr();
                            try {
                                if (city.equals("免费")) {
                                    voicePrice.setText(city);
                                    speechMode.setSpeech_cost(0);
                                } else {
                                    voicePrice.setText(city + getString(R.string.ql_cost));
                                    speechMode.setSpeech_cost(Integer.parseInt(city));
                                }
                            } catch (Exception ex) {
                                voicePrice.setText(city);
                                speechMode.setSpeech_cost(0);
                            }
                            videoSpeech();
                        }
                    });
                    alertDialog.show();
                }
                break;
        }
    }

    private void getCostList(){
        OKHttpUtils.getInstance().getRequest("app/user/getCostList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject data = new JSONObject(result);
                    JSONArray list = data.getJSONArray("list");
                    priceList.clear();
                    for (int i = 0; i < list.length(); i++) {
                        priceList.add(list.getInt(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void videoSpeech(){
        String str = null;
        try {
            str = JsonMananger.beanToJson(speechMode);
        } catch (HttpException e) {
            e.printStackTrace();
        }
        OKHttpUtils.getInstance().getRequest("app/setting/videoSpeech", str, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                getUserInfo();
                NToast.shortToast(mContext, errInfo);
            }
            @Override
            public void onSuccess(String result) {
                update();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getUserInfo(){
        OKHttpUtils.getInstance().getRequest("app/user/getUserInfo", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                if (result == null){
                    return;
                }
                try {
                    JSONObject data = new JSONObject(result);
                    speechMode.setOpen_video(data.getInt("open_video"));
                    speechMode.setVideo_cost(data.getInt("video_cost"));
                    speechMode.setOpen_speech(data.getInt("open_speech"));
                    speechMode.setSpeech_cost(data.getInt("speech_cost"));

                    update();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void update(){
        if (speechMode.getOpen_video() == 0) {
            remind_switch_video.setChecked(false);
            videoPrice.setText(getString(R.string.mime_close));
        } else if (speechMode.getOpen_video() == 1) {
            remind_switch_video.setChecked(true);
            videoPrice.setText(speechMode.getVideo_cost() == 0 ? getString(R.string.mime_mianfei) : speechMode.getVideo_cost() + getString(R.string.ql_cost));
        }

        if (speechMode.getOpen_speech() == 0) {
            remind_switch_voice.setChecked(false);
            voicePrice.setText(getString(R.string.mime_close));
        } else if (speechMode.getOpen_speech() == 1) {
            remind_switch_voice.setChecked(true);
            voicePrice.setText(speechMode.getSpeech_cost() == 0 ? getString(R.string.mime_mianfei) : + speechMode.getSpeech_cost() + getString(R.string.ql_cost));
        }
    }

    private class SpeechMode{

        private int open_video;//是否开启视频聊天 1:开启 0:关闭
        private int video_cost;//视频聊天收费，金蛋
        private int open_speech;//是否开启语音聊天 1:开启 0:关闭
        private int speech_cost;//语音聊天收费，金蛋

        public int getOpen_video() {
            return open_video;
        }

        public void setOpen_video(int open_video) {
            this.open_video = open_video;
        }

        public int getVideo_cost() {
            return video_cost;
        }

        public void setVideo_cost(int video_cost) {
            this.video_cost = video_cost;
        }

        public int getOpen_speech() {
            return open_speech;
        }

        public void setOpen_speech(int open_speech) {
            this.open_speech = open_speech;
        }

        public int getSpeech_cost() {
            return speech_cost;
        }

        public void setSpeech_cost(int speech_cost) {
            this.speech_cost = speech_cost;
        }
    }
}

