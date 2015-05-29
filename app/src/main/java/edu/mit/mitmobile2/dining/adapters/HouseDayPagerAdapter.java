package edu.mit.mitmobile2.dining.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.List;

import edu.mit.mitmobile2.dining.fragments.HouseMenuFragment;
import edu.mit.mitmobile2.dining.model.MITDiningHouseDay;


public class HouseDayPagerAdapter extends FragmentStatePagerAdapter {

    private List<MITDiningHouseDay> days;
    private HouseMenuFragment[] fragments;

    public HouseDayPagerAdapter(FragmentManager fm, List<MITDiningHouseDay> days) {
        super(fm);
        fragments = new HouseMenuFragment[days.size()];
        this.days = days;
    }
    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        HouseMenuFragment fragment = HouseMenuFragment.newInstance(days.get(position));
        fragments[position] = fragment;
        return fragment;
    }

    @Override
    public int getCount() {
        return days.size();
    }
}