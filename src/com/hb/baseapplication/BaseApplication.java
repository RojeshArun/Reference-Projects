package com.hb.baseapplication;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.android.volley.examples.toolbox.MyVolley;
import com.barter.xmpp.HBXMPP;

public class BaseApplication extends Application {

	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		super.onCreate();
		MyVolley.init(this);
		if (Config.DEVELOPER_MODE
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyDeath().build());
		}
	}

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	private HBXMPP hbxmpp;

	public HBXMPP getHbxmpp() {
		return hbxmpp;
	}

	public void setHbxmpp(HBXMPP hbxmpp) {
		this.hbxmpp = hbxmpp;
	}

}
