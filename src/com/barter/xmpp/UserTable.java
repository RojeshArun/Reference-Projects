package com.barter.xmpp;

import android.net.Uri;

public class UserTable {

	public static String FRIEND_JABBERID = "";
	public static String FRIEND_NAME = "";

	public static String CREATETABLESQL;
	public static String TABLENAME = "FriendsTable";

	public static final Uri CHAT_TABLE_URI = Uri.parse("content://"
			+ BarterDBInfo.AUTHORITY + "/" + TABLENAME + "?"
			+ BarterDBInfo.PARAMETER_NOTIFY + "=true");

	static Uri getContentUri(long id, boolean notify) {
		return Uri.parse("content://" + BarterDBInfo.AUTHORITY + "/"
				+ TABLENAME + "/" + id + "?" + BarterDBInfo.PARAMETER_NOTIFY
				+ "=" + notify);
	}

	static {

		FRIEND_JABBERID = "friend_id";
		FRIEND_NAME = "friend_name";

		CREATETABLESQL = "create table if not exists "
				+ TABLENAME
				+ " (_id INTEGER PRIMARY KEY NOT NULL,friend_id TEXT, friend_name TEXT)";
	}

}
