package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApplication;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.msebera.android.httpclient.Header;


/**
 * Created by traviswkim on 8/6/16.
 */
public class AddTweetDialogFragment extends DialogFragment {
    @BindView(R.id.etBody)
    EditText mBody;
    @BindView(R.id.btSend)
    Button mSend;
    @BindView(R.id.ibClose)
    ImageButton mClose;
    private Unbinder unBinder;
    Tweet aTweet = new Tweet();
    private TwitterClient client;

    public interface AddTweetDialogListener {
        void onFinishInputDialog(@Nullable JSONObject tweetJson);
    }

    public AddTweetDialogFragment(){
    }

    public static AddTweetDialogFragment newInstance(){
        AddTweetDialogFragment frag = new AddTweetDialogFragment();
        Bundle args = new Bundle();
//        args.putString("beginDate", ss.getBeginDate());
//        args.putString("sortOrder", ss.getSortOrder());
//        args.putSerializable("client", client);
//        args.putBooleanArray("newsDeskValues", new boolean[]{ss.isArts(), ss.isFasionStyle(), ss.isSports()});
//        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_tweet, container);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unBinder = ButterKnife.bind(this, view);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // Fetch arguments from bundle
//        final String beginDate = getArguments().getString("beginDate", "");

    }

    @OnClick(R.id.btSend)
    public void addTweet(Button btn){
        final AddTweetDialogListener listener = (AddTweetDialogListener)getActivity();
        client = TwitterApplication.getRestClient();
        aTweet.setBody(mBody.getText().toString());
        client.setTweet(aTweet);
        client.addATweet(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG", json.toString());
                listener.onFinishInputDialog(json);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(errorResponse != null) {
                    Log.d("DEBUG", errorResponse.toString());
                    listener.onFinishInputDialog(null);
                }
            }
        });
        dismiss();
    }

    @OnClick(R.id.ibClose)
    public void closeFragment(ImageButton btn){
        dismiss();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unBinder.unbind();
    }
}
