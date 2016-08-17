package com.barter.xmpp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BarterDatabaseHelper extends SQLiteOpenHelper {

	private static BarterDatabaseHelper mInstance;
	private static SQLiteDatabase myWritableDb;

	/**
	 * Constructor takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 *            the application context
	 */
	private BarterDatabaseHelper(Context context) {
		super(context, BarterDBInfo.DATABASE_NAME, null,
				BarterDBInfo.DATABASE_VERSION);
	}

	/**
	 * Get default instance of the class to keep it a singleton
	 * 
	 * @param context
	 *            the application context
	 */
	public static BarterDatabaseHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new BarterDatabaseHelper(context);
		}
		return mInstance;
	}

	/**
	 * Returns a writable database instance in order not to open and close many
	 * SQLiteDatabase objects simultaneously
	 * 
	 * @return a writable instance to SQLiteDatabase
	 */
	public SQLiteDatabase getMyWritableDatabase() {
		if ((myWritableDb == null) || (!myWritableDb.isOpen())) {
			myWritableDb = this.getWritableDatabase();
		}

		return myWritableDb;
	}

	@Override
	public void close() {
		super.close();
		if (myWritableDb != null) {
			myWritableDb.close();
			myWritableDb = null;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// db.execSQL(ContactsTable.CREATETABLESQL);
		db.execSQL(ChatTable.CREATETABLESQL);
		db.execSQL(UserTable.CREATETABLESQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion != BarterDBInfo.DATABASE_VERSION) {

			// db.execSQL("DROP TABLE IF EXISTS " + ContactsTable.TABLENAME);
			db.execSQL("DROP TABLE IF EXISTS " + ChatTable.TABLENAME);
			db.execSQL("DROP TABLE IF EXISTS " + UserTable.TABLENAME);
			onCreate(db);
		}
	}
}