package edu.mit.mitmobile2.shuttles;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SoloMapActivity;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopsAdapter;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

public class ShuttleStopsActivity extends SoloMapActivity {

    ShuttleStopsAdapter adapter;
    private List<MITShuttleStopWrapper> stops = new ArrayList<>();
    private String routeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Change this to get route ID from intent, which will be used to get the route's data from the database
        //routeID = getIntent().getStringExtra("routeID");
        MITShuttleRoute routeWrapper = getIntent().getParcelableExtra("route");
        stops.addAll(routeWrapper.getStops());
        adapter = new ShuttleStopsAdapter(this, R.layout.stops_list_row, routeWrapper.getStops());

        setMapItems((ArrayList) routeWrapper.getStops());
        displayMapItems();
    }

    @Override
    protected void fillAdapter() {
        adapter.addAll(stops);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected ArrayAdapter getMapItemAdapter() {
        return adapter;
    }
}
