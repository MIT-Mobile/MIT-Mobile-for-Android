package edu.mit.mitmobile2.facilities.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class FacilityPlace {

    @SerializedName("id")
    private String id;

    @SerializedName("url")
    private String url;

    @SerializedName("maps_places_url")
    private String mapPlacesUrl;

    @SerializedName("categories")
    private List<String> categories;

    @SerializedName("managed_by_facilities")
    private boolean managedByFacilities;

    @SerializedName("contact_name")
    private String contactName;

    @SerializedName("contact_email")
    private String contactEmail;

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

    public String getMapPlacesUrl() {
        return mapPlacesUrl;
    }

    public void setMapPlacesUrl(String mapPlacesUrl) {
        this.mapPlacesUrl = mapPlacesUrl;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public boolean isManagedByFacilities() {
        return managedByFacilities;
    }

    public void setManagedByFacilities(boolean managedByFacilities) {
        this.managedByFacilities = managedByFacilities;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
