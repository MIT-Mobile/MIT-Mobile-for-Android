package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import edu.mit.mitmobile2.objs.BuildingMapItem;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapPoint;
import edu.mit.mitmobile2.objs.PersonItem;
import edu.mit.mitmobile2.people.PeopleDB;

public class MapsDB {
	private static final String TAG = "MapsDB";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "maps.db";
	private static final String MAPS_TABLE = "map_items";
	private static final String MAPS_VIEW = "map_items_view";

	
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
	
	private static MapsDB mapsDBInstance = null;

	/********************************************************************/
	public static MapsDB getInstance(Context context) {
		if(mapsDBInstance == null) {
			mapsDBInstance = new MapsDB(context);
			return mapsDBInstance;
		} else {
			return mapsDBInstance;
		}
	}
	
	public void close() {
		mMapsDBHelper.close();
	}
	
	private MapsDB(Context context) {
		mMapsDBHelper = new MapsDatabaseHelper(context); 
	}
	private String[] whereMapIdArgs(MapItem MapItem) {
		//return new String[] {MapItem.id};
		return new String[]{""};
	}
	
	private String[] whereArgs(MapItem MapItem) {
		String id = (String)MapItem.getItemData().get("id");
		Log.d(TAG,"delete id = " + id);
		String[] args = new String[1];
		args[0] = id;
		return args;
		//return new String[] {(String)MapItem.getItemData().get("id")};
	}
	/********************************************************************/
	synchronized void clearAll() {
		SQLiteDatabase db = mMapsDBHelper.getWritableDatabase();
		db.delete(MAPS_TABLE, null, null);
	}
	
	synchronized void delete(MapItem mi) {
		SQLiteDatabase db = mMapsDBHelper.getWritableDatabase();
		int result = db.delete(MAPS_TABLE, MAP_ID_WHERE, whereArgs(mi));
		Log.d(TAG,"delete result = " + result);

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
		mapValues.put(MAP_ID, (String)mi.getItemData().get("id"));
		mapValues.put(NAME, (String)mi.getItemData().get("name"));
		mapValues.put(DISPLAY_NAME, (String)mi.getItemData().get("displayName"));
		mapValues.put(SNIPPETS, (String)mi.getItemData().get("snippets"));
		mapValues.put(STREET, (String)mi.getItemData().get("street"));
		mapValues.put(FLOORPLANS, (String)mi.getItemData().get("floorplans"));
		mapValues.put(BLDGIMG, (String)mi.getItemData().get("bldgimg"));
		mapValues.put(VIEWANGLE, (String)mi.getItemData().get("viewangle"));
		mapValues.put(BLDGNUM, (String)mi.getItemData().get("bldgnum"));
		mapValues.put(LONG_WGS84, mi.getMapPoints().get(0).long_wgs84);
		mapValues.put(LAT_WGS84, mi.getMapPoints().get(0).lat_wgs84);
		
		long row_id;
		int rows;
		String id = (String)mi.getItemData().get("id");
		Log.d(TAG,"checking map id " + id);
		if(miExists(id)) {
			rows = db.update(MAPS_TABLE, mapValues, MAP_ID_WHERE, whereMapIdArgs(mi));
			Log.d(TAG,"MapDB: updating "+rows);
		} else {
			row_id = db.insert(MAPS_TABLE, SNIPPETS, mapValues);
			mi.sql_id = row_id;
			rows = db.update(MAPS_TABLE, mapValues, MAP_ID_WHERE, whereMapIdArgs(mi));
			Log.d(TAG,"MapDB: adding "+row_id);
		}
		db.close();
		mMapsDBHelper.close();
		
	}
	/********************************************************************/
	public Cursor getMapsCursor(String name) {
		return getMapsCursor(name, null);
	}
	
	public Cursor getMapsCursor(String name, String limit) {
		SQLiteDatabase db = mMapsDBHelper.getReadableDatabase();
		String[] fields = new String[] {ID, NAME, SNIPPETS, STREET, FLOORPLANS, BLDGIMG, VIEWANGLE, BLDGNUM, LONG_WGS84, LAT_WGS84};
		
		Cursor cursor = db.query(MAPS_VIEW, fields, NAME + "=" + name, null, null, null, ID + " DESC", limit);
		return cursor;
	}

	public Cursor getMapsCursor() {
		SQLiteDatabase db = mMapsDBHelper.getReadableDatabase();
		String[] fields = new String[] {
										ID, 
										MAP_ID, 
										NAME, 
										DISPLAY_NAME, 
										SNIPPETS, 
										STREET, 
										FLOORPLANS, 
										BLDGIMG, 
										VIEWANGLE, 
										BLDGNUM, 
										LONG_WGS84, 
										LAT_WGS84
									    };
		
		Cursor cursor = db.query(MAPS_TABLE, fields, null, null, null, null, NAME + " DESC", null);
		Log.d(TAG,"num map items in cursor = " + cursor.getCount());
		return cursor;
	}
	
	
	/********************************************************************/
	static MapItem retrieveMapItem(Cursor cursor) {
		BuildingMapItem item = new BuildingMapItem();
		
		item.sql_id = cursor.getLong(cursor.getColumnIndex(ID));
		item.getItemData().put("id", cursor.getString(cursor.getColumnIndex(MAP_ID)));
		item.getItemData().put("name",cursor.getString(cursor.getColumnIndex(NAME)));
		item.getItemData().put("displayName",cursor.getString(cursor.getColumnIndex(DISPLAY_NAME)));
		item.getItemData().put("snippets",cursor.getString(cursor.getColumnIndex(SNIPPETS)));
		item.getItemData().put("street",cursor.getString(cursor.getColumnIndex(STREET)));
		item.getItemData().put("floorplans",cursor.getString(cursor.getColumnIndex(FLOORPLANS)));
		item.getItemData().put("bldgnum",cursor.getString(cursor.getColumnIndex(BLDGNUM)));
		item.getItemData().put("bldgimg", cursor.getString(cursor.getColumnIndex(BLDGIMG)));
		item.getItemData().put("viewangle",cursor.getString(cursor.getColumnIndex(VIEWANGLE)));
		item.setMapPoints(new ArrayList<MapPoint>());
		MapPoint mapPoint = new MapPoint();
		mapPoint.lat_wgs84 = cursor.getDouble(cursor.getColumnIndex(LAT_WGS84));
		mapPoint.long_wgs84 = cursor.getDouble(cursor.getColumnIndex(LONG_WGS84));
		item.getMapPoints().add(mapPoint);
		
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
			Log.d(TAG,"creating maps table");
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

	synchronized void clearAllBookmarks() {
		this.clearAll();
	}

	public ArrayList<MapItem> getMapItems() {
		Cursor cursor = this.getMapsCursor();
		ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
		if(cursor != null && cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				MapItem mapItem = MapsDB.retrieveMapItem(cursor);
				mapItems.add(mapItem);
				cursor.moveToNext();
			}
		}
		return mapItems;
	}
}
