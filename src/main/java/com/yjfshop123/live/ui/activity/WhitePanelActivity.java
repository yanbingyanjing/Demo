package com.yjfshop123.live.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjfshop123.live.R;
import com.yjfshop123.live.ui.widget.StarWhitePanel2;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WhitePanelActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title_center)
    TextView tv_title_center;

    @BindView(R.id.painter_view)
    StarWhitePanel2 vPaintPlayer;

    @BindView(R.id.clean_btn)
    Button vCleanBtn;

    @BindView(R.id.revoke_btn)
    Button vRevokeBtn;

    @BindView(R.id.laser_btn)
    Button vLaserPenBtn;

    @BindView(R.id.select_color_btn)
    Button vSelectColorBtn;

    @BindView(R.id.select_color_view)
    LinearLayout vSelectColorView;

    private boolean isLaser = false;//默认画笔

//    protected Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_panel);
        ButterKnife.bind(this);
        setHeadLayout();
        tv_title_center.setVisibility(View.VISIBLE);
        tv_title_center.setText("画板");

        vCleanBtn.setOnClickListener(this);
        vRevokeBtn.setOnClickListener(this);
        vLaserPenBtn.setOnClickListener(this);
        vSelectColorBtn.setOnClickListener(this);

        findViewById(R.id.select_color_black).setOnClickListener(this);
        findViewById(R.id.select_color_red).setOnClickListener(this);
        findViewById(R.id.select_color_yellow).setOnClickListener(this);
        findViewById(R.id.select_color_green).setOnClickListener(this);
        findViewById(R.id.select_color_blue).setOnClickListener(this);
        findViewById(R.id.select_color_purple).setOnClickListener(this);

        //开始
        vPaintPlayer.publish("1233"/*classManager,MLOC.userId*/);

        //收到数据展示
//        vPaintPlayer.setPaintData(tData,tUpid);

//        测试
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                vPaintPlayer.setPaintData("{\"account\":\"1233\",\"data\":\"1:0.85481024,0.7069998,-16777216;2:0.847971,0.70545554,-16777216;2:0.83793586,0.7036299,-16777216\"}");
//                vPaintPlayer.setPaintData("{\"account\":\"1233\",\"data\":\"2:0.81701833,0.70173246,-16777216;2:0.79627955,0.70085067,-16777216;2:0.70594233,0.70085067,-16777216;2:0.6478051,0.6998258,-16777216;2:0.5856927,0.69880104,-16777216\"}");
//                vPaintPlayer.setPaintData("{\"account\":\"1233\",\"data\":\"2:0.5129896,0.69880104,-16777216;2:0.43929738,0.69880104,-16777216;2:0.3714231,0.6995047,-16777216;2:0.23835225,0.70872974,-16777216;2:0.17810574,0.7173355,-16777216\"}");
//                vPaintPlayer.setPaintData("{\"account\":\"1233\",\"data\":\"2:0.10266852,0.7285457,-16777216;3:0.10266852,0.7285457,-16777216\"}");
//            }
//        }, 1000);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.clean_btn:
                vPaintPlayer.clean();
                break;
            case R.id.revoke_btn:
                vPaintPlayer.revoke();
                break;
            case R.id.laser_btn:
                if(isLaser){
                    vLaserPenBtn.setText("画笔");
                    vPaintPlayer.laserPenOff();
                    isLaser = false;
                }else{
                    vLaserPenBtn.setText("激光");
                    vPaintPlayer.laserPenOn();
                    isLaser = true;
                }
                break;
            case R.id.select_color_btn:
                if(vSelectColorView.getVisibility()==View.VISIBLE){
                    vSelectColorView.setVisibility(View.INVISIBLE);
                }else{
                    vSelectColorView.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.select_color_black:
                vPaintPlayer.setSelectColor(0xff000000);
                vSelectColorBtn.setBackgroundColor(Color.parseColor("#ff000000"));
                break;
            case R.id.select_color_red:
                vPaintPlayer.setSelectColor(0xffcf0206);
                vSelectColorBtn.setBackgroundColor(Color.parseColor("#ffcf0206"));
                break;
            case R.id.select_color_yellow:
                vPaintPlayer.setSelectColor(0xfff59b00);
                vSelectColorBtn.setBackgroundColor(Color.parseColor("#fff59b00"));
                break;
            case R.id.select_color_green:
                vPaintPlayer.setSelectColor(0xff3dc25a);
                vSelectColorBtn.setBackgroundColor(Color.parseColor("#ff3dc25a"));
                break;
            case R.id.select_color_blue:
                vPaintPlayer.setSelectColor(0xff0029f7);
                vSelectColorBtn.setBackgroundColor(Color.parseColor("#ff0029f7"));
                break;
            case R.id.select_color_purple:
                vPaintPlayer.setSelectColor(0xff8600a7);
                vSelectColorBtn.setBackgroundColor(Color.parseColor("#ff8600a7"));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vPaintPlayer.pause();
    }
}

