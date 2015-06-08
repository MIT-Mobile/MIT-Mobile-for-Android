package edu.mit.mitmobile2.links.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by serg on 6/4/15.
 */
public class MITLink implements Parcelable {

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;

    public MITLink() {
        // empty constructor
    }

    public MITLink(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /* Parcelable */

    protected MITLink(Parcel in) {
        name = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLink> CREATOR = new Parcelable.Creator<MITLink>() {
        @Override
        public MITLink createFromParcel(Parcel in) {
            return new MITLink(in);
        }

        @Override
        public MITLink[] newArray(int size) {
            return new MITLink[size];
        }
    };
}
