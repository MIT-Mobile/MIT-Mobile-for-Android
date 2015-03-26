package edu.mit.mitmobile2.shuttles;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import timber.log.Timber;

public class ShuttleRouteActivity extends SoloMapActivity {

    @InjectView(R.id.route_information_top)
    TextView routeStatusTextView;

    @InjectView(R.id.route_information_bottom)
    TextView routeDescriptionTextView;

    @InjectView(R.id.shuttle_imageview)
    ImageView serviceIcon;

    private ShuttleRouteAdapter adapter;
    private MITShuttleRoute route = new MITShuttleRoute();
    private String routeId;
    private String uriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View headerView = View.inflate(this, R.layout.stop_list_header, null);
        addHeaderView(headerView);

        ButterKnife.inject(this);

        //TODO: Save routeId in bundle for rotation
        routeId = getIntent().getStringExtra(Constants.ROUTE_ID_KEY);
        adapter = new ShuttleRouteAdapter(this, R.layout.stop_list_item, new ArrayList<MITShuttleStopWrapper>());

        uriString = MITShuttlesProvider.ALL_ROUTES_URI + "/" + routeId;
        Cursor cursor = getContentResolver().query(Uri.parse(uriString), Schema.Route.ALL_COLUMNS, Schema.Route.ROUTE_ID + "=\'" + routeId + "\' ", null, null);
        cursor.moveToFirst();
        route.buildFromCursor(cursor, MitMobileApplication.dbAdapter);
        cursor.close();

        setTitle(route.getTitle());

        updateMapItems((ArrayList) route.getStops());
        displayMapItems();

        isRoutePredictable = route.isPredictable();
        isRouteScheduled = route.isScheduled();

        routeDescriptionTextView.setText(route.getDescription());
        if (route.isPredictable()) {
            routeStatusTextView.setText(getResources().getString(R.string.route_in_service));
            serviceIcon.setImageResource(R.drawable.shuttle_small_active);
        } else if (route.isScheduled()) {
            routeStatusTextView.setText(getResources().getString(R.string.route_unknown));
            serviceIcon.setImageResource(R.drawable.shuttle_small_active);
        } else {
            routeStatusTextView.setText(getResources().getString(R.string.route_not_in_service));
            serviceIcon.setImageResource(R.drawable.shuttle_small_inactive);
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
            options.color(mContext.getResources().getColor(R.color.map_path_color));
            options.visible(true);
            options.width(12f);
            getMapView().addPolyline(options);
        }

        refreshMapInfoWindow();
    }

    @Override
    protected void listItemClicked(int position) {
        MITShuttleStopWrapper stop = adapter.getItem(position);
        Intent intent = new Intent(this, ShuttleStopActivity.class);
        intent.putExtra(Constants.ROUTE_ID_KEY, routeId);
        intent.putExtra(Constants.STOP_ID_KEY, stop.getId());
        startActivity(intent);
    }

    @Override
    protected ArrayAdapter getMapItemAdapter() {
        return adapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MitCursorLoader(this, Uri.parse(uriString), Schema.Route.ALL_COLUMNS, Schema.Route.ROUTE_ID + "=\'" + routeId + "\' ", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        //MITShuttleRoute route = new MITShuttleRoute();
        route.buildFromCursor(data, MitMobileApplication.dbAdapter);
        adapter.clear();
        adapter.addAll(route.getStops());
        adapter.notifyDataSetChanged();

        updateMapItems((ArrayList) route.getVehicles());

        if (swipeRefreshLayout.isRefreshing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        refreshMapInfoWindow();
    }

    @Override
    protected void updateData() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Shuttles.MODULE_KEY, Constants.SHUTTLES);
        bundle.putString(Constants.Shuttles.PATH_KEY, Constants.Shuttles.ROUTE_INFO_PATH);
        bundle.putString(Constants.Shuttles.URI_KEY, uriString);

        HashMap<String, String> pathparams = new HashMap<>();
        pathparams.put("route", routeId);
        bundle.putString(Constants.Shuttles.PATHS_KEY, pathparams.toString());

        // FORCE THE SYNC
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        Timber.d("Requesting Predictions");

        ContentResolver.requestSync(MitMobileApplication.mAccount, MitMobileApplication.AUTHORITY, bundle);
    }

    @Override
    public void onBackPressed() {
        if (!mapViewExpanded) {
            super.onBackPressed();
        } else {
            showListView();
        }
    }

    public void refreshMapInfoWindow() {
        mapView.getMap().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (marker.getTitle() != null) {
                    View view = getLayoutInflater().inflate(R.layout.mit_map_info_window, null);
                    TextView stopNameTextView = (TextView) view.findViewById(R.id.stop_name_textview);
                    TextView stopPredictionView = (TextView) view.findViewById(R.id.stop_prediction_textview);
                    stopNameTextView.setText(marker.getTitle());
                    for (MITShuttleStopWrapper stop : route.getStops()) {
                        if (marker.getSnippet().equals(stop.getId())) {
                            stopNameTextView.setText(stop.getTitle());
                            if (route.isPredictable()) {
                                if (stop.getPredictions().size() > 0) {
                                    if (stop.getPredictions().get(0).getSeconds() / 60 < 1) {
                                        stopPredictionView.setText(getString(R.string.arriving_now));
                                    } else {
                                        stopPredictionView.setText(getString(R.string.arriving_in) + " " +
                                                stop.getPredictions().get(0).getSeconds() / 60 + " " +
                                                getString(R.string.minutes));
                                    }
                                }
                            } else if (route.isScheduled()) {
                                stopPredictionView.setText(getString(R.string.route_unknown));
                            } else {
                                stopPredictionView.setText(getString(R.string.route_not_in_service));
                            }
                        }
                    }
                    return view;
                } else {
                    return null;
                }
            }
        });
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
