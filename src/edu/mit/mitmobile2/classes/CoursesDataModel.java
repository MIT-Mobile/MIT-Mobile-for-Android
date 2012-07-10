package edu.mit.mitmobile2.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.JSONParser;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.ErrorResponseListener;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;
import edu.mit.mitmobile2.NaturalSort;
import edu.mit.mitmobile2.alerts.C2DMReceiver;
import edu.mit.mitmobile2.alerts.C2DMReceiver.Device;
import edu.mit.mitmobile2.objs.CourseItem;
import edu.mit.mitmobile2.objs.CourseListItem;
import edu.mit.mitmobile2.objs.SearchResults;

public class CoursesDataModel {

	// Stellar related

	public static HashMap<String,CourseItem> myCourses;

	public static final String KEY_COURSE_ID = "course_id";
	
	private static String PREF_KEY_TERM = "term"; 
	private static String PREF_STELLAR_VERSION = "version";
	public static String PREF_KEY_STELLAR_IDS = "pref_ids";
	public static String PREF_KEY_STELLAR_TITLES = "pref_titles";
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
	public static ArrayList<CourseItem> getFavoritesList(Context context) {
		
		if (myCourses == null) {
			getMyStellar(context);
		}
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
				@SuppressWarnings("unchecked")
				ArrayList<CourseItem> cur_subjects_unsafe = (ArrayList<CourseItem>) items;
				cur_subjects = cur_subjects_unsafe;
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

		SharedPreferences pref = ctx.getSharedPreferences(Global.PREFS_STELLAR,Context.MODE_PRIVATE);  
		
		SharedPreferences.Editor editor = pref.edit();
		
		String concat1 = "";
		String concat2 = "";
		String concat4 = "";
		for (CourseItem c : myCourses.values()) {
			concat1 += c.masterId + "###";
			concat2 += c.title + "###";
			concat4 += c.read + "###";
		}

		Log.d("CoursesDataModel","stellar-alert: save-> "+concat1);
		Log.d("CoursesDataModel","stellar-alert: save-> "+concat2);
		Log.d("CoursesDataModel","stellar-alert: save-> "+concat4);
		
		editor.putString(PREF_KEY_STELLAR_IDS, concat1);  
		editor.putString(PREF_KEY_STELLAR_TITLES, concat2);  
		editor.putString(PREF_KEY_STELLAR_READ, concat4);  
		
		boolean success = editor.commit();
		if (!success) {
			Log.e("CoursesDataModel","stellar-alert: save-> failed commit");
		}
		
	}
	
	public static void getMyStellar(Context ctx) {

		SharedPreferences pref = ctx.getSharedPreferences(Global.PREFS_STELLAR,Context.MODE_PRIVATE);  
		
		myCourses = new HashMap<String,CourseItem>();
		
		String masterIds = pref.getString(PREF_KEY_STELLAR_IDS, null); 
		String descs     = pref.getString(PREF_KEY_STELLAR_TITLES, null); 
		String reads   = pref.getString(PREF_KEY_STELLAR_READ, null); 
	
		Log.d("CoursesDataModel","stellar-alert: get-> "+masterIds);
		Log.d("CoursesDataModel","stellar-alert: get-> "+descs);
		Log.d("CoursesDataModel","stellar-alert: get-> "+reads);
		
		if ((masterIds!=null)&&(descs!=null)) {
			String[] classes_alarms = masterIds.split("###");
			String[] classes_descs  = descs.split("###");
			String[] classes_reads = reads.split("###");
			if (classes_descs.length!=classes_alarms.length) {
				Log.e("CoursesDataModel","stellar-alert: get-> lengths");
				if (Global.DEBUG) throw new RuntimeException("");
				return;  // TODO flag error?
			}
			for (int x=0; x<classes_descs.length; x++) {
				String id = classes_alarms[x];
				String title = classes_descs[x];
				if ("".equals(id)) continue;
				CourseItem c = new CourseItem();
				c.masterId = id;
				c.title = title;
				c.read = Boolean.valueOf(classes_reads[x]).booleanValue();
				myCourses.put(id, c);
			}
		} else {
			Log.d("CoursesDataModel","stellar-alert: get-> ALL NULL");
		}
		
		
	}

	/************************************************************************/
	
    enum SubscriptionType {
    	SUBSCRIBE,
    	UNSUBSCRIBE
    }
     
     private static Map<String, String> courseSubscriptionParameters(final Context context, CourseItem ci, SubscriptionType subscriptionType) {
			HashMap<String, String> params = new HashMap<String, String>();
			
			params.put("module", "stellar");
			params.put("command", "myStellar");
			params.put("action", subscriptionType.toString().toLowerCase(Locale.US));
			
			// device parameters
			Device device = C2DMReceiver.getDevice(context);
			params.put("device_id", Long.toString(device.getDeviceId()));
			params.put("pass_key", Long.toString(device.getPassKey()));		
			params.put("device_type", "android");
			
			params.put("term", ci.getTermId());
			params.put("subject", ci.masterId);
			
			return params;
     }
     
	 public static void subscribeForCourse(final Context context, final CourseItem ci, SubscriptionType subscriptionType, final Handler uiHandler) {

		 	Map<String, String> params = courseSubscriptionParameters(context, ci, subscriptionType);
			
			MobileWebApi webApi = new MobileWebApi(false, true, "My Stellar", context, uiHandler);		
			
			webApi.requestJSONObject(params, 
				new MobileWebApi.JSONObjectResponseListener(new MobileWebApi.DefaultErrorListener(uiHandler), null) {					
					@Override
					public void onResponse(JSONObject object) throws JSONException {
						if (object.has("success")) {
							MobileWebApi.sendSuccessMessage(uiHandler);
							saveCourseAlert(context, ci);
						} else {
							MobileWebApi.sendErrorMessage(uiHandler);
						}
					}
				}
			);
	    }
	 
	 private static class DetectErrorListener implements ErrorResponseListener {
		 
		public boolean mSubscriptionFailed = false; 
		
		@Override
		public void onError() {
			mSubscriptionFailed = true;
		}
	 }
	 
	 private static class TermResponseListener extends MobileWebApi.JSONObjectResponseListener {

		public String mTerm;
		
		public TermResponseListener(ErrorResponseListener errorListener) {
			super(errorListener, null);
		}

		@Override
		public void onResponse(JSONObject object) throws ServerResponseException, JSONException {
			mTerm = object.getString("term");
		}
		 
	 }
	 
	/*
	 * This function will attempt to subscribe to all the course on your favorites
	 */
	 public static void updateFavoritesForTerm(final Context context) {
		 
		 final ArrayList<CourseItem> favorites = getFavoritesList(context);	 
		 
		 new Thread() {
			 
			 @Override
			 public void run() {
				 final DetectErrorListener errorListener = new DetectErrorListener();
				 
				 String cachedTerm = getTerm(context);
				 
				 HashMap<String, String> termParams = new HashMap<String, String>();
				 termParams.put("module", "stellar");
				 termParams.put("command", "term");
				 
				 

				 
				 // term has never been set need to

				 MobileWebApi webApiTerm = new MobileWebApi(false, false, "My Stellar", null, null);
				 TermResponseListener termResponseListener = new TermResponseListener(errorListener);
				 webApiTerm.requestJSONObject(termParams, termResponseListener);

				 String currentTerm = termResponseListener.mTerm;
				 if (currentTerm != null) {
					 if (cachedTerm == null || !cachedTerm.equals(currentTerm)) {
						 updateTerm(context, termResponseListener.mTerm);
						 
						 if (cachedTerm != null) {
							 removeAllFavorites(context);
						 }
					 }
				 }
				 
				 SharedPreferences pref = context.getSharedPreferences(Global.PREFS_STELLAR,Context.MODE_PRIVATE);  
				 int version = pref.getInt(PREF_STELLAR_VERSION, 1);
				 
				 if (version == 2) {
					 // migration  not needed
					 return;
				 }
				 
				 for (CourseItem favorite : favorites) {
					 favorite.setTerm(termResponseListener.mTerm);
					 
					 Map<String, String> params = courseSubscriptionParameters(context, favorite, SubscriptionType.SUBSCRIBE);
					 MobileWebApi webApi = new MobileWebApi(false, false, "My Stellar", null, null);
					 webApi.requestJSONObject(params,
						new MobileWebApi.JSONObjectResponseListener(errorListener, null) {
							@Override	
						 	public void onResponse(JSONObject object) throws ServerResponseException, JSONException {
									if (!object.has("success")) {
										errorListener.mSubscriptionFailed = true;
									}						
								}
					 		});
					 
						 if (errorListener.mSubscriptionFailed) {
							 break;
						 }
				 }
				 
				 if (!errorListener.mSubscriptionFailed) {
					 // migration succeeded
					 Editor editor = pref.edit();
					 editor.putInt(PREF_STELLAR_VERSION, 2);
					 editor.commit();
				 }
			 }
		 }.start();
	 }
	 
	 private static void removeAllFavorites(Context ctx) {
		 myCourses = new HashMap<String, CourseItem>();
		 saveMyStellar(ctx);
	 }
	 
	 private static String getTerm(Context context) {
		 SharedPreferences pref = context.getSharedPreferences(Global.PREFS_STELLAR,Context.MODE_PRIVATE);  
		 return pref.getString(PREF_KEY_TERM, null);
	 }
	 
	 private static void updateTerm(Context context, String term) {
		 SharedPreferences pref = context.getSharedPreferences(Global.PREFS_STELLAR, Context.MODE_PRIVATE); 
		 Editor editor = pref.edit();
		 editor.putString(PREF_KEY_TERM, term);
		 editor.commit();
	 }
	 
	 private static void saveCourseAlert(Context context, CourseItem ci) {
		if (!ci.getTermId().equals(getTerm(context))) {
			myCourses = new HashMap<String, CourseItem>();
		}
		
	    CourseItem alertId = myCourses.get(ci.masterId);
		// Adjust alert
		if (alertId==null) {
			myCourses.put(ci.masterId, ci);
		} else {
			myCourses.remove(ci.masterId);
		}
		saveMyStellar(context);
	}
	 
	/************************************************************************/
	/************************************************************************/
	
	private static HashMap<String, List<CourseItem>> searchCache = new HashMap<String, List<CourseItem>>();
	private static HashMap<String, CourseItem> recentlyViewedCache = new HashMap<String, CourseItem>();
	
	public static void executeSearch(final String searchTerm, final Context context, final Handler uiHandler) {
		
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
					
					@SuppressWarnings("unchecked")
					List<CourseItem> courses = parser.items;

					searchCache.put(searchTerm, courses);
					MobileWebApi.sendSuccessMessage(uiHandler, new SearchResults<CourseItem>(searchTerm, courses));				
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
