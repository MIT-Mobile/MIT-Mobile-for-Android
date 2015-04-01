package edu.mit.mitmobile2.shuttles.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.PreferenceUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.shuttles.MITShuttlesProvider;
import edu.mit.mitmobile2.shuttles.callbacks.ShuttleAdapterCallback;
import edu.mit.mitmobile2.shuttles.utils.ShuttlesDatabaseHelper;
import edu.mit.mitmobile2.shuttles.adapter.MITShuttleAdapter;
import edu.mit.mitmobile2.shuttles.model.MitMiniShuttleRoute;
import timber.log.Timber;

public class ShuttlesActivity extends MITModuleActivity implements ShuttleAdapterCallback, LoaderManager.LoaderCallbacks<Cursor> {

    int contentLayoutId = R.layout.content_shuttles;

    private static final int PREDICTIONS_PERIOD = 20000;
    private static final int PREDICTIONS_TIMER_OFFSET = 10000;
    private static final int PREDICTIONS_TIMEOUT = 60000;
    private static final int ROUTES_TIMEOUT = 80000;

    private int loopCount = 0;
    private boolean immediatelyReloadPredictions = false;
    private boolean blockServerCalls = false;

    private MITShuttleAdapter mitShuttleAdapter;
    private ArrayList<MitMiniShuttleRoute> mitShuttleRoutes = new ArrayList<>();

    @InjectView(R.id.shuttle_refresh_layout)
    SwipeRefreshLayout shuttleRefreshLayout;
    @InjectView(R.id.shuttle_listview)
    ListView shuttleListView;

    private static Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setContentLayoutId(R.layout.content_shuttles);
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        if (savedInstanceState != null) {
            mitShuttleRoutes = savedInstanceState.getParcelableArrayList("routes");
            mitShuttleAdapter = new MITShuttleAdapter(this, mitShuttleRoutes, null);
        } else {
            mitShuttleAdapter = new MITShuttleAdapter(this, new ArrayList<MitMiniShuttleRoute>(), null);
        }
        shuttleListView.setAdapter(mitShuttleAdapter);
        initialShuttleView();

        shuttleRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shuttleRefreshLayout.setRefreshing(true);
                updateAllRoutes();
                immediatelyReloadPredictions = true;
                loopCount = 0;
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void loadCursor() {
        Cursor cursor = getContentResolver().query(MITShuttlesProvider.ALL_ROUTES_URI, Schema.Route.ALL_COLUMNS, null, null, null);

        List<MitMiniShuttleRoute> routes = new ArrayList<>();
        ShuttlesDatabaseHelper.generateMiniRouteObjects(routes, cursor);
        cursor.close();

        if (routes.size() > 0) {
            routes = sortRoutesByStatus(routes);
        }

        mitShuttleAdapter.updateListItems(routes);
    }

    private void checkStatusOfDatabase() {
        long routesTimestamp = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(this).getLong(Constants.ROUTES_TIMESTAMP, 0);
        long diff = System.currentTimeMillis() - routesTimestamp;
        if (diff < ROUTES_TIMEOUT) {
            long predictionsTimestamp = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(this).getLong(Constants.PREDICTIONS_TIMESTAMP, 0);
            if ((System.currentTimeMillis() - predictionsTimestamp) < PREDICTIONS_TIMEOUT) {
                // load route info WITH preference data
                loadCursor();
                Timber.d("Predictions OK");
            } else {
                ShuttlesDatabaseHelper.clearAllPredictions();
                loadCursor();
                updatePredictions();
                Timber.d("Routes OK, refreshing predictions");
            }
        } else {
            DBAdapter.getInstance().flushStaleData();
            loadCursor();
            updateAllRoutes();
            immediatelyReloadPredictions = true;
            Timber.d("Refreshing routes");
        }
    }

    private void startTimerTask() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mitShuttleAdapter.getCount() == 0) {
                    return;
                }

                if (timer == null) {
                    return;
                }

                if (blockServerCalls) {
                    return;
                }

                Timber.d("Timer fired");

                if (loopCount == 5) {
                    updateAllRoutes();
                    immediatelyReloadPredictions = true;
                } else {
                    updatePredictions();
                }

                loopCount++;
                loopCount = loopCount % 6;
            }
        }, PREDICTIONS_TIMER_OFFSET, PREDICTIONS_PERIOD);
    }

    private void updatePredictions() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Shuttles.MODULE_KEY, Constants.SHUTTLES);
        bundle.putString(Constants.Shuttles.PATH_KEY, Constants.Shuttles.PREDICTIONS_PATH);
        bundle.putString(Constants.Shuttles.URI_KEY, MITShuttlesProvider.PREDICTIONS_URI.toString());

        String mitTuples = mitShuttleAdapter.getRouteStopTuples("mit");
        String crTuples = mitShuttleAdapter.getRouteStopTuples("charles-river");

        bundle.putString(Constants.Shuttles.MIT_TUPLES_KEY, mitTuples);
        bundle.putString(Constants.Shuttles.CR_TUPLES_KEY, crTuples);

        // FORCE THE SYNC
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        Timber.d("Requesting Predictions");

        ContentResolver.requestSync(MitMobileApplication.mAccount, MitMobileApplication.AUTHORITY, bundle);
        blockServerCalls = true;
    }

    private void updateAllRoutes() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Shuttles.MODULE_KEY, Constants.SHUTTLES);
        bundle.putString(Constants.Shuttles.PATH_KEY, Constants.Shuttles.ALL_ROUTES_PATH);
        bundle.putString(Constants.Shuttles.URI_KEY, MITShuttlesProvider.ALL_ROUTES_URI.toString());

        // FORCE THE SYNC
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        // Request All Routes info
        ContentResolver.requestSync(MitMobileApplication.mAccount, MitMobileApplication.AUTHORITY, bundle);
        blockServerCalls = true;
    }

    private void initialShuttleView() {
        View footer = ((LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.shuttle_list_footer, null, false);
        initialListViewFooter(footer);
        shuttleListView.addFooterView(footer);
    }

    public List<MitMiniShuttleRoute> sortRoutesByStatus(List<MitMiniShuttleRoute> routes) {
        List<MitMiniShuttleRoute> shuttleRouteStatusInService = new ArrayList<>();
        List<MitMiniShuttleRoute> shuttleRouteStatusNotInservice = new ArrayList<>();
        List<MitMiniShuttleRoute> shuttleRouteStatusUnknown = new ArrayList<>();

        for (MitMiniShuttleRoute route : routes) {
            if (route.isPredictable()) {
                shuttleRouteStatusInService.add(route);
            } else if (route.isScheduled()) {
                shuttleRouteStatusUnknown.add(route);
            } else {
                shuttleRouteStatusNotInservice.add(route);
            }
        }

        routes.clear();

        for (MitMiniShuttleRoute shuttleRoute : shuttleRouteStatusInService) {
            routes.add(shuttleRoute);
        }
        for (MitMiniShuttleRoute shuttleRoute : shuttleRouteStatusUnknown) {
            routes.add(shuttleRoute);
        }
        for (MitMiniShuttleRoute shuttleRoute : shuttleRouteStatusNotInservice) {
            routes.add(shuttleRoute);
        }

        for (MitMiniShuttleRoute shuttleRoute : routes) {
            mitShuttleRoutes.add(shuttleRoute);
        }

        return routes;
    }

    public void initialListViewFooter(View footer) {
        final RelativeLayout parkOffice = (RelativeLayout) footer.findViewById(R.id.park_office);
        final RelativeLayout saferide = (RelativeLayout) footer.findViewById(R.id.saferide);
        final LinearLayout realTimeBusArrivals = (LinearLayout) footer.findViewById(R.id.real_time_bus_arrivals);
        final LinearLayout realTimeTrainArribals = (LinearLayout) footer.findViewById(R.id.real_time_train_arrivals);
        final LinearLayout googleTransit = (LinearLayout) footer.findViewById(R.id.google_transit);

        parkOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneCallDialog(getResources().getString(R.string.parking_office_number));
            }
        });
        saferide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneCallDialog(getResources().getString(R.string.saferide_number));
            }
        });
        realTimeBusArrivals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMBTAUrl(getResources().getString(R.string.real_time_bus_url));
            }
        });
        realTimeTrainArribals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMBTAUrl(getResources().getString(R.string.real_time_train_url));
            }
        });
        googleTransit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMBTAUrl(getResources().getString(R.string.real_time_google_transit_url));
            }
        });
    }

    public void phoneCallDialog(final String phoneNumber) {
        String[] splittedPhoneNumber = phoneNumber.split("\\.");
        new AlertDialog.Builder(this)
                .setMessage("Call 1 (" + splittedPhoneNumber[0] + ")" + splittedPhoneNumber[1] + "-" + splittedPhoneNumber[2] + "?")
                .setPositiveButton(getResources().getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPhoneCall(phoneNumber);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void setPhoneCall(String phoneNumber) {
        String uri = "tel:" + phoneNumber.trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    public void openMBTAUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. " +
                    "Please install a map app.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void shuttleRouteClick(final String routeId) {
        Intent intent = new Intent(this, ShuttleRouteActivity.class);
        intent.putExtra(Constants.ROUTE_ID_KEY, routeId);
        startActivity(intent);
    }

    @Override
    public void shuttleStopClick(String routeId, String stopId) {
        Intent intent = new Intent(this, ShuttleStopActivity.class);
        intent.putExtra(Constants.ROUTE_ID_KEY, routeId);
        intent.putExtra(Constants.STOP_ID_KEY, stopId);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            return;
        }

        Timber.d("Notified!");
        blockServerCalls = false;

        if (!immediatelyReloadPredictions) {
            List<MitMiniShuttleRoute> routes = new ArrayList<>();
            ShuttlesDatabaseHelper.generateMiniRouteObjects(routes, data);
            routes = sortRoutesByStatus(routes);
            mitShuttleAdapter.updateListItems(routes);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (shuttleRefreshLayout.isRefreshing()) {
                        shuttleRefreshLayout.setRefreshing(false);
                    }
                }
            });
        } else {
            immediatelyReloadPredictions = false;
            updatePredictions();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStatusOfDatabase();
        timer = new Timer();
        startTimerTask();
    }

    @Override
    protected void onPause() {
        timer.cancel();
        timer.purge();
        timer = null;
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("routes", mitShuttleRoutes);
    }
}
