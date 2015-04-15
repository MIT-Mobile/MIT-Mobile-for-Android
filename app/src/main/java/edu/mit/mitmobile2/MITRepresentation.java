package edu.mit.mitmobile2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import edu.mit.mitmobile2.tour.model.MITTourStopImage;

public class MITRepresentation implements Parcelable {
    @SerializedName("representations")
    @Expose
    private List<MITTourStopImage> images;

    public List<MITTourStopImage> getImages() {
        return images;
    }

    public void setImages(List<MITTourStopImage> images) {
        this.images = images;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(images);
    }

    private MITRepresentation(Parcel p) {
        this.images = p.createTypedArrayList(MITTourStopImage.CREATOR);
    }

    public static final Parcelable.Creator<MITRepresentation> CREATOR = new Parcelable.Creator<MITRepresentation>() {
        public MITRepresentation createFromParcel(Parcel source) {
            return new MITRepresentation(source);
        }

        public MITRepresentation[] newArray(int size) {
            return new MITRepresentation[size];
        }
    };
}
