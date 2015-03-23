package edu.mit.mitmobile2.shuttles.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import edu.mit.mitmobile2.shuttles.fragment.ShuttleStopViewPagerFragment;

public class ShuttleStopViewPagerAdapter extends FragmentPagerAdapter{

    private int routeNum;

    public ShuttleStopViewPagerAdapter(FragmentManager fragmentManager, int routesNum) {
        super(fragmentManager);
        this.routeNum = routesNum;
    }

    @Override
    public Fragment getItem(int position) {
        return new ShuttleStopViewPagerFragment();
    }

    @Override
    public int getCount() {
        return routeNum;
    }
}
