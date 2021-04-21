package com.yjfshop123.live.ui.widget.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.utils.TCConstants;
import com.yjfshop123.live.live.live.play.TCLivePlayerActivity;
import com.yjfshop123.live.model.AdditionResponse;
import com.yjfshop123.live.model.UserStatusResponse;
import com.yjfshop123.live.net.response.LiveData;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.shop.pintuan.PintuanListActivity;
import com.yjfshop123.live.ui.activity.BadEggActivity;
import com.yjfshop123.live.ui.activity.HomeActivity;
import com.yjfshop123.live.ui.activity.MMDetailsActivityNew;
import com.yjfshop123.live.ui.activity.TaskNewCenterActivity;
import com.yjfshop123.live.ui.activity.WebViewActivity;
import com.yjfshop123.live.ui.adapter.AdditionAdapter;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.NumUtil;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.List;

public class DialogHomeActivitysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<UserStatusResponse.home_activitiesData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public DialogHomeActivitysAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<UserStatusResponse.home_activitiesData> list) {
        if (list == null) {
            return;
        }

        mList = list;
        notifyDataSetChanged();
    }

    HomeActivity homeActivity;

    public void setHome(HomeActivity home) {
        homeActivity = home;
    }

    public class Vh extends RecyclerView.ViewHolder {
        TextView canyu;
        TextView title;


        public Vh(View itemView) {
            super(itemView);
            canyu = itemView.findViewById(R.id.canyu);
            title = itemView.findViewById(R.id.title);
        }

        void setData(final UserStatusResponse.home_activitiesData bean) {
            if (bean == null) return;
            title.setText(bean.title);

            canyu.setText(bean.btn_name);
            canyu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(bean.type) && bean.type.equals("app")) {
                        //原生界面
                        if (!TextUtils.isEmpty(bean.tag)) {
                            if (bean.tag.equals("egg")) {
                                //砸金蛋
                                if (homeActivity != null) homeActivity.checkPrize();
                            }
                            if (bean.tag.equals("pintuan")) {
                                //拼团
                                Intent intent = new Intent(context, PintuanListActivity.class);
                                context.startActivity(intent);
                            }
                            if (bean.tag.equals("task")) {
                                //任务中心
                                Intent intent = new Intent(context, TaskNewCenterActivity.class);
                                ((Activity) context).startActivityForResult(intent, 10001);
                            }
                            if (bean.tag.equals("live")) {
                                //任务中心
                                startLivePlay(bean.live_info);
                            }

                            if (bean.tag.equals("choudan")) {
                                //臭蛋界面
                                Intent intent = new Intent(context, BadEggActivity.class);
                                context.startActivity(intent);
                            }
                        } else {
                            NToast.shortToast(context, "暂不支持此类型活动");
                        }
                        return;
                    }

                    if (!TextUtils.isEmpty(bean.type) && bean.type.equals("url")) {
                        //H5界面
                        Intent intent = new Intent(context, WebViewActivity.class);
                        if (!TextUtils.isEmpty(bean.url)) {
                            intent.putExtra("url", bean.url);
                        }
                        context.startActivity(intent);
                        return;
                    }
                    NToast.shortToast(context, "暂不支持此类型活动");
                }
            });
        }

    }

    private void startLivePlay(final LiveData liveData) {
        if (liveData == null) return;

        if (liveData.live_type == 2) {
            DialogUitl.showSimpleInputDialog(context, "请输入房间密码", DialogUitl.INPUT_TYPE_NUMBER_PASSWORD, 8,
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            if (TextUtils.isEmpty(content)) {
                                NToast.shortToast(context, "请输入房间密码");
                            } else {
                                startLivePlay_(liveData, content);
                                dialog.dismiss();
                            }
                        }
                    });
        } else if (liveData.live_type == 3) {
            DialogUitl.showSimpleHintDialog(context, context.getString(R.string.prompt), "进入该直播间将收取" + liveData.getIn_coin() + context.getString(R.string.my_jinbi),
                    true, new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                            startLivePlay_(  liveData, null);
                        }
                    });
        } else {
            startLivePlay_(liveData, null);
        }
    }

    private void startLivePlay_(final LiveData liveData, String in_password) {
        String url;
        url = "app/live/joinLive";
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("live_id", liveData.live_id)
                    .put("in_password", in_password)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                DialogUitl.showSimpleHintDialog(context, context.getString(R.string.prompt), errInfo,
                        true, new DialogUitl.SimpleCallback() {
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                dialog.dismiss();
                            }
                        });
            }

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Intent intent = new Intent(context, TCLivePlayerActivity.class);

                    if (liveData.getLive_mode() == 2) {
                        intent.putExtra("pureAudio", true);
                    } else {
                        intent.putExtra("pureAudio", false);
                    }
                    intent.putExtra(TCConstants.ROOM_TITLE, liveData.getTitle());
                    intent.putExtra(TCConstants.COVER_PIC, CommonUtils.getUrl(liveData.getCover_img()));
                    intent.putExtra("LivePlay", result);
                    intent.putExtra("vod_type", liveData.getVod_type());
                    intent.putExtra("live_id", String.valueOf(liveData.getLive_id()));
                    intent.putExtra("zhubo_user_id", liveData.getUser_id() + "");
                    ((Activity) context).startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
                }
            }
        });
    }


}
