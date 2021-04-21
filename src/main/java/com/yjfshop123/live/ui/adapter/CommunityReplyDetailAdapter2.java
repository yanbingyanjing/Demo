package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjfshop123.live.CommunityDoLike;
import com.yjfshop123.live.R;
import com.yjfshop123.live.net.response.CommunityReplyItemDetailResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;
import com.pandaq.emoticonlib.PandaEmoTranslator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommunityReplyDetailAdapter2 extends RecyclerView.Adapter<CommunityReplyDetailAdapter2.MyViewHolder> {

    private final static int VIEW_ITEM_1 = 1;
    private final static int VIEW_ITEM_BOTTOM = 2;

    int index1 = -1;
    int index2 = -1;

    private Context context;
    private List<CommunityReplyItemDetailResponse.ListBeanX> lists = new ArrayList<>();

    private OptionClickListener optionClickListener;

    private int mType;
    private boolean isSmall = false;

    public void setOptionClickListener(OptionClickListener optionClickListener) {
        this.optionClickListener = optionClickListener;
    }

    public CommunityReplyDetailAdapter2(Context context, int type, boolean isSmall) {
        this.context = context;
        this.mType = type;
        this.isSmall = isSmall;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_ITEM_1) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_community_reply_detail, parent, false);
        } else if (viewType == VIEW_ITEM_BOTTOM) {
            view = LayoutInflater.from(context).inflate(R.layout.community_item_bottom, parent, false);
        }

        MyViewHolder videoViewHolder = new MyViewHolder(view, viewType);
        view.setTag(videoViewHolder);
        return new MyViewHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (lists.size() == position) {
            return VIEW_ITEM_BOTTOM;
        } else {
            return VIEW_ITEM_1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        if (getItemViewType(position) == VIEW_ITEM_BOTTOM) {
            return;
        }

        final CommunityReplyItemDetailResponse.ListBeanX bean = lists.get(position);
        Glide.with(context).load(CommonUtils.getUrl(bean.getAvatar())).into(holder.headImg);
        holder.userNameTxt.setText(bean.getUser_nickname());
        holder.contentTxt.setText(PandaEmoTranslator
                .getInstance()
                .makeEmojiSpannable(bean.getContent()));
        holder.zanTxt.setText(bean.getLike_num() + "");

        //性别
        holder.communitySex.setVisibility(View.VISIBLE);
        if (bean.getSex() == 2) {
            holder.communitySex.setImageResource(R.mipmap.girl);
        } else {
            holder.communitySex.setImageResource(R.mipmap.boy);
        }

        //是否是vip
        if (bean.getIs_vip() == 0) {
            holder.communityVip.setVisibility(View.GONE);
        } else {
            holder.communityVip.setVisibility(View.VISIBLE);
        }

        //是否是主播
        if (bean.getDaren_status() == 2) {
            holder.communityDaren.setVisibility(View.VISIBLE);
        } else {
            holder.communityDaren.setVisibility(View.GONE);
        }

        //更多评论
        if (bean.getReply_list().getTotal() > 2) {
            holder.morePingLun.setVisibility(View.VISIBLE);
            holder.morePingLun.setText("更多" + bean.getReply_list().getTotal() + "条评论");
        } else {
            holder.morePingLun.setVisibility(View.GONE);
        }

        //是否赞过
        if (bean.getIs_like() == 1) {
            holder.zanTxt.setTextColor(context.getResources().getColor(R.color.color_ffd100));
            holder.zanImg.setImageResource(R.drawable.community_item_icon_7);
        } else {
            holder.zanTxt.setTextColor(context.getResources().getColor(R.color.purple_636363));
            holder.zanImg.setImageResource(R.drawable.community_item_icon_6);
        }

        // 是否显示评论
        if (bean.getReply_list().getTotal() == 0) {
            holder.firstLouLay.setVisibility(View.GONE);
            holder.secondLouLay.setVisibility(View.GONE);
            holder.firstTxt.setVisibility(View.GONE);
            holder.secondTxt.setVisibility(View.GONE);
        } else if (bean.getReply_list().getTotal() == 1) {
            holder.firstLouLay.setVisibility(View.VISIBLE);
            holder.secondLouLay.setVisibility(View.GONE);
            holder.firstTxt.setVisibility(View.VISIBLE);
            holder.secondTxt.setVisibility(View.GONE);
        } else if (bean.getReply_list().getTotal() >= 2) {
            holder.firstLouLay.setVisibility(View.VISIBLE);
            holder.secondLouLay.setVisibility(View.VISIBLE);
            holder.firstTxt.setVisibility(View.VISIBLE);
            holder.secondTxt.setVisibility(View.VISIBLE);
        }
        if (bean.getReply_list().getList() == null) {
            return;
        }
        for (int i = 0; i < bean.getReply_list().getList().size(); i++) {

            if (i == 0) {
                index1 = i;
            } else if (i == 1) {
                index2 = i;
            }

            CommunityReplyItemDetailResponse.ListBeanX.ReplyListBean.ListBean bean1 = bean.getReply_list().getList().get(i);
            if (i == 0) {
                //是否是楼主
                if (bean1.getIs_lz() == 0) {
                    holder.isLouZhu.setVisibility(View.GONE);
                }

                holder.louzhuName1.setText(bean1.getUser_nickname());
                holder.firstTxt.setText(PandaEmoTranslator
                        .getInstance()
                        .makeEmojiSpannable(bean1.getContent()));
                if (bean1.getReviewed_user_id() == 0) {
                    holder.aite.setVisibility(View.GONE);
                    holder.replyName.setVisibility(View.GONE);
                } else {
                    holder.aite.setVisibility(View.VISIBLE);
                    holder.replyName.setVisibility(View.VISIBLE);
                    holder.replyName.setText(bean1.getReviewed_user_nickname());
                }
            } else if (i == 1) {
                //是否是楼主
                if (bean1.getIs_lz() == 0) {
                    holder.isLouZhu1.setVisibility(View.GONE);
                }
                holder.louzhuName2.setText(bean1.getUser_nickname());
                holder.secondTxt.setText(PandaEmoTranslator
                        .getInstance()
                        .makeEmojiSpannable(bean1.getContent()));
                if (bean1.getReviewed_user_id() == 0) {
                    holder.aite1.setVisibility(View.GONE);
                    holder.replyName1.setVisibility(View.GONE);
                } else {
                    holder.aite1.setVisibility(View.VISIBLE);
                    holder.replyName1.setVisibility(View.VISIBLE);
                    holder.replyName1.setText(bean1.getReviewed_user_nickname());
                }
            }

            holder.firstTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionClickListener.itemClick(view, position, index1);
                }
            });
            holder.secondTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionClickListener.itemClick(view, position, index2);
                }
            });

        }

        holder.publishTime.setText(bean.getFloor_num() + "F  " + bean.getReply_time());

        holder.zanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserInfoUtil.getInfoComplete() == 0){
                    context.startActivity(new Intent(context, LoginActivity.class));
                    return;
                }
                if (bean.getIs_like() == 0) {
                    bean.setIs_like(1);
                    bean.setLike_num(bean.getLike_num() + 1);
                    if (mType == 2){
                        CommunityDoLike.getInstance().shortVideoDoLike(bean.getReply_id(), true);
                    }else {
                        CommunityDoLike.getInstance().dynamicDoLike(bean.getReply_id(), true);
                    }
                } else {
                    bean.setIs_like(0);
                    bean.setLike_num(bean.getLike_num() - 1);
                    if (mType == 2){
                        CommunityDoLike.getInstance().shortVideoUndoLike(bean.getReply_id(), true);
                    }else {
                        CommunityDoLike.getInstance().dynamicUndoLike(bean.getReply_id(), true);
                    }
                }
                holder.zanTxt.setText(bean.getLike_num() + "");
                notifyDataSetChanged();
            }
        });

        if (isSmall){
            holder.optionImg.setVisibility(View.GONE);
        }
        holder.optionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.optionClick();
            }
        });
        holder.headImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.headAndNameClick(view, position);
            }
        });
        holder.userNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.headAndNameClick(view, position);
            }
        });
        holder.contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                optionClickListener.itemClick(view, position, -1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lists.size() + 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.headImg)
        CircleImageView headImg;
        @BindView(R.id.userNameTxt)
        TextView userNameTxt;
        @BindView(R.id.optionImg)
        ImageView optionImg;

        @BindView(R.id.communitySex)
        ImageView communitySex;
        @BindView(R.id.communityDaren)
        ImageView communityDaren;
        @BindView(R.id.communityVip)
        ImageView communityVip;
        @BindView(R.id.isLouZhu)
        TextView isLouZhu;
        @BindView(R.id.isLouZhu1)
        TextView isLouZhu1;
        @BindView(R.id.firstLouLay)
        LinearLayout firstLouLay;
        @BindView(R.id.secondLouLay)
        LinearLayout secondLouLay;
        @BindView(R.id.louzhuName1)
        TextView louzhuName1;
        @BindView(R.id.louzhuName2)
        TextView louzhuName2;
        @BindView(R.id.replyName)
        TextView replyName;
        @BindView(R.id.replyName1)
        TextView replyName1;
        @BindView(R.id.firstTxt)
        TextView firstTxt;
        @BindView(R.id.secondTxt)
        TextView secondTxt;
        @BindView(R.id.morePingLun)
        TextView morePingLun;
        @BindView(R.id.publishTime)
        TextView publishTime;
        @BindView(R.id.zanLayout)
        RelativeLayout zanLayout;
        @BindView(R.id.zanTxt)
        TextView zanTxt;
        @BindView(R.id.zanImg)
        ImageView zanImg;

        @BindView(R.id.aite)
        TextView aite;
        @BindView(R.id.aite1)
        TextView aite1;


        @BindView(R.id.contentTxt)
        TextView contentTxt;
        @BindView(R.id.contentLayout)
        RelativeLayout contentLayout;

        public MyViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_ITEM_BOTTOM) {
                return;
            }
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OptionClickListener {
        void optionClick();

        void headAndNameClick(View view, int option);

        void itemClick(View view, int option, int index);
    }

    public void setCards(List<CommunityReplyItemDetailResponse.ListBeanX> list) {
        if (list == null) {
            return;
        }

        lists = list;
    }

}
