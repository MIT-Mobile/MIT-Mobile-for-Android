package edu.mit.mitmobile2.news.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import edu.mit.mitmobile2.MITImage;

public class MITNewsCoverImage {

    @Expose
    private List<MITImage> representations = new ArrayList<>();

    public List<MITImage> getRepresentations() {
        return representations;
    }

    public void setRepresentations(List<MITImage> representations) {
        this.representations = representations;
    }

}