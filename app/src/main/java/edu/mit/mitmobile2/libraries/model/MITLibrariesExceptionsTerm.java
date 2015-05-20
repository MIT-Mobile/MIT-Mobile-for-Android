package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by serg on 5/20/15.
 */
public class MITLibrariesExceptionsTerm implements Parcelable {

    @SerializedName("dates")
    private MITLibrariesDate dates;

    @SerializedName("hours")
    private MITLibrariesDate hours;

    @SerializedName("reason")
    private String reason;

    public MITLibrariesExceptionsTerm() {
        // empty constructor
    }

    public MITLibrariesDate getDates() {
        return dates;
    }

    public void setDates(MITLibrariesDate dates) {
        this.dates = dates;
    }

    public MITLibrariesDate getHours() {
        return hours;
    }

    public void setHours(MITLibrariesDate hours) {
        this.hours = hours;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    protected MITLibrariesExceptionsTerm(Parcel in) {
        dates = (MITLibrariesDate) in.readValue(MITLibrariesDate.class.getClassLoader());
        hours = (MITLibrariesDate) in.readValue(MITLibrariesDate.class.getClassLoader());
        reason = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(dates);
        dest.writeValue(hours);
        dest.writeString(reason);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesExceptionsTerm> CREATOR = new Parcelable.Creator<MITLibrariesExceptionsTerm>() {
        @Override
        public MITLibrariesExceptionsTerm createFromParcel(Parcel in) {
            return new MITLibrariesExceptionsTerm(in);
        }

        @Override
        public MITLibrariesExceptionsTerm[] newArray(int size) {
            return new MITLibrariesExceptionsTerm[size];
        }
    };
}
