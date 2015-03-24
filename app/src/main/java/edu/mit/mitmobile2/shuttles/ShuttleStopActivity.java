package edu.mit.mitmobile2.shuttles;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.SoloMapActivity;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopViewPagerAdapter;
import edu.mit.mitmobile2.shuttles.model.MITShuttleRoute;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

public class ShuttleStopActivity extends SoloMapActivity {

    @InjectView(R.id.prediction_viewpager)
    ViewPager predictionViewPager;

    @InjectView(R.id.transparent_map_overlay)
    View transparentView;

    private ShuttleStopViewPagerAdapter stopViewPagerAdapter;
    private MITShuttleRoute route = new MITShuttleRoute();
    private List<MITShuttleStopWrapper> stops;
    private String routeId;
    private String stopId;
    private String uriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuttle_stop);
        ButterKnife.inject(this);

        routeId = getIntent().getStringExtra(Constants.ROUTE_ID_KEY);
        stopId = getIntent().getStringExtra(Constants.STOP_ID_KEY);
        uriString = MITShuttlesProvider.STOPS_URI + "/" + stopId;
        String selectionString = Schema.Route.TABLE_NAME + "." + Schema.Stop.ROUTE_ID + "=\'" + routeId + "\'";
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
        super.updateData();
    }
}
