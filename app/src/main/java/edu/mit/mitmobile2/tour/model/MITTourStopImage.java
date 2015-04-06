package edu.mit.mitmobile2.tour.model;

import com.google.gson.annotations.Expose;

public class MITTourStopImage {
    @Expose
    private String url;
    @Expose
    private Integer width;
    @Expose
    private Integer height;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
