package com.yjfshop123.live.live.live.common.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.other.LiveUserDialogFragment;
import com.yjfshop123.live.live.live.list.TCSimpleUserInfo;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.bumptech.glide.Glide;

import java.util.LinkedList;


public class TCUserAvatarListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    LinkedList<TCSimpleUserInfo> mUserAvatarList;
    Context mContext;
    //主播id
    private String mPusherId;
    //自己的ID
    private String mUserId;
    private String liveID;
    //最大容纳量
    private final static int TOP_STORGE_MEMBER = 50;


    public TCUserAvatarListAdapter(Context context, String pusherId, String userId, String liveID) {
        this.mContext = context;
        this.mPusherId = pusherId;
        this.mUserId = userId;
        this.liveID = liveID;
        this.mUserAvatarList = new LinkedList<>();
    }

    public void setLiveID(String liveID) {
        this.liveID = liveID;
    }

    /**
     * 添加用户信息
     *
     * @param userInfo 用户基本信息
     * @return 存在重复或头像为主播则返回false
     */
    public boolean addItem(TCSimpleUserInfo userInfo) {

        //去除主播头像
        if (userInfo.userid.equals(mPusherId))
            return false;

        //去重操作
        for (TCSimpleUserInfo tcSimpleUserInfo : mUserAvatarList) {
            if (tcSimpleUserInfo.userid.equals(userInfo.userid))
                return false;
        }

        int index;
        if (userInfo.userid == mUserId) {
            //自己插在0位置
            index = 0;
        } else {
            if (mUserAvatarList.size() > 0) {
                //观众数>0新加入非自己插入1位置
                index = 1;
            } else {
                //观众数==0新加入非自己插入0位置
                index = 0;
            }
        }
        mUserAvatarList.add(index, userInfo);

        //超出时删除末尾项
        if (mUserAvatarList.size() > TOP_STORGE_MEMBER) {
            mUserAvatarList.remove(TOP_STORGE_MEMBER);
            notifyItemRemoved(TOP_STORGE_MEMBER);
        }
        notifyItemInserted(index);
        return true;
    }

    public void removeItem(String userId) {
        TCSimpleUserInfo tempUserInfo = null;

        for (TCSimpleUserInfo userInfo : mUserAvatarList)
            if (userInfo.userid.equals(userId))
                tempUserInfo = userInfo;


        if (null != tempUserInfo) {
            mUserAvatarList.remove(tempUserInfo);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_avatar, parent, false);
        AvatarViewHolder avatarViewHolder = new AvatarViewHolder(view);
        return avatarViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!TextUtils.isEmpty(mUserAvatarList.get(position).headpic)) {
            Glide.with(mContext)
                    .load(mUserAvatarList.get(position).headpic)
                    .into(((AvatarViewHolder) holder).ivAvatar);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.splash_logo)
                    .into(((AvatarViewHolder) holder).ivAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return mUserAvatarList != null ? mUserAvatarList.size() : 0;
    }

    private class AvatarViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;

        public AvatarViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TCSimpleUserInfo userInfo = mUserAvatarList.get(getLayoutPosition());

                    LiveUserDialogFragment fragment = new LiveUserDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", userInfo.userid);
                    bundle.putString("meUserId", mUserId);
                    bundle.putString("liveUid", mPusherId);
                    bundle.putString("liveID", liveID);
                    fragment.setArguments(bundle);
                    if (mContext instanceof TCLiveBasePublisherActivity) {
                        fragment.show(((TCLiveBasePublisherActivity) mContext).getSupportFragmentManager(), "LiveUserDialogFragment");
                    } else {
                        fragment.show(((TCLivePlayerActivity) mContext).getSupportFragmentManager(), "LiveUserDialogFragment");
                    }
                }
            });
        }
    }
}
