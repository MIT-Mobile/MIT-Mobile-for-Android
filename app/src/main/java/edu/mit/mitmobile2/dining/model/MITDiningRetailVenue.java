package edu.mit.mitmobile2.dining.model;

import java.io.Serializable;
import java.util.HashSet;

import com.google.gson.annotations.SerializedName;


public class MITDiningRetailVenue implements Serializable {
    protected Object cuisine;  /* The ObjC Folks dont know what this is it seems */
    @SerializedName("description_html")
    protected String descriptionHTML;
    protected boolean favorite;
    @SerializedName("homepage_url")
    protected String homepageURL;
    @SerializedName("icon_url")
    protected String iconURL;
    @SerializedName("id")
    protected String identifier;
    @SerializedName("menu_html")
    protected String menuHTML;
    @SerializedName("menu_url")
    protected String menuURL;
    protected String name;
    protected Object payment;  /* The ObjC Folks dont know what this is it seems */
    @SerializedName("short_name")
    protected String shortName;
    protected HashSet<MITDiningRetailDay> hours;
    protected MITDiningLocation location;
    protected MITDiningVenues venues;

	public Object getCuisine() {
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

	public Object getPayment() {
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