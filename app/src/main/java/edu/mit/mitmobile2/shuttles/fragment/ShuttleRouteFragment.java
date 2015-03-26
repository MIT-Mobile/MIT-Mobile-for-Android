package edu.mit.mitmobile2.shuttles.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import edu.mit.mitmobile2.shuttles.MITShuttlesProvider;
import edu.mit.mitmobile2.shuttles.MapFragmentCallback;
import edu.mit.mitmobile2.shuttles.MitCursorLoader;
import edu.mit.mitmobile2.shuttles.ShuttleStopActivity;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleRouteAdapter;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;
import timber.log.Timber;

public class ShuttleRouteFragment extends MitMapFragment {
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

        //TODO: Save routeId in bundle for rotation
        routeId = getActivity().getIntent().getStringExtra(Constants.ROUTE_ID_KEY);
        adapter = new ShuttleRouteAdapter(getActivity(), R.layout.stop_list_item, new ArrayList<MITShuttleStopWrapper>());

        uriString = MITShuttlesProvider.ALL_ROUTES_URI + "/" + routeId;
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse(uriString), Schema.Route.ALL_COLUMNS, Schema.Route.ROUTE_ID + "=\'" + routeId + "\' ", null, null);
        cursor.moveToFirst();
        route.buildFromCursor(cursor, MitMobileApplication.dbAdapter);
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
            options.color(Color.BLUE);
            options.visible(true);
            options.width(8f);
            getMapView().addPolyline(options);
        }

        return view;
    }

    @Override
    protected void listItemClicked(int position) {
        MITShuttleStopWrapper stop = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), ShuttleStopActivity.class);
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
        return new MitCursorLoader(getActivity(), Uri.parse(uriString), Schema.Route.ALL_COLUMNS, Schema.Route.ROUTE_ID + "=\'" + routeId + "\' ", null, null);
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

        if (swipeRefreshLayout.isRefreshing()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
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
