package com.yjfshop123.live.live.live.common.widget.music;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.JsonBuilder;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.beautysetting.utils.VideoDeviceUtil1;
import com.yjfshop123.live.live.live.common.widget.beautysetting.utils.VideoMaterialDownloadManager;
import com.yjfshop123.live.live.live.common.widget.beautysetting.utils.VideoMaterialDownloadProgress;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.live.live.push.TCLiveBasePublisherActivity;
import com.yjfshop123.live.live.response.MusicDownloadResponse;
import com.yjfshop123.live.live.response.MusicNameListResponse;
import com.yjfshop123.live.net.utils.MD5;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.widget.shimmer.SwipeRefreshHelper;
import com.yjfshop123.live.ui.widget.shimmer.VerticalSwipeRefreshLayout;
import com.yjfshop123.live.utils.CommonUtils;
import com.yjfshop123.live.video.VideoEditActivity;
import com.yjfshop123.live.video.VideoRecordActivity;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LiveMusicDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener {

    private MusicAdapter musicAdapter;
    private List<MusicNameListResponse.ListBean> mList = new ArrayList<>();

    private VerticalSwipeRefreshLayout mSwipeRefresh;
    private RecyclerView shimmerRecycler;
    private LinearLayoutManager linearLayoutManager;
    private TextView dialog_music_list_tv;

    private EditText searchEt;
    private ImageView searchDele;

    private boolean isLoadingMore = false;
    private int page = 1;

    private String musicName = "";//搜索内容
    private Handler mHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_music_list;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.BottomDialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = CommonUtils.dip2px(mContext, 280);
        params.height = CommonUtils.dip2px(mContext, 360);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler = new Handler(mContext.getMainLooper());

        mSwipeRefresh = mRootView.findViewById(R.id.swipe_refresh);
        shimmerRecycler = mRootView.findViewById(R.id.shimmer_recycler_view);
        dialog_music_list_tv = mRootView.findViewById(R.id.dialog_music_list_tv);

        searchEt = mRootView.findViewById(R.id.dialog_music_search_et);
        searchDele = mRootView.findViewById(R.id.dialog_music_search_dele);
        mRootView.findViewById(R.id.dialog_music_search_search).setOnClickListener(this);
        searchDele.setOnClickListener(this);
        searchEt.addTextChangedListener(mSearchEtWatcher);

        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        shimmerRecycler.setLayoutManager(linearLayoutManager);
        musicAdapter = new MusicAdapter(mContext);
        shimmerRecycler.setAdapter(musicAdapter);
        musicAdapter.setOnItemClickListener(this);

        shimmerRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItem;
                int totalItemCount;
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                totalItemCount = linearLayoutManager.getItemCount();

                //表示剩下4个item自动加载，各位自由选择
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        page++;
                        loadData();
                    }
                }
            }
        });

        initSwipeRefresh();
        page = 1;
        loadData();
    }

    private TextWatcher mSearchEtWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            musicName = charSequence.toString().trim();
            if (musicName.length() > 0) {
                searchDele.setVisibility(View.VISIBLE);
            } else {
                searchDele.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_music_search_search:
                page = 1;
                loadData();
                break;
            case R.id.dialog_music_search_dele:
                searchEt.setText("");
                searchDele.setVisibility(View.GONE);
                break;
        }
//        dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadData() {
        if (!com.yjfshop123.live.server.utils.CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, getString(R.string.net_error));
            notData();
            return;
        }

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("musicName", musicName)
                    .put("page", page)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/music/getMusicName", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (page == 1){
                    notData();
                }
            }
            @Override
            public void onSuccess(String result) {
                finishRefresh();
                dialog_music_list_tv.setVisibility(View.GONE);
                try{
                    MusicNameListResponse wuluResponse = JsonMananger.jsonToBean(result, MusicNameListResponse.class);
                    isLoadingMore = false;
                    if (page == 1) {
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                    }

                    for (int i = 0; i < wuluResponse.getList().size(); i++) {
                        if (wuluResponse.getList().get(i).getIs_pay().equals("0")){
                            mList.add(wuluResponse.getList().get(i));
                        }
                    }
                    //mList.addAll(wuluResponse.getList());

                    musicAdapter.setCards(mList);
                    musicAdapter.notifyDataSetChanged();

                    if (mList.size() == 0){
                        notData();
                    }
                }catch (Exception e){
                    if (page == 1){
                        notData();
                    }
                }
            }
        });
    }

    private void notData(){
        finishRefresh();
        dialog_music_list_tv.setVisibility(View.VISIBLE);
    }

    private void finishRefresh() {
        if (mSwipeRefresh != null) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void initSwipeRefresh() {
        if (mSwipeRefresh != null) {
            SwipeRefreshHelper.init(mSwipeRefresh, new VerticalSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    page = 1;
                    loadData();
                }
            });
        }
    }

    @Override
    public void onItemClick(Object object, int position) {
        mBean = (MusicNameListResponse.ListBean) object;
        download(String.valueOf(mBean.getId()));
    }
    private MusicNameListResponse.ListBean mBean;

    private void download(String id) {
        LoadDialog.show(getActivity());
        if (!com.yjfshop123.live.server.utils.CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, getString(R.string.net_error));
            LoadDialog.dismiss(getActivity());
            return;
        }

        String body = "";
        try {
            body = new JsonBuilder()
                    .put("id", id)
                    .build();
        } catch (JSONException e) {
        }
        OKHttpUtils.getInstance().getRequest("app/music/getMusicDownload", body, new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
                LoadDialog.dismiss(getActivity());
            }
            @Override
            public void onSuccess(String result) {
                try{
                    MusicDownloadResponse musicDownloadResponse = JsonMananger.jsonToBean(result, MusicDownloadResponse.class);
                    download_(CommonUtils.getUrl(musicDownloadResponse.getMsg()));
                }catch (Exception e){
                    LoadDialog.dismiss(getActivity());
                }
            }
        });
    }

    private void download_(String msgUrl) {
        String id = MD5.encrypt(msgUrl);
        String path = VideoDeviceUtil1.getExternalFilesDir(getActivity()).getPath() + File.separator +
                VideoMaterialDownloadProgress.ONLINE_MATERIAL_FOLDER;
        String path_ = path + File.separator + id + VideoMaterialDownloadProgress.DOWNLOAD_FILE_POSTFIX;

        if (VideoDeviceUtil1.isFilesDir(path_)){
            LoadDialog.dismiss(getActivity());
            if (mContext instanceof TCLiveBasePublisherActivity){
                ((TCLiveBasePublisherActivity) mContext).onBGMStart_(path_, mBean.getMusic_name());
            }else if (mContext instanceof VideoRecordActivity){
                ((VideoRecordActivity) mContext).onBGMStart_(path_, mBean.getMusic_name());
            }else if (mContext instanceof VideoEditActivity){
                ((VideoEditActivity) mContext).onBGMStart_(path_, mBean.getMusic_name());
            }
            dismiss();
            return;
        }

        final VideoMaterialDownloadProgress downloadProgress = VideoMaterialDownloadManager.getInstance().get(id, msgUrl, false);
        final VideoMaterialDownloadProgress.Downloadlistener listener = new VideoMaterialDownloadProgress.Downloadlistener() {
            @Override
            public void onDownloadFail(final String errorMsg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LoadDialog.dismiss(getActivity());
                        NToast.shortToast(mContext, errorMsg);
                    }
                });
            }

            @Override
            public void onDownloadProgress(final int progress) {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
            }

            @Override
            public void onDownloadSuccess(final String filePath) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LoadDialog.dismiss(getActivity());
                        if (mContext instanceof TCLiveBasePublisherActivity){
                            ((TCLiveBasePublisherActivity) mContext).onBGMStart_(filePath, mBean.getMusic_name());
                        }else if (mContext instanceof VideoRecordActivity){
                            ((VideoRecordActivity) mContext).onBGMStart_(filePath, mBean.getMusic_name());
                        }else if (mContext instanceof VideoEditActivity){
                            ((VideoEditActivity) mContext).onBGMStart_(filePath, mBean.getMusic_name());
                        }
                        dismiss();
                    }
                });
            }
        };
        downloadProgress.start(listener);
    }
}
