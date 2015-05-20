package edu.mit.mitmobile2.libraries.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesCitation {

    private String name;
    private String citation;

    public MITLibrariesCitation(String name, String citation) {
        this.name = name;
        this.citation = citation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }
}
