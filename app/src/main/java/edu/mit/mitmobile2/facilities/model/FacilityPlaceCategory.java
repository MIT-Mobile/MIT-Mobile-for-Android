package edu.mit.mitmobile2.facilities.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by serg on 6/11/15.
 */
public class FacilityPlaceCategory {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("places_url")
    private String placesUrl;

    @SerializedName("url")
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlacesUrl() {
        return placesUrl;
    }

    public void setPlacesUrl(String placesUrl) {
        this.placesUrl = placesUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
