package edu.mit.mitmobile2.dining.model;

import java.util.ArrayList;


public class MITDiningMenuItem {
	Number dietaryFlags;
	String itemDescription;
	String name;
	String station;
	ArrayList<MITDiningMeal> meal;

	public Number getDietaryFlags() {
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
}