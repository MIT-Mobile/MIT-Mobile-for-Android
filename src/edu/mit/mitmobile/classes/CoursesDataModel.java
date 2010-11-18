package edu.mit.mitmobile.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import edu.mit.mitmobile.Global;
import edu.mit.mitmobile.JSONParser;
import edu.mit.mitmobile.MobileWebApi;
import edu.mit.mitmobile.NaturalSort;
import edu.mit.mitmobile.alerts.NotificationsAlarmReceiver;
import edu.mit.mitmobile.objs.CourseItem;
import edu.mit.mitmobile.objs.CourseListItem;
import edu.mit.mitmobile.objs.SearchResults;

public class CoursesDataModel {

	// Stellar related
	static SharedPreferences pref;
	public static HashMap<String,CourseItem> myCourses;

	public static final String KEY_COURSE_ID = "course_id";
	
	public static String PREF_KEY_STELLAR_IDS = "pref_ids";
	public static String PREF_KEY_STELLAR_TITLES = "pref_titles";
	public static String PREF_KEY_STELLAR_LAST_UPDATES = "pref_updates";
	public static String PREF_KEY_STELLAR_READ = "pref_read";
	
	static public ArrayList<String> cur_course_ids;  // 1-10 or 11-20, etc
	static public ArrayList<CourseItem> cur_subjects;  // 1.01, 1.02, etc  or 2.00,2.01,etc

	// List
	static private List<CourseListItem> unsorted_courses;
	// Subject
	static private HashMap<String,ArrayList<CourseItem>> subjects_cache = new HashMap<String,ArrayList<CourseItem>>();
	//static private HashMap<String,ArrayList<CourseItem>> details_cache;  // TODO combine with subjects_cache
	static private HashMap<String,CourseItem> details_cache = new HashMap<String,CourseItem>();  // TODO combine with subjects_cache
	
	/*********************************/
	public static ArrayList<CourseItem> getFavoritesList() {
		
		Collection<CourseItem> c = myCourses.values();
		
		ArrayList<CourseItem> favs = new ArrayList<CourseItem>( c );

		//Collections.sort(favs, new NaturalComparator() );
		
		Collections.sort(favs, 
			new Comparator<CourseItem>() {
				@Override
				public int compare(CourseItem object1, CourseItem object2) {
					String[] firstParts = object1.masterId.split("\\.");                                                                    
					String[] secondParts = object2.masterId.split("\\.");                                                                   
					int length = Math.min(firstParts.length, secondParts.length);                                                           

					for(int i=0; i < length; i++) {                                                                                         
						if(NaturalSort.compare(firstParts[i], secondParts[i]) != 0) {
						    return NaturalSort.compare(firstParts[i], secondParts[i]);                                                      
						}
					}

					return firstParts.length - secondParts.length;                                                                  
				}			
		});
		
		
		return favs;
	}	
	/*********************************/
	public static ArrayList<String> getCourseIds() {
		return cur_course_ids;
	}
	/*********************************/
	public static void fetchList(Context context, final Handler uiHandler) {
		
		if(unsorted_courses != null) {
			MobileWebApi.sendSuccessMessage(uiHandler);
			return;
		}
		
		HashMap<String, String> searchParameters = new HashMap<String, String>();
		searchParameters.put("module", "stellar");
		searchParameters.put("command", "courses");
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Courses", context, uiHandler);
		
		webApi.requestJSONArray(searchParameters, new MobileWebApi.JSONArrayResponseListener(
				
			new MobileWebApi.DefaultErrorListener(uiHandler), null) {
				@Override
				public void onResponse(JSONArray array) {
					CourseListParser parser = new CourseListParser();
					unsorted_courses = parser.parseJSONArray(array);
					MobileWebApi.sendSuccessMessage(uiHandler);				
			}
		});
		
	}

	public static List<CourseListItem> getList() {
		
		return unsorted_courses;
		
	}
	/*********************************/
	public static void fetchSubjectList(Handler myHandler, final String courseId) {
		
		if(subjects_cache.containsKey(courseId)) {
			myHandler.sendEmptyMessage(0);
			return;
		}		
		
		final JSONParser cp = new CourseSubjectParser(false,false) {
			@Override
			public void saveData() {
				cur_subjects = (ArrayList<CourseItem>) items;
				if(cur_subjects.size() > 0) {
					subjects_cache.put(courseId, cur_subjects);
				}
			}
		};

		// # Subject
		//command: subjectList
		//id: courseId
		String params = "&command=subjectList&id="+courseId;
		
		
		cp.getJSONThread(params, myHandler);
		
	}
	/*********************************/
	public static void fetchDetailsList(final Context context, final Handler myHandler, final String masterId) {
		
		//boolean hit = details_cache.containsKey(masterId);
		//if (hit) {
		//	myHandler.sendEmptyMessage(0);
		//	return;
		//}
		
		
		final JSONParser cp = new CourseSubjectParser(false,true) {
			@Override
			public void saveData() {
				if (items.size()>0) {
					CourseItem ci = (CourseItem) items.get(0);
					details_cache.put(masterId, ci);
				} else {
					myHandler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, MobileWebApi.NETWORK_ERROR, Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		};
		cp.expectingObject = true;


		// # Details
		//module: stellar
		//command: subjectInfo
		//id: subjectId
		String params = "&command=subjectInfo&id="+masterId;
		
		
		cp.getJSONThread(params, myHandler);
		
	}


	
	/*********************************/
	public static ArrayList<CourseItem> getSubjectList(String courseId) {
		return subjects_cache.get(courseId);
	}

	public static CourseItem getDetails(String masterId) {
		return details_cache.get(masterId);
	}
	/****************************************************/
	static void persist() {
		// TODO save module state...
	}
	static void load() {
		// TODO restore module state...
	}
	/****************************************************/
	static void saveMyStellar(Context ctx) {

		pref = ctx.getSharedPreferences(Global.PREFS_STELLAR,Context.MODE_PRIVATE);  
		
		SharedPreferences.Editor editor = pref.edit();
		
		String concat1 = "";
		String concat2 = "";
		String concat3 = "";
		String concat4 = "";
		for (CourseItem c : myCourses.values()) {
			concat1 += c.masterId + "###";
			concat2 += c.title + "###";
			concat3 += c.last_announcement + "###";
			concat4 += c.read + "###";
		}

		Log.d("CoursesDataModel","stellar-alert: save-> "+concat1);
		Log.d("CoursesDataModel","stellar-alert: save-> "+concat2);
		Log.d("CoursesDataModel","stellar-alert: save-> "+concat3);
		Log.d("CoursesDataModel","stellar-alert: save-> "+concat4);
		
		editor.putString(PREF_KEY_STELLAR_IDS, concat1);  
		editor.putString(PREF_KEY_STELLAR_TITLES, concat2);  
		editor.putString(PREF_KEY_STELLAR_LAST_UPDATES, concat3);  
		editor.putString(PREF_KEY_STELLAR_READ, concat4);  
		
		boolean success = editor.commit();
		if (!success) {
			Log.e("CoursesDataModel","stellar-alert: save-> failed commit");
		}
		
	}
	
	public static void getMyStellar(Context ctx) {

		pref = ctx.getSharedPreferences(Global.PREFS_STELLAR,Context.MODE_PRIVATE);  
		
		myCourses = new HashMap<String,CourseItem>();
		
		String masterIds = pref.getString(PREF_KEY_STELLAR_IDS, null); 
		String descs     = pref.getString(PREF_KEY_STELLAR_TITLES, null); 
		String updates   = pref.getString(PREF_KEY_STELLAR_LAST_UPDATES, null); 
		String reads   = pref.getString(PREF_KEY_STELLAR_READ, null); 
	
		Log.d("CoursesDataModel","stellar-alert: get-> "+masterIds);
		Log.d("CoursesDataModel","stellar-alert: get-> "+descs);
		Log.d("CoursesDataModel","stellar-alert: get-> "+updates);
		Log.d("CoursesDataModel","stellar-alert: get-> "+reads);
		
		if ((masterIds!=null)&&(descs!=null)&&(updates!=null)) {
			String[] classes_alarms = masterIds.split("###");
			String[] classes_descs  = descs.split("###");
			String[] classes_updates = updates.split("###");
			String[] classes_reads = reads.split("###");
			if (classes_descs.length!=classes_alarms.length) {
				Log.e("CoursesDataModel","stellar-alert: get-> lengths");
				if (Global.DEBUG) throw new RuntimeException("");
				return;  // TODO flag error?
			}
			if (classes_descs.length!=classes_updates.length) {
				Log.e("CoursesDataModel","stellar-alert: get-> lengths");
				return; 
			}
			for (int x=0; x<classes_descs.length; x++) {
				if ("".equals(classes_updates[x])) continue;
				String id = classes_alarms[x];
				String title = classes_descs[x];
				if ("".equals(id)) continue;
				CourseItem c = new CourseItem();
				c.masterId = id;
				c.title = title;
				c.last_announcement = Long.valueOf(classes_updates[x]);
				c.read = Boolean.valueOf(classes_reads[x]).booleanValue();
				myCourses.put(id, c);
			}
		} else {
			Log.d("CoursesDataModel","stellar-alert: get-> ALL NULL");
		}
		
		
	}

	/************************************************************************/
	 public static void setAlarm(Context ctx, CourseItem ci) {

		    CourseItem alertId = myCourses.get(ci.masterId);
		
			// Adjust alert
			if (alertId==null) {
				ci.last_announcement = System.currentTimeMillis() / 1000;  // ignore announcements before now (note conversion to unixtime)
				myCourses.put(ci.masterId, ci);
			} else {
				myCourses.remove(ci.masterId);
			}
			
			saveMyStellar(ctx);
			
			startAlarms(ctx);

		}
	 
	 
	 public static void startAlarms(Context ctx) {
		
		 	AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Service.ALARM_SERVICE);

			// Cancel or Schedule alarm?
			if (myCourses.isEmpty()) {

				Log.d("CoursesDataModel","stellar-alert: startAlarms-> cancel");
				
				// Cancel 
				Intent i = new Intent(ctx, NotificationsAlarmReceiver.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
				alarmManager.cancel(pendingIntent);
				
			} else {

				Log.d("CoursesDataModel","stellar-alert: startAlarms-> schedule");
				
				// Schedule 
				
				long curTime  = System.currentTimeMillis();
				long wakeTime = curTime + 5*60*1000; 
				long period = 30*60*1000;  // TODO get from Pref
				
				if (Global.DEBUG) {
					wakeTime = curTime + 30*1000; 
					period = 3*60*1000;
				}
				
				
				Intent i = new Intent(ctx, NotificationsAlarmReceiver.class);
				
				i.setAction(NotificationsAlarmReceiver.ACTION_ALARM_CLASS);
				
				PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  wakeTime, period, pendingIntent);
				
			}
	 }
	/************************************************************************/
	/************************************************************************/
	
	private static HashMap<String, List<CourseItem>> searchCache = new HashMap<String, List<CourseItem>>();
	private static HashMap<String, CourseItem> recentlyViewedCache = new HashMap<String, CourseItem>();
	
	public static void executeSearch(final String searchTerm, Context context, final Handler uiHandler) {
		
		if(searchCache.containsKey(searchTerm)) {
			MobileWebApi.sendSuccessMessage(uiHandler, 
				new SearchResults<CourseItem>(searchTerm, searchCache.get(searchTerm))
			);	
			return;
		}
		
		HashMap<String, String> searchParameters = new HashMap<String, String>();
		searchParameters.put("module", "stellar");
		searchParameters.put("command", "search");
		searchParameters.put("query", searchTerm);

		MobileWebApi webApi = new MobileWebApi(false, true, "Courses", context, uiHandler);
		
		webApi.requestJSONArray(searchParameters, new MobileWebApi.JSONArrayResponseListener(
				
			new MobileWebApi.DefaultErrorListener(uiHandler), null ) {
				@Override
				public void onResponse(JSONArray array) {
					CourseSubjectParser parser = new CourseSubjectParser(false,false);
					parser.parseJSONArray(array);
					List<CourseItem> Courses = parser.items;
					searchCache.put(searchTerm, Courses);
					MobileWebApi.sendSuccessMessage(uiHandler, new SearchResults<CourseItem>(searchTerm, Courses));				
			}
		});
		
	}
	
	public static List<CourseItem>executeLocalSearch(String searchTerms) {
		return searchCache.get(searchTerms);
	}
	
	public static int getPosition(List<CourseItem> Courses, String masterId) {
		for(int index = 0; index < Courses.size(); index++) {
			if(Courses.get(index).masterId.equals(masterId)) {
				return index;
			}
		}
		return -1;
	}
	
	public static void markAsRecentlyViewed(CourseItem Course) {
		Course.lastViewed = new Date();
		recentlyViewedCache.put(Course.masterId, Course);
	}
	
	public static List<CourseItem> getRecentlyViewed() {
		List<CourseItem> Courses = new ArrayList<CourseItem>(recentlyViewedCache.values());

		// sort in toe descending order by lastViewed
		// i.e. the most recent should be first
		Collections.sort(Courses, 
			new Comparator<CourseItem>() {
				@Override
				public int compare(CourseItem object1, CourseItem object2) {
					return object2.lastViewed.compareTo(object1.lastViewed);
					//return 0;
				}			
		});
		
		return Courses;		
	}
	
}
