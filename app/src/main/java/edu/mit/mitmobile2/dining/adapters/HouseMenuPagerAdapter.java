package edu.mit.mitmobile2.dining.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import edu.mit.mitmobile2.dining.fragments.HouseMenuFragment;


public class HouseMenuPagerAdapter extends FragmentStatePagerAdapter {

    private static final int SIZE = 10;

    private HouseMenuFragment[] fragments = new HouseMenuFragment[SIZE];

    public HouseMenuPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        HouseMenuFragment fragment = HouseMenuFragment.newInstance();
        fragments[position] = fragment;
        return fragment;
    }

    @Override
    public int getCount() {
        return SIZE;
    }
}