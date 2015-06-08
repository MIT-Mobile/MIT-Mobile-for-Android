package edu.mit.mitmobile2.links.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by serg on 6/4/15.
 */
public class MITLinksCategory implements Parcelable {

    @SerializedName("title")
    private String title;

    @SerializedName("links")
    private List<MITLink> links;

    public MITLinksCategory() {
        // empty constructor
    }

    public MITLinksCategory(String title, List<MITLink> links) {
        this.title = title;
        this.links = links;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MITLink> getLinks() {
        return links;
    }

    public void setLinks(List<MITLink> links) {
        this.links = links;
    }

    /* Parcelable */

    protected MITLinksCategory(Parcel in) {
        title = in.readString();
        if (in.readByte() == 0x01) {
            links = new ArrayList<MITLink>();
            in.readList(links, MITLink.class.getClassLoader());
        } else {
            links = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        if (links == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(links);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITLinksCategory> CREATOR = new Parcelable.Creator<MITLinksCategory>() {
        @Override
        public MITLinksCategory createFromParcel(Parcel in) {
            return new MITLinksCategory(in);
        }

        @Override
        public MITLinksCategory[] newArray(int size) {
            return new MITLinksCategory[size];
        }
    };
}
