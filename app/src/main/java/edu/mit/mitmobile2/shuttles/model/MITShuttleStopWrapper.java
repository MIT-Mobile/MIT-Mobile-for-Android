package edu.mit.mitmobile2.shuttles.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MITShuttleStopWrapper {

    @Expose
    private Double id;
    @Expose
    private String url;
    @SerializedName("route_id")
    @Expose
    private String routeId;
    @SerializedName("route_url")
    @Expose
    private String routeUrl;
    @Expose
    private String title;
    @SerializedName("stop_number")
    @Expose
    private String stopNumber;
    @Expose
    private Double lat;
    @Expose
    private List<MITShuttlePrediction> predictions = new ArrayList<MITShuttlePrediction>();
    @SerializedName("predictions_url")
    @Expose
    private String predictionsUrl;


    public Double getId() {
        return id;
    }


    public void setId(Double id) {
        this.id = id;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getRouteId() {
        return routeId;
    }


    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }


    public String getRouteUrl() {
        return routeUrl;
    }


    public void setRouteUrl(String routeUrl) {
        this.routeUrl = routeUrl;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getStopNumber() {
        return stopNumber;
    }


    public void setStopNumber(String stopNumber) {
        this.stopNumber = stopNumber;
    }


    public Double getLat() {
        return lat;
    }


    public void setLat(Double lat) {
        this.lat = lat;
    }


    public List<MITShuttlePrediction> getPredictions() {
        return predictions;
    }


    public void setPredictions(List<MITShuttlePrediction> predictions) {
        this.predictions = predictions;
    }


    public String getPredictionsUrl() {
        return predictionsUrl;
    }


    public void setPredictionsUrl(String predictionsUrl) {
        this.predictionsUrl = predictionsUrl;
    }

}