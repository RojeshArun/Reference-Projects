package com.hb.barter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.jivesoftware.smackx.packet.VCard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twitter.HBTwitterUtility;
import org.twitter.HBTwitterUtility.TwitterCallbackListener;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.barter.xmpp.ConnectionService;
import com.barter.xmpp.HBXMPP;
import com.hb.baseapplication.BaseApplication;
import com.hb.webserviceutilities.GetSocialDetails;
import com.hb.webserviceutilities.GetSocialDetails.FBUserCallback;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

public class LoginScreen extends FragmentActivity implements OnClickListener,
		IParseListener, FBUserCallback, TwitterCallbackListener {

	private static final int IS_FBLOGIN = 301;
	private static final int IS_TWITTERLOGIN = 302;

	private EditText mEmailAddress, mPassword;
	private TextView mForgotPassword, mLoginButton, mNewUser, mBarterLink;

	private HBTwitterUtility hbTwitterUtility;

	// Header Variable
	private TextView mTitle;
	private ImageView mBackButton;

	private LinearLayout mloadingLayout;

	private SharedPreferences userDetails;

	private HBXMPP hbxmpp;
	private BaseApplication xmppDemoApp;

	private static final int LOGIN = 100;

	private static final int FORGET_PASSWORD = 101;

	private static final int FACEBOOK_LOGIN = 102;

	public static final int PORT = 5222;

	private ImageButton mLoginWithFB, mLoginWithTwitter;
	private Boolean isFromMainActivity = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		Validations.setMenuWidthHeightProportions(this);

		if (getIntent().getExtras() != null) {
			isFromMainActivity = getIntent().getExtras().getBoolean(
					"isFromMainAcitivty");

			if (isFromMainActivity) {
				overridePendingTransition(R.anim.enter_from_left,
						R.anim.exit_to_right);
			} else {
				overridePendingTransition(R.anim.enter_from_right,
						R.anim.exit_to_left);
			}
		}

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		setContentView(R.layout.activity_login);

		setTheAcitivty();
		setOnClickListener();
	}

	private void setTheAcitivty() {

		userDetails = PreferenceManager.getDefaultSharedPreferences(this);

		xmppDemoApp = (BaseApplication) getApplication();
		hbxmpp = new HBXMPP(Webservices.HOST, PORT, Webservices.SERVICE);

		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.login);

		mBackButton = (ImageView) findViewById(R.id.slidedown);
		mBackButton.setVisibility(View.INVISIBLE);

		mEmailAddress = (EditText) findViewById(R.id.edtText_EmailLogin);

		mPassword = (EditText) findViewById(R.id.edtText_passWord);
		// editor.putString("login_email_id", mEmailAddressString);
		// editor.putString("login_password", mPasswordString);
		if (!TextUtils.isEmpty(userDetails.getString("login_email_id", ""))) {
			mEmailAddress.setText(userDetails.getString("login_email_id", ""));
			mPassword.setText(userDetails.getString("login_password", ""));
		}

		mForgotPassword = (TextView) findViewById(R.id.txtView_ForgetPasswordLogin);
		mLoginButton = (TextView) findViewById(R.id.txtView_LoginTextButton);

		mNewUser = (TextView) findViewById(R.id.txtView_NewUserClickHere);
		mNewUser.setOnClickListener(this);

		mloadingLayout = (LinearLayout) findViewById(R.id.loadingllyt);

		mLoginWithFB = (ImageButton) findViewById(R.id.imgBtn_FaceBook);
		mLoginWithTwitter = (ImageButton) findViewById(R.id.imgBtn_Twitter);

		hbTwitterUtility = new HBTwitterUtility(this,
				Webservices.TWITTERCONSUMERKEY,
				Webservices.TWITTERCONSUMERSECRETKEY, this, false);

		mBarterLink = (TextView) findViewById(R.id.barterwebsitelink);
		mBarterLink.setOnClickListener(this);

	}

	private void setOnClickListener() {

		mForgotPassword.setOnClickListener(this);
		mLoginButton.setOnClickListener(this);

		mLoginWithFB.setOnClickListener(this);
		mLoginWithTwitter.setOnClickListener(this);

	}

	private GetSocialDetails mSocial;

	@Override
	public void onClick(View v) {

		mSocial = new GetSocialDetails(this);

		switch (v.getId()) {
		case R.id.txtView_ForgetPasswordLogin:
			showForgotPasswordPopUP();
			break;
		case R.id.txtView_LoginTextButton:

			if (Validations.isNetworkAvailable(this)) {
				validateLogin();
			} else {
				Validations.showSingleBtnDialog(
						"No internet Connection.Please Try again later", this);

				// Validations.showAlerDialog(
				// "No internet Connection.Please Try again later", this);
			}

			break;
		case R.id.txtView_NewUserClickHere:
			gotoCreateNewUserScreen();
			break;

		// If user click on facebook then open the Fb login page
		case R.id.imgBtn_FaceBook:
			// checkFBUser
			if (Validations.isNetworkAvailable(this)) {
				mSocial.setFBUserCallback(this);
				mSocial.getAndPostFaceBookUserDetails();
			} else {
				// Validations.showAlerDialog(
				// "No internet Connection.Please Try again later", this);
				Validations.showSingleBtnDialog(
						"No internet Connection.Please Try again later", this);
			}

			break;
		// If user click on Twitter then open the Twitter login page
		case R.id.imgBtn_Twitter:

			if (Validations.isNetworkAvailable(this)) {
				hbTwitterUtility.getUserDetails("1111");
			} else {
				// Validations.showAlerDialog(
				// "No internet Connection.Please Try again later", this);
				Validations.showSingleBtnDialog(
						"No internet Connection.Please Try again later", this);
			}
			break;
		case R.id.barterwebsitelink:
			gotoBarterWebsite();
			break;
		default:
			break;
		}
	}

	private void gotoBarterWebsite() {

		Intent browserIntent = new Intent("android.intent.action.VIEW",
				Uri.parse("http://www.gobartr.net/"));
		startActivity(browserIntent);

	}

	private void callIsTwitterLoggedIN(String twitterId) {
		// TODO Auto-generated method stub

		mloadingLayout.setVisibility(View.VISIBLE);
		Bundle mParams = new Bundle();
		mParams.putString("twitterid", twitterId);

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.IS_TWITTER_LOGIN, mParams),
				IS_TWITTERLOGIN, this);

	}

	private String fbId;
	private String email;
	private String accessToken;
	private String name;

	private void callIsFBUsers(String fbId, String email, String accessToken,
			String name, String mImageUrl) {
		// TODO Auto-generated method stub
		// http://local.configure.it/locationbasedchatapp/WS/is_fb_alredy/?
		// &email=devesh.pandy454a1@hiddenbrains.in

		mloadingLayout.setVisibility(View.VISIBLE);

		Bundle mParams = new Bundle();
		mParams.putString("email", email);
		mParams.putString("udid", Validations.udid);
		mParams.putString("device_token", Validations.deviceToken);

		this.fbId = fbId;
		this.email = email;
		this.accessToken = accessToken;
		this.name = name;

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.IS_FB_LOGIN, mParams),
				IS_FBLOGIN, this);

		//

	}

	private void gotoCreateNewUserScreen() {

		Intent mIntent = new Intent(LoginScreen.this,
				DisplayCreatedAccountScreen.class);

		mIntent.putExtra("isFrom", "Application");

		startActivity(mIntent);

	}

	private void validateLogin() {

		String mEmailAddressString, mPasswordString;

		mEmailAddressString = mEmailAddress.getText().toString().trim();
		mPasswordString = mPassword.getText().toString().trim();

		if (TextUtils.isEmpty(mEmailAddressString)) {

			// Validations.showAlerDialog(
			// getString(R.string.please_enter_email_address), this);
			Validations.showSingleBtnDialog(
					getString(R.string.please_enter_email_address), this);
			mEmailAddress.setFocusable(true);

		} else if (!Validations.isValidEmail(mEmailAddressString)) {
			// Validations.showAlerDialog(
			// getString(R.string.please_enter_valid_email_address), this);
			Validations.showSingleBtnDialog(
					getString(R.string.please_enter_valid_email_address), this);
			mEmailAddress.setFocusable(true);
		} else if (TextUtils.isEmpty(mPasswordString)) {
			Validations.showSingleBtnDialog(
					getString(R.string.please_enter_password), this);
			// Validations.showAlerDialog(
			// getString(R.string.please_enter_password), this);
			mPassword.setFocusable(true);
		} else {

			callLoginWebservice(mEmailAddressString, mPasswordString);
		}

	}

	private void callLoginWebservice(String mEmailAddressString,
			String mPasswordString) {

		mloadingLayout.setVisibility(View.VISIBLE);

		Bundle mBundle = new Bundle();

		mBundle.putString("email", mEmailAddressString);
		mBundle.putString("password", mPasswordString);
		mBundle.putString("platform", "Android");
		mBundle.putString("udid", Validations.udid);
		mBundle.putString("device_token",
				userDetails.getString("RegistrationId", ""));

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.LOGIN, mBundle), LOGIN, this);

		// Save Login Credentials
		SharedPreferences.Editor editor = userDetails.edit();
		editor.putString("login_email_id", mEmailAddressString);
		editor.putString("login_password", mPasswordString);
		editor.commit();

	}

	private boolean onlyOneTime = true;

	private void gotoMainActivity() {

		SharedPreferences.Editor mEditor = userDetails.edit();
		mEditor.putBoolean("isLoggedin", false);
		mEditor.commit();

		Intent mConnectionService = new Intent(LoginScreen.this,
				ConnectionService.class);
		mConnectionService.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startService(mConnectionService);

		mloadingLayout.setVisibility(View.GONE);

		if (onlyOneTime) {
			Intent mIntent = new Intent(LoginScreen.this, MainActivity.class);
			mIntent.putExtra("fromSignUP", false);
			startActivity(mIntent);
			onlyOneTime = false;
		}
		finish();
	}

	private Dialog mForgotPassowrdDialog;
	private String twitterId;

	private void showForgotPasswordPopUP() {

		mForgotPassowrdDialog = new Dialog(LoginScreen.this,
				R.style.AlertDialogCustom);
		mForgotPassowrdDialog.setCancelable(true);
		mForgotPassowrdDialog.setTitle(R.string.forgot_password);
		mForgotPassowrdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mForgotPassowrdDialog.setContentView(R.layout.popup_forgotpassword);

		mForgotPassowrdDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = mForgotPassowrdDialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

		final EditText mEmailForgotPassword = (EditText) mForgotPassowrdDialog
				.findViewById(R.id.forgotpassword_emailaddress);

		Button btnSubmit = (Button) mForgotPassowrdDialog
				.findViewById(R.id.btn_popupSubmit);
		Button btnCancel = (Button) mForgotPassowrdDialog
				.findViewById(R.id.btn_popupCancel);

		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				validateForgotPassword(mEmailForgotPassword.getText()
						.toString().trim());

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mForgotPassowrdDialog.dismiss();
				Validations.hideKeyboard(LoginScreen.this);

			}
		});

		mForgotPassowrdDialog.show();
		Validations.showKeyboard(this);

	}

	protected void validateForgotPassword(String mEmailAddress) {

		if (!Validations.isValidEmail(mEmailAddress)) {
			// Validations.showAlerDialog("Please enter valid email id", this);
			Validations
					.showSingleBtnDialog("Please enter valid email id", this);

		} else {
			mForgotPassowrdDialog.dismiss();
			Validations.hideKeyboard(LoginScreen.this);
			if (Validations.isNetworkAvailable(LoginScreen.this)) {
				callForgotPasswordWS(mEmailAddress);
			} else {
				Validations
						.showSingleBtnDialog(
								getResources()
										.getString(
												R.string.no_internet_connection_please_try_again),
								LoginScreen.this);
				// Validations
				// .showAlerDialog(
				// getResources()
				// .getString(
				// R.string.no_internet_connection_please_try_again),
				// LoginScreen.this);
			}
		}
	}

	private void callForgotPasswordWS(String mEmailAddress) {

		mloadingLayout.setVisibility(View.VISIBLE);

		Bundle mBundle = new Bundle();

		mBundle.putString("email_id", mEmailAddress);
		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		mRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.FORGOT_PASSWORD, mBundle),
				FORGET_PASSWORD, this);

	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		mloadingLayout.setVisibility(View.GONE);
		Validations.showSingleBtnDialog(
				"Something went wrong, Try sometime later", this);
		// Validations.showAlerDialog("Something went wrong, Try sometime later",
		// this);
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case LOGIN:
			try {
				JSONObject mSettingObject = new JSONObject(
						response.getString("settings"));

				if (mSettingObject.getString("success").equalsIgnoreCase("1")) {

					saveChatServerDetails(response);

					LoginToXmppServer();

				} else {
					mloadingLayout.setVisibility(View.GONE);
					Validations.showSingleBtnDialog(
							mSettingObject.getString("message"), this);
					// Validations.showAlerDialog(
					// mSettingObject.getString("message"), this);

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}
			break;

		case FORGET_PASSWORD:
			String mForgotPassword = "";
			try {
				JSONObject mSettingForgetPassword = new JSONObject(
						response.getString("settings"));
				mForgotPassword = mSettingForgetPassword.getString("message");
				if (mSettingForgetPassword.getString("success")
						.equalsIgnoreCase("1")) {
					// showAlerDialog(mForgotPassword, LoginScreen.this);
					Validations.showSingleBtnDialog(mForgotPassword,
							LoginScreen.this);
				}
			} catch (JSONException e) {
				// showAlerDialog(mForgotPassword, LoginScreen.this);
				Validations.showSingleBtnDialog(mForgotPassword,
						LoginScreen.this);
				e.printStackTrace();
			}
			mloadingLayout.setVisibility(View.GONE);
			break;

		case FACEBOOK_LOGIN: {

			gotoDisplayCreatedAccout(response);

		}
			break;

		case IS_FBLOGIN: {
			try {

				JSONObject mJsonObject = new JSONObject(
						response.getString("settings"));

				if (mJsonObject.getString("success").equalsIgnoreCase("0")) {

					callLoginwithFacebook(fbId, email, accessToken, name);

				} else {
					saveFacebookDetails1(response);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				mloadingLayout.setVisibility(View.GONE);
			}

		}
			break;

		case IS_TWITTERLOGIN: {
			try {

				JSONObject mJsonObject = new JSONObject(
						response.getString("settings"));

				if (mJsonObject.getString("success").equalsIgnoreCase("0")) {
					gotoDisplayCreateAccountFromTwitter();
				} else {
					// save Twitter Details and goto main screen
					saveTwitterDetails(response);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			mloadingLayout.setVisibility(View.GONE);
		}

		default:
			break;
		}

	}

	private void gotoDisplayCreateAccountFromTwitter() {

		Intent mIntent = new Intent(LoginScreen.this,
				DisplayCreatedAccountScreen.class);
		mIntent.putExtra("isFrom", "Twitter");
		mIntent.putExtra("displayname", name);
		mIntent.putExtra("twitterID", mTwitterId);

		startActivity(mIntent);

	}

	private void gotoDisplayCreatedAccout(JSONObject response) {
		SharedPreferences.Editor editor = userDetails.edit();

		try {
			JSONObject mJsonObject = new JSONObject(
					response.getString("settings"));
			if (mJsonObject.getString("success").equalsIgnoreCase("1")) {

				JSONObject mDataObj = new JSONObject(response.getString("data"));

				JSONArray mJsonArray = mDataObj.getJSONArray("check_exists");
				JSONObject mJsonObject2 = mJsonArray.getJSONObject(0);
				{
					// Store User Visibilty

					editor.putString("user_visible",
							mJsonObject2.getString("user_visible"));

				}

				JSONArray mJsonArray1 = mDataObj
						.getJSONArray("jabber_function");
				JSONObject mdataobject = mJsonArray1.getJSONObject(0);
				{
					// Store Jabber details
					editor.putString("Loggedin_jabber_id",
							mdataobject.getString("jabber_id"));
					editor.putString("Loggedin_jabber_password",
							mdataobject.getString("password"));

				}

				// JSONArray mJsonArray11 =
				// mDataObj.getJSONArray("insert_user");
				JSONObject mdataobject1 = mDataObj.getJSONObject("insert_user");
				{
					// Store User id
					editor.putString("Loggedin_userid",
							mdataobject1.getString("user_id"));

				}

				editor.commit();

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent mIntent = new Intent(LoginScreen.this,
				DisplayCreatedAccountScreen.class);
		mIntent.putExtra("isFrom", "Facebook");

		mIntent.putExtra("displayname", name);
		mIntent.putExtra("displayemail", email);
		mIntent.putExtra("firstname", firstname);
		mIntent.putExtra("lastname", lastname);
		mIntent.putExtra("imageURL", mImageUrl);

		startActivity(mIntent);

	}

	private void saveChatTwitterDetails(JSONObject response) {
		try {
			JSONArray mDataObj = new JSONArray(response.getString("data"));

			JSONObject mdataobject = mDataObj.getJSONObject(0);

			SharedPreferences.Editor editor = userDetails.edit();
			editor.putString("Loggedin_jabber_id",
					mdataobject.getString("jabber_id"));
			// editor.putString("Loggedin_jabber_password",
			// mdataobject.getString("jabber_password"));

			editor.putString("Loggedin_jabber_password", "123123");

			editor.putString("Loggedin_userid",
					mdataobject.getString("user_id"));
			editor.putString("UserLatitude", mdataobject.getString("latitude"));
			editor.putString("UserLongitude",
					mdataobject.getString("longitude"));
			editor.putString("profile_image",
					mdataobject.getString("profile_image"));

			String locationUpdateRate = null;

			if (mdataobject.has("location_update_rate"))
				locationUpdateRate = mdataobject
						.getString("location_update_rate");

			if (TextUtils.isEmpty(locationUpdateRate)) {
				editor.putInt("location_update_rate", 15000);
			} else {
				editor.putInt("location_update_rate",
						Integer.parseInt(locationUpdateRate)*1000);

			}

			editor.commit();
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	private void saveChatServerDetails(JSONObject response) {
		try {
			JSONArray mDataObj = new JSONArray(response.getString("data"));

			JSONObject mdataobject = mDataObj.getJSONObject(0);

			SharedPreferences.Editor editor = userDetails.edit();
			editor.putString("Loggedin_jabber_id",
					mdataobject.getString("jabber_id"));
			// editor.putString("Loggedin_jabber_password",
			// mdataobject.getString("jabber_password"));

			editor.putString("Loggedin_jabber_password", "123123");
			editor.putString("Loggedin_userid",
					mdataobject.getString("user_id"));
			// editor.putString("Loggedin_userid",
			// "400");
			editor.putString("UserLatitude",
					mdataobject.getString("current_latitude"));
			editor.putString("UserLongitude",
					mdataobject.getString("current_longitude"));
			editor.putString("profile_image",
					mdataobject.getString("profile_image"));
			editor.putString("i_have_currency_id",
					mdataobject.getString("preference_have_id"));
			editor.putString("i_want_currency_id",
					mdataobject.getString("preference_look_id"));

			String locationUpdateRate = null;

			if (mdataobject.has("location_update_rate"))
				locationUpdateRate = mdataobject
						.getString("location_update_rate");

			if (TextUtils.isEmpty(locationUpdateRate)) {
				editor.putInt("location_update_rate", 15000);
			} else {
				editor.putInt("location_update_rate",
						Integer.parseInt(locationUpdateRate)*1000);
			}

			if (mdataobject.has("preference_have_string"))
				editor.putString("i_have_currency",
						mdataobject.getString("preference_have_string"));
			if (mdataobject.has("preference_look_string"))
				editor.putString("i_want_currency",
						mdataobject.getString("preference_look_string"));
			editor.commit();
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	private void saveChatServerDetails1(JSONObject response) {
		try {

			SharedPreferences.Editor mEditor = userDetails.edit();
			JSONArray mDataObj = new JSONArray(response.getString("data"));

			JSONObject mdataobject = mDataObj.getJSONObject(0);

			SharedPreferences.Editor editor = userDetails.edit();
			editor.putString("Loggedin_jabber_id",
					mdataobject.getString("jabber_id"));
			// editor.putString("Loggedin_jabber_password",
			// mdataobject.getString("jabber_password"));

			editor.putString("Loggedin_jabber_password", "123123");
			editor.putString("Loggedin_userid",
					mdataobject.getString("user_id"));
			editor.putString("UserLatitude", mdataobject.getString("latitude"));
			editor.putString("UserLongitude",
					mdataobject.getString("longitude"));
			editor.putString("profile_image",
					mdataobject.getString("profile_image"));

			editor.putString("i_have_currency_id",
					mdataobject.getString("preference_have_id"));
			editor.putString("i_want_currency_id",
					mdataobject.getString("preference_look_id"));
			
			String locationUpdateRate = null;

			if (mdataobject.has("location_update_rate"))
				locationUpdateRate = mdataobject
						.getString("location_update_rate");

			if (TextUtils.isEmpty(locationUpdateRate)) {
				editor.putInt("location_update_rate", 15000);
			} else {
				editor.putInt("location_update_rate",
						Integer.parseInt(locationUpdateRate)*1000);
			}
			
			
			editor.commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void saveFacebookDetails(JSONObject response) {

		saveChatServerDetails(response);

		// LoginToXmppServer();

		gotoCreateNewUserScreen();
	}

	private void saveTwitterDetails(JSONObject response) {
		// TODO Auto-generated method stub
		saveChatTwitterDetails(response);
		LoginToXmppServer();
	}

	private void saveFacebookDetails1(JSONObject response) {
		saveChatServerDetails1(response);
		LoginToXmppServer();
	}

	private void LoginToXmppServer() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean isConnected = hbxmpp.connect();
				if (isConnected) {
					try {
						hbxmpp.login(userDetails.getString(
								"Loggedin_jabber_id", ""), userDetails
								.getString("Loggedin_jabber_password", ""));
						if (hbxmpp.getConnection().isAuthenticated()) {
							Log.e("Logged in user", hbxmpp.getConnection()
									.getUser());
							xmppDemoApp.setHbxmpp(hbxmpp);

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							uploadAvatar(
									userDetails
											.getString("Loggedin_userid", "")
											+ Webservices.CHAT_DOMAIN,
									userDetails.getString("profile_image", ""));

						}
					});
				}
			}
		}).start();
	}

	private void uploadAvatar(String jabberId, String url) {

		GetProfileBitMap mAsyncGetBitMap = new GetProfileBitMap(url);
		mAsyncGetBitMap.execute();

		// if (largeIcon != null) {
		//
		// try {
		//
		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// largeIcon.compress(Bitmap.CompressFormat.PNG, 100, stream);
		// byte[] array = stream.toByteArray();
		//
		// VCard vCard = new VCard();
		// vCard.load(hbxmpp.getConnection());
		// vCard.setFirstName(str_firstname);
		// vCard.setLastName(str_lastname);
		// vCard.setEmailHome(str_emailid);
		// vCard.setOrganization("SCLI");
		// vCard.setField("Phone", str_phonenumber);
		// vCard.setPhoneHome("Mobile", str_phonenumber);
		// vCard.setAvatar(array);
		// vCard.save(hbxmpp.getConnection());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// }
		//
		// mHandler.postDelayed(mRunnable, 0);
		//
		// showAlertMessage(SignUpScreenActivity.this,
		// getString(R.string.registeredsuccessfully));
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

			Intent mConnectionService = new Intent(LoginScreen.this,
					ConnectionService.class);
			mConnectionService.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			stopService(mConnectionService);

			gotoMainActivity();

		}

	}

	String fbuserData;
	String firstname, lastname, mImageUrl;
	private String mTwitterId;

	// Call the login webservice with facebook id , email
	private void callLoginwithFacebook(String fbId, String email,
			String accessToken, String name) {

		// http://local.configure.it/locationbasedchatapp/WS/fb_login/?
		// &email=patric123@ipsen.com
		// &facebookid=99999
		// &facebook_oauth_token=99999
		// &udid=9889999
		// &display_name=patric%20ipsen
		// &device_token=99999
		// &platform=iOS

		Bundle mParam = new Bundle();
		mParam.putString("facebookid", fbId);
		mParam.putString("email", email);
		mParam.putString("facebook_oauth_token", accessToken);
		mParam.putString("platform", "ANDROID");
		mParam.putString("udid", Validations.udid);
		mParam.putString("device_token",
				userDetails.getString("RegistrationId", ""));
		mParam.putString("display_name", name);

		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		mRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.FACEBOOKLOGIN, mParam),
				FACEBOOK_LOGIN, this);

		// Reset Shared Preferences

		SharedPreferences.Editor mEditor = userDetails.edit();

		mEditor.clear();
		mEditor.commit();

	}

	@Override
	public void getResult(String result, String accessToken) {
		Log.d("GetFbdetails", result);
		fbuserData = result;
		try {
			JSONObject mJsonObject = new JSONObject(result);
			String fbId = mJsonObject.getString("id");

			if (mJsonObject.has("email")) {
				String email = mJsonObject.getString("email");
				name = mJsonObject.getString("name");
				firstname = mJsonObject.getString("first_name");
				lastname = mJsonObject.getString("last_name");
				mImageUrl = mSocial.userFBProfilePic(fbId);

				callIsFBUsers(fbId, email, accessToken, name, mImageUrl);

			} else {

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void twitterCallback(boolean arg0, String response, String arg2,
			ArrayList<Object> arg3) {
		// TODO Auto-generated method stub

		if (arg2.equalsIgnoreCase("1111")) {

			JSONArray mJsonArray;
			try {
				mJsonArray = new JSONArray(response.toString());
				JSONObject mJsonObject = mJsonArray.getJSONObject(0);

				mTwitterId = mJsonObject.getString("id_str");
				name = mJsonObject.getString("name");

				callIsTwitterLoggedIN(mTwitterId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			//

		}

	}

}
