package edu.mit.mitmobile2.shuttles.fragment;

import android.content.ContentResolver;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.MitMapFragment;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.shuttles.MITShuttlesProvider;
import edu.mit.mitmobile2.shuttles.MitCursorLoader;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopViewPagerAdapter;
import edu.mit.mitmobile2.shuttles.callbacks.MapFragmentCallback;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;
import timber.log.Timber;

public class ShuttleStopFragment extends MitMapFragment {

    ViewPager predictionViewPager;

    private ShuttleStopViewPagerAdapter stopViewPagerAdapter;
    private MITShuttleRoute route = new MITShuttleRoute();
    private List<MITShuttleStopWrapper> stops;
    private String routeId;
    private String stopId;
    private String uriString;
    private String selectionString;
    private MapFragmentCallback callback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View shuttleStopContent = inflater.inflate(R.layout.shuttle_stop_content, null);

        callback = (MapFragmentCallback) getActivity();

        predictionViewPager = (ViewPager) shuttleStopContent.findViewById(R.id.prediction_view_pager);
        View transparentView = shuttleStopContent.findViewById(R.id.transparent_map_overlay);

        routeId = getActivity().getIntent().getStringExtra(Constants.ROUTE_ID_KEY);
        stopId = getActivity().getIntent().getStringExtra(Constants.STOP_ID_KEY);

        uriString = MITShuttlesProvider.STOPS_URI + "/" + stopId;
        selectionString = Schema.Route.TABLE_NAME + "." + Schema.Stop.ROUTE_ID + "=\'" + routeId + "\'";
        Cursor cursor = getActivity().getContentResolver().query(Uri.parse(uriString), Schema.Stop.ALL_COLUMNS, selectionString, null, null);
        cursor.moveToFirst();
        route.buildFromCursor(cursor, DBAdapter.getInstance());
        cursor.close();

        callback.setActionBarTitle(route.getTitle());

        updateMapItems((ArrayList) route.getStops(), false);
        displayMapItems();

        stops = route.getStops();
        List<String> stopIds = new ArrayList<String>();
        for (MITShuttleStopWrapper stop : stops) {
            stopIds.add(stop.getId());
        }
        stopViewPagerAdapter = new ShuttleStopViewPagerAdapter(getFragmentManager(), routeId, stopIds);

        predictionViewPager.setAdapter(stopViewPagerAdapter);
        predictionViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                callback.setActionBarSubtitle(stops.get(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        int startPosition = getStartPosition();
        callback.setActionBarSubtitle(stops.get(startPosition).getTitle());
        predictionViewPager.setCurrentItem(getStartPosition());

        addTransparentView(transparentView);
        addShuttleStopContent(shuttleStopContent);

        updateData();
        getLoaderManager().initLoader(0, null, this);

        drawRoutePath(route);

        return view;
    }

    private int getStartPosition() {
        for (MITShuttleStopWrapper stop : stops) {
            if (stop.getId().equals(stopId)) {
                return stops.indexOf(stop);
            }
        }
        return 0;
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MitCursorLoader(getActivity(), Uri.parse(uriString), Schema.Stop.ALL_COLUMNS, selectionString, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        route.buildFromCursor(data, DBAdapter.getInstance());
        stops = route.getStops();
        int currentStop = predictionViewPager.getCurrentItem();
        stopViewPagerAdapter.updatePredictions(currentStop, stops.get(currentStop).getPredictions());
    }
}
