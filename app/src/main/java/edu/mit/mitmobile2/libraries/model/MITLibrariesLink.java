package edu.mit.mitmobile2.libraries.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MITLibrariesLink implements Parcelable {

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    public MITLibrariesLink() {
        // empty constructor
    }

    public MITLibrariesLink(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    protected MITLibrariesLink(Parcel in) {
        title = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLibrariesLink> CREATOR = new Parcelable.Creator<MITLibrariesLink>() {
        @Override
        public MITLibrariesLink createFromParcel(Parcel in) {
            return new MITLibrariesLink(in);
        }

        @Override
        public MITLibrariesLink[] newArray(int size) {
            return new MITLibrariesLink[size];
        }
    };
}

