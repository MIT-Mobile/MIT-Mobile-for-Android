package edu.mit.mitmobile2.events.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.fragment.CalendarAcademicFragment;
import edu.mit.mitmobile2.events.fragment.CalendarHolidaysFragment;
import edu.mit.mitmobile2.events.fragment.CalendarsFragment;
import edu.mit.mitmobile2.events.model.MITCalendar;

/**
 * Created by serg on 4/28/15.
 */
public class CalendarsActivity extends AppCompatActivity implements CalendarsFragment.OnCalendarsFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_calendars);

        if (savedInstanceState == null) {
            CalendarsFragment fragment = CalendarsFragment.newInstance();

            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }
    }

    @Override
    public void onDone() {
        finish();
    }

    @Override
    public void onAcademicCalendarSelected(MITCalendar calendar) {
        CalendarAcademicFragment fragment = CalendarAcademicFragment.newInstance(calendar);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onHolidaysCalendarSelected(MITCalendar calendar) {
        CalendarHolidaysFragment fragment = CalendarHolidaysFragment.newInstance(calendar);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
    }
}
