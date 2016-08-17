package com.hb.models;

import org.json.JSONObject;

import com.Utilites.Validations;

public class Country {

	private String country;
	private String countryID;
	private String countryCode;
	private String stdcode;

	public Country(JSONObject mCountryData) {
		setCountry(Validations.getString(mCountryData, "country"));
		setCountryCode(Validations.getString(mCountryData, "country_code"));
		setCountryID(Validations.getString(mCountryData, "country_id"));
		setStdcode(Validations.getString(mCountryData, "dial_code"));

	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryID() {
		return countryID;
	}

	public void setCountryID(String countryID) {
		this.countryID = countryID;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getStdcode() {
		return stdcode;
	}

	public void setStdcode(String stdcode) {
		this.stdcode = stdcode;
	}

}
