package com.barter.xmpp;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smackx.ping.packet.Ping;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.hb.barter.LoadingScreen;
import com.hb.barter.MainActivity;
import com.hb.barter.R;
import com.hb.baseapplication.BaseApplication;
import com.hb.webserviceutilities.Webservices;

@SuppressLint({ "HandlerLeak", "SimpleDateFormat" })
public class ConnectionService extends Service {

	private HBXMPP hbxmpp;
	private ChatDBHelper chatDBHelper;
	private SharedPreferences userDetails;
	private ContentResolver resolver;
	private int rid;
	private Boolean isVibrate;
	private BaseApplication xmppDemoApp;

	@Override
	public IBinder onBind(Intent intent) {
		return iBinder;
	}

	private IBinder iBinder = new ContainsLocal();

	public class ContainsLocal extends Binder {
		public ConnectionService getBinder() {
			return ConnectionService.this;
		}
	}

	public static final String HOST = Webservices.HOST;
	public static final int PORT = 5222;
	public static final String SERVICE = Webservices.SERVICE;

	@Override
	public void onCreate() {
		super.onCreate();
		hbxmpp = ((BaseApplication) getApplication()).getHbxmpp();
		xmppDemoApp = (BaseApplication) getApplication();
		userDetails = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		resolver = getContentResolver();
		chatDBHelper = new ChatDBHelper();
		if (hbxmpp != null) {
			Log.i("Service", "Started");
			try {
				Presence presence = new Presence(Presence.Type.available);
				hbxmpp.getConnection().sendPacket(presence);
				setChatListener(hbxmpp.getConnection());
				setPresenceListener(hbxmpp.getConnection());
				addConnectionListener(hbxmpp.getConnection());
				handlePing(hbxmpp.getConnection());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		registerReceiver(networkConnection, new IntentFilter(
				ServiceUtility.NETWORK_CONNECTION));
		registerReceiver(restartListners, new IntentFilter(
				ServiceUtility.RESTART_LISTNERS));

	}

	private BroadcastReceiver networkConnection = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {
			mhHandler.post(new Runnable() {
				@Override
				public void run() {

					SharedPreferences.Editor editor = userDetails.edit();
					editor.putString("ReConnecting", "Yes");
					editor.commit();

					Bundle mBundle = new Bundle();
					mBundle.putBoolean("Connecting", true);
					Intent intent2 = new Intent(ServiceUtility.RE_CONNECTION);
					intent2.putExtras(mBundle);
					context.sendBroadcast(intent2);

					ReconnectingToServer(context);

				}

			});
		}
	};

	private void ReconnectingToServer(final Context context) {

		SharedPreferences.Editor editor = userDetails.edit();
		editor.putString("Connecting", "Yes");
		editor.commit();

		hbxmpp.disconnect();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					hbxmpp = new HBXMPP(HOST, PORT, SERVICE);
					hbxmpp.connect();
					if (hbxmpp.getConnection().isConnected()) {
						try {
							hbxmpp.login(userDetails.getString(
									"Loggedin_jabber_id", ""), userDetails
									.getString("password", ""));
							if (hbxmpp.getConnection().isAuthenticated()) {
								xmppDemoApp.setHbxmpp(hbxmpp);
								Log.e("Logged in user", hbxmpp.getConnection()
										.getUser());

								Presence presence = new Presence(
										Presence.Type.available);
								hbxmpp.getConnection().sendPacket(presence);
								setChatListener(hbxmpp.getConnection());
								setPresenceListener(hbxmpp.getConnection());
								addConnectionListener(hbxmpp.getConnection());
								handlePing(hbxmpp.getConnection());
								Log.i("Service", "Live");

								Bundle mBundle = new Bundle();
								mBundle.putBoolean("Connecting", false);
								Intent intent2 = new Intent(
										ServiceUtility.RE_CONNECTION);
								intent2.putExtras(mBundle);
								context.sendBroadcast(intent2);

								sendAllOfflineMessages(context);

								SharedPreferences.Editor editor = userDetails
										.edit();
								editor.putString("Connecting", "No");
								editor.putString("ReConnecting", "No");
								editor.putString("ReConnectionFailed", "No");
								editor.putBoolean("wifiCame", false);
								editor.putBoolean("NetworkCame", false);
								editor.putBoolean("NetworkLoss", false);
								editor.commit();

							} else {

								xmppDemoApp.setHbxmpp(hbxmpp);
								Log.e("Logged in user", hbxmpp.getConnection()
										.getUser());
								sendAllOfflineMessages(context);
								SharedPreferences.Editor editor = userDetails
										.edit();
								editor.putString("Connecting", "No");
								editor.putString("ReConnectionFailed", "No");
								editor.commit();
							}

						} catch (Exception e) {
							ReconnectingToServer(context);
							e.printStackTrace();
							SharedPreferences.Editor editor = userDetails
									.edit();
							editor.putString("ReConnectionFailed", "Yes");
							editor.commit();
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					ReconnectingToServer(context);
				}

			}
		}).start();
	}

	private List<ChatData> offlineConnectingHistory = new ArrayList<ChatData>();

	private void sendAllOfflineMessages(Context context) {

		try {
			offlineConnectingHistory = chatDBHelper.loadOfflineMessages(
					context, userDetails.getString("Loggedin_jabber_id", "")
							+ Webservices.CHAT_DOMAIN);

			for (int j = 0; j < offlineConnectingHistory.size(); j++) {

				Message msg = new Message(offlineConnectingHistory.get(j)
						.getToId(),

				Message.Type.chat);
				msg.setBody(offlineConnectingHistory.get(j).getOrginalMessage());
				hbxmpp.getConnection().sendPacket(msg);

			}

			chatDBHelper.setasonlineMessages(context,
					userDetails.getString("Loggedin_jabber_id", "")
							+ Webservices.CHAT_DOMAIN);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(restartListners);
		unregisterReceiver(networkConnection);
	}

	private Handler mHandler = new Handler();
	private BroadcastReceiver restartListners = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Log.i("ReconnectedListners", "true");
					try {
						hbxmpp = ((BaseApplication) getApplication())
								.getHbxmpp();
						if (hbxmpp != null) {
							setChatListener(hbxmpp.getConnection());
							setPresenceListener(hbxmpp.getConnection());
							setRosterUpdateListener(hbxmpp.getConnection());
							addConnectionListener(hbxmpp.getConnection());
							handlePing(hbxmpp.getConnection());

							Presence presence = new Presence(
									Presence.Type.available);
							hbxmpp.getConnection().sendPacket(presence);

						}

						registerReceiver(networkConnection, new IntentFilter(
								ServiceUtility.NETWORK_CONNECTION));
					} catch (Exception e) {
						e.printStackTrace();
						registerReceiver(networkConnection, new IntentFilter(
								ServiceUtility.NETWORK_CONNECTION));
					}

				}

			});
		}
	};

	private void addConnectionListener(Connection connection) {
		connection.addConnectionListener(new ConnectionListener() {

			@Override
			public void reconnectionSuccessful() {
				// TODO Auto-generated method stub
				Log.i("XMPPRECONNECTED", "RECONNECTEDSUCCESSFULLY");
			}

			@Override
			public void reconnectionFailed(Exception arg0) {
				// TODO Auto-generated method stub
				Log.i("XMPPRECONNECTED", "RECONNECTIONFAIL");
			}

			@Override
			public void reconnectingIn(int arg0) {
				// TODO Auto-generated method stub
				Log.i("XMPPRECONNECTED", "RECONNECTING");
			}

			@Override
			public void connectionClosedOnError(Exception arg0) {
				// TODO Auto-generated method stub
				Log.i("XMPPCONNECTIONLOST", "XMPPCONNECTIONLOSTONERROR");
				Intent serverIntent = new Intent(
						ServiceUtility.SERVER_CONNECTION);
				getApplication().sendBroadcast(serverIntent);
			}

			@Override
			public void connectionClosed() {
				// TODO Auto-generated method stub
				Log.i("XMPPCONNECTIONLOST", "XMPPCONNECTIONCLOSED");
				Intent serverIntent = new Intent(
						ServiceUtility.SERVER_CONNECTION_CLOSED);
				getApplication().sendBroadcast(serverIntent);
			}
		});

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		setInterval();
		return START_STICKY;
	}

	@SuppressLint("SimpleDateFormat")
	private void setChatListener(Connection connection) {
		PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
		connection.addPacketListener(new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				Message message = (Message) packet;
				if (message.getBody() != null) {
					String fromName = StringUtils.parseBareAddress(message
							.getFrom());
					Log.i("ConnectionService",
							"Text Recieved " + message.getBody() + " from "
									+ fromName);
					boolean isFile = false, isEndedMentor = false;
					if (message.getBody().contains("##@!@##IMAGE")
							|| message.getBody().contains("##@!@##VIDEO")) {
						isFile = true;
					}

					if (message.getBody().contains("###ENDMENTORSHIP###")) {
						isEndedMentor = true;
					} else {
						isEndedMentor = false;
					}

					SharedPreferences.Editor editor = userDetails.edit();

					String presentChatDetail = userDetails.getString(
							"chatDetailName", "");
					String isinChat = userDetails.getString("chat", "");

					String[] separated = fromName.split("@");

					Roster roster = hbxmpp.getConnection().getRoster();
					String frndName = roster.getEntry(fromName).getName();

					int unreadCount = chatDBHelper.getUnreadMessageCount(
							getApplicationContext(), separated[0],
							userDetails.getString("Loggedin_jabber_id", ""));
					String localTime = new String();

					DelayInformation delay = (DelayInformation) message
							.getExtension("x", "jabber:x:delay");

					long millis = Calendar.getInstance().getTimeInMillis();

					if (delay != null) {
						millis = delay.getStamp().getTime();
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(Long.valueOf(millis));
						DateFormatSymbols symbols = new DateFormatSymbols();
						symbols.setAmPmStrings(new String[] { "AM", "PM" });
						SimpleDateFormat startDate = new SimpleDateFormat(
								"dd MMMM yyyy hh:mm a", Locale.ENGLISH);
						startDate.setDateFormatSymbols(symbols);
						localTime = startDate.format(c.getTime());
					} else {
						Calendar cal = Calendar.getInstance();
						Date currentLocalTime = cal.getTime();
						DateFormat date = new SimpleDateFormat(
								"dd MMMM yyyy hh:mm a", Locale.ENGLISH);
						localTime = date.format(currentLocalTime);
					}

					ChatData chatInfoUpdated;

					if (isinChat.equalsIgnoreCase("yes")) {

						chatInfoUpdated = new ChatData();
						chatInfoUpdated.setFromId(fromName);
						chatInfoUpdated.setToId(userDetails.getString(
								"Loggedin_jabber_id", "")
								+ Webservices.CHAT_DOMAIN);
						chatInfoUpdated.setOrginalMessage(message.getBody());
						chatInfoUpdated.setUserId(userDetails.getString(
								"Loggedin_jabber_id", ""));
						chatInfoUpdated.setFriendId(separated[0]);
						chatInfoUpdated.setChatMessageDatetime(localTime);
						chatInfoUpdated.setChatFriendName(frndName);
						if (presentChatDetail.equalsIgnoreCase(fromName))
							chatInfoUpdated.setUnreadMessageCount(0);
						else
							chatInfoUpdated
									.setUnreadMessageCount(unreadCount + 1);
						chatInfoUpdated.setFriendStatus("online");
						chatInfoUpdated.setMediaStatus("notdownloaded");
						if (isFile) {
							String str = message.getBody();
							final String seperatedString[] = str
									.split("##@!@##");
							chatInfoUpdated
									.setMediaShortName(seperatedString[1]);
						} else {
							chatInfoUpdated.setMediaShortName("Text");
						}
						chatDBHelper.AddChatMessage(resolver, chatInfoUpdated);

					} else {

						chatInfoUpdated = new ChatData();
						chatInfoUpdated.setFromId(fromName);
						chatInfoUpdated.setToId(userDetails.getString(
								"Loggedin_jabber_id", "")
								+ Webservices.CHAT_DOMAIN);
						chatInfoUpdated.setOrginalMessage(message.getBody());
						chatInfoUpdated.setUserId(userDetails.getString(
								"Loggedin_jabber_id", ""));
						chatInfoUpdated.setFriendId(separated[0]);
						chatInfoUpdated.setChatMessageDatetime(localTime);
						chatInfoUpdated.setChatFriendName(frndName);
						chatInfoUpdated.setUnreadMessageCount(unreadCount + 1);
						chatInfoUpdated.setFriendStatus("online");
						chatInfoUpdated.setMediaStatus("notdownloaded");
						if (isFile) {
							String str = message.getBody();
							final String seperatedString[] = str
									.split("##@!@##");
							chatInfoUpdated
									.setMediaShortName(seperatedString[1]);
						} else {
							chatInfoUpdated.setMediaShortName("Text");
						}
						chatDBHelper.AddChatMessage(resolver, chatInfoUpdated);

					}

					if (isinChat.equalsIgnoreCase("yes")) {
						if (presentChatDetail.equalsIgnoreCase(fromName)) {

						} else {
							if (isEndedMentor)
								chatDBHelper.cleartheuserChatHistory(
										ConnectionService.this, separated[0]);
						}
					} else {
						if (isEndedMentor)
							chatDBHelper.cleartheuserChatHistory(
									ConnectionService.this, separated[0]);
					}

					if (isinChat.equalsIgnoreCase("yes")) {

						if (presentChatDetail.equalsIgnoreCase(fromName)) {
							editor.putString("refreshview", "yes");
							editor.commit();
						} else {
							editor.putString("refreshview", "no");
							editor.commit();
							if (!isEndedMentor) {
								generateNotification(getApplicationContext(),
										message, "", separated[0]);
							} else {
								NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
								notificationManager.cancelAll();
							}
						}

					} else {
						try {
							if (!isEndedMentor) {
								generateNotification(getApplicationContext(),
										message, "", separated[0]);
							} else {
								NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
								notificationManager.cancelAll();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					Bundle mBundle = new Bundle();
					String msg=chatInfoUpdated.getOrginalMessage();
					mBundle.putSerializable("MessageData", chatInfoUpdated);

					Intent intent2 = new Intent(
							ServiceUtility.MESSAGE_UPDATE_ACTION);
					intent2.putExtra("EndMentor", isEndedMentor);
					intent2.putExtras(mBundle);
					getApplication().sendBroadcast(intent2);
				}
			}
		}, filter);
	}

	@SuppressWarnings("deprecation")
	protected void generateBackGroundNotification(Context context,
			Message message, String frndName) {

		String fromName = StringUtils.parseBareAddress(message.getFrom());
		String[] separated = fromName.split("@");
		String incomingMessage = frndName + " wrotes: " + message.getBody();
		int icon = R.drawable.logo_1;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String[] mFinalMsg;
		if(incomingMessage.contains("##@!@##")){
			mFinalMsg=incomingMessage.split("##@!@##");
			incomingMessage=mFinalMsg[0]+mFinalMsg[3]+mFinalMsg[4];
		}
		
		Notification notification = new Notification(icon, incomingMessage,
				when);
		String title = context.getString(R.string.app_name);

		Bundle mbBundle = new Bundle();
		Intent notificationIntent = new Intent(context, LoadingScreen.class);
		// set intent so it does not start a new activity
		mbBundle.putString("frndName", frndName);
		mbBundle.putString("friendUserName", separated[0]);
		notificationIntent.putExtras(mbBundle);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification
				.setLatestEventInfo(context, title, incomingMessage, intent);
		// notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		notification.flags = Notification.FLAG_AUTO_CANCEL;// Notification.DEFAULT_LIGHTS
		notification.number += 1;

		// notification.defaults = Notification.DEFAULT_SOUND;
		// NOtification sound
		rid = userDetails.getInt("saved_ringtone_rid", 1);
		isVibrate = userDetails.getBoolean("isVibrate", false);

		if (rid == 10101 && !isVibrate) {
			//
			notification.defaults = Notification.DEFAULT_VIBRATE;
		} else if (!isVibrate && rid != 10101) {
			notification.sound = Uri.parse("android.resource://"
					+ getPackageName() + "/" + rid);

			notification.defaults = Notification.DEFAULT_VIBRATE;
		} else {
			notification.sound = Uri.parse("android.resource://"
					+ getPackageName() + "/" + rid);
		}

		notificationManager.notify(0, notification);

	}

	@SuppressWarnings("deprecation")
	private void generateNotification(final Context context, Message mMessage,
			String frndName, String fromuserJabberId) {

		String friendName = chatDBHelper.getFriendName(context,
				fromuserJabberId);

		String message = mMessage.getBody();
		String fromName = StringUtils.parseBareAddress(mMessage.getFrom());
		String[] separated = fromName.split("@");
		int icon = R.drawable.logo_1;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String[] mFinalMsg;
		if(message.contains("##@!@##")){
			mFinalMsg=message.split("##@!@##");
			message=mFinalMsg[0]+mFinalMsg[3]+mFinalMsg[4];
		}
		Notification notification = new Notification(icon, message, when);
//		String title = friendName;
		String title = context.getString(R.string.app_name);
		SharedPreferences.Editor editor = userDetails.edit();
		editor.putString("from_userid", fromuserJabberId);
		editor.commit();

		Bundle mbBundle = new Bundle();
		Intent notificationIntent = new Intent(ConnectionService.this,
				MainActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mbBundle.putString("isNotification", "yes");
		mbBundle.putString("fromuser_jabberid", fromuserJabberId);
		notificationIntent.putExtras(mbBundle);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		// Notification notification = new Notification.Builder(this)
		// .setContentTitle(title).setContentText(message)
		// .setContentIntent(intent).setSmallIcon(icon).build();

		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.number += 1;
		notification.defaults = Notification.DEFAULT_SOUND;
		notificationManager.notify(0, notification);
	}

	private void setPresenceListener(XMPPConnection connection) {

		PacketFilter mPacketFilter = new PacketTypeFilter(Presence.class);
		connection.addPacketListener(new PacketListener() {

			@Override
			public void processPacket(Packet paramPacket) {

				Presence presence = (Presence) paramPacket;
				String from = presence.getFrom();
				String[] fromUser = from.split("/");
				boolean isAvailable = presence.isAvailable();
				Log.d("Presecnce updated", "Presecnceupdated");

				List<ChatData> chatHistory = chatDBHelper.loadAllRecentChats(
						getApplicationContext(),
						userDetails.getString("Loggedin_jabber_id", ""));

				for (int i = 0; i < chatHistory.size(); i++) {
					if (fromUser[0].equalsIgnoreCase(chatHistory.get(i)
							.getFromId())) {
						chatDBHelper.upDateStatus(getApplicationContext(),
								chatHistory.get(i).getFriendId(), chatHistory
										.get(i).getUserId(), isAvailable);
					}
				}

				Intent intent2 = new Intent(
						ServiceUtility.PRESENCE_UPDATE_ACTION);
				getApplication().sendBroadcast(intent2);

			}
		}, mPacketFilter);
	}
	
	
	private void setRosterUpdateListener(XMPPConnection connection) {
		connection.getRoster().addRosterListener(new RosterListener() {

            @Override
            public void presenceChanged(Presence arg0) {

            }

            @Override
            public void entriesUpdated(Collection<String> arg0) {
            	Log.e("ConnectionService", "entries updated");
            }

            @Override
            public void entriesDeleted(Collection<String> arg0) {

            }

            @Override
            public void entriesAdded(Collection<String> arg0) {

            }
        });
		
	}
	

	private Handler mhHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			setInterval();
		};
	};

	public void setInterval() {
		mhHandler.removeMessages(1);
		mhHandler.sendEmptyMessageDelayed(1, 1 * 1 * 6000);

		Intent intent2 = new Intent(ServiceUtility.SERVICE_UPDATE_ACTION);
		getApplication().sendBroadcast(intent2);
	}

	class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Context... params) {
			final Context context = params[0].getApplicationContext();
			return isAppOnForeground(context);
		}

		private boolean isAppOnForeground(Context context) {
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> appProcesses = activityManager
					.getRunningAppProcesses();
			if (appProcesses == null) {
				return false;
			}
			final String packageName = context.getPackageName();
			for (RunningAppProcessInfo appProcess : appProcesses) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
						&& appProcess.processName.equals(packageName)) {
					return true;
				}
			}
			return false;
		}
	}

	private PacketListener mPongListener;

	private void handlePing(final XMPPConnection mXMPPConnection) {
		mPongListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				if (packet == null)
					return;
				Log.e("GOT PING ", packet.getPacketID());
				gotServerPong(mXMPPConnection, packet.getPacketID());
			}

		};
		mXMPPConnection.addPacketListener(mPongListener, new PacketTypeFilter(
				IQ.class));
	}

	private String mPingID;

	private void gotServerPong(XMPPConnection mXMPPConnection, String pongID) {
		if (!TextUtils.isEmpty(pongID)) {
			if (mXMPPConnection == null || mXMPPConnection.isAuthenticated()) {
				// setPresenceListener(mXMPPConnection, Type.available,
				// Mode.available);
				Ping ping = new Ping();
				ping.setType(org.jivesoftware.smack.packet.IQ.Type.GET);
				ping.setTo(hbxmpp.getService());
				// mPingID = ping.getPacketID();
				ping.setPacketID(pongID);
				// Log.e("SENT ", " " + pongID);
				mXMPPConnection.sendPacket(ping);
				return;
			}
		} else {
			if (mXMPPConnection == null || !mXMPPConnection.isAuthenticated()) {
				// setPresenceListener(mXMPPConnection, Type.unavailable, null);
				return;
			}
		}
	}

}
