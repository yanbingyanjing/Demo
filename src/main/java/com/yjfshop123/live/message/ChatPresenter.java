package com.yjfshop123.live.message;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.message.db.MessageDB;
import com.yjfshop123.live.message.interf.ChatViewIF;
import com.yjfshop123.live.message.ui.MessageListActivity;
import com.yjfshop123.live.server.widget.PromptPopupDialog;
import com.yjfshop123.live.ui.activity.MyWalletActivity1;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;

import java.util.Observable;
import java.util.Observer;

public class ChatPresenter implements Observer {

    private ChatViewIF view;
    private TIMConversation conversation;

    public ChatPresenter(ChatViewIF view, String identify, TIMConversationType type){
        this.view = view;
        conversation = TIMManager.getInstance().getConversation(type, identify);
        MessageEvent.getInstance().addObserver(this);
    }

    /**
     * 获取聊天TIM会话
     */
    public TIMConversation getConversation(){
        return conversation;
    }

    /**
     * 发送消息
     *
     * @param message 发送的消息
     */
    public void sendMessage(final TIMMessage message, final Context context, boolean isLimit) {
        //客服端、视频、语音通话、礼物不限制
        if (isLimit && /*!MessageListActivity.isMeVip*/MessageListActivity.isMsg != 1){
            //限制
            int user_message_num = UserInfoUtil.getUserMessageNum();
            int limit_message_num = UserInfoUtil.getLimitMessageNum();

//            if (limit_message_num == -1){
//                DialogUitl.showSimpleHintDialog(context, context.getString(R.string.prompt), "您的等级不足，无法发送消息哦~",
//                        true, new DialogUitl.SimpleCallback() {
//                            @Override
//                            public void onConfirmClick(Dialog dialog, String content) {
//                                dialog.dismiss();
//                            }
//                        });
//                view.onSendMessageFail(0, null, message);
//                return;
//            }
//
//            if (user_message_num >= limit_message_num){
//                PromptPopupDialog.newInstance(context,
//                        "",
//                        "充VIP无限畅聊",
//                        "成为VIP")
//                        .setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
//                            @Override
//                            public void onPositiveButtonClicked() {
//                                Intent intent = new Intent(context, MyWalletActivity1.class);
//                                intent.putExtra("currentItem",1);
//                                context.startActivity(intent);
//                            }
//                        }).setLayoutRes(R.layout.vip_popup_dialog).show();
//                view.onSendMessageFail(0, null, message);
//                return;
//            }

            user_message_num ++;
            if (user_message_num < 999999999){
                UserInfoUtil.setUserMessageNum(user_message_num);
            }
        }
        conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                view.onSendMessageFail(code, desc, message);
            }

            @Override
            public void onSuccess(TIMMessage msg) {
                //发送消息成功,消息状态已在sdk中修改，此时只需更新界面
                view.onSuccess(msg);
            }
        });
    }

    /**
     * 发送在线消息
     *
     * @param message 发送的消息
     */
    public void sendOnlineMessage(final TIMMessage message){
        conversation.sendOnlineMessage(message, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
//                view.onSendMessageFail(i, s, message);
            }

            @Override
            public void onSuccess(TIMMessage message) {

            }
        });
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent){
            if (data instanceof MessageDB) {
                MessageDB messageDB = (MessageDB) data;
                view.updateMessage(messageDB);
            }
        }
    }

}

