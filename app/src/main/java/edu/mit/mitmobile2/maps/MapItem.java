package edu.mit.mitmobile2.maps;


import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.LatLng;

public abstract class MapItem {

    private final int markType = 1;
    private final int polylineType = 2;
    private final int polygonType = 3;

    public abstract int getMapItemType();

    public abstract MarkerOptions getMarkerOptions(); //market
    public abstract PolylineOptions getPolylineOptions(); //polyline
    public abstract PolygonOptions getPolygonOptions(); //polygan

}