package edu.mit.mitmobile2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.SearchRecentSuggestions;
import android.text.TextUtils;
import android.util.Log;

/**
 * This unfortunate class is largely a copy of the
 * android.provider.SearchRecentSuggestions source code, which
 * we modify slightly because the built-in suggestions.db is not
 * designed to distinguish between multiple content authorities
 * within the same app.
 */
public class MITSearchRecentSuggestions extends SearchRecentSuggestions {

	private static final String TAG = "MITSearchRecentSuggestions";
	private static final String TABLE_NAME = "mit_suggestions";
	
	/** 
	 * everything from here on is copied verbatim from the superclass
	 * except minor modifications where specified
	 */
	
    private Context mContext;
    private String mAuthority;
    private boolean mTwoLineDisplay;
    private Uri mSuggestionsUri;
    @SuppressWarnings("unused")
	private String[] mQueriesProjection;
    
    private static final int MAX_HISTORY_COUNT = 250;
	
    static class SuggestionColumns implements BaseColumns {
        public static final String DISPLAY1 = "display1";
        public static final String DISPLAY2 = "display2";
        public static final String QUERY = "query";
        public static final String DATE = "date";
		public static final String AUTHORITY = "authority"; // added for mit
    }
    
    public static final String[] MIT_QUERIES_PROJECTION_1LINE = new String[] { // name change
        SuggestionColumns._ID,
        SuggestionColumns.DATE,
        SuggestionColumns.QUERY, 
        SuggestionColumns.DISPLAY1,
        SuggestionColumns.AUTHORITY, // added for mit
    };
    
    public static final String[] MIT_QUERIES_PROJECTION_2LINE = new String[] { // name change
        SuggestionColumns._ID, 
        SuggestionColumns.DATE,
        SuggestionColumns.QUERY, 
        SuggestionColumns.DISPLAY1,
        SuggestionColumns.DISPLAY2,
        SuggestionColumns.AUTHORITY, // added for mit
    };
    

    @Override
	public void saveRecentQuery(String queryString, String line2) {
        if (TextUtils.isEmpty(queryString)) {
            return;
        }
        if (!mTwoLineDisplay && !TextUtils.isEmpty(line2)) {
            throw new IllegalArgumentException();
        }
        
        ContentResolver cr = mContext.getContentResolver();
        long now = System.currentTimeMillis();
        
        // Use content resolver (not cursor) to insert/update this query
        try {
            ContentValues values = new ContentValues();
            values.put(SuggestionColumns.DISPLAY1, queryString);
            if (mTwoLineDisplay) {
                values.put(SuggestionColumns.DISPLAY2, line2);
            }
            values.put(SuggestionColumns.QUERY, queryString);
            values.put(SuggestionColumns.DATE, now);
            values.put(SuggestionColumns.AUTHORITY, mAuthority);
            cr.insert(mSuggestionsUri, values);
        } catch (RuntimeException e) {
            Log.e(TAG, "saveRecentQuery", e);
        }
        
        // Shorten the list (if it has become too long)
        truncateHistory(cr, MAX_HISTORY_COUNT);
    }

    @Override
	protected void truncateHistory(ContentResolver cr, int maxEntries) {
        if (maxEntries < 0) {
            throw new IllegalArgumentException();
        }
        
        try {
            // null means "delete all".  otherwise "delete but leave n newest"
            String selection = null;
            if (maxEntries > 0) {
                selection = SuggestionColumns._ID + " IN " +
                        "(SELECT " + SuggestionColumns._ID +
                        " FROM " + TABLE_NAME +
                        " WHERE " + SuggestionColumns.AUTHORITY + " LIKE '" + mAuthority + "'" +
                        " ORDER BY " + SuggestionColumns.DATE + " DESC" +
                        " LIMIT -1 OFFSET " + String.valueOf(maxEntries) + ")";
            }
            cr.delete(mSuggestionsUri, selection, null);
        } catch (RuntimeException e) {
            Log.e(TAG, "truncateHistory", e);
        }
    }
    
    
	public MITSearchRecentSuggestions(Context context, String authority,
			int mode) {
		super(context, authority, mode);

		// copied from superclass
        mTwoLineDisplay = (0 != (mode & SearchRecentSuggestionsProvider.DATABASE_MODE_2LINES));
        mContext = context;
        mAuthority = new String(authority);
        mSuggestionsUri = Uri.parse("content://" + mAuthority + "/" + TABLE_NAME);
        Log.d(TAG, mSuggestionsUri.toString());
        
        if (mTwoLineDisplay) {
            mQueriesProjection = MIT_QUERIES_PROJECTION_2LINE;
        } else {
            mQueriesProjection = MIT_QUERIES_PROJECTION_1LINE;
        }
	}

    
    
}
