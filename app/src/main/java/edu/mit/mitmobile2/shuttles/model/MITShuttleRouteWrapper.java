package edu.mit.mitmobile2.shuttles.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class MITShuttleRouteWrapper implements Parcelable {

    @Expose
    private String id;
    @Expose
    private String url;
    @Expose
    private String title;
    @Expose
    private String agency;
    @Expose
    private Boolean scheduled;
    @Expose
    private Boolean predictable;
    @Expose
    private String description;
    @SerializedName("predictions_url")
    @Expose
    private String predictionsUrl;
    @SerializedName("vehicles_url")
    @Expose
    private String vehiclesUrl;
    @Expose
    private MITShuttlePath path;
    @Expose
    private List<MITShuttleStopWrapper> stops = new ArrayList<MITShuttleStopWrapper>();


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


    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }


    public Boolean getScheduled() {
        return scheduled;
    }


    public void setScheduled(Boolean scheduled) {
        this.scheduled = scheduled;
    }


    public Boolean getPredictable() {
        return predictable;
    }


    public void setPredictable(Boolean predictable) {
        this.predictable = predictable;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getPredictionsUrl() {
        return predictionsUrl;
    }


    public void setPredictionsUrl(String predictionsUrl) {
        this.predictionsUrl = predictionsUrl;
    }


    public String getVehiclesUrl() {
        return vehiclesUrl;
    }


    public void setVehiclesUrl(String vehiclesUrl) {
        this.vehiclesUrl = vehiclesUrl;
    }


    public MITShuttlePath getPath() {
        return path;
    }


    public void setPath(MITShuttlePath path) {
        this.path = path;
    }


    public List<MITShuttleStopWrapper> getStops() {
        return stops;
    }


    public void setStops(List<MITShuttleStopWrapper> stops) {
        this.stops = stops;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(agency);
        dest.writeInt(this.scheduled ? 1 : 0);
        dest.writeInt(this.predictable ? 1 : 0);
        dest.writeString(description);
        dest.writeString(predictionsUrl);
        dest.writeString(vehiclesUrl);
        dest.writeParcelable(path, 0);
        dest.writeTypedList(stops);
    }

    private MITShuttleRouteWrapper(Parcel p) {
        this.id = p.readString();
        this.url = p.readString();
        this.title = p.readString();
        this.agency = p.readString();
        this.scheduled = p.readInt() == 1;
        this.predictable = p.readInt() == 1;
        this.description = p.readString();
        this.predictionsUrl = p.readString();
        this.vehiclesUrl = p.readString();
        this.path = p.readParcelable(MITShuttlePath.class.getClassLoader());
        p.readTypedList(this.stops, MITShuttleStopWrapper.CREATOR);
//        this.stops = p.readArrayList(MITShuttleStopWrapper.class.getClassLoader());
    }

    public static final Parcelable.Creator<MITShuttleRouteWrapper> CREATOR = new Parcelable.Creator<MITShuttleRouteWrapper>() {
        public MITShuttleRouteWrapper createFromParcel(Parcel source) {
            return new MITShuttleRouteWrapper(source);
        }

        public MITShuttleRouteWrapper[] newArray(int size) {
            return new MITShuttleRouteWrapper[size];
        }
    };
}