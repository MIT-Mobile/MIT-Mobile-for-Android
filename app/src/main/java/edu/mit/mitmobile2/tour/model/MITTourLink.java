package edu.mit.mitmobile2.tour.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class MITTourLink implements Parcelable {
    @Expose
    String name;
    @Expose
    String url;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeSerializable(url);
    }

    private MITTourLink(Parcel p) {
        name = p.readString();
        url = p.readString();
    }

    public static final Parcelable.Creator<MITTourLink> CREATOR = new Parcelable.Creator<MITTourLink>() {
        public MITTourLink createFromParcel(Parcel source) {
            return new MITTourLink(source);
        }

        public MITTourLink[] newArray(int size) {
            return new MITTourLink[size];
        }
    };
}
