package edu.mit.mitmobile2.mobius.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sseligma on 4/6/15.
 */
public class QuickSearch implements Parcelable {
    private String type;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.value);
    }

    public QuickSearch() {
    }

    private QuickSearch(Parcel in) {
        this.type = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<QuickSearch> CREATOR = new Parcelable.Creator<QuickSearch>() {
        public QuickSearch createFromParcel(Parcel source) {
            return new QuickSearch(source);
        }

        public QuickSearch[] newArray(int size) {
            return new QuickSearch[size];
        }
    };
}
