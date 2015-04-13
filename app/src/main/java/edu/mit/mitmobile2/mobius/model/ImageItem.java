package edu.mit.mitmobile2.mobius.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by sseligma on 4/11/15.
 */
public class ImageItem implements Parcelable {
    String raw_id;
    String large_id;
    String medium_id;
    String small_id;

    public ImageItem(JSONObject jObject) {
        this.raw_id = jObject.optString("raw_id");
        this.large_id = jObject.optString("large_id");
        this.medium_id = jObject.optString("medium_id");
        this.small_id = jObject.optString("small_id");
    }

    public String getRaw_id() {
        return raw_id;
    }

    public void setRaw_id(String raw_id) {
        this.raw_id = raw_id;
    }

    public String getLarge_id() {
        return large_id;
    }

    public void setLarge_id(String large_id) {
        this.large_id = large_id;
    }

    public String getMedium_id() {
        return medium_id;
    }

    public void setMedium_id(String medium_id) {
        this.medium_id = medium_id;
    }

    public String getSmall_id() {
        return small_id;
    }

    public void setSmall_id(String small_id) {
        this.small_id = small_id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.raw_id);
        dest.writeString(this.large_id);
        dest.writeString(this.medium_id);
        dest.writeString(this.small_id);
    }

    public ImageItem() {
    }

    private ImageItem(Parcel in) {
        this.raw_id = in.readString();
        this.large_id = in.readString();
        this.medium_id = in.readString();
        this.small_id = in.readString();
    }

    public static final Parcelable.Creator<ImageItem> CREATOR = new Parcelable.Creator<ImageItem>() {
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
