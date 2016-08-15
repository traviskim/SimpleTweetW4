package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.ProfileActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;
import com.codepath.apps.restclienttemplate.utils.PatternEditableBuilder;

import org.parceler.Parcels;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by traviswkim on 8/3/16.
 */
public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvUserName) TextView tvUserame;
        @BindView(R.id.tvCreatedAt) TextView tvCreatedAt;
        @BindView(R.id.tvBody) TextView tvBody;

        public ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    private List<Tweet> mTweets;
    private Context context;
    public TweetAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.mTweets = tweets;
    }

    public Context getContext(){
        return this.context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetAdapter.ViewHolder holder, int position) {
        final Tweet tweet = mTweets.get(position);
        holder.ivProfileImage.setImageResource(android.R.color.transparent);
        if(tweet.getUser() != null) {
            Glide.with(getContext()).load(tweet.getUser()
                    .getProfileImage())
                    .bitmapTransform(new RoundedCornersTransformation(getContext(), 5, 3))
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivProfileImage);
            holder.tvName.setText(tweet.getUser().getName());
            holder.tvUserame.setText("@"+tweet.getUser().getScreenName());
        }
        holder.tvBody.setText(tweet.getBody());
        holder.tvCreatedAt.setText(ParseRelativeDate.getRelativeTimeAgo(tweet.getCreatedAt()));
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                String userScreenName = text.replace("@", "");
                                User user = new User();
                                user.setUid(0);
                                user.setScreenName(userScreenName);
                                Intent i = new Intent(context, ProfileActivity.class);
                                i.putExtra("user", Parcels.wrap(user));
                                context.startActivity(i);
                            }
                        }).into(holder.tvBody);
        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("user", Parcels.wrap(tweet.getUser()));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
    @OnClick(R.id.ivProfileImage)
    public void openProfile(View v) {
//        Intent i = new Intent(context, ProfileActivity.class);
//        User user = new User();
//        i.putExtra("screen_name", Parcels.wrap(user));
//        context.startActivity(i);
        Toast.makeText(context, "Profile Image clicked", Toast.LENGTH_SHORT).show();
    }

}
