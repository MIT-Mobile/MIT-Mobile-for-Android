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
import android.widget.TextView;

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
import timber.log.Timber;

public class ShuttleStopViewPagerFragment extends Fragment implements IntersectingAdapterCallback, AlertIconCallback {

    @InjectView(R.id.stop_prediction_adapter_view)
    AdapterView predictionAdapterView;

    @InjectView(R.id.empty_predictions)
    TextView emptyPredictions;

    @InjectView(R.id.intersecting_routes_adapter_view)
    AdapterView intersectingRoutesAdapterView;

    @InjectView(R.id.empty_intersecting)
    TextView emptyIntersecting;

    private ShuttleStopPredictionsAdapter predictionsAdapter;
    private ShuttleStopIntersectingAdapter intersectingAdapter;

    private String currentRouteId;
    private String stopId;
    private MITShuttleStop stop = new MITShuttleStop();
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
        final View view = inflater.inflate(R.layout.fragment_shuttle_stop_viewpager, container, false);
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
        MITAlert alert = null;
        if (c.moveToFirst()) {
            alert = new MITAlert();
            alert.buildFromCursor(c, DBAdapter.getInstance());
        }
        c.close();

        predictionsAdapter = new ShuttleStopPredictionsAdapter(getActivity(), stop.getPredictions(), alert, this);
        intersectingAdapter = new ShuttleStopIntersectingAdapter(getActivity(), intersectingRoutes, this);

        predictionAdapterView.setAdapter(predictionsAdapter);
        intersectingRoutesAdapterView.setAdapter(intersectingAdapter);

        predictionAdapterView.setEmptyView(emptyPredictions);
        intersectingRoutesAdapterView.setEmptyView(emptyIntersecting);

        return view;
    }

    private void buildNotification(int time, int id, String description) {
        // If we keep track of the alarm IDs, we can update

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
        alarmIntent.putExtra(Constants.ALARM_ID_KEY, id);
        alarmIntent.putExtra(Constants.ALARM_DESCRIPTION, description);

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

    private String getRouteTitle() {
        Cursor cursor = DBAdapter.getInstance().db.query(Schema.Route.TABLE_NAME, new String[]{Schema.Route.ROUTE_TITLE}, Schema.Route.ROUTE_ID + "=?", new String[]{currentRouteId}, null, null, null);
        cursor.moveToFirst();
        String title = cursor.getString(cursor.getColumnIndex(Schema.Route.ROUTE_TITLE));
        cursor.close();
        return title;
    }

    private String buildAlertDescription(String title) {
        StringBuilder sb = new StringBuilder();

        sb.append(title);
        sb.append(" ");
        sb.append(getString(R.string.alert_descr_1));
        sb.append(" ");
        sb.append(stop.getTitle());
        sb.append(" ");
        sb.append(getString(R.string.alert_descr_2));
        sb.append(" ");

        List<MITShuttlePrediction> predictions = stop.getPredictions();
        int firstPredictionOffset = -1;

        for (int i = 0; i < predictions.size(); i++) {
            if (predictions.get(i).getSeconds() >= 360) {
                int mins = predictions.get(i).getSeconds() / 60;

                if (firstPredictionOffset == -1) {
                    firstPredictionOffset = mins - 5;
                }

                sb.append(mins - firstPredictionOffset);

                if (i < predictions.size() - 2) {
                    sb.append(", ");
                } else if (i < predictions.size() - 1) {
                    sb.append(", ");
                    sb.append(getString(R.string.alert_descr_3));
                    sb.append(" ");
                } else {
                    sb.append(" ");
                    sb.append(getString(R.string.alert_descr_4));
                }
            }
        }

        return sb.toString();
    }

    @Override
    public void intersectingRouteClick(String routeId) {
        Intent intent = new Intent(getActivity(), ShuttleRouteActivity.class);
        intent.putExtra(Constants.ROUTE_ID_KEY, routeId);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void alertIconClicked(int position, boolean newAlert) {
        List<MITShuttlePrediction> predictions = stop.getPredictions();

        if (predictions.size() > 0) {
            MITShuttlePrediction prediction = predictions.get(position);

            String selection = Schema.Alerts.STOP_ID + "=\'" + stopId + "\' AND " + Schema.Alerts.ROUTE_ID + "=\'" + currentRouteId + "\'";
            Cursor cursor = getActivity().getContentResolver().query(MITShuttlesProvider.ALERTS_URI, Schema.Alerts.ALL_COLUMNS, selection, null, null);
            if (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(Schema.Alerts.ID_COL));
                cancelAlarm((int) id);
                Timber.d("Deleted alarm with ID_COL = " + id);
                getActivity().getContentResolver().delete(MITShuttlesProvider.ALERTS_URI, selection, null);
            }
            cursor.close();

            if (newAlert) {
                // Save alert in DB
                MITAlert alert = new MITAlert(currentRouteId, stopId, prediction.getVehicleId(), prediction.getTimestamp());
                DBAdapter.getInstance().acquire(alert);
                long id = alert.persistToDatabase();

                String title = getRouteTitle();
                String description = buildAlertDescription(title);

                Timber.d("Created alarm with ID_COL = " + id);
                buildNotification(prediction.getSeconds(), (int) id, description);
            }
        }
    }
}
