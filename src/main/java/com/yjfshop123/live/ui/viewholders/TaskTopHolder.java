package com.yjfshop123.live.ui.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.model.UserStatusResponse;
import com.yjfshop123.live.net.response.ChatTaskListResponse;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.server.widget.SelectableRoundedImageView;
import com.yjfshop123.live.shop.pintuan.PintuanListActivity;
import com.yjfshop123.live.shop.ziying.ui.NewShopListActivity;
import com.yjfshop123.live.ui.activity.WebViewActivity;
import com.yjfshop123.live.ui.adapter.TaskAdapter;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;

public class TaskTopHolder extends RecyclerView.ViewHolder {

    private SelectableRoundedImageView banner;


    private TaskAdapter.MyItemClickListener mItemClickListener;

    public TaskTopHolder(View itemView) {
        super(itemView);

        banner = itemView.findViewById(R.id.banner);
        RelativeLayout.LayoutParams paramsSS = (RelativeLayout.LayoutParams) banner.getLayoutParams();
        //获取当前控件的布局对象
        paramsSS.width = SystemUtils.getScreenWidth(itemView.getContext())-SystemUtils.dip2px(itemView.getContext(),36);//设置当前控件布局的高度
        paramsSS.height=  paramsSS.width*2/5;
        banner.setLayoutParams(paramsSS);
    }

    public void bind(final Context context, String url) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDialog.show(context);
                OKHttpUtils.getInstance().getRequest("app/user/userStatus", "", new RequestCallback() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        LoadDialog.dismiss(context);
//                        Intent intent = new Intent(context, PintuanListActivity.class);
//                        context.startActivity(intent);
                    }

                    @Override
                    public void onSuccess(String result) {
                        LoadDialog.dismiss(context);
                        try {
                            if (TextUtils.isEmpty(result)) {
//                                Intent intent = new Intent(context, PintuanListActivity.class);
//                                context.startActivity(intent);
                                return;
                            }
                            UserStatusResponse data = new Gson().fromJson(result, UserStatusResponse.class);
                            if (data.jump != null) {
                                if (data.jump.pintuan_is_h5 == 1) {
                                    Intent intent = new Intent(context, WebViewActivity.class);
                                    if (!TextUtils.isEmpty(data.jump.pintuan_h5)) {
                                        intent.putExtra("url", data.jump.pintuan_h5);
                                        context.startActivity(intent);
                                    }

                                } else {
//                                    Intent intent = new Intent(context, PintuanListActivity.class);
//                                    context.startActivity(intent);
                                }
                            } else {
//                                Intent intent = new Intent(context, PintuanListActivity.class);
//                                context.startActivity(intent);
                            }
                        } catch (Exception e) {
//                            Intent intent = new Intent(context, PintuanListActivity.class);
//                            context.startActivity(intent);
                        }
                    }
                });

            }
        });
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.imageloader)// 正在加载中的图片  
                .error(R.drawable.imageloader)// 加载失败的图片  
                .diskCacheStrategy(DiskCacheStrategy.ALL);// 磁盘缓存策略  

        Glide.with(itemView.getContext())
                .load(CommonUtils.getUrl(url))// 图片地址  
                .apply(options)// 参数  
                .into(banner);// 需要显示的ImageView控件

    }




}
