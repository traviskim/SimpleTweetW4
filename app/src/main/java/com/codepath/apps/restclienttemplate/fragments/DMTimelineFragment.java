package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.network.TwitterApplication;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.NetworkUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by traviswkim on 8/9/16.
 */
public class DMTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    private long mId = 0;
    private int countOldTweets = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Singleton client
        client = TwitterApplication.getRestClient();
        populateTimeline();


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add the scroll listener
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Add old tweets
//                client.setMaxId(mId);
//                loadMoreTimeline();
            }
        });

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Pull new set of tweets
                populateTimeline();
            }
        });
    }

    //Send an API request to get timeline json
    //Fill the list view by creating tweets obj from the json
    private void populateTimeline() {
        if(NetworkUtil.isNetworkConnected(getContext())) {
            client.getDirectMessage(mId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    Log.d("DEBUG", json.toString());
                    //Clean and get new set of tweets
                    //tweets.clear();
                    ArrayList<Tweet> newTweets = Tweet.fromJsonArray(json);
                    if(newTweets.size() > 0) {
                        addAll(newTweets);
                        tweetAdapter.notifyDataSetChanged();
                        mId = newTweets.get(newTweets.size() - 1).getUid();
                    }
                    swipeContainer.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (swipeContainer.isRefreshing()) {
                        swipeContainer.setRefreshing(false);
                    }
                    if (errorResponse != null) {
                        Log.d("DEBUG", errorResponse.toString());
                        try {
                            //errors":[{"message":"Rate limit exceeded","code":88}
                            JSONArray jArray = errorResponse.getJSONArray("errors");
                            Toast.makeText(getActivity(), jArray.getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
