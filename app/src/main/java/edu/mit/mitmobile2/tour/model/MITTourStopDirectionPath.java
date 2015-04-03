package edu.mit.mitmobile2.tour.model;

import com.google.gson.annotations.Expose;

public class MITTourStopDirectionPath {

    @Expose
    private Double lat;
    @Expose
    private Double lon;


    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
