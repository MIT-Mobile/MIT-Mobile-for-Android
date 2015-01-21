package edu.mit.mitmobile2.maps;

import java.util.ArrayList;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;

public class MapsActivity extends MITModuleActivity {
	
	private GoogleMap mMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_maps);	
		//int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		//mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //MAP_TYPE_NONE   		No base map tiles.
        //MAP_TYPE_NORMAL		Basic maps.
        //MAP_TYPE_SATELLITE	Satellite maps with no labels.
        //MAP_TYPE_HYBRID		Satellite maps with a transparent layer of major streets.
    	//MAP_TYPE_TERRAIN		Terrain maps.		
		//final LatLng CIU = new LatLng(35.21843892856462, 33.41662287712097);
		final LatLng CIU1 = new LatLng(42.359858, -71.09913);
		final LatLng CIU2 = new LatLng(42.354934, -71.104633);
		ArrayList<LatLng> latLngList = new ArrayList<LatLng>();
		latLngList.add(CIU1);
		latLngList.add(CIU2);
		Marker ciu1 = mMap.addMarker(new MarkerOptions().position(CIU1).title("MIT Headquater"));
		Marker ciu2 = mMap.addMarker(new MarkerOptions().position(CIU2).title("IST"));
		ciu1.showInfoWindow();
		ciu2.showInfoWindow();
		ciu1.setDraggable(true);
		ciu2.setDraggable(true);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CIU1, 14));
		/*
		Marker ciu = mMap.addMarker(new MarkerOptions().position(CIU1).title("My Office"));
		ciu.setDraggable(true);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CIU1, 14));
		*/
		//zoomToCoverAllMarkers(latLngList, mMap);
	}
	
	private static void zoomToCoverAllMarkers(ArrayList<LatLng> latLngList, GoogleMap googleMap)
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        /*for (Marker marker : markers)
        {
            builder.include(marker.getPosition());
        }*/
        for (LatLng marker : latLngList)
        {
            builder.include(marker);
        }

        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);
        googleMap.animateCamera(cu);
    }


}
