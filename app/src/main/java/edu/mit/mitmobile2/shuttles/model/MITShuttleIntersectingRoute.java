package edu.mit.mitmobile2.shuttles.model;

import android.content.ContentValues;
import android.database.Cursor;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.Schema;

/**
 * Created by philipcorriveau on 3/27/15.
 */
public class MITShuttleIntersectingRoute extends MITShuttleRoute {

    /**
     * This model is used for intersecing routes since they require far less data to be loaded
     */

    public MITShuttleIntersectingRoute() {
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {

    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        setId(cursor.getString(cursor.getColumnIndex(Schema.Route.ROUTE_ID)));
        setAgency(cursor.getString(cursor.getColumnIndex(Schema.Route.AGENCY)));
        setPredictable(cursor.getInt(cursor.getColumnIndex(Schema.Route.PREDICTABLE)) == 1);
        setScheduled(cursor.getInt(cursor.getColumnIndex(Schema.Route.SCHEDULED)) == 1);
        setTitle(cursor.getString(cursor.getColumnIndex(Schema.Route.ROUTE_TITLE)));
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter, String prefix) {

    }
}
