package edu.mit.mitmobile2.maps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.mit.mitmobile2.objs.MapItem;

public class MapsDB {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "maps.db";
	private static final String MAPS_TABLE = "map_items";
	//private static final String MAPS_VIEW = "map_items_view";

	
	// map table field names
	private static final String ID = "_id";
	
	private static final String MAP_ID = "id";
	private static final String NAME = "name";
	private static final String DISPLAY_NAME = "display_name";
	private static final String SNIPPETS = "snippets";
	private static final String STREET = "street";
	private static final String FLOORPLANS = "floorplans";
	private static final String BLDGIMG = "bldgimg";
	private static final String VIEWANGLE = "viewangle";
	private static final String BLDGNUM = "bldgnum";
	private static final String LONG_WGS84 = "long_wgs84";
	private static final String LAT_WGS84 = "lat_wgs84";

	private static final String ID_WHERE = ID + "=?";
	private static final String MAP_ID_WHERE = MAP_ID + "=?";
	
	
	
	SQLiteOpenHelper mMapsDBHelper;
	
	private static MapsDB newsDBInstance = null;

	/********************************************************************/
	public static MapsDB getInstance(Context context) {
		if(newsDBInstance == null) {
			newsDBInstance = new MapsDB(context);
			return newsDBInstance;
		} else {
			return newsDBInstance;
		}
	}
	
	public void close() {
		mMapsDBHelper.close();
	}
	
	private MapsDB(Context context) {
		mMapsDBHelper = new MapsDatabaseHelper(context); 
	}
	private String[] whereMapIdArgs(MapItem MapItem) {
		return new String[] {MapItem.id};
	}
	private String[] whereArgs(MapItem MapItem) {
		return new String[] {Long.toString(MapItem.sql_id)};
	}
	/********************************************************************/
	synchronized void clearAll() {
		SQLiteDatabase db = mMapsDBHelper.getWritableDatabase();
		db.delete(MAPS_TABLE, null, null);
	}
	
	synchronized void delete(MapItem mi) {
		SQLiteDatabase db = mMapsDBHelper.getWritableDatabase();
		db.delete(MAPS_TABLE, ID_WHERE, whereArgs(mi));
		db.close();
		mMapsDBHelper.close();
	}
	/********************************************************************/
	void startTransaction() {
		mMapsDBHelper.getWritableDatabase().beginTransaction();
	}
	
	void endTransaction() {
		mMapsDBHelper.getWritableDatabase().setTransactionSuccessful();
		mMapsDBHelper.getWritableDatabase().endTransaction();
	}
	/********************************************************************/
	synchronized void saveMapItem(MapItem mi) {
		
		SQLiteDatabase db = mMapsDBHelper.getWritableDatabase();
		
		ContentValues mapValues = new ContentValues();
		mapValues.put(MAP_ID, mi.id);
		mapValues.put(NAME, mi.name);
		mapValues.put(DISPLAY_NAME, mi.displayName);
		mapValues.put(SNIPPETS, mi.snippets);
		mapValues.put(STREET, mi.street);
		mapValues.put(FLOORPLANS, mi.floorplans);
		mapValues.put(BLDGIMG, mi.bldgimg);
		mapValues.put(VIEWANGLE, mi.viewangle);
		mapValues.put(BLDGNUM, mi.bldgnum);
		mapValues.put(LONG_WGS84, mi.mapPoints.get(0).long_wgs84);
		mapValues.put(LAT_WGS84, mi.mapPoints.get(0).lat_wgs84);
		
		long row_id;
		int rows;
		if(miExists(mi.id)) {
			rows = db.update(MAPS_TABLE, mapValues, MAP_ID_WHERE, whereMapIdArgs(mi));
			Log.d("MapDB","MapDB: updating "+rows);
		} else {
			row_id = db.insert(MAPS_TABLE, SNIPPETS, mapValues);
			//mi.sql_id = row_id;
			//rows = db.update(MAPS_TABLE, mapValues, MAP_ID_WHERE, whereMapIdArgs(mi));
			Log.d("MapDB","MapDB: adding "+row_id);
		}
		db.close();
		mMapsDBHelper.close();
		
	}
	/********************************************************************/
	/*
	public Cursor getMapsCursor(String name) {
		return getMapsCursor(name, null);
	}
	
	public Cursor getMapsCursor(String name, String limit) {
		SQLiteDatabase db = mMapsDBHelper.getReadableDatabase();
		String[] fields = new String[] {ID, NAME, SNIPPETS, STREET, FLOORPLANS, BLDGIMG, VIEWANGLE, BLDGNUM, LONG_WGS84, LAT_WGS84};
		
		Cursor cursor = db.query(MAPS_VIEW, fields, NAME + "=" + name, null, null, null, ID + " DESC", limit);
		return cursor;
	}
	*/
	public Cursor getMapsCursor() {
		SQLiteDatabase db = mMapsDBHelper.getReadableDatabase();
		String[] fields = new String[] {ID, MAP_ID, NAME, DISPLAY_NAME, SNIPPETS, STREET, FLOORPLANS, BLDGIMG, VIEWANGLE, BLDGNUM, LONG_WGS84, LAT_WGS84};
		
		Cursor cursor = db.query(MAPS_TABLE, fields, null, null, null, null, NAME + " DESC", null);
		return cursor;
	}
	/********************************************************************/
	static MapItem retrieveMapItem(Cursor cursor) {
		MapItem item = new MapItem();
		
		item.sql_id = cursor.getLong(cursor.getColumnIndex(ID));
		item.id = cursor.getString(cursor.getColumnIndex(MAP_ID));
		item.name = cursor.getString(cursor.getColumnIndex(NAME));
		item.displayName = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
		item.snippets = cursor.getString(cursor.getColumnIndex(SNIPPETS));
		item.street = cursor.getString(cursor.getColumnIndex(STREET));
		item.floorplans = cursor.getString(cursor.getColumnIndex(FLOORPLANS));
		item.bldgnum = cursor.getString(cursor.getColumnIndex(BLDGNUM));
		item.bldgimg = cursor.getString(cursor.getColumnIndex(BLDGIMG));
		item.viewangle = cursor.getString(cursor.getColumnIndex(VIEWANGLE));
		item.mapPoints.get(0).long_wgs84 = cursor.getDouble(cursor.getColumnIndex(LONG_WGS84));
		item.mapPoints.get(0).lat_wgs84 = cursor.getDouble(cursor.getColumnIndex(LAT_WGS84));
		
		return item;
	}
	/********************************************************************/
	public MapItem retrieveMapItem(String name) {
		SQLiteDatabase db = mMapsDBHelper.getReadableDatabase();
		
		Cursor cursor = db.query(
			MAPS_TABLE, null, MAP_ID_WHERE, new String[]{name}, 
			null, null, null
		);
		
		if (cursor.getCount()<1) return null;
		
		cursor.moveToFirst();
		MapItem MapItem = retrieveMapItem(cursor);
		cursor.close();
		mMapsDBHelper.close();
		
		return MapItem;
	}
	/********************************************************************/
	private boolean miExists(String map_id) {
		
		SQLiteDatabase db = mMapsDBHelper.getReadableDatabase();
		
		Cursor result = db.query(
			MAPS_TABLE, 
			new String[] {MAP_ID}, 
			MAP_ID_WHERE,
			new String[] {map_id},
			null, null, null);
	
		boolean miExists = (result.getCount() > 0);
		result.close();
		return miExists;
		
	}
	/********************************************************************/
	private static class MapsDatabaseHelper extends SQLiteOpenHelper {
		
		MapsDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + MAPS_TABLE + " ("
					+ ID + " INTEGER PRIMARY KEY,"
					+ MAP_ID + " TEXT,"
					+ NAME + " TEXT,"
					+ DISPLAY_NAME + " TEXT,"
					+ SNIPPETS + " TEXT,"
					+ STREET + " TEXT,"
					+ FLOORPLANS + " INTEGER,"
					+ BLDGIMG + " TEXT,"
					+ VIEWANGLE + " TEXT,"
					+ BLDGNUM + " TEXT,"
					+ LONG_WGS84 + " INTEGER,"
					+ LAT_WGS84 + " INTEGER"
				+ ");");
				
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// no old versions exists
		}
	}
}
