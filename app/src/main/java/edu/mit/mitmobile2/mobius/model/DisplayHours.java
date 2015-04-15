package edu.mit.mitmobile2.mobius.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sseligma on 4/14/15.
 */
public class DisplayHours implements Parcelable {

    private String day;
    private String time;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.day);
        dest.writeString(this.time);
    }

    public DisplayHours() {
    }

    private DisplayHours(Parcel in) {
        this.day = in.readString();
        this.time = in.readString();
    }

    public static final Parcelable.Creator<DisplayHours> CREATOR = new Parcelable.Creator<DisplayHours>() {
        public DisplayHours createFromParcel(Parcel source) {
            return new DisplayHours(source);
        }

        public DisplayHours[] newArray(int size) {
            return new DisplayHours[size];
        }
    };
}
