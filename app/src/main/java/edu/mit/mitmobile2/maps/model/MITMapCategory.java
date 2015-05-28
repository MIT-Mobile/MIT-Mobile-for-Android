package edu.mit.mitmobile2.maps.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serg on 5/27/15.
 */
public class MITMapCategory implements Parcelable {

    @SerializedName("id")
    private String identifier;

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;

    @SerializedName("categories")
    private List<MITMapCategory> categories;

//    private HashMap<Object, Object> places;
//    private HashMap<Object, Object> placeContents;
//    private HashMap<Object, Object> children;
//    private MITMapCategory parent;
//    private MITMapSearch search;

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public List<MITMapCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<MITMapCategory> categories) {
        this.categories = categories;
    }

    /* Helpers */

    public String getSectionIndexTitle() {
        String title;
        if (identifier.equals("m") || identifier.equals("1_999")) {
            title = "#";
        } else {
            title = identifier.replaceAll("_", "-").toUpperCase();
        }

        return String.format(" %s", title);
    }

    /* Parcelable */

    protected MITMapCategory(Parcel in) {
        identifier = in.readString();
        name = in.readString();
        url = in.readString();
        if (in.readByte() == 0x01) {
            categories = new ArrayList<MITMapCategory>();
            in.readList(categories, MITMapCategory.class.getClassLoader());
        } else {
            categories = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identifier);
        dest.writeString(name);
        dest.writeString(url);
        if (categories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(categories);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITMapCategory> CREATOR = new Parcelable.Creator<MITMapCategory>() {
        @Override
        public MITMapCategory createFromParcel(Parcel in) {
            return new MITMapCategory(in);
        }

        @Override
        public MITMapCategory[] newArray(int size) {
            return new MITMapCategory[size];
        }
    };
}
