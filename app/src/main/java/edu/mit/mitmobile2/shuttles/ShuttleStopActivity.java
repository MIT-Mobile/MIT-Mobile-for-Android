package edu.mit.mitmobile2.shuttles;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SoloMapActivity;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopViewPagerAdapter;

public class ShuttleStopActivity extends SoloMapActivity {

    @InjectView(R.id.prediction_viewpager)
    ViewPager predictionViewPager;

    @InjectView(R.id.transparent_map_overlay)
    View transparentView;

    private ShuttleStopViewPagerAdapter stopViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuttle_stop);
        ButterKnife.inject(this);

        stopViewPagerAdapter = new ShuttleStopViewPagerAdapter(getSupportFragmentManager(), 3);

        predictionViewPager.setAdapter(stopViewPagerAdapter);
        predictionViewPager.setCurrentItem(0);

        addTransparentView(transparentView);
    }
}
