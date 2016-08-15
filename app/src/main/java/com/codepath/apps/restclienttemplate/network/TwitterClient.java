package com.codepath.apps.restclienttemplate.network;

import android.content.Context;
import android.text.TextUtils;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "JXCAGgUBlo3fAJKNOyrKPNmeG";       // Change this
	public static final String REST_CONSUMER_SECRET = "d1Rq6KHLwXh2BPYY5ZnItM5UyqzbBQyKKYaNlbzyHhyUTWPdvN"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://simpletweets"; // Change this (here and in manifest)
	private long sinceId = 1;
	private long maxId = 0;
	private int count = 25;
	private Tweet aTweet = new Tweet();

	public void setSinceId(long sinceId) {
		this.sinceId = sinceId;
	}

	public void setMaxId(long maxId){
		this.maxId = maxId;
	}

	public void setTweet(Tweet aTweet){
		this.aTweet = aTweet;
	}

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
//	public void getInterestingnessList(AsyncHttpResponseHandler handler) {
//		String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
//		// Can specify query string params directly or through RequestParams.
//		RequestParams params = new RequestParams();
//		params.put("format", "json");
//		client.get(apiUrl, params, handler);
//	}

	public void getHomeTimeline(long sId, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", count);
		params.put("since_id", sId);
		getClient().get(apiUrl, params, handler);
	}

	public void getMentionsTimeline(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", count);
		getClient().get(apiUrl, params, handler);
	}

	public void getMoreHomeTimeline(long mId, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", count);
		params.put("max_id", mId);
		getClient().get(apiUrl, params, handler);
	}

	public void getMoreMentionsTimeline(long mId, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", count);
		params.put("max_id", mId);
		getClient().get(apiUrl, params, handler);
	}

	//Add a tweet
	public void addATweet(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", aTweet.getBody());
		getClient().post(apiUrl, params, handler);
	}

	public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", count);
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);
	}

	public void getMoreUserTimeline(String screenName, long mId, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", count);
		params.put("screen_name", screenName);
		params.put("max_id", mId);
		getClient().get(apiUrl, params, handler);
	}

	public void getUserInfo(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/account/verify_credentials.json");
		getClient().get(apiUrl, null, handler);
	}

	public void getUserShow(long userId, String screenName, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("/users/show.json");
		RequestParams params = new RequestParams();
		if(userId > 0) {
			params.put("user_id", userId);
		}
		if(!TextUtils.isEmpty(screenName)) {
			params.put("screen_name", screenName);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void getDirectMessage(long mId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/direct_messages.json");
		RequestParams params = new RequestParams();
		params.put("count", count);
		if(mId > 0) {
			params.put("max_id", mId);
		}
		getClient().get(apiUrl, params, handler);
	}
	//Compose a tweet
	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}