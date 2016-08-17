package com.hb.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.barter.xmpp.HBXMPP;
import com.hb.barter.R;
import com.hb.fragments.NearByFragment;
import com.hb.models.NearByUsersData;
import com.hb.webserviceutilities.Webservices;

public class NearByUsersListAdapter extends BaseAdapter {

	private List<NearByUsersData> mNearByUsersList = new ArrayList<NearByUsersData>();
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	protected AQuery aq;
	private HBXMPP hbxmppObj;
	private NearByFragment mNearByFragment;

	private SharedPreferences userDetails;

	public NearByUsersListAdapter(Context mContext,
			List<NearByUsersData> mNearByUsersList, HBXMPP hbxmpp,
			NearByFragment nearByFragment) {

		this.mContext = mContext;
		this.mNearByUsersList = mNearByUsersList;
		aq = new AQuery(mContext);
		hbxmppObj = hbxmpp;
		mNearByFragment = nearByFragment;
		userDetails = PreferenceManager.getDefaultSharedPreferences(mContext);

	}

	public NearByUsersListAdapter(Context mContext2) {
		// TODO Auto-generated constructor stub

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNearByUsersList.size();
	}

	@Override
	public NearByUsersData getItem(int position) {
		// TODO Auto-generated method stub
		return mNearByUsersList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public class Holder {
		ImageView mUserStatus, mUserImage;
		TextView mUserName, mUserDetails, mUserDistance;
		RatingBar mRatingBar;

	}

	Holder mHolder = null;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mHolder = null;

		NearByUsersData mUsersData = mNearByUsersList.get(position);

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.nearby_cell, null);
			mHolder = new Holder();

			mHolder.mUserName = (TextView) convertView
					.findViewById(R.id.user_name);
			mHolder.mUserDetails = (TextView) convertView
					.findViewById(R.id.user_details);
			mHolder.mRatingBar = (RatingBar) convertView
					.findViewById(R.id.fivestar_rating);

			mHolder.mRatingBar.setEnabled(false);

			mHolder.mUserImage = (ImageView) convertView
					.findViewById(R.id.user_image);
			mHolder.mUserStatus = (ImageView) convertView
					.findViewById(R.id.online_status);

			mHolder.mUserDistance = (TextView) convertView
					.findViewById(R.id.user_distance);

			convertView.setTag(mHolder);

		} else {

			mHolder = (Holder) convertView.getTag();
		}

		ImageOptions options = new ImageOptions();

		options.round = 1200;
		int px;
		px = 65;

		// aq.id(mHolder.mUserImage).image(mUsersData.getmImageURL(), options);

		if (TextUtils.isEmpty(mUsersData.getmImageURL())
				|| mUsersData.getmImageURL().contains("noimage")) {
			aq.id(mHolder.mUserImage).image(R.drawable.com_images_);

		} else {
			aq.id(mHolder.mUserImage)
			.progress(R.id.progress)
			.image(mUsersData.getmImageURL(), true, true, 185, 0,
					new BitmapAjaxCallback() {
				@Override
				protected void callback(String url, ImageView iv, Bitmap bm,AjaxStatus status) {
					iv.setImageBitmap(makeMaskImage(mHolder.mUserImage,
							bm));
				}
			});

			// aq.id(mHolder.mUserImage).image(mUsersData.getmImageURL(),);
			// Mask image
			// Bitmap
			// mBimap=Validations.getBitmapFromURL(mUsersData.getmImageURL());
			// aq.id(mHolder.mUserImage).image(makeMaskImage(mHolder.mUserImage,
			// mBimap));

		}

		mHolder.mUserName.setText(mUsersData.getmUserName());

		// mHolder.mUserDetails.setText(mUsersData.getmMatchPreferences());
		// i_want_currency
		mHolder.mUserDetails.setText(userDetails.getString("i_want_currency",
				"USD"));

		String mRatingValue = mUsersData.getmRatingStatus();

		if (!TextUtils.isEmpty(mUsersData.getmRatingStatus())) {
			mHolder.mRatingBar.setRating(Float.parseFloat(mUsersData
					.getmRatingStatus()));
		}

		// String miles = mUsersData.getmDistance();

		double value = Double.parseDouble(mUsersData.getmDistance());
		String mDistanceValue = String.format("%.0f", value);

		mHolder.mUserDistance.setText("within " + mDistanceValue + " mi");

		if (mNearByFragment.getAvailability(
				mUsersData.getmJabberId() + Webservices.CHAT_DOMAIN)
				.equalsIgnoreCase("Online")) {

			Log.i("CHATTING", mUsersData.getmJabberId()
					+ Webservices.CHAT_DOMAIN + "Online");
			mHolder.mUserStatus.setImageResource(R.drawable.online);
		} else {

			Log.i("CHATTING", mUsersData.getmJabberId()
					+ Webservices.CHAT_DOMAIN + "offline");
			mHolder.mUserStatus.setImageResource(R.drawable.offline);
		}

		return convertView;
	}

	public void addItem(com.hb.models.Chat data, boolean status) {
		// TODO Auto-generated method stub

	}

	public void removeall() {
		// TODO Auto-generated method stub

	}

	public void refreshPresence() {
		// TODO Auto-generated method stub
		notifyDataSetChanged();

	}

	public void removItemat(NearByUsersData muserData) {
		mNearByUsersList.remove(muserData);
		refreshPresence();
	}

	public Bitmap makeMaskImage(ImageView mImageView, Bitmap original) {

		// Bitmap original = BitmapFactory
		// .decodeResource(getResources(), mContent);
		Bitmap mask = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.com_images_);
		Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(),
				Config.ARGB_8888);

		int mask_reWidht = mask.getWidth();
		int mask_reHeight = mask.getHeight();
		Bitmap resized = Bitmap.createScaledBitmap(original, mask_reWidht,
				mask_reHeight, true);

		Canvas mCanvas = new Canvas(result);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mCanvas.drawBitmap(resized, 0, 0, null);
		mCanvas.drawBitmap(mask, 0, 0, paint);
		paint.setXfermode(null);

		return result;
		// mImageView.setScaleType(ScaleType.CENTER);
		// mImageView.setImageBitmap(result);
		// mImageView.setBackgroundResource(R.drawable.photo_4);

	}

	// public Bitmap getCroppedBitmap(Bitmap bitmap) {
	// Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	// bitmap.getHeight(), Config.ARGB_8888);
	// Canvas canvas = new Canvas(output);
	//
	// final int color = 0xff424242;
	// final Paint paint = new Paint();
	// final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	//
	// paint.setAntiAlias(true);
	// canvas.drawARGB(0, 0, 0, 0);
	// paint.setColor(color);
	// // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	// canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
	// bitmap.getWidth() / 2, paint);
	// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	// canvas.drawBitmap(bitmap, rect, rect, paint);
	// // Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
	// // return _bmp;
	// return output;
	// }

}
