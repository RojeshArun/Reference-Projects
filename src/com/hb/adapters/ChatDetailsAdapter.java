package com.hb.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.barter.xmpp.HBMessage;
import com.hb.barter.R;
import com.hb.fragments.ChatDetailFragment;
import com.hb.webserviceutilities.Webservices;

public class ChatDetailsAdapter extends BaseAdapter {

	private List<HBMessage> mList = new ArrayList<HBMessage>();
	private Context mContext;
	private LayoutInflater mInflater;
	private String userID, userImageUrl, frndImageUrl;
	protected AQuery aq;
	private Fragment mFragment;

	// Date Layout
	private List<String> mTime = new ArrayList<String>();

	public ChatDetailsAdapter(FragmentActivity mActivity, String string,
			ChatDetailFragment chatDetailsFragment) {
		this.mFragment = chatDetailsFragment;
		this.mContext = mActivity;
		this.userID = string;
	}

	public ChatDetailsAdapter(FragmentActivity activity, String useridStr,
			String userimage, String frndimage,
			ChatDetailFragment chatDetailsFragment) {
		this.mFragment = chatDetailsFragment;
		this.mContext = activity;
		this.userID = useridStr;
		userImageUrl = userimage;
		frndImageUrl = frndimage;
		aq = new AQuery(activity);
	}

	public void addItems(List<HBMessage> mList) {
		this.mList = new ArrayList<HBMessage>(mList);
		notifyDataSetChanged();
	}

	public void removeItem(int pos) {
		this.mList.remove(pos);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public HBMessage getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class Holder {
		private ImageView frndImage, myImage;
		private TextView frndMessage, myMessage, frndmessageTime,
				mymessageTime, txtDate;
		private RelativeLayout frndLayout;
		private LinearLayout myLayout, mDatLayout;
		private RatingBar mRatingBar;
		private ImageView mMyLocation, mMyFriendsLocation;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			holder = new Holder();
			// Initializing the Adapter items
			convertView = mInflater.inflate(R.layout.chatdetailadapter, null);
			holder.frndImage = (ImageView) convertView
					.findViewById(R.id.frndimageview);
			holder.myImage = (ImageView) convertView
					.findViewById(R.id.myimageview);
			holder.frndLayout = (RelativeLayout) convertView
					.findViewById(R.id.frndlayout);
			holder.frndImage = (ImageView) convertView
					.findViewById(R.id.frndimageview);
			holder.mRatingBar = (RatingBar) convertView
					.findViewById(R.id.fivestar_rating);
			holder.frndMessage = (TextView) convertView
					.findViewById(R.id.friend_text);
			holder.frndmessageTime = (TextView) convertView
					.findViewById(R.id.frnd_txtdate);
			holder.myLayout = (LinearLayout) convertView
					.findViewById(R.id.mylayout);
			holder.myImage = (ImageView) convertView
					.findViewById(R.id.myimageview);
			holder.myMessage = (TextView) convertView
					.findViewById(R.id.my_text);
			holder.mymessageTime = (TextView) convertView
					.findViewById(R.id.my_txtdate);

			holder.mMyLocation = (ImageView) convertView
					.findViewById(R.id.my_location);
			holder.mMyFriendsLocation = (ImageView) convertView
					.findViewById(R.id.friend_location);

			holder.mDatLayout = (LinearLayout) convertView
					.findViewById(R.id.datelayout);
			holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		ImageOptions options = new ImageOptions();
		options.round = 1000;
		int px;
		px = 40;
		if (!(mList.get(position).getFromJID().equalsIgnoreCase(userID
				+ Webservices.CHAT_DOMAIN))) {
			holder.frndLayout.setVisibility(View.GONE);
			holder.myLayout.setVisibility(View.VISIBLE);
			holder.mymessageTime.setText(GetMessageTime(mList.get(position)
					.getMessageTime()));

			if (TextUtils.isEmpty(userImageUrl)
					|| userImageUrl.contains("noimage")) {
				aq.id(holder.myImage).image(R.drawable.com_images_).width(px)
						.height(px);
			} else {
//				aq.id(holder.myImage).image(userImageUrl, options).width(px)
//						.height(px);
//				
				aq.id(holder.myImage).image(userImageUrl, true,
						true, 185, 0, new BitmapAjaxCallback() {
							@Override
							protected void callback(String url, ImageView iv,
									Bitmap bm, AjaxStatus status) {

								super.callback(url, holder.myImage,
										makeMaskImage(holder.myImage, bm),
										status);
							}
						});
				
			}
			String message = mList.get(position).getMessage();
			if (!TextUtils.isEmpty(message)) {
				if (message.contains(mContext
						.getString(R.string._location_reg_expression))) {
					holder.mMyLocation.setVisibility(View.VISIBLE);
					Log.e("MY  Location VISIBLE", position + "");
					String[] mAddresss = message.split(mContext
							.getString(R.string._location_reg_expression));
					
					if(mAddresss.length>4)
					message = mAddresss[0] + mAddresss[3] + mAddresss[4];
					holder.mMyLocation.setTag(mAddresss[1] + mAddresss[2]);
				} else {
					holder.mMyLocation.setVisibility(View.GONE);
					Log.e("MY Location GONE", position + "");
				}
				holder.myMessage.setText(message);
			}
		} else {
			holder.frndLayout.setVisibility(View.VISIBLE);
			holder.myLayout.setVisibility(View.GONE);
			holder.frndmessageTime.setText(GetMessageTime(mList.get(position)
					.getMessageTime()));

			if (TextUtils.isEmpty(frndImageUrl)
					|| frndImageUrl.contains("noimage")) {
				aq.id(holder.frndImage).image(R.drawable.com_images_).width(px)
						.height(px);
				
				
			} else {
				
				//Mask Image
				
//				makeMaskImage(holder.frndImage,);
				
//				aq.id(holder.frndImage).image(frndImageUrl, options).width(px)
//						.height(px);
				
				aq.id(holder.frndImage).image(frndImageUrl, true,
						true, 185, 0, new BitmapAjaxCallback() {
							@Override
							protected void callback(String url, ImageView iv,
									Bitmap bm, AjaxStatus status) {

								super.callback(url, holder.frndImage,
										makeMaskImage(holder.frndImage, bm),
										status);
							}
						});
				
			}

			String message = mList.get(position).getMessage();
			if (!TextUtils.isEmpty(message)) {

				if (message.contains(mContext
						.getString(R.string._location_reg_expression))) {
					holder.mMyFriendsLocation.setVisibility(View.VISIBLE);
					Log.e("MY Friend Location VISIBLE", position + "");

					String[] mAddress = message.split(mContext
							.getString(R.string._location_reg_expression));

					message = mAddress[0]+mAddress[3] + mAddress[4];

					holder.mMyFriendsLocation.setTag(mAddress[1] + mAddress[2]);
				} else {
					holder.mMyFriendsLocation.setVisibility(View.GONE);

					Log.e("MY Friend Location GONE", position + "");
				}

				message = message.replaceFirst("I am", mList.get(position)
						.getFromName() + " is");
				holder.frndMessage.setText(message);
			}
		}

		// 23 September 2014 06:13 PM
		HBMessage message;
		try {
			message = mList.get(position);
			SimpleDateFormat sdfDB = new SimpleDateFormat(
					"dd MMMM yyyy hh:mm a");
			SimpleDateFormat sdfList = new SimpleDateFormat("dd/MM/yyyy");
			Date date = sdfDB.parse(message.getMessageTime());

			if (position > 0) {
				HBMessage messagePrevious = mList.get(position - 1);
				Date datePrevious = sdfDB.parse(messagePrevious
						.getMessageTime());

				if (date.getDate() != datePrevious.getDate()) {
					holder.mDatLayout.setVisibility(View.VISIBLE);
					holder.txtDate.setText(sdfList.format(date));
				} else {
					holder.mDatLayout.setVisibility(View.GONE);
				}
			} else {
				holder.mDatLayout.setVisibility(View.VISIBLE);
				holder.txtDate.setText(sdfList.format(date));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return convertView;
	}

	@SuppressLint("SimpleDateFormat")
	private CharSequence GetMessageTime(String messageTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		SimpleDateFormat df = new SimpleDateFormat("EEEE HH:mm");
		String dayVal = new String();
		try {
			Date messageDateandTime = formatter.parse(messageTime);
			String messageDate = df.format(messageDateandTime);
			String messageVal[] = messageDate.split(" ");
			// dayVal = messageVal[0] + " @ " + messageVal[1] + " "
			// + messageVal[2];
			dayVal = messageVal[1];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dayVal;
	}

	public void addItem(HBMessage hbMessage) {
		if (this.mList != null) {
			this.mList.add(hbMessage);
		}
		notifyDataSetChanged();
	}

	public void removeall() {
		if (this.mList != null) {
			this.mList.clear();
		}
		notifyDataSetChanged();
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
