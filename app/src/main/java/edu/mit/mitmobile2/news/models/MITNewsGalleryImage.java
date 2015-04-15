package edu.mit.mitmobile2.news.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import edu.mit.mitmobile2.tour.model.MITRepresentation;

public class MITNewsGalleryImage {

    @Expose
    private String description;
    @Expose
    private String credits;
    @Expose
    private List<MITRepresentation> representations = new ArrayList<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public List<MITRepresentation> getRepresentations() {
        return representations;
    }

    public void setRepresentations(List<MITRepresentation> representations) {
        this.representations = representations;
    }

}