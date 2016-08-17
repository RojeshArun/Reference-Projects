package org.twitter;

public class TwitterAccessToken {
	private String token, tokenSecret, screeName, userId;

	public TwitterAccessToken(String token, String tokenSecret,
			String screeName, String userId) {
		this.setToken(token);
		this.setTokenSecret(tokenSecret);
		this.setScreeName(screeName);
		this.setUserId(userId);
	}

	/**
	 * @return the token
	 */
	protected String getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	protected void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the tokenSecret
	 */
	protected String getTokenSecret() {
		return tokenSecret;
	}

	/**
	 * @param tokenSecret
	 *            the tokenSecret to set
	 */
	protected void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	protected String getScreeName() {
		return screeName;
	}

	protected void setScreeName(String screeName) {
		this.screeName = screeName;
	}

	protected String getUserId() {
		return userId;
	}

	protected void setUserId(String userId) {
		this.userId = userId;
	}

}