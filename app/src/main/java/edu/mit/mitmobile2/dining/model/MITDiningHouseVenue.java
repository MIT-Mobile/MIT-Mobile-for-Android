package edu.mit.mitmobile2.dining.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import edu.mit.mitmobile2.DateUtils;


public class MITDiningHouseVenue {

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
}