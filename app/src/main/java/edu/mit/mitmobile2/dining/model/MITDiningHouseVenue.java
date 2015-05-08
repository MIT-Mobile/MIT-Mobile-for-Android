package edu.mit.mitmobile2.dining.model;

import java.util.HashSet;


public class MITDiningHouseVenue {
	String iconURL;
	String identifier;
	String name;
	Number payment;
	String shortName;
	MITDiningLocation location;
	HashSet<MITDiningHouseDay> mealsByDay;
	MITDiningVenues venues;

	public String getIconURL() {
		return iconURL;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getName() {
		return name;
	}

	public Number getPayment() {
		return payment;
	}

	public String getShortName() {
		return shortName;
	}

	public MITDiningLocation getLocation() {
		return location;
	}

	public HashSet<MITDiningHouseDay> getMealsByDay() {
		return mealsByDay;
	}

	public MITDiningVenues getVenues() {
		return venues;
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