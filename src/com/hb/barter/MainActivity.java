package com.hb.barter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.packet.VCard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.Utilites.InterfaceUtils.IMenuClicked;
import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.barter.BackgroundServices.GPSServiceUpdater;
import com.barter.BackgroundServices.GPSTracker;
import com.barter.fragments.ConverterFragment;
import com.barter.xmpp.ChatDBHelper;
import com.barter.xmpp.ChatData;
import com.barter.xmpp.ConnectionService;
import com.barter.xmpp.HBXMPP;
import com.barter.xmpp.ServiceUtility;
import com.hb.baseapplication.BaseApplication;
import com.hb.floatingfragments.AboutAppFragment;
import com.hb.floatingfragments.BlockedUsesListFragment;
import com.hb.floatingfragments.MyProfileFragment;
import com.hb.floatingfragments.SetHigherRatingFragment;
import com.hb.floatingfragments.SetHigherRatingFragmentRequestPin;
import com.hb.floatingfragments.ShareFragment;
import com.hb.floatingfragments.SupportFragment;
import com.hb.fragments.ChatListingFragment;
import com.hb.fragments.NearByFragment;
import com.hb.models.PreferencesSetDetails;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

public class MainActivity extends FragmentActivity implements OnClickListener,
		IMenuClicked, IParseListener {

	private FragmentManager mFragmentManager;
	public FragmentTransaction mFragmentTransaction;

	private Context mContext;
	private LinearLayout mLayout, mPrefLayout, mPrefNewLayout;
	public ImageView mTab1, mTab2, mTab3;
	public LinearLayout mTab1Lyt, mTab2Lyt, mTab3Lyt;
	private TextView mTab1Txt, mTab2Txt, mTab3Txt;

	private List<PreferencesSetDetails> mPreferenceList = new ArrayList<PreferencesSetDetails>();
	private ArrayList<String> cnt = new ArrayList<String>();
	private boolean[] _IHaveItemsselection, _IAmLookingselection;
	private int selected = 0;
	private int[] mSelectedList;
	// private String preferenceMasterCode;

	public static boolean isFromChatDetails = false;
	private List<PreferencesSetDetails> mPreferenceListILook = new ArrayList<PreferencesSetDetails>();
	private ArrayList<String> cntLook = new ArrayList<String>();
	// private int selectedLook = 0;
	// private String preferenceMasterCodeLook;

	private static final int USER_LOCATION = 000;

	private Boolean isFromLogin = true;

	// Preference Vairablsez
	private String mIHaveSeqNum, mILookForSeqNum;
	private String mProgressVal;

	String user_id;
	String display_user_name;
	String display_email;
	// Seek Bar
	private SeekBar mSeekBar;
	private TextView mSeekBarValue;

	private TextView mTextViewIhaveCurrency;
	private TextView mTextViewIAmLOokingForCurrency;

	// Shared Preferences
	private SharedPreferences userDetails;
	public boolean isShowPreference = true;
	private CheckBox mVisibility, mReceiveNotificaion;

	// private CheckBox mTab1;
	private FrameLayout mMainLayout;
	private RelativeLayout mTopbarLyt;
	private LinearLayout mBottomLyt;
	private FrameLayout mFloatingLyt;

	// Top Bar Variables
	// public ImageView mLeftButton, mRightMostButton, mRightButOneButton;
	public ImageView mLeftButton;
	private TextView mHeaderTitle;

	// Floating Menu Items

	private RelativeLayout mProfileLyt, mBlockUserLyt, mSetHigherRatingLyt,
			mSupprotLyt, mShareLyt, mAboutAppLyt, mLogoutLyt;
	private ImageView mBackBtn;

	private Boolean isOpen = false;

	public Boolean isCurrentLocationSet = false;

	private static final int CHECK_HIGHER_RATING = 301;

	private static final int I_HAVE = 302;

	private static final int SET_PREFERENCES = 304;

	private static final int I_AM_LOOKING = 303;

	public static MainActivity mMainAcitivity;
	private BaseApplication xmppDemoApp;
	public static final String HOST = Webservices.HOST;
	public static final int PORT = 5222;
	public static final String SERVICE = Webservices.SERVICE;

	private List<ChatData> chatHistory = new ArrayList<ChatData>();
	private ChatDBHelper chatDBHelper;
	private HBXMPP hbxmpp;

	String mIhaveItems[];
	String mIAmLookingForItems[];

	public TextView mNotificationtxtview;

	public static MainActivity getInstance() {
		if (mMainAcitivity != null)
			return mMainAcitivity;
		else
			return new MainActivity();
	}

	// loading_transperent
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		killApplication();

		overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

		setContentView(R.layout.activity_base_latest);

		mMainAcitivity = this;

		xmppDemoApp = (BaseApplication) getApplication();
		hbxmpp = ((BaseApplication) getApplication()).getHbxmpp();
		chatDBHelper = new ChatDBHelper();
		userDetails = PreferenceManager.getDefaultSharedPreferences(this);

		isFromLogin = getIntent().getExtras().getBoolean("fromSignUP");

		if (getIntent().getExtras().getBoolean("fromSignUP1")) {
			overridePendingTransition(R.anim.enter_from_left,
					R.anim.exit_to_right);
		}

		Validations.setMenuWidthHeightProportions(this);
		getBaseAcitivityReferences();

		if (!(TextUtils
				.isEmpty(userDetails.getString("i_want_currency_id", "")))) {

			if (isFromChatDetails) {
				gotoFirstPreferencesFragment();
				disableFirstTab();
				isFromChatDetails = false;

			} else {
				gotoMiddleChattingFragment();
				disableSecondTab();
			}
		} else {
			gotoFirstPreferencesFragment();
			disableFirstTab();
		}
		// getCurrentLocation();

		if (userDetails.getBoolean("isCustomLocation", false)) {
			stopService(new Intent(getBaseContext(), GPSServiceUpdater.class));
		} else {
			startGPSService();
		}

	}

	/* Kill Application When crashed */
	private void killApplication() {

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				ex.printStackTrace();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

	}

	public void startGPSService() {

		startService(new Intent(this, GPSServiceUpdater.class));
	}

	// Method to stop the service
	public void stopService(View view) {

		stopService(new Intent(getBaseContext(), GPSServiceUpdater.class));

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// stopService(new Intent(getBaseContext(), GPSServiceUpdater.class));

	}

	public void showSingleBtnDialog(String message, Context context) {
		TextView btnOK, btnCancel, txtTitle, txtMessage;

		String title = "Bartr";

		final Dialog alertDialog = new Dialog(context,
				R.style.AlertDialogCustom);
		alertDialog.setCancelable(false);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setContentView(R.layout.layout_alert_info);
		txtTitle = (TextView) alertDialog.findViewById(R.id.txtTitle);
		txtMessage = (TextView) alertDialog.findViewById(R.id.txtMessage);

		alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = alertDialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

		if (title != null) {
			txtTitle.setText(title);
		}

		txtMessage.setText(message);

		btnOK = (TextView) alertDialog.findViewById(R.id.btnOK);
		btnOK.setText("OK");
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});

		alertDialog.show();
	}

	public static void showTwoBtnDialog(final Activity activity,
			String positiveBtnTxt, String negativeBtnTxt, String message) {

		TextView btnOK, btnCancel, txtTitle, txtMessage;

		String title = "Bartr";

		final Dialog alertDialog = new Dialog(activity,
				R.style.AlertDialogCustom);
		alertDialog.setCancelable(false);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setContentView(R.layout.layout_exit_alert);
		txtTitle = (TextView) alertDialog.findViewById(R.id.txtTitle);
		txtMessage = (TextView) alertDialog.findViewById(R.id.txtMessage);

		alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = alertDialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

		if (title != null) {
			txtTitle.setText(title);
		}

		txtMessage.setText(message);

		btnOK = (TextView) alertDialog.findViewById(R.id.btnOK);
		btnOK.setText(positiveBtnTxt);
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		btnCancel = (TextView) alertDialog.findViewById(R.id.btnCancel);
		btnCancel.setText(negativeBtnTxt);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		btnCancel.setVisibility(View.VISIBLE);

		alertDialog.show();
	}

	private Location getCurrentLocation() {

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);

		Location location = null;
		location = locationManager.getLastKnownLocation(provider);

		if (location != null) {
			// Save in Shared Preferences

			SharedPreferences.Editor editor = userDetails.edit();
			editor.putFloat("UserLatitudes", (float) location.getLatitude());
			editor.putFloat("UserLongitudes", (float) location.getLongitude());

			editor.putString("UserLatitude", location.getLatitude() + "");
			editor.putString("UserLongitude", location.getLongitude() + "");

			editor.commit();

		} else {
			// Show Pop up to ebasle GPS

			List<String> providerlist = locationManager.getAllProviders();
			if (providerlist != null && providerlist.size() > 0) {
				for (int i = providerlist.size() - 1; i >= 0; i--) {
					location = locationManager
							.getLastKnownLocation(providerlist.get(i));
					if (location != null) {

						SharedPreferences.Editor editor = userDetails.edit();
						editor.putFloat("UserLatitudes",
								(float) location.getLatitude());
						editor.putFloat("UserLongitudes",
								(float) location.getLongitude());

						editor.putString("UserLatitude", location.getLatitude()
								+ "");
						editor.putString("UserLongitude",
								location.getLongitude() + "");

						editor.commit();
						break;
					}
				}
			}
		}
		return location;

	}

	// Chat Count

	private Handler mhHandler1 = new Handler();
	private BroadcastReceiver serviceconnection = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mhHandler1.post(new Runnable() {
				@Override
				public void run() {
					settheCountData();
				}

			});
		}
	};

	private void settheCountData() {

		// chatHistory
		chatHistory.clear();
		try {
			chatHistory = chatDBHelper.loadAllRecentChatsUnReadCount(
					MainActivity.this,
					userDetails.getString("Loggedin_jabber_id", ""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int count = 0;
		for (int i = 0; i < chatHistory.size(); i++) {
			count = count + chatHistory.get(i).getUnreadMessageCount();
		}

		if (userDetails.getString("totalMessageCount", "").length() == 0) {

		} else {
			count = count
					+ Integer.valueOf(userDetails.getString(
							"totalMessageCount", ""));
		}

		if (count != 0) {
			mNotificationtxtview.setVisibility(View.VISIBLE);
			mNotificationtxtview.setText("" + count);
		} else {
			mNotificationtxtview.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.i("OnResumeCalled", "Yes");

		registerReceiver(xmppserverConnection, new IntentFilter(
				ServiceUtility.SERVER_CONNECTION));

		registerReceiver(xmppserverConnectionClosed, new IntentFilter(
				ServiceUtility.SERVER_CONNECTION_CLOSED));

		registerReceiver(updateGPS, new IntentFilter("UPDATEGPS"));

		registerReceiver(serviceconnection, new IntentFilter(
				ServiceUtility.MESSAGE_UPDATE_ACTION));

	}

	private BroadcastReceiver updateGPS = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mhHandler1.post(new Runnable() {
				@Override
				public void run() {

					if (Validations.isNetworkAvailable(MainActivity.this)) {

						callUpdateMyLocationWS();

					} else {

					}

				}

				private void callUpdateMyLocationWS() {

					// Location mLocation = getCurrentLocation();

					GPSTracker gps = new GPSTracker(MainActivity.this);
					double latitude = gps.getLatitude();
					double longitude = gps.getLongitude();

					JSONRequestResponse mRequestResponse = new JSONRequestResponse();
					Bundle mBundle = new Bundle();

					mBundle.putString("user_id",
							userDetails.getString("Loggedin_userid", ""));
					mBundle.putString("user_latitude",
							userDetails.getString("UserLatitude", ""));
					mBundle.putString("user_longitude",
							userDetails.getString("UserLongitude", ""));
					// mBundle.putString("device_type", "android");
					// mBundle.putString("udid", Validations.udid);

					mRequestResponse.getResponse(Webservices.encodeUrl(
							Webservices.UPDATE_USERLOCATION, mBundle),
							USER_LOCATION, MainActivity.this);

					// http://local.configure.it/locationbasedchatapp/WS/user_update_location/
					// ?&user_id=2&user_latitude=45.4545
					// &user_longitude=50.5050&device_type=iOS&udid=78945656

					// http://local.configure.it/locationbasedchatapp/WS/set_location_view/?
					// &user_id=4&user_latitude=23.417&user_longitude=59.356
				}

			});
		}
	};

	@Override
	protected void onStart() {
		super.onStart();

		Log.i("OnStartCalled", "Yes");

		if (Validations.isNetworkAvailable(MainActivity.this)) {

			try {
				hbxmpp = ((BaseApplication) getApplication()).getHbxmpp();
				if (hbxmpp != null && hbxmpp.getConnection().isConnected()
						&& hbxmpp.getConnection().isAuthenticated()) {
				} else {

					if (userDetails.getBoolean("NetworkLoss", false) == true) {

					} else {

						SendConnectingBroadCast(true);
						ReconnectXmpp();
					}

				}
			} catch (Exception e) {
				if (userDetails.getBoolean("NetworkLoss", false) == true) {

				} else {

					SendConnectingBroadCast(true);
					ReconnectXmpp();
				}
			}
		}

	}

	public void ReconnectXmpp() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					hbxmpp = new HBXMPP(HOST, PORT, SERVICE);
					hbxmpp.connect();
					if (hbxmpp.getConnection().isConnected()) {
						try {
							hbxmpp.login(userDetails.getString(
									"Loggedin_jabber_id", ""), userDetails
									.getString("Loggedin_jabber_password", ""));
							if (hbxmpp.getConnection().isAuthenticated()) {
								xmppDemoApp.setHbxmpp(hbxmpp);
								if (isMyServiceRunning(ConnectionService.class)) {
									Intent restartListners = new Intent(
											ServiceUtility.RESTART_LISTNERS);
									sendBroadcast(restartListners);

								} else {
									Intent mConnectionService = new Intent(
											MainActivity.this,
											ConnectionService.class);
									mConnectionService
											.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK
													| Intent.FLAG_ACTIVITY_NEW_TASK
													| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
									startService(mConnectionService);
								}
								SendConnectingBroadCast(false);
								sendAllOfflineMessages();
							} else {
								ReconnectXmpp();
							}
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									try {
										if (hbxmpp.getConnection()
												.isAuthenticated()) {
											// saveintoDatabase(hbxmpp.getConnection());
											Intent mIsXmppConnected = new Intent(
													ServiceUtility.IS_XMPP_LOGGEDIN);
											sendBroadcast(mIsXmppConnected);
											if (!isFromLogin) {
												uploadAvatar(
														userDetails
																.getString(
																		"Loggedin_userid",
																		"")
																+ Webservices.CHAT_DOMAIN,
														userDetails
																.getString(
																		"profile_image",
																		""));
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							ReconnectXmpp();
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					ReconnectXmpp();
				}
			}
		}).start();

	}

	private void uploadAvatar(String jabberId, String url) {

		GetProfileBitMap mAsyncGetBitMap = new GetProfileBitMap(url);
		mAsyncGetBitMap.execute();

	}

	public class GetProfileBitMap extends AsyncTask<Void, Void, Bitmap> {
		private String userUrl;

		public GetProfileBitMap(String url) {
			// TODO Auto-generated constructor stub
			userUrl = url;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			// TODO Auto-generated method stub

			try {
				URL url = new URL(userUrl);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				return myBitmap;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (result != null) {

				try {

					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					result.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] array = stream.toByteArray();

					VCard vCard = new VCard();
					vCard.load(hbxmpp.getConnection());
					vCard.setOrganization("Bartr");
					vCard.setAvatar(array);
					vCard.save(hbxmpp.getConnection());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

	}

	public void SendConnectingBroadCast(boolean b) {

		SharedPreferences.Editor editor = userDetails.edit();
		if (b)
			editor.putString("ReConnecting", "Yes");
		else
			editor.putString("ReConnecting", "No");
		editor.commit();

		Bundle mBundle = new Bundle();
		mBundle.putBoolean("Connecting", b);
		Intent intent2 = new Intent(ServiceUtility.RE_CONNECTION);
		intent2.putExtras(mBundle);
		sendBroadcast(intent2);
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.i("OnPauseCalled", "Yes");
		unregisterReceiver(xmppserverConnection);
		unregisterReceiver(xmppserverConnectionClosed);
		unregisterReceiver(updateGPS);
		unregisterReceiver(serviceconnection);

	}

	@Override
	protected void onStop() {
		super.onStop();

		Log.i("OnStopCalled", "Yes");

		Thread mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					hbxmpp.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mThread.start();

	}

	private Handler mhHandler = new Handler();

	private BroadcastReceiver xmppserverConnection = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mhHandler1.post(new Runnable() {
				@Override
				public void run() {

					if (Validations.isNetworkAvailable(MainActivity.this)) {
						ReconnectingToServer();
					} else {
						hbxmpp.disconnect();
						SharedPreferences.Editor editor = userDetails.edit();
						editor.putString("xmppconnection", "No");
						editor.commit();

						Intent networkIntent = new Intent(
								ServiceUtility.NETWORK_STATE);
						sendBroadcast(networkIntent);

					}

				}

			});
		}
	};

	private BroadcastReceiver xmppserverConnectionClosed = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mhHandler1.post(new Runnable() {
				@Override
				public void run() {

					if (Validations.isNetworkAvailable(MainActivity.this)) {

						ReconnectingToServer();
					} else {
						hbxmpp.disconnect();
						SharedPreferences.Editor editor = userDetails.edit();
						editor.putString("xmppconnection", "No");
						editor.commit();
					}

				}

			});
		}
	};

	private void ReconnectingToServer() {

		SharedPreferences.Editor editor = userDetails.edit();
		editor.putString("Connecting", "Yes");
		editor.commit();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					hbxmpp.disconnect();

					hbxmpp = new HBXMPP(HOST, PORT, SERVICE);
					hbxmpp.connect();
					if (hbxmpp.getConnection().isConnected()) {
						try {
							hbxmpp.login(userDetails.getString(
									"Loggedin_jabber_id", ""), userDetails
									.getString("Loggedin_jabber_password", ""));
							if (hbxmpp.getConnection().isAuthenticated()) {
								xmppDemoApp.setHbxmpp(hbxmpp);
								Log.e("Logged in user", hbxmpp.getConnection()
										.getUser());
								if (isMyServiceRunning(ConnectionService.class)) {
									Intent restartListners = new Intent(
											ServiceUtility.RESTART_LISTNERS);
									sendBroadcast(restartListners);

								} else {

									Intent mConnectionService = new Intent(
											MainActivity.this,
											ConnectionService.class);
									mConnectionService
											.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK
													| Intent.FLAG_ACTIVITY_NEW_TASK
													| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
									startService(mConnectionService);
								}

								sendAllOfflineMessages();

								SharedPreferences.Editor editor = userDetails
										.edit();
								editor.putString("Connecting", "No");
								editor.putString("ReConnectionFailed", "No");
								editor.commit();

							} else {

								xmppDemoApp.setHbxmpp(hbxmpp);
								Log.e("Logged in user", hbxmpp.getConnection()
										.getUser());

								Intent mConnectionService = new Intent(
										MainActivity.this,
										ConnectionService.class);
								mConnectionService
										.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK
												| Intent.FLAG_ACTIVITY_NEW_TASK
												| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
								startService(mConnectionService);

								sendAllOfflineMessages();

								SharedPreferences.Editor editor = userDetails
										.edit();
								editor.putString("Connecting", "No");
								editor.putString("ReConnectionFailed", "No");
								editor.commit();
							}

						} catch (Exception e) {
							ReconnectingToServer();
							e.printStackTrace();
							SharedPreferences.Editor editor = userDetails
									.edit();
							editor.putString("ReConnectionFailed", "Yes");
							editor.commit();
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					ReconnectingToServer();
				}
			}
		}).start();
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private List<ChatData> offlineConnectingHistory = new ArrayList<ChatData>();

	private void sendAllOfflineMessages() {

		try {
			offlineConnectingHistory = chatDBHelper.loadOfflineMessages(
					MainActivity.this,
					userDetails.getString("Loggedin_jabber_id", "")
							+ Webservices.CHAT_DOMAIN);

			for (int j = 0; j < offlineConnectingHistory.size(); j++) {

				Message msg = new Message(offlineConnectingHistory.get(j)
						.getToId(),

				Message.Type.chat);
				msg.setBody(offlineConnectingHistory.get(j).getOrginalMessage());
				hbxmpp.getConnection().sendPacket(msg);

			}

			chatDBHelper.setasonlineMessages(MainActivity.this,
					userDetails.getString("Loggedin_jabber_id", "")
							+ Webservices.CHAT_DOMAIN);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private void getBaseAcitivityReferences() {

		mTab1 = (ImageView) findViewById(R.id.tab1);
		mTab2 = (ImageView) findViewById(R.id.tab2);
		mTab3 = (ImageView) findViewById(R.id.tab3);

		mTab1Lyt = (LinearLayout) findViewById(R.id.tab1Lyt);
		mTab2Lyt = (LinearLayout) findViewById(R.id.tab2Lyt);
		mTab3Lyt = (LinearLayout) findViewById(R.id.tab3Lyt);

		mTab1Txt = (TextView) findViewById(R.id.maptxt);
		mTab2Txt = (TextView) findViewById(R.id.toolstxt);
		mTab3Txt = (TextView) findViewById(R.id.chattxt);

		mTopbarLyt = (RelativeLayout) findViewById(R.id.topmenu);
		mMainLayout = (FrameLayout) findViewById(R.id.frame_container);
		mBottomLyt = (LinearLayout) findViewById(R.id.bottommenu);
		mFloatingLyt = (FrameLayout) findViewById(R.id.popupLyt);

		mLeftButton = (ImageView) findViewById(R.id.slidedown);
		// mRightButOneButton = (ImageView)
		// findViewById(R.id.rightButOneButton);
		// mRightMostButton = (ImageView) findViewById(R.id.rightMostButton);

		mHeaderTitle = (TextView) findViewById(R.id.title);

		mLayout = (LinearLayout) findViewById(R.id.floatingLayout);
		mPrefLayout = (LinearLayout) findViewById(R.id.prefLayout);
		mPrefNewLayout = (LinearLayout) findViewById(R.id.preflayoutNew);

		//

		mProfileLyt = (RelativeLayout) findViewById(R.id.profileLayout);
		mBlockUserLyt = (RelativeLayout) findViewById(R.id.blockLayout);
		mSetHigherRatingLyt = (RelativeLayout) findViewById(R.id.ratingLayout);
		mSupprotLyt = (RelativeLayout) findViewById(R.id.supportLayout);
		mShareLyt = (RelativeLayout) findViewById(R.id.shareLayout);
		mAboutAppLyt = (RelativeLayout) findViewById(R.id.aboutAppLayout);
		mLogoutLyt = (RelativeLayout) findViewById(R.id.logoutLayout);
		// mVisibleUser = (RelativeLayout) findViewById(R.id.myVisibleUser);

		// mPreferencesLyt = (FrameLayout) findViewById(R.id.preferencesLayout);

		mBackBtn = (ImageView) findViewById(R.id.slidedown);

		if (userDetails.contains("Loggedin_userid")) {
			user_id = userDetails.getString("Loggedin_userid", "def");

		}
		mNotificationtxtview = (TextView) findViewById(R.id.notification_count);
		mNotificationtxtview.setVisibility(View.INVISIBLE);

		setOnClickListeners();

	}

	private void setOnClickListeners() {

		mLeftButton.setOnClickListener(this);
		// mRightButOneButton.setOnClickListener(this);
		// mRightMostButton.setOnClickListener(this);

		mTab1.setOnClickListener(this);
		mTab2.setOnClickListener(this);
		mTab3.setOnClickListener(this);

		mTab1Lyt.setOnClickListener(this);
		mTab2Lyt.setOnClickListener(this);
		mTab3Lyt.setOnClickListener(this);

		mLayout.setOnClickListener(this);
		mPrefLayout.setOnClickListener(this);

		mProfileLyt.setOnClickListener(this);
		mBlockUserLyt.setOnClickListener(this);
		mSetHigherRatingLyt.setOnClickListener(this);
		mShareLyt.setOnClickListener(this);
		mSupprotLyt.setOnClickListener(this);
		mAboutAppLyt.setOnClickListener(this);
		mLogoutLyt.setOnClickListener(this);
		// mVisibleUser.setOnClickListener(this);

		mBackBtn.setOnClickListener(this);

	}

	private NearByFragment mNearByFragment;
	private ConverterFragment mConverterFragement;
	private ChatListingFragment mChatListingFragment;
	private boolean isMapsVisible = false;
	private String tabOpened = "";

	public void gotoMiddleChattingFragment() {

		Bundle b = new Bundle();

		mNearByFragment = new NearByFragment();

		mFragmentManager = this.getSupportFragmentManager();
		checkFragmentStack();
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.setCustomAnimations(R.anim.slide_in_top,
				R.anim.slide_in_bottom);

		mFragmentTransaction.add(R.id.frame_container, mNearByFragment)
				.addToBackStack(null).commitAllowingStateLoss();
		tabOpened = "first";
	}

	public void gotoFirstPreferencesFragment() {

		Bundle b = new Bundle();

		mConverterFragement = new ConverterFragment();

		mFragmentManager = this.getSupportFragmentManager();
		checkFragmentStack();
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.setCustomAnimations(R.anim.slide_in_top,
				R.anim.slide_in_bottom);

		mFragmentTransaction.add(R.id.frame_container, mConverterFragement)
				.addToBackStack(null).commitAllowingStateLoss();
		tabOpened = "second";

	}

	private void gotoThirdChatHistoryFragment() {

		Bundle b = new Bundle();

		mChatListingFragment = new ChatListingFragment();

		mFragmentManager = this.getSupportFragmentManager();

		checkFragmentStack();
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.setCustomAnimations(R.anim.slide_in_top,
				R.anim.slide_in_bottom);

		mFragmentTransaction.add(R.id.frame_container, mChatListingFragment)
				.addToBackStack(null).commitAllowingStateLoss();
		tabOpened = "third";
	}

	@Override
	public void onClick(View v) {
		Validations.hideKeyboard(this);
		switch (v.getId()) {

		case R.id.tab1Lyt:
		case R.id.tab1:
			gotoFirstPreferencesFragment();
			disableFirstTab();

			if (isOpen)
				hideFloatingMenu();
			break;

		case R.id.tab2Lyt:
		case R.id.tab2:

			if (!(TextUtils.isEmpty(userDetails.getString("i_want_currency_id",
					"")))) {
				gotoMiddleChattingFragment();
				disableSecondTab();
				if (isOpen)
					hideFloatingMenu();
			} else {
				Validations.showSingleBtnDialog(
						getString(R.string.please_set_your_preference_), this);
				// Validations.showAlerDialog(
				// getString(R.string.please_set_your_preference_), this);
			}
			break;

		case R.id.tab3Lyt:
		case R.id.tab3:

			if (!(TextUtils.isEmpty(userDetails.getString("i_want_currency_id",
					"")))) {
				gotoThirdChatHistoryFragment();
				disableThirdTab();
				if (isOpen)
					hideFloatingMenu();
			} else {
				Validations.showSingleBtnDialog(
						getString(R.string.please_set_your_preference_), this);
				// Validations.showAlerDialog(
				// getString(R.string.please_set_your_preference_), this);
			}
			break;

		case R.id.slidedown: {

			if (!(TextUtils.isEmpty(userDetails.getString("i_want_currency_id",
					"")))) {

				if (isMapsVisible) {
					mNearByFragment.hideMaps();
				}

				if (!isShowPreference) {
					hidePreferenceMenu();

					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {

							showFloatingMenu();

						}
					}, 500);

					isShowPreference = true;
				} else {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {

							showFloatingMenu();

						}
					}, 100);

					if (!tabOpened.equalsIgnoreCase("first")) {
						gotoMiddleChattingFragment();
						disableFirstTab();

					}

				}
			} else {
				Validations.showSingleBtnDialog(
						getString(R.string.please_set_your_preference_), this);
			}
			isOpen = true;
		}
			break;

		case R.id.floatingLayout:

			hideFloatingMenu();

			break;
		case R.id.prefLayout:

			hidePreferenceMenu();

			break;

		case R.id.profileLayout:
			gotoMyProfileFragment();
			break;

		case R.id.blockLayout:
			gotoBlockedUsesList();
			break;
		// case R.id.myVisibleUser:
		// gotoVisibleUsesList();
		// break;
		case R.id.ratingLayout:
			if (Validations.isNetworkAvailable(MainActivity.this)) {
				callSetHigherRatingWS();
			} else {
				// Validations
				// .showAlerDialog(
				// getResources()
				// .getString(
				// R.string.no_internet_connection_please_try_again),
				// MainActivity.this);
				Validations
						.showSingleBtnDialog(
								getResources()
										.getString(
												R.string.no_internet_connection_please_try_again),
								MainActivity.this);
			}

			break;

		case R.id.supportLayout:
			gotoSupportFragment();
			break;

		case R.id.shareLayout:
			gotoShareFragment();
			break;

		case R.id.aboutAppLayout:
			gotoAboutAppFragment();
			break;

		// case R.id.rightMostButton:
		//
		// if (isShowPreference) {
		// showPreferences();
		// isShowPreference = false;
		// } else {
		//
		// if (TextUtils.isEmpty(mIHaveSeqNum)
		// || TextUtils.isEmpty(mILookForSeqNum)) {
		// Validations
		// .showAlerDialog(
		// getString(R.string.please_select_preferences),
		// this);
		//
		// } else {
		// hidePreferenceMenu();
		// isShowPreference = true;
		// }
		// }
		// break;
		// case R.id.rightButOneButton:
		//
		// if (!isShowPreference) {
		// isShowPreference = true;
		// hidePreferenceMenu();
		// }
		//
		// if (!isMapsVisible) {
		// // mNearByFragment.setMapLayouts();
		// mRightButOneButton.setImageResource(R.drawable.icn_list);
		// mNearByFragment.hideListing();
		// // isMapsVisible = true;
		// } else {
		// // mNearByFragment.hideMaps();
		// mRightButOneButton.setImageResource(R.drawable.map_topbar);
		// // isMapsVisible = false;
		// }
		// break;
		case R.id.logoutLayout:
			logOutBarter();
			break;

		// case R.id.popup_ihave:
		// setPreferencesData();
		// showIHaveFromPopUP(mTextViewIhaveCurrency);
		// break;
		// case R.id.popup_iamlookingfor:
		// showILookForPopUP(mTextViewIAmLOokingForCurrency);
		// break;

		default:
			break;
		}
	}

	private void callIhaveWS() {

		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		mRequestResponse.getResponse(Webservices.GET_PREFERENCES, I_AM_LOOKING,
				this);

	}

	private void callSetHigherRatingWS() {
		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", user_id);
		showProgress();
		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse
				.getResponse(Webservices.encodeUrl(
						Webservices.CHECK_HIGHER_RATING, mBundle),
						CHECK_HIGHER_RATING, this);

	}

	private void showPreferences() {

		mPrefLayout.setVisibility(View.VISIBLE);
		mLayout.setVisibility(View.GONE);

		setHeaderTitle("Preferences");

		Validations.setMenuWidthHeightProportions(this);

		LayoutParams params = (LayoutParams) mPrefNewLayout.getLayoutParams();

		params.height = Validations.screenHeight * 7 / 10;

		Validations.expandCollapse(mFloatingLyt, true);
		Validations.expandCollapse(mBottomLyt, false);

		// setPreferenceLayout();
		mNearByFragment.setSemiTransVisible();

	}

	private void showFloatingMenu() {

		mLayout.setVisibility(View.VISIBLE);
		mPrefLayout.setVisibility(View.GONE);

		Validations.expandCollapse(mFloatingLyt, true);
		Validations.expandCollapse(mBottomLyt, false);
		Validations.expandCollapse(mTopbarLyt, false);

		mNearByFragment.setSemiTransVisible();

	}

	public void hideFloatingMenu() {

		checkFragmentStackOne();

		// mChatListingFragment.setSemiTransGone();

		Validations.expandCollapse(mFloatingLyt, false);
		Validations.expandCollapse(mBottomLyt, true);
		Validations.expandCollapse(mTopbarLyt, true);
		isOpen = false;

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mNearByFragment.setSemiTransGone();
			}
		}, 200);

	}

	public void hidePreferenceMenu() {

		mNearByFragment.setSemiTransGone();

		Validations.expandCollapse(mFloatingLyt, false);
		Validations.expandCollapse(mBottomLyt, true);
		setHeaderTitle("NearBy");
		// isOpen = false;

	}

	// private void setPreferenceLayout() {
	//
	// setViewObjectsForPrefernces();
	// setClickListenersForPreferences();
	// showProgress();
	// new Handler().postDelayed(new Runnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	//
	// callIhaveWS();
	// }
	// }, 500);
	//
	// setPreferencesData();
	//
	// }

	private void setPreferencesData() {

		mSeekBarValue.setText("100 mi");

		// Set Preferences for I Have Currency
		if (!TextUtils.isEmpty(userDetails.getString("iHaveCurrencyName", ""))) {

			mTextViewIhaveCurrency.setText(userDetails.getString(
					"iHaveCurrencyName", ""));
			mIHaveSeqNum = userDetails.getString("iHaveCurrencyCode", "");

			int len = 0;
			len = (int) userDetails.getLong("iHaveCurrencyLenght", 0);

			_IHaveItemsselection = new boolean[len];

			for (int i = 0; i < len; i++) {
				_IHaveItemsselection[i] = userDetails.getBoolean(
						"iHaveCurrencyBoolArray" + i, false);

			}

			mSeekBar.setProgress(userDetails.getInt("progressval", 100));
			mSeekBarValue
					.setText(userDetails.getInt("progressval", 100) + "mi");

		}

		// Set Preference for I Am Looking for Currency
		if (!TextUtils.isEmpty(userDetails.getString(
				"iAmLookingForCurrencyName", ""))) {

			mTextViewIAmLOokingForCurrency.setText(userDetails.getString(
					"iAmLookingForCurrencyName", ""));
			mILookForSeqNum = userDetails.getString(
					"iAmLookingForCurrencyCode", "");

			int len = 0;
			len = (int) userDetails.getLong("iAmLookingForCurrencyLength", 0);

			_IAmLookingselection = new boolean[len];

			for (int i = 0; i < len; i++) {
				_IAmLookingselection[i] = userDetails.getBoolean(
						"iAmLookingForBoolArray" + i, false);

			}

		}

	}

	private void setClickListenersForPreferences() {
		mTextViewIhaveCurrency.setOnClickListener(this);
		mTextViewIAmLOokingForCurrency.setOnClickListener(this);

	}

	private void setViewObjectsForPrefernces() {

		mTextViewIhaveCurrency = (TextView) findViewById(R.id.popup_ihave);
		mTextViewIAmLOokingForCurrency = (TextView) findViewById(R.id.popup_iamlookingfor);

		mSeekBar = (SeekBar) findViewById(R.id.seekbar);

		mSeekBarValue = (TextView) findViewById(R.id.rangevalue);

		mVisibility = (CheckBox) findViewById(R.id.pref_visibility_checkbox);
		mReceiveNotificaion = (CheckBox) findViewById(R.id.pref_receivenotification_checkbox);

		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				mSeekBarValue.setText(String.valueOf(progress) + " mi");
				mProgressVal = String.valueOf(progress);

				// progressval
				SharedPreferences.Editor mEditor = userDetails.edit();

				mEditor.putInt("progressval", progress);
				mEditor.commit();

			}
		});

	}

	private void gotoSupportFragment() {
		SupportFragment mBlockFragment = new SupportFragment();

		mFragmentManager = this.getSupportFragmentManager();
		mFragmentTransaction = mFragmentManager.beginTransaction();

		mFragmentTransaction.setCustomAnimations(R.anim.enter_from_right,
				R.anim.exit_to_left, R.anim.enter_from_left,
				R.anim.exit_to_right);

		mFragmentTransaction.addToBackStack(MainActivity.class.getSimpleName())
				.add(R.id.popupLyt, mBlockFragment).commit();

	}

	private void logOutBarter() {

		SharedPreferences.Editor mEditor = userDetails.edit();
		mEditor.putBoolean("isLoggedin", true);
		mEditor.commit();

		Intent mIntent = new Intent(MainActivity.this, LoadingScreen.class);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		mIntent.putExtra("isFromMainAcitivty", true);

		SharedPreferences.Editor editor = userDetails.edit();
		editor.clear();
		editor.commit();

		stopService(mTextViewIhaveCurrency);

		startActivity(mIntent);
		finish();

	}

	private void gotoShareFragment() {

		ShareFragment mShareFragment = new ShareFragment();

		mFragmentManager = this.getSupportFragmentManager();
		mFragmentTransaction = mFragmentManager.beginTransaction();

		mFragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
				R.anim.slide_in_left, R.anim.enter_from_left,
				R.anim.exit_to_right);

		mFragmentTransaction.addToBackStack(MainActivity.class.getSimpleName())
				.add(R.id.popupLyt, mShareFragment).commit();

	}

	private void gotoAboutAppFragment() {

		// gotoSecondFragmentWithOutAnimation();

		AboutAppFragment mAboutAppFragment = new AboutAppFragment();

		mFragmentManager = this.getSupportFragmentManager();
		mFragmentTransaction = mFragmentManager.beginTransaction();

		// checkFragmentStack();

		mFragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
				R.anim.slide_in_left, R.anim.enter_from_left,
				R.anim.exit_to_right);

		mFragmentTransaction.addToBackStack(MainActivity.class.getSimpleName())
				.add(R.id.popupLyt, mAboutAppFragment).commit();

	}

	private void gotoSetHigherRatingFragment() {
		SetHigherRatingFragment mSetHigherFragment = new SetHigherRatingFragment();
		mFragmentManager = this.getSupportFragmentManager();
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
				R.anim.slide_in_left, R.anim.enter_from_left,
				R.anim.exit_to_right);
		mFragmentTransaction.addToBackStack(MainActivity.class.getSimpleName())
				.add(R.id.popupLyt, mSetHigherFragment).commit();
	}

	private void gotoSetHigherRatingRequestPINFragment() {
		SetHigherRatingFragmentRequestPin mSetHigherFragmentRequestPIN = new SetHigherRatingFragmentRequestPin();
		mFragmentManager = this.getSupportFragmentManager();
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
				R.anim.slide_in_left, R.anim.enter_from_left,
				R.anim.exit_to_right);
		mFragmentTransaction.addToBackStack(MainActivity.class.getSimpleName())
				.add(R.id.popupLyt, mSetHigherFragmentRequestPIN).commit();

	}

	private void gotoBlockedUsesList() {

		BlockedUsesListFragment mBlockFragment = new BlockedUsesListFragment();

		mFragmentManager = this.getSupportFragmentManager();
		mFragmentTransaction = mFragmentManager.beginTransaction();

		mFragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
				R.anim.slide_in_left, R.anim.enter_from_left,
				R.anim.exit_to_right);

		mFragmentTransaction.addToBackStack(MainActivity.class.getSimpleName())
				.add(R.id.popupLyt, mBlockFragment).commit();

	}

	// private void gotoVisibleUsesList() {
	//
	// VisibleUserListFragment mBlockFragment = new VisibleUserListFragment();
	//
	// mFragmentManager = this.getSupportFragmentManager();
	// mFragmentTransaction = mFragmentManager.beginTransaction();
	//
	// mFragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
	// R.anim.slide_in_left, R.anim.enter_from_left,
	// R.anim.exit_to_right);
	//
	// mFragmentTransaction.addToBackStack(MainActivity.class.getSimpleName())
	// .add(R.id.popupLyt, mBlockFragment).commit();
	//
	// }

	private void gotoMyProfileFragment() {

		MyProfileFragment mProfileFragment = new MyProfileFragment();

		mFragmentManager = this.getSupportFragmentManager();
		mFragmentTransaction = mFragmentManager.beginTransaction();

		mFragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
				R.anim.slide_in_left, R.anim.enter_from_left,
				R.anim.exit_to_right);

		mFragmentTransaction.addToBackStack(MainActivity.class.getSimpleName())
				.add(R.id.popupLyt, mProfileFragment).commit();

	}

	@Override
	public void onBackPressed() {

		if (mFragmentManager.getBackStackEntryCount() > 1) {
			// super.onBackPressed();
		} else {
			finish();
		}

	}

	public void checkFragmentStack() {
		for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); ++i) {
			mFragmentManager.popBackStack();
		}
	}

	private void checkFragmentStackOne() {
		for (int i = 1; i < mFragmentManager.getBackStackEntryCount(); ++i) {
			mFragmentManager.popBackStack();
		}
	}

	public void disableFirstTab() {

		// mRightButOneButton.setVisibility(View.VISIBLE);
		// mRightMostButton.setVisibility(View.VISIBLE);

		mTab1.setEnabled(false);
		mTab2.setEnabled(true);
		mTab3.setEnabled(true);

		mTab1Lyt.setEnabled(false);
		mTab2Lyt.setEnabled(true);
		mTab3Lyt.setEnabled(true);

		mTab1Txt.setTextColor(getResources().getColor(android.R.color.white));
		mTab2Txt.setTextColor(getResources().getColor(
				R.color.text_color_fadedgreen));
		mTab3Txt.setTextColor(getResources().getColor(
				R.color.text_color_fadedgreen));

		mTab1Lyt.setBackgroundColor(getResources().getColor(
				R.color.text_color_fadedgreen));
		mTab1.setImageResource(R.drawable.convertor_h);

		mTab2Lyt.setBackgroundColor(getResources().getColor(
				android.R.color.white));
		mTab2.setImageResource(R.drawable.near_by_matches);

		mTab3Lyt.setBackgroundColor(getResources().getColor(
				android.R.color.white));
		mTab3.setImageResource(R.drawable.chat_history);

	}

	public void disableSecondTab() {

		mTab1.setEnabled(true);
		mTab2.setEnabled(false);
		mTab3.setEnabled(true);

		mTab1Lyt.setEnabled(true);
		mTab2Lyt.setEnabled(false);
		mTab3Lyt.setEnabled(true);

		mTab1Txt.setTextColor(getResources().getColor(
				R.color.text_color_fadedgreen));
		mTab2Txt.setTextColor(getResources().getColor(android.R.color.white));
		mTab3Txt.setTextColor(getResources().getColor(
				R.color.text_color_fadedgreen));

		mTab1Lyt.setBackgroundColor(getResources().getColor(
				android.R.color.white));
		mTab1.setImageResource(R.drawable.convertor);

		mTab2Lyt.setBackgroundColor(getResources().getColor(
				R.color.text_color_fadedgreen));
		mTab2.setImageResource(R.drawable.near_by_matches_h);

		mTab3Lyt.setBackgroundColor(getResources().getColor(
				android.R.color.white));
		mTab3.setImageResource(R.drawable.chat_history);

	}

	public void disableThirdTab() {

		mTab1.setEnabled(true);
		mTab2.setEnabled(true);
		mTab3.setEnabled(false);

		mTab1Lyt.setEnabled(true);
		mTab2Lyt.setEnabled(true);
		mTab3Lyt.setEnabled(false);

		mTab1Txt.setTextColor(getResources().getColor(
				R.color.text_color_fadedgreen));
		mTab2Txt.setTextColor(getResources().getColor(
				R.color.text_color_fadedgreen));
		mTab3Txt.setTextColor(getResources().getColor(android.R.color.white));

		mTab1Lyt.setBackgroundColor(getResources().getColor(
				android.R.color.white));
		mTab1.setImageResource(R.drawable.convertor);

		mTab2Lyt.setBackgroundColor(getResources().getColor(
				android.R.color.white));
		mTab2.setImageResource(R.drawable.near_by_matches);

		mTab3Lyt.setBackgroundColor(getResources().getColor(
				R.color.text_color_fadedgreen));
		mTab3.setImageResource(R.drawable.chat_history_h);

	}

	public void enableAllTabs() {
		mTab1.setEnabled(true);
		mTab2.setEnabled(true);
		mTab3.setEnabled(true);

	}

	public void slideBack() {

		getFragmentManager().popBackStack();

	}

	public void setHeaderTitle(String title) {

		mHeaderTitle.setText(title);
	}

	public void showProgress() {
		((LinearLayout) findViewById(R.id.loading_view))
				.setVisibility(View.VISIBLE);
	}

	public void hideProgress() {
		((LinearLayout) findViewById(R.id.loading_view))
				.setVisibility(View.GONE);
	}

	@Override
	public void onbtnDismissClicked() {
		hideFloatingMenu();
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {

		hideProgress();
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {

		switch (requestCode) {
		case CHECK_HIGHER_RATING:
			try {
				JSONObject mSettingSetHighrating = new JSONObject(
						response.getString("settings"));
				mSettingSetHighrating.getString("message");
				if (mSettingSetHighrating.getString("success")
						.equalsIgnoreCase("1")) {

					JSONArray mData = new JSONArray(response.getString("data"));

					JSONObject mUserDetails = mData.getJSONObject(0);
					mUserDetails.getString("zip_code");
					mUserDetails.getString("email");
					mUserDetails.getString("city");
					mUserDetails.getString("address");
					mUserDetails.getString("phone_no");
					mUserDetails.getString("first_name");
					mUserDetails.getString("last_name");
					mUserDetails.getString("state");
					mUserDetails.getString("country");

					hideProgress();

					if (mUserDetails.getString("status").equalsIgnoreCase(
							"None")) {
						gotoSetHigherRatingRequestPINFragment();
					} else if (mUserDetails.getString("status")
							.equalsIgnoreCase("Requested")) {
						gotoSetHigherRatingFragment();
					} else if (mUserDetails.getString("status")
							.equalsIgnoreCase("Activated")
							|| mUserDetails.getString("status")
									.equalsIgnoreCase("Confirmed")) {
						// Validations.showAlerDialog(
						// mSettingSetHighrating.getString("message"),
						// MainActivity.this);
						Validations.showSingleBtnDialog(
								mSettingSetHighrating.getString("message"),
								MainActivity.this);

					}
				}

				else {

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

			break;

		case I_AM_LOOKING:

			try {
				JSONObject mCountryInfo = new JSONObject(
						response.getString("settings"));
				if (mCountryInfo.getString("success").equalsIgnoreCase("1")) {
					JSONArray mArray = new JSONArray(response.getString("data"));
					addILookForToList(mArray);
					addIHaveToList(mArray);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;

		case SET_PREFERENCES:

			String mSetPreferenceMsg = "";
			try {
				JSONObject mSettingSetPrefernces = new JSONObject(
						response.getString("settings"));
				mSetPreferenceMsg = mSettingSetPrefernces.getString("message");
				hideFloatingMenu();
				if (mSettingSetPrefernces.getString("success")
						.equalsIgnoreCase("1")) {
					// showAlerDialog(mSetPreferenceMsg, this);
					Validations.showSingleBtnDialog(mSetPreferenceMsg, this);

					// goto an page where you need!!!!!
				} else {
					// showAlerDialog(mSetPreferenceMsg, this);
					Validations.showSingleBtnDialog(mSetPreferenceMsg, this);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
			break;

		default:
			break;
		}
		hideProgress();
	}

	private void addIHaveToList(JSONArray mArray) {

		mPreferenceList = new ArrayList<PreferencesSetDetails>();

		for (int i = 0; i < mArray.length(); i++) {

			JSONObject mPreferenceObject = new JSONObject();
			try {
				mPreferenceObject = mArray.getJSONObject(i);
				PreferencesSetDetails mPreference = new PreferencesSetDetails(
						mPreferenceObject);
				mPreferenceList.add(mPreference);

			} catch (JSONException e) {

				e.printStackTrace();
			}

		}
		mIhaveItems = new String[mPreferenceList.size()];
		_IHaveItemsselection = new boolean[mPreferenceList.size()];

	}

	private void addILookForToList(JSONArray mArray) {

		mPreferenceListILook = new ArrayList<PreferencesSetDetails>();

		for (int i = 0; i < mArray.length(); i++) {

			JSONObject mPreferenceObject = new JSONObject();
			try {
				mPreferenceObject = mArray.getJSONObject(i);
				PreferencesSetDetails mPreference = new PreferencesSetDetails(
						mPreferenceObject);
				mPreferenceListILook.add(mPreference);

			} catch (JSONException e) {

				e.printStackTrace();
			}
		}

		mIAmLookingForItems = new String[mPreferenceListILook.size()];
		_IAmLookingselection = new boolean[mPreferenceListILook.size()];

	}

	public void showIHaveFromPopUP(final TextView mIHaveTextView) {

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

		// if (cnt.size() == 0) {
		for (int i = 0; i < mPreferenceList.size(); i++) {
			mIhaveItems[i] = mPreferenceList.get(i).getPreferencesName();

			cnt.add(mPreferenceList.get(i).getPreferencesName());

			// }

		}

		builder.setMultiChoiceItems(mIhaveItems, _IHaveItemsselection,
				new DialogInterface.OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						selected = which;
						_IHaveItemsselection[which] = isChecked;

					}
				});

		builder.setPositiveButton(R.string.done,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						String mSelectedItemStrings = "";
						mIHaveSeqNum = null;
						for (int i = 0; i < mIhaveItems.length; i++) {
							if (_IHaveItemsselection[i]) {

								mSelectedItemStrings = mSelectedItemStrings
										+ mIhaveItems[i] + ", ";

								if (TextUtils.isEmpty(mIHaveSeqNum)) {
									mIHaveSeqNum = mPreferenceList.get(i)
											.getPreferencesSeqNum();
								} else {
									mIHaveSeqNum = mIHaveSeqNum
											+ ","
											+ mPreferenceList.get(i)
													.getPreferencesSeqNum();
								}
							}

						}

						String mSelectedCurrency = removeLastChar(mSelectedItemStrings);
						mIHaveTextView.setText(mSelectedCurrency);

						// Save Data
						SharedPreferences.Editor editor = userDetails.edit();
						editor.putString("iHaveCurrencyName", mSelectedCurrency);
						editor.putString("iHaveCurrencyCode", mIHaveSeqNum);

						for (int i = 0; i < _IHaveItemsselection.length; i++) {
							editor.putBoolean("iHaveCurrencyBoolArray" + i,
									_IHaveItemsselection[i]);

							Log.i("BOOOOOOL", _IHaveItemsselection[i] + "");
						}
						editor.putLong("iHaveCurrencyLenght",
								_IHaveItemsselection.length);

						editor.commit();

						if (TextUtils.isEmpty(mIHaveSeqNum)) {

							mIHaveTextView.setText(getString(R.string.i_have));
						}

						dialog.dismiss();

					}
				});

		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.setCancelable(true);

		AlertDialog alert = builder.create();
		if (cnt.size() > 0)
			alert.show();

	}

	public void showILookForPopUP(final TextView mILookTextView) {

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

		// if (cntLook.size() == 0) {
		for (int i = 0; i < mPreferenceListILook.size(); i++) {

			mIAmLookingForItems[i] = mPreferenceListILook.get(i)
					.getPreferencesName();
			cntLook.add(mPreferenceListILook.get(i).getPreferencesName());
		}
		// }

		builder.setMultiChoiceItems(mIAmLookingForItems, _IAmLookingselection,
				new DialogInterface.OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						selected = which;
						_IAmLookingselection[which] = isChecked;

					}
				});

		builder.setPositiveButton(R.string.done,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						String mSelectedItemStrings = "";
						mILookForSeqNum = null;
						for (int i = 0; i < mIAmLookingForItems.length; i++) {
							if (_IAmLookingselection[i]) {

								mSelectedItemStrings = mSelectedItemStrings
										+ mIAmLookingForItems[i] + ", ";

								if (TextUtils.isEmpty(mILookForSeqNum)) {
									mILookForSeqNum = mPreferenceListILook.get(
											i).getPreferencesSeqNum();
								} else {

									mILookForSeqNum = mILookForSeqNum
											+ ","
											+ mPreferenceListILook.get(i)
													.getPreferencesSeqNum();
								}
							}
						}

						String mSelectedCurrency = removeLastChar(mSelectedItemStrings);
						mILookTextView.setText(mSelectedCurrency);

						// Save Data
						SharedPreferences.Editor editor = userDetails.edit();
						editor.putString("iAmLookingForCurrencyName",
								mSelectedCurrency);
						editor.putString("iAmLookingForCurrencyCode",
								mILookForSeqNum);

						for (int i = 0; i < _IAmLookingselection.length; i++) {
							editor.putBoolean("iAmLookingForBoolArray" + i,
									_IAmLookingselection[i]);

							Log.i("iamlook", _IAmLookingselection[i] + "");
						}
						editor.putLong("iAmLookingForCurrencyLength",
								_IAmLookingselection.length);

						editor.commit();

						if (TextUtils.isEmpty(mILookForSeqNum)) {

							mILookTextView
									.setText(getString(R.string.i_am_looking_for));
						}

						dialog.dismiss();

					}
				});

		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.setCancelable(true);

		AlertDialog alert = builder.create();
		if (cntLook.size() > 0)
			alert.show();

	}

	public void callSetPreferencesWS() {

		showProgress();

		Bundle mBundle = new Bundle();

		if (mReceiveNotificaion.isChecked()) {
			mBundle.putString("online_status", "Yes");
		} else {
			mBundle.putString("online_status", "No");
		}

		if (mVisibility.isChecked()) {
			mBundle.putString("notification", "Yes");
		} else {
			mBundle.putString("notification", "No");
		}

		mBundle.putString("user_id", user_id);
		mBundle.putString("preference_i_have", mIHaveSeqNum);
		mBundle.putString("preference_look_for", mILookForSeqNum);
		mBundle.putString("range_value", mProgressVal);

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.SET_PREFERENCES, mBundle),
				SET_PREFERENCES, this);

	}

	public String removeLastChar(String s) {
		if (s == null || s.length() == 0) {
			return s;
		}
		return s.substring(0, s.length() - 2);
	}

	public void validatePreferences() {

		if (TextUtils.isEmpty(mIHaveSeqNum)
				|| TextUtils.isEmpty(mILookForSeqNum)) {
			// Validations.showAlerDialog(
			// getString(R.string.please_select_preferences), this);
			Validations.showSingleBtnDialog(
					getString(R.string.please_select_preferences), this);

		} else {
			isShowPreference = true;

			callSetPreferencesWS();

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			MyProfileFragment.getInstance().onActivityResult(requestCode,
					resultCode, data);
		} else {

		}

	}

	//

}