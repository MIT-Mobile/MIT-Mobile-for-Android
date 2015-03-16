package edu.mit.mitmobile2.shuttles;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SoloMapActivity;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleRouteAdapter;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

public class ShuttleRouteActivity extends SoloMapActivity {

    @InjectView(R.id.route_information_top)
    TextView routeStatusTextView;

    @InjectView(R.id.route_information_bottom)
    TextView routeDescriptionTextView;

    ShuttleRouteAdapter adapter;
    private List<MITShuttleStopWrapper> stops = new ArrayList<>();
    private String routeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        routeID = getIntent().getStringExtra("routeID");
        MITShuttleRoute routeWrapper = ShuttlesDatabaseHelper.getRoute(routeID);
        stops.addAll(routeWrapper.getStops());
        adapter = new ShuttleRouteAdapter(this, R.layout.stop_list_item, routeWrapper.getStops());

        setMapItems((ArrayList) routeWrapper.getStops());
        displayMapItems();

        routeDescriptionTextView.setText(routeWrapper.getDescription());
        if (routeWrapper.isPredictable()) {
            routeStatusTextView.setText(getResources().getString(R.string.route_in_service));
        } else if (routeWrapper.isScheduled()) {
            routeStatusTextView.setText(getResources().getString(R.string.route_unknown));
        } else {
            routeStatusTextView.setText(getResources().getString(R.string.route_not_in_service));
        }
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
