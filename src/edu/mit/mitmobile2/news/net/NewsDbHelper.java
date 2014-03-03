package edu.mit.mitmobile2.news.net;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NewsDbHelper extends SQLiteOpenHelper{
		
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "news.db";
	public static final String TABLE_NAME = "news_cache";
	public static final String KEY = "url";
	public static final String VALUE = "response";
	public static final String INSERTED = "inserted";
	
	private static NewsDbHelper instance;
	
	public static NewsDbHelper getInstance(Context context){
		if(instance==null){
			instance = new NewsDbHelper(context);
		}
		return instance;
	}
	
	private NewsDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		String createTextTable = "CREATE TABLE "+TABLE_NAME+" ("+KEY+" text primary key not null, "+VALUE+" text not null, "+INSERTED+" integer not null);";
		database.execSQL(createTextTable);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(NewsDbHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
	    		+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		onCreate(db);
	}
}
