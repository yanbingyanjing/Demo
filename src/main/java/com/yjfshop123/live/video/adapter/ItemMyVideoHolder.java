package com.yjfshop123.live.video.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.SMDLResponse;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.xchat.Glide;
import com.yjfshop123.live.utils.UserInfoUtil;

public class ItemMyVideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private SelectableRoundedImageView mIcon;
    private TextView mLikeNum;
    ImageButton lvi2_del;
    private MyVideoAdapter.MyItemClickListener mItemClickListener;

    public ItemMyVideoHolder (View itemView, MyVideoAdapter.MyItemClickListener mItemClickListener) {
        super(itemView);
        lvi2_del=  itemView.findViewById(R.id.lvi2_del);
        mIcon =  itemView.findViewById(R.id.lvi2_icon);
        mLikeNum =  itemView.findViewById(R.id.lvi2_praise_tv);

        this.mItemClickListener = mItemClickListener;
        itemView.findViewById(R.id.lvi2_del).setOnClickListener(this);
        mIcon.setOnClickListener(this);
    }
    private String user_id;
    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void bind(SMDLResponse.ListBean bean, int width) {
      if(!TextUtils.isEmpty(user_id)){
          if(!user_id.equals(UserInfoUtil.getMiPlatformId())){
              lvi2_del.setVisibility(View.GONE);
          }else {
              lvi2_del.setVisibility(View.VISIBLE);
          }
      }
        mLikeNum.setText(bean.getLike_num() + "");

        ViewGroup.LayoutParams params = mIcon.getLayoutParams();
        params.width = width;
        params.height = (int)(width*1.5);
        mIcon.setLayoutParams(params);
        RequestOptions userAvatarOptions = new RequestOptions()//.signature(new ObjectKey(System.currentTimeMillis()))
              .error(R.color.black)
              ;

        com.bumptech.glide.Glide.with(itemView.getContext())
                .load(CommonUtils.getUrl(bean.getVideo_cover_img())).apply(userAvatarOptions)
                .into(mIcon);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lvi2_del:
                mItemClickListener.onItemDelete(v, getLayoutPosition());
                break;
            case R.id.lvi2_icon:
                mItemClickListener.onItemOpen(v, getLayoutPosition());
                break;
        }
    }
}
