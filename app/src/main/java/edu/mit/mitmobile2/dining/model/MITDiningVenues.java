package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class MITDiningVenues implements Parcelable {
    protected MITDiningDining dining;
    protected List<MITDiningHouseVenue> house;
    protected List<MITDiningRetailVenue> retail;

    public MITDiningDining getDining() {
        return dining;
    }

    public List<MITDiningHouseVenue> getHouse() {
        return house;
    }

    public List<MITDiningRetailVenue> getRetail() {
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

        if (in.readByte() == 0x01) {
            house = new ArrayList<>();
            in.readList(house, MITDiningHouseVenue.class.getClassLoader());
        } else {
            house = null;
        }

        if (in.readByte() == 0x01) {
            retail = new ArrayList<>();
            in.readList(retail, MITDiningRetailVenue.class.getClassLoader());
        } else {
            retail = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(dining);
        if (house == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(house);
        }
        if (retail == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(retail);
        }
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