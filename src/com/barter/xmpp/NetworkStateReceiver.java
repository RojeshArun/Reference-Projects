package com.barter.xmpp;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {

	private SharedPreferences userDetails;

	@SuppressWarnings("deprecation")
	public void onReceive(Context context, Intent intent) {
		Log.d("app", "Network connectivity change");
		if (intent.getExtras() != null) {
			NetworkInfo ni = (NetworkInfo) intent.getExtras().get(
					ConnectivityManager.EXTRA_NETWORK_INFO);
			if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
				Log.i("app", "Network " + ni.getTypeName() + " connected");
				userDetails = PreferenceManager
						.getDefaultSharedPreferences(context);
				if (userDetails.getString("internet", "No").equalsIgnoreCase(
						"No")) {

					SharedPreferences.Editor editor = userDetails.edit();
					editor.putString("internet", "Yes");
					editor.putBoolean("wifiCame", true);
					editor.putBoolean("NetworkCame", true);
					editor.putBoolean("IsNetworkPresent", true);
					editor.commit();

					// if (isMyServiceRunning(ConnectionService.class, context))
					// {
					// Intent restartListners = new Intent(
					// ServiceUtility.RESTART_LISTNERS);
					// context.sendBroadcast(restartListners);
					//
					// } else {
					//
					// Intent mConnectionService = new Intent(context,
					// ConnectionService.class);
					// mConnectionService
					// .setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK
					// | Intent.FLAG_ACTIVITY_NEW_TASK
					// | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					// context.startService(mConnectionService);
					// }

					Intent intent2 = new Intent(
							ServiceUtility.NETWORK_CONNECTION);
					context.sendBroadcast(intent2);

					Intent networkIntent = new Intent(
							ServiceUtility.NETWORK_STATE);
					context.sendBroadcast(networkIntent);

				}

			}
		}
		if (intent.getExtras().getBoolean(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
			Log.d("app", "There's no network connectivity");

			// Intent mConnectionService = new Intent(context,
			// ConnectionService.class);
			// mConnectionService.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK
			// | Intent.FLAG_ACTIVITY_NEW_TASK
			// | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			// context.stopService(mConnectionService);

			userDetails = PreferenceManager
					.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = userDetails.edit();
			editor.putString("internet", "No");
			editor.putString("NetReconnected", "No");
			editor.putBoolean("NetworkLoss", true);
			editor.putBoolean("IsNetworkPresent", false);
			editor.commit();

			Intent networkIntent = new Intent(ServiceUtility.NETWORK_STATE);
			context.sendBroadcast(networkIntent);

		}
	}

	private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}