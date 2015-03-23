package edu.mit.mitmobile2.shuttles.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.Schema;

public class MITShuttlePredictionWrapper extends DatabaseObject {

    @SerializedName("route_id")
    @Expose
    private String routeId;
    @SerializedName("route_url")
    @Expose
    private String routeUrl;
    @SerializedName("route_title")
    @Expose
    private String routeTitle;
    @SerializedName("stop_id")
    @Expose
    private String stopId;
    @SerializedName("stop_url")
    @Expose
    private String stopUrl;
    @SerializedName("stop_title")
    @Expose
    private String stopTitle;
    @Expose
    private List<MITShuttlePrediction> predictions = new ArrayList<>();
    @Expose
    private boolean predictable;
    @Expose
    private boolean scheduled;


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


    public String getRouteTitle() {
        return routeTitle;
    }


    public void setRouteTitle(String routeTitle) {
        this.routeTitle = routeTitle;
    }


    public String getStopId() {
        return stopId;
    }


    public void setStopId(String stopId) {
        this.stopId = stopId;
    }


    public String getStopUrl() {
        return stopUrl;
    }


    public void setStopUrl(String stopUrl) {
        this.stopUrl = stopUrl;
    }


    public String getStopTitle() {
        return stopTitle;
    }


    public void setStopTitle(String stopTitle) {
        this.stopTitle = stopTitle;
    }


    public List<MITShuttlePrediction> getPredictions() {
        return predictions;
    }


    public void setPredictions(List<MITShuttlePrediction> predictions) {
        this.predictions = predictions;
    }

    public boolean isPredictable() {
        return predictable;
    }

    public void setPredictable(boolean predictable) {
        this.predictable = predictable;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
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
        values.put(Schema.Route.ROUTE_ID, this.routeId);
        values.put(Schema.Stop.STOP_ID, this.stopId);
        values.put(Schema.Stop.PREDICTIONS, this.predictions.toString());
        values.put(Schema.Route.PREDICTABLE, this.predictable);
    }
}