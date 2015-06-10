package edu.mit.mitmobile2.facilities.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by serg on 6/10/15.
 */
public class FacilitiesLocation {

    private String number;
    private String uid;
    private double longitude;
    private double latitude;
    private Date roomsUpdated;
    private String name;
    private boolean isHiddenInBldgServices;                 // type = ?
    private boolean isLeased;                               // type = ?
    private HashSet<FacilitiesCategory> categories;
    private HashSet<FacilitiesContent> contents;
    private FacilitiesPropertyOwner propertyOwner;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getRoomsUpdated() {
        return roomsUpdated;
    }

    public void setRoomsUpdated(Date roomsUpdated) {
        this.roomsUpdated = roomsUpdated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHiddenInBldgServices() {
        return isHiddenInBldgServices;
    }

    public void setIsHiddenInBldgServices(boolean isHiddenInBldgServices) {
        this.isHiddenInBldgServices = isHiddenInBldgServices;
    }

    public boolean isLeased() {
        return isLeased;
    }

    public void setIsLeased(boolean isLeased) {
        this.isLeased = isLeased;
    }

    public HashSet<FacilitiesCategory> getCategories() {
        return categories;
    }

    public void setCategories(HashSet<FacilitiesCategory> categories) {
        this.categories = categories;
    }

    public HashSet<FacilitiesContent> getContents() {
        return contents;
    }

    public void setContents(HashSet<FacilitiesContent> contents) {
        this.contents = contents;
    }

    public FacilitiesPropertyOwner getPropertyOwner() {
        return propertyOwner;
    }

    public void setPropertyOwner(FacilitiesPropertyOwner propertyOwner) {
        this.propertyOwner = propertyOwner;
    }
}
