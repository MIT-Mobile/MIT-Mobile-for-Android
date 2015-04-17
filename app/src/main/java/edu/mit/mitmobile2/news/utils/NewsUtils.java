package edu.mit.mitmobile2.news.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

public class NewsUtils {
    public static String formatNewsPublishedTime(String publishedTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy");

        Date date = null;
        String formattedDate = null;
        try {
            date = inputFormat.parse(publishedTime);
            formattedDate = outputFormat.format(date);

        } catch (ParseException e) {
            Timber.d("************error************", e);
        }

        return formattedDate;
    }
}
