package edu.mit.mitmobile2.dining.model;

import java.util.HashSet;


public class MITDiningRetailVenue {
	Number cuisine;
	String descriptionHTML;
	boolean favorite;
	String homepageURL;
	String iconURL;
	String identifier;
	String menuHTML;
	String menuURL;
	String name;
	Number payment;
	String shortName;
	HashSet<MITDiningRetailDay> hours;
	MITDiningLocation location;
	MITDiningVenues venues;

	public Number getCuisine() {
		return cuisine;
	}

	public String getDescriptionHTML() {
		return descriptionHTML;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public String getHomepageURL() {
		return homepageURL;
	}

	public String getIconURL() {
		return iconURL;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getMenuHTML() {
		return menuHTML;
	}

	public String getMenuURL() {
		return menuURL;
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

	public HashSet<MITDiningRetailDay> getHours() {
		return hours;
	}

	public MITDiningLocation getLocation() {
		return location;
	}

	public MITDiningVenues getVenues() {
		return venues;
	}

	@Override
	public String toString() {
		return "MITDiningRetailVenue{" +
			"cuisine=" + cuisine +
			", descriptionHTML='" + descriptionHTML + '\'' +
			", favorite=" + favorite +
			", homepageURL='" + homepageURL + '\'' +
			", iconURL='" + iconURL + '\'' +
			", identifier='" + identifier + '\'' +
			", menuHTML='" + menuHTML + '\'' +
			", menuURL='" + menuURL + '\'' +
			", name='" + name + '\'' +
			", payment=" + payment +
			", shortName='" + shortName + '\'' +
			", hours=" + hours +
			", location=" + location +
			", venues=" + venues +
			'}';
	}
}