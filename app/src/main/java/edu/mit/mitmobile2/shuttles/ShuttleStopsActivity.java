package edu.mit.mitmobile2.shuttles;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapsActivity;
import edu.mit.mitmobile2.shuttles.adapters.ShuttleStopsAdapter;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRouteWrapper;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

public class ShuttleStopsActivity extends MapsActivity {

    ShuttleStopsAdapter adapter;
    List<MITShuttleStopWrapper> stops = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_shuttle_stops);

        MITShuttleRouteWrapper routeWrapper = getIntent().getParcelableExtra("A");
        mapItems = (ArrayList) routeWrapper.getStops();

        adapter = new ShuttleStopsAdapter(this, R.layout.stops_list_row, routeWrapper.getStops());

        super.mapView.getMap().setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                displayMapItems();
            }
        });
    }

    @Override
    protected LinearLayout getMapListHeader() {
        return super.getMapListHeader();
    }

    @Override
    protected void getMapItems(Map params) {
        super.getMapItems(params);
    }

    @Override
    protected Handler getMapItemHandler() {
        return super.getMapItemHandler();
    }

    @Override
    protected ArrayAdapter getMapItemAdapter() {
        return adapter;
    }

    @Override
    protected GoogleMap.InfoWindowAdapter getInfoWindowAdapter() {
        return super.getInfoWindowAdapter();
    }

    @Override
    protected GoogleMap.OnInfoWindowClickListener getOnInfoWindowClickListener() {
        return super.getOnInfoWindowClickListener();
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener() {
        return super.getOnItemClickListener();
    }

    @Override
    protected void displayMapItems() {
        super.displayMapItems();
    }

    @Override
    protected void viewMapItem(int mapItemIndex) {
        super.viewMapItem(mapItemIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shuttle_stops, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
