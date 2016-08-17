package com.barter.xmpp;

import java.io.Serializable;

import android.graphics.Bitmap;

public class HBUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private boolean isOnline;
	private Bitmap avatar;
	private String jid;
	private String email;
	private String userName;
	private String recentMessage;

	public String getRecentMessage() {
		return recentMessage;
	}

	public void setRecentMessage(String recentMessage) {
		this.recentMessage = recentMessage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public Bitmap getAvatar() {
		return avatar;
	}

	public void setAvatar(Bitmap avatar) {
		this.avatar = avatar;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
