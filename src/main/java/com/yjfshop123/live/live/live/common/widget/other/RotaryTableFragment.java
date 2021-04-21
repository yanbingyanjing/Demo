package com.yjfshop123.live.live.live.common.widget.other;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yjfshop123.live.Const;
import com.yjfshop123.live.R;
import com.yjfshop123.live.http.OKHttpUtils;
import com.yjfshop123.live.Interface.RequestCallback;
import com.yjfshop123.live.live.live.common.utils.DialogUitl;
import com.yjfshop123.live.live.live.common.widget.gift.AbsDialogFragment;
import com.yjfshop123.live.live.response.BigTurntableListResponse;
import com.yjfshop123.live.model.PriceResponse;
import com.yjfshop123.live.net.utils.NToast;
import com.yjfshop123.live.net.utils.json.JsonMananger;
import com.yjfshop123.live.server.utils.imageloader.CircleBitmapDisplayer;
import com.yjfshop123.live.server.utils.imageloader.DisplayImageOptions;
import com.yjfshop123.live.server.widget.LoadDialog;
import com.yjfshop123.live.ui.activity.HomeActivity;
import com.yjfshop123.live.utils.CommonUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.cretin.www.wheelsruflibrary.listener.RotateListener;
import com.cretin.www.wheelsruflibrary.view.WheelSurfView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RotaryTableFragment extends AbsDialogFragment implements View.OnClickListener {

    private WheelSurfView mWheelSurfView;
    private PriceResponse mResponse;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_rotary_table;
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
        params.width = CommonUtils.dip2px(mContext, 320);
        params.height = CommonUtils.dip2px(mContext, 320);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWheelSurfView = mRootView.findViewById(R.id.wheelSurfView);
        mWheelSurfView.setVisibility(View.INVISIBLE);
        loadData();
    }

    @Override
    public void onClick(View v) {

    }

    String testData = "{\n" +
            "    \"product_price\":11,\n" +
            "  \n" +
            "    \"list\": [\n" +
            "        {\n" +
            "            \"id\": 1,\n" +
            "            \"prize_name\": \"奖品1\",\n" +
            "\"prize_icon\":\"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "\"product_number\":2\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 2,\n" +
            "            \"prize_name\": \"奖品2\",\n" +
            "\"prize_icon\":\"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "\"product_number\":2\n" +
            "        \n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 2,\n" +
            "            \"prize_name\": \"奖品2\",\n" +
            "\"prize_icon\":\"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "\"product_number\":2\n" +
            "        },\n" +

            "        {\n" +
            "            \"id\": 2,\n" +
            "            \"prize_name\": \"奖品2\",\n" +
            "\"prize_icon\":\"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "\"product_number\":2\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 2,\n" +
            "            \"prize_name\": \"奖品2\",\n" +
            "\"prize_icon\":\"https://zb-1302869529.cos.ap-shanghai.myqcloud.com/upload/20200827/3207D06442CCE9B7F6512BA47D5E13BF.jpg?imageMogr2/thumbnail/600x\",\n" +
            "\"product_number\":2\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    private void loadData() {
        if (!com.yjfshop123.live.server.utils.CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, getString(R.string.net_error));
            return;
        }

       // LoadDialog.show(getActivity());
        OKHttpUtils.getInstance().getRequest("app/prize/getList", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LoadDialog.dismiss(getActivity());
                dismiss();
//                try {
//                    mResponse = JsonMananger.jsonToBean(testData, PriceResponse.class);
//                    loadData_();
//                } catch (Exception e) {
//                    LoadDialog.dismiss(getActivity());
//                }
            }

            @Override
            public void onSuccess(String result) {
                try {
                    mResponse = JsonMananger.jsonToBean(result, PriceResponse.class);
                    loadData_();
                } catch (Exception e) {
                    LoadDialog.dismiss(getActivity());
                }
            }
        });
    }

    private void loadData_() {

        size = mResponse.list.size();
        if(size==0)dismiss();
        colors = new Integer[size];//颜色
        txcolors= new Integer[size];//颜色
        mDes = new String[size];//文字
        for (int i = 0; i < size; i++) {

            if (i % 2 == 0) {
                txcolors[i] = Color.parseColor("#FFFCE4E3");
                colors[i] = Color.parseColor("#FFCD4830");
            } else {
                colors[i] = Color.parseColor("#FFFCE4E3");
                txcolors[i] = Color.parseColor("#FFCD4830");
            }

            mDes[i] = mResponse.list.get(i).prize_name;
        }
        //product_price = mResponse.get(i).prize_num;

        displaysImage();
    }

    List<Bitmap> mListBitmap = new ArrayList<>();//图标
    private String[] mDes;
    private Integer[] colors;
    private Integer[] txcolors;
    private int mPosition = 0;
    private int size;
    private int product_price;

    private int getPosition(int position) {
        if (position == 0) {
            position = 1;
        } else if (position == 1) {
            position = 0;
        } else {
            position = size - position + 1;
        }
        return position;
    }

    HomeActivity homeActivity;

    public void setHomeActivity(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private DisplayImageOptions options;

    private void displaysImage() {
        if (options == null) {
            options = createDisplayImageOptions();
        }
        new AsyncTask<Void, File, Void>() {

            @Override
            protected Void doInBackground(Void... values) {
                for (int i = 0; i < size; i++) {
                    File file;
                    try {
                        String url = mResponse.list.get(i).prize_icon;

                        file = Glide.with(mContext)
                                .load(url)
                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();
                    } catch (Exception e) {
                        file = null;
                    }
                    try {
                        if (file == null) {
                            file = Glide.with(mContext)
                                    .load(R.mipmap.buy_gold)
                                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                    .get();
                        }
                    } catch (Exception e) {
                    }
                    publishProgress(file);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(File... files) {
                super.onProgressUpdate(files);
                Bitmap bitmap;
                if (files[0] == null) {
                    Drawable drawable = options.getImageForEmptyUri(null);
                    bitmap = drawableToBitmap(drawable);
                } else {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(files[0].getAbsolutePath(), opt);
                    opt.inSampleSize = computeSampleSize(opt, 200, 200 * 200);
                    opt.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(files[0].getAbsolutePath(), opt);
                }
                mListBitmap.add(bitmap);
            }

            @Override
            protected void onPostExecute(Void values) {
                super.onPostExecute(values);

                LoadDialog.dismiss(getActivity());
                mWheelSurfView.setVisibility(View.VISIBLE);

                //主动旋转一下图片
                mListBitmap = WheelSurfView.rotateBitmaps(mListBitmap);
                //获取第三个视图
                WheelSurfView.Builder build = new WheelSurfView.Builder()
                        .setmColors(colors)
                        .setTxmColors(txcolors)
                        .setmDeses(mDes)
                        .setmIcons(mListBitmap)
                        .setmType(1)
                        .setmTypeNum(size)
                        .build();
                mWheelSurfView.setConfig(build);

                //添加滚动监听
                mWheelSurfView.setRotateListener(new RotateListener() {

                    @Override
                    public void rotateEnd(int position, String des) {
                        NToast.shortToast(mContext, mDes[mPosition]);
                        dismiss();
                        if (homeActivity != null) homeActivity.loadData();
                    }

                    @Override
                    public void rotating(ValueAnimator valueAnimator) {

                    }

                    @Override
                    public void rotateBefore(ImageView goImg) {
                        go();
//                        DialogUitl.showSimpleHintDialog(mContext, getString(R.string.xy_1),
//                                getString(R.string.ac_select_friend_sure),
//                                getString(R.string.cancel),
//                                "确定要花费" + product_price + "金蛋抽奖？",
//                                true,
//                                true,
//                                new DialogUitl.SimpleCallback2() {
//                                    @Override
//                                    public void onCancelClick() {
//                                    }
//                                    @Override
//                                    public void onConfirmClick(Dialog dialog, String content) {
//                                        dialog.dismiss();
//
//                                    }
//                                });
                    }
                });

            }
        }.execute();
    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        @SuppressLint("WrongConstant")
        Bitmap.Config config = drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    private DisplayImageOptions createDisplayImageOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.displayer(new CircleBitmapDisplayer());
        DisplayImageOptions options = builder.resetViewBeforeLoading(false).cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        return options;
    }

    private void go() {
        OKHttpUtils.getInstance().getRequest("app/prize/selectPrize", "", new RequestCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                NToast.shortToast(mContext, errInfo);
//                try {  JSONObject data = new JSONObject("{\"id\":2}");
//                int id = data.getInt("id");
//                for (int i = 0; i < size; i++) {
//                    int id_ = mResponse.list.get(i).id;
//                    if (id == id_){
//                        mPosition = i;
//                        mWheelSurfView.startRotate(getPosition(mPosition));
//                        break;
//                    }
//                } } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject data = new JSONObject(result);
                    int id = data.getInt("id");
                    for (int i = 0; i < size; i++) {
                        int id_ = mResponse.list.get(i).id;
                        if (id == id_) {
                            mPosition = i;
                            mWheelSurfView.startRotate(getPosition(mPosition));
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

