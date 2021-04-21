package com.yjfshop123.live.live.commondef;

public class AnchorInfo {

    public String   userID;
    public String   userName;
    public String   userAvatar;
    public String   accelerateURL;

    public AnchorInfo() {

    }

    public AnchorInfo(String userID, String userName, String userAvatar) {
        this.userID = userID;
        this.userName = userName;
        this.userAvatar = userAvatar;
    }

    public AnchorInfo(String userID, String userName, String userAvatar, String accelerateURL) {
        this.userID = userID;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.accelerateURL = accelerateURL;
    }

}
