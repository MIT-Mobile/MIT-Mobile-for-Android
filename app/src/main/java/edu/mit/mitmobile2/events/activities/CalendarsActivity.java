package edu.mit.mitmobile2.events.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.fragment.CalendarsFragment;

/**
 * Created by serg on 4/28/15.
 */
public class CalendarsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_calendars);

        if (savedInstanceState == null) {
            CalendarsFragment fragment = CalendarsFragment.newInstance();

            getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }
    }
}
