package edu.mit.mitmobile2.dining.adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import edu.mit.mitmobile2.dining.fragments.HouseDiningFragment;
import edu.mit.mitmobile2.dining.fragments.RetailFragment;

/**
 * Created by serg on 5/8/15.
 */
public class DiningPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments;

    public DiningPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        initFragments();
    }

    private void initFragments() {
        fragments.clear();
        fragments.add(HouseDiningFragment.newInstance());
        fragments.add(RetailFragment.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public ArrayList<Fragment> getFragments() {
        return fragments;
    }
}
