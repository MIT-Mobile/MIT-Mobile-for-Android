package edu.mit.mitmobile2.people.fragment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Pair;
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

import butterknife.InjectView;
import butterknife.OnItemClick;
import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.MITSearchAdapter;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.people.PeopleDirectoryManager;
import edu.mit.mitmobile2.people.PeopleDirectoryManager.PeopleDirectoryManagerCall;
import edu.mit.mitmobile2.people.activity.PersonDetailActivity;
import edu.mit.mitmobile2.people.adapter.MITPeopleDirectoryPersonAdapter;
import edu.mit.mitmobile2.people.model.MITPerson;
import edu.mit.mitmobile2.shared.SharedActivityManager;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static butterknife.ButterKnife.inject;

public class PeopleFragment extends Fragment {
    private static final int DIRECTORY_ASSISTANCE_TAG = 1504201452;
    private static final int EMERGENCY_CONTACTS_TAG = 1504201453;
    private static final String PEOPLE_DIRECTORY_SEARCH_HISTORY = "peopleSearchHistory";

    private Mode mode;

    private PeopleDirectoryManagerCall requestRunning;

    @InjectView(R.id.quick_dial_list)
    protected ListView quickDialList;
    @InjectView(R.id.search_list)
    protected ListView searchList;
    @InjectView(R.id.recent_search_list)
    protected ListView recentSearchListView;

    @InjectView(R.id.default_layout)
    protected ViewGroup defaultLayout;

    @InjectView(R.id.favorites_list)
    protected ListView favoritesList;
    @InjectView(R.id.favorites_layout)
    protected ViewGroup favoritesLayout;

    private MITPeopleDirectoryPersonAdapter favoritePersonsAdapter;
    private MITPeopleDirectoryPersonAdapter searchListAdapter;
    private MITPeopleDirectoryPersonAdapter quickDialAdapter;
    private LinkedHashSet<String> recentSearches;
    private SharedPreferences sharedPreferences;
    private MITSearchAdapter<String> searchRecommendationsAdapter;

    private Menu menu;

    public PeopleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, null);

        initializeComponents(view);

        return view;
    }

    private void initializeComponents(View subjectView) {
        inject(this, subjectView);

        this.quickDialAdapter = new MITPeopleDirectoryPersonAdapter(quickDialItems(subjectView.getContext()));
        this.searchListAdapter = new MITPeopleDirectoryPersonAdapter();
        this.searchListAdapter.setHideIcon(true);
        this.favoritePersonsAdapter = new MITPeopleDirectoryPersonAdapter();
        this.favoritePersonsAdapter.setForceShortMode(true);

        quickDialList.setAdapter(quickDialAdapter);
        searchList.setAdapter(searchListAdapter);
        favoritesList.setAdapter(favoritePersonsAdapter);

        recentSearches = new LinkedHashSet<>();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Set<String> set = sharedPreferences.getStringSet(PEOPLE_DIRECTORY_SEARCH_HISTORY, null);

        if (set != null) {
            recentSearches.addAll(set);
        }

        this.searchRecommendationsAdapter = new MITSearchAdapter<>(getActivity(), recentSearches, new MITSearchAdapter.FragmentCallback<String>() {
            @Override
            public void itemClicked(String story) { /* No-Op */ }

            @Override
            public void itemSearch(String searchText) {
                performSearch(recentSearchListView, new Pair<>(searchRecommendationsAdapter, this), searchText);
            }
        });

        this.recentSearchListView.setAdapter(this.searchRecommendationsAdapter);
    }

    private ArrayList<MITPerson> quickDialItems(Context ctx) {
        ArrayList<MITPerson> quickDialList = new ArrayList<>(2);
        quickDialList.add(new SpecialPerson("Directory Assistance", "617.253.1000", DIRECTORY_ASSISTANCE_TAG));
        quickDialList.add(new SpecialPerson("Emergency Contacts", EMERGENCY_CONTACTS_TAG));
        return quickDialList;
    }

    private void reloadFavorites() {

    }

    @Override
    public void onStart() {
        super.onStart();

        if (mode == null) {
            this.setMode(Mode.getDefault());
        }

        updateFavoritesListVisibility();
    }

    private void updateFavoritesListVisibility() {
        boolean hasItems = PeopleDirectoryManager.getPersistantFavoritesCount() > 0;

        favoritesLayout.setVisibility(hasItems ? View.VISIBLE : View.GONE);

        favoritePersonsAdapter.updateItems(hasItems ? PeopleDirectoryManager.getPersistantFavoritesList() : new ArrayList<MITPerson>());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.menu_search, menu);

        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return performSearch(searchView, this, s);
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return searchTextChanged(searchView, this, s);
            }
        });

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (mode != Mode.NO_SEARCH) {
                    setMode(Mode.NO_SEARCH);
                }
                return true;
            }
        });

        searchView.setQueryHint(getString(R.string.people_search_hint));

        View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        View bar = searchView.findViewById(android.support.v7.appcompat.R.id.search_bar);

        //noinspection ConstantConditions IntelliJ/AndroidStudio incorrectly thinks this can never be null.
        assert searchPlate != null;

        TextView searchText = (TextView) searchPlate.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        assert searchText != null;

        searchText.setTextColor(Color.WHITE);

        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            if (this.mode != Mode.NO_SEARCH) {
                this.setMode(Mode.NO_SEARCH);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(@NonNull Mode mode) {
        if (this.mode == mode) return;

        this.mode = mode;

        recentSearchListView.setVisibility(View.GONE);

        this.searchList.setVisibility(mode.isListViewVisible() ? View.VISIBLE : View.GONE);
        this.defaultLayout.setVisibility(mode.isListViewVisible() ? View.GONE : View.VISIBLE);
    }

    @OnItemClick(R.id.favorites_list)
    protected void onFavoritesItemClicked(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), PersonDetailActivity.class);
        intent.putExtra(PersonDetailActivity.PERSON_KEY, (MITPerson) favoritePersonsAdapter.getItem(position));
        this.startActivity(intent);
    }

    @OnItemClick(R.id.search_list)
    protected void onSearchItemClicked(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), PersonDetailActivity.class);
        intent.putExtra(PersonDetailActivity.PERSON_KEY, (MITPerson) searchListAdapter.getItem(position));
        this.startActivity(intent);
    }

    @OnItemClick(R.id.quick_dial_list)
    protected void onQuickDialItemClicked(AdapterView<?> parent, View view, int position, long id) {
        SpecialPerson sp = (SpecialPerson) quickDialAdapter.getItem(position);

        switch (sp.getTag()) {
            case DIRECTORY_ASSISTANCE_TAG:
                callDirectoryAssistance(view);
                break;
            case EMERGENCY_CONTACTS_TAG:
                showEmergencyContacts(view);
                break;
        }
    }

    protected void callDirectoryAssistance(View sender) {
        startActivity(SharedActivityManager.createTelephoneCallIntent(getActivity(), R.string.people_tel_directory_assistance));
    }

    protected void showEmergencyContacts(View sender) {
        startActivity(SharedActivityManager.createEmergencyContactsIntent(getActivity()));
    }

    private boolean searchTextChanged(View sender, Object handler, String s) {
        if (!TextUtils.isEmpty(s) && s.length() >= 3) {
            recentSearchListView.setVisibility(View.VISIBLE);
            searchRecommendationsAdapter.getFilter().filter(s);
        } else {
            recentSearchListView.setVisibility(View.GONE);
        }
        return false;
    }

    private boolean performSearch(View sender, Object handler, String searchText) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!recentSearches.contains(searchText)) {
            recentSearches.add(searchText);
            editor.putStringSet(PEOPLE_DIRECTORY_SEARCH_HISTORY, recentSearches);
            editor.apply();
        }


        this.setMode(Mode.LIST_BLANK);

        if (this.requestRunning != null && !this.requestRunning.isComplete()) {
            Timber.d("abend, request in process.");
            return true;
        }

        this.requestRunning = PeopleDirectoryManager.searchPeople(getActivity(), searchText, new Callback<List<MITPerson>>() {
            @Override
            public void success(List<MITPerson> list, Response response) {
                Timber.d("Success!");
                searchListAdapter.updateItems(list);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, "Failed");
            }

        });

        return true;
    }

    @Override
    public void onDestroy() {
        if (menu != null) {
            menu.removeItem(R.id.search);
            menu.clear();
        }
        super.onDestroy();
    }

    /**
     * Current view mode of this fragment.
     */
    public enum Mode {
        /**
         * This is the parimary or default screen that is shown with our quick dial and favorites
         */
        NO_SEARCH(false),
        /**
         * We are showing a list, but it is "blank" as no search has been performed.
         */
        LIST_BLANK(true),
        /**
         * We are showing a list, but it is "blank" as no data was found. (NYI?)
         */
        LIST_NODATA(true),
        /**
         * We are showing a list, with valid data.
         */
        LIST_DATA(true);

        private final boolean listViewVisible;

        Mode(boolean listViewVisible) {
            this.listViewVisible = listViewVisible;
        }

        public boolean isListViewVisible() {
            return listViewVisible;
        }

        public static Mode getDefault() {
            return NO_SEARCH;
        }
    }


    /**
     * A custom instance of the MITPerson class that should not be persisted to a database, it is only used for the
     * "Quick Dial" entries on this screen.
     *
     * @author grmartin
     */
    public static class SpecialPerson extends MITPerson {
        private static String IAE_MSG = "This wrapper around the MITPerson Class cannot handle the method being called.";
        private int tag;

        public SpecialPerson(String name, int tag) {
            this.setName(name);
            this.setTitle(MITPeopleDirectoryPersonAdapter.FORCE_SHORT_MODE);
            this.tag = tag;
        }

        public SpecialPerson(String name, String subtitle, int tag) {
            this(name, tag);
            this.setTitle(subtitle);
        }

        @Override
        public int getFavoriteIndex() {
            return -1;
        }

        @Override
        public void setFavoriteIndex(int favoriteIndex) {
            throw new IllegalAccessError();
        }

        @Override
        public boolean isFavorite() {
            throw new IllegalAccessError(IAE_MSG);
        }

        @Override
        public void setFavorite(boolean favorite) {
            throw new IllegalAccessError(IAE_MSG);
        }

        @Override
        protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
            throw new IllegalAccessError(IAE_MSG);
        }

        @Override
        public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
            throw new IllegalAccessError(IAE_MSG);
        }

        protected SpecialPerson(Parcel in) {
            throw new IllegalAccessError(IAE_MSG);
        }

        @Override
        public int describeContents() {
            throw new IllegalAccessError(IAE_MSG);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            throw new IllegalAccessError(IAE_MSG);
        }

        public int getTag() {
            return tag;
        }

        @Override
        public String toString() {
            return "SpecialPerson{" +
                    "tag=" + tag +
                    "name=" + this.getName() +
                    "title=" + this.getTitle() +
                    '}';
        }
    }
}


