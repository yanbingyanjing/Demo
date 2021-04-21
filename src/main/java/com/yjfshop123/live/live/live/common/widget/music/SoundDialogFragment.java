package com.yjfshop123.live.live.live.common.widget.music;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.MLVBLiveRoom;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.video.VideoRecordActivity;

public class SoundDialogFragment extends AbsDialogFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private MLVBLiveRoom mLiveRoom;
    private TextView liveSoundPro;
    private TextView livePersonPro;
    private SeekBar livePersonSeekBar, liveSoundSeekBar;
    private static int              mMicVolume = 50;
    private static int              mBGMVolume = 50;
    private VideoRecordActivity recordActivity;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_sound;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = CommonUtils.dip2px(mContext, 285);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Bundle bundle = getArguments();
//        if (bundle == null) {
//            return;
//        }

        if (mContext instanceof TCLiveBasePublisherActivity) {
            mLiveRoom = ((TCLiveBasePublisherActivity) mContext).getmLiveRoom();
        }else if (mContext instanceof VideoRecordActivity){
            recordActivity = (VideoRecordActivity) mContext;
        }

        liveSoundPro = (TextView) findViewById(R.id.live_sound_pro);
        livePersonPro = (TextView) findViewById(R.id.live_person_pro);
        livePersonSeekBar = (SeekBar) findViewById(R.id.live_person_seekBar);
        liveSoundSeekBar = (SeekBar) findViewById(R.id.live_sound_seekBar);

        liveSoundSeekBar.setOnSeekBarChangeListener(this);
        livePersonSeekBar.setOnSeekBarChangeListener(this);

        initData();
    }

    @Override
    public void onClick(View v) {
//        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initData() {
        if (mLiveRoom != null){
            mLiveRoom.setMicVolumeOnMixing(mMicVolume * 2);
            mLiveRoom.setBGMVolume(mBGMVolume * 2);
        }else {
            recordActivity.setMicVolumeOnMixing(mMicVolume * 2);
            recordActivity.setBGMVolume(mBGMVolume * 2);
        }

        livePersonPro.setText(String.valueOf(mMicVolume));
        livePersonSeekBar.setProgress(mMicVolume);

        liveSoundPro.setText(String.valueOf(mBGMVolume));
        liveSoundSeekBar.setProgress(mBGMVolume);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.live_person_seekBar:
                //人声
                mMicVolume = progress;
                if (mLiveRoom != null){
                    mLiveRoom.setMicVolumeOnMixing(mMicVolume * 2);
                }else {
                    recordActivity.setMicVolumeOnMixing(mMicVolume * 2);
                }
                livePersonPro.setText(String.valueOf(mMicVolume));
                break;
            case R.id.live_sound_seekBar:
                //伴奏
                mBGMVolume = progress;
                if (mLiveRoom != null){
                    mLiveRoom.setBGMVolume(mBGMVolume * 2);
                }else {
                    recordActivity.setBGMVolume(mBGMVolume * 2);
                }
                liveSoundPro.setText(String.valueOf(mBGMVolume));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

