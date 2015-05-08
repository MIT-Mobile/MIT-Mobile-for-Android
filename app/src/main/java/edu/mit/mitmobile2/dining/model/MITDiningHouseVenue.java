package edu.mit.mitmobile2.dining.model;

import java.util.HashSet;


public class MITDiningHouseVenue {
    protected String iconURL;
    protected String identifier;
    protected String name;
    protected Object payment; /* The ObjC Folks dont know what this is it seems */
    protected String shortName;
    protected MITDiningLocation location;
    protected HashSet<MITDiningHouseDay> mealsByDay;
    protected MITDiningVenues venues;

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