package edu.mit.mitmobile2.shuttles.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MITShuttleVehicles {

    @Expose
    private String id;
    @Expose
    private Double lat;
    @Expose
    private Double lon;
    @Expose
    private Integer heading;
    @SerializedName("speed_kph")
    @Expose
    private Integer speedKph;
    @SerializedName("seconds_since_report")
    @Expose
    private Integer secondsSinceReport;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


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


    public Integer getHeading() {
        return heading;
    }


    public void setHeading(Integer heading) {
        this.heading = heading;
    }


    public Integer getSpeedKph() {
        return speedKph;
    }


    public void setSpeedKph(Integer speedKph) {
        this.speedKph = speedKph;
    }


    public Integer getSecondsSinceReport() {
        return secondsSinceReport;
    }


    public void setSecondsSinceReport(Integer secondsSinceReport) {
        this.secondsSinceReport = secondsSinceReport;
    }

}
