package edu.mit.mitmobile2.dining.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MITDiningMenuItem implements Serializable {

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
}