package com.hb.models;

import org.json.JSONObject;

import com.Utilites.Validations;

public class Preferences {

	private String mPreferenceMasterID;
	private String mName;
	private String mSequenceNum;

	public Preferences(JSONObject mPreferencesData) {
		setmPreferenceMasterID(Validations.getString(mPreferencesData,
				"preference_master_id"));
		setmName(Validations.getString(mPreferencesData, "name"));
		setmSequenceNum(Validations.getString(mPreferencesData, "seq_no"));
	}

	public String getmPreferenceMasterID() {
		return mPreferenceMasterID;
	}

	public void setmPreferenceMasterID(String mPreferenceMasterID) {
		this.mPreferenceMasterID = mPreferenceMasterID;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getmSequenceNum() {
		return mSequenceNum;
	}

	public void setmSequenceNum(String mSequenceNum) {
		this.mSequenceNum = mSequenceNum;
	}

}
