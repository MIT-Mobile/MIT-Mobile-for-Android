package edu.mit.mitmobile2.dining.model;

import java.util.HashSet;

import com.google.gson.annotations.SerializedName;


public class MITDiningHouseDay {
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
}