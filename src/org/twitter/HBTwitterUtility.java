package org.twitter;

import it.configure.imageloader.ImageLoader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import oauth.json.HBJSONParser;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.twitter.TwitterApp.TwDialogListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HBTwitterUtility {
	private Activity activity;
	private TwitterApp mTwitter;

	private ProgressDialog mProgress;

	private String tweetMessage = "";
	private String screenName = "";
	private int IMAGE_TYPE;

	private int METHOD = -1;

	private static final int TWEET = 0, UPLOAD = 1, LOGIN = 2,
			USER_DETAILS = 3, FOLLOW = 4, UNFOLLOW = 5, FRIENDS = 6,
			FOLLOWERS = 7, TWEETS = 8, TAGGED_TWEETS = 9;

	private final int TYPE_FILE = 1, TYPE_URL = 2, TYPE_BITMAP = 3;

	private File file;
	private String url;
	private Bitmap bitmap;

	private TwitterCallbackListener twitterCallbackListener;
	private String REQUESTCODE;

	private final String TWITTER_API = "https://api.twitter.com";
	private final String TWITTER_UPLOAD_API = TWITTER_API;// "https://upload.twitter.com";

	private Bundle parameters = new Bundle();
	private final CommonsHttpOAuthConsumer mHttpOauthConsumer;

	private boolean showDialog = true;

	private String myScreenName = "";

	/**
	 * @param a
	 *            context of Activity in which you want to use this Utility
	 * @param twitter_consumer_key
	 *            your twiiter application's consumer key
	 * @param twitter_secret_key
	 *            your twiiter application's secret key
	 * @param twitterCallbackListener
	 *            callbackListener which gives you the response of your queries
	 * 
	 */
	public HBTwitterUtility(Activity a, String twitter_consumer_key,
			String twitter_secret_key,
			TwitterCallbackListener twitterCallbackListener) {

		activity = a;
		this.twitterCallbackListener = twitterCallbackListener;

		mProgress = new ProgressDialog(activity);
		mProgress.setMessage("Loading...");

		mTwitter = new TwitterApp(activity, twitter_consumer_key,
				twitter_secret_key, twitterCallbackListener);
		mTwitter.setListener(mTwLoginDialogListener);
		mHttpOauthConsumer = mTwitter.getOAuthConsumer();

		isTwitterAppInstalled = isTwitterAppInstalled();
	}

	/**
	 * @param a
	 *            context of Activity in which you want to use this Utility
	 * @param twitter_consumer_key
	 *            your twiiter application's consumer key
	 * @param twitter_secret_key
	 *            your twiiter application's secret key
	 * @param twitterCallbackListener
	 *            callbackListener which gives you the response of your queries
	 * 
	 * @param showDialog
	 *            dialog for user to enter data
	 */
	public HBTwitterUtility(FragmentActivity a, String twitter_consumer_key,
			String twitter_secret_key,
			TwitterCallbackListener twitterCallbackListener, boolean showDialog) {
		this(a, twitter_consumer_key, twitter_secret_key,
				twitterCallbackListener);
		this.showDialog = showDialog;
	}

	/**
	 * @param requestCode
	 *            request code
	 */
	public void loginTwitter(String requestCode) {

		parameters.clear();
		METHOD = LOGIN;
		REQUESTCODE = requestCode;
		isSessionValid();
	}

	/**
	 * @return <b>true</b> if you logout successfully <br/>
	 *         <b>false</b> if session is expired or you are not logged in <br/>
	 */
	public boolean logoutTwitter() {
		return mTwitter.resetAccessToken();
	}

	/**
	 * 
	 */
	public void getUserDetails(String requestCode) {
		parameters.clear();
		METHOD = USER_DETAILS;
		REQUESTCODE = requestCode;
		isSessionValid();
	}

	/**
	 * 
	 */
	public void getUserTweets(String screenName, String requestCode) {
		// this.screenName = screenName;
		parameters.clear();

		if (!TextUtils.isEmpty(screenName))
			parameters.putString("screen_name", screenName);

		METHOD = TWEETS;
		REQUESTCODE = requestCode;
		isSessionValid();
	}

	/**
	 * 
	 */
	public void getUserTaggedTweets(String searchQuery, String requestCode) {
		parameters.clear();

		if (TextUtils.isEmpty(searchQuery)) {
			handleUserRequest("Search Query Cannot Be Empty");
		} else {
			parameters.putString("q", searchQuery);

			METHOD = TAGGED_TWEETS;
			REQUESTCODE = requestCode;
			isSessionValid();
		}
	}

	/**
	 * @param status
	 * @param requestCode
	 */
	public void tweet(String status, String requestCode) {

		if (isTwitterAppInstalled) {
			shareUsingIntent(status, null);
		} else {
			REQUESTCODE = requestCode;
			METHOD = TWEET;
			tweetMessage = status;
			isSessionValid();
		}
	}

	/**
	 * @param url
	 * @param status
	 * @param requestCode
	 */
	public void shareImage(String url, String status, String requestCode) {

		this.url = url;
		IMAGE_TYPE = TYPE_URL;
		share(status, requestCode);
	}

	/**
	 * @param file
	 * @param status
	 * @param requestCode
	 */
	public void shareImage(File file, String status, String requestCode) {

		if (isTwitterAppInstalled) {
			shareUsingIntent(status, file.getAbsolutePath());

		} else {
			this.file = file;
			IMAGE_TYPE = TYPE_FILE;
			share(status, requestCode);
		}
	}

	/**
	 * @param bitmap
	 * @param status
	 * @param requestCode
	 */
	public void shareImage(Bitmap bitmap, String status, String requestCode) {
		this.bitmap = bitmap;
		IMAGE_TYPE = TYPE_BITMAP;
		share(status, requestCode);
	}

	/**
	 * @param bitmap
	 * @param status
	 * @param requestCode
	 */
	public void shareImage(int resourceId, String status, String requestCode) {
		bitmap = null;
		try {
			bitmap = BitmapFactory.decodeResource(activity.getResources(),
					resourceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		shareImage(bitmap, status, requestCode);
	}

	/**
	 * @param status
	 * @param requestCode
	 */
	private void share(String status, String requestCode) {
		REQUESTCODE = requestCode;
		tweetMessage = status;
		METHOD = UPLOAD;
		isSessionValid();
	}

	/**
	 * 
	 */
	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		@Override
		public void onComplete(String value) {
			handleUserRequest(value);
		}

		@Override
		public void onError(String value) {
			LinkedHashMap<String, Object> mapSettings = new LinkedHashMap<String, Object>();
			mapSettings.put("error", value);
			ArrayList<Object> listResponseData = new ArrayList<Object>();
			listResponseData.add(mapSettings);
			if (twitterCallbackListener != null)
				twitterCallbackListener.twitterCallback(false, value,
						REQUESTCODE, listResponseData);
		}
	};

	private void handleUserRequest(String value) {
		switch (METHOD) {
		case TWEET:
			tweet();
			break;

		case UPLOAD:
			upload();
			break;

		case USER_DETAILS:
			lookUp();
			break;

		case TWEETS:
			tweets();
			break;

		case TAGGED_TWEETS:
			taggedTweets();
			break;

		case FOLLOW:
			follow();
			break;

		case UNFOLLOW:
			unFollow();
			break;

		case FOLLOWERS:
			followers();
			break;

		case FRIENDS:
			friends();
			break;

		case LOGIN:
			if (twitterCallbackListener != null) {
				LinkedHashMap<String, Object> mapSettings = new LinkedHashMap<String, Object>();
				mapSettings.put("response", value);
				ArrayList<Object> listResponseData = new ArrayList<Object>();
				listResponseData.add(mapSettings);
				twitterCallbackListener.twitterCallback(true, value,
						REQUESTCODE, listResponseData);
			}
			break;
		}
	}

	/**
	 * "https://api.twitter.com/1.1/statuses/user_timeline.json"
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void tweets() {
		// parameters.putString("screen_name",
		// TextUtils.isEmpty(screenName)?mTwitter.getSession().getAccessToken().getScreeName():screenName);

		TWITTER_PARMS params[] = { TWITTER_PARMS.VERSION1_1,
				TWITTER_PARMS.STATUSES, TWITTER_PARMS.USER_TIMELINE,
				TWITTER_PARMS.GET, TWITTER_PARMS.QUERY };

		if (Build.VERSION.SDK_INT < 11) {
			new TwitterQueryTask().execute(params);
		} else {
			new TwitterQueryTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
	}

	/**
	 * "https://api.twitter.com/1.1/search/tweets.json"
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void taggedTweets() {
		// parameters.putString("screen_name",
		// TextUtils.isEmpty(screenName)?mTwitter.getSession().getAccessToken().getScreeName():screenName);

		TWITTER_PARMS params[] = { TWITTER_PARMS.VERSION1_1,
				TWITTER_PARMS.SEARCH, TWITTER_PARMS.TWEETS, TWITTER_PARMS.GET,
				TWITTER_PARMS.QUERY };

		if (Build.VERSION.SDK_INT < 11) {
			new TwitterQueryTask().execute(params);
		} else {
			new TwitterQueryTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
	}

	/**
	 * https://api.twitter.com/1.1/followers/list.json
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void followers() {
		parameters.clear();

		TWITTER_PARMS params[] = { TWITTER_PARMS.VERSION1_1,
				TWITTER_PARMS.FOLLOWERS, TWITTER_PARMS.LIST, TWITTER_PARMS.GET,
				TWITTER_PARMS.QUERY };

		if (Build.VERSION.SDK_INT < 11) {
			new TwitterQueryTask().execute(params);
		} else {
			new TwitterQueryTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
	}

	/**
	 * https://api.twitter.com/1.1/friends/list.json
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void friends() {
		parameters.clear();

		TWITTER_PARMS params[] = { TWITTER_PARMS.VERSION1_1,
				TWITTER_PARMS.FRIENDS, TWITTER_PARMS.LIST, TWITTER_PARMS.GET,
				TWITTER_PARMS.QUERY };

		if (Build.VERSION.SDK_INT < 11) {
			new TwitterQueryTask().execute(params);
		} else {
			new TwitterQueryTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
	}

	/**
	 * https://api.twitter.com/1.1/friendships/destroy.json
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void unFollow() {
		parameters.clear();
		parameters.putString("screen_name", screenName);

		TWITTER_PARMS params[] = { TWITTER_PARMS.VERSION1_1,
				TWITTER_PARMS.FRIENDSHIPS, TWITTER_PARMS.DESTROY,
				TWITTER_PARMS.POST, TWITTER_PARMS.QUERY };

		if (Build.VERSION.SDK_INT < 11) {
			new TwitterQueryTask().execute(params);
		} else {
			new TwitterQueryTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
	}

	/**
	 * https://api.twitter.com/1.1/friendships/create.json
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void follow() {
		parameters.clear();
		parameters.putString("screen_name", screenName);

		TWITTER_PARMS params[] = { TWITTER_PARMS.VERSION1_1,
				TWITTER_PARMS.FRIENDSHIPS, TWITTER_PARMS.CREATE,
				TWITTER_PARMS.POST, TWITTER_PARMS.QUERY };

		if (Build.VERSION.SDK_INT < 11) {
			new TwitterQueryTask().execute(params);
		} else {
			new TwitterQueryTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
	}

	/**
	 * @param requestCode
	 */
	public void getFollwersList(String requestCode) {
		METHOD = FOLLOWERS;
		REQUESTCODE = requestCode;

		isSessionValid();
	}

	/**
	 * @param requestCode
	 */
	public void getFriendsList(String requestCode) {
		METHOD = FRIENDS;
		REQUESTCODE = requestCode;
		isSessionValid();
	}

	/**
	 * @param screenName
	 * @param requestCode
	 */
	public void follow(String screenName, String requestCode) {
		METHOD = FOLLOW;
		REQUESTCODE = requestCode;
		this.screenName = screenName;

		isSessionValid();
	}

	/**
	 * @param screenName
	 * @param requestCode
	 */
	public void unFollow(String screenName, String requestCode) {
		METHOD = UNFOLLOW;
		REQUESTCODE = requestCode;
		this.screenName = screenName;

		isSessionValid();
	}

	/**
	 * "https://api.twitter.com/1/account/verify_credentials.json"
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void verifyCredentials() {
		TWITTER_PARMS params[] = { TWITTER_PARMS.VERSION1_1,
				TWITTER_PARMS.ACCOUNT, TWITTER_PARMS.VERIFY_CREDENTIALS,
				TWITTER_PARMS.GET, TWITTER_PARMS.QUERY };

		if (Build.VERSION.SDK_INT < 11) {
			new TwitterQueryTask().execute(params);
		} else {
			new TwitterQueryTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
	}

	/**
	 * "https://api.twitter.com/1.1/users/lookup.json"
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void lookUp() {
		parameters.putString("screen_name", mTwitter.getSession()
				.getAccessToken().getScreeName());

		TWITTER_PARMS params[] = { TWITTER_PARMS.VERSION1_1,
				TWITTER_PARMS.USERS, TWITTER_PARMS.LOOKUP, TWITTER_PARMS.GET,
				TWITTER_PARMS.QUERY };

		if (Build.VERSION.SDK_INT < 11) {
			new TwitterQueryTask().execute(params);
		} else {
			new TwitterQueryTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
	}

	private AlertDialog dialog;

	private final int TYPE_UPDATE_STATUS = 0, TYPE_UPLOAD_PHOTO = 1;

	private void tweet() {
		if (showDialog) {
			showTwitterDialog(TYPE_UPDATE_STATUS);
		} else {
			updateStatus();
		}
	}

	private void dismissSoftKeyBoard(IBinder windowToken) {
		try {
			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(windowToken, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@TargetApi(11)
	private void showTwitterDialog(final int type) {
		int theme = Build.VERSION.SDK_INT >= 11 ? android.R.style.Theme_Holo
				: android.R.style.Theme_Dialog;

		ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(
				activity, theme);

		final AlertDialog.Builder alertdialog = new AlertDialog.Builder(
				contextThemeWrapper);

		LinearLayout.LayoutParams layoutParamsWrap = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParamsWrap.gravity = Gravity.CENTER;

		LinearLayout mainLayout = new LinearLayout(activity);
		mainLayout.setBackgroundColor(Color.WHITE);
		mainLayout.setGravity(Gravity.CENTER);
		mainLayout.setLayoutParams(layoutParamsWrap);
		mainLayout.setOrientation(LinearLayout.VERTICAL);

		final TextView txt_remaining = new TextView(activity);
		layoutParamsWrap.topMargin = 5;
		layoutParamsWrap.gravity = Gravity.RIGHT;
		txt_remaining.setLayoutParams(layoutParamsWrap);
		txt_remaining.setText("Remaining "
				+ String.valueOf(TextUtils.isEmpty(tweetMessage) ? 140
						: (140 - tweetMessage.length())));

		final EditText et_message = new EditText(activity);
		et_message.setTextColor(Color.BLACK);
		et_message.setHint("Compose Tweet");
		et_message.setGravity(Gravity.TOP);
		et_message.setMaxLines(4);
		et_message.setLines(4);
		et_message.setPadding(5, 5, 5, 5);
		et_message.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				140) });
		// et_message.setMovementMethod(ScrollingMovementMethod.getInstance());

		et_message.setText(tweetMessage);
		et_message.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				int len = 140 - s.length();
				txt_remaining.setTextColor(len <= 10 ? Color.RED : Color.BLACK);
				txt_remaining.setText("Remaining " + len);

			}
		});

		LinearLayout buttonLayout = new LinearLayout(activity);
		LinearLayout.LayoutParams buttonLinearLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		buttonLinearLayoutParams.topMargin = 5;
		buttonLayout.setLayoutParams(buttonLinearLayoutParams);

		LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);

		Button cancel = new Button(activity);
		cancel.setLayoutParams(buttonLayoutParams);
		cancel.setWidth(0);
		cancel.setGravity(Gravity.CENTER);
		cancel.setText("Cancel");
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dismissSoftKeyBoard(et_message.getWindowToken());
				dialog.dismiss();
			}
		});

		Button send = new Button(activity);
		send.setLayoutParams(buttonLayoutParams);
		send.setWidth(0);
		send.setGravity(Gravity.CENTER);
		send.setText("Tweet");
		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				tweetMessage = et_message.getText().toString().trim();
				if (!tweetMessage.equalsIgnoreCase("")) {
					dismissSoftKeyBoard(et_message.getWindowToken());
					dialog.dismiss();

					if (type == TYPE_UPDATE_STATUS) {
						updateStatus();
					} else {
						uploadToTwitter();
					}
				}
			}
		});

		buttonLayout.addView(send);
		buttonLayout.addView(cancel);

		mainLayout.addView(et_message);
		mainLayout.addView(txt_remaining);
		mainLayout.addView(buttonLayout);

		mainLayout.setPadding(2, 2, 2, 2);

		if (Build.VERSION.SDK_INT >= 11) {
			dialog = alertdialog.create();
			dialog.setView(mainLayout, 0, 0, 0, 0);
			dialog.show();
		} else {
			alertdialog.setCustomTitle(mainLayout);
			dialog = alertdialog.create();
			dialog.show();
		}
	}

	/**
	 * "https://api.twitter.com/1.1/statuses/update.json"
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void updateStatus() {

		parameters.clear();

		parameters.putString("wrap_links", "true");
		parameters.putString("status", tweetMessage);

		TWITTER_PARMS params[] = { TWITTER_PARMS.VERSION1_1,
				TWITTER_PARMS.STATUSES, TWITTER_PARMS.UPDATE,
				TWITTER_PARMS.POST, TWITTER_PARMS.QUERY };

		if (Build.VERSION.SDK_INT < 11) {
			new TwitterQueryTask().execute(params);
		} else {
			new TwitterQueryTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
	}

	/**
	 * 
	 * https://api.twitter.com/1.1/statuses/update_with_media.json
	 * 
	 * 
	 */
	private void upload() {
		if (showDialog) {
			showTwitterDialog(TYPE_UPLOAD_PHOTO);
		} else {
			uploadToTwitter();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void uploadToTwitter() {
		parameters.clear();

		if (!TextUtils.isEmpty(tweetMessage)) {
			parameters.putString("status", tweetMessage);
			parameters.putString("wrap_links", "true");
		}

		parameters.putInt("image_type", IMAGE_TYPE);

		TWITTER_PARMS params[] = { TWITTER_PARMS.VERSION1_1,
				TWITTER_PARMS.STATUSES, TWITTER_PARMS.UPDATE_WITH_MEDIA,
				TWITTER_PARMS.POST, TWITTER_PARMS.QUERY };

		if (Build.VERSION.SDK_INT < 11) {
			new TwitterQueryTask().execute(params);
		} else {
			new TwitterQueryTask().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
	}

	public interface TwitterCallbackListener {
		public void twitterCallback(boolean success, String response,
				String requestCode, ArrayList<Object> listResponseData);
	}

	private ArrayList<Object> listResponseData;

	private void isSessionValid() {
		if (!isNetworkAvailable()) {
			handleUserRequest("No Network Available");
		} else if (mTwitter.hasAccessToken()) {
			// verify access token use this
			// https://api.twitter.com/oauth/access_token or some other
			handleUserRequest("");
		} else {
			mTwitter.authorize();
		}
	}

	/**
	 * 
	 * param[0] --> VERSION <br>
	 * param[1] --> CATEGORY <br>
	 * param[2] --> METHOD <br>
	 * param[3] --> HTTP_REQUEST_TYPE <br>
	 * param[4] --> TASK_TYPE ie SESSION or QUERY <br>
	 * 
	 * @author hb
	 * 
	 */
	private class TwitterQueryTask extends
			AsyncTask<TWITTER_PARMS, String, String> {

		private TWITTER_PARMS task_type = TWITTER_PARMS.SESSION;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgress.show();
		}

		@Override
		protected String doInBackground(TWITTER_PARMS... params) {

			TWITTER_PARMS version = (params[0]);
			TWITTER_PARMS category = (params[1]);
			TWITTER_PARMS method = (params[2]);
			TWITTER_PARMS http_request_type = (params[3]);

			task_type = (params[4]);

			String response = METHOD == UPLOAD ? uploadImagewithStatus(version,
					category, method) : getResponse(version, category, method,
					http_request_type);
			parameters.clear();
			HBJSONParser clsJsonParser = new HBJSONParser();
			listResponseData = clsJsonParser.parseData(response);
			return response;
		}

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			mProgress.dismiss();

			if (task_type == TWITTER_PARMS.SESSION) {
				if (response.contains(mHttpOauthConsumer.getToken())) {
					handleUserRequest("Logged in Successfully");
				} else {
					mTwitter.authorize();
				}
			} else if (twitterCallbackListener != null) {
				boolean success = (!TextUtils.isEmpty(response))
						&& (!response.contains("errors"));

				try {
					if (!success) {
						JSONObject jsonObject = new JSONObject(response);
						JSONArray jsonArray = jsonObject.getJSONArray("errors");
						JSONObject message = jsonArray.getJSONObject(0);

						switch (METHOD) {
						case TWEETS:
							break;

						case TAGGED_TWEETS:
							break;

						case FOLLOWERS:
							break;

						case FRIENDS:
							break;

						case USER_DETAILS:
							break;

						default:
							response = message.getString("message");
							break;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				twitterCallbackListener.twitterCallback(success, response,
						REQUESTCODE, listResponseData);
			}
		}
	}

	private String uploadImagewithStatus(TWITTER_PARMS version,
			TWITTER_PARMS category, TWITTER_PARMS method) {
		String response = "";
		try {
			String methodValue = method.value;

			Uri.Builder uriBuilder = new Uri.Builder()
					.appendPath(version.value).appendPath(category.value)
					.appendPath(methodValue);

			Uri uri = uriBuilder.build();

			String uriString = TWITTER_UPLOAD_API + uri.toString();

			MultipartEntity entity = new MultipartEntity();

			if (parameters.containsKey("status")) {
				String status = parameters.getString("status");

				String uriEncodedString = getEncodedString(status);
				if (uriEncodedString.length() <= 140) {
					status = new String(uriEncodedString);
				}

				entity.addPart("status",
						new StringBody(status, Charset.forName("UTF-8")));
			}

			switch (IMAGE_TYPE) {

			case TYPE_FILE:
				try {
					entity.addPart("media[]", new FileBody(file));
				} catch (Exception e) {
				}
				break;

			case TYPE_URL:
				try {
					ImageLoader imageLoader = new ImageLoader(activity);
					Bitmap b = imageLoader.getBitmap(url,false,false);
					entity.addPart("media[]", getInputStreamBody(b));
				} catch (Exception e) {
				}
				break;

			case TYPE_BITMAP:
				try {
					entity.addPart("media[]", getInputStreamBody(bitmap));
				} catch (Exception e) {
				}
				break;
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			entity.writeTo(out);

			byte[] content = out.toByteArray();

			System.setProperty("http.keepAlive", "false");

			HttpURLConnection connection = (HttpURLConnection) new URL(
					uriString).openConnection();
			connection.setRequestMethod("POST");

			// Adding Headers
			connection.setRequestProperty("Content-Length",
					String.valueOf(content.length));

			oauth.signpost.http.HttpRequest httpRequest = mHttpOauthConsumer
					.sign(new HttpPost(uriString));
			Map<String, String> headers = httpRequest.getAllHeaders();

			if (headers != null && !headers.isEmpty()) {
				for (Iterator<String> iterator = headers.keySet().iterator(); iterator
						.hasNext();) {
					String key = iterator.next();
					connection.setRequestProperty(key, headers.get(key));
				}
			}

			Header header = entity.getContentType();
			connection.setRequestProperty(header.getName(), header.getValue());

			// Adding Body
			connection.setDoOutput(true);
			connection.getOutputStream().write(content);

			// Connect
			connection.connect();
			int responseCode = connection.getResponseCode();

			Log.e("responseCode", "" + responseCode);

			// Get Stream
			InputStream inputStream = (responseCode >= 200 && responseCode < 400) ? connection
					.getInputStream() : connection.getErrorStream();
			response = convertStreamToString(inputStream);

		} catch (Exception e) {
			e.printStackTrace();
		}

		switch (IMAGE_TYPE) {

		case TYPE_FILE:
			file = null;
			break;

		case TYPE_URL:
			url = null;
			break;

		case TYPE_BITMAP:
			bitmap = null;
			break;

		default:
			break;
		}

		return response;
	}

	private String getResponse(TWITTER_PARMS version, TWITTER_PARMS category,
			TWITTER_PARMS method, TWITTER_PARMS http_request_type) {

		String response = "";

		try {
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			int timeoutConnection = 60 * 1000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.

			HttpClient httpClient = new DefaultHttpClient(httpParameters);

			Uri.Builder uriBuilder = new Uri.Builder()
					.appendPath(version.value).appendPath(category.value)
					.appendPath(method.value);

			if (!parameters.isEmpty()) {
				for (String key : parameters.keySet()) {
					String value = parameters.getString(key);
					uriBuilder.appendQueryParameter(key, value);
				}
			}

			Uri uri = uriBuilder.build();

			String uriString = TWITTER_API + uri.toString();

			String uriEncodedString = getEncodedString(uriString);
			if (uriEncodedString.length() <= 140) {
				uriString = new String(uriEncodedString);
			}

			HttpUriRequest httpUriRequest = (http_request_type == TWITTER_PARMS.GET) ? new HttpGet(
					uriString) : new HttpPost(uriString);

			mHttpOauthConsumer.sign(httpUriRequest);
			HttpResponse httpResponse = httpClient.execute(httpUriRequest);

			response = convertStreamToString(httpResponse.getEntity()
					.getContent());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	/******************************************** Utils ******************************************/

	private String getEncodedString(String string) {
		// char escapeSequences[] = { '\'', '(', ')', '*', '!' }; // dangerous
		// escape sequences

		string = string.replaceAll("!", "%21");
		string = string.replaceAll("'", "%27");
		string = string.replaceAll("[(]", "%28");
		string = string.replaceAll("[)]", "%29");
		string = string.replaceAll("[*]", "%2A");

		return string;
	}

	private boolean isNetworkAvailable() {
		boolean connection = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) activity
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo net_info = cm.getActiveNetworkInfo();
				if (net_info != null && net_info.isConnected())
					connection = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize <= 0 ? 1 : inSampleSize;
	}

	private InputStream getInputStream(String url) {
		try {
			HttpGet request = new HttpGet(url);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(request);
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(
					response.getEntity());
			return bufHttpEntity.getContent();
		} catch (Exception e) {
		}
		return null;
	}

	private InputStreamBody getInputStreamBody(Bitmap bitmap) {
		byte[] data = convertBitmapToByteArray(bitmap);
		ByteArrayInputStream bs = new ByteArrayInputStream(data);
		return new InputStreamBody(bs, "test.jpg");
	}

	/**
	 * @param inputStream
	 *            InputStream which is to be converted into String
	 * @return String by encoding the given InputStream (or) <br/>
	 *         null if InputStream is null or cannot convert the InputStream.<br/>
	 */
	private String convertStreamToString(InputStream inputStream) {
		StringBuilder sb = new StringBuilder();

		try {

			BufferedReader r = new BufferedReader(new InputStreamReader(
					inputStream), 1024);
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	/**
	 * @param bitmap
	 *            Bitmap object from which you want to get bytes
	 * @return byte[] array of bytes by compressing the bitmap to PNG format <br/>
	 *         null if bitmap passed is null (or) failed to get bytes from the
	 *         bitmap
	 */
	private byte[] convertBitmapToByteArray(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		} else {
			byte[] b = null;
			try {
				bitmap = scaleToSmallHeight(bitmap);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
				b = byteArrayOutputStream.toByteArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return b;
		}
	}

	/**
	 * Scales the bitmap to the device's width and height without changing the
	 * aspect ratio
	 * 
	 * @param bitmap
	 *            bitmap image to be scaled
	 * @param deviceWidth
	 *            device width in pixel
	 * @param deviceHeight
	 *            device height in pixel
	 * @return scaled bitmap
	 */

	private Bitmap scaleToActualAspectRatio(Bitmap bitmap) {
		Display display = activity.getWindow().getWindowManager()
				.getDefaultDisplay();

		int reqWidth = display.getWidth();
		int reqHeight = display.getHeight();

		if (bitmap != null && reqHeight > 0 && reqWidth > 0) {

			int height = bitmap.getHeight(); // 563
			int width = bitmap.getWidth(); // 900

			int dstWidth = 0;
			int dstHeight = 0;
			
			if(width > reqWidth && height > reqHeight)
			{
				dstWidth = reqWidth;
				dstHeight = reqHeight;
			}
			else if(width > reqWidth)
			{
				dstWidth = reqWidth;
				dstHeight = (dstWidth*height)/width;
				
				if(dstHeight > reqHeight)
				dstHeight = reqHeight;	
			}
			else // 99% this case will not come 
			{
				dstHeight = reqHeight;
				dstWidth = (dstHeight*width)/height;
				
				if(dstWidth > reqWidth)
					dstWidth = reqWidth;	
			}
			
			bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,false);
			
		}
		return bitmap;
	}
	
	
	private Bitmap scaleToSmallHeight(Bitmap bitmap) {
		Display display = activity.getWindow().getWindowManager()
				.getDefaultDisplay();

		int reqWidth = display.getWidth();
		int reqHeight = display.getHeight();

		if (bitmap != null && reqHeight > 0 && reqWidth > 0) {

			int height = bitmap.getHeight(); // 563
			int width = bitmap.getWidth(); // 900

			int dstWidth = 0;
			int dstHeight = 0;
			
			if(width > reqWidth && height > reqHeight)
			{
				dstWidth = reqWidth;
				dstHeight = reqHeight;
			}
			else if(width > reqWidth)
			{
				dstWidth = reqWidth;
				dstHeight = (dstWidth*height)/width;
				
				if(dstHeight > reqHeight)
				dstHeight = reqHeight;	
			}
			else // 99% this case will not come 
			{
				dstHeight = reqHeight;
				dstWidth = (dstHeight*width)/height;
				
				if(dstWidth > reqWidth)
					dstWidth = reqWidth;	
			}
			
			bitmap = Bitmap.createScaledBitmap(bitmap, 140, 140,false);
			
		}
		return bitmap;
	}

	private boolean isTwitterAppInstalled() {
		// HashMap<String, String> knownTwitterClients = new HashMap<String,
		// String>();
		//
		// knownTwitterClients.put("Twitter",
		// "com.twitter.android.PostActivity");
		// knownTwitterClients.put("UberSocial",
		// "com.twidroid.activity.SendTweet");
		// knownTwitterClients.put("TweetDeck",
		// "com.tweetdeck.compose.ComposeActivity");
		// knownTwitterClients.put("Seesmic", "com.seesmic.ui.Composer");
		// knownTwitterClients.put("TweetCaster",
		// "com.handmark.tweetcaster.ShareSelectorActivity");
		// knownTwitterClients.put("Plume",
		// "com.levelup.touiteur.appwidgets.TouiteurWidgetNewTweet");
		// knownTwitterClients.put("Twicca", "jp.r246.twicca.statuses.Send");

		ArrayList<String> twitterAppList = new ArrayList<String>();
		twitterAppList.add("com.twitter.android");
		twitterAppList.add("com.twidroid");
		twitterAppList.add("com.tweetdeck");
		twitterAppList.add("com.seesmic");
		twitterAppList.add("com.handmark.tweetcaster");
		twitterAppList.add("com.levelup.touiteur");
		twitterAppList.add("jp.r246.twicca");

		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			final PackageManager pm = activity.getPackageManager();
			final List<ResolveInfo> activityList = pm.queryIntentActivities(
					intent, 0);
			for (ResolveInfo app : activityList) {
				for (String packageName : twitterAppList) {
					if (app.activityInfo.packageName.startsWith(packageName)) {
						twitterAppPackageName = new String(
								app.activityInfo.packageName);
						twitterAppClassName = new String(app.activityInfo.name);
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private String twitterAppClassName, twitterAppPackageName;
	private boolean isTwitterAppInstalled;

	private void shareUsingIntent(String status, String filePath) {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType(TextUtils.isEmpty(filePath) ? "text/plain"
					: "image/*");

			intent.putExtra(Intent.EXTRA_TEXT, status);

			if (!TextUtils.isEmpty(filePath)) {
				intent.putExtra(Intent.EXTRA_STREAM,
						Uri.fromFile(new File(filePath)));
			}

			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			intent.setClassName(twitterAppPackageName, twitterAppClassName);

			activity.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
