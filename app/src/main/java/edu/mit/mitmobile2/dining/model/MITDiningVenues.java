package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashSet;


public class MITDiningVenues implements Parcelable {

    @SerializedName("house")
    protected ArrayList<MITDiningHouseVenue> house;

    @SerializedName("retail")
    protected ArrayList<MITDiningRetailVenue> retail;

    @Expose
    protected MITDiningDining dining;

	public MITDiningDining getDining() {
		return dining;
	}

	public ArrayList<MITDiningHouseVenue> getHouse() {
		return house;
	}

	public ArrayList<MITDiningRetailVenue> getRetail() {
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
        house = (ArrayList) in.readArrayList(ArrayList.class.getClassLoader());
        retail = (ArrayList) in.readValue(ArrayList.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(dining);
        dest.writeList(house);
        dest.writeList(retail);
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