package edu.mit.mitmobile2.maps;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.resources.ResourceItem;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
        import android.app.Activity;
        import android.view.Menu;

        import com.google.android.gms.maps.CameraUpdate;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapFragment;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.LatLngBounds;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;

public class  MapsActivity extends MITModuleActivity {

    private GoogleMap mMap;
    public static String MAP_ITEMS = "MAP_ITEMS";
    private ArrayList<MapItem> mapItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_maps);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //MAP_TYPE_NONE   		No base map tiles.
        //MAP_TYPE_NORMAL		Basic maps.
        //MAP_TYPE_SATELLITE	Satellite maps with no labels.
        //MAP_TYPE_HYBRID		Satellite maps with a transparent layer of major streets.
        //MAP_TYPE_TERRAIN		Terrain maps.
        //final LatLng CIU = new LatLng(35.21843892856462, 33.41662287712097);

        //set initial latlng for zoom in MIT area
        final LatLng initialLatLng = new LatLng(42.359858, -71.09913);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 14));

        Intent intent = getIntent();
        if(intent.hasExtra(MapsActivity.MAP_ITEMS)) {
            this.mapItems = intent.getExtras().getParcelableArrayList(MapsActivity.MAP_ITEMS);
            for (int i = 0; i < this.mapItems.size(); i++) {
                mMap.addMarker(this.mapItems.get(i).getMarkerOptions());
            }
        }

    }


    private static void zoomToCoverAllMarkers(ArrayList<LatLng> latLngList, GoogleMap googleMap)
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

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