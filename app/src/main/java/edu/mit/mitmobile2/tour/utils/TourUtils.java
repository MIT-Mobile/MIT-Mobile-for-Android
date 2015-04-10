package edu.mit.mitmobile2.tour.utils;

import android.location.Location;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.mit.mitmobile2.BuildConfig;
import edu.mit.mitmobile2.Constants;
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
        List<MITTourStop> mainLoopStops = new ArrayList<>();

        for (MITTourStop stop : tourStops) {
            if (stop.getType().equals(Constants.Tours.MAIN_LOOP)) {
                mainLoopStops.add(stop);
            }
        }

        mainLoopStops = setStopNumber(mainLoopStops);

        return mainLoopStops;
    }

    public static List<MITTourStop> getNearHereStops(List<MITTourStop> tourStops, MITTourStop currentTourStop) {
        List<MITTourStop> nearHereStops = new ArrayList<>();

        for (MITTourStop stop : tourStops) {
            nearHereStops.add(stop);
        }

        nearHereStops = setStopNumber(nearHereStops);

        Iterator<MITTourStop> iterator = nearHereStops.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(currentTourStop.getId())) {
                iterator.remove();
            }
        }

        Location currentstopLocation = new Location("currentStopLocation");
        currentstopLocation.setLongitude(currentTourStop.getCoordinates()[0]);
        currentstopLocation.setLatitude(currentTourStop.getCoordinates()[1]);

        HashMap<MITTourStop, Float> stopsHashMap = new HashMap<>();
        for (MITTourStop stop : nearHereStops) {
            Location stopLocation = new Location("stopLocation");
            stopLocation.setLongitude(stop.getCoordinates()[0]);
            stopLocation.setLatitude(stop.getCoordinates()[1]);
            float distance = currentstopLocation.distanceTo(stopLocation);
            stopsHashMap.put(stop, distance);
        }

        nearHereStops = sortNearHereStops(stopsHashMap);

        return nearHereStops;
    }

    public static List<MITTourStop> sortNearHereStops(HashMap<MITTourStop, Float> stopsHashMap) {
        List list = new LinkedList(stopsHashMap.entrySet());
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return ((Comparable) ((Map.Entry) (lhs)).getValue())
                        .compareTo(((Map.Entry) (rhs)).getValue());
            }
        });

        HashMap sortedStopsHashMap = new LinkedHashMap();
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            sortedStopsHashMap.put(entry.getKey(), entry.getValue());
        }

        return new ArrayList<MITTourStop>(sortedStopsHashMap.keySet());
    }

    public static List<MITTourStop> setStopNumber(List<MITTourStop> tourStops) {
        for (int i = 0; i < tourStops.size(); i++) {
            tourStops.get(i).setIndex(i);
        }
        return tourStops;
    }
}