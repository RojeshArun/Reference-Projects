package com.barter.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.ping.provider.PingProvider;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class HBXMPP {

	private String host;
	private int port;
	private String service;
	private XMPPConnection connection;
	private static final String TAG = HBXMPP.class.getSimpleName();

	public HBXMPP(String host, int port, String service) {
		this.host = host;
		this.port = port;
		this.service = service;
		configure(ProviderManager.getInstance());
	}

	public List<HBUser> serachUser(Connection conn, String username) {

		List<HBUser> users = new ArrayList<HBUser>();

		try {
			UserSearchManager usm = new UserSearchManager(conn);
			Form searchForm = usm.getSearchForm("search.192.168.39.24");
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", username);
			ReportedData data = usm.getSearchResults(answerForm,
					"search.192.168.39.24");
			Iterator<Row> itrator = data.getRows();
			while (itrator.hasNext()) {
				HBUser user = new HBUser();
				ReportedData.Row row = (ReportedData.Row) itrator.next();
				user.setName(row.getValues("Name").next().toString());
				user.setEmail(row.getValues("Email").next().toString());
				user.setUserName(row.getValues("Username").next().toString());
				users.add(user);
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}

		return users;
	}

	public boolean connect() {
		ConnectionConfiguration connConfig = new ConnectionConfiguration(host,
				port, service);
		connection = new XMPPConnection(connConfig);

		try {
			connection.connect();
			return true;
		} catch (XMPPException ex) {
			setConnection(null);
			ex.printStackTrace();
			return false;
		}
	}

	public void disconnect() {
		// TODO Auto-generated method stub
		if (connection != null) {
			if (connection.isConnected())
				connection.disconnect();
			connection = null;
		}
	}

	public Bitmap getFriends(String userID) {

		List<HBUser> listFriend = new ArrayList<HBUser>();
		ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
				new VCardProvider());

		if (connection == null) {

			connect();

		}

		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();

		for (RosterEntry entry : entries) {

			HBUser hbFriend = new HBUser();
			Presence availability = roster.getPresence(entry.getUser());
			int userstatus = retrieveState_mode(availability.getMode(),
					availability.isAvailable());
			if (userstatus == 1) {
				hbFriend.setOnline(true);
			} else {
				hbFriend.setOnline(false);
			}

			VCard card = new VCard();
			try {
				card.load(connection, entry.getUser());
			} catch (Exception e) {
				e.printStackTrace();
			}

			hbFriend.setName(entry.getName());
			hbFriend.setJid(entry.getUser());

			byte[] imgs = card.getAvatar();
			final Bitmap img;
			if (imgs != null) {
				int len = imgs.length;
				img = BitmapFactory.decodeByteArray(imgs, 0, len);
			} else {
				img = null;
			}

			if (userID.equalsIgnoreCase(entry.getUser())) {
				return img;
			}

			listFriend.add(hbFriend);
		}
		return null;
	}

	// public List<HBUser> getFriendImage(String userID) {
	//
	// List<HBUser> listFriend = new ArrayList<HBUser>();
	// ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
	// new VCardProvider());
	//
	// Roster roster = connection.getRoster();
	// Collection<RosterEntry> entries = roster.getEntries();
	//
	// for (RosterEntry entry : entries) {
	//
	// HBUser hbFriend = new HBUser();
	// Presence availability = roster.getPresence(entry.getUser());
	// int userstatus = retrieveState_mode(availability.getMode(),
	// availability.isAvailable());
	// if (userstatus == 1) {
	// hbFriend.setOnline(true);
	// } else {
	// hbFriend.setOnline(false);
	// }
	//
	// VCard card = new VCard();
	// try {
	// card.load(connection, entry.getUser());
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// hbFriend.setName(entry.getName());
	// hbFriend.setJid(entry.getUser());
	//
	// byte[] imgs = card.getAvatar();
	// final Bitmap img;
	// if (imgs != null) {
	// int len = imgs.length;
	// img = BitmapFactory.decodeByteArray(imgs, 0, len);
	// } else {
	// img = null;
	// }
	//
	// listFriend.add(hbFriend);
	// }
	//
	// return listFriend;
	// }

	public static int retrieveState_mode(Mode userMode, boolean isOnline) {
		int userState = 0;
		/** 0 for offline, 1 for online, 2 for away,3 for busy */
		if (userMode == Mode.dnd) {
			userState = 3;
		} else if (userMode == Mode.away || userMode == Mode.xa) {
			userState = 2;
		} else if (isOnline) {
			userState = 1;
		}
		return userState;
	}

	public void login(String userName, String password) {
		try {
			// connection.connect();
			
			
			connection.login(userName, password);

			Log.e(TAG, connection.getUser());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called by Settings dialog when a connection is establised with the XMPP
	 * server
	 * 
	 * @param connection
	 */
	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}

	public XMPPConnection getConnection() {
		return connection;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public void configure(ProviderManager pm) {

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			Log.w("TestClient",
					"Can't load class for org.jivesoftware.smackx.packet.Time");
		}

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());

		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());

		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());

		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());

		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());

		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());

		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());

		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());

		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());

		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}

		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());

		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());

		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());
		pm.addIQProvider("ping", "urn:xmpp:ping", new PingProvider());
		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}

}
