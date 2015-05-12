package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;

import com.google.gson.annotations.SerializedName;


public class MITDiningMeal implements Parcelable {
    @SerializedName("end_time")
    protected String endTimeString;
    protected String message;
    protected String name;
    @SerializedName("start_time")
    protected String startTimeString;
    protected MITDiningHouseDay houseDay;
    protected HashSet<MITDiningMenuItem> items;

    public String getEndTimeString() {
        return endTimeString;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public MITDiningHouseDay getHouseDay() {
        return houseDay;
    }

    public HashSet<MITDiningMenuItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "MITDiningMeal{" +
                "endTimeString='" + endTimeString + '\'' +
                ", message='" + message + '\'' +
                ", name='" + name + '\'' +
                ", startTimeString='" + startTimeString + '\'' +
                ", houseDay=" + houseDay +
                ", items=" + items +
                '}';
    }

    protected MITDiningMeal(Parcel in) {
        endTimeString = in.readString();
        message = in.readString();
        name = in.readString();
        startTimeString = in.readString();
        houseDay = (MITDiningHouseDay) in.readValue(MITDiningHouseDay.class.getClassLoader());
        items = (HashSet) in.readValue(HashSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(endTimeString);
        dest.writeString(message);
        dest.writeString(name);
        dest.writeString(startTimeString);
        dest.writeValue(houseDay);
        dest.writeValue(items);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITDiningMeal> CREATOR = new Parcelable.Creator<MITDiningMeal>() {
        @Override
        public MITDiningMeal createFromParcel(Parcel in) {
            return new MITDiningMeal(in);
        }

        @Override
        public MITDiningMeal[] newArray(int size) {
            return new MITDiningMeal[size];
        }
    };
}