package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.Iterator;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MITMapView {

    private GoogleMap mMap;
    private MapItem  mItem;

    public void addMapItem(MapItem mItem) {
        if (mMap != null) {
            if (mItem != null && mItem.getMapItemType() == 1) {
                mMap.addMarker(mItem.getMarkerOptions());
            } else if (mItem != null && mItem.getMapItemType() == 2) {
                mMap.addPolyline(mItem.getPolylineOptions());
            } else if (mItem != null && mItem.getMapItemType() == 3) {
                mMap.addPolygon(mItem.getPolygonOptions());
            }
        }
    }

    public void addMapItemList(ArrayList<MapItem> mapItems) {
        if (mMap != null && mapItems != null && mapItems.size()>0) {
            Iterator<MapItem> iterator = mapItems.iterator();
            while (iterator.hasNext()) {
                MapItem item = (MapItem) iterator.next();
                addMapItem(item);
            }
        }
    }

}
