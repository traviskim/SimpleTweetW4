package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.fragments.AddTweetDialogFragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements AddTweetDialogFragment.AddTweetDialogListener{
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    //private TweetsArrayAdapter aTweets;
    private TweetAdapter tweetAdapter;
    private SwipeRefreshLayout swipeContainer;
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @BindView(R.id.pbLoading) ProgressBar pb;

    private long sId = 1;
    private long mId = 0;
    int countOldTweets = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        //lvTweets = (ListView) findViewById(R.id.lvTweets);
        ButterKnife.bind(this);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(this, tweets);
        rvTweets.setAdapter(tweetAdapter);
        rvTweets.setLayoutManager(mLayoutManager);
        //Singleton client
        client = TwitterApplication.getRestClient();
        client.setSinceId(sId);
        populateTimeline();

        // Add the scroll listener
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Add old tweets
                client.setMaxId(mId);
                loadMoreTimeline();
            }
        });

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Pull new set of tweets
                sId = 1;
                client.setSinceId(sId);
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.miActionButton:
                showAddTweetDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    //Send an API request to get timeline json
    //Fill the list view by creating tweets obj from the json
    private void populateTimeline() {
        if(NetworkUtil.isNetworkConnected(TimelineActivity.this)) {
            pb.setVisibility(ProgressBar.VISIBLE);
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    Log.d("DEBUG", json.toString());
                    //Clean and get new set of tweets
                    tweets.clear();
                    ArrayList<Tweet> newTweets = Tweet.fromJsonArray(json);
                    tweets.addAll(newTweets);
                    tweetAdapter.notifyDataSetChanged();
                    mId = newTweets.get(newTweets.size() - 1).getUid();
                    swipeContainer.setRefreshing(false);
                    pb.setVisibility(ProgressBar.INVISIBLE);
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
                            Toast.makeText(TimelineActivity.this, jArray.getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    pb.setVisibility(ProgressBar.INVISIBLE);
                }
            });
        }
    }

    //Send an API request to get more old tweets json
    //Fill the list view by creating tweets obj from the json
    private void loadMoreTimeline() {
        if(NetworkUtil.isNetworkConnected(TimelineActivity.this)) {
            pb.setVisibility(ProgressBar.VISIBLE);
            client.getOldHomeTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    //Add old tweets to the bottom of Recyclerview
                    countOldTweets = json.length();
                    if (countOldTweets > 0) {
                        ArrayList<Tweet> oldTweets = Tweet.fromJsonArray(json);
                        tweets.addAll(oldTweets);
                        tweetAdapter.notifyItemInserted(json.length() - 1);
                        mId = oldTweets.get(oldTweets.size() - 1).getUid();
                    }
                    pb.setVisibility(ProgressBar.INVISIBLE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    try {
                        //errors":[{"message":"Rate limit exceeded","code":88}
                        JSONArray jArray = errorResponse.getJSONArray("errors");
                        Toast.makeText(TimelineActivity.this, jArray.getJSONObject(0).getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void showAddTweetDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddTweetDialogFragment tweetDialogFragment = AddTweetDialogFragment.newInstance();
        tweetDialogFragment.show(fm, "Add a Tweet");
    }

    public void onFinishInputDialog(JSONObject json) {
        if(json != null){
            //Pull new set of tweets
            sId = 1;
            client.setSinceId(sId);
            populateTimeline();
            Toast.makeText(this, "Success to send a tweet", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Fail to send a tweet", Toast.LENGTH_SHORT).show();
        }
    }

}
