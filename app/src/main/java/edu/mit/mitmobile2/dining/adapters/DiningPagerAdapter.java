package edu.mit.mitmobile2.dining.adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import edu.mit.mitmobile2.dining.fragments.HouseDiningFragment;
import edu.mit.mitmobile2.dining.fragments.RetailFragment;

/**
 * Created by serg on 5/8/15.
 */
public class DiningPagerAdapter extends FragmentPagerAdapter {

    public DiningPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return HouseDiningFragment.newInstance();
            case 1: return RetailFragment.newInstance();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
