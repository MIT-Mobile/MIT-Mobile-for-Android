package edu.mit.mitmobile2.people.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.Html;
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
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.people.PeopleDirectoryManager;
import edu.mit.mitmobile2.people.PeopleDirectoryManager.PeopleDirectoryManagerCall;
import edu.mit.mitmobile2.people.activity.PersonDetailActivity;
import edu.mit.mitmobile2.people.adapter.MITPeopleDirectoryPersonAdapter;
import edu.mit.mitmobile2.people.model.MITPerson;
import edu.mit.mitmobile2.shared.SharedActivityManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import static butterknife.ButterKnife.inject;



public class PeopleFragment extends Fragment {
    private static final int DIRECTORY_ASSISTANCE_TAG = 1504201452;
    private static final int EMERGENCY_CONTACTS_TAG = 1504201453;

    private Mode mode;

    private PeopleDirectoryManagerCall requestRunning;

    @InjectView(R.id.example_search)
    protected TextView exampleSearches;

    @InjectView(R.id.quick_dial_list)
    protected ListView quickDialList;
//
//    @InjectView(R.id.directory_assistance_button)
//    protected Button directoryAssistance;
//    @InjectView(R.id.emergency_contacts_button)
//    protected Button emergencyContacts;

    @InjectView(R.id.search_list)
    protected ListView searchList;

    @InjectView(R.id.default_layout)
    protected ViewGroup defaultLayout;

    @InjectView(R.id.favorites_list)
    protected ListView favoritesList;
    @InjectView(R.id.favorites_layout)
    protected ViewGroup favoritesLayout;

    private MITPeopleDirectoryPersonAdapter favoritePersonsAdapter;
    private MITPeopleDirectoryPersonAdapter searchListAdapter;
    private MITPeopleDirectoryPersonAdapter quickDialAdapter;

    public PeopleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, null);

        initializeComponents(view);

        return view;
    }

    private void initializeComponents(View subjectView) {
        this.setHasOptionsMenu(true);

        inject(this, subjectView);

        this.quickDialAdapter = new MITPeopleDirectoryPersonAdapter(quickDialItems(subjectView.getContext()));
        this.searchListAdapter = new MITPeopleDirectoryPersonAdapter();
        this.favoritePersonsAdapter = new MITPeopleDirectoryPersonAdapter();
        this.favoritePersonsAdapter.setForceShortMode(true);

        this.exampleSearches.setText(Html.fromHtml(getString(R.string.people_default_sample_search_examples)));

        quickDialList.setAdapter(quickDialAdapter);
        searchList.setAdapter(searchListAdapter);
        favoritesList.setAdapter(favoritePersonsAdapter);

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

        if (mode == null)
            this.setMode(Mode.getDefault());

        updateFavoritesListVisibility();
    }

    private void updateFavoritesListVisibility() {
        boolean hasItems = PeopleDirectoryManager.getPersistantFavoritesCount() > 0;

        favoritesLayout.setVisibility(hasItems ? View.VISIBLE : View.GONE);

        favoritePersonsAdapter.updateItems(hasItems ? PeopleDirectoryManager.getPersistantFavoritesList() : new ArrayList<MITPerson>());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_people, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String s) {
                return performSearch(searchView, this, s);
            }

            @Override public boolean onQueryTextChange(String s) { return false; }
        });

        searchView.setQueryHint(getString(R.string.people_search_hint));

        View searchPlate = searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null));

        if (searchPlate != null) {
            int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);

            TextView searchText = (TextView) searchPlate.findViewById(searchTextId);

            if (searchText != null) {
                searchText.setTextColor(Color.WHITE);
                searchText.setHintTextColor(Color.WHITE);
            }
        }
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(@NonNull Mode mode) {
        this.mode = mode;

        this.searchList.setVisibility(mode.isListViewVisible() ? View.VISIBLE : View.GONE);
        this.defaultLayout.setVisibility(mode.isListViewVisible() ? View.GONE : View.VISIBLE);
    }

    @OnItemClick(R.id.favorites_list)
    protected void onFavoritesItemClicked(AdapterView<?> parent, View view, int position, long id) {

    }

    @OnItemClick(R.id.search_list)
    protected void onSearchItemClicked(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), PersonDetailActivity.class);
        intent.putExtra(PersonDetailActivity.PERSON_KEY, (MITPerson)searchListAdapter.getItem(position));
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

//    @OnClick(R.id.directory_assistance_button)
    protected void callDirectoryAssistance(View sender) {
        startActivity(SharedActivityManager.createTelephoneCallIntent(getActivity(), R.string.people_tel_directory_assistance));
    }
//
//    @OnClick(R.id.emergency_contacts_button)
    protected void showEmergencyContacts(View sender) {
        startActivity(SharedActivityManager.createEmergencyContactsIntent(getActivity()));
    }

    private boolean performSearch(View sender, Object handler, String searchText) {
        this.setMode(Mode.LIST_BLANK);

        if (this.requestRunning != null && !this.requestRunning.isComplete()) {
            Timber.d("abend, request in process.");
            return true;
        }

        this.requestRunning = PeopleDirectoryManager.searchPeople(getActivity(), searchText, new Callback<List<MITPerson>>() {
            @Override public void success(List<MITPerson> list, Response response) {
                Timber.d("Success!");
                searchListAdapter.updateItems(list);
            }

            @Override public void failure(RetrofitError error) {
                Timber.e(error, "Failed");
            }

        });

        return true;
    }


    public enum Mode {
        NO_SEARCH(false),
        LIST_BLANK(true),
        LIST_NODATA(true),
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


    public static class SpecialPerson extends MITPerson {
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
            throw new IllegalAccessError("This wrapper around the MITPerson Class cannot handle the method being called.");
        }

        @Override
        public boolean isFavorite() {
            throw new IllegalAccessError("This wrapper around the MITPerson Class cannot handle the method being called.");
        }

        @Override
        public void setFavorite(boolean favorite) {
            throw new IllegalAccessError("This wrapper around the MITPerson Class cannot handle the method being called.");
        }

        @Override
        protected void buildSubclassFromCursor(Cursor cursor, DBAdapter dbAdapter) {
            throw new IllegalAccessError("This wrapper around the MITPerson Class cannot handle the method being called.");
        }

        @Override
        public void fillInContentValues(ContentValues values, DBAdapter dbAdapter) {
            throw new IllegalAccessError("This wrapper around the MITPerson Class cannot handle the method being called.");
        }

        protected SpecialPerson(Parcel in) {
            throw new IllegalAccessError("This wrapper around the MITPerson Class cannot handle the method being called.");
        }

        @Override
        public int describeContents() {
            throw new IllegalAccessError("This wrapper around the MITPerson Class cannot handle the method being called.");
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            throw new IllegalAccessError("This wrapper around the MITPerson Class cannot handle the method being called.");
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


