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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.EventManager;
import edu.mit.mitmobile2.events.adapters.CalendarAcademicAdapter;
import edu.mit.mitmobile2.events.model.MITCalendar;
import edu.mit.mitmobile2.events.model.MITCalendarEvent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static butterknife.ButterKnife.inject;

/**
 * Created by serg on 4/30/15.
 */
public class CalendarAcademicFragment extends Fragment {

    @InjectView(R.id.list_academic)
    StickyListHeadersListView academicListView;

    private MITCalendar calendarAcademic;
    private List<MITCalendarEvent> mitCalendarEvents;
    private CalendarAcademicAdapter adapter;

    public static CalendarAcademicFragment newInstance(MITCalendar academicCalendar) {
        CalendarAcademicFragment fragment = new CalendarAcademicFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.Events.CALENDAR, academicCalendar);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_academic, container, false);
        initializeComponents(view);
        setHasOptionsMenu(true);

        if (getArguments() != null && getArguments().containsKey(Constants.Events.CALENDAR)) {
            calendarAcademic = getArguments().getParcelable(Constants.Events.CALENDAR);
            mitCalendarEvents = getArguments().getParcelableArrayList(Constants.Events.EVENTS);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().setTitle(calendarAcademic.getName());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            mitCalendarEvents = new ArrayList<>();
            getCalendarEvents(calendarAcademic);
        } else if (savedInstanceState.containsKey(Constants.Events.CALENDAR)) {
            calendarAcademic = savedInstanceState.getParcelable(Constants.Events.CALENDAR);
            mitCalendarEvents = savedInstanceState.getParcelableArrayList(Constants.Events.EVENTS);
        }

        adapter = new CalendarAcademicAdapter(mitCalendarEvents);
        academicListView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.Events.CALENDAR, calendarAcademic);
        outState.putParcelableArrayList(Constants.Events.EVENTS, (ArrayList<? extends Parcelable>) mitCalendarEvents);
    }

    private void initializeComponents(View view) {
        inject(this, view);
    }

    private void getCalendarEvents(final MITCalendar calendarAcademic) {
        EventManager.getCalendarEvents(getActivity(), calendarAcademic, new Callback<List<MITCalendarEvent>>() {

            @Override
            public void success(List<MITCalendarEvent> mitCalendarEvents, Response response) {
                CalendarAcademicFragment.this.mitCalendarEvents.clear();
                if (mitCalendarEvents != null) {
                    CalendarAcademicFragment.this.mitCalendarEvents.addAll(mitCalendarEvents);
                }
                adapter.notifyDataSetChanged();

                Calendar calendar = Calendar.getInstance();

                final MITCalendarEvent nearestEvent = getEventNearest(mitCalendarEvents, calendar.getTime());
                if (nearestEvent != null) {
                    academicListView.post(new Runnable() {
                        @Override
                        public void run() {
                            academicListView.setSelection(CalendarAcademicFragment.this.mitCalendarEvents.indexOf(nearestEvent));
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private MITCalendarEvent getEventNearest(List<MITCalendarEvent> events, Date targetDate) {
        if (events == null || events.size() == 0) {
            return null;
        }
        MITCalendarEvent nearestEvent = events.get(0);
        for (MITCalendarEvent event : events) {
            if (event.getStartDate().compareTo(targetDate) <= 0) {
                if (event.getStartDate().compareTo(nearestEvent.getStartDate()) > 0) {
                    nearestEvent = event;
                }
            }
        }
        return nearestEvent;
    }
}
