package com.hb.webserviceutilities;

import java.net.URLEncoder;

import android.os.Bundle;
import android.util.Log;

public class Webservices {

	// Rojesh
	// 497421007060851
	// GOPI id
	// 709246459090116
//	public static String FACEBOOKAPPID = "497421007060851";
	
	public static String FACEBOOKAPPID = "1463871263902706";

	// Testing
	// public static String TWITTERCONSUMERKEY = "E92dbfv7BIHGNUdrWWEmA";
	//
	// public static String TWITTERCONSUMERSECRETKEY =
	// "8DxuiEzUqgUcfi5NhynGMBy9cRWfzRSPGXq88ghwa4";

	public static String TWITTERCONSUMERKEY = "ISLgTOAsruaWQMaZpot3k24iI";

	public static String TWITTERCONSUMERSECRETKEY = "QmWQ4lqaqQj6QuSCHKwzcBO635I8z4mSblYVIuuJQqSU8kWYld";
	
	

	// String twitter_consumer_key = "ISLgTOAsruaWQMaZpot3k24iI";
	// String twitter_secret_key =
	// "QmWQ4lqaqQj6QuSCHKwzcBO635I8z4mSblYVIuuJQqSU8kWYld";

	// Chat Server Host and Service

//	public static final String HOST = "66.85.130.226";
	
	public static final String HOST = "54.86.232.63";

//	public static final String SERVICE = "staging.projectspreview.com";

	public static final String SERVICE = "54.86.232.63";
	
	// public static final String HOST = "staging.projectspreview.com";
	//
	// public static final String SERVICE = "66.85.130.226";

	// Chat Domain...

//	public static final String CHAT_DOMAIN = "@barter.hiddenbrains.in";
	
	public static final String CHAT_DOMAIN = "@gobarter.cash.com";

	// Chat Port
	public static final int PORT = 5222;

	// Webservices Links

	public static final String LOCAL_URL = "http://local.configure.it/locationbasedchatapp/WS/";

	public static final String ONLINE_URL = "http://108.170.62.152/webservice/locationbasedchatapp/WS/";
	
	public static final String LIVE_URL = "http://gobartr.net/mobile/WS/";

	public static final String BASE_URL = LIVE_URL;

	public static final String FACEBOOKLOGIN = BASE_URL + "fb_login/?";

	public static final String IS_FB_LOGIN = BASE_URL + "is_fb_alredy/?";

	public static final String TWITTERLOGIN = BASE_URL + "twitter_login/?";

	public static final String IS_TWITTER_LOGIN = BASE_URL
			+ "is_twitter_alredy/?";

	// GCM

	public static final String CHAT_NOTIFICATION = BASE_URL
			+ "offline_chat_notification/?";

	public static String LOGIN = BASE_URL + "login/?";

	public static String REGISTRATION = BASE_URL + "registration/?";

	public static String EDIT_MY_PROFILE = BASE_URL + "edit_my_profile/?";

	public static String VIEW_MY_PROFILE = BASE_URL + "view_my_profile/?";

	public static String FORGOT_PASSWORD = BASE_URL + "forgot_password/?";

	public static String CHANGE_MY_PASSWORD = BASE_URL + "change_password/?";

	public static String MAP_PINS = BASE_URL + "map_screen_new/?";

	public static String CURRENCY = BASE_URL + "currency_name/?";

	public static String CURRENCY_CONVERTER = BASE_URL + "convert_currency/?";

	public static String GET_DETAILFROMJABBERID = BASE_URL
			+ "get_detail_from_jabberid/?";

	public static String COUNTRY_TABLE = BASE_URL + "country/?";

	public static String STATE_TABLE = BASE_URL + "state/?";

	public static String RESEND_PIN = BASE_URL + "resend_pin/?";
	
	public static String DELETE_USER = BASE_URL + "delete_user/?";

	public static String VALIDATE_PIN = BASE_URL + "validate_rating_pin/?";

	public static String ACCOUNT_VERIFICATION = BASE_URL
			+ "account_verification/?";
	public static String BLOCKED_USER = BASE_URL + "blocked_list/?";

	public static String CHECK_HIGHER_RATING = BASE_URL
			+ "check_high_rating_status/?";

	public static String SET_HIGHER_RATING = BASE_URL + "set_higher_rating/?";

	public static String POST_RATINGS = BASE_URL + "post_ratings/?";

	public static String SUPPORT_PAGE = BASE_URL
			+ "static_text/?&page_code=support";

	public static String GET_PREFERENCES = BASE_URL + "get_preferences/?";

	public static String SET_PREFERENCES = BASE_URL + "set_preference/?";

	public static String BLOCK_UNBLOCK_USER = BASE_URL + "block_or_unblock/?";

	public static String UPDATE_USERLOCATION = BASE_URL + "set_location_view/?";

	public static String SHARE_LOCATION = BASE_URL + "share_location/?";

	@SuppressWarnings("deprecation")
	public static String encodeGETUrl(Bundle parameters) {
		StringBuilder sb = new StringBuilder();

		if (parameters != null && parameters.size() > 0) {
			boolean first = true;
			for (String key : parameters.keySet()) {
				if (key != null) {

					if (first) {
						first = false;
					} else {
						sb.append("&");
					}
					String value = "";
					Object object = parameters.get(key);
					if (object != null) {
						value = String.valueOf(object);
					}

					try {
						sb.append(URLEncoder.encode(key, "UTF-8") + "="
								+ URLEncoder.encode(value, "UTF-8"));
					} catch (Exception e) {
						sb.append(URLEncoder.encode(key) + "="
								+ URLEncoder.encode(value));
					}
				}
			}
		}
		return sb.toString();
	}

	public static String encodeUrl(String url, Bundle mParams) {
		String URL;
		URL = url + encodeGETUrl(mParams);
		Log.e("Webserveice", URL);
		return URL;
	}
}
