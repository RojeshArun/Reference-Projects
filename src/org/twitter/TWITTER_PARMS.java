package org.twitter;

public enum TWITTER_PARMS {

	// VERSION
	VERSION_NONE(""), VERSION1_1("1.1"), VERSION2("2"),

	// CATEGORY
	OAUTH("oauth"), ACCOUNT("account"), STATUSES("statuses"), FRIENDS("friends"), FOLLOWERS(
			"followers"), FRIENDSHIPS("friendships"), USERS("users"), SEARCH(
			"search"),

	// METHOD
	ACCESS_TOKEN("access_token"), REQUEST_TOKEN("request_token"), UPDATE(
			"update.json"), UPLOAD("upload.json"), VERIFY_CREDENTIALS(
			"verify_credentials.json"), IDS("ids.json"), LIST("list.json"), UPDATE_PROFILE(
			"update_profile.json"), UPDATE_WITH_MEDIA("update_with_media.json"), DESTROY(
			"destroy.json"), CREATE("create.json"), LOOKUP("lookup.json"), USER_TIMELINE(
			"user_timeline.json"), TWEETS("tweets.json"),

	// HTTP PARAM TYPE
	GET("get"), POST("post"),

	// TASK TYPE
	SESSION("SESSION"), QUERY("QUERY");

	private TWITTER_PARMS(String value) {
		this.value = value;
	}

	public String value;
}
