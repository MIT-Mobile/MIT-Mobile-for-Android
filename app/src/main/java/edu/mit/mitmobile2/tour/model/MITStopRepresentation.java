package edu.mit.mitmobile2.tour.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MITStopRepresentation implements Parcelable {
    @SerializedName("representations")
    @Expose
    List<MITTourStopImage> images;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(images);
    }

    private MITStopRepresentation(Parcel p) {
        p.readTypedList(images, MITTourStopImage.CREATOR);
    }

    public static final Parcelable.Creator<MITStopRepresentation> CREATOR = new Parcelable.Creator<MITStopRepresentation>() {
        public MITStopRepresentation createFromParcel(Parcel source) {
            return new MITStopRepresentation(source);
        }

        public MITStopRepresentation[] newArray(int size) {
            return new MITStopRepresentation[size];
        }
    };
}
