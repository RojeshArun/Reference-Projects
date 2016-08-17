/*
 * Developed by Silver Softwares
 * */
package com.barter.xmpp;

import android.net.Uri;

public class BarterDBInfo {
	public static final String PARAMETER_NOTIFY = "notify";
	public static final String DATABASE_NAME = "Barter.sqlite";
	public static final int DATABASE_VERSION = 1;
	public static final String AUTHORITY = "com.barter.db";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
}
