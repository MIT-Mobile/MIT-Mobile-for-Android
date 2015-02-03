package edu.mit.mitmobile2.maps;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import android.graphics.*;
import android.content.res.Resources;
import com.google.maps.android.ui.IconGenerator;


import edu.mit.mitmobile2.R;

public class MITMapView {

    private GoogleMap mMap;
    private MapItem  mItem;
    public static String MAP_ITEMS = "MAP_ITEMS";
    private FragmentManager mFm;
    private int mapResourceId;

    //set initial latlng for zoom in MIT area
    final LatLng initialLatLng = new LatLng(42.359858, -71.09913);
    final int initialZoom = 14;
    private Context mContext;

    public MITMapView(Context mContext, FragmentManager fm, int mapResourceId) {
        this.mContext = mContext;
        this.mFm = fm;
        this.mapResourceId = mapResourceId;
        mMap = ((MapFragment) fm.findFragmentById(mapResourceId)).getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, initialZoom));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false); // delete default button
    }

    private ArrayList<MapItem> mapItems;

    public void show() {
       Fragment f = mFm.findFragmentById(mapResourceId);
        mFm.beginTransaction()
                .show(f)
                .commit();
    };

    public void hide() {
        Fragment f = mFm.findFragmentById(mapResourceId);
        mFm.beginTransaction()
                .hide(f)
                .commit();
    }

    public void addMapItem(MapItem mItem) {
        if (mMap != null) {
            int type = mItem.mapItemType;

            switch (type) {
                case 0:
                   break;

                case MapItem.MARKERTYPE:
                    if (mItem.getMarkerText() != null) {
                        IconGenerator iconGenerator = new IconGenerator(mContext);
                        iconGenerator.setBackground(mContext.getResources().getDrawable(R.drawable.usermarker));

                        iconGenerator.setTextAppearance(10); //set font size?
                        Bitmap bitmap = iconGenerator.makeIcon(mItem.getMarkerText());
                        mMap.addMarker(mItem.getMarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                    }
                    else {
                        mMap.addMarker(mItem.getMarkerOptions());
                    }
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

    public void addMapItemList(ArrayList<MapItem> mapItems, Boolean clear,Boolean fit) {
        this.mapItems = mapItems;
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
        addMapItemList(mapItems, true,true);
    }

    public void fitMapItems() {

        //Calculate the markers to get their position
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (int i = 0; i < mapItems.size(); i++) {
            MapItem mItem = mapItems.get(i);
            if (mItem.getMapItemType() == MapItem.MARKERTYPE) {
                b.include(mItem.getMarkerOptions().getPosition());
            }
        }
        LatLngBounds bounds = b.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 25,25,5);
        mMap.animateCamera(cu);
    }

    public GoogleMap getMap() {
        return this.mMap;
    }
}
