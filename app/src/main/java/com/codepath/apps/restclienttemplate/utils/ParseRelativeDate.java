package com.codepath.apps.restclienttemplate.utils;

import android.text.TextUtils;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by traviswkim on 8/3/16.
 */
public class ParseRelativeDate {
    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static String getRelativeTime(String rawJsonDate){
        String relDate = getRelativeTimeAgo(rawJsonDate);
        if(!TextUtils.isEmpty(relDate)){
            String[] sptRelDate = relDate.split(" ");
            if(sptRelDate.length >= 2){
                return String.format("%s %s", sptRelDate[0], sptRelDate[1]);
            }else{
                return relDate;
            }
        }else{
            return "";
        }

    }
}
