package edu.mit.mitmobile2.shuttles.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class MITShuttleRouteWrapper {

    @Expose
    private String id;
    @Expose
    private String url;
    @Expose
    private String title;
    @Expose
    private String agency;
    @Expose
    private Boolean scheduled;
    @Expose
    private Boolean predictable;
    @Expose
    private String description;
    @SerializedName("predictions_url")
    @Expose
    private String predictionsUrl;
    @SerializedName("vehicles_url")
    @Expose
    private String vehiclesUrl;
    @Expose
    private MITShuttlePath path;
    @Expose
    private List<MITShuttleStopWrapper> stops = new ArrayList<MITShuttleStopWrapper>();


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }


    public Boolean getScheduled() {
        return scheduled;
    }


    public void setScheduled(Boolean scheduled) {
        this.scheduled = scheduled;
    }


    public Boolean getPredictable() {
        return predictable;
    }


    public void setPredictable(Boolean predictable) {
        this.predictable = predictable;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getPredictionsUrl() {
        return predictionsUrl;
    }


    public void setPredictionsUrl(String predictionsUrl) {
        this.predictionsUrl = predictionsUrl;
    }


    public String getVehiclesUrl() {
        return vehiclesUrl;
    }


    public void setVehiclesUrl(String vehiclesUrl) {
        this.vehiclesUrl = vehiclesUrl;
    }


    public MITShuttlePath getPath() {
        return path;
    }


    public void setPath(MITShuttlePath path) {
        this.path = path;
    }


    public List<MITShuttleStopWrapper> getStops() {
        return stops;
    }


    public void setStops(List<MITShuttleStopWrapper> stops) {
        this.stops = stops;
    }

}