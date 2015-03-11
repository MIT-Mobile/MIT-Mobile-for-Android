package edu.mit.mitmobile2.shuttles.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.Schema;

public class MITShuttlePath extends DatabaseObject implements Parcelable {

    @Expose
    private List<Double> bbox = new ArrayList<>();
    @Expose
    private List<List<List<Double>>> segments = new ArrayList<List<List<Double>>>();


    public List<Double> getBbox() {
        return bbox;
    }


    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }


    public List<List<List<Double>>> getSegments() {
        return segments;
    }


    public void setSegments(List<List<List<Double>>> segments) {
        this.segments = segments;
    }

    public MITShuttlePath() {
    }

    @Override
    public int describeContents() {
        return 0;
    }


    //TODO: Need to parcel/unparcel the bbox and segments
    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeList(this.bbox);
//        dest.writeTypedList(this.segments);
    }

    private MITShuttlePath(Parcel p) {

    }

    public static final Parcelable.Creator<MITShuttlePath> CREATOR = new Parcelable.Creator<MITShuttlePath>() {
        public MITShuttlePath createFromParcel(Parcel source) {
            return new MITShuttlePath(source);
        }

        public MITShuttlePath[] newArray(int size) {
            return new MITShuttlePath[size];
        }
    };

    @Override
    protected String getTableName() {
        return Schema.Path.TABLE_NAME;
    }

    @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
        String segmentString = cursor.getString(cursor.getColumnIndex(Schema.Path.SEGMENTS));
        Gson gson = new Gson();
        Type nestedListType = new TypeToken<List<List<List<Double>>>>() {
        }.getType();
        List<List<List<Double>>> segments = gson.fromJson(segmentString, nestedListType);
        setSegments(segments);
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
        values.put(Schema.Path.SEGMENTS, this.getSegments().toString());
    }
}