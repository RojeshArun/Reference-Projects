package com.hb.floatingfragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.hb.models.Country;
import com.hb.models.State;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

public class SetHigherRatingFragmentRequestPin extends Fragment implements
		OnClickListener, IParseListener {
	// Top Bar icons
	private ImageView mBackBtn;
	private TextView mHeaderTitle;
	private MainActivity mMainAcitivity;
	private EditText mEdtText_FirstName, mEdtText_PhoneNum,
			mEdtTextView_LastName, mEdtTextView_Street, mEdtTextView_City,
			mEdtTextView_Zipcode, mEdtTextView_ISD;
	private TextView mTxtView_CountryName, mTxtView_StateName;
	private TextView mTextView_GoSendMePINRating, mEdtText_EmailId;

	private View mRootView;

	private static final int SET_HIGHER_RATING = 301;

	private static final int SET_USER_PROFILE = 302;

	public static final int PORT = 5222;

	// SharedPreferences mUserDetails;
	private SharedPreferences userDetails;
	String user_id;
	String display_user_name;
	String display_email;
	private Context mContext;
	Boolean popupFlag = false;
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

	public SetHigherRatingFragmentRequestPin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.fragment_set_higher_rating_new,
				null);
		mMainAcitivity = (MainActivity) getActivity();
		mContext = getActivity();
		setSetHigherRatingFragment();
		// callSetHigherRatingWS();

		callCountryFillWS();
		setOnCLickListener();

		return mRootView;
	}

	private void callUpdateMyProfileWS(String user_id) {
		mMainAcitivity.showProgress();
		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", user_id);

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.VIEW_MY_PROFILE, mBundle),
				SET_USER_PROFILE, this);

	}

	private void callSetHigherRatingWS() {

		mMainAcitivity.showProgress();
		Bundle mBundle = new Bundle();

		mBundle.putString("user_id", user_id);
		mBundle.putString("first_name", mEdtText_FirstName.getText().toString()
				.trim());
		mBundle.putString("last_name", mEdtTextView_LastName.getText()
				.toString().trim());
		mBundle.putString("email", mEdtText_EmailId.getText().toString().trim());
		mBundle.putString("address", mEdtTextView_Street.getText().toString()
				.trim());
		mBundle.putString("city", mEdtTextView_City.getText().toString().trim());
		mBundle.putString("zip_code", mEdtTextView_Zipcode.getText().toString()
				.trim().trim());
		mBundle.putString("country_id", mTxtView_CountryName.getText()
				.toString().trim());
		mBundle.putString("state_id", mTxtView_StateName.getText().toString()
				.trim());
		mBundle.putString("mobile_no", mEdtTextView_ISD.getText().toString()
				.trim()
				+ " " + mEdtText_PhoneNum.getText().toString().trim());

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.SET_HIGHER_RATING, mBundle),
				SET_HIGHER_RATING, this);
	}

	private void setOnCLickListener() {
		mTextView_GoSendMePINRating.setOnClickListener(this);

		mBackBtn = (ImageView) mRootView.findViewById(R.id.slidedown);
		mBackBtn.setOnClickListener(this);
		mTxtView_CountryName.setOnClickListener(this);
		mTxtView_StateName.setOnClickListener(this);
		mHeaderTitle = (TextView) mRootView.findViewById(R.id.title);
		mHeaderTitle.setText(R.string.set_higher_rating);

	}

	private void setSetHigherRatingFragment() {
		mEdtText_FirstName = (EditText) mRootView
				.findViewById(R.id.edtText_Set_Name_Rating);
		userDetails = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mEdtText_EmailId = (TextView) mRootView
				.findViewById(R.id.edtText_SetEmail_IdRating);
		mEdtText_PhoneNum = (EditText) mRootView
				.findViewById(R.id.edtText_Set_PhoneNum_Rating);
		mEdtTextView_LastName = (EditText) mRootView
				.findViewById(R.id.edtText_Set_LastName_Rating);
		mEdtTextView_City = (EditText) mRootView
				.findViewById(R.id.edtText_Set_City_Rating);

		mEdtTextView_ISD = (EditText) mRootView
				.findViewById(R.id.edtText_Set_ISDCode_Rating);

		mEdtTextView_Street = (EditText) mRootView
				.findViewById(R.id.edtText_Set_Street_Rating);

		mTextView_GoSendMePINRating = (TextView) mRootView
				.findViewById(R.id.txtView_BtnSet_SendmePin_Rating);

		mEdtTextView_Zipcode = (EditText) mRootView
				.findViewById(R.id.edtText_Set_PinCode_Rating);

		mTxtView_CountryName = (TextView) mRootView
				.findViewById(R.id.txtView_Country_myProfile);

		mTxtView_StateName = (TextView) mRootView
				.findViewById(R.id.txtView_State_myProfile);

		if (userDetails.contains("Loggedin_userid")) {
			user_id = userDetails.getString("Loggedin_userid", "def");
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.slidedown:
			slideBack();

			break;
		case R.id.txtView_BtnSet_SendmePin_Rating:
			callSetHigherRatingWS();
			break;

		case R.id.txtView_Country_myProfile:
			showCountryFromPopUP(mTxtView_CountryName);
			popupFlag = true;
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
		default:
			break;
		}
		mMainAcitivity.hideProgress();
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

	private void callCountryFillWS() {

		// mMainAcitivity.showProgress();

		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		mRequestResponse.getResponse(Webservices.COUNTRY_TABLE, COUNTRY_TABLE,
				this);

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
						mEdtTextView_ISD.setText(mSTDCODE);
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

	private void slideBack() {
		Validations.hideKeyboard(getActivity());
		for (int i = 1; i < getFragmentManager().getBackStackEntryCount(); ++i) {
			getFragmentManager().popBackStack();
		}
		// getFragmentManager().popBackStack();
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		switch (requestCode) {
		case SET_HIGHER_RATING:
			try {
				JSONObject mSettingSetHighrating = new JSONObject(
						response.getString("settings"));
				mSettingSetHighrating.getString("message");
				mMainAcitivity.hideProgress();
				if (mSettingSetHighrating.getString("success")
						.equalsIgnoreCase("1")) {
					SetHigherRatingFragment mSetHigherFragment = new SetHigherRatingFragment();
					FragmentManager mFragmentManager = getActivity()
							.getSupportFragmentManager();
					FragmentTransaction mFragmentTransaction = mFragmentManager
							.beginTransaction();
					mFragmentTransaction.setCustomAnimations(
							R.anim.slide_in_right, R.anim.slide_in_left,
							R.anim.enter_from_left, R.anim.exit_to_right);
					mFragmentTransaction
							.addToBackStack(
									SetHigherRatingFragmentRequestPin.class
											.getSimpleName())
							.add(R.id.popupLyt, mSetHigherFragment).commit();
				} else {
					// Validations.showAlerDialog(mSettingSetHighrating.getString("message"),
					// getActivity());
					Validations.showSingleBtnDialog(
							mSettingSetHighrating.getString("message"),
							getActivity());
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			mMainAcitivity.hideProgress();
			break;
		case SET_USER_PROFILE:
			String viewMyProfile = "";
			try {
				JSONObject mSettings = new JSONObject(
						response.getString("settings"));
				viewMyProfile = mSettings.getString("message");
				if (mSettings.getString("success").equalsIgnoreCase("1")) {
					JSONArray mData = new JSONArray(response.getString("data"));
					JSONObject mDetailsObject = mData.getJSONObject(0);
					mEdtText_FirstName.setText(mDetailsObject
							.getString("first_name"));
					mEdtTextView_LastName.setText(mDetailsObject
							.getString("last_name"));
					// mEdtText_FullName.setText(mDetailsObject
					// .getString("display_name"));
					mEdtText_EmailId.setText(mDetailsObject.getString("email"));
					mEdtTextView_Street.setText(mDetailsObject
							.getString("address"));
					mEdtTextView_City.setText(mDetailsObject.getString("city"));
					mEdtTextView_Zipcode.setText(mDetailsObject
							.getString("zip_code"));
					String myCountryIDString = mDetailsObject.getString(
							"country_id").toString();
					
					mCountryIDString = mDetailsObject.getString("country_id")
							.toString();

					int foo = Integer.parseInt(myCountryIDString);
//					if (mCountryList.size() > 0) {
//						mEdtTextView_ISD.setText(mCountryList.get(foo-1)
//								.getStdcode());
//
//					}
					if (mCountryList.size() > 0) {

						for (int i = 0; i < mCountryList.size(); i++) {
							String mCountryID = mCountryList.get(i)
									.getCountryID();

							if (mCountryID.equalsIgnoreCase(mDetailsObject
									.getString("country_id"))) {
								mEdtTextView_ISD.setText(mCountryList.get(i)
										.getStdcode());
								break;
							}

						}
					}
					// String mSTDcode, mPhoneNumber;
					// String[] mFullPhoneNumebr = mDetailsObject.getString(
					// "phone_no").split(" ");
					// mSTDcode = mFullPhoneNumebr[0];
					// mPhoneNumber = mFullPhoneNumebr[1];

					// .setText(mSTDcode);

					mEdtText_PhoneNum.setText(mDetailsObject
							.getString("phone_no"));
					mTxtView_CountryName.setText(mDetailsObject
							.getString("country"));
					mTxtView_StateName.setText(mDetailsObject
							.getString("state"));
					// mDetailsObject.getString("state");
					// mDetailsObject.getString("birth_date");

				}

				else {
					Validations.showSingleBtnDialog(viewMyProfile,
							getActivity());
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mMainAcitivity.hideProgress();
			break;
		case COUNTRY_TABLE:
			try {
				JSONObject mCountryInfo = new JSONObject(
						response.getString("settings"));
				if (mCountryInfo.getString("success").equalsIgnoreCase("1")) {
					JSONArray mArray = new JSONArray(response.getString("data"));
					addCountriesToList(mArray);
				}
				callUpdateMyProfileWS(user_id);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
						// Validations
						// .showAlerDialog(
						// getResources().getString(
						// R.string.no_state_found),
						// getActivity());
						Validations.showSingleBtnDialog(getResources()
								.getString(R.string.no_state_found),
								getActivity());
					} else {
						showStateFromPopUp(mTxtView_StateName);
					}

				} else {
					// Validations.showAlerDialog(
					// getResources().getString(R.string.no_state_found),
					// getActivity());
					Validations.showSingleBtnDialog(
							getResources().getString(R.string.no_state_found),
							getActivity());
				}
				// showStateFromPopUp(mTxtView_StateName);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mMainAcitivity.hideProgress();
			break;
		default:
			break;
		}
	}

}
