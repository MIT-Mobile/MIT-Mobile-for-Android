package edu.mit.mitmobile2.shuttles.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.MitMapFragment;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.shuttles.MITShuttlesProvider;
import edu.mit.mitmobile2.shuttles.callbacks.MapFragmentCallback;
import edu.mit.mitmobile2.shuttles.MitCursorLoader;
import edu.mit.mitmobile2.shuttles.activities.ShuttleStopActivity;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleRouteAdapter;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;
import timber.log.Timber;

public class ShuttleRouteFragment extends MitMapFragment implements GoogleMap.InfoWindowAdapter {
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

    MapFragmentCallback callback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        callback = (MapFragmentCallback) getActivity();

        View headerView = View.inflate(getActivity(), R.layout.stop_list_header, null);
        addHeaderView(headerView);

        ButterKnife.inject(this, view);

        if (savedInstanceState != null && savedInstanceState.getString(Constants.ROUTE_ID_KEY) != null) {
            routeId = savedInstanceState.getString(Constants.ROUTE_ID_KEY);
        } else {
            routeId = getActivity().getIntent().getStringExtra(Constants.ROUTE_ID_KEY);
        }

        adapter = new ShuttleRouteAdapter(getActivity(), R.layout.stop_list_item, new ArrayList<MITShuttleStopWrapper>());

        uriString = MITShuttlesProvider.ALL_ROUTES_URI + "/" + routeId;
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse(uriString), Schema.Route.ALL_COLUMNS, Schema.Route.ROUTE_ID + "=\'" + routeId + "\' ", null, null);
        cursor.moveToFirst();
        route.buildFromCursor(cursor, DBAdapter.getInstance());
        cursor.close();

        callback.setActionBarTitle(route.getTitle());

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
        getLoaderManager().initLoader(1, null, this);

        for (List<List<Double>> outerList : route.getPath().getSegments()) {
            List<LatLng> points = new ArrayList<>();
            PolylineOptions options = new PolylineOptions();
            for (List<Double> innerList : outerList) {
                LatLng point = new LatLng(innerList.get(1), innerList.get(0));
                points.add(point);
            }
            options.addAll(points);
            options.color(getResources().getColor(R.color.map_path_color));
            options.visible(true);
            options.width(12f);
            getMapView().addPolyline(options);
        }

        getMapView().setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getActivity(), ShuttleStopActivity.class);
                intent.putExtra(Constants.STOP_ID_KEY, marker.getSnippet());
                intent.putExtra(Constants.ROUTE_ID_KEY, route.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        refreshMapInfoWindow();

        return view;
    }

    @Override
    protected void listItemClicked(int position) {
        //Because header is counted in items
        if (position != 0) {
            MITShuttleStopWrapper stop = adapter.getItem(position - 1);
            Intent intent = new Intent(getActivity(), ShuttleStopActivity.class);
            intent.putExtra(Constants.ROUTE_ID_KEY, routeId);
            intent.putExtra(Constants.STOP_ID_KEY, stop.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }

    @Override
    protected ArrayAdapter getMapItemAdapter() {
        return adapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MitCursorLoader(getActivity(), Uri.parse(uriString), Schema.Route.ALL_COLUMNS, Schema.Route.ROUTE_ID + "=\'" + routeId + "\' ", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        route.buildFromCursor(data, DBAdapter.getInstance());
        adapter.clear();
        adapter.addAll(route.getStops());
        adapter.notifyDataSetChanged();

        updateMapItems((ArrayList) route.getVehicles());

        if (swipeRefreshLayout.isRefreshing()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        refreshMapInfoWindow();
    }

    @Override
    public void onResume() {
        super.onResume();
        MitMobileApplication.bus.register(this);
    }

    @Override
    public void onPause() {
        MitMobileApplication.bus.register(this);
        super.onPause();
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

    public void refreshMapInfoWindow() {
        mitMapView.getMap().setInfoWindowAdapter(this);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (marker.getTitle() != null) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.mit_map_info_window, null);
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

    @Override
    protected void queryDatabase() {
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse(uriString), Schema.Route.ALL_COLUMNS, Schema.Route.ROUTE_ID + "=\'" + routeId + "\' ", null, null);
        cursor.moveToFirst();
        route.buildFromCursor(cursor, DBAdapter.getInstance());
        adapter.clear();
        adapter.addAll(route.getStops());
        adapter.notifyDataSetChanged();

        updateMapItems((ArrayList) route.getVehicles());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ROUTE_ID_KEY, routeId);
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
