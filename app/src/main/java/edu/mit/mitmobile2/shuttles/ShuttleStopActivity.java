package edu.mit.mitmobile2.shuttles;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.Schema;
import edu.mit.mitmobile2.SoloMapActivity;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopViewPagerAdapter;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

public class ShuttleStopActivity extends SoloMapActivity {

    @InjectView(R.id.prediction_viewpager)
    ViewPager predictionViewPager;

    @InjectView(R.id.transparent_map_overlay)
    View transparentView;

    private ShuttleStopViewPagerAdapter stopViewPagerAdapter;
    private MITShuttleStopWrapper stop;
    private String stopId;
    private String uriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuttle_stop);
        ButterKnife.inject(this);

        stopId = getIntent().getStringExtra(Constants.STOP_ID_KEY);
        uriString = MITShuttlesProvider.STOPS_URI + "/" + stopId;
        Cursor cursor = getContentResolver().query(Uri.parse(uriString), Schema.Stop.ALL_COLUMNS, Schema.Stop.STOP_ID + "=\'" + stopId + "\' ", null, null);
        cursor.moveToFirst();
        stop.buildFromCursor(cursor, MitMobileApplication.dbAdapter);
        cursor.close();

        stopViewPagerAdapter = new ShuttleStopViewPagerAdapter(getSupportFragmentManager(), 3);

        predictionViewPager.setAdapter(stopViewPagerAdapter);
        predictionViewPager.setCurrentItem(0);

        addTransparentView(transparentView);
    }
}
