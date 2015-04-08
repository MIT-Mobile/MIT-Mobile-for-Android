package edu.mit.mitmobile2.tour.utils;

import java.text.DecimalFormat;

import edu.mit.mitmobile2.BuildConfig;

public class TourUtils {

    public static String formatEstimatedDuration(int time) {
        int hour = time / 60;
        int minute = time % 60;

        return hour + " hour " + minute + " minutes";
    }

    public static String formatDistance(int distance) {
        double mile = (distance / 1.609344);
        DecimalFormat decimalFormat = new DecimalFormat("0.0");

        return decimalFormat.format(mile) + "miles and (" + distance + "km)";
    }

    public static String getBuildDescription() {
        return BuildConfig.buildDescription;
    }

    public static int getAppVersion() {
        return BuildConfig.VERSION_CODE;
    }
}
