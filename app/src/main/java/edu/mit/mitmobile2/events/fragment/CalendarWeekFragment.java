package edu.mit.mitmobile2.events.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectViews;
import butterknife.OnClick;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.UpdateableFragment;

public class CalendarWeekFragment extends Fragment implements UpdateableFragment {

    private static final String CALENDAR = "calendar";
    private static final String DIFFERENCE = "diff";
    private static final String POSITION = "position";
    private static final String SELECTED_DATE = "selectedDate";
    public static final String DATE_FORMAT = "MMMM d, yyyy";

    private List<Date> dates = new ArrayList<>();

    @InjectViews({R.id.sunday, R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday})
    List<LinearLayout> views;

    public CalendarWeekFragment() {
    }

    public static CalendarWeekFragment newInstance(Calendar calendar, int diff, int position) {
        CalendarWeekFragment fragment = new CalendarWeekFragment();
        Bundle args = new Bundle();
        args.putSerializable(CALENDAR, calendar);
        args.putInt(DIFFERENCE, diff);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_week_fragment, null);

        ButterKnife.inject(this, view);

        Calendar calendar = new GregorianCalendar();
        Calendar startPoint = (Calendar) getArguments().getSerializable(CALENDAR);
        int diff = getArguments().getInt(DIFFERENCE);
        int position = getArguments().getInt(POSITION);

        // Can either be positive or negative here
        calendar.setTimeInMillis(startPoint.getTimeInMillis() + (diff * EventsFragment.WEEK_OFFSET));

        for (int i = 0; i < views.size(); i++) {
            LinearLayout layout = views.get(i);

            TextView day = (TextView) layout.findViewById(R.id.calendar_day);
            TextView date = (TextView) layout.findViewById(R.id.calendar_date);

            day.setText(getResources().getStringArray(R.array.days)[i]);

            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + i);
            dates.add(calendar.getTime());
            String s = (String) DateFormat.format("d", calendar.getTime());
            date.setText(s);

            setUIForDate(calendar, position, i, date);
        }

        return view;
    }

    private boolean dayIsToday(Calendar calendar, Calendar c) {
        return (calendar.get(Calendar.DAY_OF_YEAR) == c.get(Calendar.DAY_OF_YEAR)) &&
                (calendar.get(Calendar.YEAR) == c.get(Calendar.YEAR));
    }

    @OnClick({R.id.sunday, R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday})
    void dateClicked(LinearLayout layout) {
        int position = views.indexOf(layout);
        Date d = dates.get(position);
        String formatted = (String) DateFormat.format(DATE_FORMAT, d);

        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(System.currentTimeMillis());

        String todayFormatted = (String) DateFormat.format(DATE_FORMAT, c.getTime());

        for (int i = 0; i < views.size(); i++) {
            resetSelection(todayFormatted, i);
        }

        TextView date = (TextView) layout.findViewById(R.id.calendar_date);
        setSelectionForSingleItem(formatted, todayFormatted, date);

        MitMobileApplication.bus.post(new OttoBusEvent.ChangeDateTextEvent(formatted, position));
    }

    private void setSelectionForSingleItem(String formatted, String todayFormatted, TextView date) {
        if (!todayFormatted.equals(formatted)) {
            date.setTextColor(getResources().getColor(R.color.white));
            date.setBackgroundResource(R.drawable.black_circle);
        } else {
            date.setBackgroundResource(R.drawable.red_circle_small);
            date.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void resetSelection(String todayFormatted, int i) {
        TextView date = (TextView) views.get(i).findViewById(R.id.calendar_date);
        if (!todayFormatted.equals(DateFormat.format(DATE_FORMAT, dates.get(i).getTime()))) {
            date.setTextColor(getResources().getColor(R.color.black));
            date.setBackgroundResource(R.drawable.white_circle);
        } else {
            date.setBackgroundResource(R.drawable.white_circle);
            date.setTextColor(getResources().getColor(R.color.mit_red));
        }
    }

    @Override
    public void update(final int position) {
        getArguments().putInt(POSITION, position);
        if (getActivity() != null) {
            updateUI(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (views != null) {
            updateUI(getArguments().getInt(POSITION));
        }
    }

    private void updateUI(int position) {
        Calendar calendar = new GregorianCalendar(); //(Calendar) getArguments().getSerializable(CALENDAR);
        int diff = getArguments().getInt(DIFFERENCE);

        // Can either be positive or negative here
        Calendar startPoint = (Calendar) getArguments().getSerializable(CALENDAR);
        calendar.setTimeInMillis(startPoint.getTimeInMillis() + (diff * EventsFragment.WEEK_OFFSET));

        for (int i = 0; i < views.size(); i++) {
            LinearLayout layout = views.get(i);

            TextView day = (TextView) layout.findViewById(R.id.calendar_day);
            TextView date = (TextView) layout.findViewById(R.id.calendar_date);

            day.setText(getResources().getStringArray(R.array.days)[i]);

            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + i);
            String s = (String) DateFormat.format("d", calendar.getTime());
            date.setText(s);

            setUIForDate(calendar, position, i, date);
        }
    }

    public String getDate() {
        Calendar c = (Calendar) getArguments().getSerializable(SELECTED_DATE);
        Calendar date = new GregorianCalendar();
        date.setTimeInMillis(c.getTimeInMillis());
        date.set(Calendar.DAY_OF_WEEK, date.getFirstDayOfWeek() + getArguments().getInt(POSITION));
        return (String) DateFormat.format(DATE_FORMAT, date.getTime());
    }

    private void setUIForDate(Calendar calendar, int position, int i, TextView date) {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(System.currentTimeMillis());

        if (i == position && dayIsToday(calendar, c)) {
            date.setBackgroundResource(R.drawable.red_circle_small);
            date.setTextColor(getResources().getColor(R.color.white));
            getArguments().putSerializable(SELECTED_DATE, calendar);
        } else if (i == position) {
            date.setTextColor(getResources().getColor(R.color.white));
            date.setBackgroundResource(R.drawable.black_circle);
            getArguments().putSerializable(SELECTED_DATE, calendar);
        } else if (dayIsToday(calendar, c)) {
            date.setBackgroundResource(R.drawable.white_circle);
            date.setTextColor(getResources().getColor(R.color.mit_red));
        } else {
            date.setBackgroundResource(R.drawable.white_circle);
            date.setTextColor(getResources().getColor(R.color.black));
        }
    }
}
