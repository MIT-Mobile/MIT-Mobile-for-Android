package edu.mit.mitmobile2.alerts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.mit.mitmobile2.objs.ShuttleAlertItem;

public class AlertsDB {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "alerts.db";
	
	private static final String SHUTTLE_ALERTS_TABLE = "alert_items";
	private static final String CLASS_ALERTS_TABLE = "alert_items";

	
	// alert table field names
	private static final String ID = "_id";
	
	// Shuttles 
	private static final String ROUTE_ID = "route_id";
	private static final String STOP_ID = "stop_id";
	private static final String TITLE = "title";
	private static final String LAST_UPDATE = "last_update";

	private static final String ID_WHERE = ID + "=?";
	private static final String SHUTTLE_ID_WHERE = ROUTE_ID + "=?";
	
	
	
	SQLiteOpenHelper mAlertsDBHelper;
	
	private static AlertsDB newsDBInstance = null;

	/********************************************************************/
	public static AlertsDB getInstance(Context context) {
		if(newsDBInstance == null) {
			newsDBInstance = new AlertsDB(context);
			return newsDBInstance;
		} else {
			return newsDBInstance;
		}
	}
	
	public void close() {
		mAlertsDBHelper.close();
	}
	
	private AlertsDB(Context context) {
		mAlertsDBHelper = new ShuttleAlertsDatabaseHelper(context); 
	}
	
	private String[] whereArgs(ShuttleAlertItem ShuttleAlertItem) {
		return new String[] {Long.toString(ShuttleAlertItem.id)};
	}
	/********************************************************************/
	synchronized void clearAll() {
		SQLiteDatabase db = mAlertsDBHelper.getWritableDatabase();
		db.delete(SHUTTLE_ALERTS_TABLE, null, null);
	}
	
	synchronized void delete(ShuttleAlertItem mi) {
		SQLiteDatabase db = mAlertsDBHelper.getWritableDatabase();
		db.delete(SHUTTLE_ALERTS_TABLE, ID_WHERE, whereArgs(mi));
		db.close();
		mAlertsDBHelper.close();
	}
	/********************************************************************/
	void startTransaction() {
		mAlertsDBHelper.getWritableDatabase().beginTransaction();
	}
	
	void endTransaction() {
		mAlertsDBHelper.getWritableDatabase().setTransactionSuccessful();
		mAlertsDBHelper.getWritableDatabase().endTransaction();
	}
	/********************************************************************/
	synchronized void saveShuttleAlertItem(ShuttleAlertItem sai) {
		
		SQLiteDatabase db = mAlertsDBHelper.getWritableDatabase();
		
		ContentValues alertValues = new ContentValues();
		alertValues.put(ROUTE_ID, sai.route_id);
		alertValues.put(STOP_ID, sai.stop_id);
		alertValues.put(TITLE, sai.title);
		alertValues.put(LAST_UPDATE, sai.last_update);
		
		long row_id;
		int rows;
		if(shuttleAlertExists(sai.route_id,sai.stop_id)) {
			rows = db.update(SHUTTLE_ALERTS_TABLE, alertValues, ID_WHERE, whereArgs(sai));
			Log.d("ShuttleAlertDB","ShuttleAlertDB: updating "+rows);
		} else {
			row_id = db.insert(SHUTTLE_ALERTS_TABLE, TITLE, alertValues);
			Log.d("ShuttleAlertDB","ShuttleAlertDB: adding "+row_id);
		}
		db.close();
		mAlertsDBHelper.close();
		
	}

	/********************************************************************/
	public Cursor getShuttleAlertsCursor() {
		SQLiteDatabase db = mAlertsDBHelper.getReadableDatabase();
		String[] fields = new String[] {ID, ROUTE_ID, STOP_ID, TITLE, LAST_UPDATE};
		
		Cursor cursor = db.query(SHUTTLE_ALERTS_TABLE, fields, null, null, null, null, ROUTE_ID + " DESC", null);
		return cursor;
	}
	/********************************************************************/
	static ShuttleAlertItem retrieveShuttleAlertItem(Cursor cursor) {
		ShuttleAlertItem item = new ShuttleAlertItem();
		
		item.id = cursor.getLong(cursor.getColumnIndex(ID));
		item.route_id = cursor.getString(cursor.getColumnIndex(ROUTE_ID));
		item.stop_id = cursor.getString(cursor.getColumnIndex(STOP_ID));
		item.title = cursor.getString(cursor.getColumnIndex(TITLE));
		item.last_update = cursor.getString(cursor.getColumnIndex(LAST_UPDATE));
		
		return item;
	}
	
	/********************************************************************/
	public ShuttleAlertItem retrieveShuttleAlertItem(String name) {
		SQLiteDatabase db = mAlertsDBHelper.getReadableDatabase();
		
		Cursor cursor = db.query(
				SHUTTLE_ALERTS_TABLE, null, ID_WHERE, new String[]{name}, 
			null, null, null
		);
		
		if (cursor.getCount()<1) return null;
		
		cursor.moveToFirst();
		ShuttleAlertItem ShuttleAlertItem = retrieveShuttleAlertItem(cursor);
		cursor.close();
		mAlertsDBHelper.close();
		
		return ShuttleAlertItem;


	}
	/********************************************************************/
	private boolean shuttleAlertExists(String route, String stop) {
		
		SQLiteDatabase db = mAlertsDBHelper.getReadableDatabase();
		
		Cursor result = db.query(
			SHUTTLE_ALERTS_TABLE, 
			new String[] {ID}, 
			SHUTTLE_ID_WHERE,
			new String[] {route},  // TODO add stop
			null, null, null);
	
		boolean miExists = (result.getCount() > 0);
		result.close();
		return miExists;
		
	}
	/********************************************************************/
	private static class ShuttleAlertsDatabaseHelper extends SQLiteOpenHelper {
		
		ShuttleAlertsDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL("CREATE TABLE " + SHUTTLE_ALERTS_TABLE + " ("
					+ ID + " INTEGER PRIMARY KEY,"
					+ ROUTE_ID + " TEXT,"
					+ STOP_ID + " TEXT,"
					+ TITLE + " TEXT,"
					+ LAST_UPDATE + " TEXT"
				+ ");");
			
			db.execSQL("CREATE TABLE " + CLASS_ALERTS_TABLE + " ("
				+ ID + " INTEGER PRIMARY KEY,"
				+ ");");
			
				
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// no old versions exists
		}
	}
}
