package edu.mit.mitmobile2.shuttles;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.AdapterView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopIntersectingAdapter;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopPredictionsAdapter;
import edu.mit.mitmobile2.shuttles.model.MITShuttleIntersectingRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttlePrediction;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

public class ShuttleStopViewPagerFragment extends Fragment {

    @InjectView(R.id.stop_prediction_adapter_view)
    AdapterView predictionAdapterView;

    @InjectView(R.id.intersecting_routes_adapter_view)
    AdapterView intersectingRoutesAdapterView;

    private ShuttleStopPredictionsAdapter predictionsAdapter;
    private ShuttleStopIntersectingAdapter intersectingAdapter;

    private String currentRouteId;
    private String stopId;
    private MITShuttleStopWrapper stop = new MITShuttleStopWrapper();
    private List<MITShuttleIntersectingRoute> intersectingRoutes;

    public static ShuttleStopViewPagerFragment newInstance(String currentRouteId, String stopId) {
        ShuttleStopViewPagerFragment fragment = new ShuttleStopViewPagerFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ROUTE_ID_KEY, currentRouteId);
        args.putString(Constants.STOP_ID_KEY, stopId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stop_viewpager, container, false);
        ButterKnife.inject(this, view);

        currentRouteId = getArguments().getString(Constants.ROUTE_ID_KEY);
        stopId = getArguments().getString(Constants.STOP_ID_KEY);

        intersectingRoutes = new ArrayList<MITShuttleIntersectingRoute>();

        String selectionString = Schema.Stop.TABLE_NAME + "." + Schema.Stop.STOP_ID + "=\'" + stopId + "\'";
        Cursor cursor = getActivity().getContentResolver().query(MITShuttlesProvider.SINGLE_STOP_URI, Schema.Stop.ALL_COLUMNS, selectionString, null, null);
        cursor.moveToFirst();
        stop.buildFromCursor(cursor, MitMobileApplication.dbAdapter);
        cursor.close();

        String routesSelectionString = selectionString + " AND " + Schema.Route.TABLE_NAME + "." + Schema.Route.ROUTE_ID + "!=\'" + currentRouteId + "\'";
        Cursor routesCursor = getActivity().getContentResolver().query(MITShuttlesProvider.INTERSECTING_ROUTES_URI, Schema.Route.ALL_COLUMNS, routesSelectionString, null, null);
        routesCursor.moveToFirst();
        while (!routesCursor.isAfterLast()) {
            MITShuttleIntersectingRoute route = new MITShuttleIntersectingRoute();
            route.buildFromCursor(routesCursor, MitMobileApplication.dbAdapter);
            intersectingRoutes.add(route);
            routesCursor.moveToNext();
        }
        cursor.close();

        ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(stop.getTitle());

        predictionsAdapter = new ShuttleStopPredictionsAdapter(getActivity(), stop.getPredictions());
        intersectingAdapter = new ShuttleStopIntersectingAdapter(getActivity(), intersectingRoutes);

        predictionAdapterView.setAdapter(predictionsAdapter);
        intersectingRoutesAdapterView.setAdapter(intersectingAdapter);

        return view;
    }

    public void updatePredictions(List<MITShuttlePrediction> predictions) {
        predictionsAdapter = new ShuttleStopPredictionsAdapter(getActivity(), predictions);
        predictionAdapterView.setAdapter(predictionsAdapter);
    }
}
