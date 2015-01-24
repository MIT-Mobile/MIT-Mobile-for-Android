package edu.mit.mitmobile2.resources;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.mit.mitmobile2.maps.MapItem;

/**
 * Created by sseligma on 1/23/15.
 */
public class ResourceItem extends MapItem {

    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";

    private int number; // Number to display on lists and maps
    private int index;
    private String category;
    private String type;
    private String name;
    private String room;
    private String status;

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

}
