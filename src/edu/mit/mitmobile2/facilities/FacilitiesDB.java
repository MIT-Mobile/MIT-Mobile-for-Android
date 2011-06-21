package edu.mit.mitmobile2.facilities;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.util.Log;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.objs.FacilitiesItem.CategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationCategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationContentAltnameRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationContentCategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationContentRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.ProblemTypeRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.RoomRecord;
public class FacilitiesDB {

	private static final String TAG = "FacilitiesDB";	

	private static final String DATABASE_NAME = "facilities.db";

	public final static Integer STATUS_CATEGORIES_SUCCESSFUL = 900;
	public final static Integer STATUS_LOCATIONS_SUCCESSFUL = 901;
	public final static Integer STATUS_ROOMS_SUCCESSFUL = 902;
	public final static Integer STATUS_PROBLEM_TYPES_SUCCESSFUL = 903;
	
	private static final String CATEGORY_TABLE = "categories";
	private static final String LOCATION_TABLE = "locations";
	private static final String LOCATION_CATEGORY_TABLE = "location_categories";
	private static final String LOCATION_CONTENT_TABLE = "location_contents";
	private static final String LOCATION_CONTENT_CATEGORY_TABLE = "location_content_categories"; // stores one to many relationship for a location content record and its categories 
	private static final String LOCATION_CONTENT_ALTNAME_TABLE = "location_content_altnames"; // stores one to many relationship for a location content record and its altnames 

	private static final String ROOMS_TABLE = "rooms";
	private static final String PROBLEM_TYPE_TABLE = "problem_types";
	private static final String SHORT_LIST_LIMIT = "3";
	
	// BEGIN TABLE DEFINITIONS

	// CATEGORIES - distinct list of categories for locations. A location can fall into one or more categories
	static final class CategoryTable implements BaseColumns {
		static final String KEY_ID = BaseColumns._ID;
		static final String ID = "id";
		static final String NAME = "name";
	}

	// LOCATIONS
	public static final class LocationTable implements BaseColumns {
		static final String ID = "id";
		static final String NAME = "name";
		static final String LAT = "lat_wgs84";
		static final String LONG = "long_wgs84";
		static final String BLDGNUM = "bldgnum";
		static final String LAST_UPDATED = "last_updated";
	}

	// LOCATION CATEGORIES - stores the one to many relationships between location and category
	static final class LocationCategoryTable implements BaseColumns {
		static final String LOCATION_ID = "location_id";
		static final String CATEGORY_ID = "category_id";
	}

	// LOCATION CONTENTS - stores the one to many relationships between location and contents
	static final class LocationContentTable implements BaseColumns {
		static final String LOCATION_ID = "location_id";
		static final String NAME = "name";
		static final String URL = "url";
	}

	// LOCATION CONTENT CATEGORIES - stores one to many relationship for a location content record and its categories 
	static final class LocationContentCategoryTable implements BaseColumns {
		static final String LOCATION_ID = "location_id";
		static final String NAME = "name";
		static final String CATEGORY = "category";
	}

	// LOCATION CONTENT ALTNAMES - stores one to many relationship for a location content record and its altnames
	static final class LocationContentAltnameTable implements BaseColumns {
		static final String LOCATION_ID = "location_id";
		static final String NAME = "name";
		static final String ALTNAME = "altname";
	}
	
	// ROOMS
	static final class RoomTable implements BaseColumns {
		static final String BUILDING = "building";
		static final String FLOOR = "floor";
		static final String ROOM = "room";
	}

	// PROBLEM TYPES
	static final class ProblemTypeTable implements BaseColumns {
		static final String PROBLEM_TYPE = "problem_type";
	}

	// END TABLE DEFINITIONS
	
	static SQLiteOpenHelper mDBHelper;
	
	private static FacilitiesDB sInstance = null;
	
	public static FacilitiesDB getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new FacilitiesDB(context);
		}
		return sInstance;
	}
	
	private FacilitiesDB(Context context) {
		mDBHelper = new FacilitiesDBOpenHelper(context);
	}
	
	// BEGIN INSERT/UPDATE/DELETE METHODS

	// ADDCATEGORY
	synchronized public void addCategory(CategoryRecord categoryRecord) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(CategoryTable.ID, categoryRecord.id);
		values.put(CategoryTable.NAME, categoryRecord.name);
		db.insert(CATEGORY_TABLE, CategoryTable.ID + "," + CategoryTable.NAME,values);
	}

	// ADDPROBLEMTYPE
	synchronized public void addProblemType(ProblemTypeRecord problemTypeRecord) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ProblemTypeTable.PROBLEM_TYPE, problemTypeRecord.problem_type);
		db.insert(PROBLEM_TYPE_TABLE, ProblemTypeTable.PROBLEM_TYPE,values);
	}

	// ADDLOCATION
	synchronized public void addLocation(LocationRecord locationRecord) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LocationTable.ID, locationRecord.id);
		values.put(LocationTable.NAME, locationRecord.name);
		values.put(LocationTable.LAT, locationRecord.lat_wgs84);
		values.put(LocationTable.LONG,locationRecord.long_wgs84);
		values.put(LocationTable.BLDGNUM, locationRecord.bldgnum);
		try {
			db.insert(LOCATION_TABLE, LocationTable.ID + "," + LocationTable.NAME + "," + LocationTable.LAT + "," + LocationTable.LONG + "," + LocationTable.BLDGNUM,values);		
			//Log.d(TAG,"addLocation " + locationRecord.name);
		}
		catch (Exception e) {
			Log.d(TAG,"error inserting record " + e.getMessage());
		}
	}

	// ADDLOCATIONCATEGORY
	synchronized public void addLocationCategory(LocationCategoryRecord locationCategoryRecord) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LocationCategoryTable.LOCATION_ID, locationCategoryRecord.locationId);
		values.put(LocationCategoryTable.CATEGORY_ID, locationCategoryRecord.categoryId);
		try {
			db.insert(LOCATION_CATEGORY_TABLE, LocationCategoryTable.LOCATION_ID + "," + LocationCategoryTable.CATEGORY_ID,values);		
		}
		catch (Exception e) {
			Log.d(TAG,"error inserting location category: " + e.getMessage());
		}
	}

	// ADDLOCATIONCONTENT
	synchronized public void addLocationContent(LocationContentRecord locationContentRecord) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LocationContentTable.LOCATION_ID, locationContentRecord.location_id);
		values.put(LocationContentTable.NAME, locationContentRecord.name);
		values.put(LocationContentTable.URL, locationContentRecord.url);
		try {
			//Log.d(TAG,"inserting " + LOCATION_CONTENT_TABLE + " location_id = " + locationContentRecord.location_id + " name = " + locationContentRecord.name );
			db.insert(LOCATION_CONTENT_TABLE, LocationContentTable.LOCATION_ID + "," + LocationContentTable.NAME + "," + LocationContentTable.URL + ",",values);		
		}
		catch (Exception e) {
			Log.d(TAG,"error inserting location content: " + e.getMessage());
		}
	}

	// ADDLOCATIONCONTENTCATEGORY
	synchronized public void addLocationContentCategory(LocationContentCategoryRecord locationContentCategoryRecord) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LocationContentCategoryTable.LOCATION_ID, locationContentCategoryRecord.location_id);
		values.put(LocationContentCategoryTable.NAME, locationContentCategoryRecord.name);
		values.put(LocationContentCategoryTable.CATEGORY, locationContentCategoryRecord.category);
		try {
			db.insert(LOCATION_CONTENT_CATEGORY_TABLE, 
					  LocationContentCategoryTable.LOCATION_ID + "," 
					  + LocationContentCategoryTable.NAME + "," 
					  + LocationContentCategoryTable.CATEGORY,values);		
		}
		catch (Exception e) {
			Log.d(TAG,"error inserting location content category: " + e.getMessage());
		}
	}

	// ADDLOCATIONCONTENTALTNAME
	synchronized public void addLocationContentAltname(LocationContentAltnameRecord locationContentAltnameRecord) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LocationContentAltnameTable.LOCATION_ID, locationContentAltnameRecord.location_id);
		values.put(LocationContentAltnameTable.NAME, locationContentAltnameRecord.name);
		values.put(LocationContentAltnameTable.ALTNAME, locationContentAltnameRecord.altname);
		try {
			db.insert(LOCATION_CONTENT_ALTNAME_TABLE, 
					  LocationContentAltnameTable.LOCATION_ID + "," 
					  + LocationContentAltnameTable.NAME + "," 
					  + LocationContentAltnameTable.ALTNAME,values);		
		}
		catch (Exception e) {
			Log.d(TAG,"error inserting location content altname: " + e.getMessage());
		}
	}

	// ADDROOM
	synchronized public void addRoom(RoomRecord roomRecord) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RoomTable.BUILDING, roomRecord.building);
		values.put(RoomTable.FLOOR, roomRecord.floor);
		values.put(RoomTable.ROOM, roomRecord.room);
		Log.d(TAG,"adding room " + roomRecord.room );
		db.insert(ROOMS_TABLE, RoomTable.BUILDING + "," + RoomTable.FLOOR + "," + RoomTable.ROOM,values);		
	}

	// END INSERT/UPDATE/DELETE METHODS

	
	// BEGIN FETCH METHODS
	public Cursor getCategoryCursor() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.query(CATEGORY_TABLE, new String[] {
				CategoryTable._ID,
				CategoryTable.ID,
				CategoryTable.NAME
				}, null, null, null, null, null);
		return cursor;
	}

	public CategoryRecord getCategory(int position) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		CategoryRecord category = null;
		Cursor cursor = getCategoryCursor();
		cursor.move(position + 1);
		if (cursor.getCount() > 0) {
			category = new CategoryRecord();
			category.id = cursor.getString(1);
			category.name = cursor.getString(2);
		}
		cursor.close();
		return category;
	}

	public Cursor getLocationCategoryCursor() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String sql = "select " 				
					+ LOCATION_TABLE + "." + LocationTable._ID + ", " 
					+ LocationTable.ID + ", "
					+ LocationTable.NAME + ", "
					+ LocationTable.LAT + ", " 
					+ LocationTable.LONG + ", " 
					+ LocationTable.BLDGNUM + ", " 
					+ LocationTable.LAST_UPDATED + " " 
					+ "FROM " + LOCATION_CATEGORY_TABLE
					+ " JOIN " + LOCATION_TABLE + " on " + LOCATION_CATEGORY_TABLE + "." + LocationCategoryTable.LOCATION_ID + " = " + LOCATION_TABLE + "." + LocationTable.ID
					+ " where category_id = '" + Global.sharedData.getFacilitiesData().getLocationCategory() + "'"
					+ " order by " + LocationTable.NAME;
		//Log.d(TAG,"locationCategory sql = " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	public LocationCategoryRecord getLocationCategory(int position) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		LocationCategoryRecord locationCategory = null;
		Cursor cursor = getLocationCategoryCursor();
		cursor.move(position + 1);
		if (cursor.getCount() > 0) {
			locationCategory = new LocationCategoryRecord();
			locationCategory.categoryId = cursor.getString(1);
			locationCategory.locationId = cursor.getString(2);
		}
		cursor.close();
		return locationCategory;
	}

	public Cursor getLocationCursor() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.query(LOCATION_TABLE, new String[] {
				LocationTable._ID,
				LocationTable.ID,
				LocationTable.NAME,
				LocationTable.LAT,
				LocationTable.LONG,
				LocationTable.BLDGNUM,
				LocationTable.LAST_UPDATED				
				}, null, null, null, null, null);
		return cursor;
	}

	public LocationRecord getLocation(int position) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		LocationRecord location = null;
		Cursor cursor = getLocationCursor();
		cursor.move(position + 1);
		if (cursor.getCount() > 0) {
			//Log.d(TAG,"index 0 = " + cursor.getString(0) + " index 1 = " + cursor.getString(1) + " index 2 = " + cursor.getString(2));
			location = new LocationRecord();
			location.id = cursor.getString(1);
			location.name = cursor.getString(2);
			location.lat_wgs84 = cursor.getString(3);
			location.long_wgs84 = cursor.getString(4);
			location.bldgnum = cursor.getString(5);
			location.last_updated = cursor.getString(6);
		}
		cursor.close();
		return location;
	}

	public LocationRecord getLocationForCategory(int position) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		LocationRecord location = null;
		Cursor cursor = getLocationCategoryCursor();
		cursor.move(position + 1);
		if (cursor.getCount() > 0) {
			location = new LocationRecord();
			location.id = cursor.getString(1);
			location.name = cursor.getString(2);
			location.lat_wgs84 = cursor.getString(3);
			location.long_wgs84 = cursor.getString(4);
			location.bldgnum = cursor.getString(5);
			location.last_updated = cursor.getString(6);
		}
		cursor.close();
		return location;
	}
	
	public Cursor getLocationSearchCursor(CharSequence searchTerm) {
		Log.d(TAG,"searchTerm = " + searchTerm);
		String searchTermUppercase = searchTerm.toString().toUpperCase();
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String sql = "SELECT " 
	          		  + " -1 as " + LocationTable._ID + ", " 
	          		  + "'object-0'" + LocationTable.ID + ", " 
				      + " '' as " + LocationTable.BLDGNUM + ", "
				      + " '' as sort_value, "
				      + " '" + searchTerm + "' as name, " 
				      + " ? as display_name "
		              + " UNION " 
		              + " SELECT DISTINCT "
			          + LOCATION_TABLE + "." + LocationTable._ID + ", "
		              + LOCATION_TABLE + "." + LocationTable.ID + ", "
			          + LOCATION_TABLE + "." + LocationTable.BLDGNUM + ", "
			          + LOCATION_TABLE + "." + LocationTable.ID + " as sort_value, "
			          + LOCATION_TABLE + "." + LocationTable.NAME + ", "			          
			          + " CASE WHEN length(" + LOCATION_TABLE + "." + LocationTable.BLDGNUM + ") > 0 THEN " 
			          + LOCATION_TABLE + "." + LocationTable.BLDGNUM + " || '-' || " + LOCATION_TABLE + "." + LocationTable.NAME 
			          + " ELSE " + LOCATION_TABLE + "." + LocationTable.NAME + " END as display_name "
			          + " FROM " + LOCATION_TABLE
			          + " LEFT JOIN " + LOCATION_CATEGORY_TABLE + " on " + LOCATION_TABLE + "." + LocationTable.ID + " = " + LOCATION_CATEGORY_TABLE + "." + LocationCategoryTable.LOCATION_ID
			          + " LEFT JOIN " + LOCATION_CONTENT_TABLE + " on " + LOCATION_TABLE + "." + LocationTable.ID + " = " + LOCATION_CONTENT_TABLE + "." + LocationContentTable.LOCATION_ID
			          + " WHERE "
			          + " upper(" + LOCATION_TABLE + "." + LocationTable.BLDGNUM + ") like '%" + searchTermUppercase + "%'"
			          + " OR "
			          + " upper(" + LOCATION_CATEGORY_TABLE + "." + LocationCategoryTable.CATEGORY_ID + ") like '%" + searchTermUppercase + "%'"
					  + " OR "
					  + " upper(" + LOCATION_CONTENT_TABLE + "." + LocationContentTable.NAME + ") like '%" + searchTermUppercase + "%'"
					  + " ORDER BY sort_value ";

		Log.d(TAG,"location search sql = " + sql);
		Cursor cursor = db.rawQuery(sql, new String[] { "use '" + searchTerm + "'"});
		Log.d(TAG,"num results = " + cursor.getCount());
		return cursor;
	}
	
	public Cursor getLocationContentCursor() {
		Log.d(TAG,"getLocationContentCursor");
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String sql = "select " 				
					+ LOCATION_CONTENT_TABLE + "." + LocationContentTable._ID + ", " 
					+ LOCATION_CONTENT_TABLE + "." + LocationContentTable.LOCATION_ID + ", " 
					+ LOCATION_CONTENT_TABLE + "." + LocationContentTable.NAME + ", " 
					+ LOCATION_CONTENT_TABLE + "." + LocationContentTable.URL  
					+ " FROM " + LOCATION_CONTENT_TABLE
					+ " order by " + LocationContentTable.LOCATION_ID;
		Cursor cursor = db.rawQuery(sql, null);
		//DEBUG CONTENT
		cursor.moveToFirst();
		for (int c = 0; c < cursor.getCount(); c++) {
			Log.d(TAG,"CONTENT _ID = " + cursor.getString(0));
			Log.d(TAG,"CONTENT LOCATION_ID = " + cursor.getString(1));
			Log.d(TAG,"CONTENT NAME = " + cursor.getString(2));
			Log.d(TAG,"CONTENT URL = " + cursor.getString(3));
			cursor.moveToNext();
		}
		//DEBUG
		return cursor;
	}

	public Cursor getRoomCursor() {
		Log.d(TAG,"getRoomCursor");
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		String sql = "select " 				
					+ ROOMS_TABLE + "." + RoomTable._ID + ", " 
					+ ROOMS_TABLE + "." + RoomTable.BUILDING + ", " 
					+ ROOMS_TABLE + "." + RoomTable.FLOOR + ", " 
					+ ROOMS_TABLE + "." + RoomTable.ROOM  
					+ " FROM " + ROOMS_TABLE
					+ " where upper(building) = '" + Global.sharedData.getFacilitiesData().getBuildingNumber().toUpperCase() + "'"
					+ " order by " + RoomTable.ROOM;
//		String sql = "select * from " + LOCATION_CATEGORY_TABLE;
		Log.d(TAG,"room sql = " + sql);
		Cursor cursor = db.rawQuery(sql, null);
		Log.d(TAG,"number of rooms for building " + Global.sharedData.getFacilitiesData().getBuildingNumber() + " = " + cursor.getCount());
		//DEBUG ROOMS
		cursor.moveToFirst();
		for (int r = 0; r < cursor.getCount(); r++) {
			Log.d(TAG,"ROOM _ID = " + cursor.getString(0));
			Log.d(TAG,"ROOM BUILDING = " + cursor.getString(1));
			Log.d(TAG,"ROOM FLOOR = " + cursor.getString(2));
			Log.d(TAG,"ROOM ROOM = " + cursor.getString(3));
			cursor.moveToNext();
		}
		//DEBUG
		return cursor;
	}

	public RoomRecord getRoom(int position) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		RoomRecord room = null;
		Cursor cursor = getRoomCursor();
		cursor.move(position + 1);
		if (cursor.getCount() > 0) {
			room = new RoomRecord();
			if (cursor != null) {
				Log.d(TAG,"string 0 = " + cursor.getString(0));
				Log.d(TAG,"string 1 = " + cursor.getString(1));
				Log.d(TAG,"string 2 = " + cursor.getString(2));
				Log.d(TAG,"string 3 = " + cursor.getString(3));
				room.building = cursor.getString(1);
				room.floor = cursor.getString(2);
				room.room = cursor.getString(3);
			}
		}
		cursor.close();
		return room;
	}

	public Cursor getProblemTypeCursor() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.query(PROBLEM_TYPE_TABLE, new String[] {
				ProblemTypeTable._ID,
				ProblemTypeTable.PROBLEM_TYPE
		}, null, null, null, null, null);
		Log.d(TAG,"num problem types = " + cursor.getCount());
		return cursor;
	}

	
	// END FETCH METHODS
	
	synchronized void clearAll() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		try {
			db.delete(CATEGORY_TABLE, null, null);
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}
		
		try {
			db.delete(LOCATION_TABLE, null, null);
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}

		try {
			db.delete(LOCATION_CATEGORY_TABLE, null, null);
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}

		try {
			db.delete(ROOMS_TABLE, null, null);
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}

	}
	
	synchronized void clearCategories() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		try {
			db.delete(CATEGORY_TABLE, null, null);
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}		
	}

	synchronized void clearProblemTypes() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		try {
			db.delete(PROBLEM_TYPE_TABLE, null, null);
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}		
	}

	synchronized void clearLocations() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();

		try {
			db.delete(LOCATION_TABLE, null, null);
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}

		try {
			db.delete(LOCATION_CATEGORY_TABLE, null, null);
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}

		try {
			db.delete(LOCATION_CONTENT_TABLE, null, null);
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}

		try {
			db.delete(LOCATION_CONTENT_CATEGORY_TABLE, null, null);
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}

		try {
			db.delete(LOCATION_CONTENT_ALTNAME_TABLE, null, null);
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}

	}

	
	void startTransaction() {
		mDBHelper.getWritableDatabase().beginTransaction();
	}
	
	void endTransaction() {
		mDBHelper.getWritableDatabase().setTransactionSuccessful();
		mDBHelper.getWritableDatabase().endTransaction();
	}
		
	private static class FacilitiesDBOpenHelper extends SQLiteOpenHelper {
		public FacilitiesDBOpenHelper(Context context) {
			super(context, DATABASE_NAME, null,1);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			createCategoryTable(db);
			createLocationTable(db);
			createLocationCategoryTable(db);
			createLocationContentTable(db);
			createLocationContentCategoryTable(db);
			createLocationContentAltnameTable(db);
			createRoomTable(db);
			createProblemTypeTable(db);
			Log.d(TAG,"table creation complete");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO implement when upgrades available
		}
	}
	
	private static void createCategoryTable(SQLiteDatabase db) {

		String categoryTableSql = 
			"CREATE TABLE \n" + CATEGORY_TABLE + "\n ("
			+ CategoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, \n"
			+ CategoryTable.ID + " TEXT, \n "
			+ CategoryTable.NAME + " TEXT \n "
			+ ");";

		//Log.d(TAG,"create category table sql = " + categoryTableSql);
		try {
			db.execSQL(categoryTableSql);
		}
		catch (SQLException e) {
			Log.d(TAG,e.getMessage());
		}		
	}

	private static void createLocationTable(SQLiteDatabase db) {
		
		String locationTableSql = 
			"CREATE TABLE \n" + LOCATION_TABLE + "\n ("
			+ LocationTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, \n"
			+ LocationTable.ID + " TEXT, \n "
			+ LocationTable.NAME + " TEXT, \n "
			+ LocationTable.LAT + " TEXT, \n "
			+ LocationTable.LONG + " TEXT, \n "
			+ LocationTable.BLDGNUM + " TEXT, \n "
			+ LocationTable.LAST_UPDATED + " TEXT \n " 
			+ ");";

		//Log.d(TAG,"create category table sql = " + locationTableSql);
		try {
			db.execSQL(locationTableSql);
			Log.d(TAG,"location table created");
		}
		catch (SQLException e) {
			Log.d(TAG,e.getMessage());
		}				
	}
	
	private static void createLocationCategoryTable(SQLiteDatabase db) {

		String locationCategoryTableSql = 
			"CREATE TABLE \n" + LOCATION_CATEGORY_TABLE + "\n ("
			+ LocationCategoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, \n"
			+ LocationCategoryTable.LOCATION_ID + " TEXT, \n "
			+ LocationCategoryTable.CATEGORY_ID + " TEXT \n "
			+ ");";

		//Log.d(TAG,"create location category table sql = " + locationCategoryTableSql);
		try {
			db.execSQL(locationCategoryTableSql);
			Log.d(TAG,"location category table created");
		}
		catch (SQLException e) {
			Log.d(TAG,e.getMessage());
		}				
	}

	private static void createLocationContentTable(SQLiteDatabase db) {

		String locationContentTableSql = 
			"CREATE TABLE \n" + LOCATION_CONTENT_TABLE + "\n ("
			+ LocationContentTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, \n"
			+ LocationContentTable.LOCATION_ID + " TEXT, \n "
			+ LocationContentTable.NAME + " TEXT, \n "
			+ LocationContentTable.URL + " TEXT \n "
			+ ");";

		//Log.d(TAG,"create location category table sql = " + locationContentTableSql);
		try {
			db.execSQL(locationContentTableSql);
			Log.d(TAG,"location category table created");
		}
		catch (SQLException e) {
			Log.d(TAG,e.getMessage());
		}				
	}

	private static void createLocationContentCategoryTable(SQLiteDatabase db) {

		String locationContentCategoryTableSql = 
			"CREATE TABLE \n" + LOCATION_CONTENT_CATEGORY_TABLE + "\n ("
			+ LocationContentCategoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, \n"
			+ LocationContentCategoryTable.LOCATION_ID + " TEXT, \n "
			+ LocationContentCategoryTable.NAME + " TEXT, \n "
			+ LocationContentCategoryTable.CATEGORY + " TEXT \n "
			+ ");";

		//Log.d(TAG,"create location content category table sql = " + locationContentCategoryTableSql);
		try {
			db.execSQL(locationContentCategoryTableSql);
			Log.d(TAG,"location content category table created");
		}
		catch (SQLException e) {
			Log.d(TAG,e.getMessage());
		}				
	}

	private static void createLocationContentAltnameTable(SQLiteDatabase db) {

		String locationContentAltnameTableSql = 
			"CREATE TABLE \n" + LOCATION_CONTENT_ALTNAME_TABLE + "\n ("
			+ LocationContentAltnameTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, \n"
			+ LocationContentAltnameTable.LOCATION_ID + " TEXT, \n "
			+ LocationContentAltnameTable.NAME + " TEXT, \n "
			+ LocationContentAltnameTable.ALTNAME + " TEXT \n "
			+ ");";

		//Log.d(TAG,"create location content altname table sql = " + locationContentAltnameTableSql);
		try {
			db.execSQL(locationContentAltnameTableSql);
			Log.d(TAG,"location content altname table created");
		}
		catch (SQLException e) {
			Log.d(TAG,e.getMessage());
		}				
	}

	private static void createRoomTable(SQLiteDatabase db) {
		
		String roomTableSql = 
			"CREATE TABLE \n" + ROOMS_TABLE + "\n ("
			+ RoomTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, \n"
			+ RoomTable.BUILDING + " TEXT, \n "
			+ RoomTable.FLOOR + " TEXT, \n "
			+ RoomTable.ROOM + " TEXT \n "
			+ ");";

		//Log.d(TAG,"create category table sql = " + roomTableSql);
		try {
			db.execSQL(roomTableSql);
		}
		catch (SQLException e) {
			Log.d(TAG,e.getMessage());
		}				
	}

	private static void createProblemTypeTable(SQLiteDatabase db) {
		
		String roomTableSql = 
			"CREATE TABLE \n" + PROBLEM_TYPE_TABLE + "\n ("
			+ ProblemTypeTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, \n"
			+ ProblemTypeTable.PROBLEM_TYPE + " TEXT \n "
			+ ");";

		//Log.d(TAG,"create category table sql = " + roomTableSql);
		try {
			db.execSQL(roomTableSql);
		}
		catch (SQLException e) {
			Log.d(TAG,e.getMessage());
		}				
	}

	public static void updateFacilitiesDatabase(Context mContext,final Handler uiHandler) {
		// ultimately this method will check the version of the facilities DB from mobile web and update the android db if the server viewer is newer
		// for testing purposes, it currently uses data from the strings xml
		
		Log.d(TAG,"getting facilities db info from " + Global.getMobileWebDomain());		
		//Log.d(TAG,"num suggestions " + locationSuggestionValues.length);

		final FacilitiesDB db = FacilitiesDB.getInstance(mContext);
		
		try {
			updateCategories(mContext,uiHandler);
			updateLocations(mContext,uiHandler);
			updateProblemTypes(mContext,uiHandler);
		}
		catch (Exception e) {
			Log.d(TAG,"error updating the database: " + e.getMessage());
		}

	}

	public static String updateCategories(Context mContext,final Handler uiHandler) {
		final FacilitiesDB db = FacilitiesDB.getInstance(mContext);
		Message msg = new Message();
		
		final String version = Global.getVersion("remote", "map","category_list") + "";
		// compare local category version to remote version
		if (!Global.upToDate("map", "category_list")) {
			Log.d(TAG,"updating category list");
			db.clearCategories();
			MobileWebApi api = new MobileWebApi(false, true, "Facilities", mContext, uiHandler);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("module", "facilities");
			params.put("command", "categorylist");
			api.requestJSONArray(params, new MobileWebApi.JSONArrayResponseListener(
		                new MobileWebApi.DefaultErrorListener(uiHandler),
		                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
					@Override
					public void onResponse(JSONArray array) {
						db.startTransaction();
						Log.d(TAG,"category list begin transaction");
						for (int i = 0; i < array.length(); i++) {
							try {
								JSONObject obj = array.getJSONObject(i);
								CategoryRecord record = new CategoryRecord();
								record.id = obj.getString("id");
								record.name = obj.getString("name");
								db.addCategory(record);
							}
							catch (Exception e) {
								Log.d(TAG,e.getMessage());							
							}
						}
						db.endTransaction();
						Log.d(TAG,"category list end transaction");
					}
			});			
		}
		else {
			Log.d(TAG,"category list is up to date");
		}
		return version;
	}
	
	public static String updateLocations(Context mContext,final Handler uiHandler) {
			final FacilitiesDB db = FacilitiesDB.getInstance(mContext);
				
			final String version = Global.getVersion("remote", "map","location") + "";

			// compare local category version to remote version
			if (!Global.upToDate("map", "location")) {
				Log.d(TAG,"updating location list");
				db.clearLocations();
				MobileWebApi api = new MobileWebApi(false, true, "Facilities", mContext, uiHandler);
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("module", "facilities");
				params.put("command", "location");
				api.requestJSONArray(params, new MobileWebApi.JSONArrayResponseListener(
			                new MobileWebApi.DefaultErrorListener(uiHandler),
			                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
						@Override
						public void onResponse(JSONArray array) {
							Log.d(TAG,"received location response");
							db.startTransaction();
							for (int i = 0; i < array.length(); i++) {
								try {
									JSONObject obj = array.getJSONObject(i);
									LocationRecord record = new LocationRecord();
									record.id = obj.getString("id");
									record.name = obj.getString("name");
									record.lat_wgs84 = obj.getString("lat_wgs84");
									record.long_wgs84 = obj.getString("long_wgs84");
									record.bldgnum = obj.getString("bldgnum");
									db.addLocation(record);

									// convert categories into an array and add to location category table 
									String locationCategoryString = obj.getString("category");
									if (locationCategoryString != null) {
										JSONArray categories = new JSONArray(locationCategoryString);
										for (int c = 0; c < categories.length(); c++) {
											LocationCategoryRecord locationCategoryRecord = new LocationCategoryRecord();
											locationCategoryRecord.locationId = obj.getString("id");
											locationCategoryRecord.categoryId = categories.getString(c);
											db.addLocationCategory(locationCategoryRecord);
										}
									}
									
									// convert contents into an array and add to location contents table 
									if (!obj.getString("contents").equalsIgnoreCase("null")) {	
										JSONArray contentsArray = new JSONArray(obj.getString("contents"));
										for (int c = 0; c < contentsArray.length(); c++) {
											JSONObject contentObj = contentsArray.getJSONObject(c);
											LocationContentRecord locationContentRecord = new LocationContentRecord();
											locationContentRecord.location_id = obj.getString("id");
											
											// contents - name
											if (contentObj.getString("name") != null) {
												locationContentRecord.name = contentObj.getString("name");											
											}
											else {
												locationContentRecord.name = "";																						
											}
											db.addLocationContent(locationContentRecord);
											
											// contents - altname
											if (!contentObj.isNull("altname")) { 
												String contentAltnameString = contentObj.getString("altname");
												JSONArray altnameArray = new JSONArray(contentAltnameString);
												for (int a = 0; i < altnameArray.length(); a++) {
													LocationContentAltnameRecord locationContentAltnameRecord = new LocationContentAltnameRecord(); 
													locationContentAltnameRecord.location_id = obj.getString("id");
													locationContentAltnameRecord.name = contentObj.getString("name");
													locationContentAltnameRecord.altname = altnameArray.getString(a);
													db.addLocationContentAltname(locationContentAltnameRecord);
												}
											}

											// contents - category
											if (!contentObj.isNull("category")) { 
												String contentCategoryString = contentObj.getString("category");
												JSONArray categoryArray = new JSONArray(contentCategoryString);
												for (int cc = 0; i < categoryArray.length(); cc++) {
													LocationContentCategoryRecord locationContentCategoryRecord = new LocationContentCategoryRecord(); 
													locationContentCategoryRecord.location_id = obj.getString("id");
													locationContentCategoryRecord.name = contentObj.getString("name");
													locationContentCategoryRecord.category = categoryArray.getString(cc);
													db.addLocationContentCategory(locationContentCategoryRecord);
												}
											}
										}
									}
								}
								catch (Exception e) {
									Log.d(TAG,e.getMessage());							
								}
							}
							db.endTransaction();
							Log.d(TAG,"locations inserted into database");
							// update local version
							try {
								Global.setVersion("local", "map","location",Global.getVersion("remote","map","location") + "",Global.mContext);
							}
							catch (Exception e) {
								Log.d(TAG,e.getMessage());
							}
						}
				});
			}
			else {
				Log.d(TAG,"location list is up to date");
			}
			return version;
		}

	public static String updateProblemTypes(Context mContext,final Handler uiHandler) {
		final FacilitiesDB db = FacilitiesDB.getInstance(mContext);
		Log.d(TAG,"updating problem types");
		final String version = Global.getVersion("remote","facilities", "problem_type") + "";

		if (!Global.upToDate("facilities", "problem_type")) {
			Log.d(TAG,"updating problem type list");
			db.clearProblemTypes();
			MobileWebApi api = new MobileWebApi(false, true, "Facilities", mContext, uiHandler);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("module", "facilities");
			params.put("command", "problemtype");
			api.requestJSONArray(params, new MobileWebApi.JSONArrayResponseListener(
		                new MobileWebApi.DefaultErrorListener(uiHandler),
		                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
					@Override
					public void onResponse(JSONArray array) {
						db.startTransaction();
						for (int i = 0; i < array.length(); i++) {
							try {
								ProblemTypeRecord record = new ProblemTypeRecord();
								record.problem_type = array.getString(i);
								db.addProblemType(record);
								Log.d(TAG,"adding problem type " + record.problem_type);
							}
							catch (Exception e) {
								Log.d(TAG,e.getMessage());							
							}
						}
						db.endTransaction();
					}
			});			
		}
		else {
			Log.d(TAG,"problem type list is up to date");
		}
		return version;
	}

	public static void updateRooms(Context mContext,final Handler uiHandler, final String buildingNumber) {
		Message msg = new Message();
		final FacilitiesDB db = FacilitiesDB.getInstance(mContext);
	
		MobileWebApi api = new MobileWebApi(false, true, "Facilities", mContext, uiHandler);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("module", "facilities");
		params.put("command", "room");
		params.put("building", buildingNumber);
		api.requestJSONObject(params, new MobileWebApi.JSONObjectResponseListener(
	                new MobileWebApi.DefaultErrorListener(uiHandler),
	                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
				@Override
				public void onResponse(JSONObject obj) {
					db.startTransaction();
					// iterate through all building on json object
					Log.d(TAG,"got room response from server");
					Iterator b = obj.keys();
					Log.d(TAG,"response for room = " + obj.toString());
					while (b.hasNext()) {						
						try {
							String building = (String)b.next();
							Log.d(TAG,"adding rooms for building " + building);
							// iterate through each floor of the building
							JSONObject floors = (JSONObject)obj.get(building);
							Iterator f = floors.keys();
							while (f.hasNext()) {
								String floor = (String)f.next();
								// get the array of rooms for each floor
								Log.d(TAG,"adding rooms for building " + building + " floor " + floor);
								JSONArray rooms = (JSONArray)floors.getJSONArray(floor);
								for (int r = 0; r < rooms.length(); r++) {
									String room = rooms.getString(r);
									RoomRecord roomRecord = new RoomRecord();
									roomRecord.building = building;
									roomRecord.floor = floor;
									roomRecord.room = room;
									db.addRoom(roomRecord);
									Log.d(TAG,"adding room " + room + " for building " + building);
								}
							}
						}
						catch (Exception e) {
							Log.d(TAG,e.getMessage());							
						}
					}
					// Set last updated field for location so rooms are not re-read for that location
					FacilitiesDB.setLocationLastUpdated(buildingNumber);
					db.endTransaction();
					Message msg = new Message();
					msg.arg1 = FacilitiesDB.STATUS_ROOMS_SUCCESSFUL;
					Log.d(TAG, "sending room success message to uiHandler");
					uiHandler.sendMessage(msg);
				}
		});
	}

	// sets the last_updated field on the specified table and row to the given value 
	public static void setLocationLastUpdated(String locationId) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Date date = new Date();
		
		String sql = "update " + LOCATION_TABLE + " set " + LocationTable.LAST_UPDATED + " = " + date.getTime() + " where " + LocationTable.ID + " = '" + locationId + "'";
		Log.d(TAG,"setting last updated for " + locationId + " to " + date.getTime());
		db.rawQuery(sql,null);
	}
	
	// gets the last_updated value for a specified locatio id
	public static String getLocationLastUpdated(String locationId) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Date date = new Date();
		
		String sql = "select " + LocationTable.LAST_UPDATED + " from " + LOCATION_TABLE + " where " + LocationTable.ID + " = '" + locationId + "'";
		Log.d(TAG,"setting last updated for " + locationId + " to " + date.getTime());
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			return cursor.getString(0);
		}
		else {
			return null;
		}
	}
			
}
