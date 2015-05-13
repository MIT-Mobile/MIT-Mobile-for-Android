package edu.mit.mitmobile2.dining.model;

import android.content.Context;
import android.text.TextUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import edu.mit.mitmobile2.DateUtils;
import edu.mit.mitmobile2.R;


public class MITDiningMeal implements Serializable {

	@SerializedName("name")
	protected String name;

    @SerializedName("start_time")
    protected String startTimeString;

	@SerializedName("end_time")
	protected String endTimeString;

	@SerializedName("items")
    protected ArrayList<MITDiningMenuItem> items;

	@Expose
	protected MITDiningHouseDay houseDay;							// back reference

	@Expose
	protected String message;

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

	public ArrayList<MITDiningMenuItem> getItems() {
		return items;
	}

	public void setHouseDay(MITDiningHouseDay houseDay) {
		this.houseDay = houseDay;
	}

	public Date getStartTime() {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:SS").parse(houseDay.dateString + " " + startTimeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Date getEndTime() {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:SS").parse(houseDay.dateString + " " + endTimeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String mealHoursDescription(Context context) {
		String description = null;

		if (TextUtils.isEmpty(startTimeString) || TextUtils.isEmpty(endTimeString)) {
			if (!TextUtils.isEmpty(message)) {
				description = message;
			} else {
				description = context.getString(R.string.dining_venue_status_closed);
			}
		} else {
			String startString = DateUtils.MITShortTimeOfDayString(getStartTime());
			String endString = DateUtils.MITShortTimeOfDayString(getEndTime());

			description = context.getString(R.string.dining_venue_start_end_template, startString, endString);
		}

		return description;
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