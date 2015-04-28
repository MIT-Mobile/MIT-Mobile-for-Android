package edu.mit.mitmobile2.events.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import java.util.Calendar;

import edu.mit.mitmobile2.events.UpdateableFragment;
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
            CalendarWeekFragment fragment = fragments[position];
            fragment.update(positionInWeek);
            return fragment;
        }
    }

    @Override
    public int getCount() {
        return SIZE;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof UpdateableFragment) {
            ((UpdateableFragment) object).update(positionInWeek);
        }

        return super.getItemPosition(object);
    }

    public void update(int positionInWeek) {
        this.positionInWeek = positionInWeek;
        notifyDataSetChanged();
    }

    public Calendar getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Calendar startPoint) {
        this.startPoint = startPoint;
    }

    public void setFragmentPosition(int fragmentPosition) {
        this.fragmentPosition = fragmentPosition;
    }

    public String getDate() {
        CalendarWeekFragment centerFragment = fragments[fragmentPosition];
        return centerFragment.getDate();
    }
}
