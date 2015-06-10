package edu.mit.mitmobile2.facilities.model;

import java.util.HashSet;
import java.util.List;

/**
 * Created by serg on 6/10/15.
 */
public class FacilitiesPropertyOwner {

    private String name;
    private String phone;
    private String email;
    private HashSet<FacilitiesLocation> locations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashSet<FacilitiesLocation> getLocations() {
        return locations;
    }

    public void setLocations(HashSet<FacilitiesLocation> locations) {
        this.locations = locations;
    }
}
