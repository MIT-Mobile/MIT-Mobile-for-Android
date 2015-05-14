package edu.mit.mitmobile2.dining.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

import edu.mit.mitmobile2.DateUtils;



public class MITDiningHouseVenue implements Parcelable {

    @SerializedName("id")
    protected String identifier;

    @SerializedName("url")
    protected String url;

    @SerializedName("name")
    protected String name;

    @SerializedName("short_name")
    protected String shortName;

    @SerializedName("icon_url")
    protected String iconURL;

	@SerializedName("location")
    protected MITDiningLocation location;

	@SerializedName("meals_by_day")
    protected ArrayList<MITDiningHouseDay> mealsByDay;

	@Expose
    protected MITDiningVenues venues;

	@Expose
	protected Object payment; /* The ObjC Folks dont know what this is it seems */

	public String getIconURL() {
		return iconURL;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getName() {
		return name;
	}

	public Object getPayment() {
		return payment;
	}

	public String getShortName() {
		return shortName;
	}

	public MITDiningLocation getLocation() {
		return location;
	}

	public ArrayList<MITDiningHouseDay> getMealsByDay() {
		return mealsByDay;
	}

	public MITDiningVenues getVenues() {
		return venues;
	}

	public String hoursToday(Context context) {
		MITDiningHouseDay today = houseDayForDate(new Date());
		return today.dayHoursDescription(context);
	}

	public boolean isOpenNow() {
		Date date = new Date();
		MITDiningHouseDay day = houseDayForDate(date);
		MITDiningMeal meal = day.mealForDate(date);
		return (meal != null);
	}

	public MITDiningHouseDay houseDayForDate(Date date) {
		MITDiningHouseDay returnDay = null;
		if (date != null) {
			Date startOfDate = DateUtils.startOfDay(date);
			for (MITDiningHouseDay day : mealsByDay) {
				if (day.getDate() != null && DateUtils.areEqualToDateIgnoringTime(day.getDate(), startOfDate)) {
					returnDay = day;
					break;
				}
			}
		}
		return returnDay;
	}

    @Override
    public String toString() {
        return "MITDiningHouseVenue{" +
                "iconURL='" + iconURL + '\'' +
                ", identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                ", payment=" + payment +
                ", shortName='" + shortName + '\'' +
                ", location=" + location +
                ", mealsByDay=" + mealsByDay +
                ", venues=" + venues +
                '}';
    }

    protected MITDiningHouseVenue(Parcel in) {
        identifier = in.readString();
        url = in.readString();
        name = in.readString();
        shortName = in.readString();
        iconURL = in.readString();
        location = (MITDiningLocation) in.readValue(MITDiningLocation.class.getClassLoader());
        if (in.readByte() == 0x01) {
            mealsByDay = new ArrayList<MITDiningHouseDay>();
            in.readList(mealsByDay, MITDiningHouseDay.class.getClassLoader());
        } else {
            mealsByDay = null;
        }
        venues = (MITDiningVenues) in.readValue(MITDiningVenues.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identifier);
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(shortName);
        dest.writeString(iconURL);
        dest.writeValue(location);
        if (mealsByDay == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mealsByDay);
        }
        dest.writeValue(venues);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MITDiningHouseVenue> CREATOR = new Parcelable.Creator<MITDiningHouseVenue>() {
        @Override
        public MITDiningHouseVenue createFromParcel(Parcel in) {
            return new MITDiningHouseVenue(in);
        }

        @Override
        public MITDiningHouseVenue[] newArray(int size) {
            return new MITDiningHouseVenue[size];
        }
    };
}