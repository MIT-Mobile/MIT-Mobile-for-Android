package edu.mit.mitmobile2.shuttles.model;

import android.content.ContentValues;
import android.database.Cursor;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.Schema;

public class StopPrediction extends DatabaseObject {

    String stopId;
    String vehicleId;

    public StopPrediction() {
    }

    public StopPrediction(String stopId, String vehicleId) {
        this.stopId = stopId;
        this.vehicleId = vehicleId;
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

    @Override
    protected String getTableName() {
        return Schema.StopPredictions.TABLE_NAME;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        setVehicleId(cursor.getString(cursor.getColumnIndex(Schema.StopPredictions.PREDICTION_ID)));
        setStopId(cursor.getString(cursor.getColumnIndex(Schema.StopPredictions.STOP_ID)));
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        values.put(Schema.StopPredictions.PREDICTION_ID, this.vehicleId);
        values.put(Schema.StopPredictions.STOP_ID, this.stopId);
    }
}
