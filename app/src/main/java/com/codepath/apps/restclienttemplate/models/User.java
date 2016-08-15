package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by traviswkim on 8/2/16.
 */
@Parcel
public class User {
    private String name;
    private long uid;
    private String screenName;
    private String profileImage;
    private String tagline;
    private String favouritesCount;
    private String followersCount;
    private String friendsCount;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getFavouritesCount() {
        return favouritesCount;
    }

    public String getFollowersCount() {
        return followersCount;
    }

    public String getFriendsCount() {
        return friendsCount;
    }

    public String getTagline() {
        return tagline;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public static User fromJson(JSONObject json){
        User u = new User();
        try {
            u.name = json.getString("name");
            u.uid = json.getLong(("id"));
            u.screenName = json.getString("screen_name");
            u.profileImage = json.getString("profile_image_url");
            u.tagline = json.getString("description");
            u.favouritesCount = json.getString("favourites_count");
            u.followersCount = json.getString("followers_count");
            u.friendsCount = json.getString("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }
}
