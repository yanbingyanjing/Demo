package com.yjfshop123.live.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yjfshop123.live.ActivityUtils;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.response.IncomeRankingResponse;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.CircleImageView;
import com.yjfshop123.live.ui.activity.LoginActivity;
import com.yjfshop123.live.ui.adapter.H4Adapter;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.utils.UserInfoUtil;
import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class H_1_4_1_1_Fragment extends BaseFragment {

    private int page = 1;

    private List<IncomeRankingResponse.ListBean> mList = new ArrayList<>();

    @BindView(R.id.shimmer_recycler_view)
    RecyclerView shimmerRecycler;

    private View topView;

    private H4Adapter h4Adapter;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean isLoadingMore = false;

    private int type = 0;//0女神榜 1富豪榜
    private int type_2 = 0 ;//0日 1月 2总

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity();
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getInt("type", 0);
            type_2 = bundle.getInt("type_2", 0);
        }
    }

    @Override
    protected int setContentViewById() {
        return R.layout.fragment_h_4_1_1;
    }

    @Override
    protected void initAction() {
        topView = View.inflate(mContext.getApplicationContext(), R.layout.list_top_header, null);

        mLinearLayoutManager = new LinearLayoutManager(mContext);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);

        h4Adapter = new H4Adapter(mContext, topView);
        shimmerRecycler.setAdapter(h4Adapter);
    }

    private int info_complete;

    @Override
    public void onResume() {
        super.onResume();
        info_complete = UserInfoUtil.getInfoComplete();
    }

    private boolean isLogin(){
        boolean login;
        if (info_complete == 0) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            login = false;
        } else {
            login = true;
        }
        return login;
    }

    @Override
    protected void initEvent() {
        h4Adapter.setOnItemClickListener(new H4Adapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                if (!isLogin()){
                    return;
                }

                if (postion < 1) {
                    return;
                }
                postion = postion + 2;
                ActivityUtils.startUserHome(mContext,String.valueOf(mList.get(postion).getUser_id()));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });

        shimmerRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mLinearLayoutManager.getItemCount();

                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        ranking();
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        ranking();
        showLoading();
    }

    private void topView() {
        if (mList.size() == 0){
            return;
        }

        CircleImageView list_top_icon_1 = topView.findViewById(R.id.list_top_icon_1);
        TextView list_top_name_1 = topView.findViewById(R.id.list_top_name_1);
        TextView list_top_gold_1 = topView.findViewById(R.id.list_top_gold_1);
        TextView list_top_sex_1 = topView.findViewById(R.id.list_top_sex_1);
        if (mList.size() > 0){
            list_top_name_1.setText(mList.get(0).getUser_nickname());
            list_top_gold_1.setText(mList.get(0).getCoin() + getString(R.string.my_jinbi));
            if (mList.get(0).getSex() == 1){
                Drawable drawable= getResources().getDrawable(R.drawable.male_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                list_top_sex_1.setCompoundDrawables(drawable, null, null, null);
                list_top_sex_1.setText(" " + mList.get(0).getAge());//♂♀
                list_top_sex_1.setEnabled(false);
            }else {
                Drawable drawable= getResources().getDrawable(R.drawable.female_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                list_top_sex_1.setCompoundDrawables(drawable, null, null, null);
                list_top_sex_1.setText(" " + mList.get(0).getAge());//♂♀
                list_top_sex_1.setEnabled(true);
            }
            Glide.with(mContext)
                    .load(CommonUtils.getUrl(mList.get(0).getShow_photo()))
                    .into(list_top_icon_1);
        }
        list_top_icon_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startUserHome(mContext,String.valueOf(mList.get(0).getUser_id()));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });
        if (mList.size() == 1){
            return;
        }


        CircleImageView list_top_icon_2 = topView.findViewById(R.id.list_top_icon_2);
        TextView list_top_name_2 = topView.findViewById(R.id.list_top_name_2);
        TextView list_top_gold_2 = topView.findViewById(R.id.list_top_gold_2);
        TextView list_top_sex_2 = topView.findViewById(R.id.list_top_sex_2);
        if (mList.size() > 1){
            list_top_name_2.setText(mList.get(1).getUser_nickname());
            list_top_gold_2.setText(mList.get(1).getCoin() + getString(R.string.my_jinbi));
            if (mList.get(1).getSex() == 1){
                Drawable drawable= getResources().getDrawable(R.drawable.male_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                list_top_sex_2.setCompoundDrawables(drawable, null, null, null);
                list_top_sex_2.setText(" " + mList.get(1).getAge());//♂♀
                list_top_sex_2.setEnabled(false);
            }else {
                Drawable drawable= getResources().getDrawable(R.drawable.female_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                list_top_sex_2.setCompoundDrawables(drawable, null, null, null);
                list_top_sex_2.setText(" " + mList.get(1).getAge());//♂♀
                list_top_sex_2.setEnabled(true);
            }
            Glide.with(mContext)
                    .load(CommonUtils.getUrl(mList.get(1).getShow_photo()))
                    .into(list_top_icon_2);
        }
        list_top_icon_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startUserHome(mContext,String.valueOf(mList.get(1).getUser_id()));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });
        if (mList.size() == 2){
            return;
        }


        CircleImageView list_top_icon_3 = topView.findViewById(R.id.list_top_icon_3);
        TextView list_top_name_3 = topView.findViewById(R.id.list_top_name_3);
        TextView list_top_gold_3 = topView.findViewById(R.id.list_top_gold_3);
        TextView list_top_sex_3 = topView.findViewById(R.id.list_top_sex_3);
        if (mList.size() > 2){
            list_top_name_3.setText(mList.get(2).getUser_nickname());
            list_top_gold_3.setText(mList.get(2).getCoin() + getString(R.string.my_jinbi));
            if (mList.get(2).getSex() == 1){
                Drawable drawable= getResources().getDrawable(R.drawable.male_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                list_top_sex_3.setCompoundDrawables(drawable, null, null, null);
                list_top_sex_3.setText(" " + mList.get(2).getAge());//♂♀
                list_top_sex_3.setEnabled(false);
            }else {
                Drawable drawable= getResources().getDrawable(R.drawable.female_icon);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                list_top_sex_3.setCompoundDrawables(drawable, null, null, null);
                list_top_sex_3.setText(" " + mList.get(2).getAge());//♂♀
                list_top_sex_3.setEnabled(true);
            }
            Glide.with(mContext)
                    .load(CommonUtils.getUrl(mList.get(2).getShow_photo()))
                    .into(list_top_icon_3);
        }
        list_top_icon_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startUserHome(mContext,String.valueOf(mList.get(2).getUser_id()));
                getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
            }
        });

    }

    @Override
    protected void updateViews(boolean isRefresh) {
        if (!isRefresh){
            showLoading();
        }

        page = 1;
        ranking();
    }

    private void ranking(){
        String url;
        int type_ = type_2 + 1 ;//type_2 0日 1月 2总
        if (type == 0){//女神榜
            url = "app/index/incomeRanking";
        }else if (type == 1){//富豪榜
            url = "app/index/payoutRanking";
        }else {
            return;
        }
        String body = "";
        try {
            body = new JsonBuilder()
                    .put("type", type_)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest(url, body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                hideLoading();
                if (page == 1){
                    notData();
                }
            }
            @Override
            public void onSuccess(String result) {
                hideLoading();
                try {
                    IncomeRankingResponse response = JsonMananger.jsonToBean(result, IncomeRankingResponse.class);
                    isLoadingMore = false;
                    if (page == 1 && mList.size() > 0) {
                        mList.clear();
                    }

                    mList.addAll(response.getList());
                    h4Adapter.setCards(mList);
                    h4Adapter.notifyDataSetChanged();

                    if (page == 1){
                        topView();
                    }

                    if (mList.size() == 0){
                        notData();
                    }
                } catch (HttpException e) {
                    e.printStackTrace();
                    if (page == 1){
                        notData();
                    }
                }
            }
        });
    }

    private void notData(){
        if (mList.size() > 0) {
            mList.clear();
        }
        mList.add(new IncomeRankingResponse.ListBean());
        mList.add(new IncomeRankingResponse.ListBean());
        mList.add(new IncomeRankingResponse.ListBean());
        h4Adapter.setCards(mList);
        h4Adapter.notifyDataSetChanged();
    }
}