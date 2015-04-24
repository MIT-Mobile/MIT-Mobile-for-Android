package edu.mit.mitmobile2.events.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.Calendar;

import edu.mit.mitmobile2.events.fragment.CalendarWeekFragment;

public class CalendarWeekPagerAdapter extends FragmentStatePagerAdapter {


    public static final int SIZE = 2000;

    private CalendarWeekFragment[] fragments = new CalendarWeekFragment[SIZE];
    private Calendar startPoint;
    private int positionInWeek = -1;
    private int fragmentPosition;

    public CalendarWeekPagerAdapter(FragmentManager fm, Calendar startPoint) {
        super(fm);
        this.startPoint = startPoint;
        this.positionInWeek = this.startPoint.get(Calendar.DAY_OF_WEEK) - 1;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            int diff = position - (SIZE / 2); // positive or negative
            CalendarWeekFragment fragment = CalendarWeekFragment.newInstance(startPoint, diff, this.positionInWeek);
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    public void setFragmentPosition(int fragmentPosition) {
        this.fragmentPosition = fragmentPosition;
    }

    public void setPositionInWeek(int positionInWeek) {
        this.positionInWeek = positionInWeek;
        CalendarWeekFragment fragmentBefore = fragments[fragmentPosition - 1];
        if (fragmentBefore != null) {
            fragmentBefore.setNewPositionSelected(positionInWeek);
        }
        CalendarWeekFragment fragmentAfter = fragments[fragmentPosition + 1];
        if (fragmentAfter != null) {
            fragmentAfter.setNewPositionSelected(positionInWeek);
        }
    }
}
