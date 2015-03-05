package edu.mit.mitmobile2.shuttles.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MITShuttlePrediction implements Parcelable {

    @SerializedName("vehicle_id")
    @Expose
    private String vehicleId;
    @Expose
    private Integer timestamp;
    @Expose
    private Integer seconds;


    public String getVehicleId() {
        return vehicleId;
    }


    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }


    public Integer getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }


    public Integer getSeconds() {
        return seconds;
    }


    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.vehicleId);
        dest.writeInt(this.timestamp);
        dest.writeInt(this.seconds);
    }

    private MITShuttlePrediction(Parcel p) {
        this.vehicleId = p.readString();
        this.timestamp = p.readInt();
        this.seconds = p.readInt();
    }

    public static final Parcelable.Creator<MITShuttlePrediction> CREATOR = new Parcelable.Creator<MITShuttlePrediction>() {
        public MITShuttlePrediction createFromParcel(Parcel source) {
            return new MITShuttlePrediction(source);
        }

        public MITShuttlePrediction[] newArray(int size) {
            return new MITShuttlePrediction[size];
        }
    };
}

