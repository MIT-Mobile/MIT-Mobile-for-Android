package edu.mit.mitmobile2.qrreader;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import edu.mit.mitmobile2.objs.NewsItem;
import edu.mit.mitmobile2.objs.NewsItem.Image;

public class QRCodeDB {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "qrreader.db";
	private static final String QRCODES_TABLE = "qrcodes";

	private static final String ID = "_id";
	private static final String URL = "title";
	private static final String IMAGE = "image";
	private static final String DATE = "date";
	
	SQLiteOpenHelper mQRCodeDBHelper;
	
	private static QRCodeDB qrcodeDBInstance = null;
	
	public static QRCodeDB getInstance(Context context) {
		if(qrcodeDBInstance == null) {
			qrcodeDBInstance = new QRCodeDB(context);
			return qrcodeDBInstance;
		} else {
			return qrcodeDBInstance;
		}
	}
	
	private QRCodeDB(Context context) {
		mQRCodeDBHelper = new QRCodeDatabaseHelper(context); 
	}
	
	public synchronized List<QRCode> getQRCodes() {
		SQLiteDatabase db = mQRCodeDBHelper.getReadableDatabase();
		Cursor cursor = db.query(QRCODES_TABLE, null, null, null, null, null, DATE + " DESC");
		
		int url_index = cursor.getColumnIndex(URL);
		int image_index = cursor.getColumnIndex(IMAGE);
		int date_index = cursor.getColumnIndex(DATE);
		
		ArrayList<QRCode> qrcodes = new ArrayList<QRCode>();
		if(cursor.moveToFirst()) {
			while(!cursor.isAfterLast()) {
				String url = cursor.getString(url_index);
				byte[] imageBytes = cursor.getBlob(image_index);
				Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
				Date date = new Date(cursor.getLong(date_index));
				qrcodes.add(new QRCode(url, bitmap, date));
				cursor.moveToNext();
			}
		}
		cursor.close();
		db.close();
		return qrcodes;
	}
	
	public synchronized void insertQRCode(QRCode qrcode) {
		SQLiteDatabase db = mQRCodeDBHelper.getWritableDatabase();
		
		ContentValues qrcodeValues = new ContentValues();
		qrcodeValues.put(URL, qrcode.getUrl());
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		qrcode.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, ostream);
		qrcodeValues.put(IMAGE, ostream.toByteArray());
		qrcodeValues.put(DATE, qrcode.getDate().getTime());
		db.insert(QRCODES_TABLE, URL, qrcodeValues);
		
		mQRCodeDBHelper.close();
	}
	
	public synchronized void removeOldestQRCode() {
		SQLiteDatabase db = mQRCodeDBHelper.getWritableDatabase();
		Cursor cursor = db.query(QRCODES_TABLE, null, null, null, null, null, DATE + " ASC", "1");
		
		int id_index = cursor.getColumnIndex(ID);
		
		Integer oldestID = null;
		if(cursor.moveToFirst()) {
			oldestID = cursor.getInt(id_index);
		}
		cursor.close();
		
		if(oldestID != null) {
			db.delete(QRCODES_TABLE, ID + "=?", new String[] {Integer.toString(oldestID)});
		}
		
		db.close();
	}
	
	public synchronized int qrcodesCount() {
		SQLiteDatabase db = mQRCodeDBHelper.getReadableDatabase();
		Cursor cursor = db.query(QRCODES_TABLE, new String[] {ID}, null, null, null, null, null);
		
		int count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}	

	private static class QRCodeDatabaseHelper extends SQLiteOpenHelper {
		
		QRCodeDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + QRCODES_TABLE + " ("
					+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
					+ URL + " TEXT,"
					+ IMAGE + " BLOB,"
					+ DATE + " INTEGER"
				+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// no old versions exists
		}
	}
}
