package edu.mit.mitmobile2.news.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import edu.mit.mitmobile2.MITImage;

public class MITNewsGalleryImage implements Parcelable {

    @Expose
    private String description;
    @Expose
    private String credits;
    @Expose
    private List<MITImage> representations = new ArrayList<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public List<MITImage> getRepresentations() {
        return representations;
    }

    public void setRepresentations(List<MITImage> representations) {
        this.representations = representations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeString(this.credits);
        dest.writeTypedList(this.representations);
    }

    private MITNewsGalleryImage(Parcel p) {
        this.description = p.readString();
        this.credits = p.readString();
        this.representations = p.createTypedArrayList(MITImage.CREATOR);
    }

    public static final Parcelable.Creator<MITNewsGalleryImage> CREATOR = new Parcelable.Creator<MITNewsGalleryImage>() {
        public MITNewsGalleryImage createFromParcel(Parcel source) {
            return new MITNewsGalleryImage(source);
        }

        public MITNewsGalleryImage[] newArray(int size) {
            return new MITNewsGalleryImage[size];
        }
    };
}