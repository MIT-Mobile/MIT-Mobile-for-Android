package edu.mit.mitmobile2.shuttles;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.adapter.MITShuttleAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;
import android.content.ContentResolver;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ShuttlesActivity extends MITModuleActivity implements ShuttleAdapterCallback, LoaderManager.LoaderCallbacks<Cursor> {

    int contentLayoutId = R.layout.content_shuttles;

    private static final int PREDICTIONS_PERIOD = 15000;
    private static final int PREDICTIONS_TIMER_OFFSET = 10000;

    private static int loopCount = 0;

    private MITShuttleAdapter mitShuttleAdapter;

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
        initialShuttleView();

        loadCursorInBackground();

        mitShuttleAdapter = new MITShuttleAdapter(this, new ArrayList<MITShuttleRoute>());
        shuttleListView.setAdapter(mitShuttleAdapter);

        updateAllRoutes();

        shuttleRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shuttleRefreshLayout.setRefreshing(true);
                updatePredictions();
            }
        });

        shuttleRefreshLayout.setRefreshing(true);

        getSupportLoaderManager().initLoader(0, null, this);

        timer = new Timer();
        startTimerTask();
    }

    private void loadCursorInBackground() {
        new AsyncTask<Void, Void, List<MITShuttleRoute>>() {
            @Override
            protected List<MITShuttleRoute> doInBackground(Void... params) {
                Cursor cursor = getContentResolver().query(MITShuttlesProvider.ALL_ROUTES_URI, Schema.Route.ALL_COLUMNS, null, null, null);

                List<MITShuttleRoute> routes = new ArrayList<>();
                ShuttlesDatabaseHelper.generateRouteObjects(routes, cursor);
                cursor.close();

                if (routes.size() > 0) {
                    routes = sortShuttleRoutesByStatus(routes);

                }

                return routes;
            }

            @Override
            protected void onPostExecute(List<MITShuttleRoute> routes) {
                mitShuttleAdapter.updateListItems(routes);
            }
        }.execute();
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

                if (loopCount == 5) {
                    updateAllRoutes();
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

        Timber.d("Requesting Predictions");

        ContentResolver.requestSync(MitMobileApplication.mAccount, MitMobileApplication.AUTHORITY, bundle);
    }

    private void updateAllRoutes() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Shuttles.MODULE_KEY, Constants.SHUTTLES);
        bundle.putString(Constants.Shuttles.PATH_KEY, Constants.Shuttles.ALL_ROUTES_PATH);
        bundle.putString(Constants.Shuttles.URI_KEY, MITShuttlesProvider.ALL_ROUTES_URI.toString());

        // FORCE THE SYNC - No matter what
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        // Request All Routes info
        ContentResolver.requestSync(MitMobileApplication.mAccount, MitMobileApplication.AUTHORITY, bundle);
    }

    private void initialShuttleView() {
        View footer = ((LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.shuttle_list_footer, null, false);
        initialListViewFooter(footer);
        shuttleListView.addFooterView(footer);
    }

    public List<MITShuttleRoute> sortShuttleRoutesByStatus(List<MITShuttleRoute> routes) {
        List<MITShuttleRoute> shuttleRouteStatusInService = new ArrayList<>();
        List<MITShuttleRoute> shuttleRouteStatusNotInservice = new ArrayList<>();
        List<MITShuttleRoute> shuttleRouteStatusUnknown = new ArrayList<>();

        for (MITShuttleRoute route : routes) {
            if (route.isPredictable()) {
                shuttleRouteStatusInService.add(route);
            } else if (route.isScheduled()) {
                shuttleRouteStatusUnknown.add(route);
            } else {
                shuttleRouteStatusNotInservice.add(route);
            }
        }

        routes.clear();

        for (MITShuttleRoute shuttleRoute : shuttleRouteStatusInService) {
            routes.add(shuttleRoute);
        }
        for (MITShuttleRoute shuttleRoute : shuttleRouteStatusUnknown) {
            routes.add(shuttleRoute);
        }
        for (MITShuttleRoute shuttleRoute : shuttleRouteStatusNotInservice) {
            routes.add(shuttleRoute);
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
    public void shuttleRouteClick(final String routeID) {
        Intent intent = new Intent(this, ShuttleRouteActivity.class);
        intent.putExtra("routeID", routeID);
        startActivity(intent);
    }

    @Override
    public void shuttleStopClick(String stopID) {
        Intent intent = new Intent(this, ShuttleStopActivity.class);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        MitCursorLoader cursorLoader = new MitCursorLoader(this, MITShuttlesProvider.ALL_ROUTES_URI);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.d("Notified!");
        List<MITShuttleRoute> routes = new ArrayList<>();
        ShuttlesDatabaseHelper.generateRouteObjects(routes, data);
        routes = sortShuttleRoutesByStatus(routes);
        mitShuttleAdapter.updateListItems(routes);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (shuttleRefreshLayout.isRefreshing()) {
                    shuttleRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    protected void onResume() {
        super.onResume();
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
}
