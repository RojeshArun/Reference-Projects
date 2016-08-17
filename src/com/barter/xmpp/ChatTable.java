package com.barter.xmpp;

import android.net.Uri;

public class ChatTable {

	public static String ID = "";

	// single chat
	public static String FROMUSERNAME = "";
	public static String FROMID = "";
	public static String TOID = "";
	public static String USERID = "";
	public static String FRIENDID = "";
	public static String CHATMESSAGEDATETIME = "";
	public static String FROMUSERIMAGE = "";
	public static String UNREAD_MESSAGECOUNT = "";
	public static String FRIEND_STATUS = "";
	public static String MEDIA_STATUS = "";
	public static String MEDIA_SHORT = "";
	public static String MESSAGE_STATUS = "";
	public static String AVERAGE_RATING = "";
	public static String FRIEND_PROFILEIMG = "";

	// other
	public static String FROMNAME = "";
	public static String MILLIS = "";
	public static String BODY = "";
	public static String MESSAGETYPE = "";
	public static String GROUP_OWNER_ID = "";
	public static String SESSION_STATUS = "";
	public static String GROUP_OWNER_CHAT_ID = "";
	public static String SESSION_TYPE = "";
	public static String SESSION_ID = "";
	public static String GROUP_TITLE;
	public static String CHAT_DATE;
	public static String CHAT_GROUP;
	public static String ISMESSAGESENTBYME;
	public static String IMAGEURL;
	public static String PROFILEIMAGEURL;
	public static String BASE64STRING;
	public static String ISFEEDMESSAGE;
	public static String FROMUSERID;
	public static String ORGINALMESSAGE;
	public static String FROMPROFILEIMAGEURL;
	public static String PACKET_ID;
	public static String CREATETABLESQL;
	public static String TABLENAME = "ChatTable";

	public static final Uri CHAT_TABLE_URI = Uri.parse("content://"
			+ BarterDBInfo.AUTHORITY + "/" + TABLENAME + "?"
			+ BarterDBInfo.PARAMETER_NOTIFY + "=true");

	static Uri getContentUri(long id, boolean notify) {
		return Uri.parse("content://" + BarterDBInfo.AUTHORITY + "/"
				+ TABLENAME + "/" + id + "?" + BarterDBInfo.PARAMETER_NOTIFY
				+ "=" + notify);
	}

	static {

		FROMID = "from_id";
		TOID = "to_id";
		ORGINALMESSAGE = "originalmessage";
		USERID = "user_id";
		FRIENDID = "friend_id";
		CHATMESSAGEDATETIME = "chatMessageDatetime";
		FROMUSERNAME = "from_username";
		FROMUSERIMAGE = "from_userimage";
		UNREAD_MESSAGECOUNT = "unreadmessage_count";
		FRIEND_STATUS = "friend_status";
		MEDIA_STATUS = "media_status";
		MEDIA_SHORT = "media_short";
		MESSAGE_STATUS = "message_status";
		AVERAGE_RATING = "average_rating";
		FRIEND_PROFILEIMG = "friend_profile_img";

		ID = "_id";

		// CREATETABLESQL = "create table if not exists " + TABLENAME
		// + " (_id INTEGER PRIMARY KEY NOT NULL,"
		// + "from_id TEXT, to_id TEXT," + "originalmessage TEXT,"
		// + "user_id TEXT," + "friend_id TEXT,"
		// + "chatMessageDatetime TEXT," + "from_username TEXT,"
		// + "from_userimage TEXT," + "unreadmessage_count INTEGER,"
		// + "friend_status TEXT," + "media_status TEXT,"
		// + "media_short TEXT," + "message_status TEXT,"
		// + "average_rating TEXT)";

		CREATETABLESQL = "create table if not exists " + TABLENAME
				+ " (_id INTEGER PRIMARY KEY NOT NULL,"
				+ "from_id TEXT, to_id TEXT," + "originalmessage TEXT,"
				+ "user_id TEXT," + "friend_id TEXT,"
				+ "chatMessageDatetime TEXT," + "from_username TEXT,"
				+ "from_userimage TEXT," + "unreadmessage_count INTEGER,"
				+ "friend_status TEXT," + "media_status TEXT,"
				+ "media_short TEXT," + "message_status TEXT,"
				+ "average_rating TEXT," + "friend_profile_img TEXT)";

	}

}
