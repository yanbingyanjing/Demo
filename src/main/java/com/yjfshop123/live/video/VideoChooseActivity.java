package com.yjfshop123.live.video;


import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.widget.gift.utils.Constants;
import com.yjfshop123.live.live.live.common.widget.gift.utils.OnItemClickListener;
import com.yjfshop123.live.ui.CommonCallback;
import com.yjfshop123.live.video.adapter.VideoChooseAdapter;
import com.yjfshop123.live.video.bean.VideoChooseBean;
import com.yjfshop123.live.video.custom.ItemDecoration;
import com.yjfshop123.live.video.utils.VideoLocalUtil;

import java.util.List;


public class VideoChooseActivity extends AbsActivity implements OnItemClickListener<VideoChooseBean> {

    private long mMaxDuration;
    private RecyclerView mRecyclerView;
    private View mNoData;
    private VideoLocalUtil mVideoLocalUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_choose;
    }

    @Override
    protected void main() {
        setTitle(getString(R.string.video_local));
        mMaxDuration = getIntent().getLongExtra(Constants.VIDEO_DURATION, 15000);
        mNoData = findViewById(R.id.no_data);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 1, 1);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mVideoLocalUtil = new VideoLocalUtil(mContext);
        mVideoLocalUtil.getLocalVideoList(new CommonCallback<List<VideoChooseBean>>() {
            @Override
            public void callback(List<VideoChooseBean> videoList) {
                if (videoList == null || videoList.size() == 0) {
                    if (mNoData != null && mNoData.getVisibility() != View.VISIBLE) {
                        mNoData.setVisibility(View.VISIBLE);
                    }
                    return;
                }
                if (mRecyclerView != null) {
                    VideoChooseAdapter adapter = new VideoChooseAdapter(mContext, videoList);
                    adapter.setOnItemClickListener(VideoChooseActivity.this);
                    mRecyclerView.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onItemClick(VideoChooseBean bean, int position) {
//        if (bean.getDuration() > mMaxDuration + 1000) {
//            ToastUtil.show(R.string.video_duration_error);
//            return;
//        }
        Intent intent = new Intent();
        intent.putExtra(Constants.VIDEO_PATH, bean.getVideoPath());
        intent.putExtra(Constants.VIDEO_DURATION, bean.getDuration());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mVideoLocalUtil != null) {
            mVideoLocalUtil.release();
        }
        mVideoLocalUtil = null;
        mRecyclerView = null;
        mNoData = null;
        super.onDestroy();
    }
}
