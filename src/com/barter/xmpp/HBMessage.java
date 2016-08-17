package com.barter.xmpp;

public class HBMessage {

	public static final int MESSAGE_TYPE_STRING = 0;
	public static final int MESSAGE_TYPE_IMAGE = 1;
	public static final int MESSAGE_TYPE_VIDEO = 2;

	private String message;
	private String fromJID;
	private String fromName;
	private String time;
	private int messageType;
	private String toJID;
	private String messageTime;
	private String friend_profileimage;
	private String showTimeLayout;
	private String mediaStatus;
	private String avgRating;
	

	public String getMediaStatus() {
		return mediaStatus;
	}

	public void setMediaStatus(String mediaStatus) {
		this.mediaStatus = mediaStatus;
	}

	public String getShowTimeLayout() {
		return showTimeLayout;
	}

	public void setShowTimeLayout(String showTimeLayout) {
		this.showTimeLayout = showTimeLayout;
	}

	public String getFriend_profileimage() {
		return friend_profileimage;
	}

	public void setFriend_profileimage(String friend_profileimage) {
		this.friend_profileimage = friend_profileimage;
	}

	public String getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(String messageTime) {
		this.messageTime = messageTime;
	}

	public String getToJID() {
		return toJID;
	}

	public void setToJID(String toJID) {
		this.toJID = toJID;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFromJID() {
		return fromJID;
	}

	public void setFromJID(String fromJID) {
		this.fromJID = fromJID;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public static int getMessageTypeString() {
		return MESSAGE_TYPE_STRING;
	}

	public static int getMessageTypeImage() {
		return MESSAGE_TYPE_IMAGE;
	}

	public static int getMessageTypeVideo() {
		return MESSAGE_TYPE_VIDEO;
	}

	public String getAvgRating() {
		return avgRating;
	}

	public void setAvgRating(String avgRating) {
		this.avgRating = avgRating;
	}

}
