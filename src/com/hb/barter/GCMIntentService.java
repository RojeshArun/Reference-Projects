/*
 * Copyright 2012 Google Inc. Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.hb.barter;

import static com.hb.barter.CommonUtilities.SENDER_ID;
import static com.hb.barter.CommonUtilities.displayMessage;

import java.util.List;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.barter.xmpp.ServiceUtility;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	private SharedPreferences userDetails;

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		displayMessage(context, getString(R.string.gcm_registered));
		// ServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		displayMessage(context, getString(R.string.gcm_unregistered));
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			// ServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
		String message = intent.getExtras().getString("message");
		// String message = "Notification";
		displayMessage(context, message);
		// notifies user
		Log.i("MessageIntent", intent.getExtras().toString());
		userDetails = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = userDetails.edit();

		if (intent.getExtras().getString("type").equalsIgnoreCase("LG")) {
			Intent logoutIntent = new Intent(ServiceUtility.LOGOUT_NOTIFICATION);
			getApplication().sendBroadcast(logoutIntent);

		} else if (intent.getExtras().getString("type").equalsIgnoreCase("CN")) {
			editor.putString("OfflineNotification", "Yes");
			editor.putString("isChatMessage", "Yes");
			editor.putString("from_userid",
					intent.getExtras().getString("jabber_id"));
		} else {
			editor.putString("slideBadgeCount",
					intent.getExtras().getString("badge"));
			editor.putString("OfflineNotification", "Yes");
			if (intent.getExtras().getString("type").equalsIgnoreCase("AR")) {
				editor.putString("isChatMessage", "Yes");
				editor.putString("from_userid",
						intent.getExtras().getString("jabber_id"));
			} else {
				editor.putString("isChatMessage", "No");
			}

		}
		editor.commit();
		if (intent.getExtras().getString("type").equalsIgnoreCase("LG")) {
			SharedPreferences.Editor editor1 = userDetails.edit();
			editor1.clear();
			editor1.commit();
		} else {

			try {
				boolean foregroud = new ForegroundCheckTask().execute(
						getApplicationContext()).get();
				if (foregroud) {
					if (intent.getExtras().getString("type")
							.equalsIgnoreCase("CN")) {

					} else {
						Bundle mbBundle = intent.getExtras();
						generateNotification(context, message, mbBundle);
					}
				} else {
					Bundle mbBundle = intent.getExtras();
					generateofflineNotification(context, message, mbBundle);
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void generateofflineNotification(Context context, String message,
			Bundle mbBundle) {
		// TODO Auto-generated method stub

		Bundle mBundle = new Bundle();
		int icon = R.drawable.icn_barter;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		Intent notificationIntent = new Intent(context, LoadingScreen.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mBundle.putString("isNotification", "No");
		mBundle.putString("Message", message);
		mbBundle.putString("PN", "Yes");
		notificationIntent.putExtras(mBundle);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_VIBRATE;

		mHandler.post(new Runnable() {
			@Override
			public void run() {

			}
		});
		notificationManager.notify(0, notification);

		Intent intent2 = new Intent(ServiceUtility.REQUEST_NOTIFICATION_ACTION);
		intent2.putExtra("slideBadgeCount", mbBundle.getString("badge"));
		getApplication().sendBroadcast(intent2);

	}

	private void generateNotification(Context context, String message,
			Bundle mbBundle) {

		Bundle mBundle = new Bundle();
		int icon = R.drawable.icn_barter;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mBundle.putString("isNotification", "No");
		mBundle.putString("Message", message);
		mBundle.putString("type", mbBundle.getString("type"));
		notificationIntent.putExtras(mBundle);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_VIBRATE;

		mHandler.post(new Runnable() {
			@Override
			public void run() {

			}
		});
		notificationManager.notify(0, notification);

		Intent intent2 = new Intent(ServiceUtility.REQUEST_NOTIFICATION_ACTION);
		intent2.putExtra("slideBadgeCount", mbBundle.getString("badge"));
		getApplication().sendBroadcast(intent2);

		if (mbBundle.getString("type").equalsIgnoreCase("CN")) {

		} else {
			Intent reqintent = new Intent(ServiceUtility.REQUEST_NOTIFICATION);
			getApplication().sendBroadcast(reqintent);
		}

	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		displayMessage(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	@SuppressLint("NewApi")
	private static void generateNotification(final Context context,
			String message) {
		int icon = R.drawable.icn_barter;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		Intent notificationIntent = new Intent(context, MainActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		mHandler.post(new Runnable() {
			@Override
			public void run() {

				// Toast.makeText(context, "toast", Toast.LENGTH_SHORT).show();
				// ProgressDialog pd = ProgressDialog.show(context, "hmm", "g");
			}
		});// pd.show();
		notificationManager.notify(0, notification);

	}

	private final static Handler mHandler = new Handler();

	class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Context... params) {
			final Context context = params[0].getApplicationContext();
			return isAppOnForeground(context);
		}

		private boolean isAppOnForeground(Context context) {
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> appProcesses = activityManager
					.getRunningAppProcesses();
			if (appProcesses == null) {
				return false;
			}
			final String packageName = context.getPackageName();
			for (RunningAppProcessInfo appProcess : appProcesses) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
						&& appProcess.processName.equals(packageName)) {
					return true;
				}
			}
			return false;
		}
	}

}
