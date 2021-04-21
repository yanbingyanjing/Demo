package com.yjfshop123.live.utils;

import android.text.TextUtils;

import com.yjfshop123.live.App;
import com.yjfshop123.live.net.utils.NLog;

public class UserInfoUtil {

    /**
     * 云通信ID
     */
    private static final String mi_tencentId = "mi_tencentId";

    public static void setMiTencentId(String tencentId) {
        App.configUtil.storeShareDataWithCommit(mi_tencentId, tencentId);
        App.configUtil.commit();
    }

    public static String getMiTencentId() {
        return App.configUtil.getStringShareData(mi_tencentId);
    }


    /**
     * 用户ID
     */
    private static final String mi_platformId = "mi_platformId";

    public static void setMiPlatformId(String platformId) {
        App.configUtil.storeShareDataWithCommit(mi_platformId, platformId);
        App.configUtil.commit();
    }

    public static String getMiPlatformId() {
        return App.configUtil.getStringShareData(mi_platformId);
    }
    /**
     * 用户ID
     */
    private static final String showShareId = "showShareId";

    public static void setShowShareId(String shareId) {
        App.configUtil.storeShareDataWithCommit(showShareId, shareId);
        App.configUtil.commit();
    }

    public static String getShowShareId() {
        return App.configUtil.getStringShareData(showShareId);
    }

    /**
     * 登录后的签名
     */
    private static final String signature_ = "signature";

    public static void setSignature(String signature) {
        App.configUtil.storeShareDataWithCommit(signature_, signature);
        App.configUtil.commit();
    }

    public static String getSignature() {
        return App.configUtil.getStringShareData(signature_);
    }


    /**
     * 登录用户名
     */
    private static final String name_ = "name";

    public static void setName(String name) {
        App.configUtil.storeShareDataWithCommit(name_, name);
        App.configUtil.commit();
    }

    public static String getName() {
        return App.configUtil.getStringShareData(name_);
    }


    /**
     * 登录头像
     */
    private static final String avatar_ = "avatar";

    public static void setAvatar(String avatar) {
        App.configUtil.storeShareDataWithCommit(avatar_, avatar);
        App.configUtil.commit();
    }

    public static String getAvatar() {
        return App.configUtil.getStringShareData(avatar_);
    }


    /**
     * 客服
     * 抢聊开启状态
     */
    private static final String rob_to_chat_ = "rob_to_chat";

    public static void setRobToChat(String rob_to_chat) {
        App.configUtil.storeShareDataWithCommit(rob_to_chat_, rob_to_chat);
        App.configUtil.commit();
    }

    public static String getRobToChat() {
        return App.configUtil.getStringShareData(rob_to_chat_);
    }


    /**
     * 经纬度
     * 和城市
     */
    private static final String latitude_ = "latitude";
    private static final String longitude_ = "longitude";
    private static final String address_ = "address";

    public static void setAddress(String latitude, String longitude, String address) {
        if (!TextUtils.isEmpty(latitude)) {
            App.configUtil.storeShareDataWithCommit(latitude_, latitude);
        }
        if (!TextUtils.isEmpty(longitude)) {
            App.configUtil.storeShareDataWithCommit(longitude_, longitude);
        }
        if (!TextUtils.isEmpty(address)) {
            App.configUtil.storeShareDataWithCommit(address_, address);
        }
        App.configUtil.commit();
    }

    public static String getLatitude() {
        String latitude = App.configUtil.getStringShareData(latitude_);
        if (TextUtils.isEmpty(latitude)) {
            return "39.915";
        } else {
            return latitude;
        }
    }

    public static String getLongitude() {
        String longitude = App.configUtil.getStringShareData(longitude_);
        if (TextUtils.isEmpty(longitude)) {
            return "116.404";
        } else {
            return longitude;
        }
    }

    public static String getAddress() {
        String address = App.configUtil.getStringShareData(address_);
        if (TextUtils.isEmpty(address)) {
            NLog.d("城市",UserInfoUtil.getCity());
            if (!TextUtils.isEmpty(UserInfoUtil.getCity())) {
                String[] city=UserInfoUtil.getCity().split(",");
                if (city.length == 0) {
                    //  NToast.shortToast(this, "城市错误，请重新选择");
                } else if (city.length == 1) {
                    return city[0];
                } else if (city.length == 2) {
                    return city[0];
                } else if (city.length == 3) {
                    return city[1];
                }
            }
            return "北京";
        } else {
            return address;
        }
    }


    /**
     * 消息使用数
     */
    private static final String user_message_num_ = "user_message_num";
    /**
     * 限制消息数
     */
    private static final String limit_message_num_ = "limit_message_num";

    public static void setUserMessageNum(int user_message_num) {
        App.configUtil.storeIntShareData(user_message_num_, user_message_num);
        App.configUtil.commit();
    }

    public static int getUserMessageNum() {
        return App.configUtil.getIntShareData(user_message_num_, 0);
    }

    public static void setLimitMessageNum(int limit_message_num) {
        App.configUtil.storeIntShareData(limit_message_num_, limit_message_num);
        App.configUtil.commit();
    }

    public static int getLimitMessageNum() {
        //默认限制50条
        return App.configUtil.getIntShareData(limit_message_num_, 50);//TODO ==-1等级限制 50否则条数限制
    }


    /**
     * 客服发起
     * VIP提示
     */
    private static final String VIP_MESSAGE = "vip_message";

    public static void setVIPMessage(String vip_message) {
        App.configUtil.storeShareDataWithCommit(VIP_MESSAGE, vip_message);
        App.configUtil.commit();
    }

    public static String getVIPMessage() {
        return App.configUtil.getStringShareData(VIP_MESSAGE);
    }


    /**
     * 客服发起
     * 充值金蛋提示
     */
    private static final String GOLD_MESSAGE = "gold_message";

    public static void setGoldMessage(String gold_message) {
        App.configUtil.storeShareDataWithCommit(GOLD_MESSAGE, gold_message);
        App.configUtil.commit();
    }

    public static String getGoldMessage() {
        return App.configUtil.getStringShareData(GOLD_MESSAGE);
    }

    /**
     * from_uid
     */
    private static final String FROMUIS = "from_uid";

    public static void setFromUid(String from_uid) {
        App.configUtil.storeShareDataWithCommit(FROMUIS, from_uid);
        App.configUtil.commit();
    }

    public static String getFromUid() {
        return App.configUtil.getStringShareData(FROMUIS);
    }


    /**
     * 手机号保存
     */
    private static final String PHONE_NUMBER = "phone_number";

    private static final String countryCode = "countryCode";
    private static final String countryCn = "countryCn";

    public static void setcountryCode(String countryCodeStr) {
        App.configUtil.storeShareDataWithCommit(countryCode, countryCodeStr);
        App.configUtil.commit();
    }

    public static String getcountryCode() {
        return App.configUtil.getStringShareData(countryCode);
    }

    public static void setcountryCn(String countryCnStr) {
        App.configUtil.storeShareDataWithCommit(countryCn, countryCnStr);
        App.configUtil.commit();
    }

    public static String getcountryCn() {
        return App.configUtil.getStringShareData(countryCn);
    }


    public static void setPhoneNumber(String phone_number) {
        App.configUtil.storeShareDataWithCommit(PHONE_NUMBER, phone_number);
        App.configUtil.commit();
    }

    public static String getPhoneNumber() {
        return App.configUtil.getStringShareData(PHONE_NUMBER);
    }

    /**
     * token
     * info_complete
     * age
     */
    private static final String TOKEN = "token_";
    private static final String INFO_COMPLETE = "info_complete";

    public static void setToken_InfoComplete(String token, int info_complete) {
        App.configUtil.storeShareDataWithCommit(TOKEN, token);
        App.configUtil.storeIntShareData(INFO_COMPLETE, info_complete);
        App.configUtil.commit();
    }

    public static String getToken() {
        return App.configUtil.getStringShareData(TOKEN);
    }

    public static int getInfoComplete() {
        return App.configUtil.getIntShareData(INFO_COMPLETE, 0);
    }


    private static final String TIEZHI_PATH = "tiezhi_path";
    private static final String MEIFU = "meifu";
    private static final String MEI_XING = "mei_xing";
    private static final String FILTER_PROGRESS = "filter_progress";
    private static final String FILTER_STR = "filter_str";

    public static void setBeauty(String tiezhi_path,
                                 String meifu,
                                 String mei_xing,
                                 int filter_progress,
                                 String filter_str) {
        App.configUtil.storeShareDataWithCommit(TIEZHI_PATH, tiezhi_path);
        App.configUtil.storeShareDataWithCommit(MEIFU, meifu);
        App.configUtil.storeShareDataWithCommit(MEI_XING, mei_xing);
        App.configUtil.storeIntShareData(FILTER_PROGRESS, filter_progress);
        App.configUtil.storeShareDataWithCommit(FILTER_STR, filter_str);
        App.configUtil.commit();
    }

    public static String getTiezhiPath() {
        return App.configUtil.getStringShareData(TIEZHI_PATH);
    }

    public static String getMeifu() {
        return App.configUtil.getStringShareData(MEIFU);
    }

    public static String getMeiXing() {
        return App.configUtil.getStringShareData(MEI_XING);
    }

    public static int getFilterProgress() {
        return App.configUtil.getIntShareData(FILTER_PROGRESS, 50);
    }

    public static String getFilterStr() {
        return App.configUtil.getStringShareData(FILTER_STR);
    }


    /**
     *
     */
    private static final String ISREAD = "is_read";

    public static void setIsRead(boolean isRead) {
        App.configUtil.storeBooleanShareData(ISREAD, isRead);
        App.configUtil.commit();
    }

    public static boolean getIsRead() {
        return App.configUtil.getBooleanShareData(ISREAD, false);
    }

    /**
     *
     */
    private static final String SEX = "sex_";
    private static final String DAREN_STATUS = "daren_status";
    private static final String IS_VIP = "is_vip";
    private static final String SPE_INTR = "speech_introduction";
    private static final String AUTH_STATUS = "auth_status";
    private static final String AUTH_STATUS_DES = "AUTH_STATUS_DES";
    private static final String CITY = "city_";
    private static final String TAGS = "tags_";
    private static final String AGE = "age_";
    private static final String USER_SIGNATURE = "user_signature";
    private static final String INVITE_CODE = "invite_code";

    public static void setUserInfo(int sex, int daren_status, int is_vip, String speech_introduction,
                                   int auth_status, String city, String tags, int age, String user_signature) {
        App.configUtil.storeIntShareData(SEX, sex);
        App.configUtil.storeIntShareData(DAREN_STATUS, daren_status);
        App.configUtil.storeIntShareData(IS_VIP, is_vip);
        App.configUtil.storeShareDataWithCommit(SPE_INTR, speech_introduction);
        App.configUtil.storeIntShareData(AUTH_STATUS, auth_status);
        App.configUtil.storeShareDataWithCommit(CITY, city);
        App.configUtil.storeShareDataWithCommit(TAGS, tags);
        App.configUtil.storeIntShareData(AGE, age);
        App.configUtil.storeShareDataWithCommit(USER_SIGNATURE, user_signature);
        App.configUtil.commit();
    }

    public static String getInviteCode() {//,
        return App.configUtil.getStringShareData(INVITE_CODE);
    }

    public static void setInviteCode(String invite_code) {//,
        App.configUtil.storeShareDataWithCommit(INVITE_CODE, invite_code);
        App.configUtil.commit();
    }

    public static void setIsVip(int is_vip) {
        App.configUtil.storeIntShareData(IS_VIP, is_vip);
        App.configUtil.commit();
    }

    public static void setAuthStatus(int auth_status) {
        App.configUtil.storeIntShareData(AUTH_STATUS, auth_status);
        App.configUtil.commit();
    }

    public static int getSex() {
        return App.configUtil.getIntShareData(SEX, 1);
    }

    public static int getDarenStatus() {
        return App.configUtil.getIntShareData(DAREN_STATUS, 0);
    }

    public static int getIsVip() {
        return App.configUtil.getIntShareData(IS_VIP, 0);
    }

    public static String getSpeechIntroduction() {
        return App.configUtil.getStringShareData(SPE_INTR);
    }

    public static int getAuthStatus() {
        return App.configUtil.getIntShareData(AUTH_STATUS, 0);
    }

    public static String getAUTH_STATUS_DES() {
        return App.configUtil.getStringShareData(AUTH_STATUS_DES);
    }

    public static void setAUTH_STATUS_DES(String auth_status_des) {//,
        App.configUtil.storeShareDataWithCommit(AUTH_STATUS_DES, auth_status_des);
        App.configUtil.commit();
    }

    public static String getCity() {//,
        return App.configUtil.getStringShareData(CITY);
    }

    public static String getTags() {//,
        return App.configUtil.getStringShareData(TAGS);
    }

    public static int getAge() {
        return App.configUtil.getIntShareData(AGE, 19);
    }

    public static String getUserSignature() {
        return App.configUtil.getStringShareData(USER_SIGNATURE);
    }

    //1V1
    /**
     * beauty
     * white
     * ruddy
     * filter_id
     */
    private static final String Beault = "beauty_";
    private static final String White = "white_";
    private static final String Ruddy = "ruddy_";
    private static final String FilterId = "filter_id_";

    public static void setBeauty_white_ruddy(int beauty, int white, int ruddy, int filter_id) {
        App.configUtil.storeIntShareData(Beault, beauty);
        App.configUtil.storeIntShareData(White, white);
        App.configUtil.storeIntShareData(Ruddy, ruddy);
        App.configUtil.storeIntShareData(FilterId, filter_id);
        App.configUtil.commit();
    }

    public static int getBeault() {
        return App.configUtil.getIntShareData(Beault, 80);
    }

    public static int getWhite() {
        return App.configUtil.getIntShareData(White, 80);
    }

    public static int getRuddy() {
        return App.configUtil.getIntShareData(Ruddy, 80);
    }

    public static int getFilterId() {
        return App.configUtil.getIntShareData(FilterId, 1);
    }
    //1V1

    //    launch
    private static final String LAUNCH_IMG = "launch_img";
    private static final String LAUNCH_LINK = "launch_link";

    public static void setLaunch(String launch_img, String launch_link) {
        App.configUtil.storeShareDataWithCommit(LAUNCH_IMG, launch_img);
        App.configUtil.storeShareDataWithCommit(LAUNCH_LINK, launch_link);
        App.configUtil.commit();
    }

    public static String getLaunchImg() {
        return App.configUtil.getStringShareData(LAUNCH_IMG);
    }

    public static String getLaunchLink() {
        return App.configUtil.getStringShareData(LAUNCH_LINK);
    }
}
