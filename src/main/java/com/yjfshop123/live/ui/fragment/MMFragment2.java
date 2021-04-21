package com.yjfshop123.live.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.net.response.UserHomeResponse;
import com.yjfshop123.live.ui.adapter.MMAdapter;
import com.yjfshop123.live.ui.activity.VideoPreviewActivity;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;

public class MMFragment2 extends BaseFragment {

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;
    @BindView(R.id.mm2_hint)
    TextView mm2_hint;

    private MMAdapter mmAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private UserHomeResponse mResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);
    }

    public void setResponse(UserHomeResponse response){
        this.mResponse = response;
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_mm2;
    }

    @Override
    protected void initAction() {
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        shimmerRecycler.setFocusableInTouchMode(false);
    }


    @Override
    protected void initEvent() {
        if (mResponse == null){
            return;
        }
        if (mResponse.getUser_info().getVideo().size() > 0){
            mSwipeRefresh.setVisibility(View.VISIBLE);
            mm2_hint.setVisibility(View.GONE);

            mmAdapter = new MMAdapter(mContext, 2, null);
            shimmerRecycler.setAdapter(mmAdapter);
            mmAdapter.setCards(mResponse);
            mmAdapter.notifyDataSetChanged();

            mmAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(Object bean, int position) {
                    VideoPreviewActivity.startActivity(getActivity(),
                            mResponse.getUser_info().getVideo().get(position).getFull_url(),
                            mResponse.getUser_info().getVideo().get(position).getCover_img());
                }
            });
        }else {
            mSwipeRefresh.setVisibility(View.GONE);
            mm2_hint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void updateViews(boolean isRefresh) {
        finishRefresh();
    }

}
