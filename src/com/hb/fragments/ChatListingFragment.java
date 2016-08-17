package com.hb.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Profile;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.Utilites.Validations;
import com.barter.xmpp.ChatDBHelper;
import com.barter.xmpp.ChatData;
import com.barter.xmpp.HBXMPP;
import com.barter.xmpp.ServiceUtility;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hb.adapters.ChatListAdapter;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.baseapplication.BaseApplication;
import com.hb.models.Chat;
import com.hb.webserviceutilities.Webservices;

public class ChatListingFragment extends Fragment implements OnClickListener {

	private MainActivity mMainAcitivity;
	private ListView mChatList;
	private Context mContext;
	private View mRootView;

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private HBXMPP hbxmpp;
	private List<ChatData> chatHistory = new ArrayList<ChatData>();
	private ChatDBHelper chatDBHelper;
	private SharedPreferences userDetails;
	ChatListAdapter mListAdapter;
	private TextView mTxtView_NoResult;
	public HashMap<String, String> profileURLs = new HashMap<String, String>();
	public HashMap<String, String> profileDisplayNames = new HashMap<String, String>();

	public ChatListingFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMainAcitivity = (MainActivity) getActivity();
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.chat_listing_fragment, null);
		hbxmpp = ((BaseApplication) getActivity().getApplication()).getHbxmpp();

		userDetails = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		
		if (Validations.isNetworkAvailable(getActivity())) {
			getDataFromWS();
		} else {
			Validations.showSingleBtnDialog(
					getActivity().getString(
							R.string.no_internet_connection_please_try_again),
					getActivity());
		}
		return mRootView;
	}
	
	
	private void getDataFromWS(){
		mMainAcitivity.showProgress();
		new Thread(new Runnable() {
			@Override
			public void run() {
				chatDBHelper = new ChatDBHelper();
				chatHistory = chatDBHelper.loadAllRecentChats(getActivity(),
						userDetails.getString("Loggedin_jabber_id", ""));
				String ids = "";
				for(int i=0;i<chatHistory.size();i++){
					if(i>0){
						ids = ids +","+ chatHistory.get(i).getFriendId().replace("user_", "");
					}else{
						ids = ids + chatHistory.get(i).getFriendId().replace("user_", "");
					}
					
				}
				get(Webservices.BASE_URL+"get_image_url/?&user_ids="+ids);
				if(getActivity() != null){
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mMainAcitivity.hideProgress();
							setNearByFragment();
							setOnClickListeners();
							if (chatHistory.size() > 0) {
								settheRecentChats(chatHistory, false);
							} else {
								// show No Chat History
								mChatList.setVisibility(View.INVISIBLE);
								mTxtView_NoResult.setVisibility(View.VISIBLE);
							}
						}
					});
				}
			}
		}).start();
	}

	private void settheRecentChats(final List<ChatData> chatHistory,
			final boolean status) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				sorttheList(chatHistory);

				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {

							Calendar cal = Calendar.getInstance();
							SimpleDateFormat df = new SimpleDateFormat(
									"dd MMMM yyyy hh:mm a", Locale.ENGLISH);
							String formattedDate = df.format(cal.getTime());
							if (chatHistory.size() == 0) {
								mChatList.setVisibility(View.GONE);
								// text msg
								mMainAcitivity.hideProgress();
							} else {
								mChatList.setVisibility(View.VISIBLE);
							}

							for (int i = 0; i < chatHistory.size(); i++) {
								String availability = new String();
								if (status) {
									availability = getAvailability(chatHistory
											.get(i).getFriendId()
											+ Webservices.CHAT_DOMAIN);
								} else {
									availability = getAvailability(chatHistory
											.get(i).getFriendId()
											+ Webservices.CHAT_DOMAIN);
								}

								int timeDiff = checktheDiffrence(formattedDate,
										chatHistory.get(i)
										.getChatMessageDatetime());
								String timeDifferenceString = new String();
								if (timeDiff < 60) {
									timeDifferenceString = "just now";
								} else if (timeDiff < 3600) {
									timeDiff = timeDiff / 60;
									timeDifferenceString = timeDiff + "mins ago";
								}else{
									try {
										Date date = new Date();
										Date dateMessage = new SimpleDateFormat("dd MMMM yyyy hh:mm a",
												Locale.ENGLISH).parse(chatHistory.get(i)
														.getChatMessageDatetime());
										
										if(date.getDate() == dateMessage.getDate()){
											SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
											timeDifferenceString = "Today "+sdf.format(dateMessage);
										}else if((date.getDate()-1) == dateMessage.getDate()){
											SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
											timeDifferenceString = "Yesterday "+sdf.format(dateMessage);
										}else if(date.getYear() == dateMessage.getYear()){
											SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm");
											timeDifferenceString = sdf.format(dateMessage);
										} else{
											SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
											timeDifferenceString = sdf.format(dateMessage);
										}
										
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}

								Chat data = new Chat();
								data.setFriendName(chatHistory.get(i)
										.getChatFriendName());
								data.setFriendProfileImage(chatHistory.get(i)
										.getFromProfileImageUrl());
								data.setNewMessage(chatHistory.get(i)
										.getOrginalMessage());
								data.setMessageTime(timeDifferenceString);
								data.setFriendUserName(chatHistory.get(i)
										.getFriendId());

								data.setStatus(availability);
								data.setNewMessageCount(String
										.valueOf(chatHistory.get(i)
												.getUnreadMessageCount()));
								data.setFriendId(chatHistory.get(i)
										.getFriendId());
								data.setUserId(chatHistory.get(i).getUserId());
								data.setFromId(chatHistory.get(i).getFromId());

								mListAdapter.addItem(data, status);
							}
							mMainAcitivity.hideProgress();
						}

					});
				}

			}
		}).start();

	}

	private void sorttheList(List<ChatData> chatHistory) {

		Collections.sort(chatHistory, new Comparator<ChatData>() {
			SimpleDateFormat keysDate = new SimpleDateFormat(
					"dd MMMM yyyy hh:mm a", Locale.ENGLISH);

			@Override
			public int compare(ChatData lhs, ChatData rhs) {

				try {
					Date date1 = keysDate.parse(lhs.getChatMessageDatetime());
					Date date2 = keysDate.parse(rhs.getChatMessageDatetime());
					return date2.compareTo(date1);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	private int checktheDiffrence(String formattedDate,
			String chatMessageDatetime) {

		long diffInMins = 0;
		try {
			Date date1 = new SimpleDateFormat("dd MMMM yyyy hh:mm a",
					Locale.ENGLISH).parse(chatMessageDatetime);
			Date date2 = new SimpleDateFormat("dd MMMM yyyy hh:mm a",
					Locale.ENGLISH).parse(formattedDate);
			long diffInMis = date2.getTime() - date1.getTime();
			diffInMins = TimeUnit.MILLISECONDS.toSeconds(diffInMis);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) diffInMins;
	}

	public String getAvailability(String fromId) {

		String userState = "Offline";

		Roster roster = null;

		try {

			hbxmpp = ((BaseApplication) getActivity().getApplication())
					.getHbxmpp();
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

	boolean mOnceClicked = true;

	private void setOnClickListeners() {

		mChatList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mOnceClicked) {
					mOnceClicked = false;
					Chat muserData = (Chat) parent.getAdapter().getItem(
							position);
					gotoChatDetailsFragment(muserData);
				}
			}
		});
	}

	private void gotoChatDetailsFragment(Chat muserData) {

		ChatDetailFragment mChatDetailFrag = new ChatDetailFragment(
				ChatListingFragment.this, muserData);
		// NearByChatDetailsFragment mChatDetailFrag = new
		// NearByChatDetailsFragment();
		Bundle b = new Bundle();

		b.putSerializable("jabber_id", muserData.getFriendId());
		b.putSerializable("user_latitude",
				userDetails.getString("UserLatitude", ""));
		b.putSerializable("user_longitude",
				userDetails.getString("UserLongitude", ""));
		b.putSerializable("user_id",
				userDetails.getString("Loggedin_userid", ""));
		// b.putSerializable(key, value)
		b.putSerializable("friend_image", muserData.getFriendProfileImage());
		b.putSerializable("username", muserData.getFriendName());

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

	private void setNearByFragment() {
		mMainAcitivity.setHeaderTitle(getActivity().getString(R.string.chat));

		mTxtView_NoResult = (TextView) mRootView
				.findViewById(R.id.txtView_no_result);
		mTxtView_NoResult.setVisibility(View.INVISIBLE);

		mChatList = (ListView) mRootView.findViewById(R.id.nearby_users_list);

		mListAdapter = new ChatListAdapter(mContext, ChatListingFragment.this,
				hbxmpp);
		mListAdapter.removeall();
		mChatList.setAdapter(mListAdapter);

	}

	@Override
	public void onClick(View v) {

	}

	private Handler mHandler = new Handler();

	@Override
	public void onResume() {

		super.onResume();

		hbxmpp = ((BaseApplication) getActivity().getApplication()).getHbxmpp();

		getActivity().registerReceiver(presenceListener,
				new IntentFilter(ServiceUtility.PRESENCE_UPDATE_ACTION));

		getActivity().registerReceiver(serviceConnection,
				new IntentFilter(ServiceUtility.MESSAGE_UPDATE_ACTION));

		getActivity().registerReceiver(isXMPPLoggedIN,
				new IntentFilter(ServiceUtility.IS_XMPP_LOGGEDIN));
	}

	@Override
	public void onPause() {

		super.onPause();

		getActivity().unregisterReceiver(presenceListener);

		getActivity().unregisterReceiver(serviceConnection);

		getActivity().unregisterReceiver(isXMPPLoggedIN);
	}

	private BroadcastReceiver presenceListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			mHandler.post(new Runnable() {

				@Override
				public void run() {

					if (mChatList != null) {
						if (mListAdapter != null) {
							mListAdapter.notifyDataSetChanged();
						}
					}

				}
			});

		}
	};

	public void testForNoRecords() {

		if (chatHistory.size() > 1) {
			mChatList.setVisibility(View.VISIBLE);
			mTxtView_NoResult.setVisibility(View.INVISIBLE);

		} else {
			// show No Chat History
			mChatList.setVisibility(View.INVISIBLE);
			mTxtView_NoResult.setVisibility(View.VISIBLE);

		}

	}

	private BroadcastReceiver serviceConnection = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, final Intent intent) {

			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (userDetails.getString("refreshview", "")
							.equalsIgnoreCase("yes")) {

						Bundle extrasBundle = new Bundle();
						extrasBundle = intent.getExtras();
						ChatData messageData = (ChatData) extrasBundle
								.get("MessageData");

						String mFriendName = messageData.getChatFriendName();
						String mMessage = messageData.getOrginalMessage();

						String[] mFinalMsg;

						if (mMessage.contains("##@!@##")) {

							mFinalMsg = mMessage.split("##@!@##");
							mMessage = mFinalMsg[0];
						}
						mListAdapter.setRecentMessage(mMessage, mFriendName);
					}
					refreshchat();
				}
			});

		}
	};

	private BroadcastReceiver isXMPPLoggedIN = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub

			Log.e("Chat list Fragment", "Broad cast received");

			try {
				mListAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	public void removeNearByItem(Chat muserData) {
		mListAdapter.removItemat(muserData);
	}

	public void refreshchat() {
		chatHistory.clear();
		mListAdapter.removeall();
		chatHistory = chatDBHelper.loadAllRecentChats(getActivity(),
				userDetails.getString("Loggedin_jabber_id", ""));
		if (chatHistory.size() > 0) {
			settheRecentChats(chatHistory, false);
			mListAdapter.notifyDataSetChanged();
		} else {
			// show No Chat History
			mChatList.setVisibility(View.INVISIBLE);
			mTxtView_NoResult.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	public  void get(String url){
		DefaultHttpClient Client = new DefaultHttpClient();
		String result = null;
		
		profileURLs.clear();
		profileDisplayNames.clear();
		try
		{
			Log.e("URL", url);
			HttpGet httpget = new HttpGet(url);

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			result = Client.execute(httpget, responseHandler);
			Log.e("Result", result);
			
			JSONObject jobject = new JSONObject(result);
			JSONArray jsonArray = jobject.getJSONArray("data");
			
			for(int i=0;i<jsonArray.length();i++){
				profileURLs.put(jsonArray.getJSONObject(i).getString("user_id"), jsonArray.getJSONObject(i).getString("profile_image"));
				profileDisplayNames.put(jsonArray.getJSONObject(i).getString("user_id"), jsonArray.getJSONObject(i).getString("display_name"));
			}
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
