package com.hb.floatingfragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;
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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.barter.xmpp.HBXMPP;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.baseapplication.BaseApplication;
import com.hb.models.Country;
import com.hb.models.State;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;
import com.hiddenbrains.upload.UploadPicture;

public class MyProfileFragment extends Fragment implements OnClickListener,
		IParseListener {

	// TopBar Variables
	private ImageView mBackBtn, mRightBtn, mRightBut1;
	private TextView mHeaderTitle, mTxtView_TopBarLine;

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;

	private static final int VIEW_MY_PROFILE = 301;
	private static final int UPDATE_MY_PROFILE = 302;
	public static final int PORT = 5222;
	// //////////////////////////////////////////////////////////////////////
	private List<Country> mCountryList = new ArrayList<Country>();
	private ArrayList<String> cnt = new ArrayList<String>();
	private List<State> mStateList = new ArrayList<State>();
	private ArrayList<String> cntState = new ArrayList<String>();
	private String stateID;
	private String countryID = new String();
	String mCountryIDString;
	private int selectState = 0;
	private int selected = 0;
	private int mYear, mMonth, mDay;
	private static final int STATE_TABLE = 103;
	private static final int COUNTRY_TABLE = 104;
	// /////////////////////////////////////////////////////////////////////

	private LinearLayout mFloatingLayout;
	private ImageView mImgViewUser;
	private EditText mEdtText_FullName, mEdtText_FirstName, mEdtText_LastName,
			mEdtText_StreetName, mEdtText_CityName, mEdtText_ZipCode,
			mEdtText_ISDCode, mEdtText_PhoneNumber;

	private TextView mTxtView_CountryName, mTxtView_StateName, mTxtView_DOB,
			mTxtView_EmailId;

	private SharedPreferences userDetails;
	String user_id;
	String display_user_name;
	String display_email;

	private View mRootView;
	MyProfileFragment mFragment1;
	private LinearLayout mSemiTransLyt;
	private LinearLayout mBottomLyt;

	String mFullNameMyProfile;
	String mEmailIdMyProfile;
	String mFirstNameMyProfile;
	String mLastNameMyProfile;
	String mISDCodeMyProfile;
	String mMobileNumMyProfile;
	String mAddressMyProfile;
	String mCityMyProfile;
	String mZipcodeMyProfile;
	String mDOBMyProfile;
	String mCountryMyProfile;
	String mStateMyProfile;
	private MainActivity mMainAcitivity;
	Bitmap mBitmap;
	private static UploadPicture uploadPicture;
	private String mImagePath;
	private Context mContext;
	Boolean popupFlag = false;
	private AQuery mAQuery;

	String valOfDate;
	String valOfDOB;

	// Profile Variables
	private Boolean isOpen = false;

	private HBXMPP hbxmpp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_myprofile, null);
		mMainAcitivity = (MainActivity) getActivity();
		hbxmpp = ((BaseApplication) getActivity().getApplication()).getHbxmpp();
		setProfileFragment();
		setOnClickListeners();

		callCountryFillWS();

		return mRootView;
	}

	private void setProfileFragment() {
		mContext = getActivity();
		mAQuery = new AQuery(getActivity());

		userDetails = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mBackBtn = (ImageView) mRootView.findViewById(R.id.slidedown);
		mRightBtn = (ImageView) mRootView.findViewById(R.id.rightMostButton);
		mRightBut1 = (ImageView) mRootView.findViewById(R.id.rightButOneButton);

		mHeaderTitle = (TextView) mRootView.findViewById(R.id.title);
		mFloatingLayout = (LinearLayout) mRootView
				.findViewById(R.id.floatingLayout);
		mBottomLyt = (LinearLayout) mRootView.findViewById(R.id.bottommenu);
		mImgViewUser = (ImageView) mRootView
				.findViewById(R.id.imgView_Photo_myProfile);
		mSemiTransLyt = (LinearLayout) mRootView
				.findViewById(R.id.loading_transperent);
		mEdtText_FullName = (EditText) mRootView
				.findViewById(R.id.edtText_FullName_myProfile);
		mEdtText_FirstName = (EditText) mRootView
				.findViewById(R.id.edtText_UserName_myProfile);
		mEdtText_LastName = (EditText) mRootView
				.findViewById(R.id.edtText_Surname_myProfile);
		mTxtView_EmailId = (TextView) mRootView
				.findViewById(R.id.txtView_EmailId_myProfile);
		mEdtText_CityName = (EditText) mRootView
				.findViewById(R.id.edtText_City_myProfile);
		mEdtText_ZipCode = (EditText) mRootView
				.findViewById(R.id.edtText_PinCode_myProfile);
		mEdtText_StreetName = (EditText) mRootView
				.findViewById(R.id.edtText_Street_myProfile);
		mEdtText_ISDCode = (EditText) mRootView
				.findViewById(R.id.edtText_ISDCode_myProfile);
		mEdtText_PhoneNumber = (EditText) mRootView
				.findViewById(R.id.edtText_PhoneNum_myProfile);
		mTxtView_CountryName = (TextView) mRootView
				.findViewById(R.id.txtView_Country_myProfile);
		mTxtView_TopBarLine = (TextView) mRootView
				.findViewById(R.id.txtView_LineTopBar);
		mTxtView_TopBarLine.setVisibility(View.INVISIBLE);

		mTxtView_DOB = (TextView) mRootView
				.findViewById(R.id.txtView_DOB_myProfile);

		mTxtView_StateName = (TextView) mRootView
				.findViewById(R.id.txtView_State_myProfile);

		mImgViewUser.setClickable(false);

		if (userDetails.contains("Loggedin_userid")) {
			user_id = userDetails.getString("Loggedin_userid", "def");
		}

	}

	private void setOnClickListeners() {

		mHeaderTitle.setText(R.string.profile);

		mBackBtn.setOnClickListener(this);
		mRightBtn.setVisibility(View.VISIBLE);
		mRightBtn.setImageResource(R.drawable.icn_edit);

		mRightBtn.setOnClickListener(this);
		mRightBut1.setOnClickListener(this);
		mTxtView_CountryName.setOnClickListener(this);
		mTxtView_DOB.setOnClickListener(this);
		mTxtView_StateName.setOnClickListener(this);
		mFloatingLayout.setOnClickListener(this);

		mImgViewUser.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.slidedown:
			slideBack();

			break;
		case R.id.rightButOneButton:
			fillEntriesForAccount();
			if (Validations.isNetworkAvailable(getActivity())) {
				validateMyProfileForm();
			} else {
				Validations
						.showSingleBtnDialog(
								getActivity()
										.getString(
												R.string.no_internet_connection_please_try_again),
								getActivity());
				// Validations
				// .showAlerDialog(
				// getActivity()
				// .getString(
				// R.string.no_internet_connection_please_try_again),
				// getActivity());
			}

			break;
		case R.id.rightMostButton:
			if (mRightBut1.getVisibility() == View.INVISIBLE)
				enableProfileEditMode();
			else
				gotoChangePasswordFragment();
			break;
		case R.id.floatingLayout:
			// MainAcitivity.getInstance().hideFloatingMenu();
			break;

		case R.id.imgView_Photo_myProfile:
			if (mEdtText_FullName.isEnabled())
				showProfileImagePicker();
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

		case R.id.txtView_State_myProfile:

			if (mTxtView_CountryName.getText().toString()
					.equalsIgnoreCase("Country")
					|| mTxtView_CountryName.getText().toString()
							.equalsIgnoreCase("")) {
				// Validations.showAlerDialog(
				// getActivity().getString(
				// R.string.please_select_country_first),
				// getActivity());
				Validations.showSingleBtnDialog(
						getActivity().getString(
								R.string.please_select_country_first),
						getActivity());

			} else {
				callStateFillWS(mCountryIDString);
			}

			break;
		case R.id.txtView_DOB_myProfile:
			setDate();
			break;

		case R.id.txtView_Country_myProfile:
			showCountryFromPopUP(mTxtView_CountryName);
			popupFlag = true;
			break;
		default:
			break;
		}

	}

	private void callCountryFillWS() {

		mMainAcitivity.showProgress();

		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		mRequestResponse.getResponse(Webservices.COUNTRY_TABLE, COUNTRY_TABLE,
				this);

	}

	private void gotoChangePasswordFragment() {

		ChangePasswordFragment mChangePasswordFragment = new ChangePasswordFragment();

		mFragmentManager = getActivity().getSupportFragmentManager();
		mFragmentTransaction = mFragmentManager.beginTransaction();

		mFragmentTransaction.setCustomAnimations(R.anim.enter_from_right,
				R.anim.exit_to_left, R.anim.enter_from_left,
				R.anim.exit_to_right);

		mFragmentTransaction
				.addToBackStack(MyProfileFragment.class.getSimpleName())
				.add(R.id.popupLyt, mChangePasswordFragment).commit();

	}

	public void showCountryFromPopUP(final TextView mTxtViewCountryName) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		if (cnt.size() == 0) {
			for (int i = 0; i < mCountryList.size(); i++) {
				cnt.add(mCountryList.get(i).getCountry());
				if (mCountryList
						.get(i)
						.getCountryCode()
						.equalsIgnoreCase(
								mTxtView_CountryName.getText().toString()
										.trim())) {
					selected = i;
					countryID = mCountryList.get(i).getCountryID();
				}
			}
		}

		builder.setItems(cnt.toArray(new String[cnt.size()]),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						selected = which;
						mTxtViewCountryName.setText(mCountryList.get(selected)
								.getCountry().toString());
						countryID = mCountryList.get(selected).getCountryID();
						mCountryIDString = mCountryList.get(selected)
								.getCountryID();
						String country_ID = mCountryList.get(selected)
								.getCountryID();
						String mSTDCODE = mCountryList.get(selected)
								.getStdcode();
						mEdtText_ISDCode.setText(mSTDCODE);
						SharedPreferences.Editor edit = userDetails.edit();
						edit.putString("mStateID", "def");
						edit.commit();

						resetState();
					}
				});

		builder.setCancelable(true);

		AlertDialog alert = builder.create();
		if (cnt.size() > 0)
			alert.show();

	}

	private void resetState() {
		mTxtView_StateName.setText("State");
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
								mTxtView_StateName.getText().toString().trim())) {
					selectState = i;
					stateID = mStateList.get(i).getStateID();
					SharedPreferences.Editor edit = userDetails.edit();
					edit.putString("mStateID", stateID);
					edit.commit();

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
						SharedPreferences.Editor edit = userDetails.edit();
						edit.putString("mStateID", stateID);
						edit.commit();

					}
				});

		builder.setCancelable(true);

		AlertDialog alert = builder.create();
		if (cntState.size() > 0)
			alert.show();

	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// write your code here to get the selected Date

			String mMonthOfYear = getMonth(monthOfYear + 1);

			String myProfileDOB = (year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
			// mTxtView_DOB.setText(year + "-" + mMonthOfYear + "-"
			// + dayOfMonth);
			mTxtView_DOB.setText(dayOfMonth + " " + mMonthOfYear + " " + year);
			// valOfDate = mTxtView_DOB.getText().toString();
			valOfDate = myProfileDOB;
			SharedPreferences.Editor edit = userDetails.edit();
			edit.putString("mDOB_User", valOfDate);
			edit.commit();

		}
	};

	public String getMonth(int month) {
		return new DateFormatSymbols().getMonths()[month - 1];
	}

	private void setDate() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);

		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog dpDialog = new DatePickerDialog(getActivity(),
				mDateSetListener, mYear, mMonth, mDay);
		DatePicker datePicker = dpDialog.getDatePicker();

		Calendar calendar = Calendar.getInstance();// get the current day
		datePicker.setMaxDate(calendar.getTimeInMillis());// set the current
		// day as the
		// max date
		dpDialog.show();
	}

	private void validateMyProfileForm() {

		// callUpdateMyProfileWS();
		mMainAcitivity.showProgress();
		callUpdateMyProfile(mFirstNameMyProfile, mLastNameMyProfile,
				mISDCodeMyProfile, mMobileNumMyProfile, mAddressMyProfile,
				mCityMyProfile, mZipcodeMyProfile, mEmailIdMyProfile,
				mFullNameMyProfile, mDOBMyProfile, mCountryMyProfile,
				mStateMyProfile);

	}

	private void callStateFillWS(String mCountryID) {

		mMainAcitivity.showProgress();
		Bundle mBundle = new Bundle();

		mBundle.putString("country_id", mCountryID);
		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		mRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.STATE_TABLE, mBundle),
				STATE_TABLE, this);
	}

	private void callViewMyProfileWS(String user_id) {
		mMainAcitivity.showProgress();
		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", user_id);

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.VIEW_MY_PROFILE, mBundle),
				VIEW_MY_PROFILE, this);

	}

	private void enableProfileEditMode() {
		mRightBut1.setVisibility(View.VISIBLE);

		mRightBtn.setImageResource(R.drawable.lock);
		mRightBut1.setImageResource(R.drawable.save);

		// Enable My Profile
		mEdtText_FullName.setEnabled(true);
		mEdtText_FirstName.setEnabled(true);
		mEdtText_LastName.setEnabled(true);
		mTxtView_EmailId.setEnabled(true);
		mEdtText_StreetName.setEnabled(true);
		mEdtText_CityName.setEnabled(true);
		mEdtText_ISDCode.setEnabled(true);
		mEdtText_ZipCode.setEnabled(true);
		mEdtText_PhoneNumber.setEnabled(true);
		mTxtView_CountryName.setEnabled(true);
		mTxtView_DOB.setEnabled(true);
		mTxtView_StateName.setEnabled(true);

		mEdtText_FullName.setSelected(true);
		mEdtText_FirstName.setSelected(true);
		mEdtText_LastName.setSelected(true);
		mTxtView_EmailId.setSelected(true);
		mEdtText_StreetName.setSelected(true);
		mEdtText_CityName.setSelected(true);
		mEdtText_ISDCode.setSelected(true);
		mEdtText_ZipCode.setSelected(true);
		mEdtText_PhoneNumber.setSelected(true);

		mImgViewUser.setEnabled(true);
		mImgViewUser.setClickable(true);
		mTxtView_DOB
				.setBackgroundResource(R.drawable.selector_dropdown_green_runtime);
		mTxtView_CountryName
				.setBackgroundResource(R.drawable.selector_dropdown_green_runtime);
		mTxtView_StateName
				.setBackgroundResource(R.drawable.selector_dropdown_green_runtime);
		mTxtView_StateName.setTextColor(getResources().getColor(
				R.drawable.text_color));

	}

	private void disableProfileEditMode() {
		mRightBut1.setVisibility(View.GONE);

		mRightBtn.setImageResource(R.drawable.icn_edit);
		mRightBut1.setVisibility(View.INVISIBLE);

		// Enable My Profile
		mEdtText_FullName.setEnabled(false);
		mEdtText_FirstName.setEnabled(false);
		mEdtText_LastName.setEnabled(false);
		mTxtView_EmailId.setEnabled(false);
		mEdtText_StreetName.setEnabled(false);
		mEdtText_CityName.setEnabled(false);
		mEdtText_ISDCode.setEnabled(false);
		mEdtText_ZipCode.setEnabled(false);
		mEdtText_PhoneNumber.setEnabled(false);
		mTxtView_CountryName.setEnabled(false);
		mTxtView_DOB.setEnabled(false);
		mTxtView_StateName.setEnabled(false);

		mEdtText_FullName.setSelected(false);
		mEdtText_FirstName.setSelected(false);
		mEdtText_LastName.setSelected(false);
		mTxtView_EmailId.setSelected(false);
		mEdtText_StreetName.setSelected(false);
		mEdtText_CityName.setSelected(false);
		mEdtText_ISDCode.setSelected(false);
		mEdtText_ZipCode.setSelected(false);
		mEdtText_PhoneNumber.setSelected(false);

		mImgViewUser.setEnabled(false);
		mImgViewUser.setClickable(false);
		mTxtView_DOB
				.setBackgroundResource(R.drawable.selector_dropdown_green_runtime);
		mTxtView_CountryName
				.setBackgroundResource(R.drawable.selector_dropdown_green_runtime);
		mTxtView_StateName
				.setBackgroundResource(R.drawable.selector_dropdown_green_runtime);
		// mTxtView_StateName.setTextColor(getResources().getColor(
		// R.drawable.text_color));

	}

	private void slideBack() {

		Validations.hideKeyboard(getActivity());
		getFragmentManager().popBackStack();

	}

	private void fillEntriesForAccount() {

		mFullNameMyProfile = mEdtText_FullName.getText().toString().trim();
		mEmailIdMyProfile = mTxtView_EmailId.getText().toString().trim();
		mFirstNameMyProfile = mEdtText_FirstName.getText().toString().trim();
		mLastNameMyProfile = mEdtText_LastName.getText().toString().trim();
		mISDCodeMyProfile = mEdtText_ISDCode.getText().toString().trim();
		mMobileNumMyProfile = mEdtText_PhoneNumber.getText().toString().trim();
		mAddressMyProfile = mEdtText_StreetName.getText().toString().trim();
		mCityMyProfile = mEdtText_CityName.getText().toString().trim();
		mZipcodeMyProfile = mEdtText_ZipCode.getText().toString().trim();
		if (userDetails.contains("mDOB_User")) {
			valOfDOB = userDetails.getString("mDOB_User", "def");
		}
		// mDOBMyProfile = mTxtView_DOB.getText().toString().trim();
		mDOBMyProfile = valOfDOB;
		mCountryMyProfile = mTxtView_CountryName.getText().toString().trim();
		if (userDetails.contains("mStateID")) {
			mStateMyProfile = userDetails.getString("mStateID", "def");
		}
		// mStateMyProfile = mTxtView_StateName.getText().toString().trim();

	}

	private void callUpdateMyProfile(String mFirstName, String mLastName,
			String mISDCode, String mMobileNum, String mAddress, String mCity,
			String mZipcode, String mEmailId, String mFullName, String mDOB,
			String mCountry, String mState) {

		Bundle mBundle = new Bundle();

		mBundle.putString("user_id", user_id);
		mBundle.putString("display_name", mFullName);
		mBundle.putString("email", mEmailId);
		mBundle.putString("firstname", mFirstName);
		mBundle.putString("lastname", mLastName);
		mBundle.putString("address1", mAddress);
		mBundle.putString("city", mCity);
		mBundle.putString("state", mState);
		mBundle.putString("zipcode", mZipcode);
		mBundle.putString("country", mCountryIDString);
		mBundle.putString("mobile_no", mMobileNum);
		mBundle.putString("birthdate", mDOB);

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();

		HashMap<String, File> uploadImg = new HashMap<String, File>();

		int h = (int) getResources().getDimension(R.dimen.chat_pic_dim); // height
		// in
		// pixels
		int w = h; // width in pixels
		if (mBitmap != null) {
			Bitmap scaled = Bitmap.createScaledBitmap(mBitmap, h, w, true);
			if (scaled != null) {
				uploadImg
						.put("profile_photo", Validations.bitmapToFile(scaled));
				mJsonRequestResponse.setAttachFileList(uploadImg);

			}
		} else {
			mBundle.putString("profile_photo", "");
		}
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.EDIT_MY_PROFILE, mBundle),
				UPDATE_MY_PROFILE, this);

	}

	public void setSemiTransVisible() {
		mSemiTransLyt.setVisibility(View.VISIBLE);
	}

	public void setSemiTransGone() {
		mSemiTransLyt.setVisibility(View.GONE);
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {

		mMainAcitivity.hideProgress();
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
				callViewMyProfileWS(user_id);
			}
		});

		alertDialog.show();
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		String viewMyProfile = "";
		String updatedMyProfile = "";
		switch (requestCode) {
		case VIEW_MY_PROFILE: {
			try {
				JSONObject mSettings = new JSONObject(
						response.getString("settings"));
				viewMyProfile = mSettings.getString("message");
				if (mSettings.getString("success").equalsIgnoreCase("1")) {
					JSONArray mData = new JSONArray(response.getString("data"));
					JSONObject mDetailsObject = mData.getJSONObject(0);
					mEdtText_FirstName.setText(mDetailsObject
							.getString("first_name"));
					mEdtText_LastName.setText(mDetailsObject
							.getString("last_name"));
					mEdtText_FullName.setText(mDetailsObject
							.getString("display_name"));
					mTxtView_EmailId.setText(mDetailsObject.getString("email"));
					mEdtText_StreetName.setText(mDetailsObject
							.getString("address"));
					mEdtText_CityName.setText(mDetailsObject.getString("city"));
					mEdtText_ZipCode.setText(mDetailsObject
							.getString("zip_code"));

					String myCountryIDString = mDetailsObject.getString(
							"country_id").toString();
					mCountryIDString = mDetailsObject.getString("country_id")
							.toString();

					int foo = Integer.parseInt(myCountryIDString);
					if (mCountryList.size() > 0) {

						for (int i = 0; i < mCountryList.size(); i++) {
							String mCountryID = mCountryList.get(i)
									.getCountryID();

							if (mCountryID.equalsIgnoreCase(mDetailsObject
									.getString("country_id"))) {
								mEdtText_ISDCode.setText(mCountryList.get(i)
										.getStdcode());
								break;
							}

						}

					}

					mEdtText_PhoneNumber.setText(mDetailsObject
							.getString("phone_no"));
					mTxtView_CountryName.setText(mDetailsObject
							.getString("country"));
					mTxtView_StateName.setText(mDetailsObject
							.getString("state"));

					String mDOB = mDetailsObject.getString("birth_date");
					mTxtView_DOB
							.setText(mDetailsObject.getString("birth_date"));

					String mResultFormat[] = mDOB.split("-");

					if (mResultFormat[1].isEmpty()
							|| mResultFormat[1].equals("00")) {

						mTxtView_DOB.setText("DOB");
						// mTxtView_DOB.setText(mResultFormat[2] + " "
						// + mResultFormat[1] + " " + mResultFormat[0]);
					} else {
						String mMonth = getMonth(Integer
								.parseInt(mResultFormat[1]));
						mTxtView_DOB.setText(mResultFormat[2] + " " + mMonth
								+ " " + mResultFormat[0]);
					}

					String imgURL = mDetailsObject.getString("profile_image")
							.toString();
					if (!TextUtils.isEmpty(imgURL)
							&& !imgURL.contains("noimage")) {
						mAQuery.id(mImgViewUser).image(imgURL);
					}

					SharedPreferences.Editor editor = userDetails.edit();
					editor.putString("profile_image",
							mDetailsObject.getString("profile_image")
									.toString());

					editor.commit();

					disableProfileEditMode();

				}

				else {
					showSingleBtnDialog(viewMyProfile, getActivity());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			mMainAcitivity.hideProgress();

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
				callViewMyProfileWS(user_id);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mMainAcitivity.hideProgress();
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
								getActivity());

						// Validations
						// .showAlerDialog(
						// getResources().getString(
						// R.string.no_state_found),
						// getActivity());
					} else {
						showStateFromPopUp(mTxtView_StateName);
					}

				} else {
					Validations.showSingleBtnDialog(
							getResources().getString(R.string.no_state_found),
							getActivity());
					// Validations.showAlerDialog(
					// getResources().getString(R.string.no_state_found),
					// getActivity());
				}
				// showStateFromPopUp(mTxtView_StateName);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mMainAcitivity.hideProgress();
			break;
		case UPDATE_MY_PROFILE:
			try {
				JSONObject mEditMyProfile = new JSONObject(
						response.getString("settings"));
				String mSuccess = mEditMyProfile.getString("success");
				updatedMyProfile = mEditMyProfile.getString("message");
				if (mSuccess.equalsIgnoreCase("1")) {
					UploadImageInVCard(response);
				} else {
					mMainAcitivity.hideProgress();
					showSingleBtnDialog(updatedMyProfile, getActivity());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	private void UploadImageInVCard(final JSONObject response) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int h = (int) getResources().getDimension(
							R.dimen.chat_pic_dim); // height
					int w = h; // width in pixels
					Bitmap scaled = Bitmap.createScaledBitmap(mBitmap, h, w,
							true);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					scaled.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] array = stream.toByteArray();
					ProviderManager
							.getInstance()
							.addIQProvider(
									"vCard",
									"vcard-temp",
									new org.jivesoftware.smackx.provider.VCardProvider());
					final VCard vCard = new VCard();
					hbxmpp = ((BaseApplication) mMainAcitivity.getApplication())
							.getHbxmpp();
					vCard.load(hbxmpp.getConnection());
					vCard.setOrganization("Bartr");
					vCard.setNickName(mFullNameMyProfile);
					Log.e("MyProfileFragment", "mFullNameMyProfile-"
							+ mFullNameMyProfile);
					vCard.setAvatar(array);
					vCard.save(hbxmpp.getConnection());
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							Presence presence = new Presence(
									Presence.Type.available);
							hbxmpp.getConnection().disconnect();
						}
					}, 2000);
					// hbxmpp.getConnection().disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (mMainAcitivity != null) {
					mMainAcitivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mMainAcitivity.hideProgress();
							((MainActivity) getActivity()).ReconnectXmpp();
							showSingleBtnDialog(
									"Profile Updated Succesfully !",
									getActivity());
							try {
								JSONArray mData = new JSONArray(response
										.getString("data"));
								JSONObject mDetailsData = mData
										.getJSONObject(0);
								mDetailsData.getString("Name");
								mDetailsData.getString("u_user_rating");
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		}).start();

	}

	private Dialog mCameraDialog;

	private void showProfileImagePicker() {

		mCameraDialog = new Dialog(getActivity(), R.style.AlertDialogCustom);
		mCameraDialog.setCancelable(true);
		mCameraDialog.setContentView(R.layout.pop_camera);

		// mCameraDialog.setTitle(R.string.select_your_profile_image);

		mCameraDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

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

	private void addStatesToList(JSONArray mArray) {

		mStateList.clear();
		for (int i = 0; i < mArray.length(); i++) {

			JSONObject mStateData = new JSONObject();
			try {
				mStateData = mArray.getJSONObject(i);
				State mState = new State(mStateData);
				mStateList.add(mState);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void addCountriesToList(JSONArray mArray) {
		for (int i = 0; i < mArray.length(); i++) {

			JSONObject mCountryData = new JSONObject();
			try {
				mCountryData = mArray.getJSONObject(i);
				Country mCountry = new Country(mCountryData);
				mCountryList.add(mCountry);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void OpenCameraActivity() {
		uploadPicture = new UploadPicture(mMainAcitivity);
		uploadPicture.dispatchTakePictureIntent(CAPTURE_IMAGE);

	}

	public void pickimagegallery() {

		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		photoPickerIntent.setType("image/*");
		mMainAcitivity.startActivityForResult(photoPickerIntent, PICK_IMAGE);

	}

	final static int PICK_IMAGE = 1;
	final static int CAPTURE_IMAGE = 2;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		case PICK_IMAGE:

			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					Uri _uri = data.getData();
					if (_uri != null) {
						Cursor cursor = getActivity()
								.getContentResolver()
								.query(_uri,
										new String[] { android.provider.MediaStore.Images.ImageColumns.DATA },
										null, null, null);
						cursor.moveToFirst();

						mImagePath = cursor.getString(0);

						try {
							uploadPicture = new UploadPicture(getActivity());
							uploadPicture.mCurrentPhotoPath = mImagePath;
							mImgViewUser.setImageBitmap(uploadPicture
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
			break;
		case CAPTURE_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				try {
					mImagePath = uploadPicture.mCurrentPhotoPath;
					mImgViewUser.setImageBitmap(uploadPicture.setPic());
					mBitmap = uploadPicture.setPic();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		}

	}

	private static MyProfileFragment myProfileFragment;

	public MyProfileFragment() {
		myProfileFragment = MyProfileFragment.this;
	}

	public static MyProfileFragment getInstance() {
		if (myProfileFragment == null) {
			myProfileFragment = new MyProfileFragment();
		}
		return myProfileFragment;

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

}
