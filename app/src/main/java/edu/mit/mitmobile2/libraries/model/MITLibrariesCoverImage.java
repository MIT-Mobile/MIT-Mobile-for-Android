package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MITLibrariesCoverImage implements Parcelable {

    @SerializedName("width")
    private int width;

    @SerializedName("height")
    private int height;

    @SerializedName("url")
    private String url;

    public MITLibrariesCoverImage() {
        // empty constructor
    }

    public int getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    protected MITLibrariesCoverImage(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(url);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesCoverImage> CREATOR = new Parcelable.Creator<MITLibrariesCoverImage>() {
        @Override
        public MITLibrariesCoverImage createFromParcel(Parcel in) {
            return new MITLibrariesCoverImage(in);
        }

        @Override
        public MITLibrariesCoverImage[] newArray(int size) {
            return new MITLibrariesCoverImage[size];
        }
    };
}
