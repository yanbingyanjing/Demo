package com.yjfshop123.live.game.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.game.adapter.GameAdapter;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.response.GameListResponse;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.ui.fragment.BaseFragment;
import com.yjfshop123.live.ui.widget.shimmer.PaddingItemDecoration1;
import com.yjfshop123.live.utils.CommonUtils;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;

public class GameFragmentItem extends BaseFragment implements OnItemClickListener {

    @BindView(R.id.shimmer_recycler_view_video)
    RecyclerView shimmerRecycler;

    private GameAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;

    private int width;

    private String category_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
        width = (int) ((CommonUtils.getScreenWidth(getActivity()) * 7.5 / 10 - CommonUtils.dip2px(mContext, 40)) / 3);

        Bundle bundle = getArguments();
        if (bundle != null) {
            category_id = bundle.getString("category_id");
        }
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_game_item;
    }

    @Override
    protected void initAction() {
        shimmerRecycler.addItemDecoration(new PaddingItemDecoration1(mContext, 5, 10));
        mAdapter = new GameAdapter(width, this);
        mGridLayoutManager = new GridLayoutManager(mContext, 3);
        shimmerRecycler.setLayoutManager(mGridLayoutManager);

        shimmerRecycler.setAdapter(mAdapter);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {
        super.initData();
        videoList();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        videoList();
    }

    private void videoList(){
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("page", 1)
                    .put("category_id", category_id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("hg/hggame/getGameList", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                finishRefresh();
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                if (result == null){
                    return;
                }
                try {
                    GameListResponse response = JsonMananger.jsonToBean(result, GameListResponse.class);
                    if (mList.size() > 0){
                        mList.clear();
                    }

                    mList.addAll(response.getList());
                    mAdapter.setCards(mList);

                    mAdapter.notifyDataSetChanged();
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private ArrayList<GameListResponse.ListBean> mList = new ArrayList<>();

    @Override
    public void onItemClick(Object bean, int position) {
        String link = Const.getDomain() + "/apph5/hggame/gameLogin?game_id=" + mList.get(position).getGame_id();
        Intent intent = new Intent("io.xchat.intent.action.webview");
        intent.setPackage(getContext().getPackageName());
        intent.putExtra("url", link);
        startActivity(intent);
    }
}
