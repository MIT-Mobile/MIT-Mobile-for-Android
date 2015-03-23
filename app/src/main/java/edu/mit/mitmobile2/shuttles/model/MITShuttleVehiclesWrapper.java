package edu.mit.mitmobile2.shuttles.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.shuttles.ShuttlesDatabaseHelper;

public class MITShuttleVehiclesWrapper extends DatabaseObject {

    @SerializedName("route_id")
    @Expose
    private String routeId;
    @SerializedName("route_url")
    @Expose
    private String routeUrl;
    @Expose
    private String agency;
    @Expose
    private Boolean scheduled;
    @Expose
    private Boolean predictable;
    @Expose
    private List<MITShuttleVehicle> vehicles = new ArrayList<MITShuttleVehicle>();


    public String getRouteId() {
        return routeId;
    }


    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }


    public String getRouteUrl() {
        return routeUrl;
    }


    public void setRouteUrl(String routeUrl) {
        this.routeUrl = routeUrl;
    }


    public String getAgency() {
        return agency;
    }


    public void setAgency(String agency) {
        this.agency = agency;
    }


    public Boolean getScheduled() {
        return scheduled;
    }


    public void setScheduled(Boolean scheduled) {
        this.scheduled = scheduled;
    }


    public Boolean getPredictable() {
        return predictable;
    }


    public void setPredictable(Boolean predictable) {
        this.predictable = predictable;
    }


    public List<MITShuttleVehicle> getVehicles() {
        return vehicles;
    }

    /**
     * @param vehicles The vehicles
     */
    public void setVehicles(List<MITShuttleVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    protected String getTableName() {
        return null;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {

    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        // I only care about the vehicle objs, don't need anything from
        ShuttlesDatabaseHelper.batchPersistVehicles(this.vehicles, this.routeId);
    }
}