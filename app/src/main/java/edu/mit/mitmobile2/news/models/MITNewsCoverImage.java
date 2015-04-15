package edu.mit.mitmobile2.news.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import edu.mit.mitmobile2.MITRepresentation;

public class MITNewsCoverImage {

    @Expose
    private List<MITRepresentation> representations = new ArrayList<>();

    public List<MITRepresentation> getRepresentations() {
        return representations;
    }

    public void setRepresentations(List<MITRepresentation> representations) {
        this.representations = representations;
    }

}