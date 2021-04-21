package com.yjfshop123.live.live.live.common.widget.ready;


import com.yjfshop123.live.R;
import com.yjfshop123.live.live.live.common.utils.TCConstants;

import java.util.ArrayList;
import java.util.List;


public class LiveRoomTypeBean {

    private int mId;
    private int mName;
    private int mCheckedIcon;
    private int mUnCheckedIcon;
    private boolean mChecked;

    public LiveRoomTypeBean(int id, int name, int checkedIcon, int unCheckedIcon) {
        mId = id;
        mName = name;
        mCheckedIcon = checkedIcon;
        mUnCheckedIcon = unCheckedIcon;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getName() {
        return mName;
    }

    public void setName(int name) {
        mName = name;
    }

    public int getCheckedIcon() {
        return mCheckedIcon;
    }

    public void setCheckedIcon(int checkedIcon) {
        mCheckedIcon = checkedIcon;
    }

    public int getUnCheckedIcon() {
        return mUnCheckedIcon;
    }

    public void setUnCheckedIcon(int unCheckedIcon) {
        mUnCheckedIcon = unCheckedIcon;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public static List<LiveRoomTypeBean> getLiveTypeList() {
        List<LiveRoomTypeBean> list = new ArrayList<>();
        LiveRoomTypeBean bean = new LiveRoomTypeBean(TCConstants.LIVE_TYPE_NORMAL, R.string.room_1, R.mipmap.icon_live_type_normal_1, R.mipmap.icon_live_type_normal_2);
        list.add(bean);
        LiveRoomTypeBean bean1 = new LiveRoomTypeBean(TCConstants.LIVE_TYPE_PWD, R.string.room_2, R.mipmap.icon_live_type_pwd_1, R.mipmap.icon_live_type_pwd_2);
        list.add(bean1);
        LiveRoomTypeBean bean2= new LiveRoomTypeBean(TCConstants.LIVE_TYPE_PAY, R.string.room_3, R.mipmap.icon_live_type_pay_1, R.mipmap.icon_live_type_pay_2);
        list.add(bean2);
        return list;
    }
}

