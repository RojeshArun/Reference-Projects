package com.hb.adapters;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xbill.DNS.MRRecord;

import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.floatingfragments.BlockedUsesListFragment;
import com.hb.models.NearByUsersData;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class BlockedUsersListAdapter extends BaseAdapter implements
		OnClickListener, IParseListener {

	private List<NearByUsersData> mBlockedUsersList = new ArrayList<NearByUsersData>();
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private SharedPreferences userDetails;
	private MainActivity mMainActivity;
	protected AQuery aq;
	String user_id;
	private static final int UNBLOCK_USER = 301;

	BlockedUsesListFragment mBlockedFrag;

	public BlockedUsersListAdapter(Context mContext,
			List<NearByUsersData> mNearByUsersList,
			BlockedUsesListFragment mBlockedFrag) {

		this.mContext = mContext;
		this.mBlockedUsersList = mNearByUsersList;

		aq = new AQuery(mContext);
		userDetails = PreferenceManager.getDefaultSharedPreferences(mContext);
		if (userDetails.contains("Loggedin_userid")) {
			user_id = userDetails.getString("Loggedin_userid", "def");
		}
		this.mBlockedFrag = mBlockedFrag;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mBlockedUsersList.size();
	}

	@Override
	public NearByUsersData getItem(int position) {
		// TODO Auto-generated method stub
		return mBlockedUsersList.get(position);
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
		CheckBox mBlocked;

	}

	NearByUsersData mUsersData;

	Holder mHolder = null;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mUsersData = mBlockedUsersList.get(position);

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.blockusers_cell,
					null);
			mHolder = new Holder();

			mHolder.mUserName = (TextView) convertView
					.findViewById(R.id.user_name);
			mHolder.mUserDetails = (TextView) convertView
					.findViewById(R.id.user_details);
			mHolder.mRatingBar = (RatingBar) convertView
					.findViewById(R.id.fivestar_rating);
			mHolder.mUserImage = (ImageView) convertView
					.findViewById(R.id.user_image);
			mHolder.mUserStatus = (ImageView) convertView
					.findViewById(R.id.online_status);

			mHolder.mUserDistance = (TextView) convertView
					.findViewById(R.id.block_user_distance);
			mHolder.mBlocked = (CheckBox) convertView
					.findViewById(R.id.check_round_off);

			convertView.setTag(mHolder);

		} else {

			mHolder = (Holder) convertView.getTag();
		}
		ImageOptions options = new ImageOptions();

		options.round = 1200;
		int px;
		px = 60;
		// aq.id(mHolder.mUserImage).image(mUsersData.getmImageURL(), options)
		// .width(px).height(px);
		String userImageUrl = mUsersData.getmImageURL();

		if (TextUtils.isEmpty(userImageUrl) || userImageUrl.contains("noimage")) {
			aq.id(mHolder.mUserImage).image(R.drawable.com_images_).width(px)
					.height(px);
		} else {
			// aq.id(holder.myImage).image(userImageUrl, options).width(px)
			// .height(px);
			//
			aq.id(mHolder.mUserImage).image(userImageUrl, true, true, 185, 0,
					new BitmapAjaxCallback() {
						@Override
						protected void callback(String url, ImageView iv,
								Bitmap bm, AjaxStatus status) {

							super.callback(url, mHolder.mUserImage,
									makeMaskImage(mHolder.mUserImage, bm),
									status);
						}
					});

		}

		mHolder.mUserName.setText(mUsersData.getmDisplayUserName());
		// mHolder.mUserName.setTextColor(R.);
		// mHolder.mUserDetails.setText(mUsersData.getmMatchPreferences());
		mHolder.mBlocked.setTag(mUsersData);
		if (!(TextUtils.isEmpty(mUsersData.getmRatingStatus()))) {
			mHolder.mRatingBar.setRating(Float.parseFloat(mUsersData
					.getmRatingStatus()));
			mHolder.mRatingBar.setEnabled(false);
		}
		mHolder.mUserDistance.setText(mUsersData.getmDistance() + " mi");
		mHolder.mBlocked.setChecked(false);
		mHolder.mBlocked.setOnClickListener(this);
		return convertView;
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		// TODO Auto-generated method stub
		mBlockedFrag.hideProgress();
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {

		switch (requestCode) {
		case UNBLOCK_USER:
			try {
				JSONObject mSettingBlock = new JSONObject(
						response.getString("settings"));
				mBlockedFrag.hideProgress();
				if (mSettingBlock.getString("success").equalsIgnoreCase("1")) {
					// showAlerDialog(mSettingBlock.getString("message"),
					// mContext);
					mBlockedUsersList.remove(mTempData);
					notifyDataSetChanged();
					if (mBlockedUsersList.size() == 0) {
						mBlockedFrag.showNoTextResult();
					} else {
						mBlockedFrag.hideNoTextResult();
					}
				} else {
					// Validations
					// .showAlerDialog(mSettingBlock.getString("message"),
					// (FragmentActivity) mContext
					// .getApplicationContext());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}

	}

	NearByUsersData mTempData;

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.check_round_off:
			mTempData = (NearByUsersData) v.getTag();
			if (Validations.isNetworkAvailable(mContext)) {
				callUnBlockWS(mTempData.getBlockeduserId());
			} else {
				showSingleBtnDialog(
						mContext.getString(R.string.no_internet_connection_please_try_again),
						mContext);

			}

			break;

		default:
			break;
		}

	}

	public void showSingleBtnDialog(String message, Context context) {
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
				mBlockedUsersList.remove(mTempData);
				notifyDataSetChanged();
				if (mBlockedUsersList.size() == 0) {
					mBlockedFrag.slideBack();
				}
			}
		});

		alertDialog.show();
	}

	private void callUnBlockWS(String userId) {

		mBlockedFrag.showProgress();

		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", userId);
		mBundle.putString("action", "unblock");
		mBundle.putString("action_by",
				userDetails.getString("Loggedin_userid", "def"));
		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.BLOCK_UNBLOCK_USER, mBundle),
				UNBLOCK_USER, this);
	}

	public void clearData() {
		// TODO Auto-generated method stub
		mBlockedUsersList.clear();
	}

	public Bitmap makeMaskImage(ImageView mImageView, Bitmap original) {

		Bitmap mask = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.com_images_);
		Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(),
				Config.ARGB_8888);

		int mask_reHeight = mask.getHeight();
		Bitmap resized = Bitmap.createScaledBitmap(original, mask.getWidth(),
				mask_reHeight, true);

		Canvas mCanvas = new Canvas(result);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mCanvas.drawBitmap(resized, 0, 0, null);
		mCanvas.drawBitmap(mask, 0, 0, paint);
		paint.setXfermode(null);

		return result;

	}
}
