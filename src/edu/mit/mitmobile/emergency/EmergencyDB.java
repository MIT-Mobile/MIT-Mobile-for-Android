package edu.mit.mitmobile.emergency;

import edu.mit.mitmobile.objs.EmergencyItem.Contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class EmergencyDB {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "emergency.db";
	private static final String CONTACTS_TABLE = "emergency_contacts";
	
	private static final String SHORT_LIST_LIMIT = "3";
	
	// table columns
	static final class ContactsTable implements BaseColumns {
		static final String CONTACT_NAME = "contact";
		static final String CONTACT_DESCRIPTION = "description";
		static final String CONTACT_PHONE = "phone";
	}

	SQLiteOpenHelper mDBHelper;
	
	private static EmergencyDB sInstance = null;
	
	public static EmergencyDB getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new EmergencyDB(context);
		}
		return sInstance;
	}
	
	private EmergencyDB(Context context) {
		mDBHelper = new EmergencyDBOpenHelper(context);
	}
	
	public Cursor getLimitedContactsCursor() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.query(CONTACTS_TABLE, new String[] {
				ContactsTable._ID,
				ContactsTable.CONTACT_NAME,
				ContactsTable.CONTACT_PHONE,
				ContactsTable.CONTACT_DESCRIPTION
				}, null, null, null, null, null, SHORT_LIST_LIMIT);
		return cursor;
	}
	
	public Cursor getContactsCursor() {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.query(CONTACTS_TABLE, new String[] {
				ContactsTable._ID,
				ContactsTable.CONTACT_NAME,
				ContactsTable.CONTACT_PHONE,
				ContactsTable.CONTACT_DESCRIPTION
				}, null, null, null, null, null);
		return cursor;
	}
	
	public Contact getContact(int position) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Contact contact = null;
		Cursor cursor = db.query(CONTACTS_TABLE,new String[] {
				ContactsTable._ID,
				ContactsTable.CONTACT_NAME,
				ContactsTable.CONTACT_PHONE,
				ContactsTable.CONTACT_DESCRIPTION },
				null, null, null, null, null,
				String.valueOf(position) + ",1");
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			contact = new Contact();
			contact.contact = cursor.getString(cursor.getColumnIndex(ContactsTable.CONTACT_NAME));
			contact.phone = cursor.getString(cursor.getColumnIndex(ContactsTable.CONTACT_PHONE));
			contact.description = cursor.getString(cursor.getColumnIndex(ContactsTable.CONTACT_DESCRIPTION));
		}
		cursor.close();
		return contact;
	}
	
	synchronized void clearAll() {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.delete(CONTACTS_TABLE, null, null);
	}
	
	void startTransaction() {
		mDBHelper.getWritableDatabase().beginTransaction();
	}
	
	void endTransaction() {
		mDBHelper.getWritableDatabase().setTransactionSuccessful();
		mDBHelper.getWritableDatabase().endTransaction();
	}
	
	synchronized void addContact(Contact contact) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ContactsTable.CONTACT_NAME, contact.contact);
		values.put(ContactsTable.CONTACT_PHONE, contact.phone);
		if (contact.description != null) {
			values.put(ContactsTable.CONTACT_DESCRIPTION, contact.description);
		}
		
		db.insert(CONTACTS_TABLE, ContactsTable.CONTACT_NAME, values);
	}
	
	private static class EmergencyDBOpenHelper extends SQLiteOpenHelper {
		public EmergencyDBOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + CONTACTS_TABLE + " ("
					+ ContactsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ ContactsTable.CONTACT_NAME + " TEXT,"
					+ ContactsTable.CONTACT_PHONE + " TEXT,"
					+ ContactsTable.CONTACT_DESCRIPTION + " TEXT"
					+ ");");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO implement when upgrades available
		}
	}
	
}
