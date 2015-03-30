package edu.mit.mitmobile2.shuttles.callbacks;

public interface ShuttleAdapterCallback {
    public void shuttleRouteClick(String routeId);

    public void shuttleStopClick(String routeId, String stopId);
}
