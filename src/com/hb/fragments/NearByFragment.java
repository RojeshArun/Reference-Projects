package com.hb.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import khandroid.ext.apache.http.HttpEntity;
import khandroid.ext.apache.http.HttpResponse;
import khandroid.ext.apache.http.client.HttpClient;
import khandroid.ext.apache.http.client.methods.HttpPost;
import khandroid.ext.apache.http.impl.client.DefaultHttpClient;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Utilites.Validations;
import com.android.volley.VolleyError;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.barter.xmpp.ChatDBHelper;
import com.barter.xmpp.HBXMPP;
import com.barter.xmpp.ServiceUtility;
import com.hb.adapters.NearByUsersListAdapter;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.baseapplication.BaseApplication;
import com.hb.models.NearByUsersData;
import com.hb.webserviceutilities.IParseListener;
import com.hb.webserviceutilities.JSONRequestResponse;
import com.hb.webserviceutilities.Webservices;

public class NearByFragment extends Fragment implements IParseListener,
		OnCheckedChangeListener, OnClickListener/*
												 * , OnMapClickListener,
												 * OnMarkerClickListener,
												 * OnMarkerDragListener,
												 * OnMapLongClickListener
												 */{

	private static final int CODE_MAP = 100;
	String addressString;
	// Map Variablse
	// protected MySupportMapFragment mapSupportFragment;
	// protected GoogleMap mMap;
	// private ArrayList<LatLng> mMarkerPoints = new ArrayList<LatLng>();
	// private LatLng mCurrentLatLng, mLatLng;
	private TextView mTxtView_NoResult;
	private MainActivity mMainAcitivity;
	// private Marker mCurrentMarker;
	private NearByUsersListAdapter mListAdapter;
	private ListView mListView;
	private Context mContext;
	private View mRootView;
	private String mCurrentLatitude, mCurrentLongitude;
	private List<NearByUsersData> mListArray = new ArrayList<NearByUsersData>();
	// MarkerOptions markerOption;
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	Boolean ifChecked = false;
	// Transperent
	private LinearLayout mSemiTransLyt;
	ImageView mRightButOneButton;
	private LinearLayout mUserListing, mUserMaps;
	TextView mTxtViewUserName;
	private SharedPreferences userDetails;
	private ChatDBHelper chatDBHelper;
	private ContentResolver resolver;
	// View marker, mMarkerPin;
	private RadioGroup mRadioGroup;
	private boolean distance = true;
	private HBXMPP hbxmpp;
	RatingBar mRatingBar;
	ImageView mImageViewUser, mImageUserPIN;
	private AQuery mAQuery;
	private AQuery mAQueryPin;

	Boolean mEnterTheMap = false;

	public NearByFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
		mMainAcitivity = (MainActivity) getActivity();
		mContext = getActivity();
		userDetails = PreferenceManager.getDefaultSharedPreferences(mContext);
		hbxmpp = ((BaseApplication) getActivity().getApplication()).getHbxmpp();

		mAQuery = new AQuery(getActivity());
		mAQueryPin = new AQuery(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.nearby_maps_fragment, null);

		chatDBHelper = new ChatDBHelper();
		resolver = getActivity().getContentResolver();

		setNearByFragment();

		setOnClickListeners();
		checkNetworkAndCallWS();

		return mRootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		hbxmpp = ((BaseApplication) getActivity().getApplication()).getHbxmpp();
		getActivity().registerReceiver(presenceListener,
				new IntentFilter(ServiceUtility.PRESENCE_UPDATE_ACTION));
		
		getActivity().registerReceiver(isXMPPLoggedIN,
				new IntentFilter(ServiceUtility.IS_XMPP_LOGGEDIN));
		
		
	}

	@Override
	public void onPause() {
		super.onPause();

		getActivity().unregisterReceiver(presenceListener);
		
		getActivity().unregisterReceiver(isXMPPLoggedIN);
	}

	private Handler mhHandler = new Handler();

	private BroadcastReceiver presenceListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			mhHandler.post(new Runnable() {

				@Override
				public void run() {
					if (mListView != null) {
						if (mListAdapter != null) {
							mListAdapter.refreshPresence();
						}
					}
				}
			});

		}
	};

	
	private BroadcastReceiver isXMPPLoggedIN = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			Log.e("Near By List Fragment", "Broad cast received");
			try {
				mListAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private void checkNetworkAndCallWS() {
		if (Validations.isNetworkAvailable(getActivity())) {
			callOrderByDistanceWS();
		} else {
			Validations.showSingleBtnDialog(
					getActivity().getString(
							R.string.no_internet_connection_please_try_again),
					getActivity());
		}
	}

	static int mCount;

	private void setNearByFragment() {
		mMainAcitivity
				.setHeaderTitle(getActivity().getString(R.string.matches));
		mListView = (ListView) mRootView.findViewById(R.id.nearby_users_list);

		mSemiTransLyt = (LinearLayout) mRootView
				.findViewById(R.id.loading_transperent);
		mSemiTransLyt.setOnClickListener(this);
		mUserListing = (LinearLayout) mRootView.findViewById(R.id.user_listing);
		mUserListing.setVisibility(View.VISIBLE);
		mUserMaps = (LinearLayout) mRootView.findViewById(R.id.users_maps);
		mTxtView_NoResult = (TextView) mRootView
				.findViewById(R.id.txtView_NoResult);
		mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.ratingDistance);
		mRadioGroup.setOnCheckedChangeListener(this);

	}

	public void hideMaps() {
		mUserListing.setVisibility(View.VISIBLE);
	}

	public void hideListing() {
		mUserListing.setVisibility(View.GONE);
	}

	// public void testForNoRecords() {
	//
	// if (chatHistory.size() > 1) {
	// mChatList.setVisibility(View.VISIBLE);
	// mTxtView_NoResult.setVisibility(View.INVISIBLE);
	//
	// } else {
	// // show No Chat History
	// mChatList.setVisibility(View.INVISIBLE);
	// mTxtView_NoResult.setVisibility(View.VISIBLE);
	//
	// }
	//
	// }

	boolean mOnceClicked = true;

	private void setOnClickListeners() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (mOnceClicked) {
					mOnceClicked = false;
					NearByUsersData muserData = (NearByUsersData) parent
							.getAdapter().getItem(position);

					gotoChatDetailsFragment(muserData);

				}
			}
		});

	}

	public void callOrderByDistanceWS() {

		mMainAcitivity.showProgress();

		Bundle mBundle = new Bundle();
		mBundle.putString("user_id",
				userDetails.getString("Loggedin_userid", ""));
		mBundle.putString("latitude", userDetails.getString("UserLatitude", ""));
		mBundle.putString("longitude",
				userDetails.getString("UserLongitude", ""));
		mBundle.putString("order_by", "distance");

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();
		mJsonRequestResponse.getResponse(
				Webservices.encodeUrl(Webservices.MAP_PINS, mBundle), CODE_MAP,
				this);

	}

	@Override
	public void ErrorResponse(VolleyError error, int requestCode) {
		mMainAcitivity.hideProgress();
	}

	@Override
	public void SuccessResponse(JSONObject response, int requestCode) {

		switch (requestCode) {
		case CODE_MAP:
			parseMapsData(response);
			break;

		default:
			break;
		}
		mMainAcitivity.hideProgress();

	}

	private void parseMapsData(JSONObject response) {

		mListArray.clear();

		try {
			JSONObject mJsonObject = new JSONObject(
					response.getString("settings"));

			if (mJsonObject.getString("success").equalsIgnoreCase("1")) {

				JSONArray mArray = new JSONArray(response.getString("data"));

				if (mArray.length() == 0) {

					mListView.setVisibility(View.GONE);
					mTxtView_NoResult.setSingleLine(true);
					mTxtView_NoResult.setVisibility(View.VISIBLE);
					mTxtView_NoResult.setTextSize(16);
					mTxtView_NoResult.setText(R.string.no_nearby_users_found_);
				} else {
					mTxtView_NoResult.setVisibility(View.GONE);
					for (int i = 0; i < mArray.length(); i++) {
						JSONObject mJsonData = mArray.getJSONObject(i);
						NearByUsersData mData = new NearByUsersData(mJsonData);
						mListArray.add(mData);
					}
					mListAdapter = new NearByUsersListAdapter(mContext,
							mListArray, hbxmpp, NearByFragment.this);
					mListView.setAdapter(mListAdapter);
					
					mListAdapter.notifyDataSetChanged();

				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void gotoChatDetailsFragment(NearByUsersData muserData) {

		NearByChatDetailsFragment mChatDetailFrag = new NearByChatDetailsFragment(
				NearByFragment.this, muserData);

		Bundle b = new Bundle();

		b.putSerializable("username", muserData.getmUserName());
		b.putSerializable("match_preference", muserData.getmMatchPreferences());
		b.putSerializable("jabber_id", muserData.getmJabberId());
		b.putSerializable("profile_image", muserData.getmImageURL());
		b.putSerializable("avg_rating", muserData.getmRatingStatus());
		b.putSerializable("user_id", muserData.getUserid());

		mChatDetailFrag.setArguments(b);

		mFragmentManager = getActivity().getSupportFragmentManager();

		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
				R.anim.slide_in_left, R.anim.enter_from_left,
				R.anim.exit_to_right);

		mFragmentTransaction.add(R.id.main_frame, mChatDetailFrag)
				.addToBackStack(NearByFragment.class.getSimpleName())
				.commitAllowingStateLoss();

	}

	public void setSemiTransVisible() {
		mSemiTransLyt.setVisibility(View.VISIBLE);
	}

	public void setSemiTransGone() {
		mSemiTransLyt.setVisibility(View.GONE);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		Bundle mBundle = new Bundle();

		JSONRequestResponse mJsonRequestResponse = new JSONRequestResponse();

		switch (checkedId) {

		case R.id.ratingBtn:

			if (distance) {

				distance = false;
				mMainAcitivity.showProgress();
				mBundle.putString("user_id",
						userDetails.getString("Loggedin_userid", ""));
				mBundle.putString("latitude",
						userDetails.getString("UserLatitude", ""));
				mBundle.putString("longitude",
						userDetails.getString("UserLongitude", ""));
				mBundle.putString("order_by", "avg_rating");
				mJsonRequestResponse.getResponse(
						Webservices.encodeUrl(Webservices.MAP_PINS, mBundle),
						CODE_MAP, this);
			}
			break;
		case R.id.DistanceBtn:
			if (distance) {

			} else {
				distance = true;
				mMainAcitivity.showProgress();
				mBundle.putString("user_id",
						userDetails.getString("Loggedin_userid", ""));
				mBundle.putString("latitude",
						userDetails.getString("UserLatitude", ""));
				mBundle.putString("longitude",
						userDetails.getString("UserLongitude", ""));
				mBundle.putString("order_by", "distance");

				mJsonRequestResponse.getResponse(
						Webservices.encodeUrl(Webservices.MAP_PINS, mBundle),
						CODE_MAP, this);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.loading_transperent:

			mMainAcitivity.setTitle(R.string.near_by);
			if (!mMainAcitivity.isShowPreference) {
				// mMainAcitivity.isShowPreference = true;
				// mMainAcitivity.callSetPreferencesWS();

				mMainAcitivity.validatePreferences();

			} else {

				mMainAcitivity.hideFloatingMenu();
				mMainAcitivity.disableSecondTab();
				// mMainAcitivity.mTab2Lyt.callOnClick();

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						callOrderByDistanceWS();
					}
				}, 500);

			}

			break;

		default:
			break;
		}

	}

	public String getAvailability(String fromId) {
		String userState = "Offline";
		Roster roster = null;
		
		
		try {
			
			hbxmpp = ((BaseApplication) getActivity().getApplication()).getHbxmpp();
			
			Roster.setDefaultSubscriptionMode(SubscriptionMode.accept_all);
				if (hbxmpp.getConnection() != null) {
					roster = hbxmpp.getConnection().getRoster();
					roster.setSubscriptionMode(SubscriptionMode.accept_all);
				} else {

				}
			
		} catch (Exception e) {
			e.printStackTrace();
			return userState;
		}
		if (roster != null) {
			Presence availability = roster.getPresence(fromId);
			if (availability.isAvailable()) {
				userState = "Online";
			}
		}

		return userState;
	}

	NearByUsersData mUserData;

	public static Bitmap createDrawableFromView(Context context, View view) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.layout(0, 0, displayMetrics.widthPixels,
				displayMetrics.heightPixels);
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
				view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);

		return bitmap;
	}

	public void setTheAddress(double RegLat, double RegLan) {

		double lat, lng;

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

						}

						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {

								Toast.makeText(getActivity(),
										"" + addressString, Toast.LENGTH_LONG)
										.show();
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
				Toast.makeText(getActivity(), "" + addressString,
						Toast.LENGTH_SHORT).show();
			} catch (IOException e1) {

			}
		}
	}

	public static class parser_Json {
		public static JSONObject getJSONfromURL(String url) {
			InputStream is = null;
			String result = "";
			JSONObject jObject = null;

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();

			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection " + e.toString());
			}

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

			try {
				jObject = new JSONObject(result);
			} catch (JSONException e) {
				Log.e("log_tag", "Error parsing data " + e.toString());
			}

			return jObject;
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e("On Activity Create", "On ActivityCreate");
	}

	public void removeNearByItem(NearByUsersData muserData) {
		mListAdapter.removItemat(muserData);
		if (mListAdapter == null || mListAdapter.getCount() == 0) {
			mTxtView_NoResult.setVisibility(View.VISIBLE);
		}
	}
}
// mMainAcitivity.hidePreferenceMenu();