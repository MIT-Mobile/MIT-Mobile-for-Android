package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;

import com.google.gson.annotations.SerializedName;


public class MITDiningHouseDay implements Parcelable {
    @SerializedName("date")
    protected String dateString;
    protected String message;
    protected MITDiningHouseVenue houseVenue;
    protected HashSet<MITDiningMeal> meals;

    public String getDateString() {
        return dateString;
    }

    public String getMessage() {
        return message;
    }

    public MITDiningHouseVenue getHouseVenue() {
        return houseVenue;
    }

    public HashSet<MITDiningMeal> getMeals() {
        return meals;
    }

    @Override
    public String toString() {
        return "MITDiningHouseDay{" +
                "dateString='" + dateString + '\'' +
                ", message='" + message + '\'' +
                ", houseVenue=" + houseVenue +
                ", meals=" + meals +
                '}';
    }

    protected MITDiningHouseDay(Parcel in) {
        dateString = in.readString();
        message = in.readString();
        houseVenue = (MITDiningHouseVenue) in.readValue(MITDiningHouseVenue.class.getClassLoader());
        meals = (HashSet) in.readValue(HashSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateString);
        dest.writeString(message);
        dest.writeValue(houseVenue);
        dest.writeValue(meals);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITDiningHouseDay> CREATOR = new Parcelable.Creator<MITDiningHouseDay>() {
        @Override
        public MITDiningHouseDay createFromParcel(Parcel in) {
            return new MITDiningHouseDay(in);
        }

        @Override
        public MITDiningHouseDay[] newArray(int size) {
            return new MITDiningHouseDay[size];
        }
    };
}