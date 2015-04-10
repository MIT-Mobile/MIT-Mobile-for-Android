package edu.mit.mitmobile2.tour.utils;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import edu.mit.mitmobile2.BuildConfig;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.tour.model.MITTour;
import edu.mit.mitmobile2.tour.model.MITTourStop;

public class TourUtils {
    public static final int NUMBER_OF_TOUR_LOOP = 100;

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

    public static List<MITTourStop> getMainLoopStops(List<MITTourStop> tourStops) {
        Iterator<MITTourStop> iterator = tourStops.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().getType().equals(Constants.Tours.MAIN_LOOP)) {
                iterator.remove();
            }
        }
        return tourStops;
    }
}
