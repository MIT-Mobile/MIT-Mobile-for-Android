package edu.mit.mitmobile2.tour.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MITTourStop {

    public class MITStopRepresentation {
        @SerializedName("representations")
        @Expose
        List<MITTourStopImage> images;
    }

    @Expose
    private String title;
    @SerializedName("body_html")
    @Expose
    private String bodyHtml;
    @Expose
    private String id;
    @Expose
    private String type;

    @Expose
    private double[] coordinates;

    @SerializedName("images")
    @Expose
    private List<MITStopRepresentation> representations;

    @SerializedName("directions_to_next_stop")
    @Expose
    MITTourStopDirection direction;

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
        return representations.get(0).images.get(0);
    }

    public MITTourStopImage getSmallImage() {
        return representations.get(0).images.get(1);
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public MITTourStopDirection getDirection() {
        return direction;
    }

    public void setDirection(MITTourStopDirection direction) {
        this.direction = direction;
    }

    public List<MITStopRepresentation> getRepresentations() {
        return representations;
    }

    public void setRepresentations(List<MITStopRepresentation> representations) {
        this.representations = representations;
    }
}