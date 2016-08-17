package com.hb.barter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.hb.models.Country;
import com.hb.models.State;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;
import com.hiddenbrains.upload.UploadPicture;

public class CreateAccountScreen extends FragmentActivity implements
		OnClickListener, IParseListener {

	// TopBar Variables
	private ImageView mBackBtn, mRightMostButton;
	private TextView mHeaderTitle;
	private static Context mContext;
	private MainActivity mMainAcitivity;
	private LinearLayout mloadingLayout;
	private EditText mEdtText_CAFFirstName, mEdtText_CAFLastName,
			mEdtText_CAFAddress, mEdtText_CAFZipCode, mEdtText_CAFCity,
			mEdtText_CAFISDCode, mEdtText_CAFMobileNum;
	private TextView mTxtView_CAFSkipTxtBtn, mTxtView_CAFState,
			mTxtView_CAFCountry, mTxtView_CAFDateOfBirth;
	private ImageView mImgView_CAFUserImage;
	private List<Country> mCountryList = new ArrayList<Country>();
	private ArrayList<String> cnt = new ArrayList<String>();
	private List<State> mStateList = new ArrayList<State>();
	private ArrayList<String> cntState = new ArrayList<String>();
	private String stateID = new String();
	private String countryID = new String();
	String mCountryIDString;
	private int selectState = 0;
	private int selected = 0;
	private int mYear, mMonth, mDay;
	private static final int CREATE_MY_PROFILE = 102;
	private static final int STATE_TABLE = 103;
	private static final int COUNTRY_TABLE = 104;
	Boolean popupFlag = false;
	public static final int PORT = 5222;

	SharedPreferences mUserDetails;
	private static UploadPicture uploadPicture;
	private String mImagePath;
	String user_id;
	String display_user_name, userName = "";
	String userEmail = "";

	private AQuery mAQuery;
	private String isFrom = "Application";

	public CreateAccountScreen() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_create_full_account);
		overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().getBoolean("isFromEnterPIN")) {
				overridePendingTransition(R.anim.enter_from_left,
						R.anim.exit_to_right);
			}
		}
		mContext = this;
		mAQuery = new AQuery(this);

		setCreateFullAccountFragment();
		callCountryFillWS();
		setOnClickListener();
	}

	private void callStateFillWS(String mCountryID) {
		mloadingLayout.setVisibility(View.VISIBLE);
		Bundle mBundle = new Bundle();
		mBundle.putString("country_id", mCountryID);
		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		mRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.STATE_TABLE, mBundle),
				STATE_TABLE, this);
	}

	private void callCountryFillWS() {
		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		mRequestResponse.getResponse(Webservices.COUNTRY_TABLE, COUNTRY_TABLE,
				this);
	}

	private void setOnClickListener() {
		mTxtView_CAFSkipTxtBtn.setOnClickListener(this);
		mTxtView_CAFDateOfBirth.setOnClickListener(this);
		mTxtView_CAFState.setOnClickListener(this);
		mTxtView_CAFCountry.setOnClickListener(this);
		mImgView_CAFUserImage.setOnClickListener(this);
	}

	private void setCreateFullAccountFragment() {

		mUserDetails = PreferenceManager.getDefaultSharedPreferences(this);

		mEdtText_CAFFirstName = (EditText) findViewById(R.id.edtText_FirstNameCreateFullAccount);
		mEdtText_CAFFirstName.setFocusable(true);
		mEdtText_CAFLastName = (EditText) findViewById(R.id.edtText_LastnameCreateFullAccount);
		mEdtText_CAFLastName.setFocusable(true);
		mEdtText_CAFAddress = (EditText) findViewById(R.id.edttext_AddressCreateFullAccount);
		mEdtText_CAFAddress.setFocusable(true);
		mEdtText_CAFISDCode = (EditText) findViewById(R.id.edtText_ISDCodeCreateFullAccount);
		mEdtText_CAFISDCode.setFocusable(true);
		mEdtText_CAFMobileNum = (EditText) findViewById(R.id.edtText_PhoneNumCreateFullAccount);
		mEdtText_CAFMobileNum.setFocusable(true);
		mEdtText_CAFZipCode = (EditText) findViewById(R.id.edtText_ZipCodeCreateFullAccount);
		mEdtText_CAFZipCode.setFocusable(true);
		mEdtText_CAFCity = (EditText) findViewById(R.id.edtText_CityCreateFullAccount);
		mEdtText_CAFCity.setFocusable(true);
		mloadingLayout = (LinearLayout) findViewById(R.id.loadingllyt);
		mTxtView_CAFCountry = (TextView) findViewById(R.id.txtView_CountryCreateFullAccount);
		mTxtView_CAFDateOfBirth = (TextView) findViewById(R.id.txtView_DateOfBirthCreateFullAccount);
		mTxtView_CAFState = (TextView) findViewById(R.id.txtView_StateCreateFullAccount);
		mTxtView_CAFSkipTxtBtn = (TextView) findViewById(R.id.txtView_SkipCreateFullAccount);
		mImgView_CAFUserImage = (ImageView) findViewById(R.id.imgView_PhotoCFA);

		if (mUserDetails.contains("display_user_details")) {
			user_id = mUserDetails.getString("new_user_id", "def");
			display_user_name = mUserDetails.getString("display_user_details",
					"def");
			userEmail = mUserDetails.getString("email_id", "def");
		} else {
			user_id = mUserDetails.getString("Loggedin_userid", "");
		}

		mHeaderTitle = (TextView) findViewById(R.id.title);
		mHeaderTitle.setText(R.string.create_an_account);

		mBackBtn = (ImageView) findViewById(R.id.slidedown);
		mBackBtn.setOnClickListener(this);

		mRightMostButton = (ImageView) findViewById(R.id.rightMostButton);
		mRightMostButton.setVisibility(View.VISIBLE);
		mRightMostButton.setImageResource(R.drawable.ok);
		mRightMostButton.setOnClickListener(this);

		if (getIntent() != null) {
			isFrom = getIntent().getStringExtra("isFrom");
			userName = getIntent().getStringExtra("firstname");
			// mAQuery.id(mImgView_CAFUserImage).image(
			// getIntent().getStringExtra("imageURL"), true, true, 0,
			// R.drawable.no_images_);

			if (!isFrom.equalsIgnoreCase("application"))
				mAQuery.id(mImgView_CAFUserImage).image(
						getIntent().getStringExtra("imageURL"));

			userEmail = getIntent().getStringExtra("email");
			if (getIntent().getExtras().containsKey("displayname"))
				display_user_name = getIntent().getStringExtra("displayname");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtView_SkipCreateFullAccount:
			if (isFrom.equalsIgnoreCase("Facebook")
					|| isFrom.equalsIgnoreCase("Twitter")) {
				gotoHomeScreen();
			} else {
				showSingleBtnDialog(
						getResources()
								.getString(
										R.string.thank_you_for_registration_your_pin_has_been_sent_),
						CreateAccountScreen.this);
			}
			break;

		case R.id.rightMostButton:
			fillEntriesForAccount();
			break;

		case R.id.imgView_PhotoCFA:
			showProfileImagePicker();
			break;

		case R.id.slidedown:
			Intent mIntent = new Intent(CreateAccountScreen.this,
					DisplayCreatedAccountScreen.class);
			mIntent.putExtra("isFromCreate", true);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mIntent);
			finish();
			break;

		case R.id.txtView_CountryCreateFullAccount:
			showCountryFromPopUP(mTxtView_CAFCountry);
			break;

		case R.id.txtView_StateCreateFullAccount:
			if (mTxtView_CAFCountry.getText().toString()
					.equalsIgnoreCase("Country")
					|| mTxtView_CAFCountry.getText().toString()
							.equalsIgnoreCase("")) {
				Validations.showSingleBtnDialog(
						getResources().getString(
								R.string.please_select_country_first),
						CreateAccountScreen.this);

			} else {
				if (Validations.isNetworkAvailable(CreateAccountScreen.this)) {
					callStateFillWS(mCountryIDString);
				} else {
					Validations
							.showSingleBtnDialog(
									getResources()
											.getString(
													R.string.no_internet_connection_please_try_again),
									CreateAccountScreen.this);
				}
			}
			break;

		case R.id.btn_openCamera:
			OpenCameraActivity();
			mCameraDialog.dismiss();
			break;

		case R.id.btn_openGallery:
			pickimagegallery();
			mCameraDialog.dismiss();
			break;

		case R.id.btn_popupCancel:
			mCameraDialog.dismiss();
			break;

		case R.id.txtView_DateOfBirthCreateFullAccount:
			setDate();
			break;

		default:
			break;
		}

	}

	private void showSingleBtnDialog(String message, Context context) {
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
				if (isFrom.equalsIgnoreCase("Facebook")
						|| isFrom.equalsIgnoreCase("Twitter")) {
					gotoHomeScreen();
				} else {
					gotoBarterPinScreen();
				}
			}
		});

		alertDialog.show();
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mTxtView_CAFDateOfBirth.setText(year + "-" + (monthOfYear + 1)
					+ "-" + dayOfMonth);
		}
	};

	private void setDate() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);

		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog dpDialog = new DatePickerDialog(this,
				mDateSetListener, mYear, mMonth, mDay);
		DatePicker datePicker = dpDialog.getDatePicker();

		Calendar calendar = Calendar.getInstance();// get the current day
		datePicker.setMaxDate(calendar.getTimeInMillis());// set the current
		// day as the
		// max date
		dpDialog.show();
	}

	public void showCountryFromPopUP(final TextView mCountryTextView) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		if (cnt.size() == 0) {
			for (int i = 0; i < mCountryList.size(); i++) {
				cnt.add(mCountryList.get(i).getCountry());
				if (mCountryList
						.get(i)
						.getCountryCode()
						.equalsIgnoreCase(
								mTxtView_CAFCountry.getText().toString().trim())) {
					selected = i;
					countryID = mCountryList.get(i).getCountryID();
				}
			}
		}

		builder.setItems(cnt.toArray(new String[cnt.size()]),
				new DialogInterface.OnClickListener() {
					private String mSTDCODE;

					@Override
					public void onClick(DialogInterface dialog, int which) {
						selected = which;
						mCountryTextView.setText(mCountryList.get(selected)
								.getCountry().toString());
						countryID = mCountryList.get(selected).getCountryID();
						mCountryIDString = mCountryList.get(selected)
								.getCountryID();
						mSTDCODE = mCountryList.get(selected).getStdcode();
						mEdtText_CAFISDCode.setText(mSTDCODE);
						resetState();
					}
				});

		builder.setCancelable(true);

		AlertDialog alert = builder.create();
		if (cnt.size() > 0)
			alert.show();

	}

	private void resetState() {
		mTxtView_CAFState.setText("State");
		mStateList.clear();
		mStateList = new ArrayList<State>();
		cntState.clear();

	}

	public void showStateFromPopUp(final TextView mStateTextView) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		if (cntState.size() == 0) {
			for (int i = 0; i < mStateList.size(); i++) {
				cntState.add(mStateList.get(i).getState());
				if (mStateList
						.get(i)
						.getStateCode()
						.equalsIgnoreCase(
								mTxtView_CAFState.getText().toString().trim())) {
					selectState = i;
					stateID = mStateList.get(i).getStateID();
				}
			}
		}

		builder.setItems(cntState.toArray(new String[cntState.size()]),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						selectState = which;
						mStateTextView.setText(mStateList.get(selectState)
								.getState().toString());
						stateID = mStateList.get(selectState).getStateID();
						String country_ID = mStateList.get(selectState)
								.getStateID();

					}
				});

		builder.setCancelable(true);

		AlertDialog alert = builder.create();
		if (cntState.size() > 0)
			alert.show();

	}

	private void fillEntriesForAccount() {

		String mFirstName = mEdtText_CAFFirstName.getText().toString().trim();
		String mLastName = mEdtText_CAFLastName.getText().toString().trim();
		String mISDCode = mEdtText_CAFISDCode.getText().toString().trim();
		String mMobileNum = mEdtText_CAFMobileNum.getText().toString().trim();
		String mAddress = mEdtText_CAFAddress.getText().toString().trim();
		String mCity = mEdtText_CAFCity.getText().toString().trim();
		String mZipcode = mEdtText_CAFZipCode.getText().toString().trim();

		String mCountry = mTxtView_CAFCountry.getText().toString().trim();
		if (mCountry.equalsIgnoreCase("country")) {
			mCountry = "";
		}
		String mState = mTxtView_CAFState.getText().toString().trim();
		if (mState.equalsIgnoreCase("state")) {
			mState = "";
		}
		String mDOB = mTxtView_CAFDateOfBirth.getText().toString().trim();

		if (Validations.isNetworkAvailable(this)) {
			callCreateMyProfile(mFirstName, mLastName, mISDCode, mMobileNum,
					mAddress, mCity, mZipcode, mDOB, mCountry, mState);
		} else {
			Validations
					.showSingleBtnDialog(
							getString(R.string.no_internet_connection_please_try_again),
							this);
		}

	}

	Bitmap mBitmap;

	private void callCreateMyProfile(String mFirstName, String mLastName,
			String mISDCode, String mMobileNum, String mAddress, String mCity,
			String mZipcode, String mDOB, String mCountry, String mState) {

		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", user_id);
		mBundle.putString("display_name", display_user_name);
		mBundle.putString("email", userEmail);
		mBundle.putString("firstname", mFirstName);
		mBundle.putString("lastname", mLastName);
		mBundle.putString("address1", mAddress);
		mBundle.putString("city", mCity);
		mBundle.putString("state", stateID);
		mBundle.putString("zipcode", mZipcode);
		mBundle.putString("country", mCountryIDString);
		mBundle.putString("mobile_no", mMobileNum);
		mBundle.putString("birthdate", mDOB);

		Intent intent = getIntent();
		if (intent != null) {
			if (intent.hasExtra("imageURL")
					&& !TextUtils.isEmpty(intent.getStringExtra("imageURL"))) {
				mBitmap = Validations.getBitmapFromURL(intent
						.getStringExtra("imageURL"));
			}
		}

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		HashMap<String, File> uploadImg = new HashMap<String, File>();
		if (mBitmap != null) {
			uploadImg.put("profile_photo", Validations.bitmapToFile(mBitmap));
			mJsonRequestResponse.setAttachFileList(uploadImg);
		}
		mloadingLayout.setVisibility(View.VISIBLE);
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.EDIT_MY_PROFILE, mBundle),
				CREATE_MY_PROFILE, this);

	}

	private Dialog mCameraDialog;

	private void showProfileImagePicker() {

		mCameraDialog = new Dialog(this, R.style.AlertDialogCustom);
		mCameraDialog.setCancelable(true);
		mCameraDialog.setContentView(R.layout.pop_camera);
		// mCameraDialog.setTitle(R.string.select_your_profile_image);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = mCameraDialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

		Button btnCancel = (Button) mCameraDialog
				.findViewById(R.id.btn_popupCancel);
		Button btCamera = (Button) mCameraDialog
				.findViewById(R.id.btn_openCamera);
		Button btGallery = (Button) mCameraDialog
				.findViewById(R.id.btn_openGallery);
		btnCancel.setOnClickListener(this);
		btCamera.setOnClickListener(this);
		btGallery.setOnClickListener(this);
		mCameraDialog.show();

	}

	private void OpenCameraActivity() {
		uploadPicture = new UploadPicture((FragmentActivity) mContext);
		uploadPicture.dispatchTakePictureIntent(CAPTURE_IMAGE);
	}

	public void pickimagegallery() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, PICK_IMAGE);
	}

	final static int PICK_IMAGE = 102;
	final static int CAPTURE_IMAGE = 101;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PICK_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					Uri _uri = data.getData();
					if (_uri != null) {
						Cursor cursor = getContentResolver()
								.query(_uri,
										new String[] { android.provider.MediaStore.Images.ImageColumns.DATA },
										null, null, null);
						cursor.moveToFirst();
						mImagePath = cursor.getString(0);
						try {
							uploadPicture = new UploadPicture(this);
							uploadPicture.mCurrentPhotoPath = mImagePath;
							mImgView_CAFUserImage.setImageBitmap(uploadPicture
									.setPic(uploadPicture.mCurrentPhotoPath));
							mBitmap = uploadPicture
									.setPic(uploadPicture.mCurrentPhotoPath);
						} catch (Exception e) {
							e.getStackTrace();
						}
						cursor.close();
					}
				}
			}

		case CAPTURE_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				try {
					mImagePath = uploadPicture.mCurrentPhotoPath;
					mImgView_CAFUserImage
							.setImageBitmap(uploadPicture.setPic());
					mBitmap = uploadPicture.setPic();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}

	protected void gotoBarterPinScreen() {
		Intent mPinIntent = new Intent(CreateAccountScreen.this,
				BarterAccountScreen.class);
		mPinIntent.putExtra("image_path", mImagePath);
		startActivity(mPinIntent);
		finish();
	}

	protected void gotoHomeScreen() {
		Intent mIntent = new Intent(CreateAccountScreen.this,
				MainActivity.class);
		mIntent.putExtra("fromSignUP", true);
		startActivity(mIntent);
		finish();
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		mloadingLayout.setVisibility(View.GONE);
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		String mEditMyProfileMsg = "";
		switch (requestCode) {
		case CREATE_MY_PROFILE:

			try {
				JSONObject mEditMyProfile = new JSONObject(
						response.getString("settings"));
				String mSuccess = mEditMyProfile.getString("success");
				mEditMyProfileMsg = mEditMyProfile.getString("message");
				if (mSuccess.equalsIgnoreCase("1")) {
					showSingleBtnDialog(
							getResources()
									.getString(
											R.string.thank_you_for_registration_your_pin_has_been_sent_),
							CreateAccountScreen.this);

				} else {
					Validations.showSingleBtnDialog(mEditMyProfileMsg,
							CreateAccountScreen.this);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		case STATE_TABLE:

			try {
				JSONObject mCountryInfo = new JSONObject(
						response.getString("settings"));
				if (mCountryInfo.getString("success").equalsIgnoreCase("1")) {
					JSONArray mArray = new JSONArray(response.getString("data"));
					addStatesToList(mArray);
					if (mArray.length() == 0) {
						Validations.showSingleBtnDialog(getResources()
								.getString(R.string.no_state_found),
								CreateAccountScreen.this);
					} else {
						showStateFromPopUp(mTxtView_CAFState);
					}

				} else {
					Validations.showSingleBtnDialog(
							getResources().getString(R.string.no_state_found),
							CreateAccountScreen.this);

				}
				// showStateFromPopUp(mTxtView_CAFState);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case COUNTRY_TABLE:
			try {
				JSONObject mCountryInfo = new JSONObject(
						response.getString("settings"));
				if (mCountryInfo.getString("success").equalsIgnoreCase("1")) {
					JSONArray mArray = new JSONArray(response.getString("data"));
					addCountriesToList(mArray);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}

		mloadingLayout.setVisibility(View.GONE);
	}

	private void addCountriesToList(JSONArray mArray) {
		for (int i = 0; i < mArray.length(); i++) {

			JSONObject mCountryData = new JSONObject();
			try {
				mCountryData = mArray.getJSONObject(i);
				Country mCountry = new Country(mCountryData);
				mCountryList.add(mCountry);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		mTxtView_CAFState.setText(R.string.state);

	}

	private void addStatesToList(JSONArray mArray) {

		mStateList.clear();
		for (int i = 0; i < mArray.length(); i++) {

			JSONObject mStateData = new JSONObject();
			try {
				mStateData = mArray.getJSONObject(i);
				State mState = new State(mStateData);
				mStateList.add(mState);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}
}
