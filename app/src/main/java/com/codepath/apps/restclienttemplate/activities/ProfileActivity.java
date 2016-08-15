package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.network.TwitterApplication;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {
    TwitterClient client;
    User user;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvProfileFollowers) TextView tvProfileFollowers;
    @BindView(R.id.tvProfileFollowing) TextView tvProfileFollowing;
    @BindView(R.id.tvProfileName) TextView tvProfileName;
    @BindView(R.id.tvProfileScreenName) TextView tvProfileScreenName;
    @BindView(R.id.tvProfileTagline) TextView tvProfileTagline;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        if(user != null){
            client = TwitterApplication.getRestClient();
            client.getUserShow(user.getUid(), user.getScreenName(), new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = user.fromJson(response);
                    if(user != null) {
                        populateProfileHeader(user);
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(ProfileActivity.this, R.string.fail_to_get_user_profile, Toast.LENGTH_SHORT).show();

                }
            });
            populateProfileHeader(user);
        }

        String screenName = user.getScreenName();
        getSupportActionBar().setTitle("@" + screenName);
        if(savedInstanceState == null) {
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    private void populateProfileHeader(User user) {
        if(user == null){
            return;
        }
        Glide.with(this).load(user.getProfileImage())
                .bitmapTransform(new RoundedCornersTransformation(this, 5, 3))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivProfileImage);
        tvProfileName.setText(user.getName());
        tvProfileScreenName.setText("@" + user.getScreenName());
        tvProfileFollowing.setText((user.getFriendsCount()) + " Following");
        tvProfileFollowers.setText((user.getFollowersCount()) + " Followers");
        tvProfileTagline.setText(user.getTagline());
    }

    public void onSubmit(View v) {
        // closes the activity and returns to first screen
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
