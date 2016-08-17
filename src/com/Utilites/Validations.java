package com.Utilites;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.hb.barter.R;
import com.hb.webserviceutilities.IParseListener;

public class Validations {

	public static Animation expandCollapse(final View v, final boolean expand) {
		return expandCollapse(v, expand, 500);
	}

	public static Animation expandCollapse(final View v, final boolean expand,
			final int duration) {
		int currentHeight = v.getLayoutParams().height;
		v.measure(
				MeasureSpec.makeMeasureSpec(
						((View) v.getParent()).getMeasuredWidth(),
						MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0,
						MeasureSpec.UNSPECIFIED));
		final int initialHeight = v.getMeasuredHeight();

		if ((expand && currentHeight == initialHeight)
				|| (!expand && currentHeight == 0))
			return null;

		if (expand)
			v.getLayoutParams().height = 0;
		else
			v.getLayoutParams().height = initialHeight;
		v.setVisibility(View.VISIBLE);

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				int newHeight = 0;
				if (expand)
					newHeight = (int) (initialHeight * interpolatedTime);
				else
					newHeight = (int) (initialHeight * (1 - interpolatedTime));
				v.getLayoutParams().height = newHeight;
				v.requestLayout();

				if (interpolatedTime == 1 && !expand)
					v.setVisibility(View.GONE);
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		a.setDuration(duration);
		v.startAnimation(a);
		return a;
	}

	static boolean isENd = false;
	public static boolean isThirdClicked = false;

	public static Animation expandCollapsed(final View v, final boolean expand,
			final int duration, final TextView updateButton,
			FragmentActivity fragmentActivity) {

		int currentHeight = v.getLayoutParams().height;
		v.measure(
				MeasureSpec.makeMeasureSpec(
						((View) v.getParent()).getMeasuredWidth(),
						MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0,
						MeasureSpec.UNSPECIFIED));
		final int initialHeight = v.getMeasuredHeight();

		if ((expand && currentHeight == initialHeight)
				|| (!expand && currentHeight == 0))
			return null;

		if (expand)
			v.getLayoutParams().height = 0;
		else
			v.getLayoutParams().height = initialHeight;
		v.setVisibility(View.VISIBLE);

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				int newHeight = 0;
				if (expand)
					newHeight = (int) (initialHeight * interpolatedTime);
				else
					newHeight = (int) (initialHeight * (1 - interpolatedTime));

				if (isENd) {
					isENd = false;
					// newHeight = 500;
					v.setLayoutParams(new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
				} else {
					v.getLayoutParams().height = newHeight;
				}
				v.requestLayout();

				if (interpolatedTime == 1 && !expand)
					v.setVisibility(View.GONE);
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		a.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

				// if (updateButton.getVisibility() == View.VISIBLE) {
				// updateButton.setVisibility(View.INVISIBLE);
				// }

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (isThirdClicked)
					isENd = true;

				updateButton.setVisibility(View.VISIBLE);
				if (updateButton.getVisibility() == View.INVISIBLE
						|| updateButton.getVisibility() == View.GONE) {
				}

				// mScrollContainer.setLayoutParams(new
				// LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
				// mScrollContainer.requestLayout();

			}
		});
		a.setDuration(duration);
		v.startAnimation(a);
		return a;
	}

	public static String getString(JSONObject obj, String key) {
		if (!obj.has(key)) {
			return "";
		}
		String value;
		try {
			value = obj.getString(key);
			if (value != null && value.equalsIgnoreCase("null")) {
				value = "";
			}
		} catch (JSONException e) {
			value = "";
		}
		return value;
	}

	/****** Commented both methods, coz now using cusomized alerts **********/
	// public static void showAlerDialog(String mTitle, String mMessage,
	// FragmentActivity mActivity) {
	//
	// Builder mDialog = new AlertDialog.Builder(mActivity);
	// mDialog.setTitle(mTitle);
	// mDialog.setMessage(mMessage);
	//
	// mDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	// dialog.dismiss();
	// }
	// });
	//
	// }

	// public static void showAlerDialog(String mMessage,
	// FragmentActivity mActivity) {
	// AlertDialog.Builder mDialog = new AlertDialog.Builder(mActivity);
	// mDialog.setTitle(com.hb.barter.R.string.barter);
	// mDialog.setMessage(mMessage);
	// mDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// });
	// Dialog dialog=mDialog.create();
	// dialog.setCanceledOnTouchOutside(false);
	// dialog.show();
	// }

	/**
	 * Validates User Email address
	 * 
	 * @param email
	 * @return true if email is valid else false
	 */
	public static boolean isValidEmail(String emailString) {

		if (Build.VERSION.SDK_INT < 8) {
			if (emailString.toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")
					&& emailString.length() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			CharSequence inputStr = emailString;
			Pattern pattern = Patterns.EMAIL_ADDRESS;
			Matcher matcher = pattern.matcher(inputStr);
			return matcher.matches();
		}

	}

	public static void showKeyboard(FragmentActivity activity) {

		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
	}

	public static void hideKeyboard(FragmentActivity activity) {

		if (activity != null){
			try {
				InputMethodManager inputManager = (InputMethodManager) activity
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
				Log.e("KeyBoardUtil", e.toString(), e);
			}
		}
			
	}

	/**
	 * This method checks the status of Newtwork,returns boolean i.e if network
	 * is available then returns true elsee false
	 * 
	 * @param mContext
	 * @return connection
	 */

	public static boolean isNetworkAvailable(Context mContext) {
		boolean connection = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo net_info = cm.getActiveNetworkInfo();
				if (net_info != null && net_info.isConnected()) {
					connection = true;

					int type = net_info.getType();

					switch (type) {
					case ConnectivityManager.TYPE_MOBILE:

						break;
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
		}

		return connection;
	}

	public static File bitmapToFile(Bitmap bitmap) {

		try {
			long current = System.currentTimeMillis();
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/image" + current + ".png");
			FileOutputStream fOut;
			fOut = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, fOut);
			fOut.flush();
			fOut.close();
			return file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int screenWidth;
	public static int screenHeight;
	public static String udid;
	public static String deviceToken;

	/*
	 * 
	 * Set Slider Height and Width Proportions
	 */

	public static void setMenuWidthHeightProportions(Activity mActivity) {

		TelephonyManager mTelephonyMgr = (TelephonyManager) mActivity
				.getSystemService(Context.TELEPHONY_SERVICE);
		udid = mTelephonyMgr.getDeviceId();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		screenWidth = displaymetrics.widthPixels;
		screenHeight = displaymetrics.heightPixels;

		deviceToken = android.provider.Settings.System.getString(
				mActivity.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);

	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void showSingleBtnDialog(String message, Context context) {
		TextView btnOK, btnCancel, txtTitle, txtMessage;

		String title = "Bartr";

		final Dialog alertDialog = new Dialog(context,
				R.style.AlertDialogCustom);
		alertDialog.setCancelable(false);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setContentView(R.layout.layout_alert_info);
		txtTitle = (TextView) alertDialog.findViewById(R.id.txtTitle);
		txtMessage = (TextView) alertDialog.findViewById(R.id.txtMessage);

		alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = alertDialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

		if (title != null) {
			txtTitle.setText(title);
		}

		txtMessage.setText(message);

		btnOK = (TextView) alertDialog.findViewById(R.id.btnOK);
		btnOK.setText("OK");
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});

		alertDialog.show();
	}

	public static void showTwoBtnDialog(final Activity activity,
			String positiveBtnTxt, String negativeBtnTxt, String message) {

		TextView btnOK, btnCancel, txtTitle, txtMessage;

		String title = "Bartr";

		final Dialog alertDialog = new Dialog(activity,
				R.style.AlertDialogCustom);
		alertDialog.setCancelable(false);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setContentView(R.layout.layout_exit_alert);
		txtTitle = (TextView) alertDialog.findViewById(R.id.txtTitle);
		txtMessage = (TextView) alertDialog.findViewById(R.id.txtMessage);

		alertDialog.getWindow().getAttributes().windowAnimations = R.style.AlertDialogCustom;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = alertDialog.getWindow();
		lp.copyFrom(window.getAttributes());
		// This makes the dialog take up the full width
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);

		if (title != null) {
			txtTitle.setText(title);
		}

		txtMessage.setText(message);

		btnOK = (TextView) alertDialog.findViewById(R.id.btnOK);
		btnOK.setText(positiveBtnTxt);
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		btnCancel = (TextView) alertDialog.findViewById(R.id.btnCancel);
		btnCancel.setText(negativeBtnTxt);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		btnCancel.setVisibility(View.VISIBLE);

		alertDialog.show();
	}

}
