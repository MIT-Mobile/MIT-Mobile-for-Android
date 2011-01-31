package edu.mit.mitmobile2;

import java.util.ArrayList;

import android.app.Application;

import edu.mit.mitmobile2.objs.CourseItem;
import edu.mit.mitmobile2.objs.EventDetailsItem;
import edu.mit.mitmobile2.objs.MapCatItem;
import edu.mit.mitmobile2.objs.NewsItem;

public class Global extends Application {

	public static final boolean DEBUG = false;

	// Shared preferences MUST use separate entries (docs say otherwise but seen failures to commit edits)
	public static final String PREFS = "prefs";
	public static final String PREFS_SHUTTLES = "prefs_shuttles";
	public static final String PREFS_STELLAR = "prefs_stellar";
	public static final String PREFS_MAP = "prefs_map";
	
	public static final String PREF_KEY_STOPS = "pref_stops";
	public static final String PREF_KEY_EMERGENCY_VERSION = "pref_version";
	
	public static EventDetailsItem curEvent;
	public static CourseItem curCourse;
	public static NewsItem curNews;
	
	// Maps related:
	public static ArrayList<MapCatItem> curSubCats;
	
}
