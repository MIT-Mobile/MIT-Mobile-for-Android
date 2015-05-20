package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesAvailability implements Parcelable {

    @SerializedName("location")
    private String location;

    @SerializedName("collection")
    private String collection;

    @SerializedName("call_number")
    private String callNumber;

    @SerializedName("status")
    private String status;

    @SerializedName("available")
    private boolean available;

    public MITLibrariesAvailability() {
        // empty constructor
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    protected MITLibrariesAvailability(Parcel in) {
        location = in.readString();
        collection = in.readString();
        callNumber = in.readString();
        status = in.readString();
        available = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeString(collection);
        dest.writeString(callNumber);
        dest.writeString(status);
        dest.writeByte((byte) (available ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesAvailability> CREATOR = new Parcelable.Creator<MITLibrariesAvailability>() {
        @Override
        public MITLibrariesAvailability createFromParcel(Parcel in) {
            return new MITLibrariesAvailability(in);
        }

        @Override
        public MITLibrariesAvailability[] newArray(int size) {
            return new MITLibrariesAvailability[size];
        }
    };
}
