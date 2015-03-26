package edu.mit.mitmobile2.shuttles.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.List;

import edu.mit.mitmobile2.shuttles.ShuttleStopViewPagerFragment;
import edu.mit.mitmobile2.shuttles.model.MITShuttlePrediction;

public class ShuttleStopViewPagerAdapter extends FragmentPagerAdapter {

    private List<String> stopIds;
    private ShuttleStopViewPagerFragment[] fragments;

    public ShuttleStopViewPagerAdapter(FragmentManager fragmentManager, List<String> stopIds) {
        super(fragmentManager);
        this.stopIds = stopIds;
        fragments = new ShuttleStopViewPagerFragment[stopIds.size()];
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            ShuttleStopViewPagerFragment fragment = ShuttleStopViewPagerFragment.newInstance(stopIds.get(position));
            fragments[position] = fragment;
            return fragment;
        } else {
            return fragments[position];
        }
    }

    @Override
    public int getCount() {
        return stopIds.size();
    }

    public void updatePredictions(int position, List<MITShuttlePrediction> predictions) {
        if (fragments[position] != null) {
            fragments[position].updatePredictions(predictions);
        }
    }
}
