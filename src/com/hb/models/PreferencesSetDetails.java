package com.hb.models;

import org.json.JSONObject;

import com.Utilites.Validations;

public class PreferencesSetDetails {

	private String preferencesMasterId;
	private String preferencesName;
	private String preferencesSeqNum;

	public PreferencesSetDetails(JSONObject mPreferenceObject) {
		setPreferencesMasterId(Validations.getString(mPreferenceObject,
				"preference_master_id"));
		setPreferencesName(Validations.getString(mPreferenceObject, "name"));
		setPreferencesSeqNum(Validations.getString(mPreferenceObject, "seq_no"));
	}

	public String getPreferencesMasterId() {
		return preferencesMasterId;
	}

	public void setPreferencesMasterId(String preferencesMasterId) {
		this.preferencesMasterId = preferencesMasterId;
	}

	public String getPreferencesName() {
		return preferencesName;
	}

	public void setPreferencesName(String preferencesName) {
		this.preferencesName = preferencesName;
	}

	public String getPreferencesSeqNum() {
		return preferencesSeqNum;
	}

	public void setPreferencesSeqNum(String preferencesSeqNum) {
		this.preferencesSeqNum = preferencesSeqNum;
	}

}
