package com.yjfshop123.live.ui.widget.dialogorPopwindow;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;

/**
 *
 * 日期:2018/11/1
 * 描述:上传弹窗
 **/
public class UploadDialog extends BaseDialog implements View.OnClickListener {

    private Activity context;
    private LinearLayout lLayout_bg;
    private Display display;
    private TextView uploadTxt;
    private ImageView upImg;
    public TextView uploadProcess;
    private int total;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    uploadProcess.setText(msg.arg1 + "%");
                    break;
                case 2:
                    uploadProcess.setText(msg.arg1 + "/" + total);
                    break;
            }
        }
    };

    public UploadDialog(Activity context) {
        super(context, R.style.customDialog);
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_upload_dialog);
//        dialog = new Dialog(context, R.style.AlertDialogStyle);
//        dialog.setContentView(this);

        lLayout_bg = (LinearLayout) findViewById(R.id.lLayout_bg);
        uploadTxt = (TextView) findViewById(R.id.uploadTxt);
        uploadProcess = (TextView) findViewById(R.id.uploadProcess);
        upImg = (ImageView) findViewById(R.id.upImg);

//        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.50), ViewGroup.LayoutParams.WRAP_CONTENT));
        upImg.setOnClickListener(this);
    }
int process=0;
    public void setProcess(final int process) {
        Log.d("PutObject", process + "%");
       this.process=process;
        if (process >= 100) {
            dissmis();
        }
        Message mes = handler.obtainMessage(1, process);
        mes.arg1 = process;
        mes.sendToTarget();
    }
public int getProgress(){
        return process;
}
    /**
     * 上传录音
     */
    public void uploadVoice() {
        if (this.isShowing()) {
            uploadTxt.setText(R.string.other_upload_voice);
            uploadProcess.setText("0");
        }
    }

    /**
     * 上传视频
     */
    public void uploadVideo() {
        if (this.isShowing()) {
            uploadTxt.setText(R.string.other_upload_video);
            uploadProcess.setText("0");
        }
    }

    /**
     * 上传录音图片
     */
    public void uploadPhoto(int total) {
        this.total = total;
        if (this.isShowing()) {
            uploadTxt.setText(R.string.other_upload_image);
            uploadProcess.setText("1/" + total);
        }
    }

    public void setPhotoProcess(final int process) {
        if (process == total) {
//            context.finish();
            dissmis();
        }
        Message mes = handler.obtainMessage(2, process);
        mes.arg1 = process;
        mes.sendToTarget();
    }

    public void show(int dragin) {
        this.showAnim(dragin);
    }

    public void dissmis() {
        this.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upImg:
                dissmis();
                break;
        }
    }
}
