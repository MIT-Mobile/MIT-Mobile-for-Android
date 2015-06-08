package edu.mit.mitmobile2.events.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.PreferenceUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.activities.EventsDetailActivity;
import edu.mit.mitmobile2.events.adapters.CalendarEventAdapter;
import edu.mit.mitmobile2.events.callback.CalendarDayCallback;
import edu.mit.mitmobile2.events.model.MITCalendar;
import edu.mit.mitmobile2.events.model.MITCalendarEvent;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CalendarDayFragment extends Fragment implements CalendarDayCallback {

    private static final String DATE = "date";
    private String calendarFilterId = "";

    private SwipeRefreshLayout refreshLayout;
    private CalendarEventAdapter adapter;

    public CalendarDayFragment() {
    }

    public static CalendarDayFragment newInstance(Calendar startPosition, int offset) {
        CalendarDayFragment calendarDayFragment = new CalendarDayFragment();

        Calendar fragmentDate = new GregorianCalendar();
        fragmentDate.setTimeInMillis(startPosition.getTimeInMillis() + (offset * EventsFragment.DAY_OFFSET));
        String dateString = (String) DateFormat.format("yyyy-MM-dd", fragmentDate.getTime());

        Bundle args = new Bundle();
        args.putString(DATE, dateString);
        calendarDayFragment.setArguments(args);
        return calendarDayFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_day, null);

        ListView listView = (ListView) view.findViewById(R.id.daily_events_list);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.devents_refresh_layout);
        refreshLayout.setEnabled(false);

        String dateString = getArguments().getString(DATE);

        adapter = new CalendarEventAdapter(getActivity(), new ArrayList<MITCalendarEvent>(), this);
        listView.setAdapter(adapter);

        SharedPreferences sharedPrefs = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(getActivity());
        filterChanged(sharedPrefs);
        getCalendarEvents(dateString);

        return view;
    }

    private void getCalendarEvents(String dateString) {
        MITAPIClient mitApiClient = new MITAPIClient(getActivity());

        HashMap<String, String> pathParams = new HashMap<>();
        pathParams.put("calendar", "events_calendar");

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("start", dateString);
        queryParams.put("end", dateString);

        if (!calendarFilterId.equals("")) {
            queryParams.put("category", calendarFilterId);
        }

        // Needs to be posted delayed because of bug in SwipeRefreshLayout
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        }, 100);

        mitApiClient.get(Constants.EVENTS, Constants.Events.CALENDAR_EVENTS_PATH, pathParams, queryParams, new Callback<List<MITCalendarEvent>>() {
            @Override
            public void success(List<MITCalendarEvent> mitEvents, Response response) {
                LoggingManager.Timber.d("Success!");
                adapter.updateEvents(mitEvents);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public boolean filterChanged(SharedPreferences sharedPrefs) {
        if (sharedPrefs.contains(Constants.CALENDAR_FILTER_KEY)) {
            MITCalendar calendar = new Gson().fromJson(sharedPrefs.getString(Constants.CALENDAR_FILTER_KEY, ""), MITCalendar.class);

            if (!calendarFilterId.equals(calendar.getIdentifier())) {
                if (calendar.getIdentifier().equals("events_calendar")) {
                    calendarFilterId = "";
                } else {
                    calendarFilterId = calendar.getIdentifier();
                }
                getActivity().setTitle(calendar.getName());
                return true;
            } else {
                getActivity().setTitle(calendar.getName());
                return false;
            }
        } else {
            getActivity().setTitle(getActivity().getString(R.string.all_events));
            return false;
        }
    }

    @Override
    public void calendarDayDetail(MITCalendarEvent calendarEvent) {
        Intent intent = new Intent(this.getActivity(), EventsDetailActivity.class);
        intent.putExtra(Constants.Events.CALENDAR_EVENT, calendarEvent);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (CalendarDayFragment.this.isVisible()) {
            SharedPreferences sharedPrefs = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(getActivity());
            if (filterChanged(sharedPrefs)) {
                getCalendarEvents(getArguments().getString(DATE));
            }
        }
    }
}
