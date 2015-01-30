package edu.mit.mitmobile2.maps;


import android.os.Parcelable;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.LatLng;

public abstract class MapItem implements Parcelable{

    public static final int MARKERTYPE = 1;
    public static final int POLYLINETYPE = 2;
    public static final int POLYGONTYPE = 3;

    public int mapItemType = MapItem.MARKERTYPE;

    public int getMapItemType() {
        return mapItemType;
    }

    public void setMapItemType(int mapItemType) {
        this.mapItemType = mapItemType;
    }

    public MarkerOptions getMarkerOptions() {
        return null;
    } //market
    public PolylineOptions getPolylineOptions() {
        return null;
    } //polyline
    public PolygonOptions getPolygonOptions() {
        return null;
    }; //polygan


}