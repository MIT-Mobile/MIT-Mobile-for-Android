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

    @Override
    protected String getTableName() {
        return Schema.Vehicle.TABLE_NAME;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {

    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {

    }
}
