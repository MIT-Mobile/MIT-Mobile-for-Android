package edu.mit.mitmobile2.shuttles;

public interface ShuttleAdapterCallback {
    public void shuttleRouteClick(String routeId);

    public void shuttleStopClick(String routeId, String stopId);
}
