package edu.mit.mitmobile2.shuttles.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import edu.mit.mitmobile2.shuttles.ShuttleStopViewPagerFragment;
import edu.mit.mitmobile2.shuttles.model.MITShuttleStopWrapper;

public class ShuttleStopViewPagerAdapter extends FragmentPagerAdapter{

    private List<MITShuttleStopWrapper> stops;
    private ShuttleStopViewPagerFragment[] fragments;

    public ShuttleStopViewPagerAdapter(FragmentManager fragmentManager, List<MITShuttleStopWrapper> stops) {
        super(fragmentManager);
        this.stops = stops;
        fragments = new ShuttleStopViewPagerFragment[stops.size()];
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            ShuttleStopViewPagerFragment fragment = ShuttleStopViewPagerFragment.newInstance(stops.get(position));
            fragments[position] = fragment;
            return fragment;
        } else {
            return fragments[position];
        }
    }

    @Override
    public int getCount() {
        return stops.size();
    }
}
