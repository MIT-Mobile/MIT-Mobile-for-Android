package edu.mit.mitmobile2.events.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import edu.mit.mitmobile2.events.fragment.CalendarDayFragment;

public class CalendarDayPagerAdapter extends FragmentStatePagerAdapter {

    public static final int SIZE = 2000;

    private CalendarDayFragment[] fragments = new CalendarDayFragment[SIZE];
    private CalendarWeekPagerAdapter weekPagerAdapterReference;
    private int lastSeenPosition;

    public CalendarDayPagerAdapter(FragmentManager fm) {
        super(fm);
        lastSeenPosition = SIZE / 2;
    }

    @Override
    public CalendarDayFragment getItem(int position) {
        if (fragments[position] == null) {
            int diff = position - (SIZE / 2); // positive or negative
            CalendarDayFragment fragment = CalendarDayFragment.newInstance(weekPagerAdapterReference.getStartPoint(), diff);
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

    public void setWeekPagerAdapterReference(CalendarWeekPagerAdapter weekPagerAdapterReference) {
        this.weekPagerAdapterReference = weekPagerAdapterReference;
    }

    public int getLastSeenPosition() {
        return lastSeenPosition;
    }

    public void setLastSeenPosition(int lastSeenPosition) {
        this.lastSeenPosition = lastSeenPosition;
    }
}
