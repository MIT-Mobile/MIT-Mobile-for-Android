package edu.mit.mitmobile2.news.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class MITNewsCategory implements Parcelable {

    @Expose
    private String id;
    @Expose
    private String url;
    @Expose
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.url);
        dest.writeString(this.name);
    }

    private MITNewsCategory(Parcel p) {
        this.id = p.readString();
        this.url = p.readString();
        this.name = p.readString();
    }

    public static final Parcelable.Creator<MITNewsCategory> CREATOR = new Parcelable.Creator<MITNewsCategory>() {
        public MITNewsCategory createFromParcel(Parcel source) {
            return new MITNewsCategory(source);
        }

        public MITNewsCategory[] newArray(int size) {
            return new MITNewsCategory[size];
        }
    };
}