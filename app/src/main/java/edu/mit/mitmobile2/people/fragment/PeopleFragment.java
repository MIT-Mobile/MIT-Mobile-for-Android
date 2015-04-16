package edu.mit.mitmobile2.people.fragment;

import java.util.List;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.people.PeopleDirectoryManager;
import edu.mit.mitmobile2.people.PeopleDirectoryManager.PeopleDirectoryManagerCall;
import edu.mit.mitmobile2.people.adapter.MITPeopleDirectoryPersonAdapter;
import edu.mit.mitmobile2.people.model.MITPerson;
import edu.mit.mitmobile2.shared.SharedActivityManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/* The following notes were found in PeopleDetailsViewController.m within - (void) mapPersonAttributes in the iOS
 * project; at revision @6795143 and are duplicated here as they seem to apply to us.
 *
 * key : display name : accessory icon
 * -----------------------------------
 * email     : email : email
 *
 * phone     : phone : phone
 * fax       : fax   : phone
 * homephone : home  : phone
 *
 * office            : office  : map
 * street/city/state : address : map
 *
 * website   : website : external
 */

public class PeopleFragment extends Fragment {
    private Mode mode;

    private PeopleDirectoryManagerCall requestRunning;

    private ListView searchList;
    private ViewGroup defaultLayout;
    private MITPeopleDirectoryPersonAdapter adapter;

    public PeopleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.content_people, null);
        this.searchList = (ListView) view.findViewById(R.id.search_list);
        this.defaultLayout = (ViewGroup) view.findViewById(R.id.default_layout);

        this.searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onSearchItemClicked(parent, view, position, id);
            }
        });

        this.adapter = new MITPeopleDirectoryPersonAdapter();

        searchList.setAdapter(adapter);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (mode == null)
            this.setMode(Mode.getDefault());
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

    private void onSearchItemClicked(AdapterView<?> parent, View view, int position, long id) {

    }

    private void callDirectoryAssistance(View sender, Object handler) {
        startActivity(SharedActivityManager.createTelephoneCallIntent(getActivity(), R.string.people_tel_directory_assistance));
    }

    private void showEmergencyContacts(View sender, Object handler) {
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
                adapter.updateItems(list);
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
}


