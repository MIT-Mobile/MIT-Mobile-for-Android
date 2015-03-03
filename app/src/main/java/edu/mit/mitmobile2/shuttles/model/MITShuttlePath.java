package edu.mit.mitmobile2.shuttles.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;


public class MITShuttlePath {

    @Expose
    private List<Double> bbox = new ArrayList<Double>();
    @Expose
    private List<List<List<Double>>> segments = new ArrayList<List<List<Double>>>();


    public List<Double> getBbox() {
        return bbox;
    }


    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }


    public List<List<List<Double>>> getSegments() {
        return segments;
    }


    public void setSegments(List<List<List<Double>>> segments) {
        this.segments = segments;
    }

}