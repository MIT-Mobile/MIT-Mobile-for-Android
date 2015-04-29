package edu.mit.mitmobile2.events.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import edu.mit.mitmobile2.events.fragment.CalendarDayFragment;

public class CalendarDayPagerAdapter extends FragmentStatePagerAdapter {

    public static final int SIZE = 2000;

    private CalendarDayFragment[] fragments = new CalendarDayFragment[SIZE];

    public CalendarDayPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            int diff = position - (SIZE / 2); // positive or negative
            CalendarDayFragment fragment = CalendarDayFragment.newInstance();
            fragments[position] = fragment;
            return fragment;
        } else {
            return fragments[position];
        }
    }

    @Override
    public int getCount() {
        return SIZE;
    }
}
