package edu.mit.mitmobile2.tour.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MITTourStopsWrapper {

    @Expose
    private String title;
    @SerializedName("body_html")
    @Expose
    private String bodyHtml;
    @Expose
    private String id;
    @Expose
    private String type;

    private MITTourStopImage bigImage;

    private MITTourStopImage smallImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public MITTourStopImage getBigImage() {
        return bigImage;
    }

    public void setBigImage(MITTourStopImage bigImage) {
        this.bigImage = bigImage;
    }

    public MITTourStopImage getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(MITTourStopImage smallImage) {
        this.smallImage = smallImage;
    }
}