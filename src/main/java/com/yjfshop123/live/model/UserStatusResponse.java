package com.yjfshop123.live.model;

import com.yjfshop123.live.net.response.LiveData;

import java.util.List;

public class UserStatusResponse {
    public int c2c_order_running_nums;
    public int otc_order_running_nums;
    public boolean can_selectt_prize;
    public String hint1;
    public String hint2;
    public String geren_activity;
    public String tuandui_activity;
    public String yingxiong_activity;

    public JumpData jump;

    public List<home_activitiesData> home_activitys;


    public class JumpData{
        public int task_is_h5;
        public String task_h5;
        public int shop_is_h5;
        public String shop_h5;
        public int pintuan_is_h5;
        public String pintuan_h5;
        public int huodong_is_h5;
        public String huodong_h5;
    }

    /**
     * type为app表示app内跳转，为url跳h5
     * tag：每个活动单独一个tag，砸金蛋（egg），拼团（pintuan），任务中心（task）
     */
    public class home_activitiesData{
        public String title;
        public String type;
        public String tag;
        public String url;
        public String btn_name;
        public LiveData live_info;
    }
    /**
     * task_is_h5: 1  //表示是否用原生任务界面    1用网页任务界面  0原生
     * task_h5:"http://"表示是任务的h5界面url
     *
     * shop_is_h5:表示是否用原生自营商城界面    1用网页自营商城界面  0原生
     * shop_h5:表示是自营商城的h5界面url
     *
     * pintuan_is_h5:表示是否用原生拼团界面    1用网页拼团界面  0原生
     * pintuan_h5:表示是拼团的h5界面url
     *
     * home_activities:[
     *     {
     *         "title": "拼团活动",    //活动名称
     *         "type": 1,                 //1：任务   2 拼团  3砸金蛋
     *         "is_h5": 1,              //是否是H5页面
     *         "h5_url": "http://"，   //h5链接
     *         "btn_name": "立即参与",    //活动按钮显示的文字
     *     },
     *     {
     *         "title": "拼团活动",
     *         "type": 1,
     *         "is_h5": 1,
     *         "h5_url": "http://"，
     *         "btn_name": "立即参与"
     *     },
     *     {
     *         "title": "拼团活动",
     *         "type": 1,
     *         "is_h5": 1,
     *         "h5_url": "http://"，
     *         "btn_name": "立即参与"
     *     }
     * ]
     */
}
