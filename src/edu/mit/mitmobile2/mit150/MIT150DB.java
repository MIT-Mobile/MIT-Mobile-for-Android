package edu.mit.mitmobile2.mit150;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;


public class MIT150DB {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "mit150.db";
	private static final String FEATURES_TABLE = "features";
	
	private static final String MORE_THUMBS_TABLE = "more_thumbnails";
	
	private static final String FEATURE_ID = "feature_id";
	private static final String TITLE = "title";
	private static final String SUBTITLE = "subtitle";
	private static final String TINT_COLOR = "tint_color";
	private static final String TITLE_COLOR = "title_color";
	private static final String ARROW_COLOR = "arrow_color";
	private static final String URL = "url";
	private static final String PHOTO_URL = "photo_url";
	private static final String BITMAP = "bitmap";
	private static final String DIM_H = "dim_h";
	private static final String DIM_W = "dim_w";
	

	static String WHERE_URL = URL+"=?";

	static String[] proj_thumb = {URL,BITMAP};
	
	static String[] proj = {FEATURE_ID,TITLE,SUBTITLE,TINT_COLOR,TITLE_COLOR,ARROW_COLOR,URL,PHOTO_URL,BITMAP,DIM_H,DIM_W};
	
	SQLiteOpenHelper mMIT150DBHelper;
	
	private static MIT150DB mit150DBInstance = null;
	
	public static MIT150DB getInstance(Context context) {
		if(mit150DBInstance == null) {
			mit150DBInstance = new MIT150DB(context);
			return mit150DBInstance;
		} else {
			return mit150DBInstance;
		}
	}
	
	private MIT150DB(Context context) {
		mMIT150DBHelper = new MIT150DatabaseHelper(context);
	}


	public ArrayList<MIT150FeatureItem> getCachedFeatures() {

		SQLiteDatabase db = mMIT150DBHelper.getReadableDatabase();
		ArrayList<MIT150FeatureItem> fs = new ArrayList<MIT150FeatureItem>();
		MIT150FeatureItem f;
		
		Cursor c = db.query(FEATURES_TABLE, proj, null, null, null, null, null);
		c.moveToFirst();
		while(!c.isAfterLast()) {
			f = retrieveFeature(c);	
			c.moveToNext();
			fs.add(f);
		}
		c.close();
		
		// FIXME need to keep same expected order in array.. use hash with title key?
		
		return fs;
	}
	
	
	public void updateFeatures(ArrayList<MIT150FeatureItem> fs) {
		
		SQLiteDatabase db = mMIT150DBHelper.getWritableDatabase();
		
		// delete all old entries
		db.delete(FEATURES_TABLE, null, null);
		
		// insert all new replacements
		for (MIT150FeatureItem f : fs) {
			saveFeature(f);
		}
		
	}
	
	static MIT150FeatureItem retrieveFeature(Cursor cursor) {
		
		int feature_id_index = cursor.getColumnIndex(FEATURE_ID);
		int title_index = cursor.getColumnIndex(TITLE);
		int subtitle_index = cursor.getColumnIndex(SUBTITLE);
		int tint_index = cursor.getColumnIndex(TINT_COLOR);
		int title_color_index = cursor.getColumnIndex(TITLE_COLOR);
		int arrow_color_index = cursor.getColumnIndex(ARROW_COLOR);
		int url_index = cursor.getColumnIndex(URL);
		int photo_index = cursor.getColumnIndex(PHOTO_URL);
		int bitmap_index = cursor.getColumnIndex(BITMAP);
		int dim_h_index = cursor.getColumnIndex(DIM_H);		
		int dim_w_index = cursor.getColumnIndex(DIM_W);
		
		MIT150FeatureItem item = new MIT150FeatureItem();
		item.id = cursor.getString(feature_id_index);
		item.title = cursor.getString(title_index);
		item.subtitle = cursor.getString(subtitle_index);
		item.setTintColor(cursor.getInt(tint_index));
		item.setTitleColor(cursor.getInt(title_color_index));
		item.setArrowColor(cursor.getInt(arrow_color_index));
		item.url = cursor.getString(url_index);
		item.photo_url = cursor.getString(photo_index);
		item.dim.height = cursor.getInt(dim_h_index);
		item.dim.width = cursor.getInt(dim_w_index);
		
		
		byte[] imageBytes = cursor.getBlob(bitmap_index);
		if (imageBytes!=null) {
			item.bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
			assert(item.bm!=null);
		}
		
		return item;
	}	
	
	

	public synchronized void saveFeature(MIT150FeatureItem featureItem) {
		
		ContentValues cv = new ContentValues();
		cv.put(FEATURE_ID, featureItem.id);
		cv.put(TITLE, featureItem.title);
		cv.put(SUBTITLE, featureItem.subtitle);
		cv.put(TINT_COLOR, featureItem.getTintColor());
		cv.put(TITLE_COLOR, featureItem.getTitleColor());
		cv.put(ARROW_COLOR, featureItem.getArrowColor());
		cv.put(URL, featureItem.url);
		cv.put(PHOTO_URL, featureItem.photo_url);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 

		cv.put(BITMAP, baos.toByteArray());
		
		cv.put(DIM_H, featureItem.dim.height);
		cv.put(DIM_W, featureItem.dim.width);
		
		
		//db.update(FEATURES_TABLE, cv, MIT150FeatureItem._ID+"="+featureItem.row_id, null);
	}
	


	/*********************************************************************/
	public synchronized void saveMoreThumbnail(MIT150MoreItem moreItem) {
		
		@SuppressWarnings("unused")
		boolean update;

		//assert(moreItem.bd!=null);
		
		if (moreItem.bd==null) {
			return;
		}
		
		SQLiteDatabase db = mMIT150DBHelper.getWritableDatabase();
		
		// Does it already exist?
		String[] where_args = new String[]{moreItem.url};
		Cursor cursor = db.query(MORE_THUMBS_TABLE, proj_thumb, WHERE_URL, where_args, null, null, null);
		if (cursor.getCount()==0) update = false;
		else update = true;
		cursor.close();
		
		ContentValues cv = new ContentValues();
		cv.put(URL, moreItem.url);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 


		try {
			baos.flush();
			cv.put(BITMAP, baos.toByteArray());
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        /*		
		if (update) {
			int rows = db.update(MORE_THUMBS_TABLE, cv, WHERE_URL, where_args);
		} else {
			long row_id = db.insert(MORE_THUMBS_TABLE, null, cv);
		} */
	}
	
	void retrieveMoreThumb(MIT150MoreItem item) {

		SQLiteDatabase db = mMIT150DBHelper.getReadableDatabase();
		
		Cursor cursor = db.query(MORE_THUMBS_TABLE, proj_thumb, WHERE_URL, new String[]{item.url}, null, null, null);
		if (cursor.getCount()==0) {
			cursor.close();
			return;
		}
		cursor.moveToFirst();
		

		int bitmap_index = cursor.getColumnIndex(BITMAP);
		
		Bitmap bm;
		byte[] imageBytes = cursor.getBlob(bitmap_index);
		cursor.close();
		if (imageBytes!=null) {
			bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
			//assert(bm!=null);
			if (bm!=null) item.bd = new BitmapDrawable(bm);
		}		
	}	
	
	public void getCachedMoreThumbnails(ArrayList<MIT150MoreFeaturesItem> moreItems) {
		
		for (MIT150MoreFeaturesItem mfi : moreItems) {
			for (MIT150MoreItem mi : mfi.items) {
				retrieveMoreThumb(mi);
			}
		}
		
	}
	/*********************************************************************/
	
	private static class MIT150DatabaseHelper extends SQLiteOpenHelper {
		
		MIT150DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
		
			db.execSQL("CREATE TABLE " + FEATURES_TABLE + " ("
					+ FEATURE_ID + " TEXT,"
					+ TITLE + " TEXT,"
					+ SUBTITLE + " TEXT,"
					+ TINT_COLOR + " INTEGER,"
					+ TITLE_COLOR + " INTEGER,"
					+ ARROW_COLOR + " INTEGER,"
					+ URL + " TEXT,"
					+ PHOTO_URL + " TEXT,"
					+ BITMAP + " BLOB,"
					+ DIM_H + " INTEGER,"
					+ DIM_W + " INTEGER"
				+ ");");

			db.execSQL("CREATE TABLE " + MORE_THUMBS_TABLE + " ("
					+ URL + " TEXT,"
					+ BITMAP + " BLOB"
				+ ");");
					
		
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// simplist migration possible delete old table, create new table
			db.execSQL("DROP TABLE " + FEATURES_TABLE + ";");
			db.execSQL("DROP TABLE " + MORE_THUMBS_TABLE + ";");
			
			onCreate(db);
		}
	}

}

