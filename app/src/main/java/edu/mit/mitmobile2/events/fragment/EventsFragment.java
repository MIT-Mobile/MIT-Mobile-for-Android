package edu.mit.mitmobile2.events.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.Calendar;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.adapters.CalendarWeekPagerAdapter;

public class EventsFragment extends Fragment {

    public static final long WEEK_OFFSET = 604800000;
    private TextView dateTextView;
    private CalendarWeekPagerAdapter adapter;

    public EventsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_events, null);

        dateTextView = (TextView) view.findViewById(R.id.date_text);

        final ViewPager calendarWeekViewPager = (ViewPager) view.findViewById(R.id.calendar_viewpager);
        adapter = new CalendarWeekPagerAdapter(getFragmentManager(), Calendar.getInstance());
        calendarWeekViewPager.setAdapter(adapter);
        calendarWeekViewPager.setCurrentItem(CalendarWeekPagerAdapter.SIZE / 2); // put it in the middle

        calendarWeekViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                adapter.setFragmentPosition(position);
                String date = adapter.getDate();
                dateTextView.setText(date);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        dateTextView.setText(DateFormat.format("MMMM d, yyyy", Calendar.getInstance().getTime()));

        return view;
    }

    @Subscribe
    public void dateHasChanged(OttoBusEvent.ChangeDateTextEvent event) {
        String date = event.getDateText();
        dateTextView.setText(date);
        adapter.update(event.getPosition());
    }

    @Override
    public void onPause() {
        MitMobileApplication.bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        MitMobileApplication.bus.register(this);
    }
}
