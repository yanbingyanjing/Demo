package com.yjfshop123.live.live.live.list;


import com.yjfshop123.live.live.commondef.AttributeInfo;

/**
 * 消息体类
 */
public class TCChatEntity {

	private String content;//内容
	private String user_id = "system";//用户ID

	private int type;//消息类型
	private String identityType;//身份类型 0普通 1主播
	private String isGuard;// 是否守护 (1:是 0:否)
	private String isVip;//是否vip (1:是 0:否)
	private String userName;//名称
	private String user_level;//用户财富等级

	public TCChatEntity(int type_){
		setType(type_);
	}

	public TCChatEntity(String nickname, AttributeInfo attributeInfo, int type_, String user_id){
		setIdentityType(attributeInfo.getIdentityType());
		setIsGuard(attributeInfo.getIsGuard());
		setIsVip(attributeInfo.getIsVip());
		setUser_level(attributeInfo.getUser_level());
		setType(type_);
		setUserName(nickname);
		setUser_id(user_id);
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getIdentityType() {
		return identityType;
	}

	public void setIdentityType(String identityType) {
		this.identityType = identityType;
	}

	public String getIsVip() {
		return isVip;
	}

	public void setIsVip(String isVip) {
		this.isVip = isVip;
	}

	public String getIsGuard() {
		return isGuard;
	}

	public void setIsGuard(String isGuard) {
		this.isGuard = isGuard;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUser_level() {
		return user_level;
	}

	public void setUser_level(String user_level) {
		this.user_level = user_level;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
