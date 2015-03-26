package edu.mit.mitmobile2.shuttles;

import edu.mit.mitmobile2.shuttles.model.MITShuttlePrediction;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

/**
 * Created by philipcorriveau on 3/25/15.
 */
public class ShuttleUtils {

    public static final String NO_PREDICTION = "-";
    public static final String NOW = "now";
    public static final String MINUTES = "m";

    public static final int SECONDS_PER_MINUTE = 60;

    public static String formatPredictionFromStop(MITShuttleStopWrapper stop) {
        if (stop.getPredictions() != null && stop.getPredictions().size() > 0) {
            return formatPrediction(stop.getPredictions().get(0));
        } else {
            return "-";
        }
    }

    public static String formatPrediction(MITShuttlePrediction prediction) {
        if (prediction != null) {
            int timeInMins = prediction.getSeconds() / SECONDS_PER_MINUTE;
            if (timeInMins == 0) {
                return NOW;
            } else {
                return timeInMins + MINUTES;
            }
        } else {
            return "-";
        }
    }

}
