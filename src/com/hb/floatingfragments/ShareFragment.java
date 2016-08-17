package com.hb.floatingfragments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.twitter.HBTwitterUtility;
import org.twitter.HBTwitterUtility.TwitterCallbackListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Utilites.Validations;
import com.hb.barter.R;
import com.hb.webserviceutilities.GetSocialDetails;
import com.hb.webserviceutilities.GetSocialDetails.FBUserCallback;
import com.hb.webserviceutilities.Webservices;

public class ShareFragment extends Fragment implements OnClickListener,
		TwitterCallbackListener, FBUserCallback {

	// TopBar Variables
	private ImageView mBackBtn;
	private TextView mHeaderTitle;
	private TextView mBottomLine;

	private ImageButton mImgBtn_ShareFacebook, mImgBtn_ShareTwitter,
			mImgBtn_ShareEmailUs;
	private View mRootView;

	private HBTwitterUtility hbTwitterUtility;

	private LinearLayout mFloatingLayout;

	public ShareFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_share, null);

		setShareProfileFragment();
		setOnClickListener();

		mBackBtn = (ImageView) mRootView.findViewById(R.id.slidedown);
		mBackBtn.setOnClickListener(this);

		mHeaderTitle = (TextView) mRootView.findViewById(R.id.title);
		mHeaderTitle.setText(R.string.share_1);

		hbTwitterUtility = new HBTwitterUtility(getActivity(),
				Webservices.TWITTERCONSUMERKEY,
				Webservices.TWITTERCONSUMERSECRETKEY, this, false);

		return mRootView;

	}

	private void setShareProfileFragment() {

		mImgBtn_ShareFacebook = (ImageButton) mRootView
				.findViewById(R.id.imgBtn_ShareFacebook);
		mImgBtn_ShareTwitter = (ImageButton) mRootView
				.findViewById(R.id.imgBtn_ShareTwitter);
		mImgBtn_ShareEmailUs = (ImageButton) mRootView
				.findViewById(R.id.imgBtn_ShareEmailUs);

		mFloatingLayout = (LinearLayout) mRootView
				.findViewById(R.id.floatingLayout);
		mBottomLine = (TextView) mRootView
				.findViewById(R.id.txtView_LineTopBar);
		mBottomLine.setVisibility(View.GONE);

	}

	private void setOnClickListener() {
		mImgBtn_ShareEmailUs.setOnClickListener(this);
		mImgBtn_ShareFacebook.setOnClickListener(this);
		mImgBtn_ShareTwitter.setOnClickListener(this);
		mFloatingLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.imgBtn_ShareFacebook:
			try {
				saveimagetoSDcard();

				Uri uri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), "bartr_appicon.png"));

				File mFile = new File(
						Environment.getExternalStorageDirectory(),
						"bartr_appicon.png");
				
				shareAppOnFacebook(mFile.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.imgBtn_ShareEmailUs:
			shareAppInEmail();

			break;
		case R.id.imgBtn_ShareTwitter:
			shareAppOnTwitter();
			break;
		case R.id.slidedown:
			slideBack();

			break;
		case R.id.floatingLayout:

			// new Runnable() {
			// public void run() {
			// slideBack();
			// }
			// };
			// MainAcitivity.getInstance().hideFloatingMenu();
			break;
		}

	}

	private void shareAppOnTwitter() {

//		hbTwitterUtility.tweet("Bartr application", "111");
		
//		hbTwitterUtility.shareImage("http://gobartr.net/mobile/public/images/admin/bartr_logo.jpg", "Bartr - The location based currency exchange application", "111");
		
//		hbTwitterUtility.shareImage(R.drawable.icn_barter, "Bartr - The location based currency exchange application", "111");
		
//		hbTwitterUtility.shareImage(url, status, requestCode);'
		
		Bitmap mBitmap;
		
		mBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.icn_barter);
		
		hbTwitterUtility.shareImage(mBitmap, "Bartr - The location based currency exchange application", "111");

	}

	private void shareAppInEmail() {

		saveimagetoSDcard();

		Intent intent = new Intent(Intent.ACTION_SEND);
		// String[] recipients = { "xyz@gmail.com" };
		// intent.putExtra(Intent.EXTRA_EMAIL, recipients);
		intent.putExtra(Intent.EXTRA_SUBJECT,
				"Bartr :Money exchange for the rest fo us");
		intent.putExtra(
				Intent.EXTRA_TEXT,
				getResources().getString(
						R.string.get_the_new_barter_app_it_makes_currency_));

		Uri uri = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), "bartr_appicon.png"));
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		intent.setType("text/html");
		startActivity(Intent.createChooser(intent, "Send mail"));

	}

	private void saveimagetoSDcard() {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = getResources().openRawResource(R.drawable.com_images_);
			out = new FileOutputStream(new File(
					Environment.getExternalStorageDirectory(),
					"bater_appicon.png"));
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
			e.printStackTrace();
		}

	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	private void shareAppOnFacebook(String mFilePath) { 
		GetSocialDetails mSocial = new GetSocialDetails(getActivity());
		// checkFBUser
		if (Validations.isNetworkAvailable(getActivity())) {
			mSocial.setFBUserCallback(this);
			mSocial.getAndPostFaceBookUserDetails();
			mSocial.shareOnFacebook(mFilePath, getActivity());

		} else {
			// Validations.showAlerDialog(
			// "No internet Connection.Please Try again later",
			// getActivity());
			Validations.showSingleBtnDialog(
					"No internet Connection.Please Try again later",
					getActivity());
		}

	}

	private void slideBack() {

		getFragmentManager().popBackStack();

	}

	@Override
	public void twitterCallback(boolean success, String response,
			String requestCode, ArrayList<Object> listResponseData) {

	}

	@Override
	public void getResult(String result, String accesstoken) {
	}

}
