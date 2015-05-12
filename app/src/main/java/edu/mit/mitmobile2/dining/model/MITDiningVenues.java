package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;


public class MITDiningVenues implements Parcelable {
    protected MITDiningDining dining;
    protected HashSet<MITDiningHouseVenue> house;
    protected HashSet<MITDiningRetailVenue> retail;

    public MITDiningDining getDining() {
        return dining;
    }

    public HashSet<MITDiningHouseVenue> getHouse() {
        return house;
    }

    public HashSet<MITDiningRetailVenue> getRetail() {
        return retail;
    }

    @Override
    public String toString() {
        return "MITDiningVenues{" +
                "dining=" + dining +
                ", house=" + house +
                ", retail=" + retail +
                '}';
    }

    protected MITDiningVenues(Parcel in) {
        dining = (MITDiningDining) in.readValue(MITDiningDining.class.getClassLoader());
        house = (HashSet) in.readValue(HashSet.class.getClassLoader());
        retail = (HashSet) in.readValue(HashSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(dining);
        dest.writeValue(house);
        dest.writeValue(retail);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITDiningVenues> CREATOR = new Parcelable.Creator<MITDiningVenues>() {
        @Override
        public MITDiningVenues createFromParcel(Parcel in) {
            return new MITDiningVenues(in);
        }

        @Override
        public MITDiningVenues[] newArray(int size) {
            return new MITDiningVenues[size];
        }
    };
}