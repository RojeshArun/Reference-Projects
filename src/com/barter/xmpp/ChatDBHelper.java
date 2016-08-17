package com.barter.xmpp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

public class ChatDBHelper {

	public static final String IMAGE_PREFIX = "IMAGE###";
	DateFormat date;
	HBXMPP mHbxmpp;

	public ChatDBHelper() {

	}

	public void AddChatMessage(ContentResolver resolver, ChatData info) {
		ContentValues values = new ContentValues();

		// this.mHbxmpp = mHbxmpp;

		values.put(ChatTable.FROMID, info.getFromId());
		values.put(ChatTable.TOID, info.getToId());
		values.put(ChatTable.ORGINALMESSAGE, info.getOrginalMessage());
		values.put(ChatTable.FRIENDID, info.getFriendId());
		values.put(ChatTable.USERID, info.getUserId());
		values.put(ChatTable.CHATMESSAGEDATETIME, info.getChatMessageDatetime());
		values.put(ChatTable.FROMUSERNAME, info.getChatFriendName());
		values.put(ChatTable.UNREAD_MESSAGECOUNT, info.getUnreadMessageCount());
		values.put(ChatTable.FRIEND_STATUS, info.getFriendStatus());
		values.put(ChatTable.MEDIA_STATUS, info.getMediaStatus());
		values.put(ChatTable.MEDIA_SHORT, info.getMediaShortName());
		values.put(ChatTable.MESSAGE_STATUS, info.getMessageStatus());
		values.put(ChatTable.AVERAGE_RATING, info.getAvgRating());
		if (TextUtils.isEmpty(info.getFromProfileImageUrl())) {
			// values.put(ChatTable.FRIEND_PROFILEIMG,
			// getFriendImage(mContext, info.getFriendId()));

			// Bitmap mBitmap = mHbxmpp.getFriends(info.getFromName());

			// List<HBUser> mFrinedsList =
			// mHbxmpp.getFriends(info.getFromName());

			// for (int i = 0; i < mFrinedsList.size(); i++) {
			// if (mFrinedsList.get(i).getJid()
			// .equalsIgnoreCase(info.getFromId())) {
			// //
			// Bitmap mBitmap = mFrinedsList.get(i).getAvatar();
			// Toast.makeText(mContext, "SUCCESS", Toast.LENGTH_LONG)
			// .show();
			// }
			// }
			// mHbxmpp.getFriendImage(info.getFriendId());

		} else {
			values.put(ChatTable.FRIEND_PROFILEIMG,
					info.getFromProfileImageUrl());
		}
		resolver.insert(ChatTable.CHAT_TABLE_URI, values);
	}

	public void addUsertoDB(ContentResolver resolver, String friendId,
			String friendName) {
		// TODO Auto-generated method stub
		ContentValues values = new ContentValues();

		values.put(UserTable.FRIEND_JABBERID, friendId);
		values.put(UserTable.FRIEND_NAME, friendName);
		resolver.insert(UserTable.CHAT_TABLE_URI, values);
	}

	public String getFriendName(Context context, String fromuserJabberId) {
		// TODO Auto-generated method stub

		String friendName = new String();

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(context);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = UserTable.FRIEND_JABBERID + " =?";
		String args[] = new String[] { fromuserJabberId };

		Cursor cursor = sqLiteDatabase.query(UserTable.TABLENAME, null, where,
				args, null, null, null);
		if (cursor.moveToFirst()) {
			do {

				try {
					friendName = cursor.getString(2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (cursor.moveToNext());
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}

		return friendName;
	}

	public String getFriendImage(Context context, String fromuserJabberId) {
		// TODO Auto-generated method stub

		String friendImage = new String();

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(context);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = UserTable.FRIEND_JABBERID + " =?";
		String args[] = new String[] { fromuserJabberId };

		Cursor cursor = sqLiteDatabase.query(ChatTable.TABLENAME, null, where,
				args, null, null, null);

		if (cursor.moveToFirst()) {
			do {

				try {
					friendImage = cursor.getString(15);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (cursor.moveToNext());
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}

		return friendImage;
	}

	public List<ChatData> loadAllRecentChats(FragmentActivity activity,
			String userid) {

		ArrayList<ChatData> chatHistory = new ArrayList<ChatData>();

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(activity);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = ChatTable.USERID + " =?";
		String args[] = new String[] { userid };

		Cursor cursor = sqLiteDatabase.query(ChatTable.TABLENAME, null, where,
				args, ChatTable.FRIENDID, null, null);

		if (cursor.moveToFirst()) {
			do {

				ChatData mChatData = new ChatData();
				try {
					mChatData.setFromId(cursor.getString(1));
					mChatData.setToId(cursor.getString(2));
					mChatData.setOrginalMessage(cursor.getString(3));
					mChatData.setUserId(cursor.getString(4));
					mChatData.setFriendId(cursor.getString(5));
					mChatData.setChatMessageDatetime(cursor.getString(6));
					mChatData.setChatFriendName(URLDecoder.decode(
							cursor.getString(7), "utf-8"));
					mChatData.setImageUrl(cursor.getString(8));
					mChatData.setUnreadMessageCount(Integer.valueOf(cursor
							.getString(9)));
					mChatData.setFriendStatus(cursor.getString(10));
					mChatData.setFromProfileImageUrl(cursor.getString(15));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				mChatData.setMediaStatus(cursor.getString(10));
				chatHistory.add(mChatData);

			} while (cursor.moveToNext());
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}

		return chatHistory;

	}

	public List<ChatData> loadAllRecentChatsUnReadCount(
			FragmentActivity activity, String userid) {

		ArrayList<ChatData> chatHistory = new ArrayList<ChatData>();

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(activity);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = ChatTable.USERID + " =?";
		String args[] = new String[] { userid };

		Cursor cursor = sqLiteDatabase.query(ChatTable.TABLENAME, null, where,
				args, ChatTable.FRIENDID, null, null);

		if (cursor.moveToFirst()) {
			do {

				ChatData mChatData = new ChatData();
				mChatData.setUnreadMessageCount(Integer.valueOf(cursor
						.getString(9)));
				chatHistory.add(mChatData);

			} while (cursor.moveToNext());
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}

		return chatHistory;

	}

	public List<ChatData> loadConversationHistory(FragmentActivity mActivity,
			String friendjid, String userid) {

		ArrayList<ChatData> chatHistory = new ArrayList<ChatData>();

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(mActivity);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = ChatTable.FRIENDID + " =? and " + ChatTable.USERID
				+ " =?";
		String args[] = new String[] { friendjid, userid };

		Cursor cursor = sqLiteDatabase.query(ChatTable.TABLENAME, null, where,
				args, null, null, null);

		if (cursor.moveToFirst()) {
			do {

				ChatData mChatData = new ChatData();
				mChatData.setFromId(cursor.getString(1));
				mChatData.setToId(cursor.getString(2));
				mChatData.setOrginalMessage(cursor.getString(3));
				mChatData.setChatMessageDatetime(cursor.getString(6));
				mChatData.setChatFriendName(cursor.getString(7));
				mChatData.setFromProfileImageUrl(cursor.getString(8));
				mChatData.setMediaStatus(cursor.getString(10));
				mChatData.setAvgRating(cursor.getString(14));
				mChatData.setFromProfileImageUrl(cursor.getString(15));
				chatHistory.add(mChatData);

			} while (cursor.moveToNext());
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}

		return chatHistory;
	}

	public List<ChatData> getLastMessage(FragmentActivity mActivity,
			String friendjid, String userid) {

		ArrayList<ChatData> chatHistory = new ArrayList<ChatData>();

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(mActivity);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = ChatTable.FRIENDID + " =? and " + ChatTable.USERID
				+ " =?";
		String args[] = new String[] { friendjid, userid };

		Cursor cursor = sqLiteDatabase.query(ChatTable.TABLENAME, null, where,
				args, null, null, null);

		if (cursor.moveToFirst()) {
			do {

				ChatData mChatData = new ChatData();
				mChatData.setFromId(cursor.getString(1));
				mChatData.setToId(cursor.getString(2));
				mChatData.setOrginalMessage(cursor.getString(3));
				mChatData.setChatMessageDatetime(cursor.getString(6));
				mChatData.setChatFriendName(cursor.getString(7));
				mChatData.setMediaStatus(cursor.getString(10));
				chatHistory.add(mChatData);

			} while (cursor.moveToNext());
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}

		return chatHistory;
	}

	public String getLatestMessage(FragmentActivity mActivity,
			String friendjid, String userid) {

		String recentMessage = new String();

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(mActivity);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = ChatTable.FRIENDID + " =? and " + ChatTable.USERID
				+ " =?";
		String args[] = new String[] { friendjid, userid };

		Cursor cursor = sqLiteDatabase.query(ChatTable.TABLENAME, null, where,
				args, null, null, null);

		if (cursor.moveToFirst()) {
			do {

				recentMessage = cursor.getString(3);

			} while (cursor.moveToNext());
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}

		return recentMessage;
	}

	public Integer getUnreadMessageCount(Context context, String friendjid,
			String userid) {

		Integer unreadCount = 0;

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(context);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = ChatTable.FRIENDID + " =? and " + ChatTable.USERID
				+ " =?";
		String args[] = new String[] { friendjid, userid };

		Cursor cursor = sqLiteDatabase.query(ChatTable.TABLENAME, null, where,
				args, null, null, null);

		if (cursor.moveToFirst()) {
			do {

				unreadCount = Integer.valueOf(cursor.getString(9));

			} while (cursor.moveToNext());
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}

		return unreadCount;
	}

	public void setAsReadMessages(FragmentActivity activity, String friendId,
			String userId) {

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(activity);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		ContentValues values = new ContentValues();

		String where = ChatTable.FRIENDID + " =? and " + ChatTable.USERID
				+ " =?";
		String args[] = new String[] { friendId, userId };
		values.put(ChatTable.UNREAD_MESSAGECOUNT, 0);

		sqLiteDatabase.update(ChatTable.TABLENAME, values, where, args);
	}

	public List<ChatData> loadAllRecentChats(Context applicationContext,
			String userid) {

		ArrayList<ChatData> chatHistory = new ArrayList<ChatData>();

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(applicationContext);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = ChatTable.USERID + " =?";
		String args[] = new String[] { userid };

		Cursor cursor = sqLiteDatabase.query(ChatTable.TABLENAME, null, where,
				args, ChatTable.FRIENDID, null, null);

		if (cursor.moveToFirst()) {
			do {

				ChatData mChatData = new ChatData();
				mChatData.setFromId(cursor.getString(1));
				mChatData.setToId(cursor.getString(2));
				mChatData.setOrginalMessage(cursor.getString(3));
				mChatData.setUserId(cursor.getString(4));
				mChatData.setFriendId(cursor.getString(5));
				mChatData.setChatMessageDatetime(cursor.getString(6));
				mChatData.setChatFriendName(cursor.getString(7));
				mChatData.setFromProfileImageUrl(cursor.getString(8));
				mChatData.setUnreadMessageCount(Integer.valueOf(cursor
						.getString(9)));
				mChatData.setFriendStatus(cursor.getString(9));
				mChatData.setMediaStatus(cursor.getString(10));
				chatHistory.add(mChatData);

			} while (cursor.moveToNext());
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}

		return chatHistory;

	}

	public void upDateStatus(Context context, String friendId, String userId,
			boolean isAvailable) {

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(context);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		ContentValues values = new ContentValues();

		String where = ChatTable.FRIENDID + " =? and " + ChatTable.USERID
				+ " =?";
		String args[] = new String[] { friendId, userId };
		if (isAvailable)
			values.put(ChatTable.FRIEND_STATUS, "online");
		else
			values.put(ChatTable.FRIEND_STATUS, "offline");

		sqLiteDatabase.update(ChatTable.TABLENAME, values, where, args);
	}

	public void updateasdownloaded(ContentResolver resolver,
			FragmentActivity fragmentActivity, String frndId, String userid,
			String fileName, String mediastatus) {
		// TODO Auto-generated method stub
		// ScliDatabaseHelper mScliDatabaseHelper = ScliDatabaseHelper
		// .getInstance(fragmentActivity);
		// SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
		// .getMyWritableDatabase();

		ContentValues values = new ContentValues();

		String where = ChatTable.FRIENDID + " =? and " + ChatTable.MEDIA_SHORT
				+ " =?";
		String args[] = new String[] { frndId, fileName };
		values.put(ChatTable.MEDIA_STATUS, mediastatus);
		// sqLiteDatabase.update(ChatTable.TABLENAME, values, where, args);
		resolver.update(ChatTable.CHAT_TABLE_URI, values, where, args);

	}

	// public void deleteUserFromLocalDB(FragmentActivity fragmentActivity){
	// BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
	// .getInstance(fragmentActivity);
	//
	// SQLiteDatabase
	// sqLiteDatabase=mScliDatabaseHelper.getMyWritableDatabase();
	//
	// String where = ChatTable.FRIENDID + " =?";
	// String args[] = new String[] { string };
	//
	// sqLiteDatabase.delete(ChatTable.TABLENAME, whereClause, whereArgs)
	//
	//
	// }

	public void clearthechatHistory(FragmentActivity fragmentActivity) {
		// TODO Auto-generated method stub

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(fragmentActivity);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();
		sqLiteDatabase.delete(ChatTable.TABLENAME, null, null);
	}

	public void cleartheuserChatHistory(FragmentActivity activity, String string) {
		// TODO Auto-generated method stub

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(activity);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = ChatTable.FRIENDID + " =?";
		String args[] = new String[] { string };

		sqLiteDatabase.delete(ChatTable.TABLENAME, where, args);
	}

	public List<String> getAllFriends(FragmentActivity activity) {
		// TODO Auto-generated method stub

		ArrayList<String> mArrayList = new ArrayList<String>();

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(activity);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String selectQuery = "SELECT * FROM " + ChatTable.TABLENAME
				+ " group by " + ChatTable.FRIENDID;

		Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String resultArrDetails = new String();
				resultArrDetails = cursor.getString(5);
				mArrayList.add(resultArrDetails);
			} while (cursor.moveToNext());
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}
		return mArrayList;
	}

	public void setAsReadMessages(ConnectionService connectionService,
			String friendId, String userId) {
		// TODO Auto-generated method stub

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(connectionService);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		ContentValues values = new ContentValues();

		String where = ChatTable.FRIENDID + " =? and " + ChatTable.USERID
				+ " =?";
		String args[] = new String[] { friendId, userId };
		values.put(ChatTable.UNREAD_MESSAGECOUNT, 0);

		sqLiteDatabase.update(ChatTable.TABLENAME, values, where, args);
	}

	public void cleartheuserChatHistory(ConnectionService connectionService,
			String string) {
		// TODO Auto-generated method stub

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(connectionService);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = ChatTable.FRIENDID + " =?";
		String args[] = new String[] { string };

		sqLiteDatabase.delete(ChatTable.TABLENAME, where, args);
	}

	public List<ChatData> loadOfflineMessages(
			FragmentActivity fragmentActivity, String string) {
		// TODO Auto-generated method stub
		ArrayList<ChatData> chatHistory = new ArrayList<ChatData>();

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(fragmentActivity);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = ChatTable.FROMID + " =? and " + ChatTable.MESSAGE_STATUS
				+ " =?";
		String args[] = new String[] { string, "offline" };

		Cursor cursor = sqLiteDatabase.query(ChatTable.TABLENAME, null, where,
				args, null, null, null);

		if (cursor.moveToFirst()) {
			do {

				ChatData mChatData = new ChatData();
				mChatData.setMessageId(cursor.getString(0));
				mChatData.setFromId(cursor.getString(1));
				mChatData.setToId(cursor.getString(2));
				mChatData.setOrginalMessage(cursor.getString(3));
				mChatData.setChatMessageDatetime(cursor.getString(6));
				mChatData.setChatFriendName(cursor.getString(7));
				mChatData.setMediaStatus(cursor.getString(10));
				chatHistory.add(mChatData);

			} while (cursor.moveToNext());
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}

		return chatHistory;
	}

	public void setasonlineMessages(FragmentActivity fragmentActivity,
			String string) {
		// TODO Auto-generated method stub

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(fragmentActivity);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		ContentValues values = new ContentValues();

		String where = ChatTable.FROMID + " =?";
		String args[] = new String[] { string };
		values.put(ChatTable.MESSAGE_STATUS, "online");

		sqLiteDatabase.update(ChatTable.TABLENAME, values, where, args);
	}

	// public void setasonlineMessage(MainActivity mainActivity, String string,
	// String messageId) {
	// // TODO Auto-generated method stub
	//
	// TeetorDatabaseHelper mScliDatabaseHelper = TeetorDatabaseHelper
	// .getInstance(mainActivity);
	// SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
	// .getMyWritableDatabase();
	//
	// ContentValues values = new ContentValues();
	//
	// String where = ChatTable.FROMID + " =? and " + ChatTable.ID + " =?";
	// String args[] = new String[] { string, messageId };
	// values.put(ChatTable.MESSAGE_STATUS, "online");
	//
	// sqLiteDatabase.update(ChatTable.TABLENAME, values, where, args);
	//
	// }

	public List<ChatData> loadOfflineMessages(Context context, String string) {
		// TODO Auto-generated method stub
		ArrayList<ChatData> chatHistory = new ArrayList<ChatData>();

		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(context);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		String where = ChatTable.FROMID + " =? and " + ChatTable.MESSAGE_STATUS
				+ " =?";
		String args[] = new String[] { string, "offline" };

		Cursor cursor = sqLiteDatabase.query(ChatTable.TABLENAME, null, where,
				args, null, null, null);

		if (cursor.moveToFirst()) {
			do {

				ChatData mChatData = new ChatData();
				mChatData.setMessageId(cursor.getString(0));
				mChatData.setFromId(cursor.getString(1));
				mChatData.setToId(cursor.getString(2));
				mChatData.setOrginalMessage(cursor.getString(3));
				mChatData.setChatMessageDatetime(cursor.getString(6));
				mChatData.setChatFriendName(cursor.getString(7));
				mChatData.setMediaStatus(cursor.getString(10));
				chatHistory.add(mChatData);

			} while (cursor.moveToNext());
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}

		return chatHistory;
	}

	public void setasonlineMessages(Context context, String messageId) {
		// TODO Auto-generated method stub
		BarterDatabaseHelper mScliDatabaseHelper = BarterDatabaseHelper
				.getInstance(context);
		SQLiteDatabase sqLiteDatabase = mScliDatabaseHelper
				.getMyWritableDatabase();

		ContentValues values = new ContentValues();

		String where = ChatTable.FROMID + " =?";
		String args[] = new String[] { messageId };
		values.put(ChatTable.MESSAGE_STATUS, "online");

		sqLiteDatabase.update(ChatTable.TABLENAME, values, where, args);
	}

}
