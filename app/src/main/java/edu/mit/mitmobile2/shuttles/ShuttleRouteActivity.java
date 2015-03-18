package edu.mit.mitmobile2.shuttles;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.SoloMapActivity;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleRouteAdapter;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;
import edu.mit.mitmobile2.shuttles.model.MITShuttleVehicle;
import timber.log.Timber;

public class ShuttleRouteActivity extends SoloMapActivity {

    @InjectView(R.id.route_information_top)
    TextView routeStatusTextView;

    @InjectView(R.id.route_information_bottom)
    TextView routeDescriptionTextView;

    private ShuttleRouteAdapter adapter;
    private MITShuttleRoute route = new MITShuttleRoute();
    private String routeID;
    private String uriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);

        //TODO: Save routeId in bundle for rotation
        routeID = getIntent().getStringExtra("routeID");
        adapter = new ShuttleRouteAdapter(this, R.layout.stop_list_item, new ArrayList<MITShuttleStopWrapper>());

        uriString = MITShuttlesProvider.ALL_ROUTES_URI + "/" + routeID;
        Cursor cursor = getContentResolver().query(Uri.parse(uriString), Schema.Route.ALL_COLUMNS, Schema.Route.ROUTE_ID + "=\'" + routeID + "\' ", null, null);
        cursor.moveToFirst();
        route.buildFromCursor(cursor, MitMobileApplication.dbAdapter);
        cursor.close();

        updateMapItems((ArrayList) route.getStops());
        displayMapItems();

        routeDescriptionTextView.setText(route.getDescription());
        if (route.isPredictable()) {
            routeStatusTextView.setText(getResources().getString(R.string.route_in_service));
        } else if (route.isScheduled()) {
            routeStatusTextView.setText(getResources().getString(R.string.route_unknown));
        } else {
            routeStatusTextView.setText(getResources().getString(R.string.route_not_in_service));
        }

        updateData();
        getSupportLoaderManager().initLoader(1, null, this);

        for (List<List<Double>> outerList : route.getPath().getSegments()) {
            List<LatLng> points = new ArrayList<>();
            PolylineOptions options = new PolylineOptions();
            for (List<Double> innerList : outerList) {
                LatLng point = new LatLng(innerList.get(1), innerList.get(0));
                points.add(point);
            }
            options.addAll(points);
            options.color(Color.BLUE);
            options.visible(true);
            options.width(8f);
            getMapView().addPolyline(options);
        }
    }

    @Override
    protected ArrayAdapter getMapItemAdapter() {
        return adapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MitCursorLoader(this, Uri.parse(uriString), Schema.Route.ALL_COLUMNS, Schema.Route.ROUTE_ID + "=\'" + routeID + "\' ", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        MITShuttleRoute route = new MITShuttleRoute();
        route.buildFromCursor(data, MitMobileApplication.dbAdapter);
        adapter.clear();
        adapter.addAll(route.getStops());
        adapter.notifyDataSetChanged();

        updateMapItems((ArrayList) route.getVehicles());
    }

    @Override
    protected void updateData() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Shuttles.MODULE_KEY, Constants.SHUTTLES);
        bundle.putString(Constants.Shuttles.PATH_KEY, Constants.Shuttles.ROUTE_INFO_PATH);
        bundle.putString(Constants.Shuttles.URI_KEY, uriString);

        HashMap<String, String> pathparams = new HashMap<>();
        pathparams.put("route", routeID);
        bundle.putString(Constants.Shuttles.PATHS_KEY, pathparams.toString());

        // FORCE THE SYNC
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        Timber.d("Requesting Predictions");

        ContentResolver.requestSync(MitMobileApplication.mAccount, MitMobileApplication.AUTHORITY, bundle);
    }

    /*private void updateVehicles() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Shuttles.MODULE_KEY, Constants.SHUTTLES);
        bundle.putString(Constants.Shuttles.PATH_KEY, Constants.Shuttles.VEHICLES_PATH);
        bundle.putString(Constants.Shuttles.URI_KEY, MITShuttlesProvider.VEHICLES_URI.toString());

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("agency", route.getAgency());
        queryParams.put("routes", route.getId());
        bundle.putString(Constants.Shuttles.QUERIES_KEY, queryParams.toString());

        bundle.putString("return", uriString);

        // FORCE THE SYNC
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        Timber.d("Requesting Vehicles");

        ContentResolver.requestSync(MitMobileApplication.mAccount, MitMobileApplication.AUTHORITY, bundle);
    }*/
}
