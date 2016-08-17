package com.hb.barter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

public class DisplayCreatedAccountScreen extends FragmentActivity implements
		OnClickListener, IParseListener {

	private EditText mEdtText_DiplayName, mEdtText_DisplayEmailId,
			mEdtText_DisplayPassword, mEdtText_DisplayRePassword;

	private RelativeLayout mEmailIdLayout, mPasswordLayout, mRePasswordLayout;

	private TextView mNextTextView;

	private static final int REGISTRATION = 101;
	public static final int PORT = 5222;
	Boolean isFromCreateAccout = false;
	private static final int TWITTER_LOGIN = 103;

	SharedPreferences mUserDetails;

	private SharedPreferences userDetails;

	// Header Variable
	private TextView mTitle;
	private ImageView mBackButton, mRightMostButton;
	private LinearLayout mloadingLayout;

	private String isFromFlag = "";
	private String firstName, lastName, imgURL;

	private String twitterId;

	private String displayName;

	public DisplayCreatedAccountScreen() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
		setContentView(R.layout.fragment_display_create_account);
		if ((getIntent().getExtras() != null)
				&& (getIntent().getExtras().getBoolean("isFromCreate"))) {
			overridePendingTransition(R.anim.enter_from_left,
					R.anim.exit_to_right);
		}
		userDetails = PreferenceManager.getDefaultSharedPreferences(this);

		if (!TextUtils.isEmpty(getIntent().getExtras().getString("isFrom"))) {
			isFromFlag = getIntent().getExtras().getString("isFrom");
			firstName = getIntent().getExtras().getString("firstname");
			lastName = getIntent().getExtras().getString("lastname");
			imgURL = getIntent().getExtras().getString("imageURL");
			emailaddress=getIntent().getExtras().getString("displayemail");

		}
		setDisplayCreateFragment();
	}

	private void setDisplayCreateFragment() {

		mEdtText_DiplayName = (EditText) findViewById(R.id.edtText_DisplayName);
		mEdtText_DisplayEmailId = (EditText) findViewById(R.id.edtText_EmailId_Create_Account);
		mEdtText_DisplayPassword = (EditText) findViewById(R.id.edtText_password_create_account);
		mEdtText_DisplayRePassword = (EditText) findViewById(R.id.edtText_rePassword_create_account);
		mloadingLayout = (LinearLayout) findViewById(R.id.loadingllyt);
		mUserDetails = PreferenceManager.getDefaultSharedPreferences(this);
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(R.string.create_an_account);

		mNextTextView = (TextView) findViewById(R.id.txtView_nextbutton);
		mNextTextView.setVisibility(View.VISIBLE);
		mNextTextView.setOnClickListener(this);

		mEmailIdLayout = (RelativeLayout) findViewById(R.id.emailid_layout);
		mPasswordLayout = (RelativeLayout) findViewById(R.id.passowrdLayout);
		mRePasswordLayout = (RelativeLayout) findViewById(R.id.repassowrdLayout);

		if (isFromFlag.equalsIgnoreCase("Facebook")) {

			mPasswordLayout.setVisibility(View.INVISIBLE);
			mRePasswordLayout.setVisibility(View.INVISIBLE);
			mEmailIdLayout.setVisibility(View.INVISIBLE);

			displayName = getIntent().getExtras().getString("displayname");

			mEdtText_DiplayName.setText(displayName);
			// mEdtText_DisplayEmailId.setText(getIntent().getExtras().getString(
			// "displayemail"));

		} else if (isFromFlag.equalsIgnoreCase("Twitter")) {

			mPasswordLayout.setVisibility(View.INVISIBLE);
			mRePasswordLayout.setVisibility(View.INVISIBLE);

			displayName = getIntent().getExtras().getString("displayname");
			mEdtText_DiplayName.setText(getIntent().getExtras().getString(
					"displayname"));

			twitterId = getIntent().getExtras().getString("twitterID");

		} else {

		}

		mBackButton = (ImageView) findViewById(R.id.slidedown);
		mBackButton.setOnClickListener(this);

		mRightMostButton = (ImageView) findViewById(R.id.rightMostButton);
		mRightMostButton.setVisibility(View.GONE);
		mRightMostButton.setImageResource(R.drawable.next);
		mRightMostButton.setOnClickListener(this);

	}

	String emailaddress;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.slidedown:
			Intent mIntent = new Intent(DisplayCreatedAccountScreen.this,
					LoginScreen.class);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mIntent.putExtra("isFromMainAcitivty", true);
			startActivity(mIntent);
			finish();
			break;

		case R.id.txtView_nextbutton:

			if (isFromFlag.equalsIgnoreCase("Facebook")) {
				gotoCreateANewAccountScreen();
			} else if (isFromFlag.equalsIgnoreCase("Twitter")) {

				emailaddress = mEdtText_DisplayEmailId.getText().toString()
						.trim();
				if (TextUtils.isEmpty(emailaddress)) {

					// Validations.showAlerDialog(
					// getString(R.string.please_enter_email_address),
					// this);
					Validations.showSingleBtnDialog(
							getString(R.string.please_enter_email_address),
							this);
					mEdtText_DisplayEmailId.setFocusable(true);

				} else if (Validations.isValidEmail(emailaddress)) {
					callLoginwithTwitter();
				} else {
					Validations
							.showSingleBtnDialog(
									getString(R.string.please_enter_valid_email_address),
									this);
					mEdtText_DisplayEmailId.setFocusable(true);
				}
			} else {
				validateForm();
			}

			break;

		default:
			break;
		}

	}

	private void validateForm() {
		String mDisplayName, mEmailId, mPassword, mRePassword;

		mDisplayName = mEdtText_DiplayName.getText().toString().trim();
		mEmailId = mEdtText_DisplayEmailId.getText().toString().trim();
		mPassword = mEdtText_DisplayPassword.getText().toString().trim();
		mRePassword = mEdtText_DisplayRePassword.getText().toString().trim();

		if (TextUtils.isEmpty(mDisplayName)) {
			// Validations.showAlerDialog(
			// getString(R.string.please_enter_display_name), this);
			Validations.showSingleBtnDialog(
					getString(R.string.please_enter_display_name), this);
			mEdtText_DiplayName.setFocusable(true);

		} else if (TextUtils.isEmpty(mEmailId)) {
			// Validations.showAlerDialog(
			// getString(R.string.please_enter_email_id), this);
			Validations.showSingleBtnDialog(
					getString(R.string.please_enter_email_id), this);
			mEdtText_DisplayEmailId.setFocusable(true);
		} else if (!Validations.isValidEmail(mEmailId)) {
			Validations.showSingleBtnDialog(
					getString(R.string.please_enter_valid_email_id), this);
			// Validations.showAlerDialog(
			// getString(R.string.please_enter_valid_email_id), this);
			mEdtText_DisplayEmailId.setFocusable(true);
		} else if (TextUtils.isEmpty(mPassword)) {
			Validations.showSingleBtnDialog(
					getString(R.string.please_enter_password), this);
			// Validations.showAlerDialog(
			// getString(R.string.please_enter_password), this);
			mEdtText_DisplayPassword.setFocusable(true);
		} else if (mPassword.length() < 6) {
			// Validations
			// .showAlerDialog(
			// getString(R.string.password_should_contain_at_least_6_characters_),
			// this);
			Validations
					.showSingleBtnDialog(
							getString(R.string.password_should_contain_at_least_6_characters_),
							this);
			mEdtText_DisplayPassword.setFocusable(true);
		} else if (TextUtils.isEmpty(mRePassword)) {
			Validations.showSingleBtnDialog(
					getString(R.string.please_enter_re_password), this);
			// Validations.showAlerDialog(
			// getString(R.string.please_enter_re_password), this);
			mEdtText_DisplayRePassword.setFocusable(true);
		} else if (!mPassword.equalsIgnoreCase(mRePassword)) {
			Validations.showSingleBtnDialog(
					getString(R.string.password_do_not_match), this);
			// Validations.showAlerDialog(
			// getString(R.string.password_do_not_match), this);
			mEdtText_DisplayRePassword.setFocusable(true);
		} else {

			if (Validations
					.isNetworkAvailable(DisplayCreatedAccountScreen.this)) {
				callDisplayPageWS(mDisplayName, mEmailId, mPassword,
						mRePassword);
			} else {
				// Validations
				// .showAlerDialog(
				// getResources()
				// .getString(
				// R.string.no_internet_connection_please_try_again),
				// DisplayCreatedAccountScreen.this);
				Validations
						.showSingleBtnDialog(
								getResources()
										.getString(
												R.string.no_internet_connection_please_try_again),
								DisplayCreatedAccountScreen.this);
			}
		}

	}

	private void callDisplayPageWS(String mDisplayName, String mEmailId,
			String mPassword, String mRePassword) {
		// TODO Auto-generated method stub
		Validations.hideKeyboard(DisplayCreatedAccountScreen.this);
		mloadingLayout.setVisibility(View.VISIBLE);
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		Bundle mBundle = new Bundle();

		mBundle.putString("display_name", mDisplayName);
		mBundle.putString("email", mEmailId);
		mBundle.putString("password", mPassword);
		mBundle.putString("repassword", mRePassword);
		mBundle.putString("platform", "Android");
		mBundle.putString("device_token", mTelephonyMgr.getDeviceId());

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.REGISTRATION, mBundle),
				REGISTRATION, this);

		SharedPreferences.Editor mEditor = userDetails.edit();

		mEditor.clear();
		mEditor.commit();
	}

	private void gotoCreateANewAccountScreen() {
		Intent mIntent = new Intent(DisplayCreatedAccountScreen.this,
				CreateAccountScreen.class);

		if (!TextUtils.isEmpty(isFromFlag)) {

			if (isFromFlag.equalsIgnoreCase("Facebook")) {

				mIntent.putExtra("isFrom", "Facebook");
				mIntent.putExtra("firstname", firstName);
				mIntent.putExtra("lastname", lastName);
				mIntent.putExtra("imageURL", imgURL);
				mIntent.putExtra("displayname", displayName);
				mIntent.putExtra("email",
						getIntent().getExtras().getString("displayemail"));
			} else if (isFromFlag.equalsIgnoreCase("Twitter")) {
				mIntent.putExtra("isFrom", "Twitter");
				// mIntent.putExtra("", value);
				mIntent.putExtra("firstname", displayName);
				mIntent.putExtra("email", emailaddress);

			} else {
				mIntent.putExtra("isFrom", "application");
				if (mUserDetails != null) {
					mIntent.putExtra("firstname",
							mUserDetails.getString("display_user_details", ""));
					mIntent.putExtra("email",
							mUserDetails.getString("email_id", ""));
				}
			}
		}
		startActivity(mIntent);
		finish();
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		mloadingLayout.setVisibility(View.GONE);
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		String mRegisterationMsg = "";
		switch (requestCode) {
		case REGISTRATION:
			try {
				JSONObject mRegisteration = new JSONObject(
						response.getString("settings"));
				mRegisterationMsg = mRegisteration.getString("message");
				if (mRegisteration.getString("success").equalsIgnoreCase("1")) {
					SharedPreferences.Editor editor = mUserDetails.edit();
					JSONObject mData = new JSONObject(
							response.getString("data"));
					JSONArray mPingenerate = mData
							.getJSONArray("pin_generate_function");
					JSONObject mGetPinDetails = mPingenerate.getJSONObject(0);
					String mUserPin = mGetPinDetails.getString("pin");
					JSONObject mRegData = mData.getJSONObject("registration");
					String mNewUserID = mRegData.getString("new_user_id");
					JSONArray mGetUserDetailData = mData
							.getJSONArray("get_user_detail");
					JSONObject mUserDetails = mGetUserDetailData
							.getJSONObject(0);
					String mDisplayUserDetails = mUserDetails
							.getString("display_user_name");
					String mEmailDisplayDetails = mUserDetails
							.getString("email_id");
					
					JSONArray mJabberData = mData.getJSONArray("jabber_function");
					String jabberUserId = mJabberData.getJSONObject(0).getString("jabber_id");
					String password = mJabberData.getJSONObject(0).getString("password");
					editor.putString("Loggedin_jabber_id",
							jabberUserId);
					editor.putString("Loggedin_jabber_password",
							password);
					editor.commit();
					
					
					if (mNewUserID != null && mDisplayUserDetails != null
							&& mEmailDisplayDetails != null) {

						editor.putString("new_user_id", mNewUserID);
						editor.putString("Loggedin_userid", mNewUserID);

						editor.putString("display_user_details",
								mDisplayUserDetails);
						editor.putString("email_id", mEmailDisplayDetails);
						editor.putString("pin", mUserPin);
						editor.commit();
					}
					gotoCreateANewAccountScreen();
				} else {
					if (isFromFlag.equalsIgnoreCase("application")) {
						Validations.showSingleBtnDialog(mRegisterationMsg,
								DisplayCreatedAccountScreen.this);
					} else {

					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case TWITTER_LOGIN:
			saveTwitterDetails(response);
			gotoCreateANewAccountScreen();

			break;

		default:
			break;
		}
		mloadingLayout.setVisibility(View.GONE);

	}

	private void saveTwitterDetails(JSONObject response) {
		// TODO Auto-generated method stub
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

				JSONObject mdataobject1 = mDataObj.getJSONObject("insert_user");
				{
					// Store User id
					editor.putString("Loggedin_userid",
							mdataobject1.getString("new_user_id"));

				}

				JSONArray mJsonArray1 = mDataObj
						.getJSONArray("jabber_function");
				JSONObject mdataobject = mJsonArray1.getJSONObject(0);
				{
					// Store Jabber details
					editor.putString("Loggedin_jabber_id",
							mdataobject.getString("jabber_id"));
					editor.putString("Loggedin_jabber_password",
							mdataobject.getString("jabber_password"));
				}
				// editor.putString("i_have_currency_id",
				// mdataobject.getString("preference_have_id"));
				// editor.putString("i_want_currency_id",
				// mdataobject.getString("preference_look_id"));
				editor.commit();

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Call the login webservice with twitter

	private void callLoginwithTwitter() {

		// http://local.configure.it/locationbasedchatapp/WS/twitter_login/?
		// &email=cook@eng.in&twitterid=999999
		// &udid=999999
		// &display_name=cookthecaptain

		Bundle mParam = new Bundle();
		mParam.putString("twitterid", twitterId);
		mParam.putString("email", mEdtText_DisplayEmailId.getText().toString()
				.trim());
		mParam.putString("udid", Validations.udid);
		mParam.putString("device_token",
				userDetails.getString("RegistrationId", ""));
		mParam.putString("display_name", displayName);

		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		mRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.TWITTERLOGIN, mParam),
				TWITTER_LOGIN, this);
	}
}
