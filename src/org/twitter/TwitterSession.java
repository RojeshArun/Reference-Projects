package org.twitter;

import android.content.Context;
import android.content.SharedPreferences;

public class TwitterSession {
	private SharedPreferences sharedPref;

	private static final String TWEET_AUTH_KEY = "hb_twitter_auth_key";
	private static final String TWEET_AUTH_SECRET_KEY = "hb_twitter_auth_secret_key";
	 private static final String TWEET_SCREEN_NAME = "hb_twitter_screen_name";
	 private static final String TWEET_USER_ID = "hb_twitter_user_id";
	 
	 
	private static final String SHARED = "Twitter_Preferences";

	public TwitterSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
	}

	public void storeAccessToken(TwitterAccessToken twitterAccessToken) {
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(TWEET_AUTH_KEY, twitterAccessToken.getToken());
		editor.putString(TWEET_AUTH_SECRET_KEY,
				twitterAccessToken.getTokenSecret());
		
		
		 editor.putString(TWEET_SCREEN_NAME, twitterAccessToken.getScreeName());
		 editor.putString(TWEET_USER_ID, twitterAccessToken.getUserId());

		editor.commit();
	}

	public void resetAccessToken() {
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(TWEET_AUTH_KEY, null);
		editor.putString(TWEET_AUTH_SECRET_KEY, null);
		editor.putString(TWEET_SCREEN_NAME, null);
		editor.putString(TWEET_USER_ID, null);

		editor.commit();
	}

	// public String getUsername() {
	// return sharedPref.getString(TWEET_USER_NAME, "");
	// }

	public TwitterAccessToken getAccessToken() {
		String token = sharedPref.getString(TWEET_AUTH_KEY, null);
		String tokenSecret = sharedPref.getString(TWEET_AUTH_SECRET_KEY, null);
		
		String screenName = sharedPref.getString(TWEET_SCREEN_NAME, null);
		String userId = sharedPref.getString(TWEET_USER_ID, null);

		if (token != null && tokenSecret != null)
			return new TwitterAccessToken(token, tokenSecret, screenName, userId);
		else
			return null;
	}

}