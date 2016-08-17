package com.hb.models;

public class Chat {

	// test

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public String getNewMessage() {
		return newMessage;
	}

	public void setNewMessage(String newMessage) {
		this.newMessage = newMessage;
	}

	public String getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}

	public String getNewMessageCount() {
		return newMessageCount;
	}

	public void setNewMessageCount(String newMessageCount) {
		this.newMessageCount = newMessageCount;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public void toggle() {
		select = !select;
	}

	public String getChatType() {
		return chatType;
	}

	public void setChatType(String chatType) {
		this.chatType = chatType;
	}

	public String getInGroup() {
		return inGroup;
	}

	public void setInGroup(String inGroup) {
		this.inGroup = inGroup;
	}

	private String friendName;
	private String newMessage;
	private String messageTime;
	private String newMessageCount;
	private String selected;
	private boolean select;
	private String chatType;
	private String inGroup;
	private String friendUserName;
	private String status;
	private String friendProfileImage;
	private String friendId;
	private String userId;
	private String fromId;

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFriendProfileImage() {
		return friendProfileImage;
	}

	public void setFriendProfileImage(String friendProfileImage) {
		this.friendProfileImage = friendProfileImage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFriendUserName() {
		return friendUserName;
	}

	public void setFriendUserName(String friendUserName) {
		this.friendUserName = friendUserName;
	}

}
