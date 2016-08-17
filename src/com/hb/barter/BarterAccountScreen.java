package com.hb.barter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;
import com.hiddenbrains.upload.UploadPicture;

public class BarterAccountScreen extends FragmentActivity implements
		OnClickListener, IParseListener {

	private TextView mTxtView_BarterName, mTxtView_BarterEmail,
			mTxtView_BarterGoBtn, mTxtView_ResendBtn, mTxtView_startAgainBtn;
	private EditText mEdtText_BarterEnterPIN;
	private ImageView mBackBtn;

	private TextView mHeaderTitle;
	private static final int RESEND_PIN = 601, DELETE_USER = 609;
	private static final int ACCOUNT_VERIFICATION = 602;


	SharedPreferences mUserDetails;
	private ImageView mImageUser;
	// Loading variable
	private LinearLayout mloadingLayout;

	String user_id;
	String display_user_name;
	String display_email;

	String mImagePath;

	public BarterAccountScreen() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_barter_account);
		overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
		setBarterAccountFragment();
		setOnClickListener();
	}

	private void setOnClickListener() {
		mTxtView_BarterGoBtn.setOnClickListener(this);
		mBackBtn.setOnClickListener(this);
		mTxtView_ResendBtn.setOnClickListener(this);
		mTxtView_startAgainBtn.setOnClickListener(this);
		mTxtView_BarterEmail.setText(display_email);
		mTxtView_BarterName.setText(display_user_name + "!!!");
	}

	private static UploadPicture uploadPicture;

	private void setBarterAccountFragment() {

		mUserDetails = PreferenceManager.getDefaultSharedPreferences(this);
		mTxtView_BarterEmail = (TextView) findViewById(R.id.txtView_BarterEmailId);
		mTxtView_BarterGoBtn = (TextView) findViewById(R.id.txtView_GoBtn);
		mTxtView_BarterName = (TextView) findViewById(R.id.txtView_John);
		mEdtText_BarterEnterPIN = (EditText) findViewById(R.id.edtText_BarterEnterPin);
		mBackBtn = (ImageView) findViewById(R.id.slidedown);
		mBackBtn.setVisibility(View.GONE);
		mTxtView_ResendBtn = (TextView) findViewById(R.id.txtView_ResendPin);
		mTxtView_startAgainBtn = (TextView) findViewById(R.id.txtView_startAgainBtn);
		mloadingLayout = (LinearLayout) findViewById(R.id.loadingllyt);
		mImageUser = (ImageView) findViewById(R.id.imgView_Barter);

		if (mUserDetails.contains("is_pin_screenstate_resumed")
				&& mUserDetails.getBoolean("is_pin_screenstate_resumed", false)) {
			// TODO
			
			user_id=mUserDetails.getString("pinscreen_userid", "");
			display_user_name=mUserDetails.getString("pinscreen_username", "");
			display_email=mUserDetails.getString("pinscreen_useremail", "");
			mImagePath=mUserDetails.getString("pinscreen_userprofileimage", "");
			
			if(!TextUtils.isEmpty(mImagePath)){
				uploadPicture = new UploadPicture(this);
				uploadPicture.mCurrentPhotoPath = mImagePath;
				mImageUser.setImageBitmap(uploadPicture
						.setPic(uploadPicture.mCurrentPhotoPath));
				
			}
			

		} else {

			if (mUserDetails.contains("display_user_details")) {
				user_id = mUserDetails.getString("new_user_id", "def");
				display_user_name = mUserDetails.getString(
						"display_user_details", "");
				display_email = mUserDetails.getString("email_id", "");
			}

			if (!TextUtils.isEmpty(getIntent().getExtras().getString(
					"image_path"))) {
				mImagePath = getIntent().getExtras().getString("image_path");
				uploadPicture = new UploadPicture(this);
				uploadPicture.mCurrentPhotoPath = mImagePath;
				mImageUser.setImageBitmap(uploadPicture
						.setPic(uploadPicture.mCurrentPhotoPath));
			}

		}
		mHeaderTitle = (TextView) findViewById(R.id.title);
		mHeaderTitle.setText(R.string.barter);

		// Save Pin screen state
		savePinScreenState();
	}

	private void savePinScreenState() {
		// TODO Auto-generated method stub
		SharedPreferences.Editor  mEditor = mUserDetails.edit();
		mEditor.putBoolean("is_pin_screenstate_resumed", true);
		
		mEditor.putString("pinscreen_userid", user_id);
		mEditor.putString("pinscreen_useremail", display_user_name);
		mEditor.putString("pinscreen_username", display_email);
		mEditor.putString("pinscreen_userprofileimage", mImagePath);

		mEditor.commit();
		
		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtView_GoBtn:
			validatePinValue();
			break;
		case R.id.slidedown:
			finish();
			break;
		case R.id.txtView_ResendPin:
			if (Validations.isNetworkAvailable(BarterAccountScreen.this)) {
				callResendPinWS();
			} else {
				// Validations.showAlerDialog(getResources().getString(R.string.no_internet_connection_please_try_again),BarterAccountScreen.this);
				Validations
						.showSingleBtnDialog(
								getResources()
										.getString(
												R.string.no_internet_connection_please_try_again),
								BarterAccountScreen.this);
			}
			break;
		case R.id.txtView_startAgainBtn:
			if (Validations.isNetworkAvailable(BarterAccountScreen.this)) {
				callDeleteUserWS();
			} else {
				// Validations.showAlerDialog(getResources().getString(R.string.no_internet_connection_please_try_again),BarterAccountScreen.this);
				Validations
						.showSingleBtnDialog(
								getResources()
										.getString(
												R.string.no_internet_connection_please_try_again),
								BarterAccountScreen.this);
			}
			break;
		default:
			break;
		}
	}

	private void callResendPinWS() {
		mloadingLayout.setVisibility(View.VISIBLE);
		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", user_id);
		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.RESEND_PIN, mBundle),
				RESEND_PIN, this);

	}

	private void callDeleteUserWS() {
		mloadingLayout.setVisibility(View.VISIBLE);
		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", user_id);
		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.DELETE_USER, mBundle),
				DELETE_USER, this);

	}

	private void validatePinValue() {
		String pinValue = mEdtText_BarterEnterPIN.getText().toString().trim();
		if (TextUtils.isEmpty(pinValue)
				|| pinValue.equalsIgnoreCase(getResources().getString(
						R.string.barter_ENTERPIN))) {
			Validations.showSingleBtnDialog(
					getResources().getString(R.string.please_enter_pin), this);
		} else {
			if (Validations.isNetworkAvailable(BarterAccountScreen.this)) {
				callAccountVerificationWS(pinValue);
			} else {
				Validations
						.showSingleBtnDialog(
								getResources()
										.getString(
												R.string.no_internet_connection_please_try_again),
								BarterAccountScreen.this);
			}
		}

	}

	private void callAccountVerificationWS(String pinValue) {
		mloadingLayout.setVisibility(View.VISIBLE);
		Bundle mBundle = new Bundle();
		mBundle.putString("pin_number", pinValue);
		mBundle.putString("user_id", user_id);
		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(Webservices.encodeUrl(
				Webservices.ACCOUNT_VERIFICATION, mBundle),
				ACCOUNT_VERIFICATION, this);
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		mloadingLayout.setVisibility(View.GONE);
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		String mResendPIN = "";
		String mAccountVerificationString = "";
		switch (requestCode) {
		case RESEND_PIN:
			try {
				JSONObject mResendPin = new JSONObject(
						response.getString("settings"));
				mResendPIN = mResendPin.getString("message");
				if (mResendPin.getString("success").equalsIgnoreCase("1")) {
					// Validations.showAlerDialog("New PIN code resent to "+mTxtView_BarterEmail.getText().toString(),
					// BarterAccountScreen.this);
					Validations.showSingleBtnDialog("New PIN code resent to "
							+ mTxtView_BarterEmail.getText().toString(),
							BarterAccountScreen.this);

					/******
					 * This is dummy code remove after app development
					 * completion*********Rahul
					 *****/
					JSONArray mData = new JSONArray(response.getString("data"));
					JSONObject mPinValue = mData.getJSONObject(0);
					String mPin = mPinValue.getString("pin");
					// mEdtText_BarterEnterPIN.setText(mPin);
				} else {
					// Validations.showAlerDialog(mResendPIN,
					// BarterAccountScreen.this);
					Validations.showSingleBtnDialog(mResendPIN,
							BarterAccountScreen.this);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;
		case DELETE_USER:
			try {
				JSONObject mResendPin = new JSONObject(
						response.getString("settings"));
				mResendPIN = mResendPin.getString("message");
				if (mResendPin.getString("success").equalsIgnoreCase("1")) {
					clearPinSceenState();

					Intent intent = new Intent(BarterAccountScreen.this,
							LoginScreen.class);
					Bundle bundle = new Bundle();
					bundle.putBoolean("isFromBarterAccountScreen", true);
					intent.putExtras(bundle);
					startActivity(intent);
					finish();
				} else {
					// Validations.showAlerDialog(mResendPIN,
					// BarterAccountScreen.this);
					Validations.showSingleBtnDialog(mResendPIN,
							BarterAccountScreen.this);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;
		case ACCOUNT_VERIFICATION:
			try {
				JSONObject mAccountVerification = new JSONObject(
						response.getString("settings"));
				mAccountVerificationString = mAccountVerification
						.getString("message");
				if (mAccountVerification.getString("success").equalsIgnoreCase(
						"1")) {

					clearPinSceenState();
					gotoMainActivity();
				} else {
					// Validations.showAlerDialog(mAccountVerificationString,
					// BarterAccountScreen.this);
					Validations.showSingleBtnDialog(mAccountVerificationString,
							BarterAccountScreen.this);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// Save image to Vcard if available

			break;

		default:
			break;
		}
		mloadingLayout.setVisibility(View.GONE);
	}

	private void clearPinSceenState() {
		// TODO Auto-generated method stub
		
		SharedPreferences.Editor  mEditor = mUserDetails.edit();
		mEditor.putBoolean("is_pin_screenstate_resumed", false);
		
		mEditor.putString("pinscreen_userid", "");
		mEditor.putString("pinscreen_useremail", "");
		mEditor.putString("pinscreen_username", "");
		mEditor.putString("pinscreen_userprofileimage", "");

		mEditor.commit();

	}

	private void gotoMainActivity() {
		Intent mIntent = new Intent(BarterAccountScreen.this,
				MainActivity.class);
		mIntent.putExtra("fromSignUP", true);
		startActivity(mIntent);
		finish();
	}
	
	 
}
