package edu.mit.mitmobile2.dining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Html;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;

public class DiningModel {

	protected static DiningVenues sVenues;

	public static void fetchDiningData(final Context context, final Handler uiHandler) {
		uiHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					InputStream istream = context.getResources().getAssets().open("dining/data.json");
					String jsonString = convertStreamToString(istream);
					JSONObject jsonObject = new JSONObject(jsonString);
					sVenues = new DiningVenues(jsonObject);
					MobileWebApi.sendSuccessMessage(uiHandler);					
					
				} catch (IOException e) {
					e.printStackTrace();
					MobileWebApi.sendErrorMessage(uiHandler);
				} catch (JSONException e) {
					e.printStackTrace();
					MobileWebApi.sendErrorMessage(uiHandler);
				}
				
			}
			
		}, 500);
	}
	
	public static DiningVenues getDiningVenues() {
		return sVenues;
	}
	
	public static List<DiningLink> getDiningLinks() {
		ArrayList<DiningLink> links = new ArrayList<DiningLink>();
		links.add(new DiningLink("Comments for MIT Dining", "http://web.mit.edu/dining/comments"));
		links.add(new DiningLink("Food to Go", "http://web.mit.edu/dining/food-to-go"));
		links.add(new DiningLink("Full MIT Dining website", "http://web.mit.edu/dining"));
		return links;
	}
	
	private static String convertStreamToString(InputStream is) throws IOException {
            StringBuilder builder = new StringBuilder();
            
            char[] buffer = new char[2048];
            try {
            	Reader reader = new BufferedReader(new InputStreamReader(is,  "UTF-8"));
            	
            	int n;
            	while ((n = reader.read(buffer)) != -1) {
            		builder.append(buffer, 0, n);
            	}
            } finally {
            	is.close();
            }
            return builder.toString();
	}
	
	private static Calendar getCalendarDate(String date, String time) {
		GregorianCalendar calendar = new GregorianCalendar();
		String[] dateParts = date.split("\\-");
		int year = Integer.parseInt(dateParts[0]);
		int month = Integer.parseInt(dateParts[1]) - 1;
		int day = Integer.parseInt(dateParts[2]);
		if (time == null) {
			calendar.set(year, month, day, 0, 0);
		} else {
			String[] timeParts = time.split(":");
			int hourOfDay = Integer.parseInt(timeParts[0]);
			int minute = Integer.parseInt(timeParts[1]);
			calendar.set(year, month, day, hourOfDay, minute, 0);
		}		
		return calendar;
	}
	
	public static class DiningVenues {
		
		private String mAnnouncementsHtml;
		
		private ArrayList<HouseDiningHall> mHouseVenues = new ArrayList<HouseDiningHall>();
		private ArrayList<RetailDiningHall> mRetailVenues = new ArrayList<RetailDiningHall>();
		
		
		public DiningVenues(JSONObject object) throws JSONException {
			mAnnouncementsHtml = object.getString("announcements_html");
			
			JSONArray jsonHouse = object.getJSONObject("venues").getJSONArray("house");
			for (int i = 0; i < jsonHouse.length(); i++) {
				mHouseVenues.add(new HouseDiningHall(jsonHouse.getJSONObject(i)));
			}
			
			JSONArray jsonRetail = object.getJSONObject("venues").getJSONArray("retail");
			for (int i = 0; i < jsonRetail.length(); i++) {
				mRetailVenues.add(new RetailDiningHall(jsonRetail.getJSONObject(i)));
			}
		}


		public List<? extends DiningHall> getHouses() {
			return mHouseVenues;
		}

		public List<? extends DiningHall> getRetail() {
			return mRetailVenues;
		}
		
		public HouseDiningHall getHouseDiningHall(String id) {
			return (HouseDiningHall) findDiningHall(mHouseVenues, id);
		}
		
		public RetailDiningHall getRetailDiningHall(String id) {
			return (RetailDiningHall) findDiningHall(mRetailVenues, id);
		}
		
		private DiningHall findDiningHall(List<? extends DiningHall> halls, String id) {
			for (DiningHall hall : halls) {
				if (hall.getID().equals(id)) {
					return hall;
				}
			}
			throw new RuntimeException("Dining hall with id=" + id + " not found");
		}
		
		public String getAnnouncementsPlainText() {
			return Html.fromHtml(mAnnouncementsHtml).toString();
		}
		
		public String getAnnouncementsHtml() {
			return mAnnouncementsHtml;
		}
	}
	
	public static abstract class DiningHall {
		private String mId;
		private String mName;
		private String mShortName;
		private String mUrl;
		private String mIconUrl;
		private List<String> mPaymentOptions = null;

		private DiningHallLocation mLocation;
		
		public DiningHall(JSONObject object) throws JSONException {
			mId = object.getString("id");
			mUrl = object.getString("url");
			mIconUrl = object.getString("icon_url");
			mName = object.getString("name");
			mShortName = object.getString("short_name");
			mLocation = new DiningHallLocation(object.getJSONObject("location"));
			
			if (object.has("payment")) {
				JSONArray jPayArray = object.getJSONArray("payment");
				ArrayList<String> tempList = new ArrayList<String>();
				for (int i = 0; i < jPayArray.length(); i++) {
					tempList.add(jPayArray.getString(i));
				}
				mPaymentOptions = tempList;
			}
		}
		
		public enum Status {
			CLOSED,
			OPEN
		}
		
		public String getID() {
			return mId;
		}
		
		public String getName() {
			return mName;
		}
		
		public String getShortName() {
			return mShortName;
		}
		
		public String getUrl() {
			return mUrl;
		}
		
		public String getIconUrl() {
			return mIconUrl;
		}
		
		public List<String> getPaymentOptions() {
			return mPaymentOptions;
		}
		
		public String getPaymentOptionString() {
			String options ="";
			for (String s : mPaymentOptions) {
				options+= s + ", ";
			}

			return options.substring(0, options.length() - 2);
		}
		
		public DiningHallLocation getLocation() {
			return mLocation;
		}
		
		public abstract String getTodaysHoursSummary(long currentTime);
		
		public abstract Status getCurrentStatus(long currentTime);	
		
		public abstract String getCurrentStatusSummary(long currentTime);
	}
	
	public static class HouseDiningHall extends DiningHall {
		DailyMealsSchedule mSchedule;
		
		public HouseDiningHall(JSONObject object) throws JSONException {
			super(object);
			JSONArray jsonSchedule = object.getJSONArray("meals_by_day");
			ArrayList<DailyMeals> dailyMealsList = new ArrayList<DailyMeals>();
			for (int i = 0; i < jsonSchedule.length(); i++) {
				dailyMealsList.add(new DailyMeals(jsonSchedule.optJSONObject(i)));
			}
			mSchedule = new DailyMealsSchedule(dailyMealsList);
		}

		private DailyMeals getCurrentDailyMeals(long currentTime) {
			Calendar currentDay = new GregorianCalendar();
			currentDay.setTimeInMillis(currentTime);
			for (DailyMeals dailyMeals : mSchedule.mDailyMealsList) {
				if (DailyMealsSchedule.compareDates(currentDay, dailyMeals.getDay()) == 0) {
					return dailyMeals;
				}
			}
			return null;
		}
		
		@Override
		public String getTodaysHoursSummary(long currentTime) {
			DailyMeals dailyMeals = getCurrentDailyMeals(currentTime);
			if (dailyMeals != null) {
				return dailyMeals.getScheduleSummary();
			}
			return "";
		}

		@Override
		public Status getCurrentStatus(long currentTime) {
			DailyMeals dailyMeals = getCurrentDailyMeals(currentTime);
			if (dailyMeals != null) {
				for (Meal meal : dailyMeals.mMeals.values()) {
					Calendar currentDay = new GregorianCalendar();
					currentDay.setTimeInMillis(currentTime);
					if (meal.isInProgress(currentDay)) {
						return Status.OPEN;
					}
				}
			}			
			return Status.CLOSED;
		}
		
		@Override
		public String getCurrentStatusSummary(long currentTime) {			
			Calendar currentDay = new GregorianCalendar();
			currentDay.setTimeInMillis(currentTime);
			
			DailyMeals dailyMeals = getCurrentDailyMeals(currentTime);
			if (dailyMeals != null) {
				String mealKey = DailyMeals.getFirstMealName();
				while (mealKey != null) {
					Meal meal = dailyMeals.getMeal(mealKey);
					if (meal != null) {
						if (meal.isInProgress(currentDay)) {
							return "Open until " + Meal.sHourMinuteFormat.format(meal.mEnd.getTime()) + 
								Meal.sAmPmFormat.format(meal.mEnd.getTime()).toLowerCase(Locale.US);
						}
						
						if (meal.isUpcoming(currentDay)) {
							return "Opens at " + Meal.sHourMinuteFormat.format(meal.mStart.getTime()) + 
								Meal.sAmPmFormat.format(meal.mStart.getTime()).toLowerCase(Locale.US);
						}
					}
					mealKey = DailyMeals.getNextMealName(mealKey);
				}
			}
			return "Closed for the day";
		}
		
		public DailyMealsSchedule getSchedule() {
			return mSchedule;
		}				
	}
	
	public static class RetailDiningHall extends DiningHall {
		
		static class DailyHours {
			int mDayOfWeek;
			String mMessage;
			String mStartTime;
			String mEndTime;
			
			static String[] days = new String[] {
				"sunday",
				"monday",
				"tuesday",
				"wednesday",
				"thursday",
				"friday",
				"saturday",				
			};
			
			DailyHours(JSONObject object) throws JSONException {
				String dayOfWeek = object.getString("day");
				for (int i = 0; i < days.length; i++) {
					if (dayOfWeek.equalsIgnoreCase(days[i])) {
						mDayOfWeek = i + 1;
						break;
					}
				}
				
				if (!object.isNull("message")) {
					mMessage = object.getString("message");
				}
				if (!object.isNull("start_time") && !object.isNull("end_time")) {
					mStartTime = object.getString("start_time");
					mEndTime = object.getString("end_time");
				}
			}
		}
		
		String mDescriptionHtml;
		String mMenuHtml;
		String mMenuUrl;
		String mHomepageUrl;
		ArrayList<String> mCuisine = new ArrayList<String>();
		
		private static String RETAIL_BOOKMARK_KEY = "retail.bookmarks";
		
		public static void saveBookmarks(Context context, List<RetailDiningHall> list) {
			// serialize hall Ids into comma separated string, save string
			String nameString = "";
			for (RetailDiningHall hall : list) {
				if (list.indexOf(hall) == list.size() - 1) {
					nameString+= hall.getID();
				} else {
					nameString+= hall.getID() + ",";
				}
			}

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(RETAIL_BOOKMARK_KEY, nameString);
			editor.apply();
		}
		
		public static List<RetailDiningHall> getBookmarks(Context context) {
			// load string from preferences, unserialize string into flag names, lookup flags from static map
			// returns list of flags
			ArrayList<RetailDiningHall> bookmarkedHalls = new ArrayList<RetailDiningHall>();
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String nameString = prefs.getString(RETAIL_BOOKMARK_KEY, "");
			if (nameString.isEmpty()) return bookmarkedHalls;	// return empty list if no preference saved
			String [] idSet = nameString.split(",");
			DiningVenues venues = DiningModel.getDiningVenues();
			for (String id : idSet) {
				RetailDiningHall hall = venues.getRetailDiningHall(id);
				if (hall != null) bookmarkedHalls.add(hall);
			}
			return bookmarkedHalls;
		}
		
		public RetailDiningHall(JSONObject object) throws JSONException {
			super(object);
			
			mDescriptionHtml = object.getString("description_html");
			if (!object.isNull("menu_html")) {
				mMenuHtml = object.getString("menu_html");
			}
			if (!object.isNull("menu_url")) {
				mMenuUrl = object.getString("menu_url");
			}
			if (!object.isNull("homepage_url")) {
				mHomepageUrl = object.getString("homepage_url");
			}
			
			if (object.has("cuisine")) {
				JSONArray jsonCuisine = object.getJSONArray("cuisine");
				for (int i = 0; i < jsonCuisine.length(); i++) {
					mCuisine.add(jsonCuisine.getString(i));
				}
			}
		
		}

		@Override
		public String getTodaysHoursSummary(long currentTime) {
			return "8am-8pm";
		}

		@Override
		public Status getCurrentStatus(long currentTime) {
			return Status.OPEN;
		}	
		
		@Override
		public String getCurrentStatusSummary(long currentTime) {
			return "Open till 8pm";
		}	
	}
	
	public static class DailyMealsSchedule {
		
		static SimpleDateFormat sFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
		private List<DailyMeals> mDailyMealsList;
		
		public DailyMealsSchedule(List<DailyMeals> dailyMealsList) {
			mDailyMealsList = dailyMealsList;
		}
		
		public DailyMeals getDailyMeals(Calendar day) {
			String dayString = sFormat.format(day.getTime());
			for (DailyMeals dailyMeals : mDailyMealsList) {
				String dailyMealsDayString = sFormat.format(dailyMeals.mDay.getTime());
				if (dailyMealsDayString.equals(dayString)) {
					return dailyMeals;
				}
			}
			return null;
		}
		
		public List<DailyMeals> getDailyMealsForCurrentWeek(Calendar day) {
			int dayOfWeek = day.get(Calendar.DAY_OF_WEEK) - 1;  //subtract 1 so Sunday is 0
			long millisInDay = 60 * 60 * 24 * 1000;
			long millisFromStartOfWeek = dayOfWeek * millisInDay; //number of millis in day times day of week
			long millisToEndOfWeek = (7 - dayOfWeek) * millisInDay;
			GregorianCalendar startOfWeek = new GregorianCalendar();
			GregorianCalendar endOfWeek = new GregorianCalendar();
			startOfWeek.setTimeInMillis(day.getTimeInMillis() - millisFromStartOfWeek);
			endOfWeek.setTimeInMillis(day.getTimeInMillis() - millisToEndOfWeek);
			
			ArrayList<DailyMeals> mealsInWeek = new ArrayList<DailyMeals>();
			for (DailyMeals mealDay : mDailyMealsList) {
				if (	compareDates(mealDay.getDay(), startOfWeek) >= 0 &&		// compare startOfWeek
						compareDates(endOfWeek, mealDay.getDay()) <= 0 ) {		// compare endOfWeek
					// if within this week add to weekly list
					mealsInWeek.add(mealDay);
				}
			}
			return mealsInWeek;
		}
		
		/*
		 * Compare only the date (ignore hours/minutes/seconds etc...)
		 */
		public static long compareDates(Calendar a, Calendar b) {
			String aString = sFormat.format(a.getTime());
			String bString = sFormat.format(b.getTime());
			if (aString.equals(bString)) {
				return 0;
			}
			return a.getTimeInMillis() - b.getTimeInMillis();
		}

		public boolean isBeforeAllDays(Calendar day) {
			if (mDailyMealsList.size() > 0) {
				Calendar firstDay = mDailyMealsList.get(0).mDay;
				return (compareDates(firstDay, day) > 0);
			} else {
				return true;
			}
		}
		
		public boolean isAfterAllDays(Calendar day) {
			if (mDailyMealsList.size() > 0) {
				Calendar lastDay = mDailyMealsList.get(mDailyMealsList.size()-1).mDay;
				return (compareDates(lastDay, day) < 0);
			} else {
				return true;
			}
		}
	}
	
	public static class DailyMeals {
		Calendar mDay;
		String mMessage;
		HashMap<String, Meal> mMeals = new HashMap<String, Meal>();
		static String[] sMealNames = new String[] {"breakfast", "brunch", "lunch", "dinner"};
		
		public DailyMeals(JSONObject object) throws JSONException {
			String date = object.getString("date");
			mDay = getCalendarDate(date, null);
			if (!object.isNull("message")) {
				mMessage = object.getString("message");
			}
			if (!object.isNull("meals")) {
				JSONArray jsonMeals = object.getJSONArray("meals");
				for (int i = 0; i < jsonMeals.length(); i++) {
					Meal meal = new Meal(jsonMeals.getJSONObject(i), date);
					mMeals.put(meal.getName(), meal);
				}
			}
		}
		
		public String getMessage() {
			return mMessage;
		}
		
		public Calendar getDay() {
			return mDay;
		}

		public int getMealCount() {
			if (mMeals != null) {
				return mMeals.size();
			}
			return 0;
		}
		
		public Meal getMeal(String name) {
			return mMeals.get(name);
		}
		
		public HashMap<String, String> getMealTimes() {
			// return HashMap of format <Meal Name, Times Summary> both of type String.
			HashMap<String, String> schedule = new HashMap<String, String>();
			Set<String> mealKeys = mMeals.keySet();
			
			for (String key : mealKeys) {
				Meal meal = mMeals.get(key);
				if (meal.getMessage() != null && !meal.getMessage().isEmpty()) {
					schedule.put(meal.getCapitalizedName(), meal.getMessage());			// use message if we have it
				} else {
					schedule.put(meal.getCapitalizedName(), meal.getScheduleSummary());
				}
			}
			
			return schedule;
		}

		public String getScheduleSummary() {
			String summaryString = "";
			boolean first = true;
			for (String key : sMealNames) {
				if (mMeals.containsKey(key)) {
					Meal meal = mMeals.get(key);
					if (meal.getScheduleSummary() != null) {
						if (!first) {
							summaryString += ", ";
						}
						first = false;
						summaryString += meal.getScheduleSummary();
					}
				}
			}
			
			return summaryString;
		}
		
		private static int getMealIndex(String name) {
			for(int i = 0; i < sMealNames.length; i++) {
				if (sMealNames[i].equals(name)) {
					return i;
				}
			}
			throw new RuntimeException("'" + name + "' is not a valid meal name");
		}
		
		public static String getPreviousMealName(String name) {
			if (name != null) {
				int index = getMealIndex(name) - 1;
				if (index >= 0) {
					return sMealNames[index];
				}
			}
			return null;
		}
		
		public static String getNextMealName(String name) {
			if (name != null) {
				int index = getMealIndex(name) + 1;
				if (index < sMealNames.length) {
					return sMealNames[index];
				} 
			}
			return null;
		}
		
		public static String getFirstMealName() {
			return sMealNames[0];
		}
		
		public static String getLastMealName() {
			return sMealNames[sMealNames.length-1];
		}
	}
	
	public static class Meal {
		static SimpleDateFormat sHourMinuteFormat = new SimpleDateFormat("h:mm", Locale.US);
		static SimpleDateFormat sAmPmFormat = new SimpleDateFormat("a", Locale.US);
		
		String mName;
		String mMessage;
		Calendar mStart;
		Calendar mEnd;
		ArrayList<MenuItem> mMenuItems;
		
		public Meal(JSONObject object, String date) throws JSONException {
			mName = object.getString("name");
			if (!object.isNull("message")) {
				mMessage = object.getString("message");
			}
			if (!object.isNull("start_time") && !object.isNull("end_time")) {
				mStart = getCalendarDate(date, object.getString("start_time"));
				mEnd = getCalendarDate(date, object.getString("end_time"));
			}
			if (!object.isNull("items")) {
				mMenuItems = new ArrayList<MenuItem>();
				JSONArray jsonItems = object.getJSONArray("items");
				for (int i = 0; i < jsonItems.length(); i++) {
					mMenuItems.add(new MenuItem(jsonItems.getJSONObject(i)));
				}
			}
		}

		public String getCapitalizedName() {
			return mName.substring(0, 1).toUpperCase(Locale.US) + mName.substring(1);
		}
		
		public String getName() {
			return mName;
		}
		
		public String getMessage() {
			return mMessage;
		}
		
		public Calendar getStart() {
			return mStart;
		}
		
		public Calendar getEnd() {
			return mEnd;
		}
		
		public String getScheduleSummaryForColumns() {
			if (mStart != null && mEnd != null) {
				return sHourMinuteFormat.format(mStart.getTime()) + " - " +
						sHourMinuteFormat.format(mEnd.getTime()) + " " +
						sAmPmFormat.format(mEnd.getTime()).toLowerCase(Locale.US);
			} else {
				return null;
			}
		}
		
		public String getScheduleSummary() {
			if (mStart != null && mEnd != null) {
				return formatTimeForScheduleSpan(mStart) + " - " + formatTimeForScheduleSpan(mEnd);
			} else {
				return null;
			}
		}
		
		private String formatTimeForScheduleSpan(Calendar cal) {
			SimpleDateFormat shortTimeFormat = new SimpleDateFormat("h", Locale.US);
			
			SimpleDateFormat df = null;
			if (cal.get(Calendar.MINUTE) == 0) {
				df = shortTimeFormat;
			} else {
				df = sHourMinuteFormat;
			}
			
			String timeString = df.format(cal.getTime()) + sAmPmFormat.format(cal.getTime()).toLowerCase();
			return timeString;
		}
		
		
		public List<MenuItem> getMenuItems() {
			return mMenuItems;
		}

		public boolean isFinished(Calendar day) {
			if (mEnd != null) {
				return (day.getTimeInMillis() > mEnd.getTimeInMillis());
			}
			return false;
		}

		public boolean isUpcoming(Calendar day) {
			if (mStart != null) {
				return (day.getTimeInMillis() < mStart.getTimeInMillis());
			}
			return false;
		}

		public boolean isInProgress(Calendar day) {
			if (mStart != null && mEnd != null) {
				return (day.getTimeInMillis() > mStart.getTimeInMillis()) &&
						(day.getTimeInMillis() < mEnd.getTimeInMillis());
			}
			return false;
		}
	}
	
	public static class MenuItem {
		String mName;
		String mDescription;
		String mStation;
		ArrayList<DiningDietaryFlag> mDietaryFlags = new ArrayList<DiningDietaryFlag>();
		
		public MenuItem(JSONObject object) throws JSONException {
			mName = object.getString("name");
			mDescription = object.optString("description", null);
			mStation = object.getString("station");
			if (object.has("dietary_flags")) {
				JSONArray flags = object.getJSONArray("dietary_flags");
				for (int i = 0; i < flags.length(); i++) {
					mDietaryFlags.add(DiningDietaryFlag.flagsByName().get(flags.getString(i)));
				}
			}
		}		
		
		public String getStation() {
			return mStation;
		}
		
		public String getName() {
			return mName;
		}
		
		public String getDescription() {
			return mDescription;
		}
		
		public List<DiningDietaryFlag> getDietaryFlags() {
			return mDietaryFlags;
		}
	}
	
	public static class DiningHallLocation {
		
		// required
		String mStreet;
		String mCity;
		String mState;
		String mZipcode;
		
		// optional
		String mDescription;
		String mRoomNumber;
		float mLatitude;
		float mLongitude;
		
		public DiningHallLocation(JSONObject object) throws JSONException {
			mStreet = object.getString("street");
			mCity = object.getString("city");
			mState = object.getString("state");
			mZipcode = object.getString("zipcode");
			
			if (object.has("description")) {
				mDescription = object.getString("description");
			}
			
			if (object.has("mit_room_number")) {
				mRoomNumber = object.getString("mit_room_number");
			}
			
			if (object.has("latitude") && object.has("longitude")) {
				mLatitude = (float) object.getDouble("latitude");
				mLongitude = (float) object.getDouble("longitude");
			}
		}
	}
	
	public static class DiningDietaryFlag implements Parcelable, Comparable<DiningDietaryFlag> {
		private static String FILTER_PREFERENCE_KEY = "filter.preference";
		private static HashMap<String, DiningDietaryFlag> sFlagsMap;
		
		private String mName;
		private String mDisplayName;
		private int mIconId;
		
		public static Collection<DiningDietaryFlag> allFlags() {
			return flagsByName().values();
		}
		
		public static HashMap<String, DiningDietaryFlag> flagsByName() {
			if (sFlagsMap == null) {
				HashMap<String, DiningDietaryFlag> map = new HashMap<String, DiningDietaryFlag>();
				map.put("farm to fork", new DiningDietaryFlag("farm to fork", "Farm to Fork", R.drawable.dining_farm_to_fork));
				map.put("organic", new DiningDietaryFlag("organic", "Organic", R.drawable.dining_organic));
				map.put("seafood watch", new DiningDietaryFlag("seafood watch", "Seafood Watch", R.drawable.dining_seafood_watch));
				map.put("vegan", new DiningDietaryFlag("vegan", "Vegan", R.drawable.dining_vegan));
				map.put("vegetarian", new DiningDietaryFlag("vegetarian", "Vegetarian", R.drawable.dining_vegetarian));
				map.put("for your well-being", new DiningDietaryFlag("for your well-being", "For Your Well-Being", R.drawable.dining_well_being));
				map.put("made without gluten", new DiningDietaryFlag("made without gluten", "Made Without Gluten", R.drawable.dining_made_without_gluten));
				map.put("halal", new DiningDietaryFlag("halal", "Halal", R.drawable.dining_halal));
				map.put("kosher", new DiningDietaryFlag("kosher", "Kosher", R.drawable.dining_kosher));
				map.put("humane", new DiningDietaryFlag("humane", "Humane", R.drawable.dining_humane));
				map.put("in balance", new DiningDietaryFlag("in balance", "In Balance", R.drawable.dining_in_balance));
				sFlagsMap = map;
			}
			return sFlagsMap;
		}
		
		public static void saveFilters(Context context, List<DiningDietaryFlag> list) {
			// serialize flag names into comma separated string, save string
			String nameString = "";
			for (DiningDietaryFlag flag : list) {
				if (list.indexOf(flag) == list.size() - 1) {
					nameString+= flag.getName();
				} else {
					nameString+= flag.getName() + ",";
				}
			}

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(FILTER_PREFERENCE_KEY, nameString);
			editor.apply();
		}
		
		public static List<DiningDietaryFlag> loadFilters(Context context) {
			// load string from preferences, unserialize string into flag names, lookup flags from static map
			// returns list of flags
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String nameString = prefs.getString(FILTER_PREFERENCE_KEY, "");
			String [] nameSet = nameString.split(",");
			ArrayList<DiningDietaryFlag> flagList = new ArrayList<DiningDietaryFlag>();
			for (String name : nameSet) {
				DiningDietaryFlag flag = DiningDietaryFlag.flagsByName().get(name);
				if (flag != null) flagList.add(flag);
			}
			return flagList;
		}
		
		private DiningDietaryFlag(String name, String display, int resource) {
			mName = name;
			mDisplayName= display;
			mIconId = resource;
		}
		
		@Override
		public String toString() {
			return "DiningDietaryFlag:" + this.hashCode() + " name: " + mName;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof DiningDietaryFlag) {
				DiningDietaryFlag o = (DiningDietaryFlag)obj;
				if (this.getName().equals(o.getName())) {
					return true;
				}
			}
			return false;
		}

		public String getName() {
			return mName;
		}

		public String getDisplayName() {
			return mDisplayName;
		}

		public int getIconId() {
			return mIconId;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(mName);
			dest.writeString(mDisplayName);
			dest.writeInt(mIconId);
		}
		
		public static final Parcelable.Creator<DiningDietaryFlag> CREATOR = new Parcelable.Creator<DiningDietaryFlag>() {
					@Override
					public DiningDietaryFlag createFromParcel(Parcel in) {
						return new DiningDietaryFlag(in.readString(), in.readString(), in.readInt());
					}

					@Override
					public DiningDietaryFlag[] newArray(int size) {
						return new DiningDietaryFlag[size];
					}
		};

		@Override
		public int compareTo(DiningDietaryFlag another) {
			return this.getName().compareTo(another.getName());
		}
		
		public static Comparator<DiningDietaryFlag> NameDescendingComparator = new Comparator<DiningDietaryFlag>() {
			@Override
			public int compare(DiningDietaryFlag flag1, DiningDietaryFlag flag2) {
				String name1 = flag1.getName().toUpperCase();
				String name2 = flag2.getName().toUpperCase();
				return name2.compareTo(name1);
			}
		};
	}
	
	public static class DiningLink {
		private String mTitle;
		//private String mSubtitle;
		private String mUrl;
		
		public DiningLink(String title, String url) {
			mTitle = title;
			mUrl = url;
		}
		
		public String getTitle() {
			return mTitle;
		}
		
		public String getUrl() {
			return mUrl;
		}
	}
}
