package edu.mit.mitmobile2.dining.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class MITDiningMenuItem implements Parcelable {
    @SerializedName("station")
    protected String station;

	@SerializedName("name")
	protected String name;

    @SerializedName("description")
    protected String itemDescription;

	@Expose
    protected ArrayList<MITDiningMeal> meal;

	@Expose
	protected Object dietaryFlags;  /* The ObjC Folks dont know what this is it seems */

	public Object getDietaryFlags() {
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

	public ArrayList<MITDiningMeal> getMeal() {
		return meal;
	}

	@Override
	public String toString() {
		return "MITDiningMenuItem{" +
			"dietaryFlags=" + dietaryFlags +
			", itemDescription='" + itemDescription + '\'' +
			", name='" + name + '\'' +
			", station='" + station + '\'' +
			", meal=" + meal +
			'}';
	}

    protected MITDiningMenuItem(Parcel in) {
        dietaryFlags = (Object) in.readValue(Object.class.getClassLoader());
        itemDescription = in.readString();
        name = in.readString();
        station = in.readString();
        if (in.readByte() == 0x01) {
            meal = new ArrayList<MITDiningMeal>();
            in.readList(meal, MITDiningMeal.class.getClassLoader());
        } else {
            meal = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(dietaryFlags);
        dest.writeString(itemDescription);
        dest.writeString(name);
        dest.writeString(station);
        if (meal == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(meal);
        }
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