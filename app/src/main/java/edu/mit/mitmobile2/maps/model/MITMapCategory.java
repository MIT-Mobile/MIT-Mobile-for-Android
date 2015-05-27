package edu.mit.mitmobile2.maps.model;

import java.util.HashMap;

/**
 * Created by serg on 5/27/15.
 */
public class MITMapCategory {

    private String name;
    private String url;
    private String identifier;
    private HashMap<Object, Object> places;
    private HashMap<Object, Object> placeContents;
    private HashMap<Object, Object> children;
    private MITMapCategory parent;
    private MITMapSearch search;
}
