package edu.mit.mitmobile2.maps;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.resources.ResourceItem;

import java.util.ArrayList;
import java.util.zip.Inflater;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
        import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    protected ViewStub mapContentViewStub;
    protected View mapContentView; // inflated vew stub
    protected ImageView showListButton;
    protected ImageView showLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setContentLayoutId(R.layout.content_maps);
        super.onCreate(savedInstanceState);

        // Set the map content if it is defined in the child activity
        if (this.mapContentLayoutId > 0) {
            Log.d("ZZZ", "setting map content layout");
            mapContentViewStub = (ViewStub) findViewById(R.id.mapContentViewStub);
            mapContentViewStub.setLayoutResource(mapContentLayoutId);
            mapContentViewStub.inflate();
            mapContentView = (View)findViewById(R.id.mapContentView);
        }

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
        showLocationButton = (ImageView)findViewById(R.id.showLocationButton);
        showListButton = (ImageView)findViewById(R.id.showListButton);

        showListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMap();
            }
        });

        showLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.showLocation();
            }
        });

        // set initial height
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

    public void toggleMap() {
        // this method toggles between the full size map view and the split screen map with content view

        // collapse the map
        if (mapContentView.getVisibility() == View.GONE) {
            mapContentView.setVisibility(View.VISIBLE);
            showListButton.setVisibility(View.GONE);
            showLocationButton.setVisibility(View.GONE);
            mapView.toggle();
        }
        // expand the map
        else {
            mapContentView.setVisibility(View.GONE);
            showListButton.setVisibility(View.VISIBLE);
            showLocationButton.setVisibility(View.VISIBLE);
            mapView.toggle();
        }

    }
}