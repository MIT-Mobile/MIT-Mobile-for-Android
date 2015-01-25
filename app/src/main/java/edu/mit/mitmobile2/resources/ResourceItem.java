package edu.mit.mitmobile2.resources;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.maps.MapItem;

/**
 * Created by sseligma on 1/23/15.
 */
public class ResourceItem extends MapItem implements Parcelable{

    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";

    private int number; // Number to display on lists and maps
    private int index;
    private String category;
    private String type;
    private String name;
    private String room;
    private String status;
    private ArrayList<ResourceAttribute> attributes;

    public int getMapItemType() {
        return MapItem.MARKERTYPE;
    }

    @Override
    public MarkerOptions getMarkerOptions() {
        return null;
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

    protected ResourceItem(Parcel in) {
        number = in.readInt();
        index = in.readInt();
        category = in.readString();
        type = in.readString();
        name = in.readString();
        room = in.readString();
        status = in.readString();
        if (in.readByte() == 0x01) {
            attributes = new ArrayList<ResourceAttribute>();
            in.readList(attributes, ResourceAttribute.class.getClassLoader());
        } else {
            attributes = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
        dest.writeInt(index);
        dest.writeString(category);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeString(room);
        dest.writeString(status);
        if (attributes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(attributes);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ResourceItem> CREATOR = new Parcelable.Creator<ResourceItem>() {
        @Override
        public ResourceItem createFromParcel(Parcel in) {
            return new ResourceItem(in);
        }

        @Override
        public ResourceItem[] newArray(int size) {
            return new ResourceItem[size];
        }
    };
}
