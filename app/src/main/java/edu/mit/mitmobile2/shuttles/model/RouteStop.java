package edu.mit.mitmobile2.shuttles.model;


public class RouteStop {

    long routeId;
    long stopId;

    public long getStopId() {
        return stopId;
    }

    public void setStopId(long stopId) {
        this.stopId = stopId;
    }

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }
}
