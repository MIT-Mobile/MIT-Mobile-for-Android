package edu.mit.mitmobile2.dining.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.List;

import edu.mit.mitmobile2.dining.fragments.HouseMenuFragment;
import edu.mit.mitmobile2.dining.model.MITDiningMeal;


public class HouseMenuPagerAdapter extends FragmentStatePagerAdapter {

    private List<MITDiningMeal> meals;
    private HouseMenuFragment[] fragments;

    public HouseMenuPagerAdapter(FragmentManager fm, List<MITDiningMeal> meals) {
        super(fm);
        fragments = new HouseMenuFragment[meals.size()];
        this.meals = meals;
    }
    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        HouseMenuFragment fragment = HouseMenuFragment.newInstance(meals.get(position));
        fragments[position] = fragment;
        return fragment;
    }

    @Override
    public int getCount() {
        return meals.size();
    }
}