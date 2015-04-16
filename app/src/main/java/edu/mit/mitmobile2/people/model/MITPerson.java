package edu.mit.mitmobile2.people.model;

import java.util.ArrayList;
import java.util.Calendar;

import android.text.TextUtils;

public class MITPerson {
    private String uid;
    private String affiliation;
    private String city;
    private String dept;
    private ArrayList<String> email;
    private ArrayList<String> fax;
    private String givenname;
    private String name;
    private ArrayList<String> office;
    private ArrayList<String> phone;
    private ArrayList<String> home; // homephone
    private String state;
    private String street;
    private String surname;
    private String title;
    private String url;
    private ArrayList<String>  website;
    private Calendar lastUpCalendar;
    private boolean favorite;
    private int favoriteIndex;

    public int getFavoriteIndex() {
        return favoriteIndex;
    }

    public void setFavoriteIndex(int favoriteIndex) {
        this.favoriteIndex = favoriteIndex;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Calendar getLastUpCalendar() {
        return lastUpCalendar;
    }

    public void setLastUpCalendar(Calendar lastUpCalendar) {
        this.lastUpCalendar = lastUpCalendar;
    }

    public ArrayList<String> getWebsite() {
        return website;
    }

    public void setWebsite(ArrayList<String> website) {
        this.website = website;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ArrayList<String> getHome() {
        return home;
    }

    public void setHome(ArrayList<String> home) {
        this.home = home;
    }

    public ArrayList<String> getPhone() {
        return phone;
    }

    public void setPhone(ArrayList<String> phone) {
        this.phone = phone;
    }

    public ArrayList<String> getOffice() {
        return office;
    }

    public void setOffice(ArrayList<String> office) {
        this.office = office;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGivenname() {
        return givenname;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    public ArrayList<String> getFax() {
        return fax;
    }

    public void setFax(ArrayList<String> fax) {
        this.fax = fax;
    }

    public ArrayList<String> getEmail() {
        return email;
    }

    public void setEmail(ArrayList<String> email) {
        this.email = email;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFormattedAddress() {
        if (!TextUtils.isEmpty(this.street) && !TextUtils.isEmpty(this.city) && !TextUtils.isEmpty(this.state)) {
            return String.format("%s\n%s, %s", this.street, this.city, this.state);
        }

        return null;
    }
}
