package com.hb.fragments;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.models.Currency;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ToolsFragment extends Fragment implements OnClickListener,
		IParseListener, OnCheckedChangeListener {

	private static final int CURRENCY = 200;
	private static final int CURRENCY_CONVERTER = 201;

	private MainActivity mMainAcitivity;
	private Context mContext;

	private TextView mConvertFrom, mSelectTo, mConvertButton, mConvertedAmount,
			mConvertedCurrency;
	private EditText mAmount;
	private List<Currency> mCurrencyList = new ArrayList<Currency>();

	private CheckBox mRoundOffCheckBox;

	private View mRootView;
	private Boolean isFrom = true;
	DecimalFormat fAmount;
	Boolean ifRoundOff = true;
	Double mValue;
	private RelativeLayout mAmountLyt;

	public ToolsFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMainAcitivity = (MainActivity) getActivity();
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.activity_tool, null);

		SetTheToolFragment();
		if (Validations.isNetworkAvailable(getActivity())) {
			callCurrenciesWS();
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
		setOnClickListeners();

		return mRootView;
	}

	private void callCurrenciesWS() {
		mMainAcitivity.showProgress();
		JSONRequestResponse mRequestResponse = new JSONRequestResponse();
		mRequestResponse.getResponse(Webservices.CURRENCY, CURRENCY, this);

	}

	private void setOnClickListeners() {

		mConvertFrom.setOnClickListener(this);
		mSelectTo.setOnClickListener(this);
		// mRoundOffCheckBox.setOnCheckedChangeListener(this);
		mRoundOffCheckBox.setOnClickListener(this);
		mConvertButton.setOnClickListener(this);

	}

	private void SetTheToolFragment() {
		// Top Bar
		mMainAcitivity.setHeaderTitle(getActivity().getString(R.string.tools));
//		mMainAcitivity.mRightButOneButton.setVisibility(View.INVISIBLE);
//		mMainAcitivity.mRightMostButton.setVisibility(View.INVISIBLE);

		// Tools Layout
		mConvertFrom = (TextView) mRootView
				.findViewById(R.id.txtView_ConvertAmount);
		mSelectTo = (TextView) mRootView
				.findViewById(R.id.txtView_SelectAmount);
		mAmount = (EditText) mRootView.findViewById(R.id.txtView_EnterAmount);

		mRoundOffCheckBox = (CheckBox) mRootView
				.findViewById(R.id.check_round_off);

		mConvertButton = (TextView) mRootView
				.findViewById(R.id.txtView_ConvertBtn);

		mConvertedAmount = (TextView) mRootView
				.findViewById(R.id.txtView_Final_Convert_Amount);
		// mConvertedCurrency = (TextView) mRootView
		// .findViewById(R.id.txtView_Conversion_Unit);

		mAmountLyt = (RelativeLayout) mRootView
				.findViewById(R.id.conveted_amount);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.txtView_ConvertAmount:
			showConvertFromPopUP(mConvertFrom);

			break;
		case R.id.txtView_SelectAmount:

			isFrom = false;
			showConvertFromPopUP(mSelectTo);

			break;

		case R.id.check_round_off:
			if (mValue != null) {
				displayRoundOffAmount();
			}

			break;

		case R.id.txtView_ConvertBtn:

			validateConverter();

			break;
		default:
			break;
		}

	}

	private void displayRoundOffAmount() {

		if (mRoundOffCheckBox.isChecked()) {
			mConvertedAmount.setText(mValue.intValue() + " " + toCode);
			ifRoundOff = false;
		} else {
			mConvertedAmount.setText(fAmount.format(mValue) + " " + toCode);
		}

	}

	private void validateConverter() {

		String mConvertFromSting = mConvertFrom.getText().toString().trim();
		String mConvertToString = mSelectTo.getText().toString().trim();
		String mAmountToConvert = mAmount.getText().toString().trim();

		if (TextUtils.isEmpty(mConvertFromSting)
				|| mConvertFromSting.equalsIgnoreCase(getResources().getString(
						R.string.convert_from))) {
			Validations.showSingleBtnDialog(
					getActivity().getString(
							R.string.please_select_currency_from),
					getActivity());
//			Validations.showAlerDialog(
//					getActivity().getString(
//							R.string.please_select_currency_from),
//					getActivity());

		} else if (TextUtils.isEmpty(mConvertFromSting)
				|| mConvertFromSting.equalsIgnoreCase(getResources().getString(
						R.string.select_to))) {
			Validations.showSingleBtnDialog(
					getActivity().getString(R.string.please_select_currency),
					getActivity());
//			Validations.showAlerDialog(
//					getActivity().getString(R.string.please_select_currency),
//					getActivity());
		} else if (TextUtils.isEmpty(mAmountToConvert)) {
			Validations.showSingleBtnDialog(
					getActivity().getString(R.string.please_enter_amount),
					getActivity());
//			Validations.showAlerDialog(
//					getActivity().getString(R.string.please_enter_amount),
//					getActivity());

		} else {
			if (Validations.isNetworkAvailable(getActivity())) {
				callConvertWS(mConvertFromSting, mConvertToString,
						mAmountToConvert);
			} else {
				Validations.showSingleBtnDialog(
						getActivity()
						.getString(
								R.string.no_internet_connection_please_try_again),
				getActivity());
//				Validations
//						.showAlerDialog(
//								getActivity()
//										.getString(
//												R.string.no_internet_connection_please_try_again),
//								getActivity());
			}
		}

	}

	private void callConvertWS(String mConvertFromSting,
			String mConvertToString, String mAmountToConvert) {
		mMainAcitivity.showProgress();
		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", "2");
		mBundle.putString("from", fromCode);
		mBundle.putString("to", toCode);
		mBundle.putString("amount", mAmountToConvert);
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

	public void showConvertFromPopUP(final TextView mCurrencyTextView) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		if (cnt.size() == 0) {
			for (int i = 0; i < mCurrencyList.size(); i++) {
				cnt.add(mCurrencyList.get(i).getCurrencyName());
				if (mCurrencyList
						.get(i)
						.getCurrencyCode()
						.equalsIgnoreCase(
								mConvertFrom.getText().toString().trim())) {
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
						mCurrencyTextView.setText(mCurrencyList.get(selected)
								.getCurrencyName().toString());
						currencyId = mCurrencyList.get(selected)
								.getCurrencyId();

						if (isFrom) {
							fromCode = mCurrencyList.get(selected)
									.getCurrencyCode();

						} else {
							toCode = mCurrencyList.get(selected)
									.getCurrencyCode();
						}

					}
				});

		builder.setCancelable(true);

		AlertDialog alert = builder.create();
		if (cnt.size() > 0)
			alert.show();

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

					JSONArray mConvertData = new JSONArray(
							response.getString("data"));
					setConvertedData(mConvertData);

					break;

				default:
					break;
				}

			} else {
				Validations.showSingleBtnDialog(mJsonObject.getString("message"),
						getActivity());
				// Show Error Message
//				Validations.showAlerDialog(mJsonObject.getString("message"),
//						getActivity());

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMainAcitivity.hideProgress();
	}

	private void setConvertedData(JSONArray mConvertData) {
		JSONObject mValues;
		try {
			mValues = mConvertData.getJSONObject(0);

			String convertValues = mValues.getString("converted_price")
					.toString();
			// Double mValue = Double.parseDouble(convertValues);

			mValue = Double.parseDouble(convertValues);

			// DecimalFormat fAmount = new DecimalFormat("##.000");
			fAmount = new DecimalFormat("##.000");
			if (mRoundOffCheckBox.isChecked()) {
				mConvertedAmount.setText(mValue.intValue() + " " + toCode);
			} else {
				mConvertedAmount.setText(fAmount.format(mValue) + " " + toCode);
			}
			// mConvertedCurrency.setText(toCode);

			mAmountLyt.setVisibility(View.VISIBLE);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

	}
}
