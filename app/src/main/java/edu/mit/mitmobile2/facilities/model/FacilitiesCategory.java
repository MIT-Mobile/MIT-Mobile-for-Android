package edu.mit.mitmobile2.facilities.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;


public class FacilitiesCategory {

    private String uid;
    private String name;
    private HashSet<String> locationIds;
    private Date lastUpdated;
    private HashSet<FacilitiesCategory> subcategories;
    private HashSet<String> locations;
    private FacilitiesCategory parent;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<String> getLocationIds() {
        return locationIds;
    }

    public void setLocationIds(HashSet<String> locationIds) {
        this.locationIds = locationIds;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public HashSet<FacilitiesCategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(HashSet<FacilitiesCategory> subcategories) {
        this.subcategories = subcategories;
    }

    public HashSet<String> getLocations() {
        return locations;
    }

    public void setLocations(HashSet<String> locations) {
        this.locations = locations;
    }

    public FacilitiesCategory getParent() {
        return parent;
    }

    public void setParent(FacilitiesCategory parent) {
        this.parent = parent;
    }
}
