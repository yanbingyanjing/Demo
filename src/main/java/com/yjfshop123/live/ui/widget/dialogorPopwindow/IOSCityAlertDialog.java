package com.yjfshop123.live.ui.widget.dialogorPopwindow;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

public class IOSCityAlertDialog implements OnWheelChangedListener {
    private Context context;
    private Dialog dialog;
    private LinearLayout lLayout_bg;
    private TextView txt_title;

    private WheelView provincewheel;
    private WheelView citywheel;
    private WheelView areawheel;

    private TextView btn_cancel;
    private TextView btn_confirm;
    private Display display;

    // 所有省
    private String[] mProvinceDatas;
    //省-市
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    //市-区
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();

    private JSONObject mJsonObj;
    private  int provincePosition = 0;
    private  int cityPosition = 0;

    public IOSCityAlertDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public IOSCityAlertDialog builder() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_city_alert_dialog, null);

        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        provincewheel = (WheelView) view.findViewById(R.id.provincewheel);
        citywheel = (WheelView) view.findViewById(R.id.citywheel);
        areawheel = (WheelView) view.findViewById(R.id.areawheel);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT));

        //加载数据
        getJson();
        initDatas();
        setView();

        return this;
    }

    /**
     * 设置title
     *
     * @param title
     * @return
     */
    public IOSCityAlertDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)){
            txt_title.setText(title);
        }
        return this;
    }

    /**
     * 设置点击外部是否消失
     *
     * @param cancel
     * @return
     */
    public IOSCityAlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    /**
     * 右侧按钮
     *
     * @param text
     * @param listener
     * @return
     */
    public IOSCityAlertDialog setPositiveButton(String text, final View.OnClickListener listener) {
        return setPositiveButton(text, -1, listener);
    }

    public IOSCityAlertDialog setPositiveButton(String text, int color, final View.OnClickListener listener) {
        if ("".equals(text)) {
            btn_confirm.setText("");
        } else {
            btn_confirm.setText(text);
        }
        if (color == -1) {
            color = R.color.color_style;
        }
        btn_confirm.setTextColor(ContextCompat.getColor(context, color));
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(v);
                dismiss();
            }
        });
        return this;
    }

    /**
     * 左侧按钮
     *
     * @param text
     * @param listener
     * @return
     */

    public IOSCityAlertDialog setNegativeButton(String text, final View.OnClickListener listener) {

        return setNegativeButton(text, -1, listener);
    }

    public IOSCityAlertDialog setNegativeButton(String text, int color, final View.OnClickListener listener) {
        if ("".equals(text)) {
            btn_cancel.setText("");
        } else {
            btn_cancel.setText(text);
        }
        if (color == -1) {
            color = R.color.color_000000;
        }
        btn_cancel.setTextColor(ContextCompat.getColor(context, color));

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(v);
                dismiss();
            }
        });
        return this;
    }

    public void show() {
        dialog.show();
    }

    public boolean isShowing() {
        if (dialog != null) {
            if (dialog.isShowing())
                return true;
            else
                return false;
        }
        return false;
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private String cityStr;

    public String getCityStr() {
        return cityStr;
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == provincewheel) {
            updateCityWheel();
            if (mAreaDatasMap.get(mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition]) == null) {
                cityStr = mProvinceDatas[provincePosition] + "," + mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition];
            } else {
                cityStr = mProvinceDatas[provincePosition] + "," + mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition] + "," + mAreaDatasMap.get(mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition])[0];
            }
        } else if (wheel == citywheel) {
            updateAreaWheel();
            if (mAreaDatasMap.get(mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition]) == null) {
                cityStr = mProvinceDatas[provincePosition] + "," + mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition];
            } else {
                cityStr = mProvinceDatas[provincePosition] + "," + mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition] + "," + mAreaDatasMap.get(mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition])[0];

            }
        } else if (wheel == areawheel) {
            cityStr = mProvinceDatas[provincePosition] + "," + mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition] + "," + mAreaDatasMap.get(mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition])[newValue];
        }
    }

    private void setView() {
        ArrayWheelAdapter arrayWheelAdapter = new ArrayWheelAdapter<String>(context, mProvinceDatas, R.layout.wheel_layout, R.id.wheel_layout_tv);
//        arrayWheelAdapter.setItemResource();
        provincewheel.setViewAdapter(arrayWheelAdapter);
        provincewheel.setCurrentItem(0);

        //监听
        provincewheel.addChangingListener(this);
        citywheel.addChangingListener(this);
        areawheel.addChangingListener(this);
        //显示的单元格数目
        provincewheel.setVisibleItems(3);
        citywheel.setVisibleItems(3);
        areawheel.setVisibleItems(3);
        //更新
        updateCityWheel();
        updateAreaWheel();
        cityStr = mProvinceDatas[provincePosition] + "," + mCitisDatasMap.get(mProvinceDatas[provincePosition])[citywheel.getCurrentItem()] + "," + mAreaDatasMap.get(mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition])[areawheel.getCurrentItem()];
    }

    private void updateCityWheel() {
        provincePosition = provincewheel.getCurrentItem();
        String[] cities = mCitisDatasMap.get(mProvinceDatas[provincePosition]);
        if (cities == null) {
            cities = new String[]{""};
        }
        citywheel.setViewAdapter(new ArrayWheelAdapter<String>(context, cities, R.layout.wheel_layout, R.id.wheel_layout_tv));
        citywheel.setCurrentItem(0);
        updateAreaWheel();
    }

    private void updateAreaWheel() {
        cityPosition = citywheel.getCurrentItem();
        String[] areas = mAreaDatasMap.get(mCitisDatasMap.get(mProvinceDatas[provincePosition])[cityPosition]);

        if (areas == null) {
            areas = new String[]{""};
        }
        areawheel.setViewAdapter(new ArrayWheelAdapter<String>(context, areas, R.layout.wheel_layout, R.id.wheel_layout_tv));
        areawheel.setCurrentItem(0);
    }

    private void getJson() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("city.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }

            mJsonObj = new JSONObject(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 解析JSon
    private void initDatas() {
        try {
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            mProvinceDatas = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonP = jsonArray.getJSONObject(i);
                String province = jsonP.getString("name");
                mProvinceDatas[i] = province;
                JSONArray jsonCs = null;
                try {
                    jsonCs = jsonP.getJSONArray("city");
                } catch (Exception e1) {
                    continue;
                }
                String[] mCitiesDatas = new String[jsonCs.length()];
                for (int j = 0; j < jsonCs.length(); j++) {
                    JSONObject jsonCity = jsonCs.getJSONObject(j);
                    String city = jsonCity.getString("name");
                    mCitiesDatas[j] = city;
                    JSONArray jsonAreas = null;
                    try {
                        jsonAreas = jsonCity.getJSONArray("area");
                    } catch (Exception e) {
                        continue;
                    }

                    String[] mAreasDatas = new String[jsonAreas.length()];
                    for (int k = 0; k < jsonAreas.length(); k++) {
                        String area = jsonAreas.get(k).toString();
                        mAreasDatas[k] = area;
                    }
                    mAreaDatasMap.put(city, mAreasDatas);
                }
                mCitisDatasMap.put(province, mCitiesDatas);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonObj = null;
    }
}
