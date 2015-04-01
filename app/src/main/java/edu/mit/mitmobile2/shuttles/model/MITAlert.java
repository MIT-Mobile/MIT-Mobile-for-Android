package edu.mit.mitmobile2.shuttles.model;

import android.content.ContentValues;
import android.database.Cursor;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.Schema;

public class MITAlert extends DatabaseObject {

    private String routeId;
    private String stopId;
    private String vehicleId;
    private int timestamp;

    public MITAlert() {
    }

    public MITAlert(String routeId, String stopId, String vehicleId, int timestamp) {
        this.routeId = routeId;
        this.stopId = stopId;
        this.vehicleId = vehicleId;
        this.timestamp = timestamp;
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

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    protected String getTableName() {
        return Schema.Alerts.TABLE_NAME;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        setRouteId(cursor.getString(cursor.getColumnIndex(Schema.Alerts.ROUTE_ID)));
        setStopId(cursor.getString(cursor.getColumnIndex(Schema.Alerts.STOP_ID)));
        setVehicleId(cursor.getString(cursor.getColumnIndex(Schema.Alerts.VEHICLE_ID)));
        setTimestamp(cursor.getInt(cursor.getColumnIndex(Schema.Alerts.TIMESTAMP)));
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        values.put(Schema.Alerts.ROUTE_ID, this.routeId);
        values.put(Schema.Alerts.STOP_ID, this.stopId);
        values.put(Schema.Alerts.VEHICLE_ID, this.vehicleId);
        values.put(Schema.Alerts.TIMESTAMP, this.timestamp);
    }
}
