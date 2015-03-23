package edu.mit.mitmobile2.shuttles;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.Schema;
import timber.log.Timber;

public class MITShuttlesProvider extends ContentProvider {

    public static final int ROUTES = 10;
    public static final int ROUTE_ID = 20;
    public static final int STOPS = 30;
    public static final int STOPS_ID = 40;
    public static final int CHECK_ROUTES = 50;
    public static final int CHECK_STOPS = 60;
    public static final int PREDICTIONS = 70;
    public static final int LOCATION = 80;

    private static final String AUTHORITY = "edu.mit.mitmobile2.provider";

    private static final String BASE_PATH = "shuttles";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final Uri ALL_ROUTES_URI = Uri.parse(CONTENT_URI.toString() + "/routes");
    public static final Uri STOPS_URI = Uri.parse(CONTENT_URI.toString() + "/stops");
    public static final Uri CHECK_ROUTES_URI = Uri.parse(ALL_ROUTES_URI.toString() + "/check");
    public static final Uri CHECK_STOPS_URI = Uri.parse(STOPS_URI.toString() + "/check");
    public static final Uri PREDICTIONS_URI = Uri.parse(CONTENT_URI.toString() + "/predictions");
    public static final Uri VEHICLES_URI = Uri.parse(CONTENT_URI.toString() + "/vehicles");
    public static final Uri LOCATION_URI = Uri.parse(CONTENT_URI.toString() + "/location");

    private static final String queryString = "SELECT route_stops._id AS rs_id, stops._id AS s_id, routes._id, routes.route_id, routes.route_url, routes.route_title, routes.agency, routes.scheduled, routes.predictable, routes.route_description, routes.predictions_url, routes.vehicles_url, routes.path_id, stops.stop_id, stops.stop_url, stops.stop_title, stops.stop_lat, stops.stop_lon, stops.stop_number, stops.distance, stops.predictions " +
            "FROM routes " +
            "INNER JOIN route_stops ON routes.route_id = route_stops.route_id " +
            "JOIN stops ON route_stops.stop_id = stops.stop_id " +
            "ORDER BY routes._id ASC, stops.distance ASC";

    public static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/routes", ROUTES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/stops", STOPS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/routes/check", CHECK_ROUTES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/stops/check", CHECK_STOPS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/routes/*", ROUTE_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/stops/*", STOPS_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/predictions", PREDICTIONS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/location", LOCATION);
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
            case CHECK_ROUTES:
                cursor = MitMobileApplication.dbAdapter.db.query(Schema.Route.TABLE_NAME, new String[]{Schema.Route.ID_COL}, selection, null, null, null, null);
                break;
            case CHECK_STOPS:
                cursor = MitMobileApplication.dbAdapter.db.query(Schema.Stop.TABLE_NAME, new String[]{Schema.Stop.ID_COL}, selection, null, null, null, null);
                break;
            case ROUTES:
                cursor = MitMobileApplication.dbAdapter.db.rawQuery(queryString, null, null);
                break;
            case STOPS_ID:
                cursor = MitMobileApplication.dbAdapter.db.rawQuery(buildStopQueryString(selection), null, null);
                break;
            case ROUTE_ID:
                cursor = MitMobileApplication.dbAdapter.db.rawQuery(buildQueryString(Schema.Route.TABLE_NAME + "." + selection), null, null);
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
        long newID = 0;

        switch (uriType) {
            case ROUTES:
                newID = MitMobileApplication.dbAdapter.db.insertWithOnConflict(Schema.Route.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_FAIL);
                if (newID <= 0) {
                    throw new SQLException("Error");
                }
                break;
            case STOPS:
                newID = MitMobileApplication.dbAdapter.db.insertWithOnConflict(Schema.Stop.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_FAIL);
                if (newID <= 0) {
                    throw new SQLException("Error");
                }
                break;
            case LOCATION:
                newID = MitMobileApplication.dbAdapter.db.insertWithOnConflict(Schema.Location.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (newID <= 0) {
                    throw new SQLException("Error");
                }
                break;
        }
        Timber.d("DB id= " + newID);
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Timber.d("Bulk Insert");

        int uriType = sURIMatcher.match(uri);
        Timber.d("Uri= " + uriType);

        long newID;
        switch (uriType) {
            case ROUTES:
                for (ContentValues v : values) {
                    newID = MitMobileApplication.dbAdapter.db.insertWithOnConflict(Schema.Route.TABLE_NAME, null, v, SQLiteDatabase.CONFLICT_FAIL);
                    Timber.d("Bulk Insert - ID = " + newID);
                    if (newID <= 0) {
                        throw new SQLException("Error");
                    }
                }
                break;
            case STOPS:
                for (ContentValues v : values) {
                    newID = MitMobileApplication.dbAdapter.db.insertWithOnConflict(Schema.Stop.TABLE_NAME, null, v, SQLiteDatabase.CONFLICT_FAIL);
                    Timber.d("Bulk Insert - ID = " + newID);
                    if (newID <= 0) {
                        throw new SQLException("Error");
                    }
                }
                break;

        }
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Timber.d("Update");

        int uriType = sURIMatcher.match(uri);
        Timber.d("Uri= " + uriType);

        int rows = 0;

        switch (uriType) {
            case ROUTES:
                rows = MitMobileApplication.dbAdapter.db.update(Schema.Route.TABLE_NAME, values, Schema.Route.ROUTE_ID + " = \'" + values.get(Schema.Route.ROUTE_ID) + "\'", null);
                break;
            case STOPS:
                rows = MitMobileApplication.dbAdapter.db.update(Schema.Stop.TABLE_NAME, values, Schema.Stop.STOP_ID + " = \'" + values.get(Schema.Stop.STOP_ID) + "\'", null);
                break;
            case PREDICTIONS:
                rows = MitMobileApplication.dbAdapter.db.update(Schema.Stop.TABLE_NAME, values, selection, null);
                break;
            case ROUTE_ID:
                rows = MitMobileApplication.dbAdapter.db.update(Schema.Route.TABLE_NAME, values, selection, null);
                break;
        }

        return rows;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        Timber.d("Batch Update");

        for (ContentProviderOperation operation : operations) {
            operation.apply(this, null, 1);
        }

        return new ContentProviderResult[operations.size()];
    }

    public String buildQueryString(String selection) {
        return "SELECT route_stops._id AS rs_id, stops._id AS s_id, routes._id, routes.route_id, routes.route_url, routes.route_title, routes.agency, routes.scheduled, routes.predictable, routes.route_description, routes.predictions_url, routes.vehicles_url, routes.path_id, stops.stop_id, stops.stop_url, stops.stop_title, stops.stop_lat, stops.stop_lon, stops.stop_number, stops.distance, stops.predictions " +
                "FROM routes " +
                "INNER JOIN route_stops ON routes.route_id = route_stops.route_id " +
                "JOIN stops ON route_stops.stop_id = stops.stop_id " +
                "WHERE " + selection +
                "ORDER BY routes._id ASC, rs_id ASC";
    }

    public String buildStopQueryString(String selection) {
        return "SELECT route_stops._id AS rs_id, stops._id AS s_id, routes._id, routes.route_id, routes.route_url, routes.route_title, routes.agency, routes.scheduled, routes.predictable, routes.route_description, routes.predictions_url, routes.vehicles_url, routes.path_id, stops.stop_id, stops.stop_url, stops.stop_title, stops.stop_lat, stops.stop_lon, stops.stop_number, stops.distance, stops.predictions " +
                "FROM stops " +
                "INNER JOIN route_stops ON stops.stop_id = route_stops.stop_id " +
                "JOIN routes ON route_stops.route_id = routes.route_id " +
                "WHERE " + selection +
                "ORDER BY routes._id ASC, rs_id ASC";
    }
}
