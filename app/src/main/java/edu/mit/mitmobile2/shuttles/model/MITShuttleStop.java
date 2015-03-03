package edu.mit.mitmobile2.shuttles.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MITShuttleStop {

    @Expose
    private String id;
    @Expose
    private String url;
    @Expose
    private String title;
    @SerializedName("stop_number")
    @Expose
    private String stopNumber;
    @Expose
    private Double lat;
    @Expose
    private Double lon;
    @SerializedName("predictions_url")
    @Expose
    private String predictionsUrl;
    @Expose
    private List<MITShuttlePrediction> predictions = new ArrayList<MITShuttlePrediction>();


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


    public Double getLon() {
        return lon;
    }


    public void setLon(Double lon) {
        this.lon = lon;
    }


    public String getPredictionsUrl() {
        return predictionsUrl;
    }


    public void setPredictionsUrl(String predictionsUrl) {
        this.predictionsUrl = predictionsUrl;
    }


    public List<MITShuttlePrediction> getPredictions() {
        return predictions;
    }


    public void setPredictions(List<MITShuttlePrediction> predictions) {
        this.predictions = predictions;
    }

}