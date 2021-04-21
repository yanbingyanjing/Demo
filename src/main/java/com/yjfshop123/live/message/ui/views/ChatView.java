package com.yjfshop123.live.message.ui.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;

import cn.jiguang.imui.chatinput.ChatInputView;
import cn.jiguang.imui.chatinput.listener.OnCameraCallbackListener;
import cn.jiguang.imui.chatinput.listener.OnClickEditTextListener;
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener;
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener;
import cn.jiguang.imui.chatinput.menu.Menu;
import cn.jiguang.imui.chatinput.menu.MenuManager;
import cn.jiguang.imui.chatinput.myvoice.VoiceRecorderView;
import cn.jiguang.imui.chatinput.record.RecordVoiceButton;
import cn.jiguang.imui.messages.MessageList;
import cn.jiguang.imui.messages.MsgListAdapter;

public class ChatView extends RelativeLayout {

    private TextView mTitle;
    private MessageList mMsgList;
    private ChatInputView mChatInput;
    private RecordVoiceButton mRecordVoiceBtn;
    private VerticalSwipeRefreshLayout mSwipeRefresh;
    private ImageButton mSelectAlbumIb;

    private Context mContext;

    private MenuManager menuManager;

    private ImageView mChatBack;
    private ImageView mChatPeople;
    private VoiceRecorderView mVoiceRecorderView;

    public ChatView(Context context) {
        super(context);
        mContext = context;
    }

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels * 3 / 4;
    }

    public void initModule() {
        mTitle = (TextView) findViewById(R.id.title_tv);
        mMsgList = (MessageList) findViewById(R.id.msg_list);
        mChatInput = (ChatInputView) findViewById(R.id.chat_input);
        mSwipeRefresh = findViewById(R.id.swipe_refresh);
        mChatBack = findViewById(R.id.chat_back);
        mChatPeople = findViewById(R.id.chat_people_iv);
        mVoiceRecorderView = findViewById(R.id.voice_recorder);

        /**
         * Should set menu container height once the ChatInputView has been initialized.
         * For perfect display, the height should be equals with soft input height.
         */
//        mChatInput.setMenuContainerHeight(819);
        mChatInput.setMenuContainerHeight(getScreenWidth());
        mRecordVoiceBtn = mChatInput.getRecordVoiceButton();
        mSelectAlbumIb = mChatInput.getSelectAlbumBtn();

//        mMsgList.setDateBgColor(Color.parseColor("#FF4081"));
//        mMsgList.setDatePadding(5, 10, 10, 5);
//        mMsgList.setEventTextPadding(5);
//        mMsgList.setEventBgColor(Color.parseColor("#34A350"));
//        mMsgList.setDateBgCornerRadius(15);

        mMsgList.setHasFixedSize(true);
        // set show display name or not
//        mMsgList.setShowReceiverDisplayName(true);
//        mMsgList.setShowSenderDisplayName(false);


        // add Custom Menu View
        menuManager = mChatInput.getMenuManager();
        menuManager.addCustomMenu("TAG_VIDEO_VOICE_CALL", R.layout.menu_voice_video_call_item);
        menuManager.addCustomMenu("Y_TAG_CAMERA", R.layout.my_menu_item_camera);
        menuManager.addCustomMenu("Y_TAG_GIFT", R.layout.my_menu_item_gift);

       /* MenuItem mGiftMenuItem = (MenuItem) View.inflate(mContext, R.layout.menu_gift_item, null);
        MenuFeature mGiftMenuFeature = (MenuFeature) View.inflate(mContext, R.layout.menu_gift_feature, null);
        mGiftMenuFeature.findViewById(R.id.menu_text_feature_tv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "gift", Toast.LENGTH_SHORT).show();
            }
        });
        menuManager.addCustomMenu("TAG_GIFT", mGiftMenuItem, mGiftMenuFeature);*/

        // Custom menu order
        menuManager.setMenu(Menu.newBuilder().
                customize(true).
                setRight(Menu.TAG_SEND, Menu.TAG_EMOJI).
                setLeft(Menu.Y_TAG_VOICE).
//                setBottom(Menu.TAG_VOICE, "TAG_VIDEO_VOICE_CALL", Menu.TAG_CAMERA, Menu.TAG_GALLERY, Menu.TAG_EMOJI).
        setBottom(/*Menu.TAG_VOICE,*/ "TAG_VIDEO_VOICE_CALL", "Y_TAG_CAMERA", "Y_TAG_GIFT", Menu.TAG_GALLERY).
                        build());
    }

    public VerticalSwipeRefreshLayout getSwipeRefresh() {
        return mSwipeRefresh;
    }

    public void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setMenuClickListener(OnMenuClickListener listener) {
        mChatInput.setMenuClickListener(listener);
    }

    public void setAdapter(MsgListAdapter adapter) {
        mMsgList.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mMsgList.setLayoutManager(layoutManager);
    }

    public void setRecordVoiceFile(String path, String fileName) {
        mRecordVoiceBtn.setVoiceFilePath(path, fileName);
    }

    public void setCameraCaptureFile(String path, String fileName) {
        mChatInput.setCameraCaptureFile(path, fileName);
    }

    public void setRecordVoiceListener(RecordVoiceListener listener) {
        mChatInput.setRecordVoiceListener(listener, mVoiceRecorderView);
    }

    public void setOnCameraCallbackListener(OnCameraCallbackListener listener) {
        mChatInput.setOnCameraCallbackListener(listener);
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mMsgList.setOnTouchListener(listener);
    }

    public void setOnTouchEditTextListener(OnClickEditTextListener listener) {
        mChatInput.setOnClickEditTextListener(listener);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public ChatInputView getChatInputView() {
        return mChatInput;
    }

    public MessageList getMessageListView() {
        return mMsgList;
    }

    public ImageButton getSelectAlbumBtn() {
        return this.mSelectAlbumIb;
    }

    public ImageView getChatBack(){
        return this.mChatBack;
    }

    public ImageView getChatPeople(){
        return this.mChatPeople;
    }
}
