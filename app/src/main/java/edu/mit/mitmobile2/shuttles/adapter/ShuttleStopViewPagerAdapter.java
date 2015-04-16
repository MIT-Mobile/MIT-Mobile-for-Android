package edu.mit.mitmobile2.shuttles.adapter;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.List;

import edu.mit.mitmobile2.EndlessFragmentStatePagerAdapter;
import edu.mit.mitmobile2.shuttles.fragment.ShuttleStopViewPagerFragment;
import edu.mit.mitmobile2.shuttles.model.MITShuttlePrediction;

public class ShuttleStopViewPagerAdapter extends EndlessFragmentStatePagerAdapter {

    private String currentRouteId;
    private List<String> stopIds;
    private boolean reuseFragments;
    private ShuttleStopViewPagerFragment[] fragments;

    public ShuttleStopViewPagerAdapter(FragmentManager fragmentManager, String currentRouteId, List<String> stopIds) {
        super(fragmentManager, stopIds.size());
        this.currentRouteId = currentRouteId;
        this.stopIds = stopIds;
        //Due to the nature of the viewpager loading the next and previous fragments, cannot reuse fragments for infinite scrolling
        //Will cause a fragment already added error
        if (realCount <= 3) {
            reuseFragments = false;
        } else {
            reuseFragments = true;
        }
        fragments = new ShuttleStopViewPagerFragment[stopIds.size()];
    }

    @Override
    public Fragment getItem(int position) {
        int realPosition = getRealPosition(position);
        if (!reuseFragments || fragments[realPosition] == null) {
            ShuttleStopViewPagerFragment fragment = ShuttleStopViewPagerFragment.newInstance(currentRouteId, stopIds.get(realPosition));
            fragments[realPosition] = fragment;
            return fragment;
        } else {
            return fragments[realPosition];
        }
    }

    public void updatePredictions(int position, List<MITShuttlePrediction> predictions) {
        int realPosition = getRealPosition(position);
        if (fragments[realPosition] != null) {
            fragments[realPosition].updatePredictions(predictions);
        }
    }
}
