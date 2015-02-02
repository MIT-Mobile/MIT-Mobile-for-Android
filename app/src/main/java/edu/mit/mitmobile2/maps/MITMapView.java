package edu.mit.mitmobile2.maps;

import android.app.FragmentManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.mit.mitmobile2.R;

public class MITMapView {

    private GoogleMap mMap;
    private MapItem  mItem;
    public static String MAP_ITEMS = "MAP_ITEMS";

    //set initial latlng for zoom in MIT area
    final LatLng initialLatLng = new LatLng(42.359858, -71.09913);
    final int initialZoom = 14;
    private Context mContext;

    public MITMapView(Context mContext, FragmentManager fm, int resourceId) {
        this.mContext = mContext;
        mMap = ((MapFragment) fm.findFragmentById(resourceId)).getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, initialZoom));
    }

    private ArrayList<MapItem> mapItems;

    public void addMapItem(MapItem mItem) {
        if (mMap != null) {
            int type = mItem.mapItemType;

            switch (type) {
                case 0:
                   break;

                case MapItem.MARKERTYPE:
                    mMap.addMarker(mItem.getMarkerOptions());
                    break;

                case MapItem.POLYGONTYPE:
                    mMap.addPolyline(mItem.getPolylineOptions());
                    break;


                case MapItem.POLYLINETYPE:
                    mMap.addPolygon(mItem.getPolygonOptions());
                    break;

            }
        }
    }

    public void addMapItemList(ArrayList<MapItem> mapItems, Boolean clear) {
        if (clear) {
            mMap.clear();
        }
        if (mMap != null && mapItems != null && mapItems.size()>0) {
            Iterator<MapItem> iterator = mapItems.iterator();
            while (iterator.hasNext()) {
                MapItem item = (MapItem) iterator.next();
                addMapItem(item);
            }
        }
    }

    public void addMapItemList(ArrayList<MapItem> mapItems) {
        addMapItemList(mapItems, true);
    }

}
