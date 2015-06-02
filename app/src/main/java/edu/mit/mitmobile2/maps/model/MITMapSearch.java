package edu.mit.mitmobile2.maps.model;

import java.util.Date;

/**
 * Created by serg on 5/27/15.
 */
public class MITMapSearch {

    private String token;
    private String searchTerm;
    private Date date;
    private MITMapPlace place;
    private MITMapCategory category;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MITMapPlace getPlace() {
        return place;
    }

    public void setPlace(MITMapPlace place) {
        this.place = place;
    }

    public MITMapCategory getCategory() {
        return category;
    }

    public void setCategory(MITMapCategory category) {
        this.category = category;
    }
}
