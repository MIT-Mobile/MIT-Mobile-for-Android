package edu.mit.mitmobile2.events.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.EventManager;
import edu.mit.mitmobile2.events.model.MITCalendar;
import edu.mit.mitmobile2.events.model.MITCalendarEvent;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CalendarDayFragment extends Fragment {

    public CalendarDayFragment() {
    }

    public static CalendarDayFragment newInstance() {
        return new CalendarDayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_day, null);

        MITCalendar calendar = new MITCalendar();
        calendar.setIdentifier("events_calendar");
        EventManager.getCalendarEvents(getActivity(), calendar, new Callback<List<MITCalendarEvent>>() {
            @Override
            public void success(List<MITCalendarEvent> mitCalendarEvents, Response response) {
                LoggingManager.Timber.d("Worked!");
            }

            @Override
            public void failure(RetrofitError error) {
                LoggingManager.Timber.e(error, "Failed");
            }
        });
        return view;
    }


}
