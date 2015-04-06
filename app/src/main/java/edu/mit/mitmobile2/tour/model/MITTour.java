package edu.mit.mitmobile2.tour.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MITTour implements Parcelable {

    @Expose
    private String id;
    @Expose
    private String url;
    @Expose
    private String title;
    @SerializedName("short_description")
    @Expose
    private String shortDescription;
    @SerializedName("length_in_km")
    @Expose
    private Integer lengthInKm;
    @SerializedName("estimated_duration_in_minutes")
    @Expose
    private Integer estimatedDurationInMinutes;
    @SerializedName("description_html")
    @Expose
    private String descriptionHtml;
    @Expose
    private List<MITTourLink> links;
    @Expose
    private List<MITTourStop> stops;

    public MITTour() {
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
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


    public String getShortDescription() {
        return shortDescription;
    }


    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }


    public Integer getLengthInKm() {
        return lengthInKm;
    }


    public void setLengthInKm(Integer lengthInKm) {
        this.lengthInKm = lengthInKm;
    }


    public Integer getEstimatedDurationInMinutes() {
        return estimatedDurationInMinutes;
    }


    public void setEstimatedDurationInMinutes(Integer estimatedDurationInMinutes) {
        this.estimatedDurationInMinutes = estimatedDurationInMinutes;
    }

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public List<MITTourLink> getLinks() {
        return links;
    }

    public void setLinks(List<MITTourLink> links) {
        this.links = links;
    }

    public List<MITTourStop> getStops() {
        return stops;
    }

    public void setStops(List<MITTourStop> stops) {
        this.stops = stops;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
//        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(shortDescription);
        dest.writeInt(lengthInKm);
        dest.writeInt(estimatedDurationInMinutes);
        dest.writeString(descriptionHtml);
        dest.writeTypedList(stops);
        dest.writeTypedList(links);
    }

    private MITTour(Parcel p) {
        this.id = p.readString();
//        this.url = p.readString();
        this.title = p.readString();
        this.shortDescription = p.readString();
        this.lengthInKm = p.readInt();
        this.estimatedDurationInMinutes = p.readInt();
        this.descriptionHtml = p.readString();
        p.readTypedList(this.stops, MITTourStop.CREATOR);
        p.readTypedList(this.links, MITTourLink.CREATOR);
    }

    public static final Parcelable.Creator<MITTour> CREATOR = new Parcelable.Creator<MITTour>() {
        public MITTour createFromParcel(Parcel source) {
            return new MITTour(source);
        }

        public MITTour[] newArray(int size) {
            return new MITTour[size];
        }
    };
}
