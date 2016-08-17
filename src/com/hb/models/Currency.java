package com.hb.models;

import org.json.JSONObject;

import com.Utilites.Validations;

public class Currency {

	public Currency(JSONObject mCurrencyData) {

		setCurrencyId((Validations.getString(mCurrencyData,
				"currency_id")));
		setCurrencyName((Validations.getString(mCurrencyData,
				"currency_name")));
		setCurrencyCode((Validations.getString(mCurrencyData,
				"currency_code")));
		setCurrencySymbol((Validations.getString(mCurrencyData,
				"currency_symbol")));
		
	}
	
	public Currency() {
		// TODO Auto-generated constructor stub
	}

	private String currencyId, currencyName, currencyCode, currencySymbol;

	public String getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

}
