package com.barter.fragments;

import java.text.DecimalFormat;
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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.fragments.NearByFragment;
import com.hb.models.Currency;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

public class ConverterFragment extends Fragment implements OnClickListener,
		IParseListener, OnCheckedChangeListener {

	private static final int CURRENCY = 200;
	private static final int CURRENCY_CONVERTER = 201;
	private static final int SET_PREFERENCES = 204;

	private MainActivity mMainAcitivity;
	private Context mContext;

	private TextView mIhaveCurrencyTxtView, mIWantCurrencyTxtView,
			mConvertButton, mConvertedAmount, mConvertedCurrency;
	private EditText mAmountFrom, mAmountTo;
	private List<Currency> mCurrencyList = new ArrayList<Currency>();
	Boolean isSelected = false;
	private View mRootView;
	private Boolean isFrom = true;
	DecimalFormat fAmount, fAmountConversion;
	Boolean ifRoundOff = true;
	Double mValue;
	Double mBasevalue;
	private TextView mAmountLyt;
	private LinearLayout mSearchButton;

	// Shared Preferences
	private SharedPreferences userDetails;
	public boolean isShowPreference = true;
	private SharedPreferences.Editor editor;

	public ConverterFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMainAcitivity = (MainActivity) getActivity();
		mContext = getActivity();

		userDetails = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.converter_fragment, null);

		SetTheToolFragment();
		if (Validations.isNetworkAvailable(getActivity())) {
			callCurrenciesWS();
		} else {
			Validations.showSingleBtnDialog(
					getActivity().getString(
							R.string.no_internet_connection_please_try_again),
					getActivity());
		}
		setOnClickListeners();

		return mRootView;
	}

	private void callCurrenciesWS() {
		mMainAcitivity.showProgress();
		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		mRequestResponse.getResponse(Webservices.CURRENCY, CURRENCY, this);

	}

	private void setOnClickListeners() {

		mIhaveCurrencyTxtView.setOnClickListener(this);
		mIWantCurrencyTxtView.setOnClickListener(this);
		mConvertButton.setOnClickListener(this);
		mSearchButton.setOnClickListener(this);
		mAmountFrom.setCursorVisible(false);
		mAmountTo.setCursorVisible(false);

		mAmountTo.setOnClickListener(this);
		mAmountFrom.setOnClickListener(this);

		mAmountFrom.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				clearEditTexts();
			}
		});

		mAmountTo.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				clearEditTexts();
			}
		});

		mAmountTo.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					// do your stuff here
					mAmountFrom.setCursorVisible(false);
					mAmountTo.setCursorVisible(false);
				}

				// mAmountFrom.setText("");

				return false;
			}
		});
		mAmountFrom.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					// do your stuff here
					mAmountFrom.setCursorVisible(false);
					mAmountTo.setCursorVisible(false);
				}

				// mAmountFrom.setText("");

				return false;
			}
		});
		mAmountTo.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {

				// mAmountFrom.setText("");

			}
		});

	}

	private void SetTheToolFragment() {
		// Top Bar
		mMainAcitivity.setHeaderTitle(getActivity().getString(
				R.string.preferences));

		// Tools Layout
		mIhaveCurrencyTxtView = (TextView) mRootView
				.findViewById(R.id.txtView_ihavecurrency);
		mIWantCurrencyTxtView = (TextView) mRootView
				.findViewById(R.id.txtView_iwantcurrency);

		mAmountFrom = (EditText) mRootView
				.findViewById(R.id.edttxt_ConvertAmount);
		mAmountTo = (EditText) mRootView.findViewById(R.id.edttxt_SelectAmount);

		mConvertButton = (TextView) mRootView
				.findViewById(R.id.txtView_ConvertBtn);

		mConvertedAmount = (TextView) mRootView
				.findViewById(R.id.txtView_Final_Convert_Amount);

		mSearchButton = (LinearLayout) mRootView
				.findViewById(R.id.search_button);

		// Save Data
		editor = userDetails.edit();

		// Set saved Conversion parameters
		// if (!TextUtils.isEmpty(userDetails.getString("i_have_currency", "")))
		// {

		mIHaveIdString = userDetails.getString("i_have_currency_id", "149");
		mIWantIdString = userDetails.getString("i_want_currency_id", "149");
		
		mIHaveCurrencyString = userDetails.getString("i_have_currency",
				"USD");
		mIWantCurrencyString = userDetails.getString("i_want_currency",
				"USD");
		
		if(mIHaveCurrencyString.equalsIgnoreCase("")){
			mIHaveCurrencyString = "USD";
		}
		
		if(mIWantCurrencyString.equalsIgnoreCase("")){
			mIWantCurrencyString = "USD";
		}
		
		mIhaveCurrencyTxtView.setText(mIHaveCurrencyString);
		fromCode = userDetails.getString("i_have_currency", "USD");
		mIHaveCurrencyString = fromCode;

		editor.putString("i_have_currency_id", mIHaveIdString);
		editor.putString("i_want_currency_id", mIWantIdString);

		mIWantCurrencyTxtView.setText(mIWantCurrencyString);
		toCode = userDetails.getString("i_want_currency", "USD");
		mIWantCurrencyString = toCode;

		// }

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.txtView_ihavecurrency:

			clearEditTexts();
			isFrom = true;
			showConvertFromPopUP(mIhaveCurrencyTxtView);

			break;
		case R.id.txtView_iwantcurrency:
			clearEditTexts();
			isFrom = false;
			showConvertFromPopUP(mIWantCurrencyTxtView);
			break;

		case R.id.txtView_ConvertBtn:
			mAmountFrom.setCursorVisible(false);
			mAmountTo.setCursorVisible(false);
			Validations.hideKeyboard(getActivity());
			validateConverter();
			break;

		case R.id.search_button:
			saveConvertionCriteriaAndGotoChattingScreen();
			callSetPreferencesWS();
			break;
		case R.id.edttxt_SelectAmount:
			mAmountFrom.setText("");
			mAmountTo.setText("");
			mAmountTo.setCursorVisible(true);
			isSelected = true;
			break;
		case R.id.edttxt_ConvertAmount:
			mAmountFrom.setText("");
			mAmountTo.setText("");
			mAmountFrom.setCursorVisible(true);
			isSelected = false;
			break;
		default:
			break;
		}

	}


	private void saveConvertionCriteriaAndGotoChattingScreen() {

		mMainAcitivity.showProgress();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				editor.putString("i_have_currency", mIHaveCurrencyString);
				editor.putString("i_want_currency", mIWantCurrencyString);

				editor.putString("i_have_currency_id", mIHaveIdString);
				editor.putString("i_want_currency_id", mIWantIdString);
				
				Log.e("ConverterFragment", mIHaveIdString);
				Log.e("ConverterFragment", mIWantIdString);
				Log.e("ConverterFragment", mIHaveCurrencyString);
				Log.e("ConverterFragment", mIWantCurrencyString);

				editor.commit();

				mMainAcitivity.hideProgress();
				mMainAcitivity.mTab2Lyt.callOnClick();
			}
		}, 500);

		// mMainAcitivity.gotoMiddleChattingFragment();

	}

	// private void removeFocus() {
	// getActivity().getWindow().setSoftInputMode(
	// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	// }

	private void clearEditTexts() {
		mAmountFrom.setText("");
		mAmountTo.setText("");
		mConvertedAmount.setText("");

	}

	private String mAmountToConvert;
	private String mAmountFromConvert;

	private void validateConverter() {

		String mConvertFromSting = mIhaveCurrencyTxtView.getText().toString()
				.trim();
		String mConvertToString = mIWantCurrencyTxtView.getText().toString()
				.trim();
		mAmountToConvert = mAmountTo.getText().toString().trim();
		mAmountFromConvert = mAmountFrom.getText().toString().trim();

		if (TextUtils.isEmpty(mConvertFromSting)
				|| mConvertFromSting.equalsIgnoreCase(getResources().getString(
						R.string.convert_from))) {

			// Validations.showAlerDialog(
			// getActivity().getString(
			// R.string.please_select_currency_from),
			// getActivity());
			Validations.showSingleBtnDialog(
					getActivity().getString(
							R.string.please_select_currency_from),
					getActivity());

		} else if (TextUtils.isEmpty(mConvertFromSting)
				|| mConvertFromSting.equalsIgnoreCase(getResources().getString(
						R.string.select_to))) {
			// Validations.showAlerDialog(
			// getActivity().getString(R.string.please_select_currency),
			// getActivity());
			Validations.showSingleBtnDialog(
					getActivity().getString(R.string.please_select_currency),
					getActivity());
		} else if (TextUtils.isEmpty(mAmountToConvert)
				&& TextUtils.isEmpty(mAmountFromConvert)) {
			// Validations.showAlerDialog(
			// getActivity().getString(R.string.please_enter_amount),
			// getActivity());
			Validations.showSingleBtnDialog(
					getActivity().getString(R.string.please_enter_amount),
					getActivity());

		} else {
			String mToConvetAmt;
			if (!TextUtils.isEmpty(mAmountFromConvert) && isSelected == false) {

				mToConvetAmt = mAmountFromConvert;
				isFrom = true;

			} else {
				mToConvetAmt = mAmountToConvert;
				isFrom = false;
			}

			if (Validations.isNetworkAvailable(getActivity())) {
				if (!mToConvetAmt.equalsIgnoreCase("0")) {
					callConvertWS(mConvertFromSting, mConvertToString,
							mToConvetAmt);
				} else {

				}

			} else {
				// Validations
				// .showAlerDialog(
				// getActivity()
				// .getString(
				// R.string.no_internet_connection_please_try_again),
				// getActivity());
				Validations
						.showSingleBtnDialog(
								getActivity()
										.getString(
												R.string.no_internet_connection_please_try_again),
								getActivity());
			}
		}

	}

	private void callConvertWS(String mConvertFromSting,
			String mConvertToString, String mAmountToConvert) {
		mMainAcitivity.showProgress();
		Bundle mBundle = new Bundle();
		mBundle.putString(
				"user_id",
				userDetails.getString(
						userDetails.getString("Loggedin_userid", "def"), ""));

		if (isFrom) {
			mBundle.putString("from", fromCode);
			mBundle.putString("to", toCode);
			mBundle.putString("amount", mAmountToConvert);
		} else {
			mBundle.putString("from", toCode);
			mBundle.putString("to", fromCode);
			mBundle.putString("amount", mAmountToConvert);
		}
		mBundle.putString("round_off", "");

		JSONRequestResponse mRequestResponse = new JSONRequestResponse();

		mRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.CURRENCY_CONVERTER, mBundle),
				CURRENCY_CONVERTER, this);

	}

	private int selected = 0;
	private String currencyId = new String();
	private ArrayList<String> cnt = new ArrayList<String>();
	private ArrayList<String> currenciesList;
	private String fromCode, toCode;
	private String mSelectedCurrency;
	private String mIHaveCurrencyString, mIWantCurrencyString;
	private String mIHaveIdString, mIWantIdString;
	private String mSetPreferenceMsg;

	public void showConvertFromPopUP(final TextView mCurrencyTextView) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		if (cnt.size() == 0) {
			for (int i = 0; i < mCurrencyList.size(); i++) {
				cnt.add(mCurrencyList.get(i).getCurrencyName());
				if (mCurrencyList
						.get(i)
						.getCurrencyCode()
						.equalsIgnoreCase(
								mIhaveCurrencyTxtView.getText().toString()
										.trim())) {
					selected = i;
					currencyId = mCurrencyList.get(i).getCurrencyCode();
				}
			}
		}

		builder.setItems(cnt.toArray(new String[cnt.size()]),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						selected = which;
						mSelectedCurrency = mCurrencyList.get(selected)
								.getCurrencyCode().toString();
						mCurrencyTextView.setText(mCurrencyList.get(selected)
								.getCurrencyCode().toString());
						currencyId = mCurrencyList.get(selected)
								.getCurrencyId();

						if (isFrom) {
							fromCode = mCurrencyList.get(selected)
									.getCurrencyCode();

							mIHaveCurrencyString = fromCode;
							mIHaveIdString = mCurrencyList.get(selected)
									.getCurrencyId();

						} else {
							toCode = mCurrencyList.get(selected)
									.getCurrencyCode();

							mIWantCurrencyString = toCode;
							mIWantIdString = mCurrencyList.get(selected)
									.getCurrencyId();

						}

					}
				});

		builder.setCancelable(true);

		AlertDialog alert = builder.create();
		if (cnt.size() > 0)
			alert.show();

	}

	public void callSetPreferencesWS() {

		mMainAcitivity.showProgress();

		Bundle mBundle = new Bundle();

		mBundle.putString("user_id",
				userDetails.getString("Loggedin_userid", "def"));
		mBundle.putString("preference_i_have", mIHaveIdString);
		mBundle.putString("preference_look_for", mIWantIdString);

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.SET_PREFERENCES, mBundle),
				SET_PREFERENCES, this);

	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		// TODO Auto-generated method stub
		mMainAcitivity.hideProgress();
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {

		try {
			JSONObject mJsonObject = new JSONObject(
					response.getString("settings"));

			if (mJsonObject.getString("success").equalsIgnoreCase("1")) {

				switch (requestCode) {
				case CURRENCY:

					JSONArray mArray = new JSONArray(response.getString("data"));
					addCurrenciesToList(mArray);

					break;

				case CURRENCY_CONVERTER:

					JSONObject mConvertData = new JSONObject(
							response.getString("data"));
					setConvertedData(mConvertData);

					break;
				case SET_PREFERENCES:

					// Validations
					// .showAlerDialog(getActivity().getString(
					// R.string.preferences_updated_successfully),
					// getActivity());
					break;

				default:
					break;
				}

			} else {

				// Show Error Message
				// Validations.showAlerDialog(mJsonObject.getString("message"),
				// getActivity());
				Validations.showSingleBtnDialog(
						mJsonObject.getString("message"), getActivity());

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mMainAcitivity.hideProgress();
	}

	private void setConvertedData(JSONObject mConvertData) {

		try {

			// Converte Price
			JSONArray mValues = mConvertData
					.getJSONArray("convert_currency_function");
			JSONObject mDataObj = mValues.getJSONObject(0);

			String convertValues = mDataObj.getString("converted_price")
					.toString();

			// Base Price
			JSONArray mValues2 = mConvertData.getJSONArray("select_base_price");

			JSONObject mDataObj2 = mValues2.getJSONObject(0);

			String baseValue = mDataObj2.getString("base_price");

			mValue = Double.parseDouble(convertValues);
			mBasevalue = Double.parseDouble(baseValue);

			fAmountConversion = new DecimalFormat("##.00");
			fAmount = new DecimalFormat("#####.00000");

			mConvertedAmount.setVisibility(View.VISIBLE);

			// mAmountFrom.setText(fAmount.format(mValue) + " " + toCode);
			// Setting convetion
			if (TextUtils.isEmpty(mAmountFromConvert)) {
				mAmountFrom.setText(fAmountConversion.format(mValue));

			} else if (TextUtils.isEmpty(mAmountToConvert)) {
				mAmountTo.setText(fAmountConversion.format(mValue));
				// Setting Base Value
			}
			if (isFrom) {
				String finalBaseValue = "1 " + fromCode + " = "
						+ fAmount.format(mBasevalue) + " " + toCode;
				mConvertedAmount.setText(finalBaseValue);
			} else {
				String finalBaseValue = "1 " + toCode + " = "
						+ fAmount.format(mBasevalue) + " " + fromCode;
				mConvertedAmount.setText(finalBaseValue);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void addCurrenciesToList(JSONArray mArray) {

		for (int i = 0; i < mArray.length(); i++) {

			JSONObject mCurrencyData = new JSONObject();
			try {
				mCurrencyData = mArray.getJSONObject(i);
				Currency mCurrency = new Currency(mCurrencyData);
				mCurrencyList.add(mCurrency);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if( mIHaveIdString == null || mIHaveIdString.equalsIgnoreCase("") || mIWantIdString == null || mIWantIdString.equalsIgnoreCase("")){
			setCurrencyIdsIfNull();
		}
		
	}
	
	private void setCurrencyIdsIfNull(){
		for (int i = 0; i < mCurrencyList.size(); i++) {
			if (mCurrencyList
					.get(i)
					.getCurrencyCode()
					.equalsIgnoreCase(
							mIhaveCurrencyTxtView.getText().toString()
									.trim())) {
				mIHaveIdString = mCurrencyList.get(i).getCurrencyId();
			}
			
			if (mCurrencyList
					.get(i)
					.getCurrencyCode()
					.equalsIgnoreCase(
							mIWantCurrencyTxtView.getText().toString()
									.trim())) {
				mIWantIdString = mCurrencyList.get(i).getCurrencyId();
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

	}
}
