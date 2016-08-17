package com.barter.facebook;

public abstract class FacebookCallbackListner {
	public abstract void success(boolean success,Facebook mFaceBook);

	public abstract void response(String response);

}
