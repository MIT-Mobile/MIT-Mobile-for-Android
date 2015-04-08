package edu.mit.mitmobile2.shuttles.fragment;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.mit.mitmobile2.MitMapFragment;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;

public class ShuttleMapFragment extends MitMapFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PREDICTIONS_PERIOD = 15000;
    private static final int PREDICTIONS_TIMER_OFFSET = 1000;

    private static Timer timer;

    protected boolean isRoutePredictable = false;
    protected boolean isRouteScheduled = false;

    protected void drawRoutePath(MITShuttleRoute route) {
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
            options.zIndex(100);

            getMapView().addPolyline(options);
        }
    }

    private void startTimerTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateData();
            }
        }, PREDICTIONS_TIMER_OFFSET, PREDICTIONS_PERIOD);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        timer.cancel();
        timer.purge();
        timer = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        queryDatabase();
        startTimerTask();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (marker.getTitle() != null) {
            View view = View.inflate(getActivity(), R.layout.mit_map_info_window, null);
            TextView stopNameTextView = (TextView) view.findViewById(R.id.top_textview);
            TextView stopPredictionView = (TextView) view.findViewById(R.id.bottom_textview);
            stopNameTextView.setText(marker.getTitle());
            if (isRoutePredictable) {
                stopPredictionView.setText(marker.getSnippet());
            } else if (isRouteScheduled) {
                stopPredictionView.setText(getString(R.string.route_unknown));
            } else {
                stopPredictionView.setText(getString(R.string.route_not_in_service));
            }
            return view;
        } else {
            return null;
        }
    }
}
