package edu.mit.mitmobile2.maps.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class MITMapPlaceContent implements Parcelable {

    @Expose
    private String name;
    @Expose
    private List<String> category = new ArrayList<>();
    @Expose
    private String url;
    @Expose
    private List<String> altname = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getAltname() {
        return altname;
    }

    public void setAltname(List<String> altname) {
        this.altname = altname;
    }


    protected MITMapPlaceContent(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0x01) {
            category = new ArrayList<>();
            in.readList(category, String.class.getClassLoader());
        } else {
            category = null;
        }
        url = in.readString();
        if (in.readByte() == 0x01) {
            altname = new ArrayList<>();
            in.readList(altname, String.class.getClassLoader());
        } else {
            altname = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (category == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(category);
        }
        dest.writeString(url);
        if (altname == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(altname);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITMapPlaceContent> CREATOR = new Parcelable.Creator<MITMapPlaceContent>() {
        @Override
        public MITMapPlaceContent createFromParcel(Parcel in) {
            return new MITMapPlaceContent(in);
        }

        @Override
        public MITMapPlaceContent[] newArray(int size) {
            return new MITMapPlaceContent[size];
        }
    };
}