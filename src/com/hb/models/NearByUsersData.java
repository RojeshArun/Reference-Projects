package com.hb.models;

import org.json.JSONObject;

import com.Utilites.Validations;

public class NearByUsersData {

	private String mStatus, mImageURL, mUserName, mRatingStatus,
			mMatchPreferences, mDistance, mJabberId, mLatitude, mLongit,
			userid,blockeduserId,mDisplayUserName,mRatinggivenbyuser;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public NearByUsersData(JSONObject mJsonData) {

		setmMatchPreferences(Validations.getString(mJsonData,
				"match_preference"));
		setmDistance(Validations.getString(mJsonData, "distance"));
		setmImageURL(Validations.getString(mJsonData, "profile_image"));
		setmRatingStatus(Validations.getString(mJsonData, "avg_rating"));
		setmStatus(Validations.getString(mJsonData, "visible"));
		setmUserName(Validations.getString(mJsonData, "username"));
		setmLatitude(Validations.getString(mJsonData, "latitude"));
		setmLongitude(Validations.getString(mJsonData, "longitude"));
		setmJabberId(Validations.getString(mJsonData, "jabber_id"));
		setUserid(Validations.getString(mJsonData, "user_id"));
		setBlockeduserId(Validations.getString(mJsonData, "blocked_user_id"));
		setmDisplayUserName(Validations.getString(mJsonData, "display_name"));
		setmRatinggivenbyuser(Validations.getString(mJsonData, "rating_given_by_login_user"));
		

	}

	public String getmStatus() {
		return mStatus;
	}

	public void setmStatus(String mStatus) {
		this.mStatus = mStatus;
	}

	public String getmImageURL() {
		return mImageURL;
	}

	public void setmImageURL(String mImageURL) {
		this.mImageURL = mImageURL;
	}

	public String getmUserName() {
		return mUserName;
	}

	public void setmUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getmRatingStatus() {
		return mRatingStatus;
	}

	public void setmRatingStatus(String mRatingStatus) {
		this.mRatingStatus = mRatingStatus;
	}

	public String getmLocation() {
		return getmMatchPreferences();
	}

	public void setmDistance(String mDistance) {
		this.mDistance = mDistance;
	}

	public String getmDistance() {
		return mDistance;
	}

	public String getmMatchPreferences() {
		return mMatchPreferences;
	}

	public void setmMatchPreferences(String mMatchPreferences) {
		this.mMatchPreferences = mMatchPreferences;
	}

	public String getmLongit() {
		return mLongit;
	}

	public void setmLongitude(String mLongit) {
		this.mLongit = mLongit;
	}

	public String getmLatitude() {
		return mLatitude;
	}

	public void setmLatitude(String mLatitude) {
		this.mLatitude = mLatitude;
	}

	public String getmJabberId() {
		return mJabberId;
	}

	public void setmJabberId(String mJabberId) {
		this.mJabberId = mJabberId;
	}

	public String getBlockeduserId() {
		return blockeduserId;
	}

	public void setBlockeduserId(String blockeduserId) {
		this.blockeduserId = blockeduserId;
	}

	public String getmDisplayUserName() {
		return mDisplayUserName;
	}

	public void setmDisplayUserName(String mDisplayUserName) {
		this.mDisplayUserName = mDisplayUserName;
	}

	public String getmRatinggivenbyuser() {
		return mRatinggivenbyuser;
	}

	public void setmRatinggivenbyuser(String mRatinggivenbyuser) {
		this.mRatinggivenbyuser = mRatinggivenbyuser;
	}

}
