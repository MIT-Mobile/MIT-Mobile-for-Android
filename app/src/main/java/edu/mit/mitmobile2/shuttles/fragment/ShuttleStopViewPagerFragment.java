package edu.mit.mitmobile2.shuttles.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.AdapterView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.shuttles.MITShuttlesProvider;
import edu.mit.mitmobile2.shuttles.activities.ShuttleRouteActivity;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopIntersectingAdapter;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopPredictionsAdapter;
import edu.mit.mitmobile2.shuttles.callbacks.IntersectingAdapterCallback;
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

    private IntersectingAdapterCallback intersectingAdapterCallback = new IntersectingAdapterCallback() {
        @Override
        public void intersectingRouteClick(String routeId) {
            Intent intent = new Intent(getActivity(), ShuttleRouteActivity.class);
            intent.putExtra(Constants.ROUTE_ID_KEY, routeId);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    };

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
        stop.buildFromCursor(cursor, DBAdapter.getInstance());
        cursor.close();

        String routesSelectionString = selectionString + " AND " + Schema.Route.TABLE_NAME + "." + Schema.Route.ROUTE_ID + "!=\'" + currentRouteId + "\'";
        Cursor routesCursor = getActivity().getContentResolver().query(MITShuttlesProvider.INTERSECTING_ROUTES_URI, Schema.Route.ALL_COLUMNS, routesSelectionString, null, null);
        routesCursor.moveToFirst();
        while (!routesCursor.isAfterLast()) {
            MITShuttleIntersectingRoute route = new MITShuttleIntersectingRoute();
            route.buildFromCursor(routesCursor, DBAdapter.getInstance());
            intersectingRoutes.add(route);
            routesCursor.moveToNext();
        }
        cursor.close();

        predictionsAdapter = new ShuttleStopPredictionsAdapter(getActivity(), stop.getPredictions());
        intersectingAdapter = new ShuttleStopIntersectingAdapter(getActivity(), intersectingRoutes, intersectingAdapterCallback);

        predictionAdapterView.setAdapter(predictionsAdapter);
        intersectingRoutesAdapterView.setAdapter(intersectingAdapter);

        return view;
    }

    public void updatePredictions(List<MITShuttlePrediction> predictions) {
        predictionsAdapter = new ShuttleStopPredictionsAdapter(getActivity(), predictions);
        predictionAdapterView.setAdapter(predictionsAdapter);
    }
}
