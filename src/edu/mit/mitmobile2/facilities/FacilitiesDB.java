package edu.mit.mitmobile2.facilities;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.objs.FacilitiesItem.CategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationCategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.RoomRecord;
import org.json.JSONObject;

public class FacilitiesDB {

	private static final String TAG = "FacilitiesDB";	

	private static final String DATABASE_NAME = "facilities.db";

	// Data  Version Info
	private static Integer BLDG_DATA_VERSION = 1;
	private static Integer CATEGORY_LIST_VERSION = 1;
	private static Integer LOCATION_VERSION = 1;
	private static Integer ROOM_VERSION = 1;

	public final static Integer STATUS_CATEGORIES_SUCCESSFUL = 900;
	public final static Integer STATUS_LOCATIONS_SUCCESSFUL = 901;
	public final static Integer STATUS_ROOMS_SUCCESSFUL = 902;
	
	private static final String CATEGORY_TABLE = "categories";
	private static final String LOCATION_TABLE = "locations";
	private static final String LOCATION_CATEGORY_TABLE = "location_categories";
	private static final String LOCATION_CONTENT_TABLE = "location_contents";
	private static final String ROOMS_TABLE = "rooms";
	
	private static final String LOCATION_SUGGESTION_TABLE = "location_suggestion";
	
	private static final String SHORT_LIST_LIMIT = "3";
	
	// BEGIN TABLE DEFINITIONS

	// CATEGORIES - distinct list of categories for locations. A location can fall into one or more categories
	static final class CategoryTable implements BaseColumns {
		static final String KEY_ID = BaseColumns._ID;
		static final String ID = "id";
		static final String NAME = "name";
	}

	// LOCATIONS
	static final class LocationTable implements BaseColumns {
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
		static final String OBJECT_ID = "object_id";
		static final String CONTENT = "content";
	}

	// ROOMS
	static final class RoomTable implements BaseColumns {
		static final String BUILDING = "building";
		static final String FLOOR = "floor";
		static final String ROOM = "room";
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

	// ADDLOCATION
	synchronized public void addLocation(LocationRecord locationRecord) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LocationTable.ID, locationRecord.id);
		values.put(LocationTable.NAME, locationRecord.name);
		values.put(LocationTable.LAT, locationRecord.lat_wgs84);
		values.put(LocationTable.LONG,locationRecord.long_wgs84);
		values.put(LocationTable.BLDGNUM, locationRecord.bldgnum);
		//Log.d(TAG,"adding location " + locationRecord.name );
		try {
			db.insert(LOCATION_TABLE, LocationTable.ID + "," + LocationTable.NAME + "," + LocationTable.LAT + "," + LocationTable.LONG + "," + LocationTable.BLDGNUM,values);		
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
			//Log.d(TAG,"index 0 = " + cursor.getString(0) + " index 1 = " + cursor.getString(1) + " index 2 = " + cursor.getString(2));
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
//		String sql = "select * from " + LOCATION_CATEGORY_TABLE;
		Log.d(TAG,"locationCategory sql = " + sql);
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
//			Log.d(TAG,"string 0 = " + cursor.getString(0));
//			Log.d(TAG,"string 1 = " + cursor.getString(1));
//			Log.d(TAG,"string 2 = " + cursor.getString(2));
//			Log.d(TAG,"string 3 = " + cursor.getString(3));
//			Log.d(TAG,"string 4 = " + cursor.getString(4));
//			Log.d(TAG,"string 5 = " + cursor.getString(5));
//			Log.d(TAG,"string 6 = " + cursor.getString(6));
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

	public Cursor getRoomCursor() {
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
		return cursor;
	}

	public RoomRecord getRoom(int position) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		RoomRecord room = null;
		Cursor cursor = getLocationCursor();
		cursor.move(position + 1);
		if (cursor.getCount() > 0) {
			room = new RoomRecord();
			Log.d(TAG,"string 0 = " + cursor.getString(0));
			Log.d(TAG,"string 1 = " + cursor.getString(1));
			Log.d(TAG,"string 2 = " + cursor.getString(2));
			Log.d(TAG,"string 3 = " + cursor.getString(3));
			room.building = cursor.getString(1);
			room.floor = cursor.getString(2);
			room.room = cursor.getString(3);
		}
		cursor.close();
		return room;
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
			//Log.d(TAG,"onCreate()");
			createCategoryTable(db);
			createLocationTable(db);
			createLocationCategoryTable(db);
			createRoomTable(db);
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

		Log.d(TAG,"create category table sql = " + categoryTableSql);
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

		Log.d(TAG,"create category table sql = " + locationTableSql);
		try {
			db.execSQL(locationTableSql);
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

		Log.d(TAG,"create location category table sql = " + locationCategoryTableSql);
		try {
			db.execSQL(locationCategoryTableSql);
			Log.d(TAG,"location category table created");
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

		Log.d(TAG,"create category table sql = " + roomTableSql);
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
			// inserting all the rooms at once takes too long and causes android to crash, add them on a per building basis when a building is selected
			//updateRooms(mContext,uiHandler);
		}
		catch (Exception e) {
			Log.d(TAG,"error updating the database: " + e.getMessage());
		}

	}

	public static void updateCategories(Context mContext,final Handler uiHandler) {
		final FacilitiesDB db = FacilitiesDB.getInstance(mContext);
		Message msg = new Message();
		
		// compare local category version to remote version
		final int remoteVersion = Global.getVersion("map", "category_list");
		if (remoteVersion > db.CATEGORY_LIST_VERSION) {
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
						for (int i = 0; i < array.length(); i++) {
							try {
								//Log.d(TAG,array.getString(i));
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
						//MobileWebApi.sendSuccessMessage(uiHandler);
					}
			});			
			// update local version
			FacilitiesDB.setCATEGORY_LIST_VERSION(remoteVersion);
			msg.arg1 = FacilitiesDB.STATUS_CATEGORIES_SUCCESSFUL;
		}
		else {
			Log.d(TAG,"category list is up to date");
			msg.arg1 = FacilitiesDB.STATUS_CATEGORIES_SUCCESSFUL;
		}
		uiHandler.sendMessage(msg);
	}
	
	public static void updateLocations(Context mContext,final Handler uiHandler) {
			//String url = "http://" + Global.getMobileWebDomain() + "/api/map/index.php?command=categorylist";
			final FacilitiesDB db = FacilitiesDB.getInstance(mContext);
			Message msg = new Message();
				
			// compare local category version to remote version
			final int remoteVersion = Global.getVersion("map", "location");
			Log.d(TAG,"remoteVersion = " + remoteVersion + " localVersion = " + db.LOCATION_VERSION);
			if (remoteVersion > db.LOCATION_VERSION) {
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
							//Log.d(TAG, "processing locations from server");
							for (int i = 0; i < array.length(); i++) {
								try {
									//Log.d(TAG,array.getString(i));
									JSONObject obj = array.getJSONObject(i);
									LocationRecord record = new LocationRecord();
									record.id = obj.getString("id");
									record.name = obj.getString("name");
									record.lat_wgs84 = obj.getString("lat_wgs84");
									record.long_wgs84 = obj.getString("long_wgs84");
									record.bldgnum = obj.getString("bldgnum");
									Log.d("ZZZ","adding bldgnum " + record.bldgnum + " for " + record.name );
									db.addLocation(record);
									//Log.d(TAG,"after adding location" + record.name );
									
									// convert categories into an array and add to location category table 
									//Log.d(TAG,"category string for " + record.id + " = " + obj.getString("category"));
									JSONArray categories = new JSONArray(obj.getString("category"));
									for (int c = 0; c < categories.length(); c++) {
										LocationCategoryRecord locationCategoryRecord = new LocationCategoryRecord();
										locationCategoryRecord.locationId = obj.getString("id");
										locationCategoryRecord.categoryId = categories.getString(c);
										db.addLocationCategory(locationCategoryRecord);
									}

								}
								catch (Exception e) {
									Log.d(TAG,e.getMessage());							
								}
							}
							Log.d(TAG,"locations inserted into database");
							FacilitiesDB.setLOCATION_VERSION(remoteVersion);
							Message msg = new Message();
							msg.arg1 = FacilitiesDB.STATUS_LOCATIONS_SUCCESSFUL;
							Log.d(TAG, "sending location success message to uiHandler");
							uiHandler.sendMessage(msg);
						}
				});
			}
			else {
				Log.d(TAG,"location list is up to date");
				msg.arg1 = FacilitiesDB.STATUS_LOCATIONS_SUCCESSFUL;
				Log.d(TAG, "sending location success message to uiHandler");
				uiHandler.sendMessage(msg);
			}
			//MobileWebApi.sendSuccessMessage(uiHandler);
		}

	
	public static void updateRooms(Context mContext,final Handler uiHandler, final String buildingNumber) {
		final FacilitiesDB db = FacilitiesDB.getInstance(mContext);
		Message msg = new Message();
	
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
	
	
	public static Integer getBLDG_DATA_VERSION() {
		return BLDG_DATA_VERSION;
	}

	public static void setBLDG_DATA_VERSION(Integer bLDG_DATA_VERSION) {
		BLDG_DATA_VERSION = bLDG_DATA_VERSION;
	}

	public static Integer getCATEGORY_LIST_VERSION() {
		return CATEGORY_LIST_VERSION;
	}

	public static void setCATEGORY_LIST_VERSION(Integer cATEGORY_LIST_VERSION) {
		CATEGORY_LIST_VERSION = cATEGORY_LIST_VERSION;
	}

	public static Integer getLOCATION_VERSION() {
		return LOCATION_VERSION;
	}

	public static void setLOCATION_VERSION(Integer lOCATION_VERSION) {
		LOCATION_VERSION = lOCATION_VERSION;
	}

	public static Integer getROOM_VERSION() {
		return ROOM_VERSION;
	}

	public static void setROOM_VERSION(Integer rOOM_VERSION) {
		ROOM_VERSION = rOOM_VERSION;
	}

}
