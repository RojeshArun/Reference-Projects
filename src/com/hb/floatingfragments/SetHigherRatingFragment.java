package com.hb.floatingfragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

public class SetHigherRatingFragment extends Fragment implements
		OnClickListener, IParseListener {
	// Top Bar icons
	private ImageView mBackBtn;
	private TextView mHeaderTitle;
	private TextView mBottomLine;

	private TextView mText_AddressSetRating, mText_EmailIdSetRating,
			mText_PhoneSetRating, mTextView_GoSetRating,
			mTextView_SetRatingResendPIN;
	private EditText mEdtText_EnterPINSetRating;
	private View mRootView;
	private MainActivity mMainAcitivity;
	private Context mContext;

	private static final int SET_HIGHER_RATING = 301;

	private static final int VALIDATE_PIN = 302;

	private static final int UPDATE_STATUS_PIN = 303;

	public static final int PORT = 5222;
	private SharedPreferences userDetails;

	String user_id;
	String display_user_name;
	String display_email;

	public SetHigherRatingFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.fragment_set_higher_rating, null);
		mMainAcitivity = (MainActivity) getActivity();
		mContext = getActivity();
		setSetHigherRatingFragment();
		// callSetHigherRatingWS();

		if (Validations.isNetworkAvailable(getActivity())) {
			callUpdateValuesWS();
		} else {

			Validations.showSingleBtnDialog(
					getActivity().getString(
							R.string.no_internet_connection_please_try_again),
					getActivity());
//			Validations.showAlerDialog(
//					getActivity().getString(
//							R.string.no_internet_connection_please_try_again),
//					getActivity());
		}
		setOnCLickListener();

		return mRootView;
	}

	private void callUpdateValuesWS() {
		mMainAcitivity.showProgress();
		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", user_id);

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.VIEW_MY_PROFILE, mBundle),
				UPDATE_STATUS_PIN, this);
	}

	private void callValidatePINWS() {

		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", user_id);

		String edtPin = mEdtText_EnterPINSetRating.getText().toString();

		if (edtPin.isEmpty() || edtPin.equalsIgnoreCase("Enter PIN")) {
			Validations.showSingleBtnDialog(
					getResources().getString(R.string.please_enter_pin),
					getActivity());
//			Validations.showAlerDialog(
//					getResources().getString(R.string.please_enter_pin),
//					getActivity());
		} else if (!TextUtils.isEmpty(edtPin)
				&& (edtPin.length() > 4 || edtPin.length() < 4)) {
			Validations.showSingleBtnDialog(
					"Please enter valid 4 digit numeric code", getActivity());
//			Validations.showAlerDialog(
//					"Please enter valid 4 digit numeric code", getActivity());
		} else if (edtPin != null) {
			mMainAcitivity.showProgress();
			mBundle.putString("pincode", edtPin);
			JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
			mJsonRequestResponse.getResponse(
					Webservices.encodeUrl(Webservices.VALIDATE_PIN, mBundle),
					VALIDATE_PIN, this);
		}

	}

	private void callSetHigherRatingWS() {
		mMainAcitivity.showProgress();
		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", user_id);
		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.SET_HIGHER_RATING, mBundle),
				SET_HIGHER_RATING, this);
	}

	private void setOnCLickListener() {
		mTextView_GoSetRating.setOnClickListener(this);
		mTextView_SetRatingResendPIN.setOnClickListener(this);
		mBackBtn = (ImageView) mRootView.findViewById(R.id.slidedown);
		mBackBtn.setOnClickListener(this);
		mHeaderTitle = (TextView) mRootView.findViewById(R.id.title);
		mHeaderTitle.setText(R.string.set_rating);

	}

	private void setSetHigherRatingFragment() {
		mText_AddressSetRating = (TextView) mRootView
				.findViewById(R.id.txtView_SetRatingAddressContent);
		userDetails = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mText_EmailIdSetRating = (TextView) mRootView
				.findViewById(R.id.txtView_SetRatingEmailContent);
		mText_PhoneSetRating = (TextView) mRootView
				.findViewById(R.id.txtView_SetRatingMobileContent);
		mTextView_SetRatingResendPIN = (TextView) mRootView
				.findViewById(R.id.txtView_ResendSetRating);
		mEdtText_EnterPINSetRating = (EditText) mRootView
				.findViewById(R.id.txtView_SetRatingEnterPIN);
		mTextView_GoSetRating = (TextView) mRootView
				.findViewById(R.id.txtView_SetRatingGO);
		mBottomLine = (TextView) mRootView
				.findViewById(R.id.txtView_LineTopBar);
		mBottomLine.setVisibility(View.GONE);

		if (userDetails.contains("Loggedin_userid")) {
			user_id = userDetails.getString("Loggedin_userid", "def");
		}

	}

	private void goToSetHigherRatingRequestPinFragment() {
		SetHigherRatingFragmentRequestPin mSetHigherFragment = new SetHigherRatingFragmentRequestPin();
		FragmentManager mFragmentManager = getActivity()
				.getSupportFragmentManager();
		FragmentTransaction mFragmentTransaction = mFragmentManager
				.beginTransaction();
		// mFragmentTransaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right,R.anim.slide_in_right,
		// R.anim.slide_in_left );
		mFragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
				R.anim.slide_in_left, R.anim.enter_from_left,
				R.anim.exit_to_right);
		mFragmentTransaction
				.addToBackStack(SetHigherRatingFragment.class.getSimpleName())
				.add(R.id.popupLyt, mSetHigherFragment).commit();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.slidedown:
			slideBack();
			break;
		case R.id.txtView_ResendSetRating:
			goToSetHigherRatingRequestPinFragment();
			break;
		case R.id.txtView_SetRatingGO:
			if (Validations.isNetworkAvailable(getActivity())) {
				callValidatePINWS();
			} else {
//				Validations
//						.showAlerDialog(
//								getActivity()
//										.getString(
//												R.string.no_internet_connection_please_try_again),
//								getActivity());
				Validations.showSingleBtnDialog(
						getActivity()
						.getString(
								R.string.no_internet_connection_please_try_again),
				getActivity());
			}
			break;
		default:
			break;
		}

	}

	private void slideBack() {
		Validations.hideKeyboard(getActivity());
		for (int i = 1; i < getFragmentManager().getBackStackEntryCount(); ++i) {
			getFragmentManager().popBackStack();
		}
		
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		mMainAcitivity.hideProgress();
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		switch (requestCode) {

		case SET_HIGHER_RATING:
			String mSetRating = "";
			try {
				mMainAcitivity.hideProgress();
				JSONObject mSettingSetHighrating = new JSONObject(
						response.getString("settings"));
				mSetRating = mSettingSetHighrating.getString("message");
				if (mSettingSetHighrating.getString("success")
						.equalsIgnoreCase("1")) {

					JSONArray mData = new JSONArray(response.getString("data"));

					JSONObject mUserDetails = mData.getJSONObject(0);
					// Validations.showAlerDialog(
					// mSettingSetHighrating.getString("message"),
					// getActivity());
					if (mUserDetails.getString("status").equalsIgnoreCase(
							"None")) {

					} else if (mUserDetails.getString("status")
							.equalsIgnoreCase("Requested")) {

					} else if (mUserDetails.getString("status")
							.equalsIgnoreCase("Activated")
							|| mUserDetails.getString("status")
									.equalsIgnoreCase("Confirmed")) {

					}
				}

				else {
//					Validations.showAlerDialog(mSetRating, getActivity());
					Validations.showSingleBtnDialog(mSetRating, getActivity());
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;
		case VALIDATE_PIN:
			String mValidateMSG = "";
			try {
				mMainAcitivity.hideProgress();
				JSONObject mSettingValidatePIN = new JSONObject(
						response.getString("settings"));
				mValidateMSG = mSettingValidatePIN.getString("message");
				if (mSettingValidatePIN.getString("success").equalsIgnoreCase(
						"1")) {
					// Toast.makeText(getActivity(), "" + mValidateMSG,
					// Toast.LENGTH_SHORT).show();
					slideBack();
				} else {
					mEdtText_EnterPINSetRating.setText("");
//					Validations.showAlerDialog(mValidateMSG, getActivity());
					Validations.showSingleBtnDialog(mValidateMSG, getActivity());
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
			break;
		case UPDATE_STATUS_PIN:
			String mUpdatePIN = "";
			try {
				mMainAcitivity.hideProgress();
				JSONObject mSettingPIN = new JSONObject(
						response.getString("settings"));
				mUpdatePIN = mSettingPIN.getString("message");
				if (mSettingPIN.getString("success").equalsIgnoreCase("1")) {
					JSONArray mData = new JSONArray(response.getString("data"));
					JSONObject mDetailsObject = mData.getJSONObject(0);
					String mFullName = mDetailsObject.getString("display_name");
					String mAddress = mDetailsObject.getString("address");
					String mState = mDetailsObject.getString("city");
					String mCity = mDetailsObject.getString("country");
					String mEmailId = mDetailsObject.getString("email");
					String mPhoneNum = mDetailsObject.getString("phone_no");

					mText_AddressSetRating.setText(mFullName + "\n" + mAddress
							+ "\n" + mState + "\n" + mCity);

					mText_EmailIdSetRating.setText(mEmailId);
					mText_PhoneSetRating.setText(mPhoneNum);

				} else {
					// Validations.showAlerDialog(mUpdatePIN, getActivity());
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

			break;
		default:
			break;
		}
	}

}
