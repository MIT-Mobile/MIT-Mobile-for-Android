package edu.mit.mitmobile2.shuttles.model;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.Schema;

public class MitMiniShuttleRoute extends MITShuttleRoute {

    /**
     * This drastically reduces the load time for the main screen by not loading unneeded stops
     * (For ex: ~5000ms before, ~200ms after
     */

    public MitMiniShuttleRoute() {
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {

    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        setId(cursor.getString(cursor.getColumnIndex(Schema.Route.ROUTE_ID)));
        setAgency(cursor.getString(cursor.getColumnIndex(Schema.Route.AGENCY)));
        setDescription(cursor.getString(cursor.getColumnIndex(Schema.Route.ROUTE_DESCRIPTION)));
        setPredictable(cursor.getInt(cursor.getColumnIndex(Schema.Route.PREDICTABLE)) == 1);
        setScheduled(cursor.getInt(cursor.getColumnIndex(Schema.Route.SCHEDULED)) == 1);
        setTitle(cursor.getString(cursor.getColumnIndex(Schema.Route.ROUTE_TITLE)));

        buildSubclassFromCursor(cursor, dbAdapter, "");
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter, String prefix) {
        long id = getDatabaseId();
        List<MITShuttleStop> stops = new ArrayList<>();

        int count = 0;
        while (cursor.getLong(cursor.getColumnIndex(Schema.Route.ID_COL)) == id) {
            if (count < 2 && isPredictable()) {
                MITShuttleStop stopWrapper = new MITShuttleStop();
                stopWrapper.buildSubclassFromCursor(cursor, dbAdapter);
                stops.add(stopWrapper);
            }
            boolean itemsRemaining = cursor.moveToNext();
            if (!itemsRemaining) {
                break;
            }
            count++;
        }

        setStops(stops);
        // Move back 1 since we looked ahead to the next ID
        cursor.moveToPrevious();
    }
}
