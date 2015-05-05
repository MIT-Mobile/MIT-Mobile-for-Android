package edu.mit.mitmobile2.events.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.EventManager;
import edu.mit.mitmobile2.events.adapters.CalendarHolidaysAdapter;
import edu.mit.mitmobile2.events.model.MITCalendar;
import edu.mit.mitmobile2.events.model.MITCalendarEvent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static butterknife.ButterKnife.inject;

/**
 * Created by serg on 4/30/15.
 */
public class CalendarHolidaysFragment extends Fragment {

    @InjectView(R.id.list_holidays)
    ListView academicListView;

    private MITCalendar calendarHolidays;
    private List<MITCalendarEvent> mitCalendarEvents;
    private CalendarHolidaysAdapter adapter;

    public static CalendarHolidaysFragment newInstance(MITCalendar academicCalendar) {
        CalendarHolidaysFragment fragment = new CalendarHolidaysFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.Events.CALENDAR, academicCalendar);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_holidays, container, false);
        initializeComponents(view);
        setHasOptionsMenu(true);

        if (getArguments() != null && getArguments().containsKey(Constants.Events.CALENDAR)) {
            calendarHolidays = getArguments().getParcelable(Constants.Events.CALENDAR);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            mitCalendarEvents = new ArrayList<>();
            getCalendarEvents(calendarHolidays);
        } else if (savedInstanceState.containsKey(Constants.Events.CALENDAR)) {
            calendarHolidays = savedInstanceState.getParcelable(Constants.Events.CALENDAR);
            mitCalendarEvents = savedInstanceState.getParcelableArrayList(Constants.Events.EVENTS);
        }

        adapter = new CalendarHolidaysAdapter(mitCalendarEvents);
        academicListView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().setTitle(calendarHolidays.getName());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.Events.CALENDAR, calendarHolidays);
        outState.putParcelableArrayList(Constants.Events.EVENTS, (ArrayList<? extends Parcelable>) mitCalendarEvents);
    }

    private void initializeComponents(View view) {
        inject(this, view);
    }

    private void getCalendarEvents(final MITCalendar calendarAcademic) {
        EventManager.getCalendarEvents(getActivity(), calendarAcademic, new Callback<List<MITCalendarEvent>>() {

            @Override
            public void success(List<MITCalendarEvent> mitCalendarEvents, Response response) {
                CalendarHolidaysFragment.this.mitCalendarEvents.clear();
                if (mitCalendarEvents != null) {
                    CalendarHolidaysFragment.this.mitCalendarEvents.addAll(mitCalendarEvents);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }
}
