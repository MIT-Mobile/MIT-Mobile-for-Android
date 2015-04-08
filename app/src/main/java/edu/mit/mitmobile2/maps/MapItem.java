package edu.mit.mitmobile2.maps;


import android.os.Parcelable;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import edu.mit.mitmobile2.DatabaseObject;
import edu.mit.mitmobile2.R;

public abstract class MapItem extends DatabaseObject implements Parcelable {

    public static final int MARKERTYPE = 1;
    public static final int POLYLINETYPE = 2;
    public static final int POLYGONTYPE = 3;

    public int mapItemType;
    private String markerText;

    protected boolean isDynamic = false;
    protected boolean isVehicle = false;

    private int iconResource = R.drawable.usermarker; //default
    public int getMapItemType() {
        return mapItemType;
    }

    public void setMapItemType(int mapItemType) {
        this.mapItemType = mapItemType;
    }

    public MarkerOptions getMarkerOptions() {
        return null;
    } //market

    public String getMarkerText() {
        return markerText;
    }

    public void setMarkerText(String markerText) {
        this.markerText = markerText;
    }

    public PolylineOptions getPolylineOptions() {
        return null;
    } //polyline

    public PolygonOptions getPolygonOptions() {
        return null;
    } //polygon


    public boolean isDynamic() {
        return isDynamic;
    }

    public void setDynamic(boolean isDynamic) {
        this.isDynamic = isDynamic;
    }

    public boolean isVehicle() {
        return isVehicle;
    }

    public void setVehicle(boolean isVehicle) {
        this.isVehicle = isVehicle;
    }

    public int getIconResource() {
        return iconResource;
    }
}