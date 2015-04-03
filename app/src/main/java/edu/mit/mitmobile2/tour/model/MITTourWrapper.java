package edu.mit.mitmobile2.tour.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MITTourWrapper {

    @Expose
    private String id;
    @Expose
    private String url;
    @Expose
    private String title;
    @SerializedName("short_description")
    @Expose
    private String shortDescription;
    @SerializedName("length_in_km")
    @Expose
    private Integer lengthInKm;
    @SerializedName("estimated_duration_in_minutes")
    @Expose
    private Integer estimatedDurationInMinutes;


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


    public String getShortDescription() {
        return shortDescription;
    }


    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }


    public Integer getLengthInKm() {
        return lengthInKm;
    }


    public void setLengthInKm(Integer lengthInKm) {
        this.lengthInKm = lengthInKm;
    }


    public Integer getEstimatedDurationInMinutes() {
        return estimatedDurationInMinutes;
    }


    public void setEstimatedDurationInMinutes(Integer estimatedDurationInMinutes) {
        this.estimatedDurationInMinutes = estimatedDurationInMinutes;
    }

}
