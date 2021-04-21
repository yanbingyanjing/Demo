package com.yjfshop123.live.live.live.push.screen;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.common.utils.TCUtils;
import com.yjfshop123.live.live.live.common.widget.TCUserAvatarListAdapter;
import com.yjfshop123.live.live.live.common.widget.other.ContributeListFragment;
import com.yjfshop123.live.live.live.common.widget.other.LiveUserDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.ManagerDialogFragment;
import com.yjfshop123.live.live.live.common.widget.other.ManagerListFragment;
import com.yjfshop123.live.live.live.list.TCSimpleUserInfo;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.live.live.push.screen.widget.FloatingCameraView;
import com.yjfshop123.live.live.live.push.screen.widget.FloatingView;
import com.yjfshop123.live.live.response.MegUserInfo;
import com.yjfshop123.live.live.response.UserInfo4LiveResponse;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.socket.SocketUtil;
import com.bumptech.glide.Glide;
import com.umeng.socialize.UMShareAPI;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 屏幕录制Activity
 * 注：Android在API21+的版本才支持屏幕录制功能
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class TCScreenRecordActivity extends TCLiveBasePublisherActivity {

    //观众头像列表控件
    private RecyclerView mUserAvatarList;

    //主播信息
    private Timer mBroadcastTimer;
    private BroadcastTimerTask mBroadcastTimerTask;


    //悬浮摄像窗以及悬浮球
    private FloatingView mFloatingView;
    private FloatingCameraView mFloatingCameraView;
    private ImageView mPrivateBtn;
    private ImageView mCameraBtn;

    //隐私模式drawable(支持自定义大小)
//    private TextView mTVPrivateMode;
//    private Drawable mDrawableLockOn;
//    private Drawable mDrawableLockOff;

    private boolean mInPrivacy = false;
    private boolean mInCamera = false;

    private Intent serviceIntent;

    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //启动后台拉活进程（处理强制下线消息）
        serviceIntent = new Intent();
        serviceIntent.setClassName(this, TCScreenRecordService.class.getName());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }else{
            startService(serviceIntent);
        }
        //bindService(intentService, mServiceConn, BIND_AUTO_CREATE);
    }

    protected void initView() {
        setContentView(R.layout.activity_screen_record);
        super.initView();

        mUserAvatarList = (RecyclerView) findViewById(R.id.rv_user_avatar);
        mAvatarListAdapter = new TCUserAvatarListAdapter(this, zbUserId, zbUserId, mLiveID);
        mUserAvatarList.setAdapter(mAvatarListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);

        TextView tv_broadcasting_name = findViewById(R.id.tv_broadcasting_name);
        tv_broadcasting_name.setText(TCUtils.getLimitString(zbName, 10));
        TextView tv_host_id = findViewById(R.id.tv_host_id);
        tv_host_id.setText("ID:" + zbUserId);
        ImageView headIcon = findViewById(R.id.iv_head_icon);
        showHeadIcon(headIcon, zbAvatar);

        mMemberCount = findViewById(R.id.tv_member_counts);
        mMemberCount.setText("0");

        //悬浮球界面
        mFloatingView = new FloatingView(getApplicationContext(), R.layout.view_floating_default);
        mFloatingView.setPopupWindow(R.layout.popup_layout);
        mFloatingCameraView = new FloatingCameraView(getApplicationContext());
        mPrivateBtn = mFloatingView.getPopupView().findViewById(R.id.btn_privacy);
        mCameraBtn = mFloatingView.getPopupView().findViewById(R.id.btn_camera);
        mFloatingView.setOnPopupItemClickListener(this);


        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_message_input).setOnClickListener(this);

        /*mDrawableLockOn = getResources().getDrawable(R.mipmap.lock_off);
        if (null != mDrawableLockOn) mDrawableLockOn .setBounds(0, 0, 40, 40);
        mDrawableLockOff = getResources().getDrawable(R.mipmap.lock_on);
        if (null != mDrawableLockOff) mDrawableLockOff.setBounds(0, 0, 40, 40);
        mTVPrivateMode = findViewById(R.id.tv_private_mode);
        if (null != mTVPrivateMode)  mTVPrivateMode.setCompoundDrawables(mDrawableLockOff ,null, null, null);*/
    }

    private void showHeadIcon(ImageView view, String avatar) {
        Glide.with(mContext)
                .load(avatar)
                .into(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveUserDialogFragment fragment = new LiveUserDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("user_id", zbUserId);
                bundle.putString("meUserId", zbUserId);
                bundle.putString("liveUid", zbUserId);
                bundle.putString("liveID", mLiveID);
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "LiveUserDialogFragment");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        if (mArrayListChatEntity == null || mArrayListChatEntity.size() < 0){
            return;
        }
        if (mArrayListChatEntity.get(position).getType() == TCConstants.IMCMD_SYSTEM){
            return;
        }
        LiveUserDialogFragment fragment = new LiveUserDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", mArrayListChatEntity.get(position).getUser_id());
        bundle.putString("meUserId", zbUserId);
        bundle.putString("liveUid", zbUserId);
        bundle.putString("liveID", mLiveID);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveUserDialogFragment");
    }

    protected void startPublishImpl() {
        mLiveRoom.setListener(this);
//        mLiveRoom.setCameraMuteImage(BitmapFactory.decodeResource(getResources(), R.mipmap.recording_background_private_vertical));
        mLiveRoom.startScreenCapture();
        super.startPublishImpl();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //关闭悬浮球与相机
//        if (mScrOrientation == TCConstants.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            mRootRelativeLayout.setBackground(getDrawable(R.mipmap.recording_background_horizontal));
//        }

        if (mFloatingView.isShown()) {
            mFloatingView.dismiss();
        }
        if (null != mFloatingCameraView && mFloatingCameraView.isShown()) {
            mFloatingCameraView.dismiss();
            mCameraBtn.setImageResource(R.mipmap.camera_off);
            mInCamera = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void requestDrawOverLays() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N && !Settings.canDrawOverlays(TCScreenRecordActivity.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + TCScreenRecordActivity.this.getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        } else {
            showFloatingView();
        }
    }

    private void showFloatingView() {
        if (!mFloatingView.isShown()) {
            if ((null != mLiveRoom)) {
//                if (mLiveRoom.isPushing()) {
                    mFloatingView.show();
                    mFloatingView.setOnPopupItemClickListener(this);
//                    mTXLivePusher.resumePusher();
//                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        requestDrawOverLays();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopRecordAnimation();

        if (mFloatingView.isShown()) {
            mFloatingView.dismiss();
        }

        if (null != mFloatingCameraView) {
            if (mFloatingCameraView.isShown()) {
                mFloatingCameraView.dismiss();
            }
            mFloatingCameraView.release();
        }

        //unbindService(mServiceConn);
        stopService(serviceIntent);

        stopPublish();

        mLiveID = null;

//        long endPushPts = System.currentTimeMillis();
//        long diff = (endPushPts - mStartPushPts) / 1000;
//        TCUserMgr.getInstance().uploadLogs(TCConstants.ELK_ACTION_SCREEN_PUSH_DURATION, TCUserMgr.getInstance().getUserId(), diff, "录屏推流时长", null);
    }

    @Override
    public void onBackPressed() {
        showComfirmDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                //悬浮球返回主界面按钮
                Toast.makeText(getApplicationContext(), "返回主界面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), TCScreenRecordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                break;
//            case R.id.tv_private_mode:
            case R.id.btn_privacy:
                //隐私模式
//                triggerPrivateMode();
                break;
            case R.id.btn_camera:
                //camera悬浮窗
                triggerFloatingCameraView();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

//    /**
//     * 隐私模式切换
//     */
//    public void triggerPrivateMode() {
//        if (mInPrivacy) {
//            Toast.makeText(getApplicationContext(), getString(R.string.private_mode_off), Toast.LENGTH_SHORT).show();
//            mTVPrivateMode.setText(getString(R.string.private_mode_off));
//            mTVPrivateMode.setCompoundDrawables(mDrawableLockOn,null,null,null);
//            mPrivateBtn.setImageResource(R.mipmap.lock_off);
//            mLiveRoom.switchToForeground();
//        } else {
//            Toast.makeText(getApplicationContext(), getString(R.string.private_mode_on), Toast.LENGTH_SHORT).show();
//            mLiveRoom.switchToBackground();
//            mPrivateBtn.setImageResource(R.mipmap.lock_on);
//            mTVPrivateMode.setText(getString(R.string.private_mode_on));
//            mTVPrivateMode.setCompoundDrawables(mDrawableLockOff,null,null,null);
//        }
//        mInPrivacy = !mInPrivacy;
//    }

    /**
     * 处理cameraview初始化、权限申请 以及 cameraview的显示与隐藏
     */
    public void triggerFloatingCameraView() {
        //trigger
        if (mInCamera) {
            Toast.makeText(getApplicationContext(), "关闭摄像头", Toast.LENGTH_SHORT).show();
            mCameraBtn.setImageResource(R.mipmap.camera_off);
            mFloatingCameraView.dismiss();
        } else {
            //show失败显示错误信息
            if (!mFloatingCameraView.show()) {
                Toast.makeText(getApplicationContext(), "打开摄像头权限失败,请在系统设置打开摄像头权限", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "打开摄像头", Toast.LENGTH_SHORT).show();
            mCameraBtn.setImageResource(R.mipmap.camera_on);
        }
        mInCamera = !mInCamera;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N && !Settings.canDrawOverlays(TCScreenRecordActivity.this)) {
                Toast.makeText(getApplicationContext(), "请在设置-权限设置里打开悬浮窗权限", Toast.LENGTH_SHORT).show();
            } else {
                showFloatingView();
            }
        } else {
            /** attention to this below ,must add this**/
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
            NLog.d("result", "onActivityResult");
        }
    }

    @Override
    public void onEnterLive(MegUserInfo bean) {
        TCSimpleUserInfo userInfo = new TCSimpleUserInfo(bean.getUser_id(), bean.getNickname(), bean.getAvatar());
        //更新头像列表 返回false表明已存在相同用户，将不会更新数据
        if (!mAvatarListAdapter.addItem(userInfo))
            return;
        super.onEnterLive(bean);
        mMemberCount.setText(String.format(Locale.CHINA, "%d", lMemberCount));
    }

    @Override
    public void onExitLive(MegUserInfo bean) {
        mAvatarListAdapter.removeItem(bean.getUser_id());
        super.onExitLive(bean);
        mMemberCount.setText(String.format(Locale.CHINA, "%d", lMemberCount));
    }



    protected void onCreateRoomSucess() {
        super.onCreateRoomSucess();
        mAvatarListAdapter.setLiveID(mLiveID);
        startRecordAnimation();
    }

    private ObjectAnimator mObjAnim;

    /**
     * 开启红点与计时动画
     */
    private void startRecordAnimation() {
        ImageView iv = mLiveRoomViewHolder.getmRoomTimeIv();
        if (iv != null){
            mObjAnim = ObjectAnimator.ofFloat(iv, "alpha", 1f, 0f, 1f);
            mObjAnim.setDuration(1000);
            mObjAnim.setRepeatCount(-1);
            mObjAnim.start();

            //直播时间
            if (mBroadcastTimer == null) {
                mBroadcastTimer = new Timer(true);
                mBroadcastTimerTask = new BroadcastTimerTask();
                mBroadcastTimer.schedule(mBroadcastTimerTask, 1000, 1000);
            }
        }
    }

    /**
     * 关闭红点与计时动画
     */
    private void stopRecordAnimation() {

        if (null != mObjAnim)
            mObjAnim.cancel();

        //直播时间
        if (null != mBroadcastTimer) {
            mBroadcastTimerTask.cancel();
        }
    }

    /**
     * 记时器
     */
    private class BroadcastTimerTask extends TimerTask {
        public void run() {
            ++mSecond;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mTCSwipeAnimationController.isMoving()){
                        TextView tv = mLiveRoomViewHolder.getmRoomTimeTv();
                        if (tv != null){
                            tv.setText(TCUtils.formattedTime(mSecond));
                        }

//                        if (mHeartLayout != null) {
//                            mHeartLayout.addFavor();
//                        }
                    }
                }
            });
        }
    }

    public void manager(UserInfo4LiveResponse response){
        ManagerDialogFragment fragment = new ManagerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", String.valueOf(response.getUser_info().getUser_id()));//用户ID
        bundle.putString("meUserId", zbUserId);//自己ID
        bundle.putString("liveUid", zbUserId);//主播ID
        bundle.putString("mLiveId", mLiveID);//直播间ID
        bundle.putString("is_banspeech", String.valueOf(response.getUser_info().getIs_banspeech()));//是否被禁言
        bundle.putString("is_manager", String.valueOf(response.getUser_info().getIs_manager()));
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "ManagerDialogFragment");
    }

    public void managerList(){
        ManagerListFragment fragment = new ManagerListFragment();
        fragment.show(getSupportFragmentManager(), "ManagerListFragment");
    }

    public void contributeList(){
        ContributeListFragment fragment = new ContributeListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("live_id", mLiveID);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "ContributeListFragment");
    }

    public void exit(){
        super.exit();
        SocketUtil.getInstance().cancelSocketMsgListener();
        finish();
    }

    public void beauty(){
        NToast.shortToast(mContext, "录屏不支持美颜哦~");
    }
}
