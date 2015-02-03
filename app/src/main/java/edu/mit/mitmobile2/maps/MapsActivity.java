package edu.mit.mitmobile2.maps;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.resources.ResourceItem;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
        import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.ViewStub;

import com.google.android.gms.maps.CameraUpdate;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapFragment;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.LatLngBounds;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class  MapsActivity extends MITModuleActivity {

    protected MITMapView mapView;
    public static String MAP_ITEMS = "MAP_ITEMS";
    private ArrayList<MapItem> mapItems;
    protected int mapContentLayoutId;
    protected ViewStub contentViewStub;
    protected com.sothree.slidinguppanel.SlidingUpPanelLayout  slidingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setContentLayoutId(R.layout.content_maps);
        super.onCreate(savedInstanceState);

        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.slidingLayout);
//
//        if (mapContentLayoutId > 0) {
//            contentViewStub = (ViewStub) findViewById(R.id.mapContentStub);
//            contentViewStub.setLayoutResource(mapContentLayoutId);
//            contentViewStub.inflate();
//        }

        initMap();

        Intent intent = getIntent();
        if(intent.hasExtra(MapsActivity.MAP_ITEMS)) {
            this.mapItems = intent.getExtras().getParcelableArrayList(MapsActivity.MAP_ITEMS);
            for (int i = 0; i < this.mapItems.size(); i++) {
                mapView.addMapItem(this.mapItems.get(i));
            }
        }

    }

    private void initMap() {
        FragmentManager fm = getFragmentManager();
        mapView = new MITMapView(mContext,fm,R.id.map);
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

    public int getMapContentLayoutId() {
        return mapContentLayoutId;
    }

    public void setMapContentLayoutId(int mapContentLayoutId) {
        this.mapContentLayoutId = mapContentLayoutId;
    }
}