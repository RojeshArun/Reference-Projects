package com.hb.barter;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.Utilites.Validations;
import com.barter.xmpp.ConnectionService;
import com.google.android.gcm.GCMRegistrar;

public class LoadingScreen extends FragmentActivity {

	private SharedPreferences userDetails;
	private Boolean mIsLoggeOut = true;

	private static final int LOADING_DELAY = 2500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.loading_activity);

		showLoading();
		//

		pushNotificationRegister();
	}

	private void pushNotificationRegister() {

		if (Validations.isNetworkAvailable(this)) {

			GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);

			registerReceiver(mHandleMessageReceiver, new IntentFilter(
					CommonUtilities.DISPLAY_MESSAGE_ACTION));

			final String regId = GCMRegistrar.getRegistrationId(this);

			if (TextUtils.isEmpty(regId)) {
				GCMRegistrar.register(this, CommonUtilities.SENDER_ID);

			} else {

				SharedPreferences.Editor editor = userDetails.edit();
				editor.putString("RegistrationId", regId);
				editor.commit();
				Log.i("Regid", regId);
			}

		} else {
			// Validations.showAlerDialog(
			// getResources().getString(
			// R.string.no_internet_connection_please_try_again),
			// LoadingScreen.this);
			Validations.showSingleBtnDialog(
					getResources().getString(
							R.string.no_internet_connection_please_try_again),
					LoadingScreen.this);

		}
	}

	BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			String message = intent
					.getStringExtra(CommonUtilities.EXTRA_MESSAGE);

			if (TextUtils.isEmpty(message)
					&& message.equalsIgnoreCase("SERVICE_NOT_AVAILABLE")) {

				// Do Nothign

			} else {

				String notId = GCMRegistrar
						.getRegistrationId(LoadingScreen.this);

				if (!TextUtils.isEmpty(notId)) {
					SharedPreferences.Editor editor = userDetails.edit();
					editor.putString("RegistrationId", notId);
					editor.commit();
					Log.i("Regid", notId);
					// gotoChatDetailsScreen();
				}

			}

		}
	};

	private void showLoading() {

		userDetails = PreferenceManager.getDefaultSharedPreferences(this);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				if (TextUtils.isEmpty(userDetails.getString("RegistrationId",
						""))) {
					showLoading();

				} else {

					mIsLoggeOut = userDetails.getBoolean("isLoggedin", true);

					if (mIsLoggeOut) {

						if (userDetails.getBoolean(
								"is_pin_screenstate_resumed", false)) {
							gotoBarterAccountScreen();
						} else {
							gotoLoginScreen();
						}

					} else {
						gotoMainAcitivty();
					}
				}

			}
		}, LOADING_DELAY);

	}

	protected void gotoBarterAccountScreen() {
		
		Intent mIntent = new Intent(LoadingScreen.this, BarterAccountScreen.class);
		startActivity(mIntent);
		finish();
		

	}

	private boolean onlyOneTime = true;

	protected void gotoMainAcitivty() {

		SharedPreferences.Editor mEditor = userDetails.edit();
		mEditor.putBoolean("isLoggedin", false);
		mEditor.commit();

		Intent mConnectionService = new Intent(LoadingScreen.this,
				ConnectionService.class);
		mConnectionService.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startService(mConnectionService);

		if (onlyOneTime) {
			Intent mIntent = new Intent(LoadingScreen.this, MainActivity.class);
			mIntent.putExtra("fromSignUP", false);
			startActivity(mIntent);
			onlyOneTime = false;
		}
		finish();

	}

	protected void gotoChatDetailsScreen() {
		// TODO Auto-generated method stub

	}

	protected void gotoLoginScreen() {

		Intent mAcitivtyIntent1 = new Intent(LoadingScreen.this,
				LoginScreen.class);
		mAcitivtyIntent1.putExtra("isFromMainAcitivty", false);

		startActivity(mAcitivtyIntent1);
		finish();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		unregisterReceiver(mHandleMessageReceiver);
	}
}
