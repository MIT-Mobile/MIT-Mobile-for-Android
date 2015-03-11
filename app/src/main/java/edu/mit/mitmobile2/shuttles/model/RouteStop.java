package edu.mit.mitmobile2.shuttles.model;


import android.content.ContentValues;
import android.database.Cursor;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.Schema;

public class RouteStop extends DatabaseObject {

    String routeId;
    String stopId;

    public RouteStop() {
    }

    public RouteStop(String routeId, String stopId) {
        this.routeId = routeId;
        this.stopId = stopId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    @Override
    protected String getTableName() {
        return Schema.RouteStops.TABLE_NAME;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        setRouteId(cursor.getString(cursor.getColumnIndex(Schema.RouteStops.ROUTE_ID)));
        setStopId(cursor.getString(cursor.getColumnIndex(Schema.RouteStops.STOP_ID)));
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        values.put(Schema.RouteStops.ROUTE_ID, this.routeId);
        values.put(Schema.RouteStops.STOP_ID, this.stopId);
    }
}
