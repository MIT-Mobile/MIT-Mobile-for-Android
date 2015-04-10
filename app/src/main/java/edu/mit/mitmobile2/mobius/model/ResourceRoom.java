package edu.mit.mitmobile2.mobius.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.maps.MapItem;

/**
 * Created by sseligma on 4/7/15.
 */
public class ResourceRoom  extends MapItem implements android.os.Parcelable {

    private int index;
    private int mapItemIndex; // index in the mapItems array
    private String room;
    private String roomset_id;
    private String roomset_name;
    private String room_label; // roomset + room
    private double latitude;
    private double longitude;
    private ArrayList<RoomsetHours> hours;
    private ArrayList<ResourceItem> resources;
    private boolean open;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getMapItemIndex() {
        return mapItemIndex;
    }

    public void setMapItemIndex(int mapItemIndex) {
        this.mapItemIndex = mapItemIndex;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getRoomset_id() {
        return roomset_id;
    }

    public void setRoomset_id(String roomset_id) {
        this.roomset_id = roomset_id;
    }

    public String getRoomset_name() {
        return roomset_name;
    }

    public void setRoomset_name(String roomset_name) {
        this.roomset_name = roomset_name;
    }

    public String getRoom_label() {
        return room_label;
    }

    public void setRoom_label(String room_label) {
        this.room_label = room_label;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<RoomsetHours> getHours() {
        return hours;
    }

    public void setHours(ArrayList<RoomsetHours> hours) {
        this.hours = hours;
    }

    public ArrayList<ResourceItem> getResources() {
        return resources;
    }

    public void setResources(ArrayList<ResourceItem> resources) {
        this.resources = resources;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public ResourceRoom() {
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

    }

    @Override
    public int getMapItemType() {
        return MapItem.MARKERTYPE;
    }

    @Override
    public String getMarkerText() {
        return null;
    }

    @Override
    public MarkerOptions getMarkerOptions() {
        // sanity check for latitude/longitude
        // return null if latitude and longitude = 0
        MarkerOptions m = new MarkerOptions();
        m.title(this.room);
        JSONObject data = new JSONObject();
        try {
            data.put("mapItemIndex", this.getMapItemIndex());
            data.put("room", this.getRoom());
        } catch (JSONException e) {
            Log.d("ZZZ", e.getMessage());
        }
        m.snippet(data.toString());
        LatLng position = new LatLng(this.latitude, this.longitude);
        m.position(position);
        return m;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
        dest.writeInt(this.mapItemIndex);
        dest.writeString(this.room);
        dest.writeString(this.roomset_id);
        dest.writeString(this.roomset_name);
        dest.writeString(this.room_label);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeSerializable(this.hours);
        dest.writeSerializable(this.resources);
        dest.writeByte(open ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mapItemType);
        dest.writeByte(isDynamic ? (byte) 1 : (byte) 0);
        dest.writeByte(isVehicle ? (byte) 1 : (byte) 0);
    }

    private ResourceRoom(Parcel in) {
        this.index = in.readInt();
        this.mapItemIndex = in.readInt();
        this.room = in.readString();
        this.roomset_id = in.readString();
        this.roomset_name = in.readString();
        this.room_label = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.hours = (ArrayList<RoomsetHours>) in.readSerializable();
        this.resources = (ArrayList<ResourceItem>) in.readSerializable();
        this.open = in.readByte() != 0;
        this.mapItemType = in.readInt();
        this.isDynamic = in.readByte() != 0;
        this.isVehicle = in.readByte() != 0;
    }

    public static final Creator<ResourceRoom> CREATOR = new Creator<ResourceRoom>() {
        public ResourceRoom createFromParcel(Parcel source) {
            return new ResourceRoom(source);
        }

        public ResourceRoom[] newArray(int size) {
            return new ResourceRoom[size];
        }
    };
}
