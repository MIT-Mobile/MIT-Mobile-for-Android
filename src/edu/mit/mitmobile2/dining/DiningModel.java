package edu.mit.mitmobile2.dining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.MobileWebApi;

import android.content.Context;
import android.os.Handler;
import android.text.Html;

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
		private String mUrl;
		private DiningHallLocation mLocation;
		
		public DiningHall(JSONObject object) throws JSONException {
			mId = object.getString("id");
			mUrl = object.getString("url");
			mName = object.getString("name");
			mLocation = new DiningHallLocation(object.getJSONObject("location"));
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

		@Override
		public String getTodaysHoursSummary(long currentTime) {
			return "8am-10:59am, 11am-3pm";
		}

		@Override
		public Status getCurrentStatus(long currentTime) {
			return Status.CLOSED;
		}
		
		@Override
		public String getCurrentStatusSummary(long currentTime) {
			return "Opens at 5:30pm";
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
		ArrayList<String> mPayment = new ArrayList<String>();
		
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
			
			if (object.has("payment")) {
				JSONArray jsonPayment = object.getJSONArray("payment");
				for (int i = 0; i < jsonPayment.length(); i++) {
					mPayment.add(jsonPayment.getString(i));
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
		
		/*
		 * Compare only the date (ignore hours/minutes/seconds etc...)
		 */
		private static long compareDates(Calendar a, Calendar b) {
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
		ArrayList<String> mDietaryFlags = new ArrayList<String>();
		
		public MenuItem(JSONObject object) throws JSONException {
			mName = object.getString("name");
			mDescription = object.optString("description", null);
			mStation = object.getString("station");
			if (object.has("dietary_flags")) {
				JSONArray flags = object.getJSONArray("dietary_flags");
				for (int i = 0; i < flags.length(); i++) {
					mDietaryFlags.add(flags.getString(i));
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
		
		public List<String> getDietaryFlags() {
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
