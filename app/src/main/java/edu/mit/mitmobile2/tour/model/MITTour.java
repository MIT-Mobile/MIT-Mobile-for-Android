package edu.mit.mitmobile2.tour.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MITTour {

    public class MITTourLink {
        @Expose
        String name;
        @Expose
        String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

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
    @SerializedName("description_html")
    @Expose
    private String descriptionHtml;
    @Expose
    private List<MITTourLink> links;
    @Expose
    private List<MITTourStop> stops;


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

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public List<MITTourLink> getLinks() {
        return links;
    }

    public void setLinks(List<MITTourLink> links) {
        this.links = links;
    }

    public List<MITTourStop> getStops() {
        return stops;
    }

    public void setStops(List<MITTourStop> stops) {
        this.stops = stops;
    }
}
