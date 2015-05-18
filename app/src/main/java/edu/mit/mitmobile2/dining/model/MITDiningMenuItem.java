package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class MITDiningMenuItem implements Parcelable {
    @SerializedName("station")
    protected String station;

    @SerializedName("name")
    protected String name;

    @SerializedName("description")
    protected String itemDescription;

    @SerializedName("dietary_flags")
    @Expose
    protected List<String> dietaryFlags;

    public List<String> getDietaryFlags() {
        return dietaryFlags;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getName() {
        return name;
    }

    public String getStation() {
        return station;
    }

    @Override
    public String toString() {
        return "MITDiningMenuItem{" +
                "dietaryFlags=" + dietaryFlags +
                ", itemDescription='" + itemDescription + '\'' +
                ", name='" + name + '\'' +
                ", station='" + station + '\'' +
                '}';
    }

    protected MITDiningMenuItem(Parcel in) {
        if (in.readByte() == 0x01) {
            dietaryFlags = new ArrayList<>();
            in.readList(dietaryFlags, String.class.getClassLoader());
        } else {
            dietaryFlags = null;
        }
        itemDescription = in.readString();
        name = in.readString();
        station = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (dietaryFlags == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(dietaryFlags);
        }
        dest.writeString(itemDescription);
        dest.writeString(name);
        dest.writeString(station);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITDiningMenuItem> CREATOR = new Parcelable.Creator<MITDiningMenuItem>() {
        @Override
        public MITDiningMenuItem createFromParcel(Parcel in) {
            return new MITDiningMenuItem(in);
        }

        @Override
        public MITDiningMenuItem[] newArray(int size) {
            return new MITDiningMenuItem[size];
        }
    };
}