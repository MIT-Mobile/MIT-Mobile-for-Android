package edu.mit.mitmobile2.maps;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.app.SearchManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.LatLngBounds;
        import com.google.android.gms.maps.model.Marker;

public class  MapsActivity extends MITModuleActivity {

    protected MITMapView mapView;
    public static String MAP_ITEMS = "MAP_ITEMS";
    protected ListView mapListView;
    protected ImageView showListButton;
    protected ImageView showLocationButton;
    protected ArrayList mapItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setContentLayoutId(R.layout.content_maps);
        super.onCreate(savedInstanceState);
        initMap();

        Intent intent = getIntent();
        if(intent.hasExtra(MapsActivity.MAP_ITEMS)) {
            this.mapItems = intent.getExtras().getParcelableArrayList(MapsActivity.MAP_ITEMS);
            for (int i = 0; i < this.mapItems.size(); i++) {
                Object m = this.mapItems.get(i);
                if (MapItem.class.isAssignableFrom(m.getClass())) {
                    mapView.addMapItem((MapItem)m);
                }
            }
        }


        HashMap<String, String> queries = new HashMap<>();
        queries.put("agency", "mit");

        apiClient.get(Constants.SHUTTLES, "shuttles/predictions", null, queries, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d("ZZZ", "onResume");

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("ZZZ", "onResume");

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ZZZ", "onResume");

        Intent intent = getIntent();
        if (intent.hasExtra(MITMapView.MAP_ITEMS)) {
            this.mapItems = intent.getExtras().getParcelableArrayList(MITMapView.MAP_ITEMS);
            displayMapItems();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            handleSearch(query);
        }
    }

    private void initMap() {
        Log.d(TAG,"initMap()");
        FragmentManager fm = getFragmentManager();
        mapView = new MITMapView(mContext,fm,R.id.map);

        // set the InfoWindowAdapter if not null
        if (this.getInfoWindowAdapter() != null) {
            mapView.getMap().setInfoWindowAdapter(this.getInfoWindowAdapter());
        }

        //OnInfoWindowClickListener
        if (this.getOnInfoWindowClickListener() != null) {
            mapView.getMap().setOnInfoWindowClickListener(this.getOnInfoWindowClickListener());
        }

        // set onmarker click listener
        mapView.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mapView.setLastClickedMarker(marker);
                return false;
            }
        });

                mapListView = (ListView)findViewById(R.id.mapListView);
        mapListView.addHeaderView(getMapListHeader());

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
        Log.d(TAG, "initMap()");
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

    public void toggleMap() {
        // this method toggles between the full size map view and the split screen map with content view


        // collapse the map if there the map items list is not empty
        // the map list button shouldn't be visible if there are no map items to display
        if (!mapItems.isEmpty() && mapListView.getVisibility() == View.GONE) {
            if (mapView.getLastClickedMarker() != null) {
                mapView.getLastClickedMarker().hideInfoWindow();
            }
            mapListView.setVisibility(View.VISIBLE);
            Log.d(TAG,"list view height = " + mapListView.getHeight());
            showListButton.setVisibility(View.GONE);
            showLocationButton.setVisibility(View.GONE);
            mapView.toggle();
        }
        // expand the map
        else {
            mapListView.setVisibility(View.GONE);
            showListButton.setVisibility(View.VISIBLE);
            showLocationButton.setVisibility(View.VISIBLE);
            mapView.toggle();
        }

    }

    // Add transparent header to list to set initial position below the map
    protected LinearLayout getMapListHeader() {
        LinearLayout header = (LinearLayout)inflater.inflate(R.layout.map_list_header, mapListView, false);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMap();
            }
        });

        return header;
    }

    // Override this method to get map items
    protected void getMapItems(Map params) {
    }

    // Override this method to specify the map item handler
    protected Handler getMapItemHandler() {
        return null;
    }

    // Override this method to use a custom ArrayAdapter
    protected ArrayAdapter<MapItem> getMapItemAdapter() {
        return null;
    }

    // Override this method to use a custom InfoWindowAdapter
    protected GoogleMap.InfoWindowAdapter getInfoWindowAdapter() {
        return null;
    }

    // Override this method to use a custom OnInfoWindowClickListener
    protected GoogleMap.OnInfoWindowClickListener getOnInfoWindowClickListener() {
        return null;
    }
        // override this method to use a custom onclick listener
    protected AdapterView.OnItemClickListener getOnItemClickListener() {
        return null;
    }

    protected void displayMapItems() {
        Log.d(TAG,"displayMapItems()");
        ArrayAdapter<MapItem> arrayAdapter = this.getMapItemAdapter();
        mapListView.setAdapter(arrayAdapter);

        if (mapView != null) {
            mapView.addMapItemList(this.mapItems);
        }

        mapListView.setOnItemClickListener(this.getOnItemClickListener());
        toggleMap();
    }

    // Override this method to get map items
    protected void viewMapItem(int mapItemIndex) {}

}