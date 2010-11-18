package edu.mit.mitmobile;

import edu.mit.mitmobile.MITSearchRecentSuggestions.SuggestionColumns;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * This is a slightly edited subset of the
 * android.content.SearchRecentSuggestionsProvider source code.
 * 
 * The necessity of this class is an unfortunate consequence of the
 * fact that the built-in suggestions.db is not designed to distinguish
 * between multiple content authorities within the same app.
 */
public class MITSearchRecentsProvider extends SearchRecentSuggestionsProvider {
	
    /** our own fields **/
    
	private static final String sDatabaseName = "mit_suggestions.db";
	private static final String TABLE_NAME = "mit_suggestions";
    private static final String TAG = "MITSuggestionsProvider";

    /*
	static final class Columns implements BaseColumns {
		static final String DISPLAY1 = "display1";
		static final String DISPLAY2 = "display2";
		static final String QUERY = "query";
		static final String DATE = "date";
		static final String AUTHORITY = "authority";
	}
	*/

    /** fields copied from superclass **/

	private SQLiteOpenHelper mOpenHelper;
	private static final String ORDER_BY = "date DESC";
	private static final String NULL_COLUMN = SuggestionColumns.QUERY;
	private static final String sSuggestions = "mit_suggestions";
    
	private Uri mSuggestionsUri;
    private UriMatcher mUriMatcher;
    private String mAuthority;
    private int mMode;
    private boolean mTwoLineDisplay;
    
    private String mSuggestSuggestionClause;
    private String[] mSuggestionProjection;

    // Uri and query support
    private static final int URI_MATCH_SUGGEST = 1;

    // Table of database versions.  Don't forget to update!
    // NOTE:  These version values are shifted left 8 bits (x 256) in order to create space for
    // a small set of mode bitflags in the version int.
    //
    // 1      original implementation with queries, and 1 or 2 display columns
    // 1->2   added UNIQUE constraint to display1 column
    private static final int DATABASE_VERSION = 2 * 256;
	
	/*
	@Override
	String getType(Uri uri) {
		
	}
	*/
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        
        // special case for actual suggestions (from search manager)
        if (mUriMatcher.match(uri) == URI_MATCH_SUGGEST) {
            String suggestSelection;
            String[] myArgs;
            if (TextUtils.isEmpty(selectionArgs[0])) {
                suggestSelection = SuggestionColumns.AUTHORITY + " LIKE ?";
                myArgs = new String[] { mAuthority };
            } else {
                String like = "%" + selectionArgs[0] + "%";
                if (mTwoLineDisplay) {
                    myArgs = new String [] { like, like, mAuthority };
                } else {
                    myArgs = new String [] { like, mAuthority };
                }
                suggestSelection = mSuggestSuggestionClause;
            }
            // Suggestions are always performed with the default sort order
            Cursor c = db.query(sSuggestions, mSuggestionProjection,
                    suggestSelection, myArgs, null, null, ORDER_BY, null);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        }
        
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
	}
	
	/**
	 * code copied from superclass and amended to match our table columns
	 */
	@Override
    protected void setupSuggestions(String authority, int mode) {
        if (TextUtils.isEmpty(authority) || 
                ((mode & DATABASE_MODE_QUERIES) == 0)) {
            throw new IllegalArgumentException();
        }
        // unpack mode flags
        mTwoLineDisplay = (0 != (mode & DATABASE_MODE_2LINES));
            
        // saved values
        mAuthority = new String(authority);
        mMode = mode;
        
        // derived values
        mSuggestionsUri = Uri.parse("content://" + mAuthority + "/" + TABLE_NAME);
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(mAuthority, SearchManager.SUGGEST_URI_PATH_QUERY, URI_MATCH_SUGGEST);
        
        if (mTwoLineDisplay) {
            mSuggestSuggestionClause = "(" 
            	+ SuggestionColumns.DISPLAY1 + " LIKE ? OR "
            	+ SuggestionColumns.DISPLAY2 + " LIKE ?) AND "
            	+ SuggestionColumns.AUTHORITY + " LIKE ?";

            mSuggestionProjection = new String [] {
                    "0 AS " + SearchManager.SUGGEST_COLUMN_FORMAT,
                    SuggestionColumns.DISPLAY1 + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
                    SuggestionColumns.DISPLAY2 + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_2,
                    SuggestionColumns.QUERY + " AS " + SearchManager.SUGGEST_COLUMN_QUERY,
                    SuggestionColumns._ID
            };
        } else {
            mSuggestSuggestionClause = SuggestionColumns.DISPLAY1 + " LIKE ? AND "
            	+ SuggestionColumns.AUTHORITY + " LIKE ?";

            mSuggestionProjection = new String [] {
                    "0 AS " + SearchManager.SUGGEST_COLUMN_FORMAT,
                    SuggestionColumns.DISPLAY1 + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
                    SuggestionColumns.QUERY + " AS " + SearchManager.SUGGEST_COLUMN_QUERY,
                    SuggestionColumns._ID
            };
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        
        private int mNewVersion;
        
        public DatabaseHelper(Context context, int newVersion) {
            super(context, sDatabaseName, null, newVersion);
            mNewVersion = newVersion;
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE " + TABLE_NAME + " (");
            builder.append(SuggestionColumns._ID + " INTEGER PRIMARY KEY,");
            builder.append(SuggestionColumns.DISPLAY1 + " TEXT UNIQUE ON CONFLICT REPLACE, ");

            if (0 != (mNewVersion & DATABASE_MODE_2LINES)) {
                builder.append(SuggestionColumns.DISPLAY2 + " TEXT,");
            }
            
            builder.append(SuggestionColumns.QUERY + " TEXT, ");
            builder.append(SuggestionColumns.AUTHORITY + " TEXT, ");
            builder.append(SuggestionColumns.DATE + " LONG);");
            db.execSQL(builder.toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
	
	/**
	 * code copied verbatim from superclass
	 */
	@Override
	public boolean onCreate() {
        if (mAuthority == null || mMode == 0) {
            throw new IllegalArgumentException("Provider not configured");
        }
        int mWorkingDbVersion = DATABASE_VERSION + mMode;
        mOpenHelper = new DatabaseHelper(getContext(), mWorkingDbVersion);
        
        return true;
	}

    /**
     * This method is provided for use by the ContentResolver.  Do not override, or directly
     * call from your own code.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int length = uri.getPathSegments().size();
        if (length < 1) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        // Note:  This table has on-conflict-replace semantics, so insert() may actually replace()
        long rowID = -1;
        String base = uri.getPathSegments().get(0);
        Uri newUri = null;
        if (base.equals(sSuggestions)) {
            if (length == 1) {
                rowID = db.insert(sSuggestions, NULL_COLUMN, values);
                if (rowID > 0) {
                    newUri = Uri.withAppendedPath(mSuggestionsUri, String.valueOf(rowID));
                }
            }
        }
        if (rowID < 0) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

	/**
	 * code copied verbatim from superclass
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int length = uri.getPathSegments().size();
        if (length != 1) {
            throw new IllegalArgumentException("Unknown Uri");
        }

        final String base = uri.getPathSegments().get(0);
        int count = 0;
        if (base.equals(sSuggestions)) {
            count = db.delete(sSuggestions, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown Uri");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
	}
}
