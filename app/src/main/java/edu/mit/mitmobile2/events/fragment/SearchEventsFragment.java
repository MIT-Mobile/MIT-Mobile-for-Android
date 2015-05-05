package edu.mit.mitmobile2.events.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.EventManager;
import edu.mit.mitmobile2.events.adapters.CalendarEventAdapter;
import edu.mit.mitmobile2.events.adapters.CalendarSearchEventAdapter;
import edu.mit.mitmobile2.events.adapters.SearchRecentAdapter;
import edu.mit.mitmobile2.events.model.MITCalendar;
import edu.mit.mitmobile2.events.model.MITCalendarEvent;
import edu.mit.mitmobile2.news.models.MITNewsCategory;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class SearchEventsFragment extends Fragment implements SearchRecentAdapter.SearchEventsInteractionListener {

    @InjectView(R.id.recent_search_listview)
    ListView recentSearchListView;

    @InjectView(R.id.events_listview)
    StickyListHeadersListView eventsListView;

    @InjectView(R.id.no_results_textview)
    TextView noResultsTextView;

    private static final String EVENTS_SEARCH_HISTORY = "eventsSearchHistory";
    public static final String SEARCH_RESULT = "searchResult";
    public static final String SEARCH_QUERY = "searchQuery";

    private MITAPIClient apiClient;

    private List<String> recentSearches;
    private SharedPreferences sharedPreferences;
    private SearchRecentAdapter searchAdapter;

    private List<MITCalendarEvent> mitCalendarEvents;
    private CalendarSearchEventAdapter eventsAdapter;

    private MITCalendar allEventsCategory;
    private MITCalendar filterCategory;

    public SearchEventsFragment() {
        // Required empty public constructor
    }

    public static SearchEventsFragment newInstance() {
        SearchEventsFragment fragment = new SearchEventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_search, container, false);
        this.setHasOptionsMenu(true);
        ButterKnife.inject(this, view);

        apiClient = new MITAPIClient(getActivity());

        recentSearches = new ArrayList<>();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Set<String> set = sharedPreferences.getStringSet(EVENTS_SEARCH_HISTORY, null);
        if (set != null) {
            recentSearches.addAll(set);
        }

        // TODO: set as parameters
        allEventsCategory = new MITCalendar();
        allEventsCategory.setIdentifier("19");

        filterCategory = new MITCalendar();
        filterCategory.setIdentifier("8");
        filterCategory.setName("Cinema");

        searchAdapter = new SearchRecentAdapter(getActivity().getApplicationContext(), recentSearches, filterCategory, this);
        recentSearchListView.setAdapter(searchAdapter);

        mitCalendarEvents = new ArrayList<>();
        eventsAdapter = new CalendarSearchEventAdapter(getActivity().getApplicationContext(), mitCalendarEvents);
        eventsListView.setAdapter(eventsAdapter);
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: navigate to event details
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);

        final MenuItem menuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setQueryHint(getString(R.string.search_hint));
        menuItem.expandActionView();

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                EventsFragment eventsFragment = new EventsFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, eventsFragment).commit();
                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                eventsListView.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSearchResult(query);
                SharedPreferences.Editor edior = sharedPreferences.edit();
                boolean queryExisted = false;
                for (String s : recentSearches) {
                    if (s.equals(query)) {
                        queryExisted = true;
                        break;
                    }
                }
                if (!queryExisted) {
                    recentSearches.add(query);
                    Set<String> set = new HashSet<String>(recentSearches);
                    edior.putStringSet(EVENTS_SEARCH_HISTORY, set);
                    edior.commit();

                    searchAdapter.notifyDataSetChanged();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recentSearchListView.setVisibility(View.VISIBLE);
                noResultsTextView.setVisibility(View.GONE);
                searchAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onRecentSearchSelected(String query) {
        getSearchResult(query);
    }

    @Override
    public void onClearFilter(MITCalendar filterCategory) {
        filterCategory = null;
        searchAdapter.setFilterCalendar(null);
    }

    private void getSearchResult(final String searchText) {
        MITCalendar eventCalendar = new MITCalendar();
        eventCalendar.setIdentifier(MITCalendar.EVENTS_CALENDAR_ID);

        MITCalendar category = (filterCategory == null) ? allEventsCategory : filterCategory;

        EventManager.getCalendarEvents(getActivity(), eventCalendar, category, searchText, new Callback<List<MITCalendarEvent>>() {
            @Override
            public void success(List<MITCalendarEvent> mitCalendarEvents, Response response) {
                if (mitCalendarEvents != null) {
                    SearchEventsFragment.this.mitCalendarEvents.clear();
                    SearchEventsFragment.this.mitCalendarEvents.addAll(mitCalendarEvents);
                }

                eventsListView.setVisibility(View.VISIBLE);
                eventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
