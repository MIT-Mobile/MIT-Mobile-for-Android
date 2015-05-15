package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MITDiningDining implements Parcelable {
    @SerializedName("announcements_html")
    protected String announcementsHTML;

    @SerializedName("url")
    protected String url;

    @SerializedName("links")
    protected ArrayList<MITDiningLinks> links;

    @SerializedName("venues")
    protected MITDiningVenues venues;

    public String getAnnouncementsHTML() {
        return announcementsHTML;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<MITDiningLinks> getLinks() {
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
        if (in.readByte() == 0x01) {
            links = new ArrayList<>();
            in.readList(links, MITDiningLinks.class.getClassLoader());
        } else {
            links = null;
        }
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
        if (links == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(links);
        }
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