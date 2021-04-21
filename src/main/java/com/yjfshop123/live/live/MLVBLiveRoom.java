package com.yjfshop123.live.live;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.yjfshop123.live.live.commondef.AnchorInfo;
import com.faceunity.FURenderer;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;


public abstract class MLVBLiveRoom {

    public static MLVBLiveRoom sharedInstance(Context context) {
        return MLVBLiveRoomImpl.sharedInstance(context);
    }

    /**
     * 设置回调接口
     *
     * @param listener
     */
    public abstract void setListener(IMLVBLiveRoomListener listener);

    /**
     * 开始推流 主播
     *
     * @param live_id
     * @param pushURL
     * @param callback
     */
    public abstract void createRoom(String live_id, String pushURL, IMLVBLiveRoomListener.CreateRoomCallback callback);

    /**
     * 进入房间（观众调用）
     *
     * @param vod_type
     * @param live_id
     * @param mixedPlayURL
     * @param view
     * @param callback
     */
    public abstract void enterRoom(int vod_type, String live_id, String mixedPlayURL, final TXCloudVideoView view, final IMLVBLiveRoomListener.EnterRoomCallback callback);

    /**
     * 离开房间
     *
     * @param callback
     */
    public abstract void exitRoom(IMLVBLiveRoomListener.ExitRoomCallback callback);

    /**
     * 发送消息
     *
     * @param cmd
     * @param message
     * @param callback
     */
    public abstract void sendRoomMsg(int cmd, String message, IMLVBLiveRoomListener.RequestCallback callback);


    /**
     * 改加速流
     *
     * @param accelerateURL
     */
    public abstract void startPlayAccelerateURL(String accelerateURL);

    /**
     * 进入连麦状态
     *
     * @param callback
     */
    public abstract void joinAnchor(String selfPushUrl, final IMLVBLiveRoomListener.JoinAnchorCallback callback);

    /**
     * 观众退出连麦
     */
    public abstract void quitJoinAnchor(String userId);

    /**
     * 主播踢除连麦观众
     *
     * @param userID
     */
    public abstract void kickoutJoinAnchor(String userID);

    /**
     * 开启本地视频的预览画面
     *
     * @param frontCamera
     * @param view
     * @param fuRenderer
     */
    public abstract void startLocalPreview(boolean frontCamera, TXCloudVideoView view, FURenderer fuRenderer);

    /**
     * 停止本地视频采集及预览
     */
    public abstract void stopLocalPreview();

    /**
     * 启动渲染远端视频画面
     *
     * @param anchorInfo
     * @param view
     * @param callback
     */
    public abstract void startRemoteView(final AnchorInfo anchorInfo, final TXCloudVideoView view, final IMLVBLiveRoomListener.PlayCallback callback);

    /**
     * 停止渲染远端视频画面
     *
     * @param anchorInfo
     */
    public abstract void stopRemoteView(final AnchorInfo anchorInfo);

    /**
     * 完成PK
     *
     * @param live_id
     * @param isLaunch
     */
    public abstract void finishPK(String live_id, boolean isLaunch);

    /**
     * 关闭PK
     *
     * @param live_id
     * @param isLaunch
     */
    public abstract void closePK(String live_id, boolean isLaunch);


    /**
     * 启动录屏。
     *
     */
    public abstract void startScreenCapture();

    /**
     * 结束录屏。
     *
     */
    public abstract void stopScreenCapture();

    /**
     * 是否屏蔽本地音频
     *
     * @param mute
     */
    public abstract void muteLocalAudio(boolean mute);

    /**
     * 设置指定用户是否静音
     *
     * @param userID 对方的用户标识
     * @param mute true:静音 false:非静音
     */
    public abstract void muteRemoteAudio(String userID, boolean mute);

    /**
     * 设置所有远端用户是否静音
     *
     * @param mute true:静音 false:非静音
     */
    public abstract void muteAllRemoteAudio(boolean mute);

    /**
     * 切换摄像头
     */
    public abstract void switchCamera();

    /**
     * 设置摄像头缩放因子（焦距）
     *
     * @param distance 取值范围 1 - 5 ，当为1的时候为最远视角（正常镜头），当为5的时候为最近视角（放大镜头），这里最大值推荐为5，超过5后视频数据会变得模糊不清
     */
    public abstract boolean setZoom(int distance);

    /**
     * 开关闪光灯
     *
     * @param enable true：开启；false：关闭
     */
    public abstract boolean enableTorch(boolean enable);
	
	/**
     * 主播屏蔽摄像头期间需要显示的等待图片
	 *
	 * 当主播屏蔽摄像头，或者由于 App 切入后台无法使用摄像头的时候，我们需要使用一张等待图片来提示观众“主播暂时离开，请不要走开”。
     *
     * @param bitmap 位图
     */
    public abstract void setCameraMuteImage(Bitmap bitmap);

    /**
     * 主播屏蔽摄像头期间需要显示的等待图片
	 *
	 * 当主播屏蔽摄像头，或者由于 App 切入后台无法使用摄像头的时候，我们需要使用一张等待图片来提示观众“主播暂时离开，请不要走开”。
     *
     * @param id 设置默认显示图片的资源文件
     */
    public abstract void setCameraMuteImage(final int id);

    public abstract void resumePush();

    public abstract void pausePush();

    /**
     * 设置美颜、美白、红润效果级别
     *
     * @param beautyStyle 美颜风格，三种美颜风格：0 ：光滑；1：自然；2：朦胧
     * @param beautyLevel 美颜级别，取值范围 0 - 9； 0 表示关闭， 1 - 9值越大，效果越明显
     * @param whitenessLevel 美白级别，取值范围 0 - 9； 0 表示关闭， 1 - 9值越大，效果越明显
     * @param ruddinessLevel 红润级别，取值范围 0 - 9； 0 表示关闭， 1 - 9值越大，效果越明显
     */
    public abstract boolean setBeautyStyle(int beautyStyle , int beautyLevel, int whitenessLevel, int ruddinessLevel);

    /**
     * 设置指定素材滤镜特效
     *
     * @param image 指定素材，即颜色查找表图片。注意：一定要用 png 格式！！！
     */
    public abstract void setFilter(Bitmap image);

    /**
     * 设置滤镜浓度
     *
     * @param concentration 从0到1，越大滤镜效果越明显，默认取值0.5
     */
    public abstract void setFilterConcentration(float concentration);

    /**
     * 添加水印，height 不用设置，sdk 内部会根据水印宽高比自动计算 height
     *
     * @param image 水印图片 null 表示清除水印
     * @param x     归一化水印位置的 X 轴坐标，取值[0,1]
     * @param y     归一化水印位置的 Y 轴坐标，取值[0,1]
     * @param width 归一化水印宽度，取值[0,1]
     *
     */
    public abstract void setWatermark(Bitmap image, float x, float y, float width);

    /**
     * 设置动效贴图
     *
     * @param filePaht 动态贴图文件路径
     */
    public abstract void setMotionTmpl(String filePaht);

    /**
     * 设置绿幕文件
     *
     * 目前图片支持jpg/png，视频支持mp4/3gp等Android系统支持的格式
     *
     * @param file 绿幕文件位置，支持两种方式：
     *             1.资源文件放在assets目录，path直接取文件名
     *             2.path取文件绝对路径
     * @return false：调用失败；true：调用成功
     *
     * @note API要求18
     */
    public abstract boolean setGreenScreenFile(String file);

    /**
     * 设置大眼效果
     *
     * @param level 大眼等级取值为 0 ~ 9。取值为0时代表关闭美颜效果。默认值：0
     */
    public abstract void setEyeScaleLevel(int level);

    /**
     * 设置V脸（特权版本有效，普通版本设置此参数无效）
     *
     * @param level V脸级别取值范围 0 ~ 9。数值越大，效果越明显。默认值：0
     */
    public abstract void setFaceVLevel(int level);

    /**
     * 设置瘦脸效果
     *
     * @param level 瘦脸等级取值为 0 ~ 9。取值为0时代表关闭美颜效果。默认值：0
     */
    public abstract void setFaceSlimLevel(int level);

    /**
     * 设置短脸（特权版本有效，普通版本设置此参数无效）
     *
     * @param level 短脸级别取值范围 0 ~ 9。 数值越大，效果越明显。默认值：0
     */
    public abstract void setFaceShortLevel(int level);

    /**
     * 设置下巴拉伸或收缩（特权版本有效，普通版本设置此参数无效）
     *
     * @param chinLevel 下巴拉伸或收缩级别取值范围 -9 ~ 9。数值越大，拉伸越明显。默认值：0
     */
    public abstract void setChinLevel(int chinLevel);

    /**
     * 设置瘦鼻（特权版本有效，普通版本设置此参数无效）
     *
     * @param noseSlimLevel 瘦鼻级别取值范围 0 ~ 9。数值越大，效果越明显。默认值：0
     */
    public abstract void setNoseSlimLevel(int noseSlimLevel);

    /**
     * 调整曝光
     *
     * @param value 曝光比例，表示该手机支持最大曝光调整值的比例，取值范围从-1 - 1。
     *              负数表示调低曝光，-1是最小值；正数表示调高曝光，1是最大值；0表示不调整曝光
     */
    public abstract void setExposureCompensation(float value);

    /**
     * 播放背景音乐
     *
     * @param path 背景音乐文件路径
     * @return true：播放成功；false：播放失败
     */
    public abstract boolean playBGM(String path);

    /**
     * 背景音回调
     *
     * @param var1
     */
    public abstract void setBGMNofify(TXLivePusher.OnBGMNotify var1);

    /**
     * 停止播放背景音乐
     */
    public abstract void stopBGM();

    /**
     * 暂停播放背景音乐
     */
    public abstract void pauseBGM();

    /**
     * 继续播放背景音乐
     */
    public abstract void resumeBGM();

    /**
     * 获取音乐文件总时长
     *
     * @param path 音乐文件路径，如果 path 为空，那么返回当前正在播放的 music 时长
     *
     * @return 成功返回时长，单位毫秒，失败返回-1
     */
    public abstract int getBGMDuration(String path);

    /**
     * 设置麦克风的音量大小，播放背景音乐混音时使用，用来控制麦克风音量大小
     *
     * @param volume: 音量大小，100为正常音量，建议值为0 - 200
     */
    public abstract void setMicVolumeOnMixing(int volume);

    /**
     * 设置背景音乐的音量大小，播放背景音乐混音时使用，用来控制背景音音量大小
     *
     * @param volume 音量大小，100为正常音量，建议值为0 - 200，如果需要调大背景音量可以设置更大的值
     */
    public abstract void setBGMVolume(int volume);

    /**
     * 设置混响效果
     *
     * @param reverbType 混响类型，详见
     *                      {@link TXLiveConstants#REVERB_TYPE_0 } (关闭混响)
     *                      {@link TXLiveConstants#REVERB_TYPE_1 } (KTV)
     *                      {@link TXLiveConstants#REVERB_TYPE_2 } (小房间)
     *                      {@link TXLiveConstants#REVERB_TYPE_3 } (大会堂)
     *                      {@link TXLiveConstants#REVERB_TYPE_4 } (低沉)
     *                      {@link TXLiveConstants#REVERB_TYPE_5 } (洪亮)
     *                      {@link TXLiveConstants#REVERB_TYPE_6 } (磁性)
     */
    public abstract void setReverbType(int reverbType);

    /**
     * 设置变声类型
     * 原声 0
     * 熊孩子 1
     * 萝莉 2
     * 大叔 3
     * 重金属 4
     * 感冒 5
     * 外国人 6
     * 困兽 7
     * 死肥仔 8
     * 强电流 9
     * 重机械 10
     * 空灵 11
     *
     * @param voiceChangerType 变声类型，详见 TXVoiceChangerType
     */
    public abstract void setVoiceChangerType(int voiceChangerType);

    /**
     * 设置背景音乐的音调。
     *
     * 该接口用于混音处理,比如将背景音乐与麦克风采集到的声音混合后播放。
     *
     * @param pitch 音调，0为正常音调，范围是 -1 - 1。
     */
    public abstract void setBgmPitch(float pitch);


    public abstract void changeOneself(boolean isD, int widthPixels, int topMargin, ImageView mBgImageView);
}
