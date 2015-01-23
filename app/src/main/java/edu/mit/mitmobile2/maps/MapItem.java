package edu.mit.mitmobile2.maps;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.Polygon;

public abstract class MapItem {

    private final int markType = 1;
    private final int polylineType = 2;
    private final int polygonType = 3;

    public abstract Marker getMarker();
    public abstract Polyline getPolyline();
    public abstract Polygon getPolygon();
}
