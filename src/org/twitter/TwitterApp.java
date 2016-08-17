package org.twitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import org.twitter.HBTwitterUtility.TwitterCallbackListener;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


@SuppressLint("HandlerLeak")
public class TwitterApp {

	private TwitterSession mSession;
	private CommonsHttpOAuthConsumer mHttpOauthConsumer;
	private OAuthProvider mHttpOauthprovider;
	private String mConsumerKey;
	private String mSecretKey;
	private ProgressDialog mProgressDlg;
	private TwDialogListener mListener;
	private Context context;

	protected static final String CALLBACK_URL = "twitterapp://connect"; // oob
	private static final String TAG = "TwitterApp";

	public TwitterApp(Context context, String consumerKey, String secretKey,
			TwitterCallbackListener twitterCallbackListener) {

		this.context = context;

		setSession(new TwitterSession(context));
		mProgressDlg = new ProgressDialog(context);

		mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mConsumerKey = consumerKey;
		mSecretKey = secretKey;

		mHttpOauthConsumer = new CommonsHttpOAuthConsumer(mConsumerKey,
				mSecretKey);
		mHttpOauthprovider = new CommonsHttpOAuthProvider(
				"https://twitter.com/oauth/request_token",
				"https://twitter.com/oauth/access_token",
				"https://twitter.com/oauth/authorize?force_login=true");

		setAccessToken();
	}

	private void setAccessToken() {
		if (hasAccessToken()) {
			TwitterAccessToken accessToken = getSession().getAccessToken();

			if (accessToken != null)
				mHttpOauthConsumer.setTokenWithSecret(accessToken.getToken(),
						accessToken.getTokenSecret());
		}
	}

	protected void setListener(TwDialogListener listener) {
		mListener = listener;
	}

	protected boolean hasAccessToken() {
		return (getSession().getAccessToken() == null) ? false : true;
	}

	protected boolean resetAccessToken() {
		boolean success = false;

		if (getSession().getAccessToken() != null) {
			try {
				getSession().resetAccessToken();
				CookieSyncManager.createInstance(context);
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.removeAllCookie();
				success = true;
			} catch (Exception e) {
			}
		}

		return success;
	}

	protected CommonsHttpOAuthConsumer getOAuthConsumer() {
		return mHttpOauthConsumer;
	}

	protected void authorize() {
		mProgressDlg.setMessage("Connecting to Twitter ...");
		mProgressDlg.show();
		final Message message = new Message();
		new Thread() {
			@Override
			public void run() {
				String authUrl = "";
				int what = 1;

				try {
					authUrl = mHttpOauthprovider.retrieveRequestToken(
							mHttpOauthConsumer, CALLBACK_URL);
					what = 0;

					Log.d(TAG, "Request token url " + authUrl);
				} catch (Exception e) {
					Log.d(TAG, "Failed to get request token");
					e.printStackTrace();
					String message = e.getMessage();
					Log.d("Date Error Message", message);
					if (message.contains("Communication"))
						what = 585;
				}
				if (what == 585) {
					message.what = what;
					mHandler.sendMessage(message);
				} else
					mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0,
							authUrl));
			}
		}.start();

	}

	private void processToken(final String callbackUrl) {
		mProgressDlg.setMessage("Redirecting back to your app ...");
		mProgressDlg.show();

		new Thread() {
			@Override
			public void run() {
				int what = 1;

				try {
					mHttpOauthprovider.retrieveAccessToken(mHttpOauthConsumer,
							getVerifier(callbackUrl));

					String userId = "";
					String screenName = "";
					
					try
					{
						HttpParams httpParameters = new BasicHttpParams();
						// Set the timeout in milliseconds until a connection is
						// established.
						int timeoutConnection = 60*1000;
						HttpConnectionParams.setConnectionTimeout(httpParameters,
								timeoutConnection);
						// Set the default socket timeout (SO_TIMEOUT)
						// in milliseconds which is the timeout for waiting for data.

						HttpClient httpClient = new DefaultHttpClient(httpParameters);

						String uriString = "https://api.twitter.com/1.1/account/verify_credentials.json";

						HttpUriRequest httpUriRequest =  new HttpGet(uriString);

						mHttpOauthConsumer.sign(httpUriRequest);
						HttpResponse httpResponse = httpClient.execute(httpUriRequest);

						String response = convertStreamToString(httpResponse.getEntity().getContent());
						
						JSONObject jsonObject = new JSONObject(response);
						screenName = jsonObject.getString("screen_name");
						userId = jsonObject.getString("id");
						
					}
					catch(Exception e)
					{
						
					}
					
					TwitterAccessToken mAccessToken = new TwitterAccessToken(
							mHttpOauthConsumer.getToken(),
							mHttpOauthConsumer.getTokenSecret(),screenName,userId);

					getSession().storeAccessToken(mAccessToken);

					what = 0;
				} catch (Exception e) {
					Log.d(TAG, "Error getting access token");
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
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
	
	private String getVerifier(String callbackUrl) {
		String verifier = "";

		try {
			callbackUrl = callbackUrl.replace("twitterapp", "http");

			URL url = new URL(callbackUrl);
			String query = url.getQuery();

			String array[] = query.split("&");

			for (String parameter : array) {
				String v[] = parameter.split("=");

				try {
					if (URLDecoder.decode(v[0], "utf-8").equals(
							oauth.signpost.OAuth.OAUTH_VERIFIER)) {
						verifier = URLDecoder.decode(v[1], "utf-8");
						break;
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return verifier;
	}

	private void showLoginDialog(String url) {
		final TwDialogListener listener = new TwDialogListener() {
			@Override
			public void onComplete(String value) {
				processToken(value);
			}

			@Override
			public void onError(String value) {
				mListener.onError("Failed opening authorization page");
			}
		};

		TwitterDialog twitterDialog = new TwitterDialog(context, url, listener);
		twitterDialog.show();
	}

	public TwitterSession getSession() {
		return mSession;
	}

	public void setSession(TwitterSession mSession) {
		this.mSession = mSession;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgressDlg.dismiss();

			if (msg.what == 1) {
				if (msg.arg1 == 1)
					mListener.onError("Error getting request token");
				else
					mListener.onError("Error getting access token");
			} else if (msg.what == 585) {
				mListener
						.onError("Please check your device Date and Time. If they are past then Twitter will not allow you to login");
			} else 
			{
				if (msg.arg1 == 1)
					showLoginDialog((String) msg.obj);
				else
				{
					mListener.onComplete("");
				}
			}
		}
	};

	protected interface TwDialogListener {
		public void onComplete(String value);

		public void onError(String value);
	}

	// private void showAlert(String message) {
	// AlertDialog.Builder alBuilder = new AlertDialog.Builder(context);
	// alBuilder.setTitle("Alert");
	// alBuilder.setMessage(message);
	// alBuilder.setPositiveButton("OK", null);
	// alBuilder.show();
	// }
	//
	// public static final Pattern ID_PATTERN = Pattern
	// .compile(".*?\"id_str\":\"(\\d*)\".*");
	// public static final Pattern SCREEN_NAME_PATTERN = Pattern
	// .compile(".*?\"screen_name\":\"([^\"]*).*");
	//
	// public boolean tweet(String message) {
	// if (message == null || message.length() > 140) {
	// throw new IllegalArgumentException(
	// "message cannot be null and must be less than 140 chars");
	// }
	// // create a request that requires authentication
	//
	// try {
	// HttpClient httpClient = new DefaultHttpClient();
	// Uri.Builder builder = new Uri.Builder();
	// builder.appendPath("statuses").appendPath("update.json")
	// .appendQueryParameter("status", message);
	// Uri man = builder.build();
	// HttpPost post = new HttpPost("http://twitter.com" + man.toString());
	// mHttpOauthConsumer.sign(post);
	// HttpResponse resp = httpClient.execute(post);
	// String jsonResponseStr = convertStreamToString(resp.getEntity()
	// .getContent());
	// LOGHB.i(TAG, "response: " + jsonResponseStr);
	// String id = getFirstMatch(ID_PATTERN, jsonResponseStr);
	// LOGHB.i(TAG, "id: " + id);
	// String screenName = getFirstMatch(SCREEN_NAME_PATTERN,
	// jsonResponseStr);
	// LOGHB.i(TAG, "screen name: " + screenName);
	//
	// final String url = MessageFormat.format(
	// "https://twitter.com/#!/{0}/status/{1}", screenName, id);
	// LOGHB.i(TAG, "url: " + url);
	//
	// return resp.getStatusLine().getStatusCode() == 200;
	//
	// } catch (Exception e) {
	// LOGHB.e(TAG, "trying to tweet: " + message, e);
	// return false;
	// }
	// }

	// private String getTwitterUserName() {
	// try {
	// // https://api.twitter.com/1/account/verify_credentials.json
	//
	// HttpClient httpClient = new DefaultHttpClient();
	// Uri.Builder builder = new Uri.Builder();
	// builder.appendPath("1").appendPath("account")
	// .appendPath("verify_credentials.json");
	// Uri man = builder.build();
	//
	// HttpGet get = new HttpGet("https://api.twitter.com"
	// + man.toString());
	// mHttpOauthConsumer.sign(get);
	// HttpResponse resp = httpClient.execute(get);
	// String jsonResponseStr = convertStreamToString(resp.getEntity()
	// .getContent());
	// return jsonResponseStr;
	//
	// } catch (Exception e) {
	// return null;
	// }
	// }

	// public static String getFirstMatch(Pattern pattern, String str) {
	// Matcher matcher = pattern.matcher(str);
	// if (matcher.matches()) {
	// return matcher.group(1);
	// }
	// return null;
	// }
}