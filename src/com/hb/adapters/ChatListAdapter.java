package com.hb.adapters;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.barter.xmpp.ChatDBHelper;
import com.barter.xmpp.HBXMPP;
import com.hb.barter.R;
import com.hb.baseapplication.BaseApplication;
import com.hb.fragments.ChatListingFragment;
import com.hb.models.Chat;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

public class ChatListAdapter extends BaseAdapter implements IParseListener {

	private Context mContext;
	private List<Chat> mList = new ArrayList<Chat>();
	private boolean isImageRefreshNo = false;
	private LayoutInflater mInflater;
	ChatListingFragment mChatListingFragment;
	protected AQuery aq;
	private ChatDBHelper chatDBHelper;
	private SharedPreferences userDetails;
	private HBXMPP hbxmpp;

	private static final int CODE_GETDETAILS = 700;

	public ChatListAdapter(Context context,
			ChatListingFragment chatListingFragment, HBXMPP hbxmpp) {
		this.mContext = context;
		this.mChatListingFragment = chatListingFragment;
		aq = new AQuery(mContext);
		chatDBHelper = new ChatDBHelper();
		userDetails = PreferenceManager.getDefaultSharedPreferences(mContext);
		this.hbxmpp = ((BaseApplication) chatListingFragment.getActivity().getApplication()).getHbxmpp();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Chat getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class Holder {

		private ImageView frndImage, frndOnlineStatus;
		private TextView frndName, newMessage, messageTime,
		mNotificatoinNumber;
		private int positionholder;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		final Holder holder;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			holder = new Holder();
			// Initializing the Adapter items
			convertView = mInflater.inflate(R.layout.chatlist_cell, null);

			holder.frndImage = (ImageView) convertView
					.findViewById(R.id.user_image);
			holder.frndOnlineStatus = (ImageView) convertView
					.findViewById(R.id.online_status);
			holder.frndName = (TextView) convertView
					.findViewById(R.id.user_name);
			holder.newMessage = (TextView) convertView
					.findViewById(R.id.user_lastmessage);
			holder.messageTime = (TextView) convertView
					.findViewById(R.id.lastmessagetime);
			holder.mNotificatoinNumber = (TextView) convertView
					.findViewById(R.id.chatmessages_number);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		try {
			holder.frndName.setText(mChatListingFragment.profileDisplayNames.get(mList.get(position).getFriendId().replace("user_","")));
		} catch (Exception e1) {
			holder.frndName.setText(mList.get(position).getFriendName());
			e1.printStackTrace();
		}
		String mMessage = mList.get(position).getNewMessage();

		String[] mFinalMsg;

		if (mMessage.contains("##@!@##")) {

			mFinalMsg = mMessage.split("##@!@##");
			mMessage = mFinalMsg[0];
		}

		holder.newMessage.setText(mMessage);

		String messagetime = mList.get(position).getMessageTime();

		Log.e("CHatListAdapter", "MessageTime - "+messagetime);

		if (!TextUtils.isEmpty(messagetime)) {
			if (messagetime.trim().equalsIgnoreCase("just now")) {
				messagetime = "Just now";
			} else if (messagetime.contains("m")
					&& messagetime.trim().indexOf("m") <= 2) {
				messagetime = messagetime.trim().replace("m", " m");
			}
			holder.messageTime.setText(messagetime);
		} else {
			holder.messageTime.setText(mList.get(position).getMessageTime());
		}

		final ImageOptions options = new ImageOptions();

		options.round = 1200;

		try {
			
			String url = mChatListingFragment.profileURLs.get(mList.get(position).getFriendId().replace("user_",""));
			
			if (TextUtils.isEmpty(url)
					||url.contains("noimage")) {
				aq.id(holder.frndImage).image(R.drawable.com_images_);
			} else {
				aq.id(holder.frndImage)
				.progress(R.id.progress)
				.image(url, true, true, 185, 0,
						new BitmapAjaxCallback() {
					@Override
					protected void callback(String url, ImageView iv, Bitmap bm,AjaxStatus status) {
						iv.setImageBitmap(makeMaskImage(holder.frndImage,
								bm));
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*loadProfileBitmap(mList.get(position).getFriendId()
				+ Webservices.CHAT_DOMAIN, position, holder);*/

		hbxmpp = ((BaseApplication) mChatListingFragment.getActivity().getApplication()).getHbxmpp();
		if (mChatListingFragment.getAvailability(
				mList.get(position).getFriendId() + Webservices.CHAT_DOMAIN)
				.equalsIgnoreCase("Online")) {
			holder.frndOnlineStatus.setImageResource(R.drawable.online);
		} else {
			holder.frndOnlineStatus.setImageResource(R.drawable.offline);
		}

		// set
		int mUnreadMsgCount = chatDBHelper.getUnreadMessageCount(mContext,
				mList.get(position).getFriendId(),
				userDetails.getString("Loggedin_jabber_id", ""));
		if (mUnreadMsgCount != 0) {
			holder.mNotificatoinNumber.setVisibility(View.VISIBLE);
			holder.mNotificatoinNumber.setText(mUnreadMsgCount + "");

		} else {
			holder.mNotificatoinNumber.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	private void loadProfileBitmap(String string, int position, Holder holder) {
		// TODO Auto-generated method stub
		GetProfileBitMap mAsyncGetBitMap = new GetProfileBitMap(string,
				position, holder);
		mAsyncGetBitMap.execute();
	}

	public class GetProfileBitMap extends AsyncTask<Void, Void, Bitmap> {
		private String user;
		private int mposition;
		private Holder mHolder;

		public GetProfileBitMap(String userid, int position, Holder holder) {
			// TODO Auto-generated constructor stub
			user = userid;
			mposition = position;
			mHolder = holder;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			// TODO Auto-generated method stub
			SmackConfiguration.setPacketReplyTimeout(30000);

			Bitmap FrndimgBitmap;
			byte[] imgsarray;
			final VCard vCard = new VCard();
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
					new VCardProvider());
			try {
				vCard.load(hbxmpp.getConnection(), user);
			} catch (Exception e) {
				e.printStackTrace();
			}

			imgsarray = vCard.getAvatar();
			if (imgsarray != null) {
				int len = imgsarray.length;
				FrndimgBitmap = BitmapFactory
						.decodeByteArray(imgsarray, 0, len);
			} else {
				FrndimgBitmap = null;
			}


			return FrndimgBitmap;

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);


			if (result != null) {
				Bitmap.createScaledBitmap(
						result, 185, 185, false);
				result = makeMaskImage(mHolder.frndImage, result);
				aq.id(mHolder.frndImage).progress(R.id.progress).image(result);
			} else {
				mHolder.frndImage
				.setBackgroundResource(R.drawable.com_images_);
			}
		}

	}

	public Bitmap makeMaskImage(ImageView mImageView, Bitmap original) {

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

	}

	public void removeall() {
		// TODO Auto-generated method stub

		if (this.mList != null) {
			this.mList.clear();
		}
		notifyDataSetChanged();
	}

	public void addItem(Chat data, boolean status) {
		// TODO Auto-generated method stub

		if (this.mList != null) {
			this.mList.add(data);
			isImageRefreshNo = status;
		}
		notifyDataSetChanged();
	}

	public void setRecentMessage(String mMessage, String mFriendName) {

		for (int j = 0; j < getCount(); j++) {

			if (mList.get(j).getFriendName().equalsIgnoreCase(mFriendName)) {
				mList.get(j).setNewMessage(mMessage);
				notifyDataSetChanged();
			}

		}

	}

	public void removItemat(Chat muserData) {
		mList.remove(muserData);
		notifyDataSetChanged();
	}

	String friendURL = null;

	public void getUsersImage(String friendId) {

		Bundle mBundle = new Bundle();
		mBundle.putString("jabber_id", friendId);
		mBundle.putString("user_latitude",
				userDetails.getString("UserLatitude", ""));
		mBundle.putString("user_longitude",
				userDetails.getString("UserLongitude", ""));
		mBundle.putString("logged_userid",
				userDetails.getString("Loggedin_userid", ""));
		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(Webservices.encodeUrl(
				Webservices.GET_DETAILFROMJABBERID, mBundle), CODE_GETDETAILS,
				this);

		// if(TextUtils.isEmpty(friendURL)){
		//
		// }else{
		// return friendURL;
		// }

		// TODO Auto-generated method stub

	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case CODE_GETDETAILS:
			try {
				JSONObject mJsonObject = new JSONObject(
						response.getString("settings"));
				if (mJsonObject.getString("success").equalsIgnoreCase("1")) {

					JSONArray mUserDetails = response.getJSONArray("data");
					JSONObject mUserdata = mUserDetails.getJSONObject(0);

					friendURL = mUserdata.getString("profile_image");

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}

	}

}
