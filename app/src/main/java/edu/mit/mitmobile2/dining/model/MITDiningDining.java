package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;

import com.google.gson.annotations.SerializedName;


public class MITDiningDining implements Parcelable {
    @SerializedName("announcements_html")
    protected String announcementsHTML;
    protected String url;
    protected HashSet<MITDiningLinks> links;
    protected MITDiningVenues venues;

    public String getAnnouncementsHTML() {
        return announcementsHTML;
    }

    public String getUrl() {
        return url;
    }

    public HashSet<MITDiningLinks> getLinks() {
        return links;
    }

    public MITDiningVenues getVenues() {
        return venues;
    }

    @Override
    public String toString() {
        return "MITDiningDining{" +
                "announcementsHTML='" + announcementsHTML + '\'' +
                ", url='" + url + '\'' +
                ", links=" + links +
                ", venues=" + venues +
                '}';
    }

    protected MITDiningDining(Parcel in) {
        announcementsHTML = in.readString();
        url = in.readString();
        links = (HashSet) in.readValue(HashSet.class.getClassLoader());
        venues = (MITDiningVenues) in.readValue(MITDiningVenues.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(announcementsHTML);
        dest.writeString(url);
        dest.writeValue(links);
        dest.writeValue(venues);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITDiningDining> CREATOR = new Parcelable.Creator<MITDiningDining>() {
        @Override
        public MITDiningDining createFromParcel(Parcel in) {
            return new MITDiningDining(in);
        }

        @Override
        public MITDiningDining[] newArray(int size) {
            return new MITDiningDining[size];
        }
    };
}