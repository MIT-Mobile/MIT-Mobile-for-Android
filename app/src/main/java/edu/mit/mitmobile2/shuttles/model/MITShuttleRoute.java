package edu.mit.mitmobile2.shuttles.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.shuttles.ShuttlesDatabaseHelper;


public class MITShuttleRoute extends DatabaseObject implements Parcelable {

    @Expose
    private String id;
    @Expose
    private String url;
    @Expose
    private String title;
    @Expose
    private String agency;
    @Expose
    private Boolean scheduled;
    @Expose
    private Boolean predictable;
    @Expose
    private String description;
    @SerializedName("predictions_url")
    @Expose
    private String predictionsUrl;
    @SerializedName("vehicles_url")
    @Expose
    private String vehiclesUrl;
    @Expose
    private MITShuttlePath path;
    @Expose
    private List<MITShuttleStopWrapper> stops = new ArrayList<>();
    @Expose
    private List<MITShuttleVehicle> vehicles = new ArrayList<>();

    public MITShuttleRoute() {
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }


    public Boolean isScheduled() {
        return scheduled;
    }


    public void setScheduled(Boolean scheduled) {
        this.scheduled = scheduled;
    }


    public Boolean isPredictable() {
        return predictable;
    }


    public void setPredictable(Boolean predictable) {
        this.predictable = predictable;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getPredictionsUrl() {
        return predictionsUrl;
    }


    public void setPredictionsUrl(String predictionsUrl) {
        this.predictionsUrl = predictionsUrl;
    }


    public String getVehiclesUrl() {
        return vehiclesUrl;
    }


    public void setVehiclesUrl(String vehiclesUrl) {
        this.vehiclesUrl = vehiclesUrl;
    }


    public MITShuttlePath getPath() {
        return path;
    }


    public void setPath(MITShuttlePath path) {
        this.path = path;
    }


    public List<MITShuttleStopWrapper> getStops() {
        return stops;
    }

    public void setStops(List<MITShuttleStopWrapper> stops) {
        this.stops = stops;
    }

    public List<MITShuttleVehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<MITShuttleVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(agency);
        dest.writeInt(this.scheduled ? 1 : 0);
        dest.writeInt(this.predictable ? 1 : 0);
        dest.writeString(description);
        dest.writeString(predictionsUrl);
        dest.writeString(vehiclesUrl);
        dest.writeParcelable(path, 0);
        dest.writeTypedList(stops);
    }

    private MITShuttleRoute(Parcel p) {
        this.id = p.readString();
        this.url = p.readString();
        this.title = p.readString();
        this.agency = p.readString();
        this.scheduled = p.readInt() == 1;
        this.predictable = p.readInt() == 1;
        this.description = p.readString();
        this.predictionsUrl = p.readString();
        this.vehiclesUrl = p.readString();
        this.path = p.readParcelable(MITShuttlePath.class.getClassLoader());
        p.readTypedList(this.stops, MITShuttleStopWrapper.CREATOR);
//        this.stops = p.readArrayList(MITShuttleStopWrapper.class.getClassLoader());
    }

    public static final Parcelable.Creator<MITShuttleRoute> CREATOR = new Parcelable.Creator<MITShuttleRoute>() {
        public MITShuttleRoute createFromParcel(Parcel source) {
            return new MITShuttleRoute(source);
        }

        public MITShuttleRoute[] newArray(int size) {
            return new MITShuttleRoute[size];
        }
    };

    @Override
    protected String getTableName() {
        return Schema.Route.TABLE_NAME;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        long pathId = cursor.getLong(cursor.getColumnIndex(Schema.Route.MIT_PATH_ID));
        setPath(ShuttlesDatabaseHelper.getPath(pathId));
        setId(cursor.getString(cursor.getColumnIndex(Schema.Route.ROUTE_ID)));
        setUrl(cursor.getString(cursor.getColumnIndex(Schema.Route.ROUTE_URL)));
        setAgency(cursor.getString(cursor.getColumnIndex(Schema.Route.AGENCY)));
        setDescription(cursor.getString(cursor.getColumnIndex(Schema.Route.ROUTE_DESCRIPTION)));
        setPredictable(cursor.getInt(cursor.getColumnIndex(Schema.Route.PREDICTABLE)) == 1);
        setScheduled(cursor.getInt(cursor.getColumnIndex(Schema.Route.SCHEDULED)) == 1);
        setTitle(cursor.getString(cursor.getColumnIndex(Schema.Route.ROUTE_TITLE)));
        setPredictionsUrl(cursor.getString(cursor.getColumnIndex(Schema.Route.PREDICTIONS_URL)));
        setVehiclesUrl(cursor.getString(cursor.getColumnIndex(Schema.Route.PREDICTIONS_URL)));
        setVehicles(ShuttlesDatabaseHelper.getVehicles(this.id));

        buildSubclassFromCursor(cursor, dbAdapter, "");
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter, String prefix) {
        long id = getDatabaseId();
        this.stops = new ArrayList<>();

        while (cursor.getLong(cursor.getColumnIndex(Schema.Route.ID_COL)) == id) {
            MITShuttleStopWrapper stopWrapper = new MITShuttleStopWrapper();
            stopWrapper.buildSubclassFromCursor(cursor, dbAdapter);
            this.stops.add(stopWrapper);
            boolean itemsRemaining = cursor.moveToNext();
            if (!itemsRemaining) {
                break;
            }
        }
        // Move back 1 since we looked ahead to the next ID
        cursor.moveToPrevious();
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        values.put(Schema.Route.ROUTE_ID, this.id);
        values.put(Schema.Route.AGENCY, this.agency);
        values.put(Schema.Route.PREDICTABLE, this.predictable ? 1 : 0);
        values.put(Schema.Route.PREDICTIONS_URL, this.predictionsUrl);
        values.put(Schema.Route.SCHEDULED, this.scheduled ? 1 : 0);
        values.put(Schema.Route.ROUTE_DESCRIPTION, this.description);
        values.put(Schema.Route.ROUTE_TITLE, this.title);
        values.put(Schema.Route.VEHICLES_URL, this.vehiclesUrl);
        values.put(Schema.Route.ROUTE_URL, this.url);

        ShuttlesDatabaseHelper.batchPersistStops(this.stops, this.id, this.predictable);
        ShuttlesDatabaseHelper.batchPersistVehicles(this.vehicles, this.id);

        //TODO: Remove this IF condition when SyncAdapter added; paths will just get cleared each time
        if (!dbAdapter.exists(Schema.Path.TABLE_NAME, Schema.Path.ALL_COLUMNS)) {
            dbAdapter.acquire(this.path);
            long pathId = path.persistToDatabase();
            values.put(Schema.Route.MIT_PATH_ID, pathId);
        }
    }
}