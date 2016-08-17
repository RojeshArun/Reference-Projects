package com.hb.models;

import org.json.JSONObject;

import com.Utilites.Validations;

public class State {

	private String stateID;
	private String state;
	private String stateCode;

	public State(JSONObject mStateData) {
		setStateID((Validations.getString(mStateData, "state_id")));
		setStateCode((Validations.getString(mStateData, "state_code")));
		setState((Validations.getString(mStateData, "state")));

	}

	public String getStateID() {
		return stateID;
	}

	public void setStateID(String stateID) {
		this.stateID = stateID;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

}
