package edu.mit.mitmobile2.mobius;

import android.content.Context;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.maps.MapItem;

/**
 * Created by sseligma on 1/23/15.
 */
public class ResourceItem extends MapItem implements Parcelable {

    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";

    private Context mContext;
    private ViewGroup parent;

    private int number; // Number to display on lists and maps
    private int index;
    private int mapItemIndex; // index in the mapItems array
    private String category;
    private String type;
    private String name = "";
    private String room;
    private String building = "";
    private Boolean buildingHeader = false;
    private double latitude;
    private double longitude;
    private String status;
    private ArrayList<ResourceAttribute> attributes;


    public int getMapItemType() {
        return MapItem.MARKERTYPE;
    }

    @Override
    public MarkerOptions getMarkerOptions() {
        MarkerOptions m = new MarkerOptions();
        m.title(this.name);
        JSONObject data = new JSONObject();
        try {
            data.put("mapItemIndex", this.getMapItemIndex());
            data.put("name", this.getName());
            data.put("room", this.getRoom());
            data.put("status", this.getStatus());
        } catch (JSONException e) {
            Log.d("ZZZ", e.getMessage());
        }
        m.snippet(data.toString());
        LatLng position = new LatLng(this.latitude, this.longitude);
        m.position(position);
        return m;
    } //market


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Boolean getBuildingHeader() {
        return buildingHeader;
    }

    public void setBuildingHeader(Boolean buildingHeader) {
        this.buildingHeader = buildingHeader;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public ArrayList<ResourceAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<ResourceAttribute> attributes) {
        this.attributes = attributes;
    }

    public ResourceItem() {

    }

    public ResourceItem(Context mContext, ViewGroup parent) {
        this.mContext = mContext;
        this.parent = parent;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.number);
        dest.writeInt(this.index);
        dest.writeString(this.category);
        dest.writeString(this.type);
        dest.writeString(this.name);
        dest.writeString(this.room);
        dest.writeString(this.building);
        dest.writeByte((byte) (this.buildingHeader ? 1 : 0));
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.status);
        dest.writeList(this.attributes);
    }

    private ResourceItem(Parcel in) {
        this.number = in.readInt();
        this.index = in.readInt();
        this.category = in.readString();
        this.type = in.readString();
        this.name = in.readString();
        this.room = in.readString();
        this.building = in.readString();
        this.buildingHeader = in.readByte() != 0;
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.status = in.readString();
        this.attributes = (ArrayList<ResourceAttribute>) in.readArrayList(ResourceAttribute.class.getClassLoader());
    }

    public static final Parcelable.Creator<ResourceItem> CREATOR = new Parcelable.Creator<ResourceItem>() {
        public ResourceItem createFromParcel(Parcel source) {
            return new ResourceItem(source);
        }

        public ResourceItem[] newArray(int size) {
            return new ResourceItem[size];
        }
    };

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
}