package com.barter.facebook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.barter.facebook.Facebook.DialogListener;
import com.hb.barter.R;
import com.hb.webserviceutilities.Webservices;

/**
 * @author hb
 * 
 */
public class FacebookUtilityWithCallback {
	private Activity activity;
	private String facebookAppId = Webservices.FACEBOOKAPPID;

	private ProgressDialog progressDialog;
	private Facebook mFacebook;

	private final String[] PERMISSIONS = { "publish_checkins,publish_actions,publish_stream,user_photos,user_videos,email,user_photos" }; // offline_access

	private final int POST = 0, IMAGE = 1, VIDEO = 2, DIALOG = 3, LOGIN = 4;

	private int METHOD_TYPE = -1;

	private Bundle params;

	private FacebookCallbackListner fbcallbackListner = null;

	/**
	 * @param a
	 *            context of Activity in which you want to use this Utility
	 * @param facebookAppId
	 *            appId of your application which you have registered in
	 *            Facebook
	 */
	public FacebookUtilityWithCallback(Activity a, String facebookAppId) {
		activity = a;

		progressDialog = new ProgressDialog(activity);
		progressDialog.setIcon(R.drawable.com_images_);
		progressDialog.setTitle("Please wait!");
		progressDialog.setMessage("Sharing...");

		params = new Bundle();
		this.facebookAppId = facebookAppId;
		mFacebook = new Facebook(this.facebookAppId);

		// 187943967969061 my appid
		// Visit http://www.facebook.com/developers/createapp.php to create your
		// own Facebook App id and replace the one given here.
	}

	public boolean autheticateData() {
		boolean success = false;
		try {
			success = SessionStore.restore(mFacebook, activity);

			// long l = mFacebook.getAccessExpires();
			//
			// Calendar calendar = Calendar.getInstance();
			// calendar.setTimeInMillis(l);
			//
			// int hour = calendar.get(Calendar.HOUR);
			// int hour24 = calendar.get(Calendar.HOUR_OF_DAY);
			// int min = calendar.get(Calendar.MINUTE);

			// Log.d("time", ""+l);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}

	public void facebooklogin(FacebookCallbackListner fbcallbacklistner) {
		this.METHOD_TYPE = LOGIN;
		this.fbcallbackListner = fbcallbacklistner;
		fblogin();
	}

	private void fblogin() {
		if (isNetworkAvailable()) {
			if (!autheticateData()) {

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mFacebook.authorize(activity, PERMISSIONS, -1,
								new FbLoginDialogListener());
					}
				});

			} else {
				if ((METHOD_TYPE == LOGIN) && (this.fbcallbackListner != null)) {
					this.fbcallbackListner.success(true, mFacebook);
					this.fbcallbackListner.response("Already Logged In");
				}
			}
		} else {
			this.fbcallbackListner.response("No network connection available.");
			this.fbcallbackListner.success(false, null);
		}
	}

	/**
	 * @return logout from your facebook session
	 */
	public void facebookLogout(FacebookCallbackListner facebookcallback) {
		this.fbcallbackListner = facebookcallback;
		new ThreadFaceBook().start();
	}

	public class ThreadFaceBook extends Thread {
		@Override
		public void run() {
			super.run();
			// fbLogout();
		}
	}

	public void fbLogout(FacebookCallbackListner facebookcallback) {

		if (mFacebook != null) {
			if (autheticateData()) {
				try {
					SessionStore.clear(activity);
					mFacebook.logout(activity);
					facebookcallback.response("Logged out successfully");
					facebookcallback.success(true, mFacebook);
				} catch (Exception ex) {
					facebookcallback.response("Error on logout "
							+ ex.getMessage());
					facebookcallback.success(false, null);
				}
			} else {
				facebookcallback.response("First Login to Logout");
				facebookcallback.success(false, null);
			}
		} else {
			facebookcallback.response("First Login to Logout");
			facebookcallback.success(false, null);
		}
	}
	
	public void fbLogout() {

		if (mFacebook != null) {
			if (autheticateData()) {
				try {
					SessionStore.clear(activity);
					mFacebook.logout(activity);
					fbcallbackListner.response("Logged out successfully");
					fbcallbackListner.success(true, mFacebook);
				} catch (Exception ex) {
					fbcallbackListner.response("Error on logout "
							+ ex.getMessage());
					fbcallbackListner.success(false, null);
				}
			} else {
				fbcallbackListner.response("First Login to Logout");
				fbcallbackListner.success(false, null);
			}
		} else {
			fbcallbackListner.response("First Login to Logout");
			fbcallbackListner.success(false, null);
		}
	}

	public void inviteFriends(String message) {
		// Safe programming

		Bundle parameters = new Bundle();

		parameters.putString("message", message);
		mFacebook.dialog(activity, "apprequests", parameters,
				new Facebook.DialogListener() {
					@Override
					public void onComplete(Bundle values) {
						// TODO Auto-generated method stub
						if (callBack != null) {
							String st = values.getString("request");
							if (st != null) {
								callBack1.getCancelListnerWithSuccess();
							} else {
								callBack.getCancelListner();
							}
						}

					}

					@Override
					public void onFacebookError(FacebookError e) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onError(DialogError e) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onCancel() {
					}
				});

	}

	// public void inviteFriends(String message, DialogListener mDialogListener)
	// {
	// // Safe programming
	// params.clear();
	// if (autheticateData()) {
	// // Bundle parameters = new Bundle();
	//
	// // Get the friend ids
	// // String friendsIdsInFormat = "";
	// // for (int i = 0; i < friendsIds.size() - 1; i++) {
	// // friendsIdsInFormat = friendsIdsInFormat + friendsIds.get(i) +
	// // ", ";
	// // }
	// // friendsIdsInFormat = friendsIdsInFormat
	// // + friendsIds.get(friendsIds.size() - 1).getId();
	//
	// // parameters.putString("to", friendsIdsInFormat);
	// params.putString("message", message);
	//
	// // Show dialog for invitation
	// mFacebook.dialog(activity, "apprequests", params, mDialogListener);
	// } else {
	// fblogin();
	// }
	//
	// }

	// public boolean fbLogout() {
	// boolean success = false;
	//
	// if (mFacebook != null) {
	// // mFacebook = new Facebook(facebookAppId);
	// //
	// // if(mFacebook.isSessionValid())
	// // {
	// try {
	// SessionStore.clear(activity);
	// mFacebook.logout(activity);
	// success = true;
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// // }
	// }
	// return success;
	// }

	private final class FbLoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			mFacebook.setAccessExpires(0);
			SessionStore.save(mFacebook, activity);

			if (values != null) {
				Set<String> set = values.keySet();
				for (Iterator<String> iterator = set.iterator(); iterator
						.hasNext();) {
					String key = iterator.next();
					String value = null;
					try {
						value = (String) values.get(key);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (value != null) {
					} else {
					}

					if (iterator.hasNext()) {
					} else {
					}

				}
			}

			switch (METHOD_TYPE) {
			case POST:
				fbPost();
				break;

			case IMAGE:
				fbUploadImageToALbum();
				break;

			case VIDEO:
				fbUploadVideoToAlbum();
				break;

			case DIALOG:
				fbdialog();
				break;

			case LOGIN:
				if ((METHOD_TYPE == LOGIN) && (fbcallbackListner != null)) {
					fbcallbackListner.success(true, mFacebook);
					fbcallbackListner.response("Logged in successfully");
				}
				break;
			}

		}

		public void onFacebookError(FacebookError error) {
			if (fbcallbackListner != null) {
				fbcallbackListner.response(error.getMessage());
				fbcallbackListner.success(false, null);
			}
		}

		public void onError(DialogError error) {
			if (fbcallbackListner != null) {
				fbcallbackListner.response(error.getFailingUrl());
				fbcallbackListner.success(false, null);
			}
		}

		public void onCancel() {

			if (fbcallbackListner != null) {
				fbcallbackListner.response("Cancelled");
				fbcallbackListner.success(false, null);
			}

		}
	}

	private final class WallPostListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, Object state) {

			if (progressDialog.isShowing())
				progressDialog.dismiss();

			if ((response != null && response.contains("error"))
					|| (response == null)) // {"id":"424647147560692","post_id":"100000462326257_424647167560690"}
			{
				if (fbcallbackListner != null) {
					fbcallbackListner.response(response);
					fbcallbackListner.success(false, null);
				}
			} else {
				if (fbcallbackListner != null) {
					fbcallbackListner.response(response);
					fbcallbackListner.success(true, mFacebook);
				}
				// handler.post(new Runnable() {

			}
		}

		@Override
		public void onIOException(IOException e, Object state) {

			if (progressDialog.isShowing())
				progressDialog.dismiss();

			if (fbcallbackListner != null) {
				fbcallbackListner.response(e.getMessage());
				fbcallbackListner.success(false, mFacebook);
			}
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {

			if (progressDialog.isShowing())
				progressDialog.dismiss();

			if (fbcallbackListner != null) {
				fbcallbackListner.response(e.getMessage());
				fbcallbackListner.success(false, null);
			}
			// Toast.makeText(activity,
			// "Not Posted to Facebook! File not found",
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {

			if (progressDialog.isShowing())
				progressDialog.dismiss();

			if (fbcallbackListner != null) {
				fbcallbackListner.response(e.getMessage());
				fbcallbackListner.success(false, null);
			}
			// Toast.makeText(activity, "Not Posted to Facebook! Malformed Url",
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {

			if (progressDialog.isShowing())
				progressDialog.dismiss();

			if (fbcallbackListner != null) {
				fbcallbackListner.response(e.getMessage());
				fbcallbackListner.success(false, null);
			}
			// Toast.makeText(activity,
			// "Not Posted to Facebook! " + e.getMessage().toString(),
			// Toast.LENGTH_SHORT).show();
		}
	}

	private void fbPost() {

		// if (!progressDialog.isShowing())
		// progressDialog.show();

		AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		// mAsyncRunner.request("me/photos", params, new WallPostListener());
		// --> gets wallpaper
		// mAsyncRunner.request("me/feed", params, new WallPostListener()); -->
		// get first 10 posts
		mAsyncRunner.request("me/feed", params, "POST", new WallPostListener(),
				null);
	}

	public void fbdialog() {
		mFacebook.dialog(activity, "feed", params, new SampleDialogListener());
	}

	private void fbUploadImageToALbum() {

		if (!progressDialog.isShowing())
			progressDialog.show();

		params.putString("method", "photos.upload");

		AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		mAsyncRunner
				.request(null, params, "POST", new WallPostListener(), null);
	}

	private void fbUploadVideoToAlbum() {

		if (!progressDialog.isShowing())
			progressDialog.show();

		params.putString("method", "videos.upload");

		AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		mAsyncRunner
				.request(null, params, "POST", new WallPostListener(), null);
	}

	/**
	 * @param message
	 *            message that is to be posted on your wall
	 * @throws Exception
	 */
	public void post(String message, FacebookCallbackListner fbcallbackListner)
			throws Exception {
		this.fbcallbackListner = fbcallbackListner;
		// params.clear();

		params.putString("message", message);

		METHOD_TYPE = POST;

		if (autheticateData()) {
			fbPost();

		} else {
			fblogin();
		}
	}

	/**
	 * @param linkUrl
	 *            link address which you want to share
	 * 
	 * @throws Exception
	 */
	public void shareLinkWithDialog(String linkUrl,
			FacebookCallbackListner fbcallback) throws Exception {
		this.fbcallbackListner = fbcallback;
		params.clear();

		if (isurlValid(linkUrl)) {
			params.putString("link", linkUrl);
			params.putString("message", "Sharing Link");
			params.putString("name", "");
			params.putString("caption", "");
			params.putString("description", "");
			params.putString("picture", "");

			METHOD_TYPE = DIALOG;

			if (autheticateData()) {
				mFacebook.dialog(activity, "feed", params,
						new SampleDialogListener());
			} else {
				fblogin();
			}
		} else {
			if (fbcallbackListner != null) {
				fbcallbackListner.success(false, null);
				fbcallbackListner.response("Invalid Link Url");
			}
		}
	}

	/**
	 * @param message
	 *            message that is to be posted on your wall
	 * @param link
	 *            link address which you want to share
	 * @param linkName
	 *            name of the link address which you have specified above
	 * @throws Exception
	 */
	public void openDialogAndShare(String linkUrl, String linkName,
			String linkCaption, String linkDescription, String linkPictureURL,
			FacebookCallbackListner fbcallback) throws Exception {
		this.fbcallbackListner = fbcallback;
		params.clear();

		if (isurlValid(linkUrl)) {

			// params.putString("message", message);
			params.putString("link", linkUrl);
			params.putString("name", linkName);
			params.putString("caption", linkCaption);
			params.putString("description", linkDescription);
			params.putString("picture", linkPictureURL);

			METHOD_TYPE = DIALOG;

			if (autheticateData()) {
				mFacebook.dialog(activity, "feed", params,
						new SampleDialogListener());
			} else {
				fblogin();
			}
		} else {
			if (fbcallbackListner != null) {
				fbcallbackListner.success(false, null);
				fbcallbackListner.response("Invalid Link Url");
			}
		}
	}

	public class SampleDialogListener extends BaseDialogListener {
		@Override
		public void onComplete(Bundle values) {
			if (values == null) {
				relogin();
			} else if (values.containsKey("error_code")
					|| values.containsKey("error_msg")) {
				relogin();
			} else {
				final String postId = values.getString("post_id");
				if (postId != null) {
					if (fbcallbackListner != null) {
						fbcallbackListner
								.response("Posted to facebook successfully");
						fbcallbackListner.success(true, mFacebook);
					}
					// mAsyncRunner.request(postId, new
					// WallPostRequestListener());
				} else {
					if (fbcallbackListner != null) {
						fbcallbackListner.response("Not posted to facebook");
						fbcallbackListner.success(false, null);
					}
					// Not posted
				}
			}
		}

		@Override
		public void onCancel() {
			super.onCancel();
		}

		@Override
		public void onError(DialogError e) {
			super.onError(e);
		}

		@Override
		public void onFacebookError(FacebookError e) {
			super.onFacebookError(e);
		}

	}

	public class WallPostRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {

		}
	}

	/**
	 * @param message
	 * @param linkUrl
	 * @param linkCaption
	 * @param linkName
	 * @param linkDescription
	 * @param linkPictureURL
	 * @param fbcallback
	 * @throws Exception
	 */
	public void shareLink(String message, String linkUrl, String linkCaption,
			String linkName, String linkDescription, String linkPictureURL,
			FacebookCallbackListner fbcallback) throws Exception {
		this.fbcallbackListner = fbcallback;
		params.clear();

		if (!isurlValid(linkUrl)) {
			if (fbcallbackListner != null) {
				fbcallbackListner.success(false, null);
				fbcallbackListner.response("Invalid Link Url");
			}
		}
		// else if(!isurlValid(linkPictureURL))
		// {
		// if(fbcallbackListner!=null)
		// {
		// fbcallbackListner.success(false,null);
		// fbcallbackListner.response("Invalid Link Picture Url");
		// }
		// }
		else {
			params.putString("message", message);
			params.putString("link", linkUrl);
			params.putString("name", linkName);
			params.putString("caption", linkCaption);
			params.putString("description", linkDescription);

			if (isurlValid(linkPictureURL))
				params.putString("linkPictureURL", linkPictureURL);

			METHOD_TYPE = POST;

			if (autheticateData()) {
				fbPost();
			} else {
				fblogin();
			}
		}
	}

	private void relogin() {
		try {
			mFacebook.logout(activity);
			mFacebook.authorize(activity, PERMISSIONS, -1,
					new FbLoginDialogListener());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param imageUrl
	 *            url of the image which you want to share
	 * @param imageCaption
	 *            caption to above image
	 * @param imageDescription
	 *            description about your image
	 * @throws Exception
	 */
	public void shareImage(URL imageUrl, String imageCaption, String message,
			String imageDescription, FacebookCallbackListner fbcallback)
			throws Exception // Working
	{
		this.fbcallbackListner = fbcallback;
		params.clear();

		if (isURLValid(imageUrl)) {

			params.putString("message", message);// "test message "+System.currentTimeMillis());
			params.putString("picture", imageUrl.toString().trim());
			params.putString("caption", imageCaption);
			params.putString("description", imageDescription);

			// params.putString("message",
			// "If message is there then only image is coming");//"test message "+System.currentTimeMillis());
			// params.putString("picture", imageUrl.toString().trim());

			// params.putString("message",
			// "If message is there then only image is coming");//"test message "+System.currentTimeMillis());
			// params.putString("picture", imageUrl.toString().trim());
			// params.putString("caption", imageCaption);
			// params.putString("description", imageDescription);

			// params.putString("message",
			// imageUrl.toString());//"test message "+System.currentTimeMillis());

			METHOD_TYPE = POST;

			if (autheticateData()) {
				fbPost();
			} else {
				fblogin();
			}
		} else {
			if (fbcallbackListner != null) {
				fbcallbackListner.success(false, null);
				fbcallbackListner.response("Invalid Image Url");
			}
		}
		// AsyncFacebookRunner mAsyncRunner = new
		// AsyncFacebookRunner(mFacebook);
		// mAsyncRunner.request("me/feed", params, "POST", new
		// WallPostListener());
	}

	/**
	 * @param imageUrl
	 *            url of the image which you want to share
	 * @param imageCaption
	 *            caption to above image
	 * @param imageDescription
	 *            description about your image
	 * @throws Exception
	 */
	public void shareImage(String imageUrl, String imageCaption,
			String imageDescription, FacebookCallbackListner fbcallback)
			throws Exception // Working
	{

		this.fbcallbackListner = fbcallback;
		params.clear();

		if (isurlValid(imageUrl)) {

			params.putString("message", "");
			params.putString("picture", imageUrl.toString().trim());
			params.putString("caption", imageCaption);
			params.putString("description", imageDescription);

			METHOD_TYPE = POST;

			if (autheticateData()) {
				fbPost();
			} else {
				fblogin();
			}
		} else {
			if (fbcallbackListner != null) {
				fbcallbackListner.success(false, null);
				fbcallbackListner.response("Invalid Image Url");
			}
		}

		// AsyncFacebookRunner mAsyncRunner = new
		// AsyncFacebookRunner(mFacebook);
		// mAsyncRunner.request("me/feed", params, "POST", new
		// WallPostListener());
	}

	/**
	 * @param filePath
	 *            path of the image which you want to share
	 * @param imageCaption
	 *            caption to above image
	 * @param imageDescription
	 *            description about your image
	 * @throws Exception
	 */
	public void shareLocalImage(String filePath, String imageCaption,
			String imageDescription, FacebookCallbackListner pfbcallbackListner)
			throws Exception // Working
	{
		this.fbcallbackListner = pfbcallbackListner;

		final String FilePath = filePath;
		final String iCaption = imageCaption;
		final String iDescription = imageDescription;

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				byte[] byteArray = readBytes(FilePath);

				params.clear();

				params.putByteArray("picture", byteArray);
				params.putString("description", iDescription);
				params.putString("caption", iCaption);

				METHOD_TYPE = IMAGE;

				if (autheticateData()) {
					fbUploadImageToALbum();
				} else {
					fblogin();
				}

			}
		});
	}

	/**
	 * @param imageUrl
	 *            url of the image which you want to share
	 * @param link
	 *            link address which you want to share
	 * @param linkName
	 *            name of the link address which you have specified above
	 * @throws Exception
	 */
	public void shareImageWithLink(URL imageUrl, String linkURL,
			String linkName, FacebookCallbackListner fbcallback)
			throws Exception // Working
	{
		this.fbcallbackListner = fbcallback;
		params.clear();

		if (!isURLValid(imageUrl)) {
			if (fbcallbackListner != null) {
				fbcallbackListner.success(false, null);
				fbcallbackListner.response("Invalid Image Url");
			}
		} else if (!isurlValid(linkURL)) {
			if (fbcallbackListner != null) {
				fbcallbackListner.success(false, null);
				fbcallbackListner.response("Invalid Link URL Url");
			}
		} else {

			params.putString("message", "");
			params.putString("picture", imageUrl.toString());
			params.putString("link", linkURL);
			params.putString("name", linkName);

			METHOD_TYPE = POST;

			if (autheticateData()) {
				fbPost();
			} else {
				fblogin();
			}
		}

		// AsyncFacebookRunner mAsyncRunner = new
		// AsyncFacebookRunner(mFacebook);
		// mAsyncRunner.request("me/feed", params, "POST", new
		// WallPostListener());
	}

	/**
	 * @param filePath
	 *            path of the video which you want to share
	 * @param description
	 *            description about your video
	 * @param caption
	 *            caption to above video
	 * @throws Exception
	 */
	public void shareVideo(String filePath, String description, String caption,
			FacebookCallbackListner fbcallback) throws Exception // working
	{
		this.fbcallbackListner = fbcallback;
		byte[] byteArray = readBytes(filePath);

		params.clear();

		params.putByteArray("video", byteArray);
		params.putString("description", description);
		params.putString("caption", caption);

		METHOD_TYPE = VIDEO;

		if (autheticateData()) {
			fbUploadVideoToAlbum();
		} else {
			fblogin();
		}
	}

	/*
	 * public void shareVideoWithLink(URL videoUrl,String link,String linkName)
	 * throws Exception // Working { params.clear();
	 * 
	 * params.putString("message", ""); params.putString("video",
	 * videoUrl.toString()); params.putString("link",link);
	 * params.putString("name", linkName);
	 * 
	 * METHOD_TYPE = POST;
	 * 
	 * if(autheticateData()) { fbPost(); } else { fblogin(); }
	 * 
	 * //AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	 * //mAsyncRunner.request("me/feed", params, "POST", new
	 * WallPostListener()); }
	 */

	/**
	 * @param videoUrl
	 *            url of the video which you want to share
	 * @param videoDescription
	 *            description about your video
	 * @param videoCaption
	 *            caption to above video
	 * @throws Exception
	 */
	public void shareVideo(URL videoUrl, String videoDescription,
			String videoCaption, FacebookCallbackListner fbcallback)
			throws Exception // working
	{
		this.fbcallbackListner = fbcallback;
		METHOD_TYPE = POST;

		params.clear();

		if (isURLValid(videoUrl)) {

			params.putString("message", "");
			params.putString("name", videoCaption);
			params.putString("description", videoDescription);
			params.putString("link", videoUrl.toString());

			if (autheticateData()) {
				fbPost();
			} else {
				fblogin();
			}
		} else {
			if (fbcallbackListner != null) {
				fbcallbackListner.success(false, null);
				fbcallbackListner.response("Invalid Video Url");
			}
		}

		// AsyncFacebookRunner mAsyncRunner = new
		// AsyncFacebookRunner(mFacebook);
		// mAsyncRunner.request("me/feed", params, "POST", new
		// WallPostListener());
	}

	private byte[] readBytes(String filePath) {
		byte[] byteArray = null;

		// This dynamically extends to take the bytes you read.
		ByteArrayOutputStream byteBuffer;
		try {
			File f = new File(filePath);

			@SuppressWarnings("resource")
			InputStream inputStream = new FileInputStream(f);

			byteBuffer = new ByteArrayOutputStream();

			// This is storage overwritten on each iteration with bytes.
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			// We need to know how may bytes were read to write them to the
			// byteBuffer.
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				byteBuffer.write(buffer, 0, len);

				byteArray = byteBuffer.toByteArray();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// And then we can return your byte array.
		return byteArray;
	}

	private boolean isNetworkAvailable() {
		boolean connection = false;
		try {

			ConnectivityManager cm = (ConnectivityManager) activity
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo net_info = cm.getActiveNetworkInfo();
				if (net_info != null && net_info.isAvailable()
						&& net_info.isConnected())
					connection = true;
			}

			// Returning success
		} // End of try
		catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}// end of isNetworkThere

	private boolean isURLValid(URL url) {
		if (url != null && url.toString().trim().length() > 0
				&& url.toString().trim().startsWith("http"))
			return true;
		else
			return false;
	}

	private boolean isurlValid(String url) {
		if (url != null && url.trim().length() > 0 && url.startsWith("http"))
			return true;
		else
			return false;
	}

	FbFriendsCallBack callBack, callBack1;

	public void setcallBack(FbFriendsCallBack callBack) {
		this.callBack = callBack;
		this.callBack1 = callBack;
	}

	public interface FbFriendsCallBack {
		void getCancelListner();

		void getCancelListnerWithSuccess();
	}
}