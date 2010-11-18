package edu.mit.mitmobile.people;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.mit.mitmobile.objs.PersonItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class PeopleDB {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "people.db";
	private static final String RECENTS_TABLE = "recents";
	
	private static final String FIELD_SEPARATOR = "\t";
	
	static final class RecentsTable implements BaseColumns {
		static final String UID = "uid";
		static final String GIVENNAME = "givenname";
		static final String SURNAME = "surname";
		static final String DEPT = "dept";
		static final String EMAIL = "email";
		static final String FAX = "fax";
		static final String OFFICE = "office";
		static final String PHONE = "phone";
		static final String TITLE = "title";
		static final String LASTVIEWED = "lastViewed";
		static final String LASTUPDATE = "lastUpdate";
		
		static final String[] COLUMN_NAMES = new String[] {
			UID, GIVENNAME, SURNAME, DEPT,
			EMAIL, FAX, OFFICE, PHONE,
			TITLE, LASTUPDATE, LASTVIEWED
		};
		
		static final String[] COLUMN_TYPES = new String[] {
			"TEXT UNIQUE ON CONFLICT REPLACE",
			"TEXT", "TEXT", "TEXT", "TEXT", "TEXT",
			"TEXT", "TEXT", "TEXT", "INTEGER", "INTEGER"
		};
		
		static final String NULL_HACK = UID;
		static final String ORDER_BY = LASTVIEWED + " DESC";
	}
	
	private SQLiteOpenHelper mDBHelper;
	
	private static PeopleDB sInstance = null;
	
	public static PeopleDB getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new PeopleDB(context);
		}
		return sInstance;
	}
	
	private PeopleDB(Context context) {
		mDBHelper = new PeopleDBOpenHelper(context);
	}
	
	public List<PersonItem> getAllAsList() {
		ArrayList<PersonItem> items = new ArrayList<PersonItem>();
		Cursor c = getAllRecords();
		c.moveToFirst();
		for (int row = 0; row < c.getCount(); row++) {
			PersonItem person = personFromCursor(c);
			items.add(person);
			c.moveToNext();
		}
		c.close();
		return items;
	}
	
	public Cursor getAllRecords() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.query(
				RECENTS_TABLE,
				RecentsTable.COLUMN_NAMES,
				null, null, null, null, RecentsTable.ORDER_BY);
		return cursor;
	}
	
	public PersonItem getRecord(String uid) {

		PersonItem person = null;
		
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.query(RECENTS_TABLE,
				RecentsTable.COLUMN_NAMES,
				null, null, null, null, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			person = personFromCursor(cursor);
		}
		cursor.close();
		return person;
		
	}
	
	synchronized void addPerson(PersonItem person) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(RecentsTable.UID, person.uid);
		values.put(RecentsTable.GIVENNAME, stringFromList(person.givenname));
		values.put(RecentsTable.SURNAME, stringFromList(person.surname));
		values.put(RecentsTable.DEPT, stringFromList(person.dept));
		values.put(RecentsTable.EMAIL, stringFromList(person.email));
		values.put(RecentsTable.FAX, stringFromList(person.fax));
		values.put(RecentsTable.OFFICE, stringFromList(person.office));
		values.put(RecentsTable.PHONE, stringFromList(person.phone));
		values.put(RecentsTable.TITLE, stringFromList(person.title));
		values.put(RecentsTable.LASTUPDATE, (int)(person.lastUpdate.getTime() / 1000));
		values.put(RecentsTable.LASTVIEWED, (int)(person.lastViewed.getTime() / 1000));
		db.insert(RECENTS_TABLE, RecentsTable.NULL_HACK, values);
	}
	
	synchronized void clearAll() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(RECENTS_TABLE, null, null);
	}
	
	private static PersonItem personFromCursor(Cursor cursor) {
		PersonItem person = new PersonItem();
		person.uid = cursor.getString(cursor.getColumnIndex(RecentsTable.UID));
		person.givenname = listFromString(
				cursor.getString(cursor.getColumnIndex(RecentsTable.GIVENNAME)));
		person.surname = listFromString(
				cursor.getString(cursor.getColumnIndex(RecentsTable.SURNAME)));
		person.dept = listFromString(
				cursor.getString(cursor.getColumnIndex(RecentsTable.DEPT)));
		person.email = listFromString(
				cursor.getString(cursor.getColumnIndex(RecentsTable.EMAIL)));
		person.fax = listFromString(
				cursor.getString(cursor.getColumnIndex(RecentsTable.FAX)));
		person.office = listFromString(
				cursor.getString(cursor.getColumnIndex(RecentsTable.OFFICE)));
		person.phone = listFromString(
				cursor.getString(cursor.getColumnIndex(RecentsTable.PHONE)));
		person.title = listFromString(
				cursor.getString(cursor.getColumnIndex(RecentsTable.TITLE)));
		person.lastUpdate = new Date(cursor.getInt(cursor.getColumnIndex(RecentsTable.LASTUPDATE)) * 1000);
		person.lastViewed = new Date(cursor.getInt(cursor.getColumnIndex(RecentsTable.LASTVIEWED)) * 1000);
		return person;
	}
	
	private static String stringFromList(List<String> aList) {
		
		
		StringBuffer sb = new StringBuffer();
		int size = aList.size();
		for (int i = 0; i < size; i++) {
			sb.append(aList.get(i));
			if (i != size - 1) {
				sb.append(FIELD_SEPARATOR);
			}
		}
		return sb.toString();
	}
	
	private static List<String> listFromString(String aString) {
		if (aString.length() == 0)
			return new ArrayList<String>();
		return Arrays.asList(aString.split(FIELD_SEPARATOR));
	}
	
	private static class PeopleDBOpenHelper extends SQLiteOpenHelper {
		public PeopleDBOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuilder builder = new StringBuilder();
			builder.append("CREATE TABLE " + RECENTS_TABLE + " (");
			builder.append(RecentsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
			for (int i = 0; i < RecentsTable.COLUMN_NAMES.length; i++) {
				String name = RecentsTable.COLUMN_NAMES[i];
				String type = RecentsTable.COLUMN_TYPES[i];
				builder.append(name + " " + type);
				if (i != RecentsTable.COLUMN_NAMES.length - 1) {
					builder.append(",");
				}
			}
			builder.append(");");
			
			db.execSQL(builder.toString());
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVesion, int newVersion) {
			throw new UnsupportedOperationException();
		}
	}

}
