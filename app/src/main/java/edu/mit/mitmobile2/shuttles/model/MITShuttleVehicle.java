package edu.mit.mitmobile2.shuttles.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.Schema;

public class MITShuttleVehicle extends DatabaseObject {

    @Expose
    private String id;
    @Expose
    private Double lat;
    @Expose
    private Double lon;
    @Expose
    private Integer heading;
    @SerializedName("speed_kph")
    @Expose
    private Integer speedKph;
    @SerializedName("seconds_since_report")
    @Expose
    private Integer secondsSinceReport;

    private String routeId;

    public MITShuttleVehicle() {
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public Double getLat() {
        return lat;
    }


    public void setLat(Double lat) {
        this.lat = lat;
    }


    public Double getLon() {
        return lon;
    }


    public void setLon(Double lon) {
        this.lon = lon;
    }


    public Integer getHeading() {
        return heading;
    }


    public void setHeading(Integer heading) {
        this.heading = heading;
    }


    public Integer getSpeedKph() {
        return speedKph;
    }


    public void setSpeedKph(Integer speedKph) {
        this.speedKph = speedKph;
    }


    public Integer getSecondsSinceReport() {
        return secondsSinceReport;
    }


    public void setSecondsSinceReport(Integer secondsSinceReport) {
        this.secondsSinceReport = secondsSinceReport;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    @Override
    protected String getTableName() {
        return Schema.Vehicle.TABLE_NAME;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        setId(cursor.getString(cursor.getColumnIndex(Schema.Vehicle.VEHICLE_ID)));
        setLat(cursor.getDouble(cursor.getColumnIndex(Schema.Vehicle.VEHICLE_LAT)));
        setLon(cursor.getDouble(cursor.getColumnIndex(Schema.Vehicle.VEHICLE_LON)));
        setHeading(cursor.getInt(cursor.getColumnIndex(Schema.Vehicle.HEADING)));
        setSpeedKph(cursor.getInt(cursor.getColumnIndex(Schema.Vehicle.SPEED)));
        setSecondsSinceReport(cursor.getInt(cursor.getColumnIndex(Schema.Vehicle.SECS_SINCE_REPORT)));
        setRouteId(cursor.getString(cursor.getColumnIndex(Schema.Vehicle.ROUTE_ID)));
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        values.put(Schema.Vehicle.VEHICLE_ID, this.id);
        values.put(Schema.Vehicle.VEHICLE_LAT, this.lat);
        values.put(Schema.Vehicle.VEHICLE_LON, this.lon);
        values.put(Schema.Vehicle.HEADING, this.heading);
        values.put(Schema.Vehicle.SPEED, this.speedKph);
        values.put(Schema.Vehicle.SECS_SINCE_REPORT, this.secondsSinceReport);
        values.put(Schema.Vehicle.ROUTE_ID, this.routeId);
    }
}
