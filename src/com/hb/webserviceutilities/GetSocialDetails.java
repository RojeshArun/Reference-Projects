package com.hb.webserviceutilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.Utilites.Validations;
import com.barter.facebook.Facebook;
import com.barter.facebook.FacebookCallbackListner;
import com.barter.facebook.FacebookUtilityWithCallback;
import com.hb.barter.LoginScreen;
import com.hb.barter.R;
import com.hb.floatingfragments.ShareFragment;

public class GetSocialDetails {

	/**
	 * Added for testing
	 */
	private String twitter_consumer_key = Webservices.TWITTERCONSUMERKEY;
	private String twitter_secret_key = Webservices.TWITTERCONSUMERSECRETKEY;

	private FragmentActivity mactvity;

	public GetSocialDetails(FragmentActivity mactvity) {
		this.mactvity = mactvity;
	}

	FacebookUtilityWithCallback mCallback;

	/**
	 * Start FaceBook
	 */
	public void getAndPostFaceBookUserDetails() {

		mCallback = new FacebookUtilityWithCallback(mactvity,
				Webservices.FACEBOOKAPPID);
		mCallback.facebooklogin(mCallFb);

	}

	/**
	 * Share On Facebook
	 * 
	 * @param fragmentActivity
	 * @param mFilePath
	 */

	public void shareOnFacebook(String mFilePath,
			FragmentActivity fragmentActivity) {

		mCallback = new FacebookUtilityWithCallback(mactvity,
				Webservices.FACEBOOKAPPID);

		try {

			mCallback
					.openDialogAndShare(
							"http://www.gobartr.net",
							"Bartr : Money exchange for the rest fo us.",
							"http://www.hiddenbrains.com",
							" Get the new Bartr app, it makes currency exchange easy.\n \n  Bartr - The location based currency exchange application \n \n ",
							"http://gobartr.net/mobile/public/images/admin/bartr_logo.jpg",
							mCallFb);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Login Call Back
	 */
	public FacebookCallbackListner mCallFb = new FacebookCallbackListner() {

		@Override
		public void success(boolean success, Facebook mFaceBook) {
			if (success) {
				GetFbData mLocalTask = new GetFbData();
				mLocalTask.execute(mFaceBook);
			}
		}

		@Override
		public void response(String response) {

		}
	};
	FBUserCallback callback;

	public void setFBUserCallback(FBUserCallback callback) {
		this.callback = callback;
	}

	public interface FBUserCallback {
		void getResult(String result, String accesstoken);
	}

	/**
	 * ' Logout Once you Took Information
	 */
	public FacebookCallbackListner mLOgout = new FacebookCallbackListner() {

		@Override
		public void success(boolean success, Facebook mFaceBook) {

		}

		@Override
		public void response(String response) {

		}
	};

	// Get User profile pic

	public String userFBProfilePic(String fbid) {
		// https://graph.facebook.com/10204860804265047/picture

		String mPrefix = "https://graph.facebook.com/";
		String mSuffix = "/picture";

		return mPrefix + fbid + mSuffix;
	}

	/**
	 * Load FaceBook User Profile Data
	 * 
	 * @author hb
	 * 
	 */
	private class GetFbData extends AsyncTask<Facebook, Void, String> {

		ProgressDialog pd;
		Facebook mFb;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(mactvity, mactvity.getResources()
					.getString(R.string.plsWait), mactvity.getResources()
					.getString(R.string.fetchInfo));
			pd.setIcon(R.drawable.com_images_);
			pd.show();
		}

		@Override
		protected String doInBackground(Facebook... params) {
			String str = "";
			try {
				mFb = params[0];
				// mFb1 = params[1];
				// mFb2 = params[2];
				str = mFb.request("me");

				mFb.request("firstname");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return str;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pd.dismiss();
			try {

				if (callback != null)
					callback.getResult(result, mFb.getAccessToken());

				JSONObject mjsoObject = new JSONObject(result);
				String id = mjsoObject.getString("id");
				String accesstoken = mFb.getAccessToken();

				try {
					if (mjsoObject.has("email")) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								mCallback.facebookLogout(mLOgout);
							}
						}).start();
					} else {
						showPopupFB(id, accesstoken, accesstoken);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				// Validations.showAlerDialog(
				// mactvity.getResources()
				// .getString(R.string.fbloginError), mactvity);
				Validations.showSingleBtnDialog(mactvity.getResources()
						.getString(R.string.fbloginError), mactvity);
			}
		}
	}

	// public static TwitterCallbackUtility mCallBackTwitter;

	/**
	 * Start FaceBook
	 */
	// public void getAndPostTwitterUserDetails() {
	// mCallBackTwitter = new TwitterCallbackUtility(mactvity,
	// twitter_consumer_key, twitter_secret_key);
	// mCallBackTwitter.twitterLogin(mCallTwitter);
	// }

	// TwitterCallbackListner mCallTwitter = new TwitterCallbackListner() {
	//
	// @Override
	// public void status(boolean status, Twitter mTwitter, String userID) {
	// if (status) {
	// GetTwitterData mData = new GetTwitterData(userID);
	// mData.execute(mTwitter);
	// }
	//
	// }

	// @Override
	// public void response(String response) {
	//
	// }
	// };

	// TWUserCallback twcallback;
	//
	// public void setTWUserCallback(TWUserCallback twCallback) {
	// this.twcallback = twCallback;
	// }
	//
	// public interface TWUserCallback {
	// void getTwitterResult(Integer result2, String result,
	// String accessToekn, String authtoken, User username,
	// String userImage);
	//
	// void getTwitterResult(Integer result2, String result,
	// String accessToekn, String authtoken, String username);
	// }

	/**
	 * Load Twitter User Profile Data
	 * 
	 * @author hb
	 * 
	 */
	// private class GetTwitterData extends AsyncTask<Twitter, Void, User> {
	//
	// String mUserName = "";
	// ProgressDialog pd;
	// Twitter mTwitter;
	// String userImage;
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// pd = ProgressDialog.show(mactvity, mactvity.getResources()
	// .getString(R.string.plsWait), mactvity.getResources()
	// .getString(R.string.fetchInfo));
	// pd.setIcon(R.drawable.ic_launcher);
	// pd.show();
	// }
	//
	// public GetTwitterData(String mUserName) {
	// this.mUserName = mUserName;
	// }
	//
	// String userName;
	//
	// @Override
	// protected User doInBackground(Twitter... params) {
	// int name = -1;
	// try {
	// mTwitter = params[0];
	// User mUser = mTwitter.showUser(mTwitter.getId());
	// name = (int) mUser.getId();
	// userImage = mUser.getBiggerProfileImageURL();
	// userName = mUser.getScreenName();
	// return mUser;
	// } catch (TwitterException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(User result) {
	// super.onPostExecute(result);
	// pd.dismiss();
	// if (result == null) {
	// Validations.showAlerDialog(
	// mactvity.getResources().getString(
	// R.string.twitterloginError), mactvity);
	// } else {
	//
	// if (twcallback != null) {
	// AccessToken mact;
	// String accessToekn = "", authtoken = "";
	// try {
	// mact = mTwitter.getOAuthAccessToken();
	// accessToekn = mact.getToken();
	// authtoken = mact.getTokenSecret();
	// // twcallback.getTwitterResult(result,
	// // String.valueOf(TwitterCode), accessToekn,
	// // authtoken, result.getScreenName(), userImage);
	//
	// twcallback.getTwitterResult((int) result.getId(),
	// String.valueOf(TwitterCode), accessToekn,
	// authtoken, result, userImage);
	//
	// // twcallback.getTwitterResult((int) result.getId(),
	// // String.valueOf(TwitterCode), accessToekn,
	// // authtoken, result.getScreenName(), userImage);
	// } catch (TwitterException e) {
	// e.printStackTrace();
	// }
	// }
	// // NSApplication.getInstance().setId(String.valueOf(result));
	// // AccessToken mact;
	// // String accessToekn = "", authtoken = "";
	// // try {
	// // mact = mTwitter.getOAuthAccessToken();
	// // accessToekn = mact.getToken();
	// // authtoken = mact.getTokenSecret();
	// // NSApplication.getInstance().setAccessToken(accessToekn);
	// // NSApplication.getInstance().setTwAccessToken(authtoken);
	// // } catch (TwitterException e) {
	// // e.printStackTrace();
	// // }
	// // AuthenTicate(String.valueOf(result),
	// // String.valueOf(TwitterCode), "", accessToekn, "");
	// // showPopup(result, accessToekn, authtoken);
	// }
	// }
	// }

	public static final int FBCode = 1, TwitterCode = 3, Gplus = 5;

	public void AuthenTicate(String id, String type, String accs,
			String twitterAccs, String email) {
		HashMap<String, String> mParam = new HashMap<String, String>();
		mParam.put("id", id);
		mParam.put("email", email);
		mParam.put("type", type);
		mParam.put("accessFb", accs);
		mParam.put("accsTw", twitterAccs);

		// NSApplication.getInstance().setEmailID(email);
		// PostDataAndGetData post = new PostDataAndGetData(mactvity, mParam,
		// "POST", "http://74.53.231.140:8003/newsstand_webservice.asmx",
		// Integer.parseInt(type), true);
		// post.setDialogThem(mactvity.getResources().getColor(R.color.newspapar),
		// R.drawable.redgradientbutton);
		// post.execute(GetHTMLContent.getLoginWithOpenId(id, type, accs,
		// twitterAccs, email));
		// if (mCallBackTwitter != null)
		// mCallBackTwitter.logoutTwitter();
	}

	Dialog dialog = null;

	public void showPopup(final Integer result, final String accessToekn,
			final String authtoken) {
		try {

			dialog = new Dialog(mactvity, R.style.ThemeDialogCustom);
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.popup_forgotpassword);

			final TextView displyText1 = (TextView) dialog
					.findViewById(R.id.title);
			final EditText emailEdit = (EditText) dialog
					.findViewById(R.id.forgotpassword_emailaddress);
			try {
				InputMethodManager imm = (InputMethodManager) mactvity
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(emailEdit, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			InputMethodManager imm = (InputMethodManager) mactvity
					.getSystemService(LoginScreen.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

			Button submitBtn = (Button) dialog.findViewById(R.id.btn_popupSubmit);
			Button cancelBtn = (Button) dialog
					.findViewById(R.id.btn_popupCancel);

			submitBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						InputMethodManager mInput = (InputMethodManager) mactvity
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						mInput.hideSoftInputFromInputMethod(
								emailEdit.getWindowToken(), 0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (emailEdit.getText().toString().length() == 0) {
						Validations.showSingleBtnDialog(
								mactvity.getResources().getString(
										R.string.please_enter_email_address),
								mactvity);
						// Validations.showAlerDialog(
						// mactvity.getResources().getString(
						// R.string.please_enter_email_address),
						// mactvity);
					} else {
						if (!isValidEmail(emailEdit.getText().toString().trim())) {
							Validations
									.showSingleBtnDialog(
											mactvity.getResources()
													.getString(
															R.string.please_enter_valid_email_address),
											mactvity);
							// Validations
							// .showAlerDialog(
							// mactvity.getResources()
							// .getString(
							// R.string.please_enter_valid_email_address),
							// mactvity);
						} else {
							InputMethodManager mInput = (InputMethodManager) mactvity
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							mInput.hideSoftInputFromInputMethod(
									emailEdit.getWindowToken(), 0);
							AuthenTicate(String.valueOf(result),
									String.valueOf(TwitterCode), "",
									accessToekn, "");
							dialog.dismiss();
						}
					}
				}
			});

			cancelBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						InputMethodManager mInput = (InputMethodManager) mactvity
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						mInput.hideSoftInputFromInputMethod(
								emailEdit.getWindowToken(), 0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					dialog.dismiss();
				}
			});

			dialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void showPopupFB(final String result, final String accessToekn,
			final String authtoken) {
		try {

			dialog = new Dialog(mactvity, R.style.ThemeDialogCustom);
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.popup_forgotpassword);

			final TextView displyText1 = (TextView) dialog
					.findViewById(R.id.title);
			final EditText emailEdit = (EditText) dialog
					.findViewById(R.id.forgotpassword_emailaddress);
			try {
				InputMethodManager imm = (InputMethodManager) mactvity
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(emailEdit, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			InputMethodManager imm = (InputMethodManager) mactvity
					.getSystemService(LoginScreen.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

			Button submitBtn = (Button) dialog.findViewById(R.id.btn_popupSubmit);
			Button cancelBtn = (Button) dialog
					.findViewById(R.id.btn_popupCancel);

			submitBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						InputMethodManager mInput = (InputMethodManager) mactvity
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						mInput.hideSoftInputFromInputMethod(
								emailEdit.getWindowToken(), 0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (emailEdit.getText().toString().length() == 0) {
						Validations.showSingleBtnDialog(
								mactvity.getResources().getString(
										R.string.please_enter_email_address),
								mactvity);
						// Validations.showAlerDialog(
						// mactvity.getResources().getString(
						// R.string.please_enter_email_address),
						// mactvity);
					} else {
						if (!isValidEmail(emailEdit.getText().toString().trim())) {
							// Validations
							// .showAlerDialog(
							// mactvity.getResources()
							// .getString(
							// R.string.please_enter_valid_email_address),
							// mactvity);
							Validations
									.showSingleBtnDialog(
											mactvity.getResources()
													.getString(
															R.string.please_enter_valid_email_address),
											mactvity);
						} else {
							InputMethodManager mInput = (InputMethodManager) mactvity
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							mInput.hideSoftInputFromInputMethod(
									emailEdit.getWindowToken(), 0);
							dialog.dismiss();

							AuthenTicate(result, String.valueOf(FBCode),
									accessToekn, "", emailEdit.getText()
											.toString());
							new Thread(new Runnable() {

								@Override
								public void run() {
									mCallback.facebookLogout(mLOgout);
								}
							}).start();

						}
					}
				}
			});

			cancelBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						InputMethodManager mInput = (InputMethodManager) mactvity
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						mInput.hideSoftInputFromInputMethod(
								emailEdit.getWindowToken(), 0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					dialog.dismiss();
				}
			});

			dialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}
}
