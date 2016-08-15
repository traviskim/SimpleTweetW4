package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragments.AddTweetDialogFragment;
import com.codepath.apps.restclienttemplate.fragments.DMTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.HomeTimeLineFragment;
import com.codepath.apps.restclienttemplate.fragments.MentionsTimelineFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.network.TwitterApplication;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TimelineActivity extends AppCompatActivity implements AddTweetDialogFragment.AddTweetDialogListener{
    private ActionBarDrawerToggle drawerToggle;
    private String tabTitle[] = {"Home", "Mention", "DM"};
    TweetsPagerAdapter tpAdapter;
    TwitterClient client;
    User user;
    View headerLayout;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nvView) NavigationView nvDrawer;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.viewpager) ViewPager vpPager;
    @BindView(R.id.tabs) PagerSlidingTabStrip tabStrip;
    @BindView(R.id.fabAdd) FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        client = TwitterApplication.getRestClient();

        //Navigation drawer
        setSupportActionBar(toolbar);
        setupDrawerContent(nvDrawer);

        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        tpAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(tpAdapter);
        tabStrip.setViewPager(vpPager);

        // Inflate the header view at runtime
        headerLayout = nvDrawer.inflateHeaderView(R.layout.nav_header);
        // We can now look up items within the header if needed
//        ImageView ivHeaderPhoto = (ImageView)headerLayout.findViewById(R.id.imageView);
        //Add user profile to drawer header
        getUserInfo();
        setListener();

    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setListener() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAddTweetDialog();
            }
        });
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                getSupportActionBar().setTitle(tabTitle[position]);
                if(position == 0){
                    fabAdd.show();
                }else{
                    fabAdd.hide();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getUserInfo(){
        client.getUserInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = user.fromJson(response);
                getSupportActionBar().setTitle(user.getScreenName());
                ImageView ivHeaderProfile = (ImageView) headerLayout.findViewById(R.id.ivHdrProfile);
                TextView tvHeaderName = (TextView)headerLayout.findViewById(R.id.tvHdrName);
                TextView tvHeaderScreenName = (TextView)headerLayout.findViewById(R.id.tvHdrScreenName);
                TextView tvFollowingCount = (TextView)headerLayout.findViewById(R.id.tvFollowingCount);
                TextView tvFollowersCount = (TextView)headerLayout.findViewById(R.id.tvFollowersCount);
                Glide.with(TimelineActivity.this).load(user.getProfileImage())
                        .bitmapTransform(new RoundedCornersTransformation(TimelineActivity.this, 5, 3))
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivHeaderProfile);
                tvHeaderName.setText(user.getName());
                tvHeaderScreenName.setText("@" + user.getScreenName());
                tvFollowingCount.setText((user.getFriendsCount()));
                tvFollowersCount.setText((user.getFollowersCount()));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(TimelineActivity.this, R.string.fail_to_get_user_profile, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_profile_fragment:
                //fragmentClass = Profile.class;
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("user", Parcels.wrap(user));
                startActivity(intent);
                break;
            case R.id.nav_logout:
                client.clearAccessToken();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
                //fragmentClass = FirstFragment.class;
        }

        try {
            //fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
//        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch(item.getItemId()){
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void showAddTweetDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddTweetDialogFragment tweetDialogFragment = AddTweetDialogFragment.newInstance(user.getProfileImage());
        tweetDialogFragment.show(fm, "Add a Tweet");
    }

    public void onFinishInputDialog(JSONObject json) {
        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
        HomeTimeLineFragment fm = (HomeTimeLineFragment)tpAdapter.getCurrentFragment(0);
        fm.onRefresh();
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter{
        Fragment fragment = null;
        HashMap<Integer, Fragment> hm = new HashMap<>();
        public TweetsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                fragment =  new HomeTimeLineFragment();
                hm.put(0, fragment);
            }else if(position == 1){
                fragment =  new MentionsTimelineFragment();
                hm.put(1, fragment);
            }else if(position == 2){
                fragment = new DMTimelineFragment();
                hm.put(2, fragment);
            }else{
                return null;
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }

        @Override
        public int getCount() {
            return tabTitle.length;
        }

        @Override
        public int getItemPosition (Object object) {
            return POSITION_NONE;
        }

        public Fragment getCurrentFragment(int position){
            return hm.get(position);
        }
    }

}
