package edu.mit.mitmobile2.shuttles;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.SoloMapActivity;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopViewPagerAdapter;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;
import timber.log.Timber;

public class ShuttleStopActivity extends SoloMapActivity {

    ViewPager predictionViewPager;
    View transparentView;

    private ShuttleStopViewPagerAdapter stopViewPagerAdapter;
    private MITShuttleRoute route = new MITShuttleRoute();
    private List<MITShuttleStopWrapper> stops;
    private String routeId;
    private String stopId;
    private String uriString;
    private String selectionString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View shuttleStopContent = View.inflate(this, R.layout.shuttle_stop_content, null);

        predictionViewPager = (ViewPager) shuttleStopContent.findViewById(R.id.prediction_view_pager);
        transparentView = shuttleStopContent.findViewById(R.id.transparent_map_overlay);

        routeId = getIntent().getStringExtra(Constants.ROUTE_ID_KEY);
        stopId = getIntent().getStringExtra(Constants.STOP_ID_KEY);
        uriString = MITShuttlesProvider.STOPS_URI + "/" + stopId;
        selectionString = Schema.Route.TABLE_NAME + "." + Schema.Stop.ROUTE_ID + "=\'" + routeId + "\'";
        Cursor cursor = getContentResolver().query(Uri.parse(uriString), Schema.Stop.ALL_COLUMNS, selectionString, null, null);
        cursor.moveToFirst();
        route.buildFromCursor(cursor, MitMobileApplication.dbAdapter);
        cursor.close();

        stops = route.getStops();
        List<String> stopIds = new ArrayList<String>();
        for (MITShuttleStopWrapper stop : stops) {
            stopIds.add(stop.getId());
        }
        stopViewPagerAdapter = new ShuttleStopViewPagerAdapter(getSupportFragmentManager(), stopIds);

        predictionViewPager.setAdapter(stopViewPagerAdapter);
        predictionViewPager.setCurrentItem(getStartPosition());

        addTransparentView(transparentView);
        addShuttleStopContent(shuttleStopContent);

        updateData();
        getSupportLoaderManager().initLoader(0, null, this);
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
        return null;//new MitCursorLoader(this, Uri.parse(uriString), Schema.Stop.ALL_COLUMNS, selectionString, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        MITShuttleRoute route = new MITShuttleRoute();
        route.buildFromCursor(data, MitMobileApplication.dbAdapter);
        stops = route.getStops();
        int currentStop = predictionViewPager.getCurrentItem();
        stopViewPagerAdapter.updatePredictions(currentStop, stops.get(currentStop).getPredictions());
    }
}
