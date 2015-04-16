package edu.mit.mitmobile2.news.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import edu.mit.mitmobile2.MITImage;

public class MITNewsCoverImage implements Parcelable {

    @Expose
    private List<MITImage> representations = new ArrayList<>();

    public List<MITImage> getRepresentations() {
        return representations;
    }

    public void setRepresentations(List<MITImage> representations) {
        this.representations = representations;
    }

    private MITNewsCoverImage(Parcel p) {
        this.representations = (List<MITImage>) p.readArrayList(MITImage.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.representations);
    }

    public static final Parcelable.Creator<MITNewsCoverImage> CREATOR = new Parcelable.Creator<MITNewsCoverImage>() {
        public MITNewsCoverImage createFromParcel(Parcel source) {
            return new MITNewsCoverImage(source);
        }

        public MITNewsCoverImage[] newArray(int size) {
            return new MITNewsCoverImage[size];
        }
    };
}