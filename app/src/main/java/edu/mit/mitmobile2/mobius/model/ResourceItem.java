package edu.mit.mitmobile2.mobius.model;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.maps.MapItem;
import timber.log.Timber;

/**
 * Created by sseligma on 1/23/15.
 */
public class ResourceItem extends MapItem implements Parcelable {

    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";

    private Context mContext;
    private ViewGroup parent;

    private String _id;
    private int number; // Number to display on lists and maps
    private int index;
    private int mapItemIndex; // index in the mapItems array
    private String _category;
    private String category;
    private String _type;
    private String type;
    private String _template;
    private String name = "";
    private String room;
    private String roomset_id;
    private String roomset_name;
    private String dlc_id;
    private String dlc_name;
    private String building = "";
    private Boolean buildingHeader = false;
    private double latitude;
    private double longitude;
    private String status;
    private ArrayList<RoomsetHours> hours;
    private ArrayList<ResourceAttribute> attributes;
    private String[] images;


    public int getMapItemType() {
        return 0;
    }

    @Override
    public MarkerOptions getMarkerOptions() {
        return null;
    } //market


    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public ViewGroup getParent() {
        return parent;
    }

    public void setParent(ViewGroup parent) {
        this.parent = parent;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

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

    public String get_category() {
        return _category;
    }

    public void set_category(String _category) {
        this._category = _category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String get_template() {
        return _template;
    }

    public void set_template(String _template) {
        this._template = _template;
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

    public String getDlc_id() {
        return dlc_id;
    }

    public void setDlc_id(String dlc_id) {
        this.dlc_id = dlc_id;
    }

    public String getDlc_name() {
        return dlc_name;
    }

    public void setDlc_name(String dlc_name) {
        this.dlc_name = dlc_name;
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

    public ArrayList<RoomsetHours> getHours() {
        return hours;
    }

    public void setHours(ArrayList<RoomsetHours> hours) {
        this.hours = hours;
    }

    public ArrayList<ResourceAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<ResourceAttribute> attributes) {
        this.attributes = attributes;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }


    public ResourceItem(Context mContext, ViewGroup parent) {
        this.mContext = mContext;
        this.parent = parent;
    }



   @Override
    protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {

    }

    @Override
    protected String getTableName() {
        return null;
    }

    @Override
    public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {

    }

    public ResourceItem(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeInt(this.number);
        dest.writeInt(this.index);
        dest.writeInt(this.mapItemIndex);
        dest.writeString(this._category);
        dest.writeString(this.category);
        dest.writeString(this._type);
        dest.writeString(this.type);
        dest.writeString(this._template);
        dest.writeString(this.name);
        dest.writeString(this.room);
        dest.writeString(this.roomset_id);
        dest.writeString(this.roomset_name);
        dest.writeString(this.dlc_id);
        dest.writeString(this.dlc_name);
        dest.writeString(this.building);
        dest.writeValue(this.buildingHeader);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.status);
        dest.writeTypedList(this.hours);
        dest.writeTypedList(this.attributes);
        dest.writeStringArray(this.images);
        dest.writeInt(this.mapItemType);
        dest.writeByte(isDynamic ? (byte) 1 : (byte) 0);
        dest.writeByte(isVehicle ? (byte) 1 : (byte) 0);
    }

    private ResourceItem(Parcel in) {
        this._id = in.readString();
        this.number = in.readInt();
        this.index = in.readInt();
        this.mapItemIndex = in.readInt();
        this._category = in.readString();
        this.category = in.readString();
        this._type = in.readString();
        this.type = in.readString();
        this._template = in.readString();
        this.name = in.readString();
        this.room = in.readString();
        this.roomset_id = in.readString();
        this.roomset_name = in.readString();
        this.dlc_id = in.readString();
        this.dlc_name = in.readString();
        this.building = in.readString();
        this.buildingHeader = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.status = in.readString();
        this.hours = new ArrayList<RoomsetHours>();
        in.readTypedList(hours,RoomsetHours.CREATOR);
        this.attributes = new ArrayList<ResourceAttribute>();
        in.readTypedList(attributes,ResourceAttribute.CREATOR);
        this.images = in.createStringArray();
        this.mapItemType = in.readInt();
        this.isDynamic = in.readByte() != 0;
        this.isVehicle = in.readByte() != 0;
    }

    public static final Creator<ResourceItem> CREATOR = new Creator<ResourceItem>() {
        public ResourceItem createFromParcel(Parcel source) {
            return new ResourceItem(source);
        }

        public ResourceItem[] newArray(int size) {
            return new ResourceItem[size];
        }
    };
}

