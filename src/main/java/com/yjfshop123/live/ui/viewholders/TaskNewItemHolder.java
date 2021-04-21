package com.yjfshop123.live.ui.viewholders;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.xchat.Glide;
import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.model.TaskNewResponse;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.taskcenter.SucaiActivity;
import com.yjfshop123.live.taskcenter.UploadAdapter;
import com.yjfshop123.live.taskcenter.UploadSucaiAcitivity;
import com.yjfshop123.live.ui.activity.InviteFriendActivity;
import com.yjfshop123.live.ui.activity.SheQuPublishContentActivity;
import com.yjfshop123.live.ui.adapter.TaskAdapter;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.video.VideoRecordActivity;

import java.util.ArrayList;

public class TaskNewItemHolder extends RecyclerView.ViewHolder {


    private SelectableRoundedImageView day_task_img;
    private TextView name;
    private TextView iscomplete;
    private TextView des;
    private TextView end_time;
    private TaskAdapter.MyItemClickListener mItemClickListener;
    private TextView jindu;
    public TaskNewItemHolder(View itemView) {
        super(itemView);
        day_task_img = itemView.findViewById(R.id.day_task_img);
        name = itemView.findViewById(R.id.name);
        iscomplete = itemView.findViewById(R.id.iscomplete);
        des = itemView.findViewById(R.id.des);
        end_time = itemView.findViewById(R.id.end_time);
        jindu= itemView.findViewById(R.id.jindu);

    }


    public void bind(final Context context, final TaskNewResponse.TaskItem bean) {
        Glide.with(context).load(CommonUtils.getUrl(bean.task_icon)).into(day_task_img);
        name.setText(bean.name );
        jindu.setText("已完成(" + bean.deal_time + "/" + bean.need_times + ")");
        des.setText(bean.des);
//        if (!TextUtils.isEmpty(bean.end_time))
//            end_time.setText("截止时间：" + bean.end_time);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(bean.tag) && bean != null && !bean.is_complete) {
                    if (bean.tag.equals(TaskNewResponse.view_video)) {
                        //看视频
                        DialogUitl.showSimpleDialog(context,
                                "是否跳转到短视频界面？",
                                new DialogUitl.SimpleCallback2() {
                                    @Override
                                    public void onCancelClick() {

                                    }

                                    @Override
                                    public void onConfirmClick(Dialog dialog, String content) {
                                        ((Activity) context).setResult(10001);
                                        ((Activity) context).finish();
                                        return;                                    }
                                });

                    }
                    if (bean.need_check==1) {
                        //发朋友圈
                        Intent intent = new Intent(context, UploadSucaiAcitivity.class);
                        intent.putExtra("id",bean.id);
                        intent.putExtra("des",bean.des);
                        intent.putExtra("template_id",bean.template_id);
                        intent.putExtra("task_example",bean.task_example);
                        ((Activity) context).startActivity(intent);
                        return;
                    }
                    if (bean.tag.equals(TaskNewResponse.invite_new_user)) {
                        //邀请新人
                        Intent intent = new Intent(context, InviteFriendActivity.class);
                        ((Activity)context).startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);

                        return;
                    }

                    if (bean.tag.equals(TaskNewResponse.send_gift)) {
                        //跳转到直播主页界面
                        DialogUitl.showSimpleDialog(context,
                                "是否跳转到直播界面？",
                                new DialogUitl.SimpleCallback2() {
                                    @Override
                                    public void onCancelClick() {

                                    }

                                    @Override
                                    public void onConfirmClick(Dialog dialog, String content) {
                                        ((Activity) context).setResult(10002);
                                        ((Activity) context).finish();
                                        return;
                                    }
                                });
                    }
                    if (bean.tag.equals(TaskNewResponse.follow_daren)) {
                        //关注主播跳转到主播个人主页界面
                        ActivityUtils.startUserHome(context, bean.daren_id+"");
                        ((Activity)context).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        return;
                    }
                    if (bean.tag.equals(TaskNewResponse.quanzi)) {
                        //发圈子
                        Intent intent = new Intent(context, SheQuPublishContentActivity.class);
                        intent.putExtra("type", 2);
                        ((Activity)context).startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
//                        Intent intent = new Intent(context, SheQuPublishContentActivity.class);
//                        intent.putExtra("type", 4);
//                       ((Activity)context).startActivity(intent);
//                        ((Activity)context).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        return;
                    }
                    if (bean.tag.equals(TaskNewResponse.short_video)) {
                        //发短视频
                        ((Activity)context).startActivity(new Intent(context, VideoRecordActivity.class));
                        ((Activity)context).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                        return;
                    }
                    if (bean.tag.equals(TaskNewResponse.short_video_like)) {
                        //大公鸡短视频点赞评论关注
                        DialogUitl.showSimpleDialog(context,
                                "是否跳转到短视频界面？",
                                new DialogUitl.SimpleCallback2() {
                                    @Override
                                    public void onCancelClick() {

                                    }

                                    @Override
                                    public void onConfirmClick(Dialog dialog, String content) {
                                        ((Activity) context).setResult(10001);
                                        ((Activity) context).finish();
                                        return;
                                    }
                                });
//                        ((Activity) context).setResult(10001);
//                        ((Activity) context).finish();
//                        return;
                    }
                    if (bean.tag.equals(TaskNewResponse.chunzhuang_like)) {
                        //大公鸡短视频点赞评论关注
                        DialogUitl.showSimpleDialog(context,
                                "是否跳转到春庄界面？",
                                new DialogUitl.SimpleCallback2() {
                                    @Override
                                    public void onCancelClick() {

                                    }

                                    @Override
                                    public void onConfirmClick(Dialog dialog, String content) {
                                        ((Activity) context).setResult(10003);
                                        ((Activity) context).finish();
                                        return;
                                    }
                                });
//                        ((Activity) context).setResult(10001);
//                        ((Activity) context).finish();
//                        return;
                    }
                }
            }
        });
        iscomplete.setText(bean.task_status);
        if (bean.is_complete) {
           // iscomplete.setText(R.string.has_complete);
            iscomplete.setBackgroundResource(R.drawable.bg_gradient_fae2ae_b28d51_11);
        } else {
            //iscomplete.setText(R.string.weiwancheng);
            iscomplete.setBackgroundResource(R.drawable.bg_404040_13);
        }
    }



}
