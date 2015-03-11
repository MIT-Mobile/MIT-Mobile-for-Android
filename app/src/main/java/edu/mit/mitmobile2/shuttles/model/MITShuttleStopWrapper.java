package edu.mit.mitmobile2.shuttles.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.maps.MapItem;

public class MITShuttleStopWrapper extends MapItem implements Parcelable {

    @Expose
    private String id;
    @Expose
    private String url;
    @SerializedName("route_id")
    @Expose
    private String routeId;
    @SerializedName("route_url")
    @Expose
    private String routeUrl;
    @Expose
    private String title;
    @SerializedName("stop_number")
    @Expose
    private String stopNumber;
    @Expose
    private Double lat;
    @Expose
    private Double lon;
    @Expose
    private List<MITShuttlePrediction> predictions = new ArrayList<MITShuttlePrediction>();
    @SerializedName("predictions_url")
    @Expose
    private String predictionsUrl;
    private float distance;

    public MITShuttleStopWrapper() {
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


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getStopNumber() {
        return stopNumber;
    }


    public void setStopNumber(String stopNumber) {
        this.stopNumber = stopNumber;
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

    public List<MITShuttlePrediction> getPredictions() {
        return predictions;
    }


    public void setPredictions(List<MITShuttlePrediction> predictions) {
        this.predictions = predictions;
    }


    public String getPredictionsUrl() {
        return predictionsUrl;
    }


    public void setPredictionsUrl(String predictionsUrl) {
        this.predictionsUrl = predictionsUrl;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public int getMapItemType() {
        return MARKERTYPE;
    }

    @Override
    public MarkerOptions getMarkerOptions() {
        MarkerOptions m = new MarkerOptions();
        m.title(this.title);
        LatLng position = new LatLng(this.lat, this.lon);
        m.position(position);
        return m;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.url);
        dest.writeString(this.routeId);
        dest.writeString(this.routeUrl);
        dest.writeString(this.title);
        dest.writeString(this.stopNumber);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
        dest.writeList(this.predictions);
        dest.writeString(this.predictionsUrl);
    }

    private MITShuttleStopWrapper(Parcel p) {
        this.id = p.readString();
        this.url = p.readString();
        this.routeId = p.readString();
        this.routeUrl = p.readString();
        this.title = p.readString();
        this.stopNumber = p.readString();
        this.lat = p.readDouble();
        this.lon = p.readDouble();
        this.predictions = (List<MITShuttlePrediction>) p.readArrayList(MITShuttlePrediction.class.getClassLoader());
        this.predictionsUrl = p.readString();
    }

    public static final Parcelable.Creator<MITShuttleStopWrapper> CREATOR = new Parcelable.Creator<MITShuttleStopWrapper>() {
        public MITShuttleStopWrapper createFromParcel(Parcel source) {
            return new MITShuttleStopWrapper(source);
        }

        public MITShuttleStopWrapper[] newArray(int size) {
            return new MITShuttleStopWrapper[size];
        }
    };

    @Override
    protected String getTableName() {
        return Schema.Stop.TABLE_NAME;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        //TODO: Pull predictions from their table

        setId(cursor.getString(cursor.getColumnIndex(Schema.Stop.STOP_ID)));
        setUrl(cursor.getString(cursor.getColumnIndex(Schema.Stop.STOP_URL)));
        setRouteId(cursor.getString(cursor.getColumnIndex(Schema.Stop.ROUTE_ID)));
        setRouteUrl(cursor.getString(cursor.getColumnIndex(Schema.Stop.ROUTE_URL)));
        setTitle(cursor.getString(cursor.getColumnIndex(Schema.Stop.STOP_TITLE)));
        setStopNumber(cursor.getString(cursor.getColumnIndex(Schema.Stop.STOP_NUMBER)));
        setLat(cursor.getDouble(cursor.getColumnIndex(Schema.Stop.STOP_LAT)));
        setLon(cursor.getDouble(cursor.getColumnIndex(Schema.Stop.STOP_LON)));
        setPredictionsUrl(cursor.getString(cursor.getColumnIndex(Schema.Stop.PREDICTIONS_URL)));
        setDistance(cursor.getFloat(cursor.getColumnIndex(Schema.Stop.DISTANCE)));
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        //TODO: Store Predictions in their own table, also store ids in relational stop<->pred table
        if (predictions != null) {
            dbAdapter.batchPersistPredictions(this.predictions, this.id);
        }

        values.put(Schema.Stop.STOP_ID, this.id);
        values.put(Schema.Stop.STOP_URL, this.url);
        values.put(Schema.Stop.ROUTE_ID, this.routeId);
        values.put(Schema.Stop.ROUTE_URL, this.routeUrl);
        values.put(Schema.Stop.STOP_TITLE, this.title);
        values.put(Schema.Stop.STOP_NUMBER, this.stopNumber);
        values.put(Schema.Stop.STOP_LAT, this.lat);
        values.put(Schema.Stop.STOP_LON, this.lon);
        values.put(Schema.Stop.PREDICTIONS_URL, this.predictionsUrl);
        values.put(Schema.Stop.DISTANCE, this.distance);
    }
}