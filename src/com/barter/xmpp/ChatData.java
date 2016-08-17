package com.barter.xmpp;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.text.TextUtils;

@SuppressLint("SimpleDateFormat")
public class ChatData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	public static final String CHAT_MESSAGE_DATE_FORMAT = "d MMMM, yyyy hh:mm aaa"; // "d MMM, yyyy hh:mm aaa";

	public static final String CHAT_MESSAGE_DATE_FORMAT_WITH_MONTH_ONLY = "d MMM, yyyy";

	public static final String CHAT_MESSAGE_DATE_FORMAT_WITH_TIME_ONLY = "hh:mm aaa";

	/*
	 * String fromId, String groupIdWithExtension, String fromName, String date,
	 * long millis, String body, int messageType
	 */

	private String countryCode = "";
	private String packetId = "";
	private String fromId = "";
	private String fromUserId = "";
	private String fromName = "";
	private String fromUserName = "";
	private long millis = 0L;
	private String body = "";
	private String orginalMessage = "";
	private int messageType = 0;

	private String group_title = "";
	private String chatDate = "";
	private String chat_group = "";

	private boolean isMessageSentByMe = false;

	private String imageUrl = "";
	private String profileImageUrl = "";

	private String fromProfileImageUrl = "";
	private String avgRating="";

	private String base64String = "";
	private boolean isFeedMessage = false;

	private String groupOwnerId = "";
	private String sessionStatus = "";
	private String groupOwnerChatId = "";

	private String session_type = "";
	private String session_id = "";

	private String chatDateWithMonthOnly = "", chatDateWithTimeOnly = "";
	private String toId = "";
	private String userId = "";
	private String friendId = "";
	private String chatMessageDatetime = "";
	private String chatFriendName = "";
	private Integer unreadMessageCount;
	private String friendStatus;
	private String mediaStatus;
	private String mediaShortName;
	private String messageStatus;
	private String messageId;
	


	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(String messageStatus) {
		this.messageStatus = messageStatus;
	}

	public String getMediaShortName() {
		return mediaShortName;
	}

	public void setMediaShortName(String mediaShortName) {
		this.mediaShortName = mediaShortName;
	}

	public String getMediaStatus() {
		return mediaStatus;
	}

	public void setMediaStatus(String mediaStatus) {
		this.mediaStatus = mediaStatus;
	}

	public String getFriendStatus() {
		return friendStatus;
	}

	public void setFriendStatus(String friendStatus) {
		this.friendStatus = friendStatus;
	}

	public Integer getUnreadMessageCount() {
		return unreadMessageCount;
	}

	public void setUnreadMessageCount(Integer unreadMessageCount) {
		this.unreadMessageCount = unreadMessageCount;
	}

	public String getChatFriendName() {
		return chatFriendName;
	}

	public void setChatFriendName(String chatFriendName) {
		this.chatFriendName = chatFriendName;
	}

	public String getChatMessageDatetime() {
		return chatMessageDatetime;
	}

	public void setChatMessageDatetime(String chatMessageDatetime) {
		this.chatMessageDatetime = chatMessageDatetime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public String getToId() {
		return toId;
	}

	public void setToId(String toId) {
		this.toId = toId;
	}

	public String getChatDate() {
		return chatDate;
	}

	/**
	 * 19 Jan,2013 09:20 PM --> d MMM, yyyy hh:mm aaa
	 * 
	 * @param chatDate
	 */
	public void setChatDate(String chatDate) {

		if (!TextUtils.isEmpty(chatDate))
			this.chatDate = chatDate;
	}

	public String getGroup_title() {
		return group_title;
	}

	public void setGroup_title(String group_title) {
		if (!TextUtils.isEmpty(group_title))
			this.group_title = group_title;
	}

	public String getSession_type() {
		return session_type;
	}

	public void setSession_type(String session_type) {
		if (!TextUtils.isEmpty(session_type))
			this.session_type = session_type;
	}

	public String getChat_group() {
		return chat_group;
	}

	public void setChat_group(String chat_group) {
		if (!TextUtils.isEmpty(chat_group))
			this.chat_group = chat_group;
	}

	public boolean isMessageSentByMe() {
		return isMessageSentByMe;
	}

	public void setMessageSentByMe(boolean isMessageSentByMe) {
		this.isMessageSentByMe = isMessageSentByMe;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		if (!TextUtils.isEmpty(imageUrl))
			this.imageUrl = imageUrl;
	}

	public String getBase64String() {
		return base64String;
	}

	public void setBase64String(String base64String) {
		if (!TextUtils.isEmpty(base64String))
			this.base64String = base64String;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		if (!TextUtils.isEmpty(profileImageUrl))
			this.profileImageUrl = profileImageUrl;
	}

	public boolean isFeedMessage() {
		return isFeedMessage;
	}

	public void setFeedMessage(boolean isFeedMessage) {
		this.isFeedMessage = isFeedMessage;
	}

	public String getGroupOwnerId() {
		return groupOwnerId;
	}

	public void setGroupOwnerId(String groupOwnerId) {
		if (!TextUtils.isEmpty(groupOwnerId))
			this.groupOwnerId = groupOwnerId;
	}

	public String getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(String sessionStatus) {
		if (!TextUtils.isEmpty(sessionStatus)
				&& !sessionStatus.equalsIgnoreCase("null"))
			this.sessionStatus = sessionStatus;
	}

	public String getGroupOwnerChatId() {
		return groupOwnerChatId;
	}

	public void setGroupOwnerChatId(String groupOwnerChatId) {
		if (!TextUtils.isEmpty(groupOwnerChatId))
			this.groupOwnerChatId = groupOwnerChatId;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		if (!TextUtils.isEmpty(session_id))
			this.session_id = session_id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the fromId
	 */
	public String getFromId() {
		return fromId;
	}

	/**
	 * @param fromId
	 *            the fromId to set
	 */
	public void setFromId(String fromId) {
		if (!TextUtils.isEmpty(fromId))
			this.fromId = fromId;
	}

	/**
	 * @return the fromName
	 */
	public String getFromName() {
		return fromName;
	}

	/**
	 * @param fromName
	 *            the fromName to set
	 */
	public void setFromName(String fromName) {
		if (!TextUtils.isEmpty(fromName))
			this.fromName = fromName;
	}

	/**
	 * @return the millis
	 */
	public long getMillis() {
		return millis;
	}

	/**
	 * @param millis
	 *            the millis to set
	 */
	public void setMillis(long millis) {
		this.millis = millis;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		if (!TextUtils.isEmpty(body))
			this.body = body;
	}

	/**
	 * @return the messageType
	 */
	public int getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType
	 *            the messageType to set
	 */
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the fromUserId
	 */
	public String getFromUserId() {
		return fromUserId;
	}

	/**
	 * @param fromUserId
	 *            the fromUserId to set
	 */
	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	/**
	 * @return the fromUserName
	 */
	public String getFromUserName() {
		return fromUserName;
	}

	/**
	 * @param fromUserName
	 *            the fromUserName to set
	 */
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	/**
	 * @return the orginalMessage
	 */
	public String getOrginalMessage() {
		return orginalMessage;
	}

	/**
	 * @param orginalMessage
	 *            the orginalMessage to set
	 */
	public void setOrginalMessage(String orginalMessage) {
		this.orginalMessage = orginalMessage;
	}

	/**
	 * @return the receiverImageUrl
	 */
	public String getReceiverImageUrl() {
		return getFromProfileImageUrl();
	}

	/**
	 * @param receiverImageUrl
	 *            the receiverImageUrl to set
	 */
	public void setReceiverImageUrl(String receiverImageUrl) {
		this.setFromProfileImageUrl(receiverImageUrl);
	}

	/**
	 * @return the fromProfileImageUrl
	 */
	public String getFromProfileImageUrl() {
		return fromProfileImageUrl;
	}

	/**
	 * @param fromProfileImageUrl
	 *            the fromProfileImageUrl to set
	 */
	public void setFromProfileImageUrl(String fromProfileImageUrl) {
		this.fromProfileImageUrl = fromProfileImageUrl;
	}

	/**
	 * @return the packetId
	 */
	public String getPacketId() {
		return packetId;
	}

	/**
	 * @param packetId
	 *            the packetId to set
	 */
	public void setPacketId(String packetId) {
		this.packetId = packetId;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode
	 *            the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return the chatDateWithTimeOnly
	 */
	@SuppressLint("SimpleDateFormat")
	public String getChatDateWithTimeOnly(String chatDate) {

		Date date = null;
		try {
			date = new SimpleDateFormat(CHAT_MESSAGE_DATE_FORMAT)
					.parse(chatDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat fo = new SimpleDateFormat(
				CHAT_MESSAGE_DATE_FORMAT_WITH_TIME_ONLY);

		chatDateWithTimeOnly = fo.format(date);

		return chatDateWithTimeOnly;
	}

	/**
	 * @return the chatDateWithMonthOnly
	 */
	public String getChatDateWithMonthOnly(String chatDate) {

		Date date = null;
		try {
			date = new SimpleDateFormat(
					CHAT_MESSAGE_DATE_FORMAT_WITH_MONTH_ONLY).parse(chatDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat fo = new SimpleDateFormat(
				CHAT_MESSAGE_DATE_FORMAT_WITH_MONTH_ONLY);

		chatDateWithMonthOnly = fo.format(date);

		return chatDateWithMonthOnly;
	}

	public String getAvgRating() {
		return avgRating;
	}

	public void setAvgRating(String avgRating) {
		this.avgRating = avgRating;
	}

}