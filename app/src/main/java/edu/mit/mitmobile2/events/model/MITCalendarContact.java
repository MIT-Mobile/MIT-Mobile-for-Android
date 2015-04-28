package edu.mit.mitmobile2.events.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by grmartin on 4/27/15.
 */
public class MITCalendarContact implements Parcelable {
    protected String email;
    protected String location;
    protected String name;
    protected String phone;
    @SerializedName("website_url")
    protected String websiteURL;
    // I have a feeling this might create a cyclic reference situation, commenting out for now.
    // HashSet<MITCalendarEvent> events;


    public MITCalendarContact() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

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

    public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

//    public HashSet<MITCalendarEvent> getEvents() {
//        return events;
//    }
//
//    public void setEvents(HashSet<MITCalendarEvent> events) {
//        this.events = events;
//    }

    @Override
    public String toString() {
        return "MITCalendarContact{" +
            "email='" + email + '\'' +
            ", location='" + location + '\'' +
            ", name='" + name + '\'' +
            ", phone='" + phone + '\'' +
            ", websiteURL='" + websiteURL + '\'' +
            '}';
    }

    protected MITCalendarContact(Parcel in) {
        email = in.readString();
        location = in.readString();
        name = in.readString();
        phone = in.readString();
        websiteURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(location);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(websiteURL);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITCalendarContact> CREATOR = new Parcelable.Creator<MITCalendarContact>() {
        @Override
        public MITCalendarContact createFromParcel(Parcel in) {
            return new MITCalendarContact(in);
        }

        @Override
        public MITCalendarContact[] newArray(int size) {
            return new MITCalendarContact[size];
        }
    };
}
