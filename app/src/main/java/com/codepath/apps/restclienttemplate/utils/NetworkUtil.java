package com.codepath.apps.restclienttemplate.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by traviswkim on 7/25/16.
 */
public class NetworkUtil {

    public NetworkUtil(){
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            //Toast.makeText(context, "Internet is available", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            //Toast.makeText(context, "Internet is not available", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
