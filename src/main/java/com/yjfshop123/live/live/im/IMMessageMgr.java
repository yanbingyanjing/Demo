package com.yjfshop123.live.live.im;

import android.content.Context;
import android.os.Handler;

import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveReceiveGiftBean;
import com.yjfshop123.live.live.live.common.widget.gift.bean.LiveReceiveGiftBeanItem;
import com.yjfshop123.live.message.MessageEvent;
import com.yjfshop123.live.message.db.IMConversation;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;


public class IMMessageMgr implements Observer {

    private Context                         mContext;
    private Handler                         mHandler;
    private IMMessageCallback               mMessageListener;

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent){
            if (data instanceof TIMMessage) {
                TIMMessage message = (TIMMessage) data;
                newMessages(message);
            }
        }
    }

    /**
     * 函数级公共Callback定义
     */
    public interface Callback{
        void onError(int code, String errInfo);
        void onSuccess(Object... args);
    }

    /**
     * 模块回调Listener定义
     */
    public interface IMMessageListener {
        /**
         * 实时音视频礼物
         */
        void onRoomReceiveGift(int roomId, LiveReceiveGiftBean bean);
    }

    public IMMessageMgr(final Context context) {
        this.mContext = context.getApplicationContext();
        this.mHandler = new Handler(this.mContext.getMainLooper());
        this.mMessageListener = new IMMessageCallback(null);

        MessageEvent.getInstance().addObserver(this);
    }

    /**
     * 设置回调
     * @param listener
     */
    public void setIMMessageListener(IMMessageListener listener){
        this.mMessageListener.setListener(listener);
    }

    /**
     * 反初始化
     */
    public void unInitialize(){
        mContext = null;
        mHandler = null;

        if (mMessageListener != null) {
            mMessageListener.setListener(null);
            mMessageListener = null;
        }
    }

    /**
     * 发送CC（端到端）自定义消息
     *
     * @param callback
     */
    public void sendC2CCustomMessage(final String roomdbId, final int roomId, final LiveReceiveGiftBean bean,
                                     final IMConversation imConversationDB, final Callback callback) {
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                TIMMessage message = new TIMMessage();

                String data_ = "";
                JSONObject dataJson_ = new JSONObject();
                try{
                    dataJson_.put("roomdbId", roomdbId);
                    dataJson_.put("roomId", roomId);
                    dataJson_.put("type", 100);
                    dataJson_.put("giftChatLiveInfo", JsonMananger.beanToJson(bean.getLiveReceiveGiftBeanItem()));
                    data_ = dataJson_.toString();
                }catch (JSONException e){
                    if (callback != null) {
                        callback.onError(-1, "发送CC消息失败");
                    }
                    return;
                } catch (HttpException e) {
                    if (callback != null) {
                        callback.onError(-1, "发送CC消息失败");
                    }
                    return;
                }
                TIMCustomElem customElem_ = new TIMCustomElem();
                customElem_.setData(data_.getBytes());
                message.addElement(customElem_);


                String data = "";
                JSONObject dataJson = new JSONObject();
                try{
                    dataJson.put("userIMId",imConversationDB.getUserIMId());
                    dataJson.put("otherPartyIMId",imConversationDB.getOtherPartyIMId());
                    dataJson.put("userId",imConversationDB.getUserId());
                    dataJson.put("otherPartyId",imConversationDB.getOtherPartyId());
                    dataJson.put("userName", imConversationDB.getUserName());
                    dataJson.put("userAvatar", imConversationDB.getUserAvatar());
                    dataJson.put("otherPartyName", imConversationDB.getOtherPartyName());
                    dataJson.put("otherPartyAvatar", imConversationDB.getOtherPartyAvatar());
                    data = dataJson.toString();
                }catch (JSONException e){
                    if (callback != null) {
                        callback.onError(-1, "发送CC消息失败");
                    }
                    return;
                }
                TIMCustomElem customElem = new TIMCustomElem();
                customElem.setData(data.getBytes());
                message.addElement(customElem);


                TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, imConversationDB.getOtherPartyIMId());
                conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int i, String s) {
                        if (callback != null){
                            callback.onError(i, s);
                        }
                    }

                    @Override
                    public void onSuccess(TIMMessage timMessage) {
                        if (callback != null){
                            callback.onSuccess();
                        }

                        try{
                            bean.setUid(imConversationDB.getUserIMId());
                            bean.setAvatar(imConversationDB.getUserAvatar());
                            bean.setUserNiceName(imConversationDB.getUserName());

                            if (mMessageListener != null){
                                mMessageListener.onRoomReceiveGift(roomId, bean);
                            }
                        }catch (Exception e){
                        }
                    }
                });
            }
        });
    }

    private void newMessages(TIMMessage message){
        if (message.getElementCount() < 2){
            return;
        }

        LiveReceiveGiftBean bean = new LiveReceiveGiftBean();
        int roomId = 0;
        boolean a = false;
        boolean b = false;

        for (int i = 0; i < message.getElementCount(); i++) {
            TIMElem element = message.getElement(i);

            switch (element.getType()){
                case GroupSystem:{
                    break;
                }

                case Custom: {
                    TIMCustomElem elem = (TIMCustomElem) element;
                    try{
                        String str = new String(elem.getData(), "UTF-8");
                        JSONObject jsonObj = new JSONObject(str);
                        int type = jsonObj.getInt("type");
                        if (type == 100){
                            String giftChatLiveInfo = jsonObj.getString("giftChatLiveInfo");
                            roomId = jsonObj.getInt("roomId");

                            LiveReceiveGiftBeanItem item = JsonMananger.jsonToBean(giftChatLiveInfo, LiveReceiveGiftBeanItem.class);
                            bean.setLiveReceiveGiftBeanItem(item);

                            a = true;
                        }
                    }catch (Exception e){

                    }

                    try{
                        String str = new String(elem.getData(), "UTF-8");
                        JSONObject jsonObj = new JSONObject(str);
                        String userIMId = jsonObj.getString("userIMId");
                        String userName = jsonObj.getString("userName");
                        String userAvatar = jsonObj.getString("userAvatar");

                        bean.setUid(userIMId);
                        bean.setAvatar(userAvatar);
                        bean.setUserNiceName(userName);
                        b = true;
                    }catch (Exception e){

                    }
                    break;
                }
                case GroupTips: {
                    break;
                }
            }
        }

        if (mMessageListener != null & a & b){
            mMessageListener.onRoomReceiveGift(roomId, bean);
        }
    }

    /**
     * 辅助类 IM Message Listener
     */
    private class IMMessageCallback implements IMMessageListener {
        private IMMessageListener listener;

        public IMMessageCallback(IMMessageListener listener) {
            this.listener = listener;
        }

        public void setListener(IMMessageListener listener) {
            this.listener = listener;
        }

        @Override
        public void onRoomReceiveGift(final int roomId, final LiveReceiveGiftBean bean) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onRoomReceiveGift(roomId, bean);
                    }
                }
            });
        }
    }

}

