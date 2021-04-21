package com.yjfshop123.live;

import android.content.Context;
import android.content.Intent;

import com.yjfshop123.live.ui.activity.GSYVideoActivity;
import com.yjfshop123.live.ui.activity.HomeActivity;
import com.yjfshop123.live.ui.activity.MMDetailsActivityNew;
import com.yjfshop123.live.video.activity.PersonalDetailsActivity;

import java.io.Serializable;

public class ActivityUtils {

    //LIVE_1
    //LIVE_2
    //1V1
    //VIDEO
    //GAME
    public static String TYPE = Const.getMetaDataString("HOME_TYPE");

    //去首页
    public static void startHome(Context context){
//        if (TYPE.equals("LIVE_1")){
//            context.startActivity(new Intent(context, LiveHomeActivity.class));
//        }else if (TYPE.equals("LIVE_2")){
            context.startActivity(new Intent(context, HomeActivity.class));
//        }else if (TYPE.equals("1V1")){
//            context.startActivity(new Intent(context, ChatHomeActivity.class));
//        }else if (TYPE.equals("VIDEO")){
//            context.startActivity(new Intent(context, SmallVideoActivity.class));
//        }else if (TYPE.equals("GAME")){
//            context.startActivity(new Intent(context, GameHomeActivity.class));
//        }
    }

    //通知意图
    public static Intent getHome(Context context){
//        if (TYPE.equals("LIVE_1")){
//            return new Intent(context, LiveHomeActivity.class);
//        }else if (TYPE.equals("LIVE_2")){
            return new Intent(context, HomeActivity.class);
//        }else if (TYPE.equals("1V1")){
//            return new Intent(context, ChatHomeActivity.class);
//        }else if (TYPE.equals("VIDEO")){
//            return new Intent(context, SmallVideoActivity.class);
//        }else if (TYPE.equals("GAME")){
//            return new Intent(context, GameHomeActivity.class);
//        }
//        return null;
    }

    //去用户主页
    public static void startUserHome(Context context, String user_id){
        Intent intent;
        if (TYPE.equals("VIDEO")){
            intent = new Intent(context, PersonalDetailsActivity.class);
        }else {
            intent = new Intent(context, MMDetailsActivityNew.class);
        }
        intent.putExtra("USER_ID", user_id);
        context.startActivity(intent);
    }

    public static boolean IS1V1(){
        if (TYPE.equals("1V1")){
            return true;
        }
        return false;
    }

    public static boolean IS_VIDEO(){
        if (TYPE.equals("VIDEO")){
            return true;
        }
        return false;
    }

    //播放小视频
    //type 1社区小视频  2抖音短视频
    //"app/forum/videoDynamic" 社区短视频
    //"app/shortVideo/getVideoById" 抖音我的短视频
    //"app/shortVideo/getPopularVideoList" 抖音短视频
    // ActivityUtils.startGSYVideo(mContext, 2, String.valueOf(mList.get(position).getDynamic_id()), "app/shortVideo/getPopularVideoList");
   // getActivity().overridePendingTransition(R.anim.slide_right_entry, R.anim.hold);
    public static void startGSYVideo(Context context, int type, String dynamic_id, String url){
        Intent intent = new Intent(context, GSYVideoActivity.class);
        intent.putExtra("DYNAMIC_ID", dynamic_id);
        intent.putExtra("URL", url);
        intent.putExtra("TYPE", type);
        context.startActivity(intent);
    }

    //type 3
    public static void startGSYVideo(Context context, String user_id, int type, int page, Serializable datas, String url, int position){
        Intent intent = new Intent(context, GSYVideoActivity.class);
        intent.putExtra("DATAS", datas);
        intent.putExtra("USER_ID", user_id);
        intent.putExtra("URL", url);
        intent.putExtra("TYPE", type);
        intent.putExtra("PAGE", page);
        intent.putExtra("POSITION", position);
        context.startActivity(intent);
    }
}
