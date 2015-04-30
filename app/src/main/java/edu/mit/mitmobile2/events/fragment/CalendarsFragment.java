package edu.mit.mitmobile2.events.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.EventManager;
import edu.mit.mitmobile2.events.adapters.CalendarsAdapter;
import edu.mit.mitmobile2.events.model.MITCalendar;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static butterknife.ButterKnife.inject;

/**
 * Created by serg on 4/28/15.
 */
public class CalendarsFragment extends Fragment {

    @InjectView(R.id.event_calendars_list)
    ExpandableListView calendarsListView;

    private List<MITCalendar> mitCalendars;
    private CalendarsAdapter adapter;
    private ExpandableListViewClickListener expandableListViewClickListener;

    public static CalendarsFragment newInstance() {
        CalendarsFragment fragment = new CalendarsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_calendars, container, false);
        initializeComponents(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.Events.CALENDARS)) {
            mitCalendars = savedInstanceState.getParcelableArrayList(Constants.Events.CALENDARS);
        } else {
            mitCalendars = new ArrayList<>();

            getCalendars();
        }

        adapter = new CalendarsAdapter(getActivity().getApplicationContext(), mitCalendars);
        expandableListViewClickListener = new ExpandableListViewClickListener();

        calendarsListView.setAdapter(adapter);
        calendarsListView.setOnGroupClickListener(expandableListViewClickListener);
        calendarsListView.setOnChildClickListener(expandableListViewClickListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_calendars, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done: {
                // TODO: save selected one here
                getActivity().finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.Events.CALENDARS, (ArrayList<? extends Parcelable>) mitCalendars);
    }

    private void getCalendars() {
        EventManager.getCalendars(getActivity(), new Callback<List<MITCalendar>>() {
            @Override
            public void success(List<MITCalendar> mitCalendars, Response response) {
                CalendarsFragment.this.mitCalendars.clear();

                if (mitCalendars != null) {
                    CalendarsFragment.this.mitCalendars.addAll(mitCalendars);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
//                LoggingManager.Log.d(TAG, "ERROR => " + error);
            }
        });
    }

    private void initializeComponents(View view) {
        inject(this, view);
    }

    private class ExpandableListViewClickListener implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            if (adapter.isCheckable(groupPosition, childPosition)) {
                MITCalendar checkedCalendar = adapter.getChild(groupPosition, childPosition);
                adapter.setCheckedCalendar(checkedCalendar);
            }
            return false;
        }

        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            if (adapter.isCheckable(groupPosition)) {
                MITCalendar checkedCalendar = adapter.getGroup(groupPosition);
                adapter.setCheckedCalendar(checkedCalendar);
            }
            return false;
        }
    }
}
