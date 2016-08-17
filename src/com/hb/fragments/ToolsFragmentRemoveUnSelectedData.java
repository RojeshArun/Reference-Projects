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

public class ToolsFragmentRemoveUnSelectedData extends Fragment implements OnClickListener,
		IParseListener, OnCheckedChangeListener {

	private static final int CURRENCY = 200;
	private static final int CURRENCY_CONVERTER = 201;

	private MainActivity mMainAcitivity;
	private Context mContext;

	private TextView mConvertFrom, mSelectTo, mConvertButton, mConvertedAmount,
			mConvertedCurrency;
	private EditText mAmount;
	private List<Currency> mCurrencyConvertFrom = new ArrayList<Currency>();
	private List<Currency> mCurrencyConvertTo = new ArrayList<Currency>();

	private CheckBox mRoundOffCheckBox;

	private View mRootView;
	private Boolean isFrom = true;
	DecimalFormat fAmount;
	Boolean ifRoundOff = true;
	Double mValue;
	private RelativeLayout mAmountLyt;

	public ToolsFragmentRemoveUnSelectedData() {

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
		mRoundOffCheckBox.setOnCheckedChangeListener(this);
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

			callCurrenciesWS();
			isFrom = true;
			break;
		case R.id.txtView_SelectAmount:

			callCurrenciesWS();
			isFrom = false;

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
			callConvertWS(mConvertFromSting, mConvertToString, mAmountToConvert);
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

	private int selectedFrom = -1;
	private int selectedTo = -1;
	private String currencyId = new String();
	private ArrayList<String> cnt = new ArrayList<String>();
	private ArrayList<String> currenciesList;
	private String fromCode, toCode;

	public void showConvertFromPopUP(final TextView mCurrencyTextView) {
		cnt = new ArrayList<String>();
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		for (int i = 0; i < mCurrencyConvertFrom.size(); i++) {
			cnt.add(mCurrencyConvertFrom.get(i).getCurrencyName());
			if (mCurrencyConvertFrom.get(i).getCurrencyCode()
					.equalsIgnoreCase(mConvertFrom.getText().toString().trim())) {
				selectedFrom = i;
				currencyId = mCurrencyConvertFrom.get(i).getCurrencyCode();
			}

			if (mTempConvertTo != null)
				if (mCurrencyConvertFrom.get(i).getCurrencyId()
						.equalsIgnoreCase(mTempConvertTo.getCurrencyId())) {
					selectedTo = i;
				}

		}

		if (selectedTo != -1) {
			cnt.remove(selectedTo);
			mCurrencyConvertFrom.remove(mTempConvertTo);

		}

		builder.setItems(cnt.toArray(new String[cnt.size()]),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						selectedFrom = which;

						mTempConvertFrom = new Currency();
						mTempConvertFrom = mCurrencyConvertFrom.get(which);

						mCurrencyTextView
								.setText(mCurrencyConvertFrom.get(selectedFrom)
										.getCurrencyName().toString());
						currencyId = mCurrencyConvertFrom.get(selectedFrom)
								.getCurrencyId();

						fromCode = mCurrencyConvertFrom.get(selectedFrom)
								.getCurrencyCode();

					}
				});

		builder.setCancelable(true);

		AlertDialog alert = builder.create();
		if (cnt.size() > 0)
			alert.show();

	}

	private Currency mTempConvertTo, mTempConvertFrom;

	public void showConvertToPopUP(final TextView mCurrencyTextView) {
		cnt = new ArrayList<String>();
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		for (int i = 0; i < mCurrencyConvertTo.size(); i++) {
			cnt.add(mCurrencyConvertTo.get(i).getCurrencyName());
			if (mCurrencyConvertTo.get(i).getCurrencyCode()
					.equalsIgnoreCase(mConvertFrom.getText().toString().trim())) {
				selectedTo = i;
				currencyId = mCurrencyConvertTo.get(i).getCurrencyCode();
			}
			if (mTempConvertTo != null)
				if (mCurrencyConvertTo.get(i).getCurrencyId()
						.equalsIgnoreCase(mTempConvertFrom.getCurrencyId())) {
					selectedFrom = i;
				}
		}

		if (selectedFrom != -1) {
			cnt.remove(selectedFrom);
			mCurrencyConvertTo.remove(mTempConvertFrom);

		}

		builder.setItems(cnt.toArray(new String[cnt.size()]),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						selectedTo = which;
						mTempConvertTo = new Currency();
						mTempConvertTo = mCurrencyConvertTo.get(which);

						mCurrencyTextView.setText(mCurrencyConvertTo
								.get(selectedTo).getCurrencyName().toString());
						currencyId = mCurrencyConvertTo.get(selectedTo)
								.getCurrencyId();

						toCode = mCurrencyConvertTo.get(selectedTo)
								.getCurrencyCode();

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

					if (isFrom)
						showConvertFromPopUP(mConvertFrom);
					else
						showConvertToPopUP(mSelectTo);

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

				// Show Error Message
//				Validations.showAlerDialog(mJsonObject.getString("message"),
//						getActivity());
				Validations.showSingleBtnDialog(mJsonObject.getString("message"),
						getActivity());

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
		mCurrencyConvertFrom.clear();
		mCurrencyConvertTo.clear();
		for (int i = 0; i < mArray.length(); i++) {

			JSONObject mCurrencyData = new JSONObject();
			try {
				mCurrencyData = mArray.getJSONObject(i);
				Currency mCurrency = new Currency(mCurrencyData);

				mCurrencyConvertFrom.add(mCurrency);
				mCurrencyConvertTo.add(mCurrency);

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
