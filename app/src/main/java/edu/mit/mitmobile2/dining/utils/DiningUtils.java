package edu.mit.mitmobile2.dining.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.mit.mitmobile2.shared.logging.LoggingManager;

public class DiningUtils {
    public static Date formatMealTime(String timeString) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            date = format.parse(timeString);
        } catch (ParseException e) {
            LoggingManager.Timber.e(e, "___________DateFormatError___________");
        }
        return date;
    }

    public static int getMenuDietaryFlagImage(Context context, String dietaryFlag) {
        String newFilterString;

        if (dietaryFlag.contains("-")) {
            dietaryFlag.replaceAll("\\s", "-");
        }

        newFilterString = dietaryFlag.replaceAll(" ", "");

        int resId = context.getResources().getIdentifier("dining_" + newFilterString
                        .toLowerCase(), "drawable",
                context.getPackageName());

        return resId;
    }
}
