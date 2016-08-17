package com.barter.BackgroundServices;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.VolleyError;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

public class GPSServiceUpdater extends Service implements IParseListener {
	String tag = "UpdateGPSData";

	private static final int USER_LOCATION = 001;

	/** interface for clients that bind */
	IBinder mBinder;
	/** indicates whether onRebind should be used */
	boolean mAllowRebind;

	public final static String ACTION = "NotifyServiceAction";
	public final static String STOP_SERVICE = "";
	public final static int RQS_STOP_SERVICE = 1;

	private int locationUpdateRate = 15000;

	// Shared Preferences
	private SharedPreferences userDetails;

	@Override
	public void onCreate() {
		super.onCreate();
		// Toast.makeText(this, "Service created...", Toast.LENGTH_LONG).show();
		Log.e(tag, "Service created...");
		userDetails = PreferenceManager.getDefaultSharedPreferences(this);

		SharedPreferences.Editor editor = userDetails.edit();
		editor.putBoolean("isFromBackgroudSrevice", true);
		editor.commit();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		updateGPSData();
		return START_STICKY;
	}

	private void updateGPSData() {
		// TODO Auto-generated method stub

		locationUpdateRate = userDetails.getInt("location_update_rate", 15000);

		repeatOnInterval();

	}

	private void repeatOnInterval() {
		// TODO Auto-generated method stub

		mhHandler.removeMessages(1);
		mhHandler.sendEmptyMessageDelayed(1, locationUpdateRate);
		//
		// Intent mIntent = new Intent("UPDATEGPS");
		// getApplication().sendBroadcast(mIntent);
		
		
		GPSTracker gps = new GPSTracker(this);
		double latitude = gps.getLatitude();
		double longitude = gps.getLongitude();
		
		if(latitude!=0 || longitude!=0)
		callUpdateMyLocationWS(latitude,longitude);

	}

	private Handler mhHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			updateGPSData();
		};
	};

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.e(tag, "Service started...");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mhHandler.removeMessages(1);
		stopSelf();
		Log.e("service Stopped", "");

		// Toast.makeText(this, "Service destroyed...",
		// Toast.LENGTH_LONG).show();
		// this.unregisterReceiver(notifyServiceReceiver);

		SharedPreferences.Editor editor = userDetails.edit();
		editor.putBoolean("isFromBackgroudSrevice", false);
		editor.commit();
	}

	@Override
	public boolean stopService(Intent name) {
		// TODO Auto-generated method stub
		mhHandler.removeMessages(1);
		stopSelf();
		Log.e("service Stopped", "");

		return super.stopService(name);

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	/** Called when all clients have unbound with unbindService() */
	@Override
	public boolean onUnbind(Intent intent) {
		// Toast.makeText(this, "Service unbound...", Toast.LENGTH_LONG).show();
		return mAllowRebind;
	}

	/** Called when a client is binding to the service with bindService() */
	@Override
	public void onRebind(Intent intent) {
		// Toast.makeText(this, "Service onRebound...",
		// Toast.LENGTH_LONG).show();
	}

	public void callUpdateMyLocationWS(double latitude, double longitude) {  

	

		Editor editor = userDetails.edit();
		editor.putFloat("UserLatitudes", (float) latitude);
		editor.putFloat("UserLongitudes", (float) longitude);
		editor.putString("UserLatitude", latitude + "");
		editor.putString("UserLongitude", longitude + "");
		editor.commit();

		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		Bundle mBundle = new Bundle();

		mBundle.putString("user_id",
				userDetails.getString("Loggedin_userid", ""));
		

		mBundle.putString("user_latitude",
				userDetails.getString("UserLatitude", ""));
		mBundle.putString("user_longitude",
				userDetails.getString("UserLongitude", ""));

		Log.e("Lat & Long Background service",
				userDetails.getString("UserLatitude", "")
						+ userDetails.getString("UserLongitude", ""));

		mRequestResponse
				.getResponse(Webservices.encodeUrl(
						Webservices.UPDATE_USERLOCATION, mBundle),
						USER_LOCATION, this);

	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		// TODO Auto-generated method stub
		Log.e("GPS error response ", error.toString());
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		// TODO Auto-generated method stub

		Log.e("GPS success response ", response.toString());

	}

}
