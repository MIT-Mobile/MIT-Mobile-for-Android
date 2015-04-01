package edu.mit.mitmobile2.shuttles.fragment;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
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
import edu.mit.mitmobile2.shuttles.AlarmReceiver;
import edu.mit.mitmobile2.shuttles.MITShuttlesProvider;
import edu.mit.mitmobile2.shuttles.activities.ShuttleRouteActivity;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopIntersectingAdapter;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopPredictionsAdapter;
import edu.mit.mitmobile2.shuttles.callbacks.AlertIconCallback;
import edu.mit.mitmobile2.shuttles.callbacks.IntersectingAdapterCallback;
import edu.mit.mitmobile2.shuttles.model.MITAlert;
import edu.mit.mitmobile2.shuttles.model.MITShuttleIntersectingRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttlePrediction;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStop;
import edu.mit.mitmobile2.shuttles.utils.ShuttlesDatabaseHelper;
import timber.log.Timber;

public class ShuttleStopViewPagerFragment extends Fragment implements IntersectingAdapterCallback, AlertIconCallback {

    @InjectView(R.id.stop_prediction_adapter_view)
    AdapterView predictionAdapterView;

    @InjectView(R.id.intersecting_routes_adapter_view)
    AdapterView intersectingRoutesAdapterView;

    private ShuttleStopPredictionsAdapter predictionsAdapter;
    private ShuttleStopIntersectingAdapter intersectingAdapter;

    private String currentRouteId;
    private String stopId;
    private MITShuttleStop stop = new MITShuttleStop();
    private List<MITShuttleIntersectingRoute> intersectingRoutes;
    private List<MITAlert> alerts = new ArrayList<>();

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

        intersectingRoutes = new ArrayList<>();

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

        String selection = Schema.Alerts.ROUTE_ID + "=\'" + currentRouteId + "\' AND " + Schema.Alerts.STOP_ID + "=\'" + stopId + "\'";
        Cursor c = getActivity().getContentResolver().query(MITShuttlesProvider.ALERTS_URI, Schema.Alerts.ALL_COLUMNS, selection, null, null);
        alerts = ShuttlesDatabaseHelper.getAlerts(c);

        predictionsAdapter = new ShuttleStopPredictionsAdapter(getActivity(), stop.getPredictions(), alerts, this);
        intersectingAdapter = new ShuttleStopIntersectingAdapter(getActivity(), intersectingRoutes, this);

        predictionAdapterView.setAdapter(predictionsAdapter);
        intersectingRoutesAdapterView.setAdapter(intersectingAdapter);

        return view;
    }

    private void buildNotification(int time, int id) {
        // If we keep track of the alarm IDs, we can update

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
        alarmIntent.putExtra(Constants.ROUTE_ID_KEY, currentRouteId);
        alarmIntent.putExtra(Constants.STOP_ID_KEY, stopId);
        alarmIntent.putExtra(Constants.ALARM_ID_KEY, id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((time * 1000) - (60000 * 5)), pendingIntent);
    }

    private void cancelAlarm(int id) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
        alarmIntent.putExtra(Constants.ROUTE_ID_KEY, currentRouteId);
        alarmIntent.putExtra(Constants.STOP_ID_KEY, stopId);
        alarmIntent.putExtra(Constants.ALARM_ID_KEY, id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingIntent);
    }

    public void updatePredictions(List<MITShuttlePrediction> predictions) {
        predictionsAdapter.updateItems(predictions);
        predictionAdapterView.setAdapter(predictionsAdapter);
    }

    @Override
    public void intersectingRouteClick(String routeId) {
        Intent intent = new Intent(getActivity(), ShuttleRouteActivity.class);
        intent.putExtra(Constants.ROUTE_ID_KEY, routeId);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void alertIconClicked(int position) {
        MITShuttlePrediction prediction = stop.getPredictions().get(position);

        String selection = Schema.Alerts.STOP_ID + "=\'" + stopId + "\' AND " + Schema.Alerts.ROUTE_ID + "=\'" + currentRouteId + "\'";
        Cursor cursor = getActivity().getContentResolver().query(MITShuttlesProvider.ALERTS_URI, Schema.Alerts.ALL_COLUMNS, selection, null, null);
        if (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(Schema.Alerts.ID_COL));
            cancelAlarm((int) id);
            Timber.d("Deleted alarm with ID_COL = " + id);
            getActivity().getContentResolver().delete(MITShuttlesProvider.ALERTS_URI, selection, null);
        }
        cursor.close();

        // Save alert in DB
        MITAlert alert = new MITAlert(currentRouteId, stopId, prediction.getVehicleId(), prediction.getTimestamp());
        DBAdapter.getInstance().acquire(alert);
        long id = alert.persistToDatabase();

        Timber.d("Created alarm with ID_COL = " + id);
        buildNotification(prediction.getSeconds(), (int) id);
    }
}
