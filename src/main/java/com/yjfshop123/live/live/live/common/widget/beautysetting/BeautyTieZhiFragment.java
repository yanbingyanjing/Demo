package com.yjfshop123.live.live.live.common.widget.beautysetting;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.widget.beautysetting.utils.VideoDeviceUtil1;
import com.yjfshop123.live.live.live.common.widget.beautysetting.utils.VideoMaterialDownloadProgress;
import com.yjfshop123.live.live.response.TiezhiResponse;
import com.yjfshop123.live.net.network.http.HttpException;
import com.yjfshop123.live.net.utils.json.JsonMananger;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BeautyTieZhiFragment extends DialogFragment {

    public static final int BEAUTYPARAM_MOTION_TMPL = 6;//贴纸
    private View mLayoutPitu;

    static public class BeautyParams{
        public VideoMaterialMetaData mVideoMaterialMetaData;
    }

    public interface OnBeautyTieZhiChangeListener{
        void onBeautyTieZhiChange(BeautyParams params, int key);
    }

    private BeautyParams mBeautyParams;
    private OnBeautyTieZhiChangeListener mOnBeautyTieZhiChangeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog2);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_beauty_tiezhi);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消

        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.BottomDialog_Animation);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);

        mLayoutPitu = dialog.findViewById(R.id.material_recycler_view);

        OKHttpUtils.getInstance().getRequest("sprout/tie/getTiezhiList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }
            @Override
            public void onSuccess(String result) {
                try {
                    JSONArray jsa = new JSONArray(result);
                    pitu(JsonMananger.jsonToBean(jsa.getString(0), TiezhiResponse.class));
                } catch (HttpException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        IBeauty iBeauty = (IBeauty) getActivity();
        if(iBeauty != null){
            iBeauty.closeTieZhi(mBeautyParams);
        }
    }

    public void setBeautyParamsListner(BeautyParams params, OnBeautyTieZhiChangeListener listener){
        mBeautyParams = params;
        mOnBeautyTieZhiChangeListener = listener;
        if (mOnBeautyTieZhiChangeListener instanceof OnBeautyTieZhiChangeListener){
            mOnBeautyTieZhiChangeListener.onBeautyTieZhiChange(mBeautyParams, BEAUTYPARAM_MOTION_TMPL);
        }
    }

    private void pitu(TiezhiResponse response){
        if (response == null){
            return;
        }

        List<VideoMaterialMetaData> materials = new ArrayList<>();
        materials.add(new VideoMaterialMetaData("video_none", "video_none", "", ""));
        for (int i = 0; i < response.getList().size(); i++) {
            TiezhiResponse.ListBean listBean = response.getList().get(i);
            materials.add(new VideoMaterialMetaData(listBean.getName(), "", listBean.getEffect(), listBean.getIcon()));

        }
        materials = loadLocalMaterials(materials);
        mMaterialAdapter = new MaterialAdapter(getActivity(), materials, mSelectPosition);
        mMaterialAdapter.setOnItemClickListener(mFilterClickListener);
        RecyclerView recyclerView = (RecyclerView)mLayoutPitu;

//        recyclerView.addItemDecoration(new PaddingItemDecoration2(getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView.setAdapter(mMaterialAdapter);
    }

    private int mSelectPosition = 0;
    private MaterialAdapter mMaterialAdapter;
    private MaterialAdapter.OnItemClickListener mFilterClickListener = new MaterialAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(VideoMaterialMetaData materialMetaData) {
            mBeautyParams.mVideoMaterialMetaData = materialMetaData;
            if (mOnBeautyTieZhiChangeListener instanceof OnBeautyTieZhiChangeListener){
                mOnBeautyTieZhiChangeListener.onBeautyTieZhiChange(mBeautyParams, BEAUTYPARAM_MOTION_TMPL);
            }
        }
    };

    private List<VideoMaterialMetaData> loadLocalMaterials(List<VideoMaterialMetaData> materials) {
        String path = VideoDeviceUtil1.getExternalFilesDir(getActivity()).getPath() + File.separator +
                VideoMaterialDownloadProgress.ONLINE_MATERIAL_FOLDER;

        for (int i = 0; i < materials.size(); i++) {
            VideoMaterialMetaData material = materials.get(i);
            if(TextUtils.isEmpty(material.path)) {
                String path_ = path + File.separator + material.id + ".mp3";
                if (VideoDeviceUtil1.isFilesDir(path_)){
                    material.path = path_;

                    if (path_.equals(mBeautyParams.mVideoMaterialMetaData.path)){
                        mSelectPosition = i;
                    }
                }
            }
        }

//        Iterator var1 = materials.iterator();
//        while(var1.hasNext()) {
//            VideoMaterialMetaData material = (VideoMaterialMetaData)var1.next();
//            if(TextUtils.isEmpty(material.path)) {
//                String path_ = path + File.separator + material.id + ".mp3";
//                if (VideoDeviceUtil1.isFilesDir(path_)){
//                    material.path = path_;
//                }
//                material.path = mPrefs.getString(material.id, "");
//            }
//        }
        return materials;
    }

//    private SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(VideoUtil1.getContext());

}