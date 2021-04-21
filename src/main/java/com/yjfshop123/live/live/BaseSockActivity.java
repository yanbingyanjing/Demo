package com.yjfshop123.live.live;

import android.os.Bundle;
import android.os.Handler;

import com.yjfshop123.live.live.commondef.AnchorInfo;
import com.yjfshop123.live.live.live.common.widget.prompt.LivePromptBean;
import com.yjfshop123.live.live.response.MegGift;
import com.yjfshop123.live.live.response.MegUserInfo;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.NLog;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.socket.SocketMsgListener;
import com.yjfshop123.live.socket.SocketUtil;
import com.yjfshop123.live.ui.activity.BaseActivityH;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseSockActivity extends BaseActivityH implements SocketMsgListener {

    /**
     * 当前直播间ID
     */
    public static String mLiveID;

    /**
     * 主播间连麦或者PK时对方直播间ID
     */
    protected String otherLiveID;

    protected boolean isPKLaunch = false;

    protected MLVBLiveRoom mLiveRoom;
    protected Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SocketUtil.getInstance().setMesgListener(this);
        mLiveRoom = MLVBLiveRoom.sharedInstance(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //###################################################
    //               SOCKET 回调
    //###################################################
    @Override
    public void onConnect() {

    }

    @Override
    public void onDisConnect() {

    }

    @Override
    public void onMessage(String content) {
        if (mLiveID == null){
            return;
        }
        try {
            JSONObject jso = new JSONObject(content);
            String code = jso.getString("code");
            if (code.equals("live_home")){
                JSONObject data = jso.getJSONObject("data");

                String cmd = data.getString("cmd");

                if (cmd.equals("send_big_gift_in_live")){
                    LivePromptBean bean = new LivePromptBean(
                            data.getJSONObject("content").getJSONObject("notice_info").getString("from_user_nickname"),
                            data.getJSONObject("content").getJSONObject("notice_info").getString("to_user_nickname"),
                            data.getJSONObject("content").getJSONObject("notice_info").getString("gift_name"),
                            data.getJSONObject("content").getJSONObject("notice_info").getString("icon_img"),
                            data.getJSONObject("content").getJSONObject("notice_info").getString("live_id"));
                    onLivePrompt(bean);
                    return;
                }

                String live_id = data.getJSONObject("content").getString("live_id");
                if (!live_id.equals(mLiveID)){
                    return;
                }

                if (cmd.equals("viewer_join_live")){
                    //进入直播间
                    MegUserInfo bean = JsonMananger.jsonToBean(data.getJSONObject("content").getString("user_info"), MegUserInfo.class);
                    onEnterLive(bean);
                }else if (cmd.equals("viewer_leave_live")){
                    //离开直播间
                    MegUserInfo bean = JsonMananger.jsonToBean(data.getJSONObject("content").getString("user_info"), MegUserInfo.class);
                    onExitLive(bean);
                }else if (cmd.equals("send_gift_in_live")) {
                    //收到礼物
                    MegUserInfo bean = JsonMananger.jsonToBean(data.getJSONObject("content").getString("user_info"), MegUserInfo.class);
                    MegGift beanGift = JsonMananger.jsonToBean(data.getJSONObject("content").getString("gift_info"), MegGift.class);
                    onGiftMsg(bean, beanGift);
                }else if (cmd.equals("send_group_msg_in_live")){
                    //直播间文字消息
                    MegUserInfo bean = JsonMananger.jsonToBean(data.getJSONObject("content").getString("user_info"), MegUserInfo.class);
                    String txt = data.getJSONObject("content").getJSONObject("message").getString("msg_text");
                    onTxtMsg(false, bean, txt);
                }else if (cmd.equals("send_bullet_screen_msg_in_live")){
                    //弹幕消息
                    MegUserInfo bean = JsonMananger.jsonToBean(data.getJSONObject("content").getString("user_info"), MegUserInfo.class);
                    String txt = data.getJSONObject("content").getJSONObject("message").getString("msg_text");
                    onTxtMsg(true, bean, txt);
                }else if (cmd.equals("send_watch_in_live")){
                    //守护消息
                    MegUserInfo bean = JsonMananger.jsonToBean(data.getJSONObject("content").getString("user_info"), MegUserInfo.class);
                    String total_watch_num = data.getJSONObject("content").getString("total_watch_num");
                    guardianMsg(bean, total_watch_num);
                }else if (cmd.equals("send_like_msg_in_live")){
                    //点赞消息
                    MegUserInfo bean = JsonMananger.jsonToBean(data.getJSONObject("content").getString("user_info"), MegUserInfo.class);
                    likeMsg(bean);
                }else if (cmd.equals("remove_viewer_in_live")){
                    //直播间踢人
                    String user_id = data.getJSONObject("content").getJSONObject("user_info").getString("user_id");
                    onKickoutPeople(user_id);
                }else if (cmd.equals("liver_coin_change_in_live")){
                    //直播间总金蛋发生变化 消息
                    String total_coin = data.getJSONObject("content").getString("total_coin");
                    allCoinChange(total_coin);

                    //############################################################################################
                    //linkmic
                }else if (cmd.equals("viewer_apply_lianmai_in_live")){
                    //主播收到连麦请求
                    JSONObject user_info = data.getJSONObject("content").getJSONObject("user_info");
                    String user_id = user_info.getString("user_id");
                    String nickname = user_info.getString("nickname");
                    String avatar = user_info.getString("avatar");
                    AnchorInfo info = new AnchorInfo(user_id, nickname, avatar);
                    onRequestJoinAnchor(info);
                }else if (cmd.equals("viewer_reply_lianmai_in_live")){
                    //发起连麦 小主播 收到主播的连麦应答
                    String type = data.getJSONObject("content").getString("type");
                    NLog.e("连麦中",type);
                    if (type.equals("1")){
                        //同意
                        //小主播推流地址
                        String selfPushUrl = data.getJSONObject("content").getString("viewer_t_live_url");
                        //结束播放大主播的普通流，改为播放加速流
                        String accelerateURL = data.getJSONObject("content").getJSONObject("b_live_quick_url").getString("rtmp");
//                        final JSONArray jsa = data.getJSONObject("content").getJSONArray("viewer_b_live_quick_list");
                        onXZBAccept(type, selfPushUrl, accelerateURL);
                    }else {
                        onXZBAccept(type, null, null);
                    }
                }else if (cmd.equals("viewer_join_lianmai_in_live")){
                    //有小主播加入
                    String userID = data.getJSONObject("content").getJSONObject("user_info").getString("user_id");
                    String userName = data.getJSONObject("content").getJSONObject("user_info").getString("nickname");
                    String userAvatar = data.getJSONObject("content").getJSONObject("user_info").getString("avatar");
                    String rtmp = data.getJSONObject("content").getJSONObject("b_live_quick_url").getString("rtmp");
                    AnchorInfo anchorInfo = new AnchorInfo(userID, userName, userAvatar, rtmp);
                    onAnchorEnter(anchorInfo);
                }else if (cmd.equals("close_lianmai_in_live")){
                    //有小主播 退出
                    String userID = data.getJSONObject("content").getJSONObject("user_info").getString("user_id");
                    String userName = data.getJSONObject("content").getJSONObject("user_info").getString("nickname");
                    String userAvatar = data.getJSONObject("content").getJSONObject("user_info").getString("avatar");
                    AnchorInfo anchorInfo = new AnchorInfo(userID, userName, userAvatar, "");
                    onAnchorExit(anchorInfo);


                    //############################################################################################
                    //主播间连麦
                }else if (cmd.equals("liver_apply_lianmai_in_live")){
                    //收到主播间连麦
                    otherLiveID = data.getJSONObject("content").getString("other_live_id");
                    String userID = data.getJSONObject("content").getJSONObject("user_info").getString("user_id");
                    String userName = data.getJSONObject("content").getJSONObject("user_info").getString("nickname");
                    String userAvatar = data.getJSONObject("content").getJSONObject("user_info").getString("avatar");
                    AnchorInfo anchorInfo = new AnchorInfo(userID, userName, userAvatar, "");
                    onRequestRoomAA(anchorInfo);
                }else if (cmd.equals("liver_reply_lianmai_in_live")){
                    //主播主播 相应连麦
                    String type = data.getJSONObject("content").getString("type");
                    if (type.equals("1")){
                        //同意
                        otherLiveID = data.getJSONObject("content").getString("other_live_id");
                        String userID = data.getJSONObject("content").getJSONObject("user_info").getString("user_id");
                        String userName = data.getJSONObject("content").getJSONObject("user_info").getString("nickname");
                        String userAvatar = data.getJSONObject("content").getJSONObject("user_info").getString("avatar");
                        String rtmp = data.getJSONObject("content").getJSONObject("b_live_quick_url").getString("rtmp");
                        AnchorInfo anchorInfo = new AnchorInfo(userID, userName, userAvatar, rtmp);
                        onZBAccept("1", anchorInfo);

                    }else {
                        onZBAccept("2", null);
                    }
                }else if (cmd.equals("liver_close_lianmai_in_live")){
                    //关闭连麦（主播-主播）
                    onQuitRoomAA();

                    //############################################################################################
                    //PK
                }else if (cmd.equals("liver_apply_pk_in_live")){
                    //收到PK邀请
                    otherLiveID = data.getJSONObject("content").getString("other_live_id");
                    String userID = data.getJSONObject("content").getJSONObject("user_info").getString("user_id");
                    String userName = data.getJSONObject("content").getJSONObject("user_info").getString("nickname");
                    String userAvatar = data.getJSONObject("content").getJSONObject("user_info").getString("avatar");
                    AnchorInfo anchorInfo = new AnchorInfo(userID, userName, userAvatar, "");
                    onRequestRoomPK(anchorInfo);
                }else if (cmd.equals("launch_liver_reply_pk_in_live")){
                    //发起者 直播间 收到 PK同意消息
                    String type = data.getJSONObject("content").getString("type");
                    if (type.equals("1")){
                        //同意
                        otherLiveID = data.getJSONObject("content").getString("other_live_id");
                        String userID = data.getJSONObject("content").getJSONObject("user_info").getString("user_id");
                        String userName = data.getJSONObject("content").getJSONObject("user_info").getString("nickname");
                        String userAvatar = data.getJSONObject("content").getJSONObject("user_info").getString("avatar");
                        String rtmp = data.getJSONObject("content").getJSONObject("b_live_quick_url").getString("rtmp");
                        String pkRestTime = data.getJSONObject("content").getString("pk_rest_time");
                        AnchorInfo anchorInfo = new AnchorInfo(userID, userName, userAvatar, rtmp);
                        onPKLiveMsg("1", anchorInfo, pkRestTime);
                    }else {
                        onPKLiveMsg("2", null, null);
                    }
                }else if (cmd.equals("accept_liver_reply_pk_in_live")){
                    //接收者 直播间 收到 PK同意消息
                    String type = data.getJSONObject("content").getString("type");
                    if (type.equals("1")){
                        //同意
                        otherLiveID = data.getJSONObject("content").getString("other_live_id");
                        String userID = data.getJSONObject("content").getJSONObject("user_info").getString("user_id");
                        String userName = data.getJSONObject("content").getJSONObject("user_info").getString("nickname");
                        String userAvatar = data.getJSONObject("content").getJSONObject("user_info").getString("avatar");
                        String accelerateURL = data.getJSONObject("content").getJSONObject("b_live_quick_url").getString("rtmp");
                        AnchorInfo anchorInfo = new AnchorInfo(userID, userName, userAvatar, accelerateURL);
                        String pkRestTime = data.getJSONObject("content").getString("pk_rest_time");
                        onPKLiveMsg("10", anchorInfo, pkRestTime);
                    }
                }else if (cmd.equals("liver_finish_pk_in_live")) {
                    //完成PK 发起者 接受者 观众都收到此消息
                    String self_get_coin = data.getJSONObject("content").getString("self_get_coin");
                    String other_get_coin = data.getJSONObject("content").getString("other_get_coin");
                    String pk_punish_rest_time = data.getJSONObject("content").getString("pk_punish_rest_time");
                    onPKLiveFinish(self_get_coin, other_get_coin, pk_punish_rest_time);
                }else if (cmd.equals("send_pk_gift_in_live")){
                    //PK礼物数更新消息
                    String self_get_coin = data.getJSONObject("content").getString("self_get_coin");
                    String other_get_coin = data.getJSONObject("content").getString("other_get_coin");
                    updateGift(self_get_coin, other_get_coin);
                } else if (cmd.equals("liver_invalid_live")){
                    //主播 网络异常直播间已经关闭 图片提示
                    NLog.d("断开连麦","直播间一场1");
                    liverInvalidLive();

                }else if (cmd.equals("liver_close_pk_in_live")){
                    //在PK中人员处理消息：PK中关闭了直播间 清PK UI
                    liveClosePK();

                    //############################################################################################
                }else if (cmd.equals("liver_close_live")){
                    // 观众收到消息：主播关闭直播间
                    liveClose();

                }else if (cmd.equals("sys_send_warning_msg_in_live")){
                    //直播间提示
                    String message = data.getJSONObject("content").getJSONObject("message").getString("msg_text");
                    onLiveNotice(message);
                }else if (cmd.equals("sys_send_close_msg_in_live")){
                    //直播间关闭
                    closeLive();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }
    //###################################################
    //               SOCKET 回调
    //###################################################


    //进入房间
    public void onEnterLive(MegUserInfo bean){}

    //退出房间
    public void onExitLive(MegUserInfo bean){}

    //收到礼物
    public void onGiftMsg(MegUserInfo bean, MegGift beanGift){}

    //收到文字消息
    public void onTxtMsg(boolean danmu, MegUserInfo bean, String content){}

    //守护消息
    public void guardianMsg(MegUserInfo bean, String total_watch_num){}

    //收到总金蛋发生改变
    public void allCoinChange(String coin){}

    //被T消息
    public void onKickoutPeople(String user_id){}

    //主播收到观众连麦请求时的回调
    public void onRequestJoinAnchor(AnchorInfo anchorInfo){}

    //小主播收到 主播的连麦应答
    public void onXZBAccept(String type, String selfPushUrl, String accelerateURL){}

    //有新加入连麦观众
    public void onAnchorEnter(AnchorInfo pusherInfo) {}

    //有新退出连麦观众
    public void onAnchorExit(AnchorInfo pusherInfo) {}

    //直播间收到PK
    public void onRequestRoomPK(AnchorInfo pusherInfo) {}

    //开始PK原始用户收到 PK消息
    public void onPKLiveMsg(String type, AnchorInfo anchorInfo, String pkRestTime) {}

    //PK 完成
    public void onPKLiveFinish(String self_get_coin, String other_get_coin, String pk_punish_rest_time) {}

    //PK更新礼物
    public void updateGift(String self_get_coin, String other_get_coin) {}

    //PK中主播关闭直播间
    public void liveClosePK() {}

    //主播间连麦应答
    public void onZBAccept(String type, AnchorInfo anchorInfo){}

    //主播间 收到连麦
    public void onRequestRoomAA(AnchorInfo anchorInfo){}

    //主播间关闭连麦
    public void onQuitRoomAA(){}

    //观众收到主播关闭直播间
    public void liveClose(){}

    //主播 网络异常直播间已经关闭 图片提示
    public void liverInvalidLive(){}

    //点赞消息
    public void likeMsg(MegUserInfo bean){}

    //直播间提示
    public void onLiveNotice(String message){}

    //直播间关闭
    public void closeLive(){}

    //全场礼物提示
    public void onLivePrompt(LivePromptBean bean) {}

}
