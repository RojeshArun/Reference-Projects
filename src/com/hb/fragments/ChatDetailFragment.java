package com.hb.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import khandroid.ext.apache.http.HttpEntity;
import khandroid.ext.apache.http.HttpResponse;
import khandroid.ext.apache.http.client.HttpClient;
import khandroid.ext.apache.http.client.methods.HttpPost;
import khandroid.ext.apache.http.impl.client.DefaultHttpClient;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.Utilites.MapJsonParser;
import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.barter.xmpp.ChatDBHelper;
import com.barter.xmpp.ChatData;
import com.barter.xmpp.HBMessage;
import com.barter.xmpp.HBXMPP;
import com.barter.xmpp.ServiceUtility;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hb.adapters.ChatDetailsAdapter;
import com.hb.barter.LoadingScreen;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.baseapplication.BaseApplication;
import com.hb.models.Chat;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

@SuppressLint("NewApi")
public class ChatDetailFragment extends Fragment implements OnClickListener,
		OnCheckedChangeListener, IParseListener, OnMapClickListener,
		OnMapLongClickListener, OnMarkerDragListener,
		OnLocationChangedListener, LocationListener, OnRatingBarChangeListener {

	private View mRootView;
	String addressString;
	Boolean isMapOpen = false;
	// TopBar Variables
	private ImageView mBackBtn, mRightBtn, mCenterImageView;
	private TextView mHeaderTitle, mTxtUserName, mTxtPreference, mTxtSend,
			mTxtMapHint;
	private TextView mBottomLine;
	private CheckBox mBlockUser;
	private RatingBar mRateUser, mUserRating;
	private MainActivity mMainAcitivity;
	private ListView mchatListView;
	MarkerOptions markerOption;
	ArrayList<Marker> markerPoint;
	private EditText chatEditMessage;
	private HBXMPP hbxmpp;
	private ChatDBHelper chatDBHelper;
	private ContentResolver resolver;
	private List<ChatData> chatHistory = new ArrayList<ChatData>();
	private List<HBMessage> Allmessages = new ArrayList<HBMessage>();
	private SharedPreferences userDetails;
	private ChatDetailsAdapter mChatDetailAdapter;
	private String frienduserName, friendprofileimageurl;
	LocationManager locationManager;
	private static final int CODE_GETDETAILS = 700;
	private LinearLayout mUserMaps, mUserListing;
	private static final int BLOCK_USER = 701, POST_RATING = 709;
	private static final int CHAT_NOTIFICATION = 702, SHARE_LOCATION = 809;
	private GoogleMap mMap;
	 private double userCurrentLatitude = -1, userCurrentLongitude = -1;
	private double userFriendLatitude = -1, userFriendLongitude = -1;
	private double midPointLatitude = -1, midPointLongitude = -1;
	private double lineMarkerLatitude = -1, lineMarkerLongitude = -1;
	private boolean isFromListItem = false;
	private Chat muserData;
	private ChatListingFragment mChatListingFragment;
	private String mFrinedImageURL;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMainAcitivity = (MainActivity) getActivity();
	}

	public ChatDetailFragment(ChatListingFragment mChatListingFragment,
			Chat muserData) {
		this.mChatListingFragment = mChatListingFragment;
		this.muserData = muserData;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_chat_barter_new, null);

		showProgress();
		mBottomLine = (TextView) mRootView
				.findViewById(R.id.txtView_LineTopBar);
		mBottomLine.setVisibility(View.INVISIBLE);
		mTxtMapHint = (TextView) mRootView.findViewById(R.id.txtMapHint);
		mCenterImageView = (ImageView) mRootView
				.findViewById(R.id.centerImageView);
		mCenterImageView.setVisibility(View.VISIBLE);
		mCenterImageView.setSelected(true);

		hbxmpp = ((BaseApplication) getActivity().getApplication()).getHbxmpp();

		chatDBHelper = new ChatDBHelper();
		resolver = getActivity().getContentResolver();

		userDetails = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		if (getArguments().getString("jabber_id") == null) {
		} else {
			frienduserName = getArguments().getString("jabber_id")
					+ Webservices.CHAT_DOMAIN;
			friendprofileimageurl = getArguments().getString("friend_image");
			callGetUserDetailsWS(getArguments());
		}

		setAsReadMessage();

		return mRootView;
	}

	private void setAsReadMessage() {

		// Get this users message count and remove form the total number;

		int mUnreadMsgCount = chatDBHelper.getUnreadMessageCount(getActivity(),
				getArguments().getString("jabber_id"),
				userDetails.getString("Loggedin_jabber_id", ""));

		// Update the count

		if (!TextUtils.isEmpty(mMainAcitivity.mNotificationtxtview.getText())) {
			int mTotalCount = Integer
					.parseInt((String) mMainAcitivity.mNotificationtxtview
							.getText());

			int finalcount = mTotalCount - mUnreadMsgCount;

			if (finalcount != 0) {
				mMainAcitivity.mNotificationtxtview.setText(finalcount + "");
			} else {
				mMainAcitivity.mNotificationtxtview
						.setVisibility(View.INVISIBLE);

			}

		} else {
			mMainAcitivity.mNotificationtxtview.setVisibility(View.INVISIBLE);
		}

		chatDBHelper.setAsReadMessages(getActivity(),
				getArguments().getString("jabber_id"),
				userDetails.getString("Loggedin_jabber_id", ""));

	}

	public void setMapLayouts() {

	}

	public void hideMaps() {

		lineMarkerLatitude = -1;
		lineMarkerLongitude = -1;
		mUserMaps.setVisibility(View.GONE);
		mUserListing.setVisibility(View.VISIBLE);
		mCenterImageView.setVisibility(View.VISIBLE);
		mHeaderTitle.setVisibility(View.GONE);
		mRightBtn.setVisibility(View.VISIBLE);
		mRightBtn.setImageResource(R.drawable.map_topbar);

		if (mMap != null) {
			mMap.clear();
		}
	}

	public void hideListing() {
		mUserMaps.setVisibility(View.VISIBLE);
		mUserListing.setVisibility(View.GONE);
	}

	private void callGetUserDetailsWS(Bundle arguments) {
		showProgress();

		Bundle mBundle = new Bundle();
		mBundle.putString("jabber_id", arguments.getString("jabber_id"));
		mBundle.putString("user_latitude", arguments.getString("user_latitude"));
		mBundle.putString("user_longitude",
				arguments.getString("user_longitude"));
		mBundle.putString("logged_userid", arguments.getString("user_id"));
		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(Webservices.encodeUrl(
				Webservices.GET_DETAILFROMJABBERID, mBundle), CODE_GETDETAILS,
				this);
	}

	@Override
	public void onResume() {
		super.onResume();

		SharedPreferences.Editor editor1 = userDetails.edit();
		editor1.putString("chat", "yes");
		editor1.putString("chatDetailName",
				getArguments().getString("jabber_id") + Webservices.CHAT_DOMAIN);
		editor1.putString("MainClass", "Yes");
		editor1.commit();

		getActivity().registerReceiver(serviceconnection,
				new IntentFilter(ServiceUtility.MESSAGE_UPDATE_ACTION));

		try {
			chatHistory = chatDBHelper.loadConversationHistory(getActivity(),
					getArguments().getString("jabber_id"),
					userDetails.getString("Loggedin_jabber_id", ""));
			mchatListView.setAdapter(mChatDetailAdapter);

			Allmessages.clear();
			mChatDetailAdapter.removeall();

			for (int i = 0; i < chatHistory.size(); i++) {
				HBMessage mHbMessage = new HBMessage();
				mHbMessage.setFromJID(chatHistory.get(i).getFromId());
				mHbMessage.setToJID(chatHistory.get(i).getToId());
				mHbMessage.setMessage(chatHistory.get(i).getOrginalMessage());
				mHbMessage.setMessageTime(chatHistory.get(i)
						.getChatMessageDatetime());
				mHbMessage.setFromName(chatHistory.get(i).getChatFriendName());
				mHbMessage.setShowTimeLayout("No");
				mHbMessage.setMediaStatus(chatHistory.get(i).getMediaStatus());
				mHbMessage.setAvgRating(chatHistory.get(i).getAvgRating());
				mChatDetailAdapter.addItem(mHbMessage);
				Allmessages.add(mHbMessage);
				mchatListView.setSelection(mChatDetailAdapter.getCount() - 1);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(serviceconnection);
		SharedPreferences.Editor editor = userDetails.edit();
		editor.putString("chat", "no");
		editor.putString("chatDetailName", "");
		editor.commit();
	}

	private Handler mhHandler = new Handler();

	private BroadcastReceiver serviceconnection = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, final Intent intent) {
			// setListAdapter(getListArray());
			mhHandler.post(new Runnable() {

				@Override
				public void run() {
					if (userDetails.getString("refreshview", "")
							.equalsIgnoreCase("yes")) {

						Bundle extrasBundle = new Bundle();
						extrasBundle = intent.getExtras();
						ChatData messageData = (ChatData) extrasBundle
								.get("MessageData");

						HBMessage hbMessage = new HBMessage();
						hbMessage.setFromJID(messageData.getFromId());
						hbMessage.setToJID(messageData.getToId());
						hbMessage.setMessage(messageData.getOrginalMessage());
						hbMessage.setMessageTime(messageData
								.getChatMessageDatetime());
						hbMessage.setFromName(messageData.getChatFriendName());
						hbMessage.setMediaStatus(messageData.getMediaStatus());
						hbMessage.setShowTimeLayout("No");
						if (mChatDetailAdapter != null) {
							mChatDetailAdapter.addItem(hbMessage);
							Allmessages.add(hbMessage);
							mchatListView.smoothScrollToPosition(Allmessages
									.size() - 1);
						}
					}
				}
			});
		}
	};

	private void setOnClickListeners() {
		mBlockUser.setOnClickListener(this);
		// mBlockUser.setOnCheckedChangeListener(this);
		mRightBtn.setOnClickListener(this);
		mCenterImageView.setOnClickListener(this);
	}

	private void setChatDetailsFragment(JSONObject mJsonData) {
		mBlockUser = (CheckBox) mRootView
				.findViewById(R.id.checkBoxBlockUnblock);
		mBackBtn = (ImageView) mRootView.findViewById(R.id.slidedown);
		mBackBtn.setOnClickListener(this);
		mRightBtn = (ImageView) mRootView.findViewById(R.id.rightMostButton);
		mRightBtn.setVisibility(View.VISIBLE);
		mRightBtn.setImageResource(R.drawable.map_topbar);
		// mRightFirstBtn = (ImageView) mRootView
		// .findViewById(R.id.rightButOneButton);
		// mRightFirstBtn.setVisibility(View.VISIBLE);
		// mRightFirstBtn.setImageResource(R.drawable.map_topbar);
		// mRightFirstBtn.setOnClickListener(this);

		mHeaderTitle = (TextView) mRootView.findViewById(R.id.title);
		// mHeaderTitle.setText(R.string.chat);
		mHeaderTitle.setVisibility(View.GONE);
		mRateUser = (RatingBar) mRootView.findViewById(R.id.fivestar_rating);
		mUserRating = (RatingBar) mRootView
				.findViewById(R.id.fivestar_rating_given);
		mUserRating.setOnRatingBarChangeListener(this);

		mRateUser.setEnabled(false);

		try {
			mUserRating.setRating(Float.parseFloat(mJsonData
					.getString("rating_given_by_login_user")));
			mRateUser.setRating(Float.parseFloat(mJsonData
					.getString("avg_rating_result")));

		} catch (NumberFormatException e2) {
			e2.printStackTrace();
		} catch (JSONException e2) {
			e2.printStackTrace();
		}

		mTxtUserName = (TextView) mRootView
				.findViewById(R.id.txtView_NameChatBarter);
		mTxtPreference = (TextView) mRootView
				.findViewById(R.id.txtView_PreferencePerson);
		mTxtSend = (TextView) mRootView.findViewById(R.id.txtView_ChatSend);
		mTxtSend.setOnClickListener(this);
		mUserListing = (LinearLayout) mRootView.findViewById(R.id.user_listing);
		mUserListing.setVisibility(View.VISIBLE);
		mUserMaps = (LinearLayout) mRootView.findViewById(R.id.users_maps);
		// mBottomLine = (TextView) mRootView
		// .findViewById(R.id.txtView_LineTopBar);
		// mBottomLine.setVisibility(View.GONE);
		chatEditMessage = (EditText) mRootView
				.findViewById(R.id.edtText_ChatMessages);
		mchatListView = (ListView) mRootView.findViewById(R.id.chatdetail_list);

		try {
			if (mJsonData != null && mJsonData.has("latitude")
					&& mJsonData.has("longitude")) {
				userFriendLatitude = Double.parseDouble(mJsonData
						.getString("latitude"));
				userFriendLongitude = Double.parseDouble(mJsonData
						.getString("longitude"));
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		mchatListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String mMessage = Allmessages.get(position).getMessage();

				String regularExpression = getResources().getString(
						R.string._location_reg_expression);
				if (!TextUtils.isEmpty(mMessage)
						&& mMessage.contains(getResources().getString(
								R.string._location_reg_expression))) {

					String[] mLatLongdetails = mMessage
							.split(regularExpression);

					double latitude = Double.parseDouble(mLatLongdetails[1]);
					double longitude = Double.parseDouble(mLatLongdetails[2]);

					// double latitude = Double.parseDouble(mMessage.trim()
					// .substring(mMessage.trim().lastIndexOf(",") + 1,
					// mMessage.trim().length()));
					// double longitude = Double.parseDouble(mMessage.trim()
					// .substring(mMessage.trim().lastIndexOf("#") + 1,
					// mMessage.trim().lastIndexOf(",")));

					lineMarkerLatitude = latitude;
					lineMarkerLongitude = longitude;

					if (isMapOpen == false) {
						isFromListItem = true;
						gotoMapsScreen();
						hideListing();
						mTxtMapHint.setVisibility(View.INVISIBLE);
						isMapOpen = true;
						// mCenterImageView.setVisibility(View.GONE);
						// mHeaderTitle.setVisibility(View.VISIBLE);
						mHeaderTitle.setText("Route");
						// LinearLayout.LayoutParams paramsExample = new
						// LinearLayout.LayoutParams(
						// LayoutParams.WRAP_CONTENT,
						// LayoutParams.WRAP_CONTENT);
						// mHeaderTitle.setLayoutParams(paramsExample);
						mRightBtn.setVisibility(View.INVISIBLE);
					}
				}
			}
		});

		try {
			mTxtUserName.setText(mJsonData.getString("display_name"));
			mTxtPreference.setText(userDetails.getString("i_want_currency",
					"USD"));

			if (!TextUtils.isEmpty(mJsonData.getString("avg_rating_result"))) {
				mRateUser.setRating(Float.parseFloat(mJsonData
						.getString("avg_rating_result")));
			}

			mFrinedImageURL = mJsonData.getString("profile_image");

			// Change User url and mFrined url to resized bitmap

			mChatDetailAdapter = new ChatDetailsAdapter(getActivity(),
					getArguments().getString("jabber_id"),
					userDetails.getString("profile_image", ""),
					mJsonData.getString("profile_image"),
					ChatDetailFragment.this);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		chatHistory = chatDBHelper.loadConversationHistory(getActivity(),
				getArguments().getString("jabber_id"),
				userDetails.getString("Loggedin_jabber_id", ""));
		mchatListView.setAdapter(mChatDetailAdapter);

		Allmessages.clear();
		mChatDetailAdapter.removeall();

		for (int i = 0; i < chatHistory.size(); i++) {
			HBMessage mHbMessage = new HBMessage();
			mHbMessage.setFromJID(chatHistory.get(i).getFromId());
			mHbMessage.setToJID(chatHistory.get(i).getToId());
			mHbMessage.setMessage(chatHistory.get(i).getOrginalMessage());
			mHbMessage.setMessageTime(chatHistory.get(i)
					.getChatMessageDatetime());
			mHbMessage.setFromName(chatHistory.get(i).getChatFriendName());

			// // Add code for date comparison
			// if(i==0){
			//
			// // if(chatHistory.get(i).getChatMessageDatetime())
			//
			// String mFulldate=chatHistory.get(i).getChatMessageDatetime();
			//
			// mHbMessage.setShowTimeLayout("");
			//
			// mHbMessage.setShowTimeLayout("Yes");
			//
			// }

			mHbMessage.setMediaStatus(chatHistory.get(i).getMediaStatus());
			mHbMessage.setAvgRating(chatHistory.get(i).getAvgRating());
			mChatDetailAdapter.addItem(mHbMessage);
			Allmessages.add(mHbMessage);
			mchatListView.setSelection(mChatDetailAdapter.getCount() - 1);
			// TODO Set rating
			// if (!TextUtils.isEmpty(chatHistory.get(i).getAvgRating())) {
			// mRateUser.setRating(Float.parseFloat(chatHistory.get(i)
			// .getAvgRating()));
			// }

		}

		chatEditMessage.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				getActivity().getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				if (mchatListView.getLastVisiblePosition() == mchatListView
						.getCount() - 1) {
					mchatListView.postDelayed(new Runnable() {
						@Override
						public void run() {
							mchatListView.setSelection(mchatListView.getCount());
						}
					}, 700);
				}

				return false;
			}
		});

		chatEditMessage.setOnEditorActionListener(mListener);
		hideProgress();

	}

	private OnEditorActionListener mListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				getActivity()
						.getWindow()
						.setSoftInputMode(
								WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				v.setOnFocusChangeListener(null);
				v.clearFocus();
				// v.setOnFocusChangeListener(mOnFocusChangeListener);
			}
			return false;
		}
	};

	private void resetMap() {

		// getActivity().finish();
		// Intent mIntent = new Intent(mMainAcitivity, MainActivity.class);
		// mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// mIntent.putExtra("fromSignUP1", true);
		// startActivity(mIntent);
		hideMaps();
		mHeaderTitle.setText(R.string.chat);
		mRightBtn.setImageResource(R.drawable.icn_setting_topbar);
		// mRightFirstBtn.setVisibility(View.VISIBLE);
		isMapOpen = false;
	}

	public void setValueChatString(List<Marker> markerslist) {

		String mregexp = getResources().getString(
				R.string._location_reg_expression);
		if (markerslist != null && markerslist.size() > 0) {
			for (int i = 0; i < markerslist.size(); i++) {
				LatLng mlatlng = markerslist.get(i).getPosition();

				String mMainAddress;
				if (!TextUtils.isEmpty(markerslist.get(i).getSnippet())) {
					String[] splidAddress = markerslist.get(i).getSnippet()
							.split(",");
					mMainAddress = splidAddress[0];

					String sendText = "I am suggesting this location.\n"
							+ mregexp + mlatlng.latitude + mregexp
							+ mlatlng.longitude + mregexp + mMainAddress
							+ mregexp + markerslist.get(i).getSnippet()
							+ mregexp;

					chatEditMessage.setText(sendText);
					mTxtSend.callOnClick();
				}
			}
		}
	}

	// public void setValueChatString(double regLat, double regLong) {
	// String sendText = "I am suggesting this location.\n" + addressString
	// + getActivity().getString(R.string._location_reg_expression) + regLat +
	// "," + regLong;
	//
	// chatEditMessage.setText(sendText);
	// mTxtSend.callOnClick();
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.slidedown:
			slideBack();
			break;
		case R.id.centerImageView:
			gotoSettingScreen();
			break;
		case R.id.rightMostButton:
			if (isMapOpen == false) {
				((ImageView) mRootView.findViewById(R.id.rightMostButton))
						.setImageDrawable(getResources().getDrawable(
								R.drawable.ok));
				isFromListItem = false;
				gotoMapsScreen();
				hideListing();
				isMapOpen = true;
			} else {
				if (markerPoint != null && markerPoint.size() > 0) {
					goOutFromMap();
					if (markerPoint != null && markerPoint.size() > 0) {
						markerPoint.clear();
					}
				} else {
					showTwoBtnDialog(getActivity(), "OK", "NO",
							"Are you sure,  want to exit without picking meeting location.");
				}
			}
			break;
		case R.id.txtView_ChatSend:
			if (!TextUtils.isEmpty(chatEditMessage.getText().toString().trim())) {
				if (Validations.isNetworkAvailable(getActivity())) {
					hbxmpp = ((BaseApplication) getActivity().getApplication())
							.getHbxmpp();
					try {
						if (hbxmpp != null
								&& hbxmpp.getConnection().isConnected()
								&& hbxmpp.getConnection().isAuthenticated()) {
							String availability = getAvailability(getArguments()
									.getString("jabber_id")
									+ Webservices.CHAT_DOMAIN);
							if (availability.equalsIgnoreCase("Offline")) {
								callwebserviceForChatNotification();
								offlineMessages();
								sendMessage();
							} else {
								sendMessage();
							}
							Validations.hideKeyboard(getActivity());
						} else {
							offlineMessages();
						}
					} catch (Exception e) {
						e.printStackTrace();
						offlineMessages();
					}
				} else {
					offlineMessages();
				}
			}
			break;
		case R.id.checkBoxBlockUnblock:
			if (((CheckBox) mBlockUser).isChecked()) {
				if (Validations.isNetworkAvailable(getActivity())) {
					callBlockUserWS();
				} else {
					Validations
							.showSingleBtnDialog(
									getActivity()
											.getString(
													R.string.no_internet_connection_please_try_again),
									getActivity());
				}
			}
			break;
		default:
			break;
		}
	}

	private void goOutFromMap() {
		((ImageView) mRootView.findViewById(R.id.rightMostButton))
				.setImageDrawable(getResources().getDrawable(
						R.drawable.map_topbar));
		setValueChatString(markerPoint);
		callwebserviceForSharingLocation();
		hideMaps();
		isMapOpen = false;
	}

	private void showTwoBtnDialog(final Activity activity,
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
		btnCancel = (TextView) alertDialog.findViewById(R.id.btnCancel);
		btnCancel.setText(negativeBtnTxt);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		btnOK = (TextView) alertDialog.findViewById(R.id.btnOK);
		btnOK.setText(positiveBtnTxt);
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				goOutFromMap();
			}
		});

		btnCancel.setVisibility(View.VISIBLE);

		alertDialog.show();
	}

	private void callwebserviceForChatNotification() {
		try {
			Bundle params = new Bundle();
			params.putString("sender_id",
					userDetails.getString("Loggedin_userid", ""));
			params.putString("receiver_id",
					getArguments().getString("jabber_id"));

			JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
			mJsonRequestResponse.getResponse(Webservices.encodeUrl(
					Webservices.CHAT_NOTIFICATION, params), CHAT_NOTIFICATION,
					this);
			Log.i("ChatDetailsFragment ChatNotification Web Service",
					Webservices
							.encodeUrl(Webservices.CHAT_NOTIFICATION, params));
		} catch (Exception e) {
			e.printStackTrace();
			offlineMessages();
		}
	}

	private void callwebserviceForSharingLocation() {
		try {

			if (markerPoint != null && markerPoint.size() > 0) {
				for (int i = 0; i < markerPoint.size(); i++) {
					Bundle params = new Bundle();
					params.putString("sender_id",
							userDetails.getString("Loggedin_userid", ""));
					params.putString("receiver_id",
							getArguments().getString("jabber_id"));
					params.putString("location_name",
							(markerPoint.get(i).getSnippet()));
					params.putString("latitude", String.valueOf(markerPoint
							.get(i).getPosition().latitude));
					params.putString("longitude", String.valueOf(markerPoint
							.get(i).getPosition().longitude));
					params.putString("share_location_id", 2 + "");

					JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
					mJsonRequestResponse.getResponse(Webservices.encodeUrl(
							Webservices.SHARE_LOCATION, params),
							SHARE_LOCATION, this);
					Log.i("CALLWEBSERVICEFORSHARINGLOCATION", Webservices
							.encodeUrl(Webservices.SHARE_LOCATION, params));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			offlineMessages();
		}
	}

	public String getAvailability(String fromId) {
		String userState = "Offline";
		Roster roster = null;
		try {
			Roster.setDefaultSubscriptionMode(SubscriptionMode.accept_all);
			roster = hbxmpp.getConnection().getRoster();
			roster.setSubscriptionMode(SubscriptionMode.accept_all);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Presence availability = null;
		try {
			availability = roster.getPresence(fromId);
			if (availability.isAvailable()) {
				userState = "Online";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return userState;
		}
		return userState;
	}

	private void callBlockUserWS() {
		showProgress();
		Bundle mBundle = new Bundle();
		mBundle.putString("user_id",
				muserData.getFriendId().replace("user_", ""));
		mBundle.putString("action", "block");
		mBundle.putString("action_by",
				userDetails.getString("Loggedin_userid", ""));
		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.BLOCK_UNBLOCK_USER, mBundle),
				BLOCK_USER, this);
	}

	private void offlineMessages() {
		Calendar cal = Calendar.getInstance();
		Date currentLocalTime = cal.getTime();
		DateFormat date = new SimpleDateFormat("dd MMMM yyyy hh:mm a",
				Locale.ENGLISH);
		final String localTime = date.format(currentLocalTime);
		final String messageString = chatEditMessage.getText().toString()
				.trim();
		if (!TextUtils.isEmpty(messageString)) {
			try {
				HBMessage hbMessage = new HBMessage();
				hbMessage.setMessage(messageString);
				hbMessage.setFromJID(userDetails.getString(
						"Loggedin_jabber_id", "") + Webservices.CHAT_DOMAIN);
				hbMessage.setMessageTime(localTime);
				hbMessage.setShowTimeLayout("No");
				hbMessage.setMediaStatus("uploading");
				Allmessages.add(hbMessage);
				mChatDetailAdapter.addItem(hbMessage);
				mChatDetailAdapter.notifyDataSetChanged();
				mchatListView.smoothScrollToPosition(Allmessages.size() - 1);
				AddInDB(localTime, "offline");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		chatEditMessage.setText("");
	}

	private void sendMessage() {
		Calendar cal = Calendar.getInstance();
		Date currentLocalTime = cal.getTime();
		DateFormat date = new SimpleDateFormat("dd MMMM yyyy hh:mm a",
				Locale.ENGLISH);
		final String localTime = date.format(currentLocalTime);
		String messageString = chatEditMessage.getText().toString().trim();
		String mLat, mLong;
		if (!TextUtils.isEmpty(messageString)) {
			try {
				HBMessage hbMessage = new HBMessage();

				hbMessage.setMessage(messageString);
				hbMessage.setFromJID(userDetails.getString(
						"Loggedin_jabber_id", "") + Webservices.CHAT_DOMAIN);
				hbMessage.setMessageTime(localTime);
				hbMessage.setShowTimeLayout("No");
				hbMessage.setMediaStatus("uploading");
				Allmessages.add(hbMessage);
				mChatDetailAdapter.addItem(hbMessage);
				mChatDetailAdapter.notifyDataSetChanged();
				mchatListView.smoothScrollToPosition(Allmessages.size() - 1);
				AddInDB(localTime, "online");
				Message msg = new Message(getArguments().getString("jabber_id")
						+ Webservices.CHAT_DOMAIN, Message.Type.chat);
				msg.setBody(chatEditMessage.getText().toString().trim());
				hbxmpp.getConnection().sendPacket(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		chatEditMessage.setText("");
	}

	private void AddInDB(String localTime, String msgStatus) {
		Roster roster = hbxmpp.getConnection().getRoster();
		String frndName = roster
				.getEntry(
						getArguments().getString("jabber_id")
								+ Webservices.CHAT_DOMAIN).getName();
		ChatData chatInfoUpdated = new ChatData();
		chatInfoUpdated.setFromId(userDetails.getString("Loggedin_jabber_id",
				"") + Webservices.CHAT_DOMAIN);
		chatInfoUpdated.setToId(getArguments().getString("jabber_id")
				+ Webservices.CHAT_DOMAIN);
		chatInfoUpdated.setOrginalMessage(chatEditMessage.getText().toString()
				.trim());
		chatInfoUpdated.setUserId(userDetails.getString("Loggedin_jabber_id",
				""));
		chatInfoUpdated.setFriendId(getArguments().getString("jabber_id"));
		chatInfoUpdated.setChatMessageDatetime(localTime);
		// chatInfoUpdated.setChatFriendName(frienduserName);
		chatInfoUpdated.setChatFriendName(frndName);
		chatInfoUpdated.setUnreadMessageCount(0);
		chatInfoUpdated.setFromProfileImageUrl(friendprofileimageurl);
		chatInfoUpdated.setMediaStatus("uploading");
		chatInfoUpdated.setMediaShortName("Text");
		chatInfoUpdated.setMessageStatus(msgStatus);
		chatDBHelper.AddChatMessage(resolver, chatInfoUpdated);
	}

	private void gotoSettingScreen() {
		onDestroyView();
		slideBack();
		mMainAcitivity.isFromChatDetails = true;
		mMainAcitivity.mTab1.callOnClick();

	}

	private void slideBack() {
		//
		// mChatListingFragment.mListAdapter.notifyDataSetChanged();
		// // mChatListingFragment.refreshchat();
		// getFragmentManager().popBackStack();
		//

		Validations.hideKeyboard(getActivity());

		if (isMapOpen) {
			goOutFromMap();
		} else {
			mChatListingFragment.refreshchat();
			// mChatListingFragment.mListAdapter.notifyDataSetChanged();
			getFragmentManager().popBackStack();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// if (buttonView.isChecked()) {
		// Validations.showAlerDialog("User Blocked", getActivity());
		// }
	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		hideProgress();
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {
		switch (requestCode) {
		case CODE_GETDETAILS:
			try {
				JSONObject mJsonObject = new JSONObject(
						response.getString("settings"));
				if (mJsonObject.getString("success").equalsIgnoreCase("1")) {
					JSONArray mArray = new JSONArray(response.getString("data"));
					JSONObject mJsonData = mArray.getJSONObject(0);
					setChatDetailsFragment(mJsonData);
					setOnClickListeners();
					mMainAcitivity.hideProgress();
				} else {
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case BLOCK_USER:
			try {
				JSONObject mSettingBlock = new JSONObject(
						response.getString("settings"));
				mMainAcitivity.hideProgress();
				if (mSettingBlock.getString("success").equalsIgnoreCase("1")) {

					mChatListingFragment.testForNoRecords();
					showSingleBtnDialog(
							getArguments().getString("username")
									+ " is now blocked, will be removed from chat list\n",
							getActivity());
					// + mSettingBlock.getString("message")
				} else {
					Validations.showSingleBtnDialog(
							mSettingBlock.getString("message"), getActivity());
					// Validations.showAlerDialog(
					// mSettingBlock.getString("message"), getActivity());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;
		case POST_RATING:
			try {
				JSONObject mSettingBlock = new JSONObject(
						response.getString("settings"));
				mMainAcitivity.hideProgress();
				hideProgress();
				if (mSettingBlock.getString("success").equalsIgnoreCase("1")) {
					if (mSettingBlock.getString("message").equalsIgnoreCase(
							"Update_User_Rating.")) {
						Validations.showSingleBtnDialog(
								"Rating successfully updated", getActivity());
						// Validations.showAlerDialog("Rating successfully updated",
						// getActivity());
					} else {
						Validations.showSingleBtnDialog(
								"Rating successfully posted", getActivity());
						// Validations.showAlerDialog("Rating successfully posted",
						// getActivity());
					}
				} else {
					Validations.showSingleBtnDialog(
							mSettingBlock.getString("message"), getActivity());
					// Validations.showAlerDialog(
					// mSettingBlock.getString("message"), getActivity());
					mUserRating.setOnRatingBarChangeListener(null);
					mUserRating.setRating(0);
					mUserRating.setOnRatingBarChangeListener(this);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;
		case CHAT_NOTIFICATION:
			try {
				JSONObject mSettingObject = new JSONObject(
						response.getString("settings"));
				if (mSettingObject.getString("success").equals("-1")) {
					SharedPreferences.Editor editor = userDetails.edit();
					editor.clear();
					editor.commit();
					chatDBHelper.clearthechatHistory(getActivity());
					Intent intent = new Intent(getActivity(),
							LoadingScreen.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
					getActivity().finish();
				} else if (mSettingObject.getString("success").equals("1")) {
					mMainAcitivity.hideProgress();
				} else {
					mMainAcitivity.hideProgress();
					Validations.showSingleBtnDialog(
							mSettingObject.getString("message").toString(),
							getActivity());
					// Validations.showAlerDialog(
					// mSettingObject.getString("message").toString(),
					// "Oops !", getActivity());
				}
			} catch (JSONException e) {
				e.printStackTrace();
				mMainAcitivity.hideProgress();
			}
			break;
		case SHARE_LOCATION:
			hideProgress();
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MapFragment f = (MapFragment) getActivity().getFragmentManager()
				.findFragmentById(R.id.mapContainers);
		if (f != null)
			getActivity().getFragmentManager().beginTransaction().remove(f)
					.commit();
	}

	public void gotoMapsScreen() {
		zoomingMarkersList = new ArrayList<Marker>();
		
		mCenterImageView.setVisibility(View.GONE);
		mHeaderTitle.setVisibility(View.VISIBLE);
		mHeaderTitle.setText("Pick Meeting Location");

		mRightBtn.setImageResource(R.drawable.ok);
		// mRightFirstBtn.setVisibility(View.GONE);
		markerPoint = new ArrayList<Marker>();
		mMap = ((MapFragment) getActivity().getFragmentManager()
				.findFragmentById(R.id.mapContainers)).getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setMyLocationEnabled(true);
		mMap.getUiSettings().setCompassEnabled(false);
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMap.getUiSettings().setMyLocationButtonEnabled(false);
		mMap.setTrafficEnabled(true);

		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);

		if (location != null) {
			onLocationChanged(location);
		} else {
			List<String> providerlist = locationManager.getAllProviders();
			if (providerlist != null && providerlist.size() > 0) {
				for (int i = providerlist.size() - 1; i >= 0; i--) {
					location = locationManager
							.getLastKnownLocation(providerlist.get(i));
					if (location != null) {
						onLocationChanged(location);
						break;
					}
				}
			}
		}
		// locationManager.requestLocationUpdates(provider, 20000, 0, this);
		mMap.setOnMapClickListener(this);
		mMap.setOnMarkerDragListener(this);
		mMap.setOnMapLongClickListener(this);
		// mMap.setOnMarkerClickListener(this);

	}

	Bitmap maskedImage;

	@Override
	public void onLocationChanged(Location location) {
		mMap.clear();
		final MarkerOptions mpCurrent = new MarkerOptions();
		MarkerOptions mpUser = new MarkerOptions();
		MarkerOptions mpSharedPoint = new MarkerOptions();

		Editor editor = userDetails.edit();
		LatLng savedLatlng = null;
		if (userDetails != null) {
			if (userDetails.contains("UserLatitudes")
					&& userDetails.contains("UserLongitudes")) {
				if (userDetails.getFloat("UserLatitudes", -1) != -1
						&& userDetails.getFloat("UserLongitudes", -1) != -1) {
					savedLatlng = new LatLng(userDetails.getFloat(
							"UserLatitudes", -1), userDetails.getFloat(
							"UserLongitudes", -1));
				}
			}
		}

		// Based on service
		if (userDetails.getBoolean("isFromBackgroudSrevice", false)) {
			savedLatlng = null;
		}

		mpCurrent.title("Current Location");
		if (savedLatlng != null) {
			userCurrentLatitude=savedLatlng.latitude;
			userCurrentLongitude = savedLatlng.longitude;
//			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
//					savedLatlng.latitude, savedLatlng.longitude), 6));
			mpCurrent.position(new LatLng(savedLatlng.latitude,
					savedLatlng.longitude));
			mMap.setMyLocationEnabled(false);

			final String link = userDetails.getString("profile_image","");//mFrinedImageURL;

			if (TextUtils.isEmpty(link)) {
				mpCurrent.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.default_map));
				Marker zoomMarker= mMap.addMarker(mpCurrent);
				zoomingMarkersList.add(zoomMarker);
			} else {

				new Thread(new Runnable() {

					@Override
					public void run() {
						final Bitmap img = Validations.getBitmapFromURL(link);
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								maskedImage = makeMaskImage(null, img);
								mpCurrent.icon(BitmapDescriptorFactory
										.fromBitmap(maskedImage));
								Marker zoomMarkier= mMap.addMarker(mpCurrent);
								zoomingMarkersList.add(zoomMarkier);
							}
						});
					}
				}).start();
			}

			if (userFriendLatitude != -1 && userFriendLongitude != -1) {
				makePathURL(savedLatlng.latitude, savedLatlng.longitude,
						userFriendLatitude, userFriendLongitude, false);
			}
			if (lineMarkerLatitude != -1 && lineMarkerLongitude != -1) {
				setTheAddress(lineMarkerLatitude, lineMarkerLongitude, null);
				if (userFriendLatitude != -1 && userFriendLongitude != -1) {
					makePathURL(savedLatlng.latitude, savedLatlng.longitude,
							userFriendLatitude, userFriendLongitude, false);
				}
				makePathURL(savedLatlng.latitude, savedLatlng.longitude,
						lineMarkerLatitude, lineMarkerLongitude, true);
			}

		} else {
			mpCurrent.position(new LatLng(location.getLatitude(), location
					.getLongitude()));
			userCurrentLatitude=location.getLatitude();
			userCurrentLongitude = location.getLongitude();
//			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
//					location.getLatitude(), location.getLongitude()), 6));
			// mpCurrent.icon(null);
			mMap.setMyLocationEnabled(true);
			if (userFriendLatitude != -1 && userFriendLongitude != -1) {
				makePathURL(location.getLatitude(), location.getLongitude(),
						userFriendLatitude, userFriendLongitude, false);
			}
			if (lineMarkerLatitude != -1 && lineMarkerLongitude != -1) {
				setTheAddress(lineMarkerLatitude, lineMarkerLongitude, null);
				if (userFriendLatitude != -1 && userFriendLongitude != -1) {
					makePathURL(location.getLatitude(),
							location.getLongitude(), userFriendLatitude,
							userFriendLongitude, false);
				}
				makePathURL(location.getLatitude(), location.getLongitude(),
						lineMarkerLatitude, lineMarkerLongitude, true);
			}
		}

	}
	private List<Marker> zoomingMarkersList;

	private void setZoomingToMap() {
		if(zoomingMarkersList!=null && zoomingMarkersList.size()>0){
			//Calculate the markers to get their position
			try {
				LatLngBounds.Builder b = new LatLngBounds.Builder();
				for (Marker m : zoomingMarkersList) {
				    b.include(m.getPosition());
				}
				if(userCurrentLatitude!=-1 && userCurrentLongitude!=-1){
					b.include(new LatLng(userCurrentLatitude,userCurrentLongitude));
				}
				
				LatLngBounds bounds = b.build();
				//Change the padding as per needed
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 30);
//			mMap.moveCamera(cu);
				mMap.animateCamera(cu);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void makePathURL(double sourcelat, double sourcelog, double destlat,
			double destlog, boolean isDrawpath) {
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.googleapis.com/maps/api/directions/json");
		urlString.append("?origin=");// from
		urlString.append(Double.toString(sourcelat));
		urlString.append(",");
		urlString.append(Double.toString(sourcelog));
		urlString.append("&destination=");// to
		urlString.append(Double.toString(destlat));
		urlString.append(",");
		urlString.append(Double.toString(destlog));
		urlString.append("&sensor=false&mode=driving&alternatives=true");

		ConnectPathAsyncTask task = new ConnectPathAsyncTask(isDrawpath);
		task.execute(urlString.toString());
		// return urlString.toString();
	}

	private class ConnectPathAsyncTask extends AsyncTask<String, Void, String> {
		boolean isDraw;
		private ProgressDialog progressDialog;

		ConnectPathAsyncTask(boolean urlToDraw) {
			isDraw = urlToDraw;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(getActivity());
			progressDialog
					.setMessage("Loading..."/* "Fetching route, Please wait..." */);
			progressDialog.setIndeterminate(true);
			if (isDraw) {
				progressDialog.show();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			MapJsonParser jParser = new MapJsonParser();
			String json = jParser.getJSONFromUrl(params[0]);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				if (isDraw) {
					drawPath(result, isDraw);
					progressDialog.hide();
				} else {
					getDirectionMidPoint(result);
				}
			}
		}

	}

	private void getDirectionMidPoint(String result) {
		try {
			final JSONObject json = new JSONObject(result);
			if (json != null) {
				if (json.has("status")
						&& json.getString("status").equalsIgnoreCase("ok")) {
					JSONArray routeArray = json.getJSONArray("routes");
					JSONObject routes = routeArray.getJSONObject(0);
					JSONObject overviewPolylines = routes
							.getJSONObject("overview_polyline");
					String encodedString = overviewPolylines
							.getString("points");
					List<LatLng> list = decodePoly(encodedString);
					LatLng midLtLg = null;
					if (list != null && list.size() > 0) {
						if (list.size() > 2) {
							midLtLg = list.get((list.size() / 2) + 1);
						} else {
							midLtLg = list.get((list.size() / 2));
						}

						midPointLatitude = midLtLg.latitude;
						midPointLongitude = midLtLg.longitude;

						MarkerOptions midMarker = new MarkerOptions();
						midMarker.position(new LatLng(midPointLatitude,
								midPointLongitude));
						midMarker.title("Mid point between you and "
								+ mTxtUserName.getText().toString());
						midMarker.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.pin_green));
						Marker zoomMarker = mMap.addMarker(midMarker);
						zoomingMarkersList.add(zoomMarker);
						setZoomingToMap();
					}
				} else {
					if (userFriendLatitude != -1 && userFriendLongitude != -1) {
						final MarkerOptions midMarker = new MarkerOptions();
						midMarker.position(new LatLng(userFriendLatitude,
								userFriendLongitude));
						midMarker.title("Other User");
						new Thread(new Runnable() {

							@Override
							public void run() {
								final Bitmap img = Validations
										.getBitmapFromURL(mFrinedImageURL);

								getActivity().runOnUiThread(new Runnable() {

									@Override
									public void run() {
										if (img != null) {
											maskedImage = makeMaskImage(null,
													img);
											midMarker.icon(BitmapDescriptorFactory
													.fromBitmap(maskedImage));

										} else {
											midMarker
													.icon(BitmapDescriptorFactory
															.fromResource(R.drawable.default_map));
										}
										
										Marker zoomMarker=mMap.addMarker(midMarker);
										zoomingMarkersList.add(zoomMarker);
										setZoomingToMap();
									}
								});

							}
						}).start();
					}
				}
			}
		} catch (JSONException e) {
		}

	}

	public void drawPath(String result, boolean isdraw) {
		try {
			// Tranform the string into a json object
			final JSONObject json = new JSONObject(result);
			if (json != null) {
				if (json.has("status")
						&& json.getString("status").equalsIgnoreCase("ok")) {
					if (isdraw) {
						JSONArray routeArray = json.getJSONArray("routes");
						JSONObject routes = routeArray.getJSONObject(0);
						JSONObject overviewPolylines = routes
								.getJSONObject("overview_polyline");
						String encodedString = overviewPolylines
								.getString("points");
						List<LatLng> list = decodePoly(encodedString);

						for (int z = 0; z < list.size() - 1; z++) {
							LatLng src = list.get(z);
							LatLng dest = list.get(z + 1);
							Polyline line = mMap
									.addPolyline(new PolylineOptions()
											.add(new LatLng(src.latitude,
													src.longitude),
													new LatLng(dest.latitude,
															dest.longitude))
											.width(7).color(Color.BLUE)
											.geodesic(true));
						}
					} else {
						/**********
						 * if status is ok for midpoint draw than show midpoint
						 * here
						 *********/
					}

				} else {
					/**********
					 * if status is not ok for midpoint draw than show other
					 * user exact location here
					 *********/
					if (isdraw) {
//						mMap.animateCamera(CameraUpdateFactory.zoomOut());
						Validations
								.showSingleBtnDialog(
										"Sorry, Can't retrive route for given location",
										getActivity());
					} else {
						if (userFriendLatitude != -1
								&& userFriendLongitude != -1) {
							final MarkerOptions midMarker = new MarkerOptions();
							midMarker.position(new LatLng(userFriendLatitude,
									userFriendLongitude));
							midMarker.title("Other User");
							new Thread(new Runnable() {

								@Override
								public void run() {
									final Bitmap img = Validations
											.getBitmapFromURL(mFrinedImageURL);
									getActivity().runOnUiThread(new Runnable() {

										@Override
										public void run() {
											maskedImage = makeMaskImage(null,
													img);
											midMarker
													.icon(BitmapDescriptorFactory
															.fromBitmap(maskedImage));
											mMap.addMarker(midMarker);
										}
									});
								}
							}).start();
						}
					}
				}
			}
		} catch (JSONException e) {
		}
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
	}

	@Override
	public void onMarkerDragEnd(Marker mMarker) {
		double latitude = mMarker.getPosition().latitude;
		double longitude = mMarker.getPosition().longitude;
		showProgress();
		setTheAddress(latitude, longitude, mMarker);
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
	}

	@Override
	public void onMapLongClick(LatLng mMarker) {
		if (!isFromListItem) {
			showProgress();
			double latitude = mMarker.latitude;
			double longitude = mMarker.longitude;
			markerOption = new MarkerOptions();
			markerOption.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.map_pin_2));
			markerOption.draggable(true);
			markerOption.position(mMarker);
			Marker newMarker = mMap.addMarker(markerOption);
			markerPoint.add(newMarker);
			setTheAddress(latitude, longitude, newMarker);
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {
	}

	public void setTheAddress(final double RegLat, final double RegLan,
			final Marker marker) {
		final String newLatitude = toString().valueOf(RegLat);
		final String newLongitude = toString().valueOf(RegLan);
		try {
			Thread th = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						JSONObject jsonObj = parser_Json
								.getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?latlng="
										+ newLatitude
										+ ","
										+ newLongitude
										+ "&sensor=true");
						Log.e("Google APi", jsonObj.toString());
						String Status = jsonObj.getString("status");
						if (Status.equalsIgnoreCase("OK")) {
							JSONArray Results = jsonObj.getJSONArray("results");
							JSONObject zero = Results.getJSONObject(0);
							addressString = (String) zero
									.get("formatted_address");
							try {
								addressString = new String(addressString
										.getBytes("ISO-8859-1"), "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}else{
							addressString="";
						}
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								hideProgress();
								if (isFromListItem) {
									MarkerOptions mpSharedPoint = new MarkerOptions();
									mpSharedPoint.position(new LatLng(
											lineMarkerLatitude,
											lineMarkerLongitude));
									mpSharedPoint.title("SharedPoint");
									mpSharedPoint.snippet(addressString);
									mpSharedPoint.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.map_pin_2));
								Marker zoomMarker=	mMap.addMarker(mpSharedPoint);
								zoomingMarkersList.add(zoomMarker);
								} else {
									showTwoBtnDialog("SET", "DELETE",
											addressString, getActivity(),
											RegLan, RegLat, marker);
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						e.getMessage();
					}
				}
			});
			th.start();
		} catch (Exception e) {
			try {
				hideProgress();
				Geocoder geocoder = new Geocoder(getActivity(),
						Locale.getDefault());
				List<Address> addresses = geocoder.getFromLocation(RegLat,
						RegLan, 1);
				StringBuilder sb = new StringBuilder();
				if (addresses.size() > 0) {
					Address address = addresses.get(0);
					String addressLine1 = address.getAddressLine(0);
					String addressLine2 = address.getAddressLine(1);
					String addressLine3 = address.getAddressLine(2);

					sb.append(addressLine1).append(",");
					sb.append(addressLine2).append(",");
					sb.append(addressLine3).append("\n");

					// sb.append(address.getCountryName());
					// RegLat = String.valueOf(address.getLatitude());
					// RegLan = String.valueOf(address.getLongitude());
				}
				addressString = sb.toString();
				try {
					addressString = new String(
							addressString.getBytes("ISO-8859-1"), "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				// edtAddress.setText(addressString);
				// Toast.makeText(getActivity(), "" + addressString,
				// Toast.LENGTH_SHORT).show();
			} catch (IOException e1) {
			}
		}
	}

	public static class parser_Json {
		public static JSONObject getJSONfromURL(String url) {
			// initialize
			InputStream is = null;
			String result = "";
			JSONObject jObject = null;
			// http post
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection " + e.toString());
			}
			// convert response to string
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			} catch (Exception e) {
				Log.e("log_tag", "Error converting result " + e.toString());
			}
			// try parse the string to a JSON object
			try {
				jObject = new JSONObject(result);
			} catch (JSONException e) {
				Log.e("log_tag", "Error parsing data " + e.toString());
			}
			return jObject;
		}
	}

	private void showTwoBtnDialog(String positiveBtnTxt, String negativeBtnTxt,
			final String message, FragmentActivity activity,
			final double regLan, final double regLat, final Marker marker) {

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
		btnCancel = (TextView) alertDialog.findViewById(R.id.btnCancel);
		btnCancel.setText(negativeBtnTxt);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				deleteMarker(marker.getId());
			}
		});
		btnOK = (TextView) alertDialog.findViewById(R.id.btnOK);
		btnOK.setText(positiveBtnTxt);
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				marker.setSnippet(message);
			}
		});

		btnCancel.setVisibility(View.VISIBLE);
		alertDialog.show();
	}

	private void deleteMarker(String markerId) {
		if (markerPoint != null && markerPoint.size() > 0) {
			for (int i = 0; i < markerPoint.size(); i++) {
				String markerid = markerPoint.get(i).getId();
				if (markerid.equalsIgnoreCase(markerId)) {
					markerPoint.get(i).remove();
					markerPoint.remove(i);
				}
			}
		}
	}

	private void showSingleBtnDialog(String message, Context context) {
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
				mChatListingFragment.removeNearByItem(muserData);

				// delete User from Local DB
				chatDBHelper.cleartheuserChatHistory(getActivity(),
						muserData.getFriendId());

				slideBack();
			}
		});

		alertDialog.show();
	}

	private double getRound(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	private List<LatLng> decodePoly(String encoded) {
		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}
		return poly;
	}

	private void showProgress() {
		((LinearLayout) mRootView.findViewById(R.id.loading_view))
				.setVisibility(View.VISIBLE);
	}

	private void hideProgress() {
		((LinearLayout) mRootView.findViewById(R.id.loading_view))
				.setVisibility(View.GONE);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		if (fromUser) {
			callGiveRatingWS(rating);
		}
	}

	private void callGiveRatingWS(float rating) {
		showProgress();
		Bundle mBundle = new Bundle();
		mBundle.putString("user_id", getArguments().getString("user_id"));
		mBundle.putString("rating_given_to",
				getArguments().getString("jabber_id"));
		mBundle.putString("rating", String.valueOf(rating));
		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.POST_RATINGS, mBundle),
				POST_RATING, this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mChatListingFragment.mOnceClicked = true;
			}
		}, 200);

	}

	public Bitmap makeMaskImage(ImageView mImageView, Bitmap original) {

		// Bitmap original = BitmapFactory
		// .decodeResource(getResources(), mContent);
		Bitmap mask = BitmapFactory.decodeResource(getResources(),
				R.drawable.map_pin_blank);
		Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(),
				Config.ARGB_8888);

		// int mask_reWidht = mask.getWidth() - 30;
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
		// mImageView.setScaleType(ScaleType.CENTER);
		// mImageView.setImageBitmap(result);
		// mImageView.setBackgroundResource(R.drawable.photo_4);

	}

}
