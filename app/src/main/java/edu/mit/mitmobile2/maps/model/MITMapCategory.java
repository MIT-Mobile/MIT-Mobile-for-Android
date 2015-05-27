package edu.mit.mitmobile2.maps.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

/**
 * Created by serg on 5/27/15.
 */
public class MITMapCategory {

    @SerializedName("id")
    private String identifier;

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;

//    private HashMap<Object, Object> places;
//    private HashMap<Object, Object> placeContents;
//    private HashMap<Object, Object> children;
//    private MITMapCategory parent;
//    private MITMapSearch search;

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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
