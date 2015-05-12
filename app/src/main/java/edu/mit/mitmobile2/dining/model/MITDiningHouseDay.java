package edu.mit.mitmobile2.dining.model;

import android.content.Context;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import edu.mit.mitmobile2.R;


public class MITDiningHouseDay {

    @SerializedName("date")
    protected String dateString;

	@SerializedName("meals")
    protected ArrayList<MITDiningMeal> meals;

	@Expose
	protected String message;

	@Expose
	protected MITDiningHouseVenue houseVenue;

	public String getDateString() {
		return dateString;
	}

	public String getMessage() {
		return message;
	}

	public MITDiningHouseVenue getHouseVenue() {
		return houseVenue;
	}

	public ArrayList<MITDiningMeal> getMeals() {
		return meals;
	}

	public Date getDate() {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public MITDiningMeal mealForDate(Date date) {
		MITDiningMeal returnMeal = null;
		long dateInterval = date.getTime();
		for (MITDiningMeal meal : meals) {
			long startTime = (meal.getStartTime() == null) ? 0 :meal.getStartTime().getTime();
			long endTime = (meal.getEndTime() == null) ? 0 :meal.getEndTime().getTime();
			if (startTime <= dateInterval && dateInterval < endTime) {
				returnMeal = meal;
				break;
			}
		}

		return returnMeal;
	}

	public String dayHoursDescription(Context context) {
		String dayHoursDescription = null;

		if (!TextUtils.isEmpty(message)) {
			dayHoursDescription = message;
		} else {
			ArrayList<String> hoursStrings = new ArrayList<>();
			ArrayList<MITDiningMeal> sortedMeals = sortedMealsArray();

			for (MITDiningMeal meal : sortedMeals) {
				String hours = meal.mealHoursDescription(context);
				if (!TextUtils.isEmpty(hours)) {
					hoursStrings.add(hours);
				}
			}

			if (hoursStrings.size() > 0) {
				dayHoursDescription = TextUtils.join(", ", hoursStrings);
			} else {
				dayHoursDescription = context.getString(R.string.dining_venue_closed_for_the_day);
			}
		}

		return dayHoursDescription;
	}

	private ArrayList<MITDiningMeal> sortedMealsArray() {
		ArrayList<MITDiningMeal> mealsArrayList = new ArrayList<MITDiningMeal>(meals);
		Collections.sort(mealsArrayList, new Comparator<MITDiningMeal>() {
			@Override
			public int compare(MITDiningMeal lhs, MITDiningMeal rhs) {
				return lhs.getStartTime().compareTo(rhs.getStartTime());
			}
		});
		return mealsArrayList;
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