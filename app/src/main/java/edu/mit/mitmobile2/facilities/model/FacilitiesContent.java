package edu.mit.mitmobile2.facilities.model;

import java.util.HashSet;
import java.util.List;

/**
 * Created by serg on 6/10/15.
 */
public class FacilitiesContent {

    private List<String> altname;
    private String name;
    private String url;
    private HashSet<FacilitiesCategory> categories;
    private FacilitiesLocation location;

    public List<String> getAltname() {
        return altname;
    }

    public void setAltname(List<String> altname) {
        this.altname = altname;
    }

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

    public HashSet<FacilitiesCategory> getCategories() {
        return categories;
    }

    public void setCategories(HashSet<FacilitiesCategory> categories) {
        this.categories = categories;
    }

    public FacilitiesLocation getLocation() {
        return location;
    }

    public void setLocation(FacilitiesLocation location) {
        this.location = location;
    }
}
