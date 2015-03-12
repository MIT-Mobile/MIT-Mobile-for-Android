package edu.mit.mitmobile2.shuttles;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import org.apache.http.auth.AUTH;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.Schema;
import timber.log.Timber;

public class MITShuttlesProvider extends ContentProvider {

    private static final int ROUTES = 10;
    private static final int ROUTE_ID = 20;
    private static final int STOPS = 30;
    private static final int STOPS_ID = 40;

    private static final String AUTHORITY = "edu.mit.mitmobile2.provider";

    private static final String BASE_PATH = "shuttles";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/routes", ROUTES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/routes/*", ROUTE_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/stops", STOPS);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        Timber.d("Query");

        int uriType = sURIMatcher.match(uri);
        Timber.d("Uri= " + uriType);
        Timber.d("Selection= " + selection);

        switch (uriType) {
            case ROUTES:
                cursor = MitMobileApplication.dbAdapter.db.query(Schema.Route.TABLE_NAME, Schema.Route.ALL_COLUMNS, selection, null, null, null, null);
                break;
            case STOPS:
                cursor = MitMobileApplication.dbAdapter.db.query(Schema.Stop.TABLE_NAME, Schema.Stop.ALL_COLUMNS, selection, null, null, null, null);
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return new String();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Timber.d("Insert");

        int uriType = sURIMatcher.match(uri);
        Timber.d("Uri= " + uriType);
        switch (uriType) {
            case ROUTES:
                long newID = MitMobileApplication.dbAdapter.db.insertWithOnConflict(Schema.Route.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_FAIL);
                Timber.d("DB id= " + newID);
                if (newID <= 0) {
                    throw new SQLException("Error");
                }
                break;
            case STOPS:
                long newStopID = MitMobileApplication.dbAdapter.db.insertWithOnConflict(Schema.Stop.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_FAIL);
                Timber.d("DB id= " + newStopID);
                if (newStopID <= 0) {
                    throw new SQLException("Error");
                }
                break;

        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Timber.d("Update");

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case ROUTES:
                MitMobileApplication.dbAdapter.db.update(Schema.Route.TABLE_NAME, values,
                        Schema.Route.ROUTE_ID + " = \'" + values.get(Schema.Route.ROUTE_ID) + "\'", null);
                break;
            case STOPS:
                MitMobileApplication.dbAdapter.db.update(Schema.Stop.TABLE_NAME, values,
                        Schema.Stop.STOP_ID + " = \'" + values.get(Schema.Stop.STOP_ID) + "\'", null);
                break;

        }

        return 0;
    }
}
