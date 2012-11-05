package edu.mit.mitmobile2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import edu.mit.mitmobile2.about.Config;

public class WebImageCacheProvider extends ContentProvider {
	
	public static final String AUTHORITY = Config.release_project_name + ".WebImageCacheProvider";
	private static final String DATABASE_NAME = "imageCache.db";
	private static final int DATABASE_VERSION = 1;
	private static final String IMAGES_TABLE = "images";
	
	
	public static class Columns implements BaseColumns {
		public static final String IMAGE_ID = "_id";
		public static final String URL = "url";
		public static final String DATA = "data";
		public static final String TIMESTAMP = "timestamp";
		public static final String SIZE = "size";
	}
	
	private static final String URI_STRING = "content://" + AUTHORITY + "/" + IMAGES_TABLE;
	public final static Uri CONTENT_URI = Uri.parse(URI_STRING);
	private final static UriMatcher sUriMatcher;
	private final static int IMAGE_QUERY = 1;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, IMAGES_TABLE, IMAGE_QUERY);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(
				"CREATE TABLE " + IMAGES_TABLE + "( " +
					Columns.IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					Columns.URL + " TEXT," +
					Columns.DATA + " BLOB," +
					Columns.TIMESTAMP + " INTEGER," +
					Columns.SIZE + " INTEGER" +
				");"
			);	
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}	
	}
	
	@Override
	public String getType(Uri uri) {
		if(sUriMatcher.match(uri) == IMAGE_QUERY) {
			return "vnd.android.cursor.item/webimage";
		}
		throw new IllegalArgumentException("Unsupported URI: " + uri);
	}

	private static final String IMAGE_CACHE_SIZE_TOTAL_KEY = "cache_size_total";
	private static final String PREFS_NAME = "WebImageCachePrefs";
	private static final long MAX_BYTES = 20000000;
	
	private SharedPreferences mSettings;
	
	private DatabaseHelper mDBHelper;
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	synchronized public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		int size = values.getAsByteArray(Columns.DATA).length;
		values.put(Columns.SIZE, size);
		values.put(Columns.TIMESTAMP, System.currentTimeMillis());
		
		// prune db to limit the total size of all the cached images
		long currentCacheTotal = mSettings.getLong(IMAGE_CACHE_SIZE_TOTAL_KEY, 0);
		if(currentCacheTotal > MAX_BYTES) {
			Cursor cursor = db.query(
				IMAGES_TABLE, 
				new String[] { Columns.IMAGE_ID, Columns.SIZE },				
				null, null, null, null, 
				Columns.TIMESTAMP + " ASC"
			);
			
			int idIndex = cursor.getColumnIndex(Columns.IMAGE_ID);
			int sizeIndex = cursor.getColumnIndex(Columns.SIZE);
			if(cursor.moveToFirst()) {
				do {
				  long imageId = cursor.getLong(idIndex);
				  long imageSize = cursor.getLong(sizeIndex);
				  db.delete(IMAGES_TABLE, Columns.IMAGE_ID + "=?", new String[] {Long.toString(imageId)});
				  currentCacheTotal -= imageSize;
				  cursor.moveToNext();
				} while(currentCacheTotal > MAX_BYTES && !cursor.isAfterLast());
			}
			
			// clean up
			cursor.close();
			mSettings.edit()
				.putLong(IMAGE_CACHE_SIZE_TOTAL_KEY, currentCacheTotal)
				.commit();
		}		
		
		db.insertOrThrow(IMAGES_TABLE, Columns.SIZE, values);
		mSettings.edit()
			.putLong(IMAGE_CACHE_SIZE_TOTAL_KEY, currentCacheTotal+size)
			.commit();
			
		// only want client code to access images by URL, so don't need to return ID info
		return null;
	}

	
	@Override
	public boolean onCreate() {
		mDBHelper = new DatabaseHelper(getContext());
		mSettings = getContext().getSharedPreferences(PREFS_NAME, 0);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		if(sUriMatcher.match(uri) == IMAGE_QUERY) {
			SQLiteDatabase db = mDBHelper.getReadableDatabase();
			return db.query(IMAGES_TABLE, projection, selection, selectionArgs, null, null, Columns.TIMESTAMP + " DESC", "1");
		}
		
		throw new IllegalArgumentException("Uknown URI" + uri);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new RuntimeException("Update not implemented for this content provider");
	}

	
}
