package edu.mit.mitmobile2.news.models;

import com.google.gson.annotations.Expose;

public class MITNewsCategory {

    @Expose
    private String id;
    @Expose
    private String url;
    @Expose
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}