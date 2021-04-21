package com.yjfshop123.live.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yjfshop123.live.R;
import com.yjfshop123.live.model.EggListDataResponse;
import com.yjfshop123.live.model.GameListResponse;
import com.yjfshop123.live.ui.activity.EggOrderDetailActivity;
import com.yjfshop123.live.ui.activity.EggOrderListActivity;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.SystemUtils;
import com.yjfshop123.live.utils.ThirdGameUtil;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class GameListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GameListResponse.GameData> mList;
    private LayoutInflater layoutInflater;
    private Context context;


    public GameListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Vh(layoutInflater.inflate(R.layout.item_game_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Vh) holder).setData(mList.get(position));
    }

    int accountant = -1;

    public void setAccount(int accountant) {
        this.accountant = accountant;
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setCards(List<GameListResponse.GameData> list) {
        if (list == null) {
            return;
        }
        mList = list;
        notifyDataSetChanged();
    }

    public class Vh extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView game_img;


        public Vh(View itemView) {
            super(itemView);
            game_img = itemView.findViewById(R.id.game_img);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) game_img.getLayoutParams();
            //获取当前控件的布局对象

            params.width = SystemUtils.getScreenWidth(context) - SystemUtils.dip2px(context, 32);
            params.height = params.width * 104 / 343;//设置当前控件布局的高度
            game_img.setLayoutParams(params);
        }

        void setData(final GameListResponse.GameData bean) {
            Glide.with(context)
                    .load(CommonUtils.getUrl(bean.pic))
                    .into(game_img);
            game_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThirdGameUtil.getInstance().openGame(context, bean.id);
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item:
                    Intent intent = new Intent(context, EggOrderDetailActivity.class);
                    intent.putExtra("data", new Gson().toJson(mList.get(getLayoutPosition())));
                    context.startActivity(intent);
                    break;

            }
        }
    }

}
