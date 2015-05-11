package edu.mit.mitmobile2.dining.model;

import java.util.HashSet;

import com.google.gson.annotations.SerializedName;


public class MITDiningMeal {
    @SerializedName("end_time")
    protected String endTimeString;
    protected String message;
    protected String name;
    @SerializedName("start_time")
    protected String startTimeString;
    protected MITDiningHouseDay houseDay;
    protected HashSet<MITDiningMenuItem> items;

	public String getEndTimeString() {
		return endTimeString;
	}

	public String getMessage() {
		return message;
	}

	public String getName() {
		return name;
	}

	public String getStartTimeString() {
		return startTimeString;
	}

	public MITDiningHouseDay getHouseDay() {
		return houseDay;
	}

	public HashSet<MITDiningMenuItem> getItems() {
		return items;
	}

	@Override
	public String toString() {
		return "MITDiningMeal{" +
			"endTimeString='" + endTimeString + '\'' +
			", message='" + message + '\'' +
			", name='" + name + '\'' +
			", startTimeString='" + startTimeString + '\'' +
			", houseDay=" + houseDay +
			", items=" + items +
			'}';
	}
}