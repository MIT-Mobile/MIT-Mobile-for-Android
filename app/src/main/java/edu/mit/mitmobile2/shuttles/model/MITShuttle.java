package edu.mit.mitmobile2.shuttles.model;

import edu.mit.mitmobile2.Constants;

public class MITShuttle {

    private String firstStopID;
    private String secondStopID;
    private String routeID;
    private String routeName;
    private String firstStopName;
    private String firstMinute;
    private String secondStopName;
    private String secondMinute;
    private boolean isPredicable;
    private boolean isScheduled;


    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setFirstStopName(String firstStopName) {
        this.firstStopName = firstStopName;
    }

    public String getFirstStopName() {
        return firstStopName;
    }

    public void setFirstMinute(String firstMinute) {
        this.firstMinute = firstMinute;
    }

    public String getFirstMinute() {
        return firstMinute;
    }

    public void setSecondStopName(String secondStopName) {
        this.secondStopName = secondStopName;
    }

    public String getSecondStopName() {
        return secondStopName;
    }

    public void setSecondMinute(String secondMinute) {
        this.secondMinute = secondMinute;
    }

    public String getSecondMinute() {
        return secondMinute;
    }

    public void setPredicable(boolean isPredicable) {
        this.isPredicable = isPredicable;
    }

    public boolean isPredicable() {
        return isPredicable;
    }

    public void setFirstStopID(String firstStopID) {
        this.firstStopID = firstStopID;
    }

    public String getFirstStopID() {
        return firstStopID;
    }

    public void setSecondStopID(String secondStopID) {
        this.secondStopID = secondStopID;
    }

    public String getSecondStopID() {
        return secondStopID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getRouteID() {
        return routeID;
    }

    public void setScheduled(boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    public boolean isScheduled() {
        return isScheduled;
    }
}
