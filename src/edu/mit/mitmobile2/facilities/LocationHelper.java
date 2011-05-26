package edu.mit.mitmobile2.facilities;

import edu.mit.mitmobile2.R;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class LocationHelper extends SQLiteOpenHelper{
	private static final String TAG = "LocationHelper";
	private static final String DB_NAME = "facilities.db";
	private static final int DB_VERSION = 1;
	
	public static final String TABLE_LOCATION = "location_suggestion";
	public static final String KEY_ID = BaseColumns._ID;
	public static final String KEY_LOCATION = "location";

	private static final String DATABASE_CREATE
		= "create table " + TABLE_LOCATION + " ( "
		+ KEY_ID + " integer primary key, "
		+ KEY_LOCATION + " text"
		+ ");"
		;
	
	public String[] locationTypes;
	
	public LocationHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);

		// get locationTypes from string xml
		Resources res = context.getResources();
		locationTypes = res.getStringArray(R.array.facilities_location_types);	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "Creating database");
		db.execSQL(DATABASE_CREATE);
		
		// populate with values from locationTypes
		int i = 0;
		for (i = 0; i < locationTypes.length; i++) {
			db.execSQL("insert into " + TABLE_LOCATION + " (" + KEY_LOCATION + ") values (" + locationTypes[i] + ")");
			Log.d(TAG,"added " + locationTypes[i]);
		}		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "Upgrading database from version " + oldVersion
					+ " to version " + newVersion);
		// A drastic "upgrade"
		db.execSQL("drop table if exists " + TABLE_LOCATION);
		onCreate(db);
	}


}
