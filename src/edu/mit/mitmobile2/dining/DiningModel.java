package edu.mit.mitmobile2.dining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

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
		ArrayList<DailyMeals> mSchedule = new ArrayList<DailyMeals>();
		
		public HouseDiningHall(JSONObject object) throws JSONException {
			super(object);
			JSONArray jsonSchedule = object.getJSONArray("meals_by_day");
			for (int i = 0; i < jsonSchedule.length(); i++) {
				mSchedule.add(new DailyMeals(jsonSchedule.optJSONObject(i)));
			}
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
		
		public List<DailyMeals> getSchedule() {
			return mSchedule;
		}				
	}
	
	public static class RetailDiningHall extends DiningHall {
		ArrayList<DailyMeals> mSchedule = new ArrayList<DailyMeals>();
		
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
	
	public static class DailyMeals {
		Calendar mDay;
		String mMessage;
		ArrayList<Meal> mMeals;
		
		public DailyMeals(JSONObject object) throws JSONException {
			String date = object.getString("date");
			mDay = getCalendarDate(date, null);
			if (!object.isNull("message")) {
				mMessage = object.getString("message");
			}
			if (!object.isNull("meals")) {
				mMeals = new ArrayList<Meal>();
				JSONArray jsonMeals = object.getJSONArray("meals");
				for (int i = 0; i < jsonMeals.length(); i++) {
					mMeals.add(new Meal(jsonMeals.getJSONObject(i), date));
				}
			}
		}
		
		public String getMessage() {
			return mMessage;
		}
		
		public Calendar getDay() {
			return mDay;
		}

		public List<Meal> getMeals() {
			if (mMeals != null) {
				return mMeals;
			} else {
				return Collections.emptyList();
			}
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
